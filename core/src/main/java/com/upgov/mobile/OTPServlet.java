package com.upgov.mobile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletResponse;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;

@SlingServlet(
        methods = {"GET", "POST"},
        paths = {"/bin/mobile/v1/otp/authenticate"},
        extensions = {"json"})
public class OTPServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private WorkflowService workflowService;

    public static final String RP_USER_MOBILE_NUMBER = "mobile";
    public static final String RP_OTP = "otp";

    public static final String PN_OTP = "otp";
    public static final String PN_MOBILE_NUMBER = "mobile";
    public static final String PN_DATE = "jcr:created";
    public static final String PN_TYPE = "type";

    @Override
    protected void doGet(final SlingHttpServletRequest slingRequest, SlingHttpServletResponse slingResponse) throws IOException {
        String userMobileNo = slingRequest.getParameter(RP_USER_MOBILE_NUMBER);
        String userOtp = slingRequest.getParameter(RP_OTP);
        slingResponse.setContentType("application/json");
        boolean isValidOtp = false;
        Map<String, Object> authInfo = new HashMap<String, Object>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, "adminResourceResolver");

        ResourceResolver resourceResolver = null;
        Session session = null;

        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put("group.path", "/content/usergenerated/content");
        queryMap.put("group.1_property", PN_MOBILE_NUMBER);
        queryMap.put("group.1_property.value", userMobileNo);
        queryMap.put("group.2_property", PN_OTP);
        queryMap.put("group.2_property.value", userOtp);
        queryMap.put("group.3_property", "unverifiedFlag");
        queryMap.put("group.3_property.value", "true");
//        queryMap.put("group.3_properties", PN_TYPE);
//        queryMap.put("group.3_properties.value", "otpnode");
        queryMap.put("group.4_relativedaterange.property", PN_DATE);
        queryMap.put("group.4_relativedaterange.upperBound", "0");
        queryMap.put("group.4_relativedaterange.lowerBound", "-1d");

        try {
            log.debug("Getting resourceresolver and session. Received otp = {0}, and mobile number = {1}", userOtp, userMobileNo);
            resourceResolver = resolverFactory.getServiceResourceResolver(authInfo);
            session = resourceResolver.adaptTo(Session.class);
//            String sqlQuery = "select * from [nt:unstructured] where isDescendantNode('/content/usergenerated/content') and ["+PN_MOBILE_NUMBER+"] = '"+userMobileNo+"' and ["+PN_OTP+"] = '"+userOtp+"' and [unverifiedFlag] = 'true'";
            log.debug("Creating query");
            Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
            log.info("Query formed = {0}", query.toString());

            SearchResult result = query.getResult();
            Iterator<Resource> itr = result.getResources();
//            Iterator<Resource> itr = resourceResolver.findResources(sqlQuery, javax.jcr.query.Query.JCR_SQL2);
//           log.error(isValidOtp+"sql query"+sqlQuery);
            while (itr.hasNext()) {
                Resource resource = itr.next();
                Node node = resource.adaptTo(Node.class);
                node.setProperty("unverifiedFlag", false);
                String path = node.getPath();
                String model = "/etc/workflow/models/complaint-handler-chooser/jcr:content/model";
                WorkflowSession wfSession = workflowService.getWorkflowSession(session);
                WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", path);
                WorkflowModel wfModel = wfSession.getModel(model);
                wfSession.startWorkflow(wfModel, wfData);
//                ValueMap properties = resource.adaptTo(ValueMap.class);
//                String otp = properties.get(PN_OTP, "");
                isValidOtp = true;
//                log.debug("otp found = {0}, creating user", otp);
                // Logic to create a user profile follows
                break;
            }
            session.save();
            if (isValidOtp) {
                slingResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                slingResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (LoginException loginExp) {
            log.warn("LoginException thrown while resolving an admin resourceresolver ", loginExp);
        }
        catch (WorkflowException workflowExp) {
            log.error("Error in starting workflow", workflowExp);
        }
        catch (RepositoryException repoException) {
            log.warn("Unable to remove unverified flag from node", repoException);
        }finally {
            if (resourceResolver != null && resourceResolver.isLive()) {
                resourceResolver.close();
            }
            if (session != null && session.isLive()) {
                session.logout();
            }
        }
    }
}

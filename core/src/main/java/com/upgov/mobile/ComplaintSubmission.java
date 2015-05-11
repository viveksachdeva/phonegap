package com.upgov.mobile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;

import com.upgov.core.config.GrievanceRedressConfiguration;
import com.upgov.core.impl.SMSService;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(
        methods = {"GET", "POST"},
        paths = {"/bin/mobile/v1/complaint/submit"},
        extensions = {"json"})
public class ComplaintSubmission extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SMSService smsService;

    @Reference
    GrievanceRedressConfiguration configuration;


    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        try {
            Map<String, Object> authInfo = new HashMap<String, Object>();
            authInfo.put(ResourceResolverFactory.SUBSERVICE, "adminResourceResolver");
            ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(authInfo);
            NodeUtil nodeUtil = new NodeUtil();
            Map<String, String> requestMap = nodeUtil.storeContent(request, response, resourceResolver);

            if (requestMap.containsKey("unverifiedFlag")) {
                if (smsService != null && configuration.getEnableSMSServiceValue()) {
                    smsService.sendSMSToNumber("Your OTP is " + requestMap.get("otp"), requestMap.get("mobile"));
                }
            }

            JSONObject jsonObj = new JSONObject();
            
            if (requestMap != null && !requestMap.isEmpty()) {
                jsonObj = new JSONObject(requestMap);
            } else {
                jsonObj.put("success", "0");
            }
            
            response.setContentType("application/json");
            
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    		response.setHeader("Access-Control-Allow-Credentials", "true");
    		response.setHeader("Access-Control-Allow-Origin", "*");
    		response.setHeader("Access-Control-Allow-Headers", "Content-Type, *");

            response.getWriter().write(jsonObj.toString());

        } catch (RepositoryException repExp) {
            log.warn("RepositoryException while handling form submission from Mobile app", repExp);
        } catch (JSONException jsonExp) {
            log.warn("JSONException while preparing JSON response for Mobile app", jsonExp);
        } catch (Exception exception) {
            log.error("Some error occured");
        }
    }

}

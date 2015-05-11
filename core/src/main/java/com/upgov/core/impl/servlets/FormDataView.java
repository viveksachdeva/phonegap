package com.upgov.core.impl.servlets;

import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Route;
import com.day.cq.workflow.exec.Workflow;
import com.upgov.core.config.GrievanceRedressConfiguration;
import com.upgov.core.impl.SMSService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by intelligrape on 3/1/2015.
 */
@SuppressWarnings("serial")
@SlingServlet(resourceTypes = {"UPGov-POC/components/content/formData"}, selectors = {"upload","register","inProgress", "completed", "reassigned"}, extensions = "html", methods = {"POST"})
public class FormDataView extends SlingAllMethodsServlet {
    /**
     * Default log.
     */
    @Reference
    SMSService smsService;

    @Reference
    private WorkflowService workflowService;

    @Reference
    private MessageGatewayService messageGatewayService;

    @Reference
    private Replicator replicator;

    @Reference
    private GrievanceRedressConfiguration configuration;


    private MessageGateway messageGateway;
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {

        doPost(req, resp);
    }

    @Override
    protected void doPost(final SlingHttpServletRequest req,
                          final SlingHttpServletResponse resp) throws ServletException, IOException {

        String selector = req.getRequestPathInfo().getSelectorString();
        org.apache.sling.api.resource.Resource resource = req.getResource();
        Node node = resource.adaptTo(Node.class);
        String redirectPagePath = req.getParameter("currentPagePath")+".html";

        if (StringUtils.equals(selector, "upload")) {
            ResourceResolver resourceResolver = req.getResourceResolver();

            try {
                Node commentNode = JcrUtil.createUniqueNode(node, "Comment", "sling:Folder", resourceResolver.adaptTo(Session.class));
                RequestParameterMap requestParameterMap = req.getRequestParameterMap();
                for (Map.Entry<String, RequestParameter[]> entry : requestParameterMap.entrySet()) {
                    String value = entry.getValue()[0].getString();
                    if (!StringUtils.equalsIgnoreCase(entry.getKey(), "commentImage")) {
                        commentNode.setProperty(entry.getKey(), value);
                    }
                }
                String imagePath = node.getName() + "/" + commentNode.getName();

                commentNode.setProperty("commentImage", uploadImage(req, imagePath));
                resourceResolver.commit();
                String userEmail = node.getProperty("userEmailId") != null ? node.getProperty("userEmailId").getString() : "";
                markTicketAsComplete(node, req, userEmail);
                // resp.sendRedirect("/bin/pages/formupdate.completed.html?complaintId=" + node.getName());

            } catch (RepositoryException e) {
                log.error(e.getMessage());
            } catch (ReplicationException e) {
                log.error(e.getMessage());
            } catch (WorkflowException e) {
                log.error(e.getMessage());
            }
        }

        try {

            String userEmail = node.getProperty("userEmailId") != null ? node.getProperty("userEmailId").getString() : "";
            Session session = req.getResourceResolver().adaptTo(Session.class);
            if (selector.equalsIgnoreCase("inProgress")) {

                if(req.getParameter("status")!=null) {
                    node.setProperty("status", req.getParameter("status"));
                }
                UserManager userManager = req.getResourceResolver().adaptTo(UserManager.class);

                String userId = session.getUserID();
               // Authorizable authorizable = userManager.getAuthorizable(userId);
               // Iterator<Group> allgroups = authorizable.declaredMemberOf();
                String assignedPerson = req.getParameter("searchGroup");
                if(assignedPerson.equalsIgnoreCase("escalatedAssignee"))
                {
                    setAssignees(node, userId, req, assignedPerson);
                }
                setAssignees(node, userId, req, "assignee");

               /* while (allgroups.hasNext()) {
                    Group group = allgroups.next();
                    String groupId = group.getID();
                    if (StringUtils.equals(groupId, "jeg")) {
                        setAssignee(node, userId, req, "assignee");
                        break;
                    } else if (StringUtils.equals(groupId, "seg") || StringUtils.equals(groupId, "ceg")) {

                            setAssignee(node, userId, req, "escalatedAssignee");

                        break;
                    }
                }*/


                String complaintId = node.getName();
                setProgressStatus(req.getResourceResolver(), node, "inProgress");
                sendEmail(req.getResourceResolver(), userEmail, complaintId, "inProgress");
                replicator.replicate(req.getResourceResolver().adaptTo(Session.class), ReplicationActionType.ACTIVATE, node.getPath());
            }else if (selector.equalsIgnoreCase("register")) {
                log.error(selector + "-----------------------Selector--------------------------");
                log.error(node + "-----------------------Node--------------------------");
                setProgressStatus(req.getResourceResolver(), node, "registered");
                log.error(req + "-----------------------request--------------------------");
                setAssignee(node, session.getUserID(), req, "currentAssignee");
                req.getResourceResolver().commit();
            }
            else if (selector.equalsIgnoreCase("completed")) {
                markTicketAsComplete(node, req, userEmail);
            } else if (selector.equalsIgnoreCase("reassigned")) {
                log.error(selector + "-----------------------Selector--------------------------");
                log.error(node + "-----------------------Node--------------------------");
                setReassignedStatus(req.getResourceResolver(), node);
                log.error(req + "-----------------------request--------------------------");
                if(req.getParameter("status")!=null) {
                    node.setProperty("status", req.getParameter("status"));
                }
                completeWorkflow(req, node);

            }

        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (WorkflowException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ReplicationException e) {
            e.printStackTrace();
        }


        resp.sendRedirect(redirectPagePath);


    }

    private void setAssignee(Node node, String userId, SlingHttpServletRequest request, String assignee) throws RepositoryException {
        node.setProperty(assignee, userId);

    }

    private void markTicketAsComplete(Node node, SlingHttpServletRequest req, String userEmail) throws RepositoryException, WorkflowException, ReplicationException, PersistenceException {
        String complaintId = node.getName();
        log.error(node + "-----------------------Node--------------------------");
        setProgressStatus(req.getResourceResolver(), node, "completed");
        log.error(req + "-----------------------request--------------------------");
        completeWorkflow(req, node);
        sendEmail(req.getResourceResolver(), userEmail, complaintId, "completed");
        if(configuration.getEnableSMSServiceValue() && smsService !=null) {
            String contactNumber = node.getProperty("mobile").getString();
            String bodyText = "Thank You "+node.getProperty("name").getString()+", Your complaint has been registered. You Complaint ID is "+complaintId;

            smsService.sendSMSToNumber(bodyText, contactNumber);
        }
        replicator.replicate(req.getResourceResolver().adaptTo(Session.class), ReplicationActionType.ACTIVATE, node.getPath());
    }

    private void setAssignees(Node node, String userId, SlingHttpServletRequest request, String assignee) throws RepositoryException {
        ValueFactory valueFactory = request.getResourceResolver().adaptTo(Session.class).getValueFactory();


        Value vs[] = new Value[0];
        List<Value> assigneeList = new ArrayList<Value>();
        Value assigneeValue = valueFactory.createValue(userId);
        if (node.hasProperty(assignee)) {
            for (Value v : node.getProperty(assignee).getValues()) {
                if (v != null) {
                    assigneeList.add(v);
                }
            }
            assigneeList.add(assigneeValue);
        } else {

            assigneeList.add(assigneeValue);
        }

        Value[] finalValues = (Value[]) assigneeList.toArray(vs);
        node.setProperty(assignee, finalValues);


    }

    /**
     * @param req
     * @param node
     * @throws RepositoryException
     * @throws WorkflowException
     */
    private void completeWorkflow(SlingHttpServletRequest req, Node node) throws RepositoryException, WorkflowException, PersistenceException {
        ResourceResolver resolver = req.getResourceResolver();
        WorkflowSession workSession = workflowService.getWorkflowSession(resolver.adaptTo(Session.class));
        log.error(workSession + "-----------------------workSession--------------------------");
        String workflowId = node.getProperty("workflowid").getString();
        Workflow workflow = workSession.getWorkflow(workflowId);

        log.info("ID OF the work flow ===-"+workflow.getId());
        log.info("Items ------------"+workflow.getWorkItems());
        log.info("Items size --->"+workflow.getWorkItems().size());



        List<Route> routes = workSession.getRoutes(workflow.getWorkItems().get(0));
        List<Route> backroutes = workSession.getBackRoutes(workflow.getWorkItems().get(0));


        if (StringUtils.isNotEmpty(req.getParameter("reassignedTo"))) {
            Route backrouteValue = null;
            String reassignedTo = req.getParameter("reassignedTo");
            for (Route route : backroutes) {
                if (StringUtils.equals(reassignedTo, route.getName())) {
                    backrouteValue = route;
                    break;
                }
            }


            workSession.complete(workflow.getWorkItems().get(0), backrouteValue);
            if(StringUtils.isNotEmpty(reassignedTo))
            {
                if(req.getParameter("currentAssignedGroup")!=null) {
                    node.setProperty("currentAssignedGroup", req.getParameter("currentAssignedGroup"));
                }
            }

        } else {
            workSession.complete(workflow.getWorkItems().get(0), routes.get(0));
        }
            resolver.commit();
    }

    /**
     * @param resourceResolver
     * @param node
     * @param status
     * @throws RepositoryException
     */
    private void setProgressStatus(ResourceResolver resourceResolver, Node node, String status) throws RepositoryException {
        node.setProperty("complaintStatus", status);
        if (status.equalsIgnoreCase("inProgress")) {
            node.setProperty("inProgressDate", Calendar.getInstance());
        } else if (status.equalsIgnoreCase("completed")) {
            node.setProperty("completedDate", Calendar.getInstance());
            node.setProperty("status", "-1");
        }
        resourceResolver.adaptTo(Session.class).save();
    }


    /**
     * @param resourceResolver
     * @param node
     * @throws RepositoryException
     */
    private void setReassignedStatus(ResourceResolver resourceResolver, Node node) throws RepositoryException {
        node.setProperty("reassigned", 1);
        resourceResolver.adaptTo(Session.class).save();
    }


    /**
     * @param resourceResolver
     * @param userEmail
     * @param complaintId
     * @param complaintStatus
     */
    private void sendEmail(ResourceResolver resourceResolver, String userEmail, String complaintId, String complaintStatus) {

        log.info("inside Email -----------------------------------");
        ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
        HtmlEmail email = new HtmlEmail();

        log.info("inside Email -----------------------------------");

        Map<String, String> mailTokens = new HashMap<String, String>();

        mailTokens.put("contactName", "Dear Ashish");
        mailTokens.put("complaintId", complaintId);
        org.apache.sling.api.resource.Resource resource = resourceResolver.getResource("/apps/UPGov-POC/notification/email/default/en.html");
        MailTemplate mailTemplate = MailTemplate.create(resource.getPath(), resourceResolver.adaptTo(Session.class));
        try {
            emailRecipients.add(new InternetAddress(userEmail));

            email.setSubject("Your Complaint has been registered");
            email = mailTemplate.getEmail(StrLookup.mapLookup(mailTokens), HtmlEmail.class);
            //email.setHtmlMsg( getHTMLMessage(name));
            email.setTo(emailRecipients);
            messageGateway = messageGatewayService.getGateway(HtmlEmail.class);

            messageGateway.send(email);
        } catch (Exception e) {
            log.error(e.getMessage());

        }
    }

    private String uploadImage(SlingHttpServletRequest request, String path) {

        String imagePath = "";
        try {
            final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);

            if (isMultipart) {
                final java.util.Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();
                for (final java.util.Map.Entry<String, org.apache.sling.api.request.RequestParameter[]> pairs : params.entrySet()) {
                    final String k = pairs.getKey();
                    final org.apache.sling.api.request.RequestParameter[] pArr = pairs.getValue();
                    final org.apache.sling.api.request.RequestParameter param = pArr[0];
                    final InputStream stream = param.getInputStream();

                    if (param.getFileName() != null) {
                        imagePath = writeToDam(request, stream, param.getFileName(), path);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private String writeToDam(SlingHttpServletRequest request
            , InputStream is, String name, String complaintId) {
        try {
            //Inject a ResourceResolver
            ResourceResolver resourceResolver = request.getResourceResolver();

            //Use AssetManager to place the file into the AEM DAM
            com.day.cq.dam.api.AssetManager assetMgr = resourceResolver.adaptTo(com.day.cq.dam.api.AssetManager.class);
            String newFile = "/content/dam/upGov/" + complaintId + "/" + name;
            String format = StringUtils.substringAfterLast(name, ".");
            assetMgr.createAsset(newFile, is, "image/" + format, true);

            // Return the path to the file was stored
            return newFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}


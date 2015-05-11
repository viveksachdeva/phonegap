
package com.upgov.core.impl.servlets;

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
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/pages/formupdate", selectors = "register,inProgress,completed,reassigned", extensions = "html")
public class FormUpdate extends SlingSafeMethodsServlet {

    @Reference
    SMSService smsService;

    @Reference
    private WorkflowService workflowService;

    @Reference
    private MessageGatewayService messageGatewayService;

    @Reference
    private Replicator replicator;


    private MessageGateway messageGateway;
    /**
     * Default log.
     */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        String selector = req.getRequestPathInfo().getSelectorString();
        log.error(selector + "-----------------------Selector--------------------------");

        try {
            String complaintId = req.getParameter("complaintId");

            Node node = getComplaintNode(req.getResourceResolver(), complaintId);
            String userEmail = node.getProperty("userEmailId") != null ? node.getProperty("userEmailId").getString() : "";
            Session session = req.getResourceResolver().adaptTo(Session.class);
            if (selector.equalsIgnoreCase("inProgress")) {

                node.setProperty("status", "1");
                UserManager userManager = req.getResourceResolver().adaptTo(UserManager.class);

                String userId = session.getUserID();
                Authorizable authorizable = userManager.getAuthorizable(userId);
                Iterator<Group> allgroups = authorizable.declaredMemberOf();
                while (allgroups.hasNext()) {
                    Group group = allgroups.next();
                    String groupId = group.getID();
                    if (StringUtils.equals(groupId, "jeg")) {
                        setAssignees(node, userId, req, "assignees");
                        break;
                    } else if (StringUtils.equals(groupId, "seg") || StringUtils.equals(groupId, "ceg")) {
                        if (StringUtils.equals(groupId, "seg")) {
                            setAssignees(node, userId, req, "escalatedAssignee");
                            setAssignees(node, userId, req, "assignees");
                        } else {
                            setAssignees(node, userId, req, "escalatedAssignee");
                            setAssignees(node, userId, req, "assignees");
                        }
                        break;
                    }
                }
                setProgressStatus(req.getResourceResolver(), node, "inProgress");
                sendEmail(req.getResourceResolver(), userEmail, complaintId, "inProgress");
                replicator.replicate(req.getResourceResolver().adaptTo(Session.class), ReplicationActionType.ACTIVATE, node.getPath());
            } else if (selector.equalsIgnoreCase("completed")) {
                log.error(selector + "-----------------------Selector--------------------------");
                log.error(node + "-----------------------Node--------------------------");
                setProgressStatus(req.getResourceResolver(), node, "completed");
                log.error(req + "-----------------------request--------------------------");
                completeWorkflow(req, node);
                sendEmail(req.getResourceResolver(), userEmail, complaintId, "completed");
                String contactNumber = node.getProperty("mobile").getString();
                String bodyText = "Thank You, Your complaint id is : " + complaintId;
                smsService.sendSMSToNumber(bodyText, contactNumber);
                replicator.replicate(req.getResourceResolver().adaptTo(Session.class), ReplicationActionType.ACTIVATE, node.getPath());
            } else if (selector.equalsIgnoreCase("register")) {
                log.error(selector + "-----------------------Selector--------------------------");
                log.error(node + "-----------------------Node--------------------------");
                setProgressStatus(req.getResourceResolver(), node, "registered");
                log.error(req + "-----------------------request--------------------------");
                setAssignee(node, session.getUserID(), req, "assignee");
            }
            else if (selector.equalsIgnoreCase("reassigned")) {
                log.error(selector + "-----------------------Selector--------------------------");
                log.error(node + "-----------------------Node--------------------------");
                node.getProperty("assignee").remove();
                setReassignedStatus(req.getResourceResolver(), node);
                log.error(req + "-----------------------request--------------------------");
                node.setProperty("status", "0");
                completeWorkflow(req, node);

            }

        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (WorkflowException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ReplicationException e) {
            e.printStackTrace();
        }
        resp.sendRedirect("/content/UPGov/en/juniorengineer.html");
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

    private void setAssignee(Node node, String userId, SlingHttpServletRequest request, String assignee) throws RepositoryException {
        node.setProperty(assignee, userId);

    }

    /**
     * @param req
     * @param node
     * @throws RepositoryException
     * @throws WorkflowException
     */
    private void completeWorkflow(SlingHttpServletRequest req, Node node) throws RepositoryException, WorkflowException {
        ResourceResolver resolver = req.getResourceResolver();
        WorkflowSession workSession = workflowService.getWorkflowSession(resolver.adaptTo(Session.class));
        log.error(workSession + "-----------------------workSession--------------------------");
        String workflowId = node.getProperty("workflowid").getString();
        Workflow workflow = workSession.getWorkflow(workflowId);

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
            if (reassignedTo.equalsIgnoreCase("Junior Engineer")) {
                node.setProperty("currentAssignedGroup", "jeg");
            }
            if (reassignedTo.equalsIgnoreCase("Senior Engineer")) {
                node.setProperty("currentAssignedGroup", "seg");
            }
        } else {
            workSession.complete(workflow.getWorkItems().get(0), routes.get(0));
        }

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
            node.setProperty("status", "4");
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
     * @param complaintId
     * @return
     * @throws RepositoryException
     */
    private Node getComplaintNode(ResourceResolver resourceResolver, String complaintId) throws RepositoryException {
        Session session = resourceResolver.adaptTo(Session.class);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        String queryStartString = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([";
        String searchFolder = "/content/usergenerated/content/UPGovForm";
        String queryEndString = "]) and CONTAINS(s.complaintNumber,";
        String endParenthesis = ")";
        String expression = queryStartString + searchFolder + queryEndString + "'" + complaintId + "'" + endParenthesis;
        Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);

// Execute the query and get the results ...
        QueryResult result = query.execute();

// Iterate over the nodes in the results ...

        NodeIterator nodeIter = result.getNodes();
        return nodeIter.nextNode();
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
        Resource resource = resourceResolver.getResource("/apps/UPGov-POC/notification/email/default/en.html");
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


}




package com.upgov.core.impl;

import com.upgov.core.ComplaintInformationService;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: shivani
 * Date: 8/2/15
 * Time: 8:23 AM
 * To change this template use File | Settings | File Templates.
 */

@Service(value = ComplaintInformationService.class)
@Component(immediate = true)
public class ComplaintInformationServiceImpl implements ComplaintInformationService {
	
	@Reference
    private ResourceResolverFactory resolverFactory;

    /**
     * Default log.
     */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    
    public static final String COMPLAINT_NODE_RESOURCETYPE = "UPGov-POC/components/content/formData";
    
    public static final String COMPLAINT_STORE_PARENT_PATH = "/content/usergenerated/content/UPGovForm";

    @Override
    public List<String> getAllComplaintRegisteredByUser(String UserID) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getComplaintStatus(String complaintNumber) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getAllRegisteredComplaintAssignedToEngineer(SlingHttpServletRequest request, String engineerID) throws RepositoryException {
        Session session = request.getResourceResolver().adaptTo(Session.class);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        
        String expression = String.format("SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([%s]) and CONTAINS(s.complaintStatus,'%s')", COMPLAINT_STORE_PARENT_PATH, "registered");
        Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
        log.debug("getAllRegisteredComplaintAssignedToEngineer------------ {}", expression);
        // Execute the query and get the results ...
        QueryResult result = query.execute();

        // Iterate over the nodes in the results ...
        List<String> complaintList = new ArrayList<String>();
        NodeIterator nodeIter = result.getNodes();
        while (nodeIter.hasNext()) {
            Node node = nodeIter.nextNode();
            String complaintNumber = node.getProperty("complaintNumber").getString();
            log.debug("complaintNumber------------ {}",complaintNumber);
            complaintList.add(complaintNumber);
        }
        return complaintList;
    }
    
    
    @Override
    public List<String> getAllComplaintAssignedTOEngineer(SlingHttpServletRequest request, java.lang.String engineerID) throws RepositoryException {
        Session session = request.getResourceResolver().adaptTo(Session.class);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        
        String expression = String.format("SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([%s]) and CONTAINS(s.complaintStatus,'%s')", COMPLAINT_STORE_PARENT_PATH, "*");
        Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
        QueryResult result;
        // Execute the query and get the results ...

        result = query.execute();

        // Iterate over the nodes in the results ...
        List<String> complaintList = new ArrayList<String>();
        NodeIterator nodeIter = result.getNodes();
        while (nodeIter.hasNext()) {
            Node node = nodeIter.nextNode();
            String complaintNumber = node.getProperty("complaintNumber").getString();
            complaintList.add(complaintNumber);
        }
        return complaintList;
    }

    @Override
    public List<String> getAllInProgressComplaintAssignedTOEngineer(SlingHttpServletRequest request, String engineerID) throws RepositoryException {
        Session session = request.getResourceResolver().adaptTo(Session.class);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        String expression = String.format("SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([%s]) and CONTAINS(s.complaintStatus,'%s')", COMPLAINT_STORE_PARENT_PATH, "inProgress");
        
        Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
        QueryResult result = query.execute();

        // Iterate over the nodes in the results ...
        List<String> complaintList = new ArrayList<String>();
        NodeIterator nodeIter = result.getNodes();
        while (nodeIter.hasNext()) {
            Node node = nodeIter.nextNode();
            String complaintNumber = node.getProperty("complaintNumber").getString();
            complaintList.add(complaintNumber);
        }
        return complaintList;
    }


    @Override
    public List<Node> getComplaintNode(ResourceResolver resourceResolver, String complaintId) throws RepositoryException {
        Session session = resourceResolver.adaptTo(Session.class);

        QueryManager queryManager = session.getWorkspace().getQueryManager();
        List<Node> nodeList = new ArrayList<Node>();
        
        String expression = String.format("SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([%s]) and (CONTAINS(s.complaintNumber,'%s') or CONTAINS(s.mobile,'%s')) ORDER BY [s.jcr:created] asc", COMPLAINT_STORE_PARENT_PATH, complaintId, complaintId);
        //String queryStrin = String.format("SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([%s]) and (CONTAINS(s.complaintNumber,'%s') or CONTAINS(s.mobile,'%s'))", "/content/usergenerated/content", complaintId, complaintId);
        
        Query query = queryManager.createQuery(expression, javax.jcr.query.Query.JCR_SQL2);
        QueryResult result = query.execute();
        NodeIterator nodeIter = result.getNodes();
        // Return the first result returned ...
        while (nodeIter.hasNext()) {
        	nodeList.add(nodeIter.nextNode());
        }
        
        return nodeList;
    }

    @Override
    public String getComplaintId(SlingHttpServletRequest request, String contentPath) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        
        Resource resource = resourceResolver.getResource(contentPath);
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        String complaintId = (String) valueMap.get("complaintNumber");
        return complaintId;
    }
    
    public Map<String, Map<String, String>> getComplaintNodesData(SlingHttpServletRequest request, String complaintId) throws RepositoryException {
    	Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
    	
    	Map<String, Object> authInfo = new HashMap<String, Object>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, "adminResourceResolver");
        ResourceResolver resourceResolver = null;
        try {
			resourceResolver = resolverFactory.getServiceResourceResolver(authInfo);
		} catch (LoginException loginExp) {
			log.warn("LoginException while getting a resourceResolver", loginExp);
		}
        
        if (resourceResolver == null) {
        	resourceResolver = request.getResourceResolver();
        }
        
    	List<Node> nodeList = getComplaintNode(resourceResolver, complaintId);
    	Iterator<Node> nodeItr = nodeList.iterator();
    	
        while (nodeItr.hasNext()) {
        	Node node = nodeItr.next();
        	
        	if (node.hasProperty("complaintNumber")) {
        		resultMap.put(node.getProperty("complaintNumber").getString(), getComplaintNodeData(node));
        	}
        }
    	
		return resultMap;
    }

    @Override
    public Map<String, String> getComplaintNodeData(Node node) throws RepositoryException {
        Map<String, String> map = new HashMap<String, String>();
        	
        	if (node != null && node.hasProperty("complaintNumber")) {
                map.put("complaintStatus", node.getProperty("complaintStatus").getString());
                Calendar calander = node.getProperty("jcr:created").getDate();
                Format formatter = new SimpleDateFormat("dd MMM YYYY");
                String receivedDate = formatter.format(calander.getTime());
                map.put("receivedDate", receivedDate);
                map.put("name", node.getProperty("name").getString());
                map.put("nodePath", node.getPath());
                map.put("complaintId", node.getProperty("complaintNumber").getString());
                
                if (node.hasProperty("location")) {
                    map.put("location", node.getProperty("location").getString());
                }
                if (node.hasProperty("complaint")) {
                    map.put("comment", node.getProperty("complaint").getString());
                }

                if (node.hasProperty("department")) {
                    map.put("department", node.getProperty("department").getString());
                }
                if (node.hasProperty("mobile")) {
                    map.put("mobile", node.getProperty("mobile").getString());
                }
                if (node.hasProperty("status")) {
                    map.put("status", node.getProperty("status").getString());
                }

                if (node.hasProperty("imagePath")) {
                    String imagePath =  node.getProperty("imagePath").getString();
                    map.put("imagePath", imagePath);
                }
                if (node.hasProperty("reassigned")) {
                    String reassigned = node.getProperty("reassigned").getString();
                    map.put("reassigned", reassigned);
                }

                if (node.hasProperty("assignee")) {
                    Value[] values = node.getProperty("assignee").getValues();
                    Value lastAssignee = values[values.length - 1];
                    map.put("lastAssignee", lastAssignee.getString());
                }

                if (node.hasProperty("escalatedAssignee")) {
                    Value[] values = node.getProperty("escalatedAssignee").getValues();
                    Value lastEscalatedAssignee = values[values.length - 1];
                    map.put("lastEscalatedAssignee", lastEscalatedAssignee.getString());
                }

                if (node.hasProperty("currentAssignedGroup")) {
                    String currentAssignedGroup = node.getProperty("currentAssignedGroup").getString();
                    map.put("currentAssignedGroup", currentAssignedGroup);
                }
                if (node.hasProperty("completedDate")) {
                    Calendar completedDatecalander = node.getProperty("completedDate").getDate();

                    String completedDate = formatter.format(completedDatecalander.getTime());
                    map.put("completedDate", completedDate);
                }
                if (node.hasProperty("inProgressDate")) {
                    Calendar inProgressDateCalander = node.getProperty("inProgressDate").getDate();

                    String inProgressDate = formatter.format(inProgressDateCalander.getTime());
                    map.put("inProgressDate", inProgressDate);
                }
            }
        
        return map;
    }

    @Override
    public List<String> getTotalComplaints(SlingHttpServletRequest request) throws RepositoryException {
        Session session = request.getResourceResolver().adaptTo(Session.class);

        //String expression = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + COMPLAINT_STORE_PARENT_PATH + "]) and CONTAINS(s.[sling:resourceType], '" + COMPLAINT_NODE_RESOURCETYPE + "')";
        String expression = String.format("SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([%s]) and CONTAINS(s.[sling:resourceType], '%s')", COMPLAINT_STORE_PARENT_PATH, COMPLAINT_NODE_RESOURCETYPE);
        return executeQuery(session, expression);
    }

    @Override
    public List<String> getAllCompletedComplaints(SlingHttpServletRequest request) throws RepositoryException {
        Session session = request.getResourceResolver().adaptTo(Session.class);
        
        //String expression = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + COMPLAINT_STORE_PARENT_PATH + "]) and CONTAINS(s.[sling:resourceType], '" + COMPLAINT_NODE_RESOURCETYPE + "') and CONTAINS(s.complaintStatus, 'completed')";
        String expression = String.format("SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([%s]) and CONTAINS(s.[sling:resourceType], '%s') and CONTAINS(s.complaintStatus, 'completed')", COMPLAINT_STORE_PARENT_PATH, COMPLAINT_NODE_RESOURCETYPE);
        return executeQuery(session, expression);
    }
    
    private List<String> executeQuery(Session session, String queryExpression) throws RepositoryException {
    	
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query query = queryManager.createQuery(queryExpression, javax.jcr.query.Query.JCR_SQL2);
        
        // Execute the query and get the results ...
        QueryResult result = query.execute();

        // Iterate over the nodes in the results ...
        NodeIterator nodeIter = result.getNodes();
        long total = nodeIter.getSize();
        List<String> digits = new ArrayList<String>();
        
        if (total == 0) {
        	digits.add("0");
        } else {
        	while (total > 0) {
                digits.add(String.valueOf(total % 10));
                total /= 10;
            }
        	Collections.reverse(digits);
        }
        
        return digits;
    }
}




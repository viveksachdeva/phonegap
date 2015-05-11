package com.upgov.mobile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.http.HttpServletResponse;

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
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(
        methods = {"GET", "POST"},
        paths = {"/bin/mobile/v1/complaint/track"},
        extensions = {"json"})
public class ComplaintTracking extends SlingAllMethodsServlet {
	
	private static final long serialVersionUID = 1L;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Reference
    private ResourceResolverFactory resolverFactory;
	
	public static final String PARAM_COMPLAINT_ID = "complaintId";
	
	public static final List<String> DATE_PROPERTY_LISTING = new ArrayList<String>(Arrays.asList("jcr:created", "completedDate", "inProgressDate"));
	
	public static final DateFormat datePathFormat = new SimpleDateFormat("dd MMM yyyy");
	
	@Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
    	String complaintId = request.getParameter(PARAM_COMPLAINT_ID);
    	
    	
    	JSONArray jsonArray = new JSONArray();
    	
    	Map<String, Object> authInfo = new HashMap<String, Object>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, "adminResourceResolver");
        
        ResourceResolver resourceResolver;
		try {
			resourceResolver = resolverFactory.getServiceResourceResolver(authInfo);
			List<Node> complaintNodeList = getComplaintNode(resourceResolver, complaintId);
			
			if (!complaintNodeList.isEmpty()) {
				Iterator<Node> complaintItr = complaintNodeList.iterator();
				
				
				while (complaintItr.hasNext()) {
					Node complaintNode = complaintItr.next();
					
					Resource complaintResource = resourceResolver.getResource(complaintNode.getPath());
					ValueMap complaintProperties = complaintResource.adaptTo(ValueMap.class);
					
					Iterator<String> itr = complaintProperties.keySet().iterator();
					JSONObject jsonObj = new JSONObject();
					
					while (itr.hasNext()) {
						String key = itr.next();
						
						if (DATE_PROPERTY_LISTING.contains(key)) {
							// Date handling code follows
							
							if (complaintProperties.get(key, "") != "") {
								String dateString = datePathFormat.format(complaintProperties.get(key, Date.class));
					            
					            if (key.equalsIgnoreCase("jcr:created")) {
					            	key = "receivedDate";
					            }
					            
					            jsonObj.put(key, dateString);
							}
				            
						} else {
							jsonObj.put(key, complaintProperties.get(key, String.class));
						}
					}
					jsonArray.put(jsonObj);
				}
				
				//jsonObj = new JSONObject(complaintProperties);
				
			} else {
				//jsonObj.put("status", "0");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (LoginException loginExp) {
			log.warn("LoginException thrown while getting an admin resourceResolver instance", loginExp);
		} catch (JSONException jsonExp) {
			log.warn("JSONException while creating JSON response for mobile complaint tracking servlet.", jsonExp);
		} catch (RepositoryException repExp) {
			log.warn("RepositoryException thrown while searching for complaint nodes.", repExp);
		} 
		
		response.setContentType("application/json");
		
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, *");
		
		response.getWriter().write(jsonArray.toString());
    	
    }
	
	private List<Node> getComplaintNode(ResourceResolver resourceResolver, String complaintId) throws RepositoryException {
        Session session = resourceResolver.adaptTo(Session.class);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        
        String queryString = String.format("SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([%s]) and (CONTAINS(s.complaintNumber,'%s') or CONTAINS(s.mobile,'%s'))", "/content/usergenerated/content", complaintId, complaintId);
        log.debug("Mobile complaint tracking querytring = {}", queryString);
        
        Query query = queryManager.createQuery(queryString, javax.jcr.query.Query.JCR_SQL2);
        
        List<Node> nodeList = new ArrayList<Node>();
        

        // Execute the query and get the results ...
        QueryResult result = query.execute();

        // Iterate over the nodes in the results ...
        NodeIterator nodeIter = result.getNodes();
        
        // Return the first result returned ...
        while (nodeIter.hasNext()) {
        	nodeList.add(nodeIter.nextNode());
        }
        
        return nodeList;
	}
}

package com.upgov.core;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Priyanku Dwivedi
 * Date: 8/2/15
 * Time: 8:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ComplaintInformationService {

    List<String> getAllComplaintRegisteredByUser(String UserID);
    String getComplaintStatus(String complaintNumber);
    List<String> getAllRegisteredComplaintAssignedToEngineer(SlingHttpServletRequest request,String engineerID) throws RepositoryException;
    List<String> getAllInProgressComplaintAssignedTOEngineer(SlingHttpServletRequest request, java.lang.String engineerID) throws RepositoryException ;

    List<String> getAllComplaintAssignedTOEngineer(SlingHttpServletRequest request, java.lang.String engineerID) throws RepositoryException;
    List<String> getAllCompletedComplaints(SlingHttpServletRequest request) throws RepositoryException;
    List<String> getTotalComplaints(SlingHttpServletRequest request) throws RepositoryException;
    List<Node> getComplaintNode(ResourceResolver resourceResolver, String complaintId) throws RepositoryException  ;
    String getComplaintId(SlingHttpServletRequest request,String contentPath);
    Map<String, Map<String, String>> getComplaintNodesData(SlingHttpServletRequest request, String complaintId) throws RepositoryException;
    Map<String, String> getComplaintNodeData(Node node) throws RepositoryException;
}

package com.upgov.core.components;

/**
 * Created by intelligrape on 3/1/2015.
 */

import com.upgov.core.ComplaintInformationService;
import com.upgov.core.viewGenerator.AbstractViewGenerator;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.util.*;

/**
 * Created by Priyanku Dwivedi on 2/22/2015.
 */
@Component(description = "Profile Viewer", immediate = true, metatype = true, label = "Profile Viewer")
@Service(value = ProfileComponent.class)
public class ProfileComponent extends AbstractViewGenerator {
    @Reference
    ComplaintInformationService complaintInformationService;

    @Override
    public Map<String, Object> onGetData(Map<String, Object> content, Map<String, Object> objects) {
        SlingHttpServletRequest request = (SlingHttpServletRequest) objects
                .get(REQUEST);

        String userId = request.getResourceResolver().getUserID();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<Map<String, String>> complaintList = new ArrayList<Map<String, String>>();
        try {
            List<String> complaints = complaintInformationService.getAllComplaintAssignedTOEngineer(request, "");
            for (String complaint : complaints) {
            	List<Node> nodeList = complaintInformationService.getComplaintNode(request.getResourceResolver(), complaint);
            	Iterator<Node> nodeItr = nodeList.iterator();
            	
            	while (nodeItr.hasNext()) {
            		Node node = nodeItr.next();
            		complaintList.add(complaintInformationService.getComplaintNodeData(node));
            	}
            }
            dataMap.put("complaintList", complaintList);
            dataMap.put("currentUser", userId);
            dataMap.put("currentGroup", getAuthorizeUserGroup(userId, request));

        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return dataMap;
    }

    private String getAuthorizeUserGroup(String userId, SlingHttpServletRequest request) throws RepositoryException {

        UserManager userManager = request.getResourceResolver().adaptTo(UserManager.class);
        Authorizable authorizable = userManager.getAuthorizable(userId);
        Iterator<Group> allGroups = authorizable.declaredMemberOf();
        while (allGroups.hasNext()) {
            Group group = allGroups.next();
            String groupId = group.getID();
            if (StringUtils.equals(groupId, "jeg")) {
                return "jeg";

            }
            if (StringUtils.equals(groupId, "seg")) {
                return "seg";

            }
            if (StringUtils.equals(groupId, "ceg")) {
                return "ceg";

            }

        }

        return "";
    }
}

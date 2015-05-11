package com.upgov.core.components;

import com.upgov.core.ComplaintInformationService;
import com.upgov.core.viewGenerator.AbstractViewGenerator;
import com.upgov.core.viewGenerator.ViewGenerator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.w3c.dom.views.AbstractView;

import javax.jcr.RepositoryException;
import javax.servlet.jsp.PageContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Priyanku Dwivedi on 2/22/2015.
 */
@Component(description = "Component Statics", immediate = true, metatype = true, label = "Component Statics")
@Service(value = ComplaintStatisticsComponent.class)
public class ComplaintStatisticsComponent extends AbstractViewGenerator {

    @Reference
    ComplaintInformationService complaintInformationService;
    @Override
    public Map<String, Object> onGetData(Map<String, Object> content, Map<String, Object> objects) {
        SlingHttpServletRequest request = (SlingHttpServletRequest) objects
                .get(REQUEST);
        SlingHttpServletResponse response = (SlingHttpServletResponse) objects
                .get(RESPONSE);
        Map<String,Object> dataMap = new HashMap<String, Object>();
        List<String> totalComplaints;
        List<String> completedComplaints;
        try {
            totalComplaints = complaintInformationService.getTotalComplaints(request);
            completedComplaints = complaintInformationService.getAllCompletedComplaints(request);
            dataMap.put("totalComplaints",totalComplaints);
            dataMap.put("allCompletedComplaints",completedComplaints);
            dataMap.put("testingVariable","This is a testing variable");
        } catch (RepositoryException e) {
            e.printStackTrace();
        }



        return  dataMap;
    }
}

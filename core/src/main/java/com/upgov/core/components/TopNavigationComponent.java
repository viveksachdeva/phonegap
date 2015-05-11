package com.upgov.core.components;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.upgov.core.viewGenerator.AbstractViewGenerator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.*;

/**
 * Created by intelligrape on 2/24/2015.
 */
@Component(description = "Top Navigation", immediate = true, metatype = true, label = "Top Navigation")
@Service(value = TopNavigationComponent.class)
public class TopNavigationComponent extends AbstractViewGenerator {

    @Override
    public Map<String, Object> onGetData(Map<String, Object> content, Map<String, Object> objects) {
        SlingHttpServletRequest request = (SlingHttpServletRequest) objects
                .get(REQUEST);
        SlingHttpServletResponse response = (SlingHttpServletResponse) objects
                .get(RESPONSE);
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = request.getResource();
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        Page currentPage = pageManager.getContainingPage(resource);
        Page parentPage = currentPage.getAbsoluteParent(2);
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<Page> topNavList = new ArrayList<Page>();
        Iterator<Page> childPages = parentPage.listChildren();
        while (childPages.hasNext()) {
            Page page = childPages.next();
            if (!page.isHideInNav()) {
                topNavList.add(page);
            }
        }
        dataMap.put("topNavigation", topNavList);
        return dataMap;
    }
}

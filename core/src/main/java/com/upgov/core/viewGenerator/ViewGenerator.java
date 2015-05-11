package com.upgov.core.viewGenerator;

/**
 * Created by intelligrape on 2/22/2015.
 */
import javax.servlet.jsp.PageContext;
import java.util.Map;

public interface ViewGenerator {




    String COMPONENT_PROPERTIES = "properties";
    String PROPERTIES = "properties";

    String RESOURCE_RESOLVER = "resourceResolver";


    String SLING = "sling";


    Map<String, Object> getData(final PageContext pageContext);
}

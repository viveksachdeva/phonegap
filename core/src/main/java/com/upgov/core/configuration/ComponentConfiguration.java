package com.upgov.core.configuration;

/**
 * Created by intelligrape on 2/22/2015.
 */

import java.util.Dictionary;

import com.upgov.core.utils.DictionaryUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;



@Component(name = "com.upgov.core.configuration.ComponentConfiguration",description = "ComponentConfiguration", immediate = true, metatype = true, label = "ComponentConfiguration")
@Service(value = ComponentConfiguration.class)
public class ComponentConfiguration {


    @Reference
    private BaseConfiguration baseConfig;

    /**
     * This method returns ViewHelper class name to the specific component
     * passed as parameter.
     *
     * @param componentName
     *            -name of the component.
     * @return - ViewHelper class of the component.
     */
    public String getViewHelper(final String componentName) {

        @SuppressWarnings("rawtypes")
        Dictionary properties = getConfigProperties();

        return DictionaryUtils.getString(properties, componentName);

    }

    /**
     * This method returns the properties specified under the config node.
     *
     * @return <code>Dictionary</code> of properties.
     */
    @SuppressWarnings("rawtypes")
    private Dictionary getConfigProperties() {
        return baseConfig.getConfigProperties((ComponentConfiguration.class)
                .getName());
    }
}

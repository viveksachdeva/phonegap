package com.upgov.core.configuration;

/**
 * Created by intelligrape on 2/22/2015.
 */

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

@Component(description = "BaseConfiguration", immediate = true, metatype = true, label = "BaseConfiguration")
@Service(value = BaseConfiguration.class)
public class BaseConfiguration {
    /** The Constant PLACEHOLDER_DATA_CONFIG_PATH_REGEX_PATTERN. */
    private static final String PLACEHOLDER_DATA_CONFIG_PATH_REGEX_PATTERN = "%(.*?)%";

    /**
     * Injected service {@link ConfigurationAdmin}.
     */
    @Reference
    private ConfigurationAdmin configAdmin;
    /**
     * Map storing whether to loading config files at runtime.
     */
    private Map<String, Object> dynamicLoadMap = new HashMap<String, Object>();
    /**
     * Map of config files properties.
     */
    private Map<String, Object> dictionaryMap = new HashMap<String, Object>();

    /**
     * This method returns the Configuration file properties for the config
     * File. passed as input.
     *
     * @param className
     *            Config name to be loaded.
     * @return properties Config values as a Dictionary
     */
    @SuppressWarnings("rawtypes")
    private Dictionary getDictionary(final String className) {
        Dictionary properties;
        Configuration configuration = null;

        try {
            configuration = configAdmin.getConfiguration(className);
        } catch (IOException e) {
            e.printStackTrace();
        }

        properties = configuration.getProperties();
        return properties;
    }

    /**
     * This method returns whether to load the Config file at runtime or not.
     * The value of dynamicLoadMap variable is refreshed on server startup.
     *
     * @param className
     *            Config name to be loaded.
     * @return dynamicLoadMap Load the config at Run time or not
     */
    @SuppressWarnings("rawtypes")
    private boolean loadAtRunTime(final String className) {

        if (null == dynamicLoadMap.get(className)) {
            Dictionary properties = getDictionary(className);
            Boolean load = (Boolean) properties.get("loadAtRunTime");
            if (null == load) {
                load = true;
            }
            dynamicLoadMap.put(className, load);
        }

        return (Boolean) dynamicLoadMap.get(className);
    }

    /**
     * This method returns the properties specified under the config node.
     *
     * @param className
     *            Config name to be loaded.
     * @return <code>Dictionary</code> of properties.
     */
    @SuppressWarnings("rawtypes")
    public Dictionary getConfigProperties(final String className) {
        Dictionary configDictionary;

        if (loadAtRunTime(className)) {
            configDictionary = getDictionary(className);

        } else {
            if (null == dictionaryMap.get(className)) {
                configDictionary = getDictionary(className);
                dictionaryMap.put(className, configDictionary);
            }
            configDictionary = (Dictionary) dictionaryMap.get(className);
        }

        return configDictionary;
    }

    /**
     * This method is used to replace the disclosure placeholder variables. with
     * its actual values.
     *
     * @param template
     *            - Text with embedded placeholder variables
     * @param values
     *            - placeholder data list
     * @return - Disclosure Text with replaced dynamic values
     */
    protected String replaceTokens(final String template,
                                   final Map<String, String> values) {

        final StringBuffer stringBuff = new StringBuffer();
        final Pattern pattern = Pattern.compile(
                PLACEHOLDER_DATA_CONFIG_PATH_REGEX_PATTERN, Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            final String key = matcher.group(1);
            final String replacement = values.get(key);
            if (replacement != null) {
                matcher.appendReplacement(stringBuff, replacement);
            }
        }
        matcher.appendTail(stringBuff);
        return stringBuff.toString();
    }

    /**
     * This method returns the Configuration file properties for the config
     * node. passed as input.
     *
     * @param nodeName
     *            the node name
     * @return properties Config values as a Dictionary
     */
    @SuppressWarnings("rawtypes")
    public Dictionary getDictionaryForNode(final String nodeName) {
        Dictionary properties;
        Configuration configuration = null;

        try {
            configuration = configAdmin.getConfiguration(nodeName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        properties = configuration.getProperties();
        return properties;
    }

    /**
     * get the value for the OSGI component configuration.
     *
     * @param template
     *            , template for which configuration is to be retrieved
     * @param dataConfigTokens
     *            the data config tokens
     * @return , configuration value retrieved.
     */
    public String getAbsoluteConfigValue(final String template,
                                         Map<String, String> dataConfigTokens) {
        return this.replaceTokens(template, dataConfigTokens);
    }
}

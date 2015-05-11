package com.upgov.core.config;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Map;

/**
 * Created by intelligrape on 3/3/2015.
 */

@Component(
        immediate = true,
        label = "UP Gov Configuration",
        description = "UP Gov Configuration",
        metatype = true)
@Properties({
        @Property(name = Constants.SERVICE_DESCRIPTION, value = "UP Gov Configuration"),
        @Property(name = Constants.SERVICE_VENDOR, value = "UP Gov Configuration")})
@Service(value = GrievanceRedressConfiguration.class)
public class GrievanceRedressConfiguration {

    /**
     * This is private variable to accommodate all the defined properties.
     */
    @SuppressWarnings("rawtypes")
    private static Dictionary props;

    /**
     * The Constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(GrievanceRedressConfiguration.class);

    private String enableSMSServer;

    @Property(
            label = "Enable SMS Service",
            description = "Enable SMS Service",
            options = {
                    @PropertyOption(name = "true", value = "Yes"),
                    @PropertyOption(name = "false", value = "No")},
            value = "false")
    private static final String PROPERTY_SMS_SERVICE = "sms.service";

    /**
     * Activate.
     *
     * @param context the context
     */
    @Activate
    protected void activate(@SuppressWarnings("rawtypes") final Map context) {
        this.enableSMSServer = PropertiesUtil.toString(context.get(PROPERTY_SMS_SERVICE), "");

    }

    @Deactivate
    protected void deactivate() {
        this.enableSMSServer = null;
    }

    @Modified
    protected void modified(ComponentContext context){
        this.enableSMSServer = PropertiesUtil.toString(
                context.getProperties().get(PROPERTY_SMS_SERVICE), "");
    }


    public boolean getEnableSMSServiceValue() {
        return Boolean.parseBoolean(enableSMSServer);
    }
}

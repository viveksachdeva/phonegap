package com.upgov.core.form.impl;

/**
 * Created with IntelliJ IDEA.
 * User: Priyanku
 * Date: 10/2/15
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */

import com.upgov.core.config.GrievanceRedressConfiguration;
import com.upgov.core.form.FormService;
import com.upgov.core.impl.SMSService;
import com.upgov.mobile.NodeUtil;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(description = "Form Service", immediate = true, metatype = true, label = "Form Service")
@Service(value = FormService.class)
public class FormServiceImpl implements FormService {

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SMSService smsService;

    @Reference
    GrievanceRedressConfiguration configuration;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public String execute(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        log.info("inside Execute -----------------------------------");

        Map<String, String> map = null;
        NodeUtil nodeUtil = new NodeUtil();

        try {
            Map<String, Object> authInfo = new HashMap<String, Object>();
            authInfo.put(ResourceResolverFactory.SUBSERVICE, "adminResourceResolver");
            ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(authInfo);
            map = nodeUtil.storeContent(request, response, resourceResolver);
            if (map.containsKey("unverifiedFlag") && map.get("unverifiedFlag").equalsIgnoreCase("true")) {
                if (smsService != null && configuration.getEnableSMSServiceValue()) {
                    smsService.sendSMSToNumber("Your OTP is " + map.get("otp"), map.get("mobile"));
                }
            }
        } catch (RepositoryException repExp) {
            log.warn("RepositoryException thrown while creating complaint node ", repExp);
        } catch (Exception exception) {
            log.error("Error ", exception);
        }
        String queryString = NodeUtil.getQueryStringFromMap(map);

        return queryString;
    }
}

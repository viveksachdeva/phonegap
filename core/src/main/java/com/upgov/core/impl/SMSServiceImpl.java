package com.upgov.core.impl;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = true, enabled = true)
@Service
@org.apache.felix.scr.annotations.Properties({
        @Property(name = "service.vendor", value = "UP Govt"),
        @Property(name = "service.description", value = "sms service to send sms.")
})
public class SMSServiceImpl implements SMSService {

    /** Default log. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());


    final public static String TWILIO_URL = "api.twilio.com/2010-04-01/Accounts"  ;

    private final String twilioAccountSid = "AC7a6db3f695fcbb08b84e6891968ce221";
    final public static String TwiloAppAccount = "9eef27c64978927964372e8840da4b30";

    @Override
    public void sendSMSToNumber(String bodyText, String toMobileNumber ){
        String twilioOauthToken = TwiloAppAccount  ;
        String twilioAppAccountSid = twilioAccountSid  ;

        try{
            String urlString = "https://"+TWILIO_URL+"/"+twilioAppAccountSid+"/SMS/Messages.json";
            HttpClientParams httpClientParams = new HttpClientParams();
            httpClientParams.setAuthenticationPreemptive(true);

            HttpClient client = new HttpClient();
            Credentials defaultCreds = new UsernamePasswordCredentials(twilioAppAccountSid, twilioOauthToken);
            client.getState().setCredentials(AuthScope.ANY, defaultCreds);

            PostMethod postMethod = new PostMethod(urlString);
            postMethod.addParameter(new NameValuePair("Body", bodyText));
            postMethod.addParameter(new NameValuePair("To", "+91"+toMobileNumber));
            postMethod.addParameter(new NameValuePair("From", "+13029669826"));

            System.out.println("urlString "+urlString);
            int responseCode = client.executeMethod(postMethod);
            System.out.println("body "+postMethod.getResponseBodyAsString());
            postMethod.releaseConnection();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

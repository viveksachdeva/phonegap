package com.upgov.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.upgov.core.config.GrievanceRedressConfiguration;
import com.upgov.core.impl.SMSService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Component
@Service
public class SendEmailAndSMS implements WorkflowProcess {

    @Property(value = "Send Email and Service Workflow")
    static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
    @Property(value = "Intelligrape")
    static final String VENDOR = Constants.SERVICE_VENDOR;
    @Property(value = "SMS and Email")
    static final String LABEL="process.label";

    @Reference
    private ResourceResolverFactory resourceResolverFactory = null;

    @Reference
    private MessageGatewayService messageGatewayService;

    private MessageGateway<HtmlEmail> messageGateway;

    @Reference
    SMSService smsService;

    @Reference
    GrievanceRedressConfiguration configuration;

    /** Default log. */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String TYPE_JCR_PATH = "JCR_PATH";

    ResourceResolver resourceResolver;
    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        //To change body of implemented methods use File | Settings | File Templates.
        WorkflowData workflowData = workItem.getWorkflowData();
        String path = StringUtils.EMPTY;
        if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
            path = workflowData.getPayload().toString() ;

            try {
                Session jcrSession = workflowSession.adaptTo(Session.class);
                final Map<String, Object> authenticationMap = new HashMap<String, Object> ();
                authenticationMap.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, jcrSession);
                resourceResolver = resourceResolverFactory.getResourceResolver(authenticationMap);
                Resource resource = resourceResolver.getResource(path);
                ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
                Node complaintNode = resource.adaptTo(Node.class);
                if(!complaintNode.getProperty("unverifiedFlag").getBoolean()){
                    log.error("Sending message for complaint id");
                    String name = (String)valueMap.get("name");
                    String number = (String)valueMap.get("mobile");
                    String email = (String)valueMap.get("userEmailId");
                    String complaintId = (String)valueMap.get("complaintNumber");
                    sendEmail(resourceResolver,email,name,complaintId);
                    String bodyText = "Thank You "+name+", Your complaint has been registered. You Complaint ID is "+complaintId;
                    if(smsService!=null && configuration.getEnableSMSServiceValue()) {
                        smsService.sendSMSToNumber(bodyText, number);
                    }
                    valueMap.put("workflowid", workItem.getWorkflow().getId());
                    resourceResolver.commit();
                }
            } catch (LoginException e) {
                log.error(e.getMessage());
            } catch (PersistenceException e) {
                log.error(e.getMessage());
            }
            catch (RepositoryException e) {
                log.error(e.getMessage());
            }


        }


    }


  private void sendEmail(ResourceResolver resourceResolver, String userEmailId, String name, String complaintId) {

        log.info("inside Email -----------------------------------");
        ArrayList<InternetAddress> emailRecipients = new ArrayList<InternetAddress>();
        HtmlEmail email = new HtmlEmail();
      Map<String, String> mailTokens = new HashMap<String, String>();

      mailTokens.put("contactName", "Dear "+name);
      mailTokens.put("complaintId", complaintId);
      Resource resource = resourceResolver.getResource("/apps/UPGov-POC/notification/email/default/en.html");
      MailTemplate mailTemplate = MailTemplate.create(resource.getPath(),resourceResolver.adaptTo(Session.class));
      try {
            emailRecipients.add(new InternetAddress(userEmailId));

            email.setSubject("Your Complaint has been registered");
            email = mailTemplate.getEmail(StrLookup.mapLookup(mailTokens),HtmlEmail.class);
           //email.setHtmlMsg( getHTMLMessage(name));
          email.setTo(emailRecipients);
            messageGateway = messageGatewayService.getGateway(HtmlEmail.class);

            messageGateway.send(email);
        } catch (Exception e) {
            log.error(e.getMessage());

        }
    }

    private String getHTMLMessage(String name) {

        String htmlmessage="<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns:v=\"urn:schemas-microsoft-com:vml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/><title>UP Govt.</title><style type=\"text/css\"><!--body { margin: 0; padding: 0; background:#F5F5F5; }img{ border:0px;}v\\:\\* { behavior: url(#default#VML); display:inline-block; }--> </style></head><body><table width=\"600\" height=\"750px\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" background=\"http://qa3.intelligrape.net:11020/content/dam/upGov/background.jpg\" style=\"background:url(http://qa3.intelligrape.net:11020/content/dam/upGov/background.jpg)\"><!–[if gte vml 1]><v:shape stroked='f' style='position:absolute; z-index:-1;visibility:visible;width:600px; height:750px;top:0px;left:-3px;border:0;'><v:imagedata src=\"http://localhost:4502/etc/designs/UPGov-POC/img/background.jpg/\"/></v:shape><![endif]–><tr><td height=\"20px\">&nbsp;</td></tr> <tr><td height=\"190px\" valign=\"top\" align=\"right\"><table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td width=\"82%\">&nbsp;</td><td valign=\"top\"><a href=\"#\"><img src=\"http://qa3.intelligrape.net:11020/content/dam/upGov/fb-icon.png\" alt=\"Facebook\" title=\"Facebook\" /></a></td><td valign=\"top\"><a href=\"#\"><img src=\"http://qa3.intelligrape.net:11020/content/dam/upGov/twitter-icon.png\" align=\"Twitter\" title=\"Twitter\" /></a></td></tr> </table></td></tr><tr><td valign=\"top\" align=\"center\"><table width=\"70%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"> <tr><td valign=\"top\" height=\"25px\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:15px; font-weight:bold\">Dear "+name+",</td></tr><tr><td height=\"340px\" valign=\"top\" style=\"font-family:Arial, Helvetica, sans-serif; font-size:14px; line-height:20px\">Thank you for taking the first step towards making out state the nation's pride. I would like to personally assure you that we will look into your complaint and that together, we will find a solution as soon as possible.<br />In the meantime, you can track your complaint's status. We will inform your when we have resolved the issue.</td></tr>          <tr><td><a href=\"#\"><img src=\"http://qa3.intelligrape.net:11020/content/dam/upGov/button.png\" alt=\"see solution\" title=\"see solution\" /></a></td></tr></table>        </td>        </tr>        </table>       </body>        </html> ";
        return htmlmessage   ;
    }


}

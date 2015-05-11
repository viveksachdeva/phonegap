package com.upgov.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;

import com.adobe.granite.workflow.model.WorkflowTransition;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by intelligrape on 2/28/2015.
 */
@Component
@Service
public class EscalationStep implements WorkflowProcess {

    @Property(value = "UP - Government Escalation step")
    static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
    @Property(value = "Intelligrape")
    static final String VENDOR = Constants.SERVICE_VENDOR;
    @Property(value = "UP - Government Escalation step")
    static final String LABEL = "process.label";

    /**
     * Default log.
     */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String TYPE_JCR_PATH = "JCR_PATH";

    @Reference
    private ResourceResolverFactory resourceResolverFactory = null;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        //To change body of implemented methods use File | Settings | File Templates.
        WorkflowData workflowData = workItem.getWorkflowData();
        String path = StringUtils.EMPTY;
        if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
            path = workflowData.getPayload().toString();

            try {
                Session jcrSession = workflowSession.adaptTo(Session.class);
                final Map<String, Object> authenticationMap = new HashMap<String, Object>();
                authenticationMap.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, jcrSession);


                ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(authenticationMap);
                Resource resource = resourceResolver.getResource(path);
                ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
                Route route = workflowSession.getRoutes(workItem, false).get(0);
                WorkflowTransition workflowTransition = route.getDestinations().get(0);
                WorkflowNode workflowNode = workflowTransition.getTo();
                MetaDataMap metaMap = workflowNode.getMetaDataMap();
                Value participantValue = (Value) metaMap.get("PARTICIPANT");
                setEscalatedGroup(participantValue.getString(), resource, resourceResolver);

            } catch (LoginException e) {
                log.error(e.getMessage());
            } catch (RepositoryException e) {
                e.printStackTrace();
            }


        }

    }

    private void setEscalatedGroup(String participant, Resource resource, ResourceResolver resourceResolver) throws RepositoryException {

        ValueFactory valueFactory = resourceResolver.adaptTo(Session.class).getValueFactory();
        Node node = resource.adaptTo(Node.class);
        node.getProperty("currentAssignee").remove();

        Value vs[] = new Value[0];
        List<Value> assigneeList = new ArrayList<Value>();
        Value assigneeValue = valueFactory.createValue(participant);
        if (node.hasProperty("escalatedGroup")) {
            for (Value v : node.getProperty("escalatedGroup").getValues()) {
                if (v != null) {
                    assigneeList.add(v);

                }
            }
            assigneeList.add(assigneeValue);
        } else {

            assigneeList.add(assigneeValue);
        }

        Value[] finalValues = (Value[]) assigneeList.toArray(vs);
        node.setProperty("escalatedGroup", finalValues);
        node.setProperty("complaintStatus", "received");
        if (StringUtils.equalsIgnoreCase(participant, "seg")) {
            node.setProperty("status", "2");
            node.setProperty("currentAssignedGroup", "seg");
        } else if (StringUtils.equalsIgnoreCase(participant, "ceg")) {
            node.setProperty("status", "3");
            node.setProperty("currentAssignedGroup", "ceg");
        }
    }
}

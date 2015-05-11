package com.upgov.mobile;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;

public class NodeUtil {

    public static final String PARENT_NODE = "UPGovForm";

    public static final String CONTENT_TREE_HIERARCHY = "/content/usergenerated/content";

    public static final String DAM_PARENT_HIERARCHY = "/content/dam/upGov/";

    public static final String TICKET_PREFIX = "UP";

    public static final String PARAM_LOCATION = "location";

    public static final String PARAM_DEPARTMENT = "department";

    public static final String PARAM_MOBILE = "mobile";

    public static final String PARAM_ANONYMOUS_USER_MOBILE = "anonymousMobile";

    public static final String PARAM_NAME = "name";

    public static final String PARAM_ANONYMOUS_USER_NAME = "anonymousName";

    public static final String PARAM_COMPLAINT = "complaint";

    public static final String PARAM_PINCODE = "pincode";

    public static final String PARAM_EMAIL = "email";

    public static final String PARAM_ANONYMOUS_EMAIL = "anonymousEmail";

    public static final String PROP_COMPLAINT_NUMBER = "complaintNumber";

    public static final String PROP_EMAILID = "userEmailId";

    public static final String PROP_STATUS = "status";

    public static final String PROP_COMPLAINT_STATUS = "complaintStatus";

    public static final String PROP_CURRENT_ASSIGNED_GROUP = "currentAssignedGroup";

    public static final String PROP_IMAGE_PATH = "imagePath";

    public static final String PARAM_UNVERIFIED_FLAG = "unverifiedFlag";
    public static final String PARAM_OTP = "otp";

    protected final static Logger log = LoggerFactory.getLogger(NodeUtil.class);

    public final Map<String, String> storeContent(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceResolver resourceResolver) throws RepositoryException {

        Resource resource = resourceResolver.getResource(CONTENT_TREE_HIERARCHY);
        Node pathNode = createIntermediaryNodes(resource, request, resourceResolver);

        Session jcrSession = resourceResolver.adaptTo(Session.class);

        Map<String, String> propertyMap = new HashMap<String, String>();

        //String complaintId = getComplaintId();
        Node formDataNode = addPropertiesToNode(request, pathNode, propertyMap, resourceResolver);

        if (formDataNode != null) {
            log.info("inside storeContent. Intermediary node path = {}", formDataNode.getPath());
        }

        jcrSession.save();

        return propertyMap;
    }

    public static final String getQueryStringFromMap(Map<String, String> map) {
        if (map != null) {
            Iterator<String> mapItr = map.keySet().iterator();
            StringBuilder queryString = new StringBuilder(30);

            while (mapItr.hasNext()) {
                String key = mapItr.next();
                queryString.append(key).append("=").append(map.get(key)).append("&");
                log.warn("Map values : key = {0} AND value = {1}", key, map.get(key));
            }

            return StringUtils.removeEndIgnoreCase(queryString.toString(), "&");
        }
        return StringUtils.EMPTY;
    }

    /**
     * This method creates the intermediary path structure for the incoming request under
     * "/content/usergenerated/content" hierarchy.
     *
     * @param resource - Base Resource relative to which the data node would be created. In this case it is "/content/usergenerated/content".
     * @param request  - SlingRequest object.
     * @return Created JCR node
     * @throws RepositoryException
     */
    private Node createIntermediaryNodes(Resource resource, SlingHttpServletRequest request, ResourceResolver resourceResolver) throws RepositoryException {
        Node contentNode = resource.adaptTo(Node.class);
        Node formSaveNode = null;

        if (request.getParameter(PARAM_LOCATION) != null) {
            String complaintId = getComplaintId();
            String location = request.getParameter(PARAM_LOCATION);
            DateFormat datePathFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar calender = Calendar.getInstance();
            String datePath = PARENT_NODE + "/" + location + "/" + datePathFormat.format(calender.getTime()) + "/" + complaintId;

            formSaveNode = JcrUtil.createPath(contentNode, datePath, true, JcrResourceConstants.NT_SLING_FOLDER, NodeType.NT_UNSTRUCTURED, resourceResolver.adaptTo(Session.class), false);
        }

        return formSaveNode;
    }

    /**
     * This method generates unique complaintId using a combination of current date and 4 digit random number generation.
     *
     * @return String representing unique complaintId
     * @throws RepositoryException
     */
    private String getComplaintId() throws RepositoryException {

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calender = Calendar.getInstance();
        String date = dateFormat.format(calender.getTime());
        String randomNumber = String.valueOf(randInt(1, 9999));

        String complaintId = new StringBuilder(TICKET_PREFIX).append(date).append(randomNumber).append(System.currentTimeMillis()).toString();
        return complaintId;
    }

    /**
     * Method to generate random digits.
     *
     * @param min - Minimum inclusive
     * @param max - Maximum inclusive
     * @return integer
     */
    private int randInt(int min, int max) {
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    private Node addPropertiesToNode(SlingHttpServletRequest request, Node complaintNode, Map<String, String> parameterMap, ResourceResolver resourceResolver) throws RepositoryException {
        //Node complaintNode = null;

        Session session = request.getResourceResolver().adaptTo(Session.class);
        UserManager userManager = resourceResolver.adaptTo(UserManager.class);

        String email = StringUtils.EMPTY;

        if (request.getParameter(PARAM_EMAIL) != null) {
            email = "".equals(request.getParameter(PARAM_EMAIL).trim()) ? request.getParameter(PARAM_ANONYMOUS_EMAIL) : request.getParameter(PARAM_EMAIL);
        } else {
            /* to get the current user */
            Authorizable auth = userManager.getAuthorizable(session.getUserID());
            
        	/* to get the property of the authorizable, use relative path */
            Value[] names = auth.getProperty("./profile/email");

            email = names[0].getString();
        }

        String complaintId = complaintNode.getName();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calender = Calendar.getInstance();
        String relativePath = dateFormat.format(calender.getTime());
        String imagePath = uploadImage(request, relativePath, complaintId);

        if (complaintNode != null) {

            if (request.getParameter(PARAM_NAME) != null) {
                String name = "".equals(request.getParameter(PARAM_NAME).trim()) ? request.getParameter(PARAM_ANONYMOUS_USER_NAME) : request.getParameter(PARAM_NAME);
                complaintNode.setProperty(PARAM_NAME, name);
                parameterMap.put(PARAM_NAME, name);
            }

            if (request.getParameter(PARAM_MOBILE) != null) {
                String mobile = "".equals(request.getParameter(PARAM_MOBILE).trim()) ? request.getParameter(PARAM_ANONYMOUS_USER_MOBILE) : request.getParameter(PARAM_MOBILE);
                complaintNode.setProperty(PARAM_MOBILE, mobile);
                parameterMap.put(PARAM_MOBILE, mobile);
            }

            if (request.getParameter(PARAM_DEPARTMENT) != null) {
                complaintNode.setProperty(PARAM_DEPARTMENT, request.getParameter(PARAM_DEPARTMENT));
                parameterMap.put(PARAM_DEPARTMENT, request.getParameter(PARAM_DEPARTMENT));
            }

            if (request.getParameter(PARAM_LOCATION) != null) {
                complaintNode.setProperty(PARAM_LOCATION, request.getParameter(PARAM_LOCATION));
                parameterMap.put(PARAM_LOCATION, request.getParameter(PARAM_LOCATION));
            }

            if (request.getParameter(PARAM_COMPLAINT) != null) {
                complaintNode.setProperty(PARAM_COMPLAINT, request.getParameter(PARAM_COMPLAINT));
                parameterMap.put(PARAM_COMPLAINT, request.getParameter(PARAM_COMPLAINT));
            }

            if (request.getParameter(PARAM_PINCODE) != null) {
                complaintNode.setProperty(PARAM_PINCODE, request.getParameter(PARAM_PINCODE));
                parameterMap.put(PARAM_PINCODE, request.getParameter(PARAM_PINCODE));
            }

            if (null != request.getParameter(PARAM_UNVERIFIED_FLAG)) {
                String otp = "" + ((int) (Math.random() * 9000) + 1000);
                complaintNode.setProperty(PARAM_UNVERIFIED_FLAG, request.getParameter(PARAM_UNVERIFIED_FLAG));
                parameterMap.put(PARAM_UNVERIFIED_FLAG, request.getParameter(PARAM_UNVERIFIED_FLAG));
                complaintNode.setProperty(PARAM_OTP, otp);
                parameterMap.put(PARAM_OTP, otp);
            }

            if (imagePath != null) {
                complaintNode.setProperty(PROP_IMAGE_PATH, imagePath);
                parameterMap.put(PROP_IMAGE_PATH, imagePath);
            }

            complaintNode.setProperty(PROP_COMPLAINT_STATUS, "received");
            parameterMap.put(PROP_COMPLAINT_STATUS, "received");

            complaintNode.setProperty(PROP_COMPLAINT_NUMBER, complaintId);
            parameterMap.put(PROP_COMPLAINT_NUMBER, (complaintId != null ? complaintId : ""));

            complaintNode.setProperty(PROP_CURRENT_ASSIGNED_GROUP, "jeg");
            complaintNode.setProperty(PROP_STATUS, "0");

            complaintNode.setProperty("jcr:created", Calendar.getInstance());

            // Set these 3 properties for the node to reverse replicate
            complaintNode.setProperty("cq:distribute", true);
            complaintNode.setProperty("cq:lastModified", Calendar.getInstance());
            complaintNode.setProperty("cq:lastModifiedBy", session.getUserID() != null ? session.getUserID() : "admin");

            complaintNode.setProperty("sling:resourceType", "UPGov-POC/components/content/formData");
            complaintNode.setProperty(PROP_EMAILID, email);
        }
        return complaintNode;
    }


    private String uploadImage(SlingHttpServletRequest request, String relativePath, String complaintId) {

        String imagePath = "";
        try {
            final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);

            if (isMultipart) {
                final java.util.Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();
                for (final java.util.Map.Entry<String, org.apache.sling.api.request.RequestParameter[]> pairs : params.entrySet()) {
                    final String k = pairs.getKey();
                    final org.apache.sling.api.request.RequestParameter[] pArr = pairs.getValue();
                    final org.apache.sling.api.request.RequestParameter param = pArr[0];
                    final InputStream stream = param.getInputStream();

                    if (param.getFileName() != null) {
                        imagePath = writeToDam(request, stream, param.getFileName(), relativePath, complaintId);
                    }

                }
            }
        } catch (Exception e) {
            log.warn("Exception thrown while uploading images to DAM ", e);
        }
        return imagePath;
    }

    private String writeToDam(SlingHttpServletRequest request, InputStream is, String name, String relativePath, String complaintId) {
        try {
            //Inject a ResourceResolver
            ResourceResolver resourceResolver = request.getResourceResolver();

            //Use AssetManager to place the file into the AEM DAM
            com.day.cq.dam.api.AssetManager assetMgr = resourceResolver.adaptTo(com.day.cq.dam.api.AssetManager.class);
            String newFile = DAM_PARENT_HIERARCHY + relativePath + "/" + complaintId + "/" + name;
            String format = StringUtils.substringAfterLast(name, ".");
            assetMgr.createAsset(newFile, is, "image/" + format, true);

            // Return the path to the file was stored
            return newFile;
        } catch (Exception e) {
            log.warn("Exception thrown while uploading images to DAM ", e);
        }
        return null;
    }

}

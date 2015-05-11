package com.upgov.core.impl.servlets;


import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;

@SlingServlet(paths = "/bin/servlets/dashDropDown")
public class DashboardDropdownServlet extends SlingSafeMethodsServlet {
    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        try{
            Resource resource = req.getResourceResolver().getResource("/etc/designs/up-gov-nodes");

            JSONArray department = getJsonArray(resource.getChild("department"));
            JSONArray location = getJsonArray(resource.getChild("location"));
            JSONArray users = getJsonArray(resource.getChild("users"));
            JSONObject object = new JSONObject();
            object.put("department",department);
            object.put("location",location);
            object.put("users",users);

            resp.getWriter().println(object);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    JSONArray getJsonArray(Resource rs){
        JSONArray array = new JSONArray();
        if(rs== null){
            JSONArray js =new JSONArray();
            return js;
        }
        Iterator<Resource> resIterator =  rs.listChildren();
        while(resIterator.hasNext()){
            JSONObject object =new JSONObject();
            Resource temp = resIterator.next();
            ValueMap properties = temp.getValueMap();
            try {
                object.put("title",properties.get("title",""));
                object.put("value",properties.get("value",""));
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            array.put(object);

        }
        return array;
    }

}

package com.upgov.core.impl.servlets;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

@SlingServlet(paths = "/bin/tag", extensions = "html", generateComponent = false)
@Component
public class TagListFetcher extends SlingSafeMethodsServlet {
    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        String path = req.getParameter("path");
        if (path != null && (!path.equals(""))) {
            Resource resource = req.getResourceResolver().getResource(path);
            Iterator<Resource> itr = resource.listChildren();
            Resource tempRes = null;
            String text = "", value = "";
            JSONObject jsonObject =null;
            JSONArray jsonArray = new JSONArray();
            try{
            while (itr.hasNext()) {
                tempRes = itr.next();
                ValueMap properties = tempRes.adaptTo(ValueMap.class);
                text = properties.get("jcr:title", "");
                value = tempRes.getName();
                jsonObject = new JSONObject();
                jsonObject.put("text",text);
                jsonObject.put("value",value);
                jsonArray.put(jsonObject);

            }
                PrintWriter out = resp.getWriter();
                out.println(jsonArray);

            }catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}

package com.upgov.core.impl.servlets;

import com.day.cq.commons.ImageHelper;
import com.day.image.Layer;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.servlet.ServletException;
import java.awt.*;
import java.io.IOException;

@Component
@Service
@org.apache.felix.scr.annotations.Properties({
        @org.apache.felix.scr.annotations.Property(name="sling.servlet.resourceTypes", value="sling/servlet/default"),
        @org.apache.felix.scr.annotations.Property(name="sling.servlet.selectors", value={"resize","thumb","thumbnail"}),
        @org.apache.felix.scr.annotations.Property(name="sling.servlet.extensions", value={"jpg", "png", "gif"}),
        @org.apache.felix.scr.annotations.Property(name="sling.servlet.methods", value="GET")
})

public class UPGovImageServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String ORIGINAL_PATH = "/jcr:content/renditions/original/jcr:content";
    private static final Dimension DEFAULT_DIMENSION = new Dimension(100, 100);
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        try {
            String[] selectors = request.getRequestPathInfo().getSelectors();
            String extension = request.getRequestPathInfo().getExtension();
            String imagePath = request.getRequestPathInfo().getResourcePath().substring(0, request.getRequestPathInfo().getResourcePath().indexOf("."));
            int maxHeight = selectors.length > 1 ? Integer.valueOf(selectors[1]).intValue() : (int)DEFAULT_DIMENSION.getHeight();
            int maxWidth = selectors.length > 2 ? Integer.valueOf(selectors[2]).intValue() : (int)DEFAULT_DIMENSION.getWidth();
            boolean margin = (selectors.length > 3) && (selectors[3].equals("margin"));

            Session session = (Session)request.getResourceResolver().adaptTo(Session.class);
            Node imageNode = session.getNode(imagePath+"."+extension+ORIGINAL_PATH);

            Property data = imageNode.getProperty("jcr:data");
            Layer imageLayer = ImageHelper.createLayer(imageNode.getSession(), imageNode.getPath());
            imageLayer.resize(maxWidth, maxHeight);

            response.setContentLength((int)data.getLength());
            response.setContentType(imageLayer.getMimeType());
            imageLayer.write(imageLayer.getMimeType(), imageLayer.getMimeType().equals("image/gif") ? 255 : 1.0, response.getOutputStream());

        } catch (PathNotFoundException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }


}

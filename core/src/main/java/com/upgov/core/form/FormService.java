package com.upgov.core.form;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: shivani
 * Date: 10/2/15
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FormService {

    String execute(SlingHttpServletRequest request,SlingHttpServletResponse response) throws IOException, ServletException;
}

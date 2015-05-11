<%--
    ADOBE CONFIDENTIAL
    __________________

     Copyright 2013 Adobe Systems Incorporated
     All Rights Reserved.

    NOTICE:  All information contained herein is, and remains
    the property of Adobe Systems Incorporated and its suppliers,
    if any.  The intellectual and technical concepts contained
    herein are proprietary to Adobe Systems Incorporated and its
    suppliers and are protected by trade secret or copyright law.
    Dissemination of this information or reproduction of this material
    is strictly forbidden unless prior written permission is obtained
    from Adobe Systems Incorporated.
--%><%
%><%@include file="/libs/foundation/global.jsp" %><%
%><%@ page session="false"
           import="java.util.Iterator,
                    com.day.cq.wcm.foundation.Image,
                    com.day.cq.wcm.foundation.List,
                    com.adobe.cq.mobile.angular.data.util.FrameworkContentExporterUtils,
	                org.apache.sling.api.resource.Resource,
	                org.apache.sling.api.resource.ValueMap,
	                com.day.cq.wcm.api.Page" %>
<%-- initialize the list --%>
<cq:include script="init.jsp"/>
<div>
    <ul class="topcoat-list__container">
<%

    List list = (List)request.getAttribute("list");
    Iterator<Page> items = list.getPages();
    if (items != null) {
        while (items.hasNext()) {
            Page carouselPage = items.next();
            ValueMap vm = carouselPage.getProperties();
            // Only include Angular pages
            Resource currentPageContentResource = carouselPage.getContentResource();
            // Skip this page if it is not based on ng-page
            if (currentPageContentResource == null ||
                    !currentPageContentResource.isResourceType(FrameworkContentExporterUtils.NG_PAGE_RESOURCE_TYPE)) {
                continue;
            }

            String title = (carouselPage.getTitle() == null ? "" : carouselPage.getTitle());
%><%
%>        <li class="topcoat-list__item">
            <a x-cq-linkchecker="skip" ng-click="go('<%= request.getContextPath() %><%= xssAPI.getValidHref(carouselPage.getPath()) %>')"><%= xssAPI.encodeForHTML(title) %>
                <div class="arrow-right right"></div>
                <span class="right"><%= xssAPI.getValidHref(vm.get("itemCount", "")) %></span>
            </a>
        </li><%
%><%
        }
    }
%>
    </ul>
</div>
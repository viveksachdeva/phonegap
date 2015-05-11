<%--
	ADOBE CONFIDENTIAL
	__________________

	 Copyright 2014 Adobe Systems Incorporated
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
                    com.day.cq.wcm.api.WCMMode,
                    com.day.cq.wcm.foundation.List,
					com.day.cq.wcm.foundation.Placeholder,
                    com.adobe.cq.mobile.angular.data.util.FrameworkContentExporterUtils,
	                org.apache.sling.api.resource.Resource,
	                com.day.cq.wcm.api.Page" %><%
%><c:set var="wcmMode"><%= WCMMode.fromRequest(request) != WCMMode.DISABLED %></c:set><%

    // Carousel playspeed
    String playSpeed = properties.get("playSpeed", "4000");
%>

<%-- initialize the list --%>
<cq:include script="init.jsp"/>

<c:choose>
    <c:when test="${empty list.pages}">
        <c:if test="${wcmMode}"><%
            String classicPlaceholder =
                    "<img src=\"/libs/cq/ui/resources/0.gif\" class=\"cq-carousel-placeholder\" alt=\"\">";
            String placeholder = Placeholder.getDefaultPlaceholder(slingRequest, component, classicPlaceholder);%>
            <%= placeholder %>
        </c:if>
    </c:when>
    <c:otherwise>
        <div cq-swipe-carousel auto="<%= xssAPI.encodeForHTMLAttr(playSpeed) %>">
            <div class="swipe-wrap"><%

            // Determine the top level app resource
            Resource topLevelAppResource = FrameworkContentExporterUtils.getTopLevelAppResource(currentPage.adaptTo(Resource.class));

            List list = (List)request.getAttribute("list");
            Iterator<Page> items = list.getPages();
            int numberOfPages = 0;
            if (items != null) {
                while (items.hasNext()) {
                    Page carouselPage = items.next();
                    // Only include Angular pages
                    Resource currentPageContentResource = carouselPage.getContentResource();
                    // Skip this page if it is not based on ng-page
                    if (currentPageContentResource == null ||
                            !currentPageContentResource.isResourceType(FrameworkContentExporterUtils.NG_PAGE_RESOURCE_TYPE)) {
                        continue;
                    }

                    numberOfPages++;
                    String imgSrc = "#";
                    Resource imageResource = carouselPage.getContentResource("image");
                    if (imageResource != null) {
                        boolean appExport = Boolean.parseBoolean(slingRequest.getParameter("appExport"));
                        // image.getExtension() returns the incorrect extension, so .png has been hardcoded
                        imgSrc = carouselPage.getPath() + ".img.png";
                        imgSrc = FrameworkContentExporterUtils.getPathToAsset(topLevelAppResource, imgSrc, appExport);
                    }

                    String title = (carouselPage.getTitle() == null ? "" : carouselPage.getTitle());
                    String description = (carouselPage.getDescription() == null ? "" : carouselPage.getDescription());
                    String navTitle = (carouselPage.getNavigationTitle() == null ? title : carouselPage.getNavigationTitle());
        %>
                <div class="swipe-carousel-slide">
                    <img src="<%= xssAPI.getValidHref(imgSrc) %>" alt="<%= xssAPI.encodeForHTMLAttr(title) %>" />

                    <a x-cq-linkchecker="skip" class="carouselImageOverlay" ng-click="go('<%= request.getContextPath() %><%= xssAPI.encodeForHTMLAttr(carouselPage.getPath()) %>')" title="<%= xssAPI.encodeForHTMLAttr(title) %>">
                        <h1 class="title"><%= xssAPI.encodeForHTML(title) %></h1>
                        <h4 class="description"><%= xssAPI.encodeForHTML(description) %></h4>
                        <div class="bottom-center">
                            <div class="carouselImageLink"><%= xssAPI.encodeForHTML(navTitle) %></div>
                        </div>
                    </a>
                </div>
        <%
                }
            }
        %>
            </div>

            <ul class="swipe-position">
                <%
                    for (int i = 0; i < numberOfPages; i++) {
                %>
                <li class="<%= (i == 0) ? "active" : "" %>"></li>
                <%
                    }
                %>
            </ul>
        </div>
    </c:otherwise>
</c:choose>
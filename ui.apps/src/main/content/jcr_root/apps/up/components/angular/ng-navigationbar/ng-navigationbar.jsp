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
%><%@page session="false"
          import="com.adobe.cq.mobile.angular.data.util.FrameworkContentExporterUtils" %><%
%><%@include file="/libs/foundation/global.jsp" %><%

    String headerText = currentPage.getTitle();
    String headerImage = currentPage.getProperties().get("headerImage", "");
    boolean useImage = (headerImage != null && headerImage.length() > 0);

    boolean isTopLevelAppPage = FrameworkContentExporterUtils.isTopLevelAppResource(currentPage.adaptTo(Resource.class));

%><c:set var="useImage"><%= useImage %></c:set><%
%><c:set var="isTopLevelAppPage"><%= isTopLevelAppPage %></c:set><%
%>
<div class="topcoat-navigation-bar">
	<div class="topcoat-navigation-bar__item left quarter">
        <c:choose>
            <c:when test="${isTopLevelAppPage}">
                <a class="topcoat-icon-button--quiet" ng-click="toggleMenu()">
                    <span class="topcoat-icon topcoat-icon--menu-stack"></span>
                </a>
            </c:when>
            <c:otherwise>
                <a ng-click="back()" class="topcoat-icon-button--quiet">
                    <span class="topcoat-icon topcoat-icon--back"></span>
                </a>
            </c:otherwise>
        </c:choose>
	</div>
	<div class="topcoat-navigation-bar__item center half">
        <c:choose>
            <c:when test="${useImage}">
                <span class="logo-wrapper"><img class="logo" src="<%= xssAPI.getValidHref(headerImage) %>"></span>
            </c:when>
            <c:otherwise>
                <h1 class="topcoat-navigation-bar__title"><%= xssAPI.encodeForHTML(headerText) %></h1>
            </c:otherwise>
        </c:choose>
	</div>

</div>

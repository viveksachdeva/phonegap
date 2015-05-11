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
%><%@ page session="false"
           import="com.adobe.cq.mobile.angular.data.util.FrameworkContentExporterUtils" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
         String casestudy = properties.get("casestudy")!=null?(String)properties.get("casestudy"):"Enter Case Study Summary";
	  String name = properties.get("name")!=null?(String)properties.get("name"):"Enter Name";


%><c:set var="componentDataPath"><%= FrameworkContentExporterUtils.getJsFriendlyResourceName(resource.getPath()) %></c:set><%
%>    <div ng-repeat="image in <c:out value='${componentDataPath}'/>">
    <li style="width: 100%; float: left; display: block;">
        <div class="thumbImage"><img src="{{image.imgSrc}}" draggable="false"></div>
    <div class="peapleSpeaks">
        <p><%=casestudy%>
        </p>

        <p><span><%=name%></span></p>
    </div>
    <div class="seeStory">
        <a href="javascript:void(0)">${properties.linkText}</a>
    </div>
</li>
</div>

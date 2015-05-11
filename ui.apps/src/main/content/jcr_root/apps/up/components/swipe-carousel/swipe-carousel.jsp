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
			import="com.day.cq.wcm.api.WCMMode,
					com.day.cq.wcm.api.components.DropTarget" %><%
%><c:set var="wcmMode"><%= WCMMode.fromRequest(request) != WCMMode.DISABLED %></c:set><%
%><c:set var="ddClassName"><%= DropTarget.CSS_CLASS_PREFIX + "pages" %></c:set>

<c:if test="${wcmMode}">
    <div class="<c:out value='${ddClassName}'/>">
</c:if>

<cq:include script="template.jsp"/>

<c:if test="${wcmMode}">
    </div>
</c:if>

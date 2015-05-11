<%--

  Frequently Asked Questions component.

  

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page session="false"
           import="com.day.cq.commons.Doctype,
                   com.day.cq.wcm.foundation.Image,
                   com.day.cq.wcm.foundation.Placeholder,
                   com.day.cq.wcm.api.components.DropTarget,
                   java.util.Map,
                   com.adobe.cq.mobile.angular.data.util.FrameworkContentExporterUtils,
                   org.apache.sling.api.resource.Resource,
                   com.day.cq.wcm.api.WCMMode" %><%
%>

    <%
	Image image = new Image(resource);
%><c:set var="wcmMode"><%= WCMMode.fromRequest(request) != WCMMode.DISABLED %></c:set><%
%><c:set var="ddClassName"><%= DropTarget.CSS_CLASS_PREFIX + "image" %></c:set><%
%><c:set var="imageHasContent"><%= image.hasContent() %></c:set><%
%>


<c:choose>

    <c:when test="${!imageHasContent}">
        <c:if test="${wcmMode}">
            <img class="<c:out value='${ddClassName}'/> cq-image-placeholder" src="/etc/designs/default/0.gif">
        </c:if>
    </c:when>
    <c:otherwise>
        <cq:include script="template.jsp"/>
    </c:otherwise>
</c:choose>



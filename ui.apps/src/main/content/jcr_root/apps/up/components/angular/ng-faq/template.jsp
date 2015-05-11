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
%>
<%@ page session="false"
         import="com.adobe.cq.mobile.angular.data.util.FrameworkContentExporterUtils" %>
<%@ page import="com.day.cq.wcm.foundation.Image" %>
<%
%>
<%@include file="/libs/foundation/global.jsp" %>

<%
    String question = properties.get("question") != null ? (String) properties.get("question") : "Enter the Question for FAQ";
    String answer = properties.get("answer") != null ? (String) properties.get("answer") : "Enter the related Answer for FAQ";
    String quote = properties.get("quote") != null ? (String) properties.get("quote") : "Please enter the quote";
    String submitComplaintText = properties.get("submitComplaintText") != null ? (String) properties.get("submitComplaintText") : "Please enter the submit text";
%>
<%
%><c:set var="componentDataPath"><%= FrameworkContentExporterUtils.getJsFriendlyResourceName(resource.getPath()) %>
</c:set><%
%>
<style type="text/css">
    @media only screen and (max-width: 479px){
        .threeSteps .steps .step1 {
            text-decoration: none !important;
            color: #ff4d40 !important;
        }

        .threeSteps .steps .step2 {
            text-decoration: none !important;
            color: #ff9540 !important;
        }

        .commonManWrapper .commonMan .seeStory a {
            width: 80% !important;
        }

        .commonManWrapper .commonMan .seeStory {
            width: 225px !important;
        }
    }
</style>

<div ng-repeat="image in <c:out value='${componentDataPath}'/>">
    <%
        boolean appExport = Boolean.parseBoolean(slingRequest.getParameter("appExport"));
        Image spotlightImage = new Image(resource);
        String currentUrl = spotlightImage.getSrc();
        String modifiedUrl = currentUrl.substring(0, currentUrl.indexOf("ng_faq")).replace("jcr:content", "_jcr_content") + "ng_faq.img.png";
        if (appExport) {
            modifiedUrl = "http://qa3.intelligrape.net:11020" + modifiedUrl;
        }
    %>
    <div class="width100Per spotlight" style="background:url(<%=modifiedUrl%>)">
        <div class="container">
            <div class="quotes">
                <img><%=quote%>.<br>
            <span>&ndash; Hon. Chief Minister, Shri Akhilesh Yadav <img
                    src="<%=currentDesign.getPath()%>/img/right-quote.png"></span>
            </div>
        </div>
        <div class="threeSteps">
            <h2>3 Steps to a Better State</h2>

            <div class="container">
                <div class="steps">
                    <ul>
                        <li><a
                                href="javascript:void(0)"
                                ng-click="go('/content/phonegap/up/apps/ng-up/en/home/fileComplaint')"><img
                                class="flow-part-1"><br><span class="step1">Submit a complaint</span></a>/li>
                        <li><a
                                href="javascript:void(0)"
                                ng-click="go('/content/phonegap/up/apps/ng-up/en/home/trackComplaint')"
                                x-cq-linkchecker="skip"><img class="flow-part-2"><br><span class="step2">Track your complaint</span></a>
                        </li>
                        <li><img class="flow-part-3"><br><span class="step3"><a
                                href="javascript:;">See the solution</a></span></li>
                    </ul>
                </div>
                <p><%=submitComplaintText%>
                </p>
            </div>
        </div>
    </div>

</div>

<div style="clear:both"></div>









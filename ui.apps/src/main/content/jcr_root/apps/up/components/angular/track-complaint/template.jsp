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
<%
%>
<%@include file="/libs/foundation/global.jsp" %>

<%
%><c:set var="componentDataPath"><%= FrameworkContentExporterUtils.getJsFriendlyResourceName(resource.getPath()) %>
</c:set><%
%>

<div ng-repeat="image in <c:out value='${componentDataPath}'/>">
    <style>
        .trackContainer .formControlTrack input {
            width: 100%;
            margin-bottom: 10px;
        }

        .trackContainer .formControlTrack button {
            margin-left: 25%;
        }

        .stepText {
            left: 0 !important;
        }

        .step2 .stepText {
            left: -5px !important;
        }

        .trackContainer .progressInfo {
            border-top: none !important;
        }

        .breadcrumb li a.done-state{
            background: hsla(147, 57%, 50%, 1);
        }

        .breadcrumb li a.done-state:after{
            border-left-color: hsla(147, 57%, 50%, 1);
        }

    </style>

    <div class="width100Per registration">
        <div class="container">
            <h1 class="reg_title"><span>Track Your Complaint</span></h1>

            <div class="trackContainer">
                <div class="formControlTrack">
                    <input type="text" class="formInputTrack" name="complainNum" value="Complaint Number"
                           onblur="(this.value == '') &amp;&amp; (this.value = 'Complaint Number')"
                           onfocus="(this.value == 'Complaint Number') &amp;&amp; (this.value = '')">
                    <button type="submit" class="track_btn" onclick="trackAndUpdateUI()">Track</button>
                </div>


            </div>
        </div>
    </div>

    <div class="complaint-tracking-template" style="display: none">
        <hr style="clear: both; border-color: white" class="horizontal-break">
        <h2 class="complaint-id"></h2>
        <div class="breadcrumbsHolder">
            <ul class="breadcrumb">
                <li><a href="#">Complaint Received</a></li>
                <li><a href="#">Complaint Processed</a></li>
                <li><a href="#">Complaint Delivered</a></li>
            </ul>
        </div>
        <div class="progressSteps">
            <div class="steps">
                <div class="step1"><img class="active">
                    <span class="stepText"></span></div>
                <div class="step2"><img class="inactive"><span class="stepText"></span></div>
                <div class="step3"><img class="inactive"><span class="stepText"></span></div>
            </div>
        </div>
        <div class="progressInfo"></div>
    </div>



</div>
<div style="clear:both;"></div>


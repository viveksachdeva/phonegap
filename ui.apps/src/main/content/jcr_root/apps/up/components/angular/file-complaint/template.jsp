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

    <style type="text/css">
        .faqWrapper form ul li {
            list-style-type: none;
            margin-bottom: 15px;
        }

        .faqWrapper form ul li label {
            display: block;

        }

        .faqWrapper form ul li input, .faqWrapper form ul li textarea, .faqWrapper form ul li select {
            min-width: 220px;
            max-width: 100%;
            background: #e4d7cd;
        }

        .faqWrapper .navBottom {
            width: 100%
        }

        .width100PerComplaint {
            width: 100%;
            background-color: #ddcdc0;
        }

        .complaint-confirmation {
            padding: 50px;
        }

        .otp-verification ul {
            padding-top: 40px;
        }

        .otp-verification ul li label {
            padding-bottom: 10px;
        }

        .errorField {
            color: red;
        }
        .loader {
            position: fixed;
            left: 0;
            top: 0;
            opacity: .5;
            width: 100%;
            height: 100%;
            z-index: 9999;
            background: url('/etc/designs/up/clientlib/img/spinner.png') 50% 50% no-repeat rgb(249,249,249);
        }
    </style>


    <div class="loader" style="display: none"></div>
    <div class="width100PerComplaint">
        <div class="container1000Px">
            <div class="faqWrapper">
                <div class="navBottom">File a Complaint</div>

                <form name="complaint-form">
                    <input type="hidden" name="unverifiedFlag" value="true"/>
                    <ul>
                        <li class="form-error errorField" style="display: none;"></li>
                        <li>*All fields are mandatory</li>

                        <li><label>Name</label> <input type="text" name="name" data-nice-name="Name"></li>
                        <li><label>Email</label> <input type="text" name="email" data-nice-name="Email"></li>
                        <li><label>Mobile</label><input type="number" name="mobile" maxlength="10" data-nice-name="Mobile"></li>
                        <li><label>What is your complaint related to?</label><select name="department" data-nice-name="Department">
                            <option value="">Select a department</option>
                            <option value="agriculture">Agriculture</option>
                            <option value="roads">Roads and Buildings</option>
                            <option value="water">Water Supply</option>
                        </select></li>

                        <li><label>What problem are you facing?</label><textarea name="complaint" data-nice-name="Complaint"
                                                                                 rows="5"></textarea></li>

                        <li><label>Pin Code</label><input type="number" name="pincode" maxlength="6" data-nice-name="Pin Code"></li>
                        <li><label>District</label><select name="location" data-nice-name="District">
                            <option value="">Select District</option>
                            <option value="lucknow">Lucknow</option>
                            <option value="kanpur">Kanpur</option>
                            <option value="jaunpur">Jaunpur</option>
                            <option value="bareily">Bareily</option>
                            <option value="gorakhpur">Gorakhpur</option>
                            <option value="agra">Agra</option>
                            <option value="muzzafarnagar">Muzaffarnagar</option>
                            <option value="aligarh">Aligarh</option>
                            <option value="meerut">Meerut</option>
                        </select></li>
                        <li>
                            <label>Upload Image</label><input type="file" id="complaintImage"
                                                              accept="image/x-png, image/gif, image/jpeg">
                        </li>
                        <li>
                            <div class="submit_box"><a href="javascript:void(0)" onclick="submitComplaintForm()"><img
                                    alt="Submit"
                                    title="Submit"></a>
                            </div>
                        </li>
                    </ul>

                </form>


            </div>
            <div class="faqWrapper" style="display: none">
                <div class="otp-verification">
                    <div class="navBottom">Verify Mobile Number</div>

                    <form name="otp-verification-form">
                        <input type="hidden" name="mobile" value="">
                        <ul>
                            <li><label>Verification code has been sent to your mobile. Kindly verify</label> <input
                                    type="text" name="otp" required=""></li>
                            <li>
                                <div class="submit_box"><a href="javascript:void(0)" onclick="checkOtp()"><img
                                        alt="Submit"
                                        title="Submit"></a>
                                </div>
                            </li>
                        </ul>

                    </form>
                </div>
                <div class="complaint-confirmation" style="display: none">
                    <span></span>
                </div>


            </div>
        </div>
    </div>

</div>
<div style="clear:both;"></div>

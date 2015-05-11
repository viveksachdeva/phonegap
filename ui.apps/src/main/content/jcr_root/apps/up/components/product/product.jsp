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
%><%@ page session="false"
           import="com.adobe.cq.commerce.api.Product,
                   org.apache.sling.api.resource.Resource,
                   com.day.cq.i18n.I18n" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@include file="/apps/geometrixx-outdoors-app/global.jsp"%><%

I18n i18n = new I18n(slingRequest);

    // Get the product this page represents
    Resource currentPageResource = currentPage.adaptTo(Resource.class);
    Product product = getProduct(currentPageResource);
    String summaryHTML = product.getProperty("summary", String.class);
    if (summaryHTML == null || summaryHTML.equals("...")) {
        summaryHTML = "";
    }
    String productPrice = "n/a";
    if(product != null) {
        productPrice = getProductPrice(product, currentPageResource, slingRequest, slingResponse);
        request.setAttribute("productPrice", productPrice);
    }

    // TODO: implement numberOfLikes and numberOfComments
%>
<article class="product-details" ng-controller="ProductCtrl">
    <div class="product-header">
        <span class="name"><%= xssAPI.encodeForHTML(product.getTitle()) %></span>
        <span class="price"><%= xssAPI.encodeForHTML(productPrice) %></span>
    </div>

    <div class="product-image">
        <cq:include path="ng-image" resourceType="geometrixx-outdoors-app/components/image" />

        <div class="metrics">
            <div class="metrics-button likes"> 0
                <%-- Include SRC in the IMG's below to support FireFox and IE, which do not support CSS content
                    changing. --%>
                <img class="thumbsup_img_src" height="24px"
                     src="<%= request.getContextPath() %>/etc/designs/phonegap/geometrixx/ng-outdoorsapp/ng-clientlibsall/img/ThumbUp_24.svg"
                     alt="<%= i18n.get("Number of Likes") %>">
            </div>
            <div class="metrics-button comments"> 0
                <img class="comment_img_src" height="24px"
                     src="<%= request.getContextPath() %>/etc/designs/phonegap/geometrixx/ng-outdoorsapp/ng-clientlibsall/img/Comment_18.svg"
                     alt="<%= i18n.get("Number of Comments") %>">
            </div>
            <div class="add-to-cart" data-sku="<%= xssAPI.encodeForHTMLAttr(product.getSKU()) %>">
                <img class="added_img_src clear" height="40px"
                     src="<%= request.getContextPath() %>/etc/designs/phonegap/geometrixx/ng-outdoorsapp/ng-clientlibsall/img/Check-Circle_18_orange.svg"
                     alt="<%= i18n.get("Add to cart") %>">
                <img class="add_img_src" height="40px"
                     src="<%= request.getContextPath() %>/etc/designs/phonegap/geometrixx/ng-outdoorsapp/ng-clientlibsall/img/Add-Circle_18.svg"
                     alt="<%= i18n.get("Add to cart") %>"
                     ng-click="addToCartClickHandler($event)">
            </div>
        </div>
    </div>

    <div class="product-details-information">
        <h4 class="product-details-description"><%= xssAPI.encodeForHTML(product.getDescription()) %></h4>
        <div class="product-details-summary">
            <%= xssAPI.filterHTML(summaryHTML) %>
        </div>
    </div>

    <div class="product-comments">

    </div>
</article>
/*
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
 */
;(function(mobile, document, undefined) {
    /*
     * TODO: Replace this functionality with AngularJS directives
     */

    function createSearchResults(key) {

        //Hide search bar
        mobile.Utils.toggleClass(searchBarEl, "active");

        //TODO: basic search implementation based on local products
        //TODO: use templates to generate results page
        //TODO: convert to API that can search on client and server

        var foundPages = [],
            allPages = mobile.SinglePageNav.getAllPages(),
            currentPage = mobile.SinglePageNav.getCurrentPage();

        currentPage.innerHTML = "";

        //Look for pages whose name or contents contain the search term
        for (var path in allPages) {
            var page = allPages[path];
            if (path.indexOf(key.toLowerCase()) >=0 || page.innerHTML.toLowerCase().indexOf(key.toLowerCase()) >= 0) {
                foundPages.push(page);
            }
        }

        var view = document.createElement("div");
        view.setAttribute("class", "full responsive-row padded");

        for (var i=0; i<foundPages.length; i++) {
            var page = foundPages[i];
            //Only process product pages
            if (page.querySelector(".product-page")) {
                //Add product to view
                var title = page.querySelector("h1.topcoat-navigation-bar__title").textContent;
                var image = page.querySelector("img").cloneNode(true);

                var link = document.createElement("a");
                link.setAttribute("href", "#");
                link.setAttribute("data-href", page.id);
                link.appendChild(image);

                var item = document.createElement("div");
                item.setAttribute("class", "responsive-cell product-menu-item");
                item.appendChild(link);
                view.appendChild(item);
            }
        }

        currentPage.appendChild(view);

    }

})(CQ.mobile, document);

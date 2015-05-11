/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2013 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
;(function (angular, undefined) {

    "use strict";

    angular.module('cqProductList', []);

    function ProductListCtrl($scope) {

        var LIKED_INDICATED_CLASS = 'liked';

        $scope.likeClickHandler = function(event) {
            var thumbsup = angular.element(event.currentTarget);
            if (thumbsup.hasClass(LIKED_INDICATED_CLASS) === false) {
                angular.element(event.currentTarget).addClass(LIKED_INDICATED_CLASS);
            } else {
                angular.element(event.currentTarget).removeClass(LIKED_INDICATED_CLASS)
            }

            event.preventDefault();
            event.stopPropagation();
            return false;
        }
    };

    angular.module('cqProductList')
        .controller('ProductListCtrl', ["$scope", ProductListCtrl])

})(angular);
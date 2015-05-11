/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2014 Adobe Systems Incorporated
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

angular.module('cqSwipeCarousel', [])
.directive('cqSwipeCarousel', function ($parse, $timeout) {
    return {
        scope: true,
        link: function(scope, element, attrs) {
            element.addClass('swipe');

            var config = {};

            if ( attrs.auto ) {
                config.auto = parseInt(attrs.auto,10);
            }
            if ( attrs.startSlide ) {
                config.startSlide = parseInt(attrs.startSlide,10);
            }
            if ( attrs.speed ) {
                config.speed = parseInt(attrs.speed,10);
            }

            var swipePositionListItems = element.find("ul").children();

            config.callback = function (position) {
                // Use timeout to avoid stuttering transitions
                $timeout( function () {
                    // Update the swipe position indicators
                    angular.forEach(swipePositionListItems, function(listItem, key) {
                        if (position === key) {
                            angular.element(listItem).addClass("active");
                        }
                        else {
                            angular.element(listItem).removeClass("active");
                        }
                    });
                }, 1);
            };

            var swiperProperty = attrs.swiper || 'swiper';
            var swiper = new Swipe(element[0], config);

            scope[swiperProperty] = swiper;
        }
    };
});

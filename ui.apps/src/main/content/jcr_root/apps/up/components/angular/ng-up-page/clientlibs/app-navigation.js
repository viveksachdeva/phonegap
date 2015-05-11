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
;
(function( angular, undefined ) {

	"use strict";

	/**
	 * Module to handle general navigation in the app
	 */
	angular.module( 'cqAppNavigation', ['cqContentSyncUpdate'] )
		.controller( 'AppNavigationController', ['$scope', '$window', '$location', 'cqContentSyncUpdate',
			function( $scope, $window, $location, cqContentSyncUpdate ) {
				$scope.transition = '';
				$scope.updating = false;

				// Counter to indicate how far we've travelled from the root of the app
				var numberOfPagesFromRoot = 0;

				/**
				 * Handle back button
				 */
				$scope.back = function() {
					if( $scope.wcmMode == false ) {
						// Prevent any back action that will take us further than the root
						if (numberOfPagesFromRoot === 0) {
							return;
						}
						numberOfPagesFromRoot--;
					}
					$scope.transition = 'transition-right';
					$window.history.back();
					console.log( '[nav] handled back event.' );
				};

				/**
				 * Handle navigation to app pages
				 */
				$scope.go = function( path ) {
					if( window.ADB ) {
						ADB.trackState( path, {} );
					}
					if( $scope.wcmMode ) {
						// WCMMode is not disabled; head to the page itself
						navigateToPageInAuthorMode( path );
					}
					else {
						navigateToPageInApp( path );
					}

					console.log( '[nav] app navigated to: [' + path + '].' );
				};

				/**
				 * Toggle the menu
				 */
				$scope.toggleMenu = function() {
					if( window.ADB && !$scope.navigationMenuStatus ) {
						ADB.trackState( 'menu', {} );
					}

					$scope.navigationMenuStatus = !$scope.navigationMenuStatus;
				};

				/**
				 * Trigger an app update
				 */
				$scope.updateApp = function( $event ) {
					// don't start updating again if we're already updating.
					if($scope.updating) return;

					// Prevent this event from propagating
					$event.stopPropagation();
					$scope.updating = true;

					if( window.ADB ) {
						ADB.trackAction( 'updateApp', {} );
						ADB.trackTimedActionStart( 'updateAppTimed', {} );
					}

					try {
						cqContentSyncUpdate.fetchAndApplyDeltaUpdate().then(
							function( result ) {
								$scope.updating = false;
							},
							function( rejection ) {
								// todo: display error
								console.error( rejection );
								$scope.updating = false;
							}
						);
					} catch( err ) {
						console.log( 'Update Failed: ' + err );
						if( window.ADB ) {
							ADB.trackAction( 'updateAppFailed', {} );
						}
					}

					if( window.ADB ) {
						ADB.trackTimedActionEnd( 'updateAppTimed' );
					}
				};

				/**
				 * Handle navigation to product pages
				 */
				$scope.goProduct = function( templatePath, sku ) {
					if( $scope.wcmMode ) {
						// WCMMode is not disabled; head to the product page itself
						navigateToPageInAuthorMode( getFullProductPagePath( templatePath, sku ) );
					}
					else {
						navigateToPageInApp( templatePath + '/' + sku );
					}

					console.log( '[nav] app navigated to product: [' + sku + '].' );
				};

				/*
				 * Private helpers
				 */
				function navigateToPageInAuthorMode( path ) {
					$window.location.href = path + '.html';
				}

				function navigateToPageInApp( path ) {
					// SPA hash navigation
					numberOfPagesFromRoot++;
					$scope.transition = 'transition-left';
					$location.url( path );
					$scope.navigationMenuStatus = false;
				}

				function getFullProductPagePath( base, sku ) {
					// Sample SKUs are at least 6 chars long, but 4 is the min.
					if( sku.length < 4 ) {
						// Invalid SKU
						return null;
					}

					return base + '/' + sku.substring( 0, 2 ) + '/' + sku.substring( 0, 4 ) + '/' + sku;
				}
			}
		] );
})( angular );
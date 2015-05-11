#!/usr/bin/env node
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

//
// This hook copies various resource files
// from our version control system directories
// into the appropriate platform specific location
//

var filestocopy = {
///////////////////////////
//          iOS
///////////////////////////
    ios : [
// iOS Icons
        {
            "www/res/icons/ios/icon-72.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-72.png"
        },
        {
            "www/res/icons/ios/icon-57.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-57.png"
        },
        {
            "www/res/icons/ios/icon-57-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-57@2x.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-72@2x.png"
        },
// iOS Icons created as of PhoneGap cli 3.3
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-40.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-40@2x.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-50.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-50@2x.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-60.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-60@2x.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-76.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-76@2x.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-small.png"
        },
        {
            "www/res/icons/ios/icon-72-2x.png":
                "platforms/ios/Geometrixx/Resources/icons/icon-small@2x.png"
        },
// iOS Splash
        {
            "www/res/screens/ios/screen-iphone-portrait-2x.png":
                "platforms/ios/Geometrixx/Resources/splash/Default@2x~iphone.png"
        },
        {
            "www/res/screens/ios/screen-iphone-portrait-568h-2x.png":
                "platforms/ios/Geometrixx/Resources/splash/Default-568h@2x~iphone.png"
        },
        {
            "www/res/screens/ios/screen-iphone-portrait.png":
                "platforms/ios/Geometrixx/Resources/splash/Default~iphone.png"
        }
    ],
///////////////////////////
//          ANDROID
///////////////////////////
    android: [
// Android base Icon
        {
            "www/icon.png":
                "platforms/android/res/drawable/icon.png"
        },
// Android Icons
        {
            "www/res/icons/android/icon-36-ldpi.png":
                "platforms/android/res/drawable-ldpi/icon.png"
        },
        {
            "www/res/icons/android/icon-48-mdpi.png":
                "platforms/android/res/drawable-mdpi/icon.png"
        },
        {
            "www/res/icons/android/icon-72-hdpi.png":
                "platforms/android/res/drawable-hdpi/icon.png"
        },
        {
            "www/res/icons/android/icon-96-xhdpi.png":
                "platforms/android/res/drawable-xhdpi/icon.png"
        },
// Android Screens
        {
            "www/res/screens/android/screen-xhdpi-portrait_bg.png" :
                "platforms/android/res/drawable/screen.png"
        }/*,
        {
            "www/res/screens/android/screen-hdpi-landscape.png":
                "platforms/android/res/drawable-hdpi/splashlandscape.png"
        },
        {
            "www/res/screens/android/screen-hdpi-portrait.png":
                "platforms/android/res/drawable-hdpi/splashportrait.png"
        },
        {
            "www/res/screens/android/screen-ldpi-landscape.png":
                "platforms/android/res/drawable-ldpi/screenlandscape.png"
        },
        {
            "www/res/screens/android/screen-ldpi-portrait.png":
                "platforms/android/res/drawable-ldpi/screenportrait.png"
        },
        {
            "www/res/screens/android/screen-mdpi-landscape.png":
                "platforms/android/res/drawable-mdpi/screenlandscape.png"
        },
        {
            "www/res/screens/android/screen-mdpi-portrait.png":
                "platforms/android/res/drawable-mdpi/screenportrait.png"
        },
        {
            "www/res/screens/android/screen-xhdpi-landscape.png":
                "platforms/android/res/drawable-xhdpi/screenlandscape.png"
        },
        {
            "www/res/screens/android/screen-xhdpi-portrait.png":
                "platforms/android/res/drawable-xhdpi/screenportrait.png"
        }*/
    ]
};

var fs = require('fs');
var path = require('path');

// no need to configure below
var rootdir = process.argv[2];
var platforms = fs.readdirSync('platforms');

for(var i in platforms) {
    var platform = platforms[i];

    if (filestocopy[platform] == undefined) {
        continue;
    }

    filestocopy[platform].forEach(function(obj) {
        Object.keys(obj).forEach(function(key) {
            var val = obj[key];
            var srcfile = path.join(rootdir, key);
            var destfile = path.join(rootdir, val);

            var destdir = path.dirname(destfile);
            if (fs.existsSync(srcfile) && fs.existsSync(destdir)) {
                fs.createReadStream(srcfile).pipe(
                    fs.createWriteStream(destfile));
            }
        });
    });
};

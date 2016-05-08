# Minimal-News-Feed-Reader-Widget
Android widget to read Atom and RSS feeds on your home screen.  APK distributed on Google Play: https://play.google.com/store/apps/details?id=com.abk.mrw

## About

I was unable to find good examples of Android widgets in list-view form that utilized background services to retrieve data over the network.  I was also unable to find a decent RSS reader widget on Google Play that was also simple and single-purpose.  This repo kills the two birds.

## Build

```
$ gradle assemble
$ adb install app/build/output/apk/ap-debug.apk
```

## Atlas

### `app` Module

Contains all the UI classes for the widget.

### `common` Module

Contains the model and network I/O code.

### `notificationui` Module

Contains an experimental and unreleased notification-based UI.
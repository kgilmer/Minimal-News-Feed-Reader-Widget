# Minimal-News-Feed-Reader-Widget
Android widget to read Atom and RSS feeds on your home screen.  APK distributed on Google Play: https://play.google.com/store/apps/details?id=com.abk.mrw

## About

<div>
<img align="left" src="https://lh3.googleusercontent.com/IffD4mIAMt1HA1t16arEW5cUoI-4xAE8wRx9Gx8m5Vl1eGLmbXrMw8euujD88Ye_fw=h310-rw" alt="screenshot"/>
<p>
I was unable to find good examples of Android widgets in list-view form that utilized background services to retrieve data over the network.  I was also unable to find a decent RSS reader widget on Google Play that was also simple and single-purpose.  This repo aims to kill the two birds.
</p>
</div>
<br/>

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

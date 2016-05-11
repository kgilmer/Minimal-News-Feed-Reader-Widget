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

## Open to Contribution

If you're interested in learning more about Android development or how open source works, feel free to make a change and submit a pull request.  If it's a good change I'll accept the request, build, and release a new version of the app on Google Play.  A quick way to get started is to add your own favorite feed to the picker in this file: https://github.com/kgilmer/Minimal-News-Feed-Reader-Widget/blob/master/app/src/main/res/values/array.xml.

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

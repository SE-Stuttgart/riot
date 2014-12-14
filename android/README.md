# RioT Android

## IDE Setup (Eclipse)

### Prerequisites

- Everything in ../README.md
- JDK6 from Oracle (?? might not be necessary, another JDK might work, too)
- Android-SDK - In case you don't want the Android Studio, you find it here: http://developer.android.com/sdk/index.html#Other ("SDK Tools Only")
- From the Android SDK Manager, choose at least API21 and API16 and the latest Platform-tools and Build-tools.

### Plugins
Install the following plugins from the Eclipse Marketplace if not installed already:
- Android for Maven Eclipse (Upon installation, this plugin will probably ask you for the SDK path)

### Environment Variable
Create an environment variable named "ANDROID_HOME" that points to your Android SDK installation path (e.g. C:\Users\<xx>\AppData\Local\Android\android-sdk).
Then close all your console windows.

### Importing the projects
Import as described in ../README.md. In case you get a warning about missing Maven Plugin (Plugin execution not covered by lifecycle configuration),
use the "Discover new m2e connectors" QuickFix in this error message and let it download the plugin (again).
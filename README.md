# RioT Project

## IDE Setup (Eclipse)

### Prerequisites

- Install the lates JDK8 from Oracle. For Android, you might additionally need JDK6.
- Clone the git repository.

### Plugins
Install the following plugins from the Eclipse Marketplace if not installed already:

- Maven Integration for Eclipse
- Checkstyle Plug-in

### Eclipse Configuration
Apart from turning off the annoying spell checking in Eclipse, you should go to "General -> Content Types", select "Text -> Java Properties File", enter "UTF-8" as the default encoding below and confirm with "Update" and "OK".

### Import the Maven project
- Make sure the JDK8 (and JDK6, if applicable) is configured in the *Installed JREs* section of the Eclipse preferences.
- Open the import dialog via File->Import->...
- Select `Maven->Existing Maven Projects` and click *Next*.
- Select the folder with the cloned repository as root directory.
- Select the projects you want to import. You might want to avoid Android or Server/UserManagement, if you don't need them and don't have the prerequisites installed.

### Configure Checkstyle
- Windows -> Preference -> Checkstyle -> New
  - Type: Project Relative Configuration
  - Name: IoT
  - Location: Browse -> riot.commons\src\main\resources\checkstyle\conventions.xml
- Select new configuration as default
- Windows -> Preferences -> Java -> Code Style -> Formatter
  - Import -> riot.commons\src\main\resources\checkstyle\formatter.xml
  - Check that the imported formatter is selected in the project specific settings
- Optional: Windows -> Preferences -> Java -> Editor -> Save Actions -> Perform all selected actions on save
- Right-click on each project -> Checkstyle -> Activate Checkstyle 

## Testdata Creation
To use the integrated .sql scripts to fill your local database with test data, first set up the database (see README.md in server project).
Then open your ~/.m2/settings.xml and configure the server as follows (replace the password with your actual MySQL user password):

```html
<settings
        xmlns="http://maven.apache.org/Settings/1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/Settings/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
	<server>
		<id>riotMySQL</id>
		<username>riot</username>
		<password>123456</password>
	</server>
    </servers>
</settings>
```

Per project, all .sql files in src\*\resources\schema will be executed first, then all .sql files in src\*\resources\data, when you call
mvn generate-test-resources -Pmysql
You can also do this along with your regular build when you fetched a new version from Git and want to compile and test it:
mvn clean install -Pmysql

## Localization
All localized strings should be kept in "commons/src/main/resources/languages". The files created there should be named "xxx_yy.properties", where xxx is module/application domain that the strings inside belong to and yy is the locale code. When using Eclipse, it is important to use UTF-8-Encoding and a normal text editor (not the Eclipse visual GUI for properties files).

These .properties files are accessible in the Server applications and other applications that use the commons project through the respective classes in de.uni_stuttgart.riot.i18n (located in the commons project). These can be used like regular resource bundles.

For Android, there is a simpler alternative: The Maven goal "mvn initialize" in the Android project, which is also executed during the normal "install" build, will generate XML files for Android. Thus, the strings are available in "R.string.xxx_yyy" or "@string/xxx_yyy", where xxx is the prefix of the source properties file and yyy is the key of the string inside the file.

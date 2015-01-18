# RioT Project

## IDE Setup (Eclipse)

### Prerequisites

- Install the lates JDK8 from Oracle. For Android, you might additionally need JDK6.
- Clone the git repository.

### Plugins
Install the following plugins from the Eclipse Marketplace if not installed already:

- Maven Integration for Eclipse
- Checkstyle Plug-in

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
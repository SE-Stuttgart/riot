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

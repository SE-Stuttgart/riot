# R-IOT Web Application

This is the web application for the R-IOT project.


## IDE Setup (Eclipse)

### Prerequisites

- install the lates JDK8 from Oracle
- download and unpack the application server Glassfish 4.1 (Java e 7 Full Platform)  from https://glassfish.java.net/download.html 
- clone the git repository


### Plugins
Install the following plugins from the eclipse marketplace if not installed already:

- maven integration for Eclipse
- Glassfish Tools plugin
- Checkstyle Plug-in

### Import the Maven project
- make sure the JDK8 is configured in the *installed JREs* section of the Eclipse preferences
- Open the import dialog via File->Import->
- select `Maven->Existing Maven Projects` and click *next*
- select the folder with the cloned repository as root directory and click *finish*

### Configure the application server

- Add a new Server in Eclipse:
`File->new->other->Server->Glassfish 4`
- click *next*
- Server root: `<path to glassfish root>`/glassfish
- Java Development Kit: Java-8-oracle
- click *finish*
- select `run as->run on server` in the context menu of the project and choose the Glassfish server
- open http://localhost:4848 to check if the application was started on the server

### Configure Checkstyle
- Windows -> Preference -> Checkstyle -> New
  - Type: External Configuration File
  - Name: IoT
  - Location: Browse -> webapp\src\main\resources\checkstyle\conventions.xml
- Select new configuration as default
- Windows -> Preferences -> Java -> Code Style -> Formatter
  - Import -> webapp\src\main\resources\checkstyle\formatter.xml
  - Check that the imported formatter is selected in the project specific settings
- Windows -> Preferences -> Java -> Editor -> Save Actions -> Perform all selected actions on save
- Right-click on Project -> Checkstyle -> Activate Checkstyle 

## Database Setup

- install the latest mysql-server
- open the mySQL shell as administrative user (e.g. with `mysql -u root -p`)
  - create a user for the riot application: `CREATE USER 'riot'@'localhost' IDENTIFIED BY 'password';`
  - create a database for the riot app: `CREATE DATABASE riot CHARACTER SET utf8;`
  - grant permissions: `GRANT ALL ON riot.* TO 'riot'@'%' WITH GRANT OPTION; FLUSH PRIVILEGES;`
- download the mysql-connector jar from https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.34.zip
- unzip the package and put the jar into the domain/lib folder of your glassfish installation
- restart glassfish and open the admin panel: http://localhost:4848/common/index.jsf
- create a new JDBC connection pool to mysql JDBC->JDBC Connection Pools->New...
  - Select MySql as Database Driver Vendor and javax.sql.ConnectionPoolDataSource as Resource Type, click next
  - add properties "user:riot", "password:yourpassword", "databaseName:riot" and save
- go back to jdbc and create a resource with the jndi name "jdbc/riot-pool" and reference the connection pool
- the jndi name can now be used to access the pool in the jee containers

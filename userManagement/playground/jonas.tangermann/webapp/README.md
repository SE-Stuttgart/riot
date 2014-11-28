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

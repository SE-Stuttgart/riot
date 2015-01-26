# RioT Webapp

## Prerequisites

The following sofware components are needed to setup the development environment:

 - [node](http://nodejs.org/)
 - [npm](https://www.npmjs.com/)
 - [grunt](http://gruntjs.com/)
 - [bower](http://bower.io/)
 - [Yeoman](http://yeoman.io/)
 
## Maven integration

It is not necessary to install all prerequisites for building the application.
The build and test process can be triggered with maven via `mvn build` and `mvn test` respectively.
The required binaries are then downloaded and executed via a maven plugin.

The generated files might not be shown immediately in eclipse unless the following setting is activated:
`preferences->general->workspace->refresh using native hooks or polling`

### Testing
By default maven triggers headless tests via grunt (no real browsers are used to run karma tests).
You can change this behaviour by specifying the property `<webapp.test.goal>test</webapp.test.goal>`
in your local `m2/settings.xml` file. If Karma, the js test runner, has issues discovering installed
browsers then manually set the appropriate environment variables
(e.g. with `export CHROME_BIN=/usr/bin/chromium-browser` on Linux).
 

### Install on Linux

    sudo apt-get install nodejs
    sudo apt-get install npm
    sudo npm install -g grunt-cli
    sudo npm install -g bower
    sudo npm install -g yo

It may be necessary to install the legacy package of node with the following command.

    sudo apt-get install nodejs-legacy

### Install on Windows

 - Downlod the [installer](http://nodejs.org/download/) (the .msi package)
 - **Important:** Node has to be added to the `PATH` in Windows.

After the installation is complete:

    npm install -g grunt-cli
    npm install -g bower
    npm install -g yo

It may be necessary to add the `NODE_PATH` variable to your path in order to install `yo`: 

    setx NODE_PATH "%NODE_PATH%;C:\Users\<YOUR USERNAME>\AppData\Roaming\npm\node_modules"

## Development

### Setup

Run the following commands in the root directory of the webapp to load the required dependencies.

    npm install
    bower install

If you get a prompt during installation with the message "Unable to find a suitable version for angular, please choose one", select angular version 1.2.28 to download.


## Project Structure

The project follows the best practice guidelines for AngularJS where specific functions are grouped together into a component. Each component consists of its own folder which contains the necessary angular views, controllers, directives, tests and much more. A component can further be separated into sub-components. This structure allows a clean development process where developers should firstly consider the separation of concern before developing and create new components accordingly.

### Grunt

The project uses [grunt](http://gruntjs.com/) for performing repetitive tasks like minification, compilation and unit testing. The following grunt tasks are available:

 - grunt **build**: Build the project. This will create the project in the *dist* directory, ready for deployment on the server.
 - grunt **serve**: This is the main task which should be used during development. The task will start a webserver and watch any changes on the files. Use [Livereaload](http://livereload.com/) to automatically reload the website on changes.
 - grunt **test**: Run the unit tests.

### Yeoman
For further automation the [Yeoman](http://yeoman.io/) project is used. The project structure uses the [cg-angular](https://github.com/cgross/generator-cg-angular) which has additional generators for adding new angular views, directives, filters and much more. Read the github page of the angular generator for further details.

This means for common tasks you will simply run a command and don't need to create and integrate the files manually.

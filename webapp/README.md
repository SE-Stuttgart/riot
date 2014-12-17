# RioT Webapp

## Prerequisites

The following sofware components are needed to setup the build environment:

 - [node](http://nodejs.org/)
 - [npm](https://www.npmjs.com/)
 - [grunt](http://gruntjs.com/)
 - [bower](http://bower.io/)
 - [Yeoman](http://yeoman.io/)

### Install on Linux

    sudo apt-get install nodejs
    sudo apt-get install npm
    npm install -g grunt-cli
    npm install -g bower
    npm install -g yo

### Install on Windows

TODO

## Development

### Setup

Run the following commands to load the required dependencies.

    npm install
    bower install

### Grunt

The project uses [grunt](http://gruntjs.com/) for performing repetitive tasks like minification, compilation and unit testing. The following grunt tasks are available:

 - grunt **build**: Build the project. This will create the project in the *dist* directory, ready for deployment on the server.
 - grunt **serve**: This is the main task which should be used during development. The task will start a webserver and watch any changes on the files. Use [Livereaload](http://livereload.com/) to automatically reload the website on changes.
 - grunt **test**: Run the unit tests.

### Yeoman
For further automation the [Yeoman](http://yeoman.io/) project is used. The project structure uses the [angular generator](https://github.com/yeoman/generator-angular) which has additional generators for adding new angular controllers, directives, filters and much more. Read the github page of the angular generator for further details.

This means for common tasks you will simply run a command and don't need to create and integrate the files manually.

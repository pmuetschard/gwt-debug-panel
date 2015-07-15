

# Building the Source #

This document explains how to setup the build environment to build the Debug Panel distributable from the SVN source tree. The source tree contains an [Ant](http://ant.apache.org/) build file, as well as [Eclipse](http://www.eclipse.org/) project settings that allow you to easy build the project.

There is a [README](http://code.google.com/p/gwt-debug-panel/source/browse/trunk/README) file included in the source tree, which contains the same information.

## Build Requirements ##

To build the project you will need
  * [JDK 1.5](http://java.sun.com/") - Note that in order to run the tests as well as the included example in hosted mode, you will need a 32-bit version of the JDK.
  * [GWT 1.6.2](http://code.google.com/webtoolkit/download.html) - If you are using Eclipse, it is highly recommended to use the [Google Plugin for Eclipse](http://code.google.com/eclipse/).
  * [Ant 1.6](http://ant.apache.org/) - If you are building from the command line
  * [jMock 2](http://www.jmock.org/download.html) - Required for running the tests. Note that jMock includes [Hamcrest 1.1](http://code.google.com/p/hamcrest/), which is needed as well.
  * [Emma](http://emma.sourceforge.net/downloads.html) - If you wish to generate test coverage reports.


---


## Building From the Command Line ##

  1. [Checkout](http://code.google.com/p/gwt-debug-panel/source/checkout) the trunk and change into the directory where you checked out the code to.
  1. If you need to, download and extract GWT
  1. Point the build file to where GWT is by either
    * Creating a symbolic link called `gwt` in the trunk of the tree pointing to where you have extracted GWT
    * Editing the `build.xml` file and setting the `gwt` property to where GWT is
    * Passing the `-Dgwt=</path/to/GWT>` flag on each of the below ant calls
  1. Point the build file to the dependency libraries for the unit tests and code coverage
    1. Create a folder called `libs` in the trunk (or create it with a different name anywhere else)
    1. Copy/Link `jmock.jar`, `hamcrest-core.jar` and `hamcrest-library.jar` into this folder
    1. Optionally, copy/link `emma.jar` into the folder as well
    1. If you didn't create the folder in the trunk either
      * Edit the `build.xml` file and set the `libs` property to where you created the folder
      * Pass the `-Dlibs=</path/to/folder>` flag on each of the below ant calls
  1. Execute `ant -p` to get info on the project's targets
    * `ant package` will build the Debug Panel package
    * `ant test` will execute the tests
    * `ant build` (default) will build the package as well as execute the tests
    * `ant example` will run the example in hosted mode
    * `ant deploy.example` will deploy the example as a WAR file
    * `ant coverage` will execute the tests gathering code coverage statistics with Emma

All the build artifacts will be located in the `build` folder.


---


## Building From Eclipse ##

  1. Ensure that you have a 32-bit 1.5 (or better) JRE installed for Eclipse (Check Window -> Preferrences, Java -> Installed JREs)
  1. [Checkout](http://code.google.com/p/gwt-debug-panel/source/checkout) the trunk
    * If you have an SVN team provider, check out the trunk from the repository as a new project
    * Otherwise, checkout the trunk using the command line and import the directory as an existing project into your workspace (File -> Import, General -> Existing Projects into Workspace)
  1. If you are not using the [Google Plugin for Eclipse](http://code.google.com/eclipse/), edit the build path by removing the invalid GWT library and adding the `gwt-user.jar` and `gwt-dev-xxx.jar` files
  1. Add the unit test dependency libraries by either
    * following the steps above to create the `libs` folder
    * adding the `jmock.jar`, `hamcrest-core.jar` and `hamcrest-library.jar` libraries to the build path

At this point, the project should compile without any errors. There are also two launch targets included in the trunk:

  * DebugPanelTests - this will run the tests
  * DebugPanelExample - this will run the example in hosted mode

On Mac Os X you will need to add `-XstartOnFirstThread` to the JVM args of the launchers.
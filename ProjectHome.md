# Debug Panel for GWT #

The primary intent of the Debug Panel for the [Google Web Toolkit](http://code.google.com/webtoolkit/) (GWT) is to provide the developer of a GWT application performance data about the application as well as tools to debug the client side application code.

Once a GWT application is deployed, the client side code is run, well, on the client. This means that the developer no longer has control over the execution environment, which makes it difficult to find and diagnose problems in the application. The Debug Panel allows the developer of a GWT (version 1.6+) application to run the application in the client environment and get runtime data that will help diagnose the application.

The Debug Panel is useful in these various aspect and stages of the development of an application:

  * **Development**: The Debug Panel will provide the developer with performance statistics and debug information (such as exception details) while developing the application both in web mode and hosted mode.
  * **Automated Testing**: Automated web tests can use the Debug Panel to gather performance statistics (i.e. benchmark tests) as well as information about internal errors, which may not be detectable in the test facing UI.
  * **Manual Testing**: The Debug Panel will allow a human tester to interact with the environment (e.g. the cookies) to simulate different scenarios.
  * **Production**: the Debug Panel can be safely included in the production code and will allow the developer to diagnose the running application from within the client environment.

### [Getting Started](GettingStarted.md) ###
Get started in using the Debug Panel in your GWT application. Learn how to correctly integrate it in your application and how to configure it to meet your needs.

### [Building the Source](BuildingTheSource.md) ###
Instruction on how to check out the source code and build the project's distributable either from the command line or from within Eclipse.

### [Check Out the Example](http://gwt-debug-panel.googlecode.com/svn/example/app.html) ###
You can check out an example that shows off the Debug Panel's capabilities right out of SVN: [http://gwt-debug-panel.googlecode.com/svn/example/app.html](http://gwt-debug-panel.googlecode.com/svn/example/app.html).
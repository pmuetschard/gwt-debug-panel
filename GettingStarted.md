

# Getting Started #

This document will explain how to add and configure the Debug Panel to your GWT application. It assumes that you have already setup your application (even if it is just a simple "Hello World").


---


## Overview ##

Let's quickly look at what needs to be done in order to integrate the Debug Panel with your application:

  1. You will need to download/install the library containing the Debug Panel code into your project
  1. You will need to create a new [GWT module](http://code.google.com/webtoolkit/doc/1.6/DevGuideOrganizingProjects.html#DevGuideModules) for the Debug Panel
  1. You will need to configure your panel
  1. You will need to change your [GWT host page](http://code.google.com/webtoolkit/doc/1.6/DevGuideOrganizingProjects.html#DevGuideHostPage) to include the new entry point


---


## Installation ##

To install the Debug Panel into your application, simply download the latest version of the gwt-debug-panel-x.x.x.jar file and add it to your application's client side compile classpath. You can get the jar file either from the projects download section, or by checking out and building the project's source code yourself.


---


## Creating a New Module ##

In order to allow you to include the Debug Panel in your production environment, as well as keeping the production code as independent, separated and unaffected by the Debug Panel, you will add and configure the Debug Panel to your application in a separate stand-alone module. This has the added benefit of allowing you to (server side) decide which clients may see the Debug Panel and which don't. Most likely you do not wish to bother your users with all that debug information...

It is assumed here that you are familiar with the [GWT Project Organization](http://code.google.com/webtoolkit/doc/1.6/DevGuideOrganizingProjects.html). Please follow the [GWT Developer Guide](http://code.google.com/webtoolkit/doc/1.6/DevGuide.html) for the steps mentioned below.

  1. Choose a package and module name (e.g. `com.example.panel.MyDebugPanel`)
  1. Create the package
  1. Add the _Module_.gwt.xml file (e.g. `MyDebugPanel.gwt.xml`)
  1. Add the client package (e.g. `com.example.panel.client`)
  1. Add the entry point class (e.g. `com.example.panel.client.MyDebugPanel`) and reference it from the module XML file
  1. Inherit the `com.google.gwt.debugpanel.DebugPanel` module in the module XML file
  1. Add the new module to the compile step of your application's build script

Check out the [example](http://code.google.com/p/gwt-debug-panel/source/browse/#svn/trunk/src/example) in the source repository to see the layout. In the example, the main application code is in the `com.example.app` package, and the Debug Panel portion is in `com.example.panel` package.

Check out the example target in the [build script](http://code.google.com/p/gwt-debug-panel/source/browse/trunk/build.xml) to see how to take care of the last step above. When deploying the example, the compile will create separate JavaSript files for your main application and for the Debug Panel. That way, the main application's JavaSript code can be used independently - with or without - the Debug Panel's JavaScript code.


---


## Configure Your Debug Panel ##

You will configure the Debug Panel to show the components you are interested in, by editing the above created module's entry point. Please see ConfigureYourPanel and the [example](http://code.google.com/p/gwt-debug-panel/source/browse/trunk/src/example/java/com/example/panel/client/MyDebugPanel.java) to see how to do this. The simplest way is to copy the example code and remove the components you do not need. See ExtendingThePanel to see how you can create and add your own custom components to the Debug Panel.


---


## Edit Your Host HTML Page ##

At this point, your host HTML page only includes your main application. In order to add the Debug Panel, the host HTML needs to first include the panel's bootstrap JavaScript code and second include a small JavaScript snippet to tie the two applications together.

### Including the Panel's Bootstrap Code ###

Simply add another `script` tag to the `head` tag of your host HTML file that points the _Module_.nocache.js file of your panel (i.e. `DebugPanel/DebugPanel.nocache.js`). You may wish to compile your application so see what the exact path is.

### The Glue Snippet ###

To allow the main application to communicate with the Debug Panel module - without directly invoking it to keep it independent - you will need to add the following JavaScript snippet to your host HTML file:
```
<script type="text/javascript" language="javascript">
var stats = window.__stats = [];
window.__gwtStatsEvent = function(evt) {
  stats[stats.length] = evt;
  var listener = window.__stats_listener;
  listener && listener(evt);
  return true;
}
</script>
```

This registers an event listener with the GWT stats system, that will store and forward the events to the Debug Panel.


---


## Deploy and Enjoy... ##

You are now done, so deploy your application and enjoy all the new insights you are getting into the runtime characteristics of your application through the Debug Panel.
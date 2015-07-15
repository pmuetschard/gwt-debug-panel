

# Configure, Configure, Configure #

There are three ways to configure and tweak how and what the GWT Debug Panel will display:
  1. Your module entry point - define what components to show, when to show them, etc.
  1. The CSS stylesheet - define/customize the look of the components
  1. The `com.google.gwt.debugpanel.common.Utils.Util` interface

The first is required, the second is highly recommended (without a style sheet the panel doesn't look very good and the filtering is broken) and the third is optional.


---


## The Module Entry Point ##

In the module entry point for the GWT Debug Panel, you will need to initialize the components you wish to show and add the GWT Debug Panel to your DOM to display it. To do so, follow these simple steps:

  1. Check the example to get an idea how to do it
  1. Create and initialize your components
  1. Create and then insert the `DebugPanelWidget` into your DOM

### Review The Example ###

The [example](http://code.google.com/p/gwt-debug-panel/source/browse/trunk/src/example/java/com/example/panel/client/MyDebugPanel.java) shows how to add a GWT Debug Panel that contains all the components implemented in this project. It is easiest to copy over the entry point's code and then adjust it to your needs by arranging the components and removing the ones you do not need.

### Create and Initialize the Components ###

The GWT Debug Panel widget requires a list of implementations of the `DebugPanelWidget.Component` interface. The components implemented in this project are all found in the `com.google.gwt.debugpanel.client` package. See ExtendingThePanel to see how you can write your own custom components.

In the `onModuleLoad()` method, create and initialize all the components you wish to use. It is easiest to check the [example](http://code.google.com/p/gwt-debug-panel/source/browse/trunk/src/example/java/com/example/panel/client/MyDebugPanel.java) to see what initialization is required for each component.

### Insert It Into the DOM ###

Now that all components are initialized create a `DebugPanelWidget` and add insert it into the DOM. It is a good idea to add a `div` to the host HTML page where you wish to insert the GWT Debug Panel and locate it with `RootPanel.get("name")`.


---


## The CSS Stylesheet ##

The best way to design your own styles is to copy the [CSS file from the example](http://code.google.com/p/gwt-debug-panel/source/browse/trunk/src/example/war/debug-panel.css) and adjust the values. All the classes used by the GWT Debug Panel code are found in the example file, which also contains comments documenting what they are for.


---


## The Utils.Util Interface ##

To get the most control over the appearance of the GWT Debug Panel, for things not possible to be changed following the above sections, you can provide the GWT Debug Panel module with your own implementation of the `com.google.gwt.debugpanel.common.Utils.Util` interface. To do so, follow these simple steps:

  1. Check the example to get an idea of how to do it
  1. Write your own implementation
  1. Add the `replace-with` tag to your module XML file

### Review The Example ###

The example uses it's own implementation of the `Utils.Util` interface to change how class names are shown in the GWT Debug Panel. Check out the [implementation](http://code.google.com/p/gwt-debug-panel/source/browse/trunk/src/example/java/com/example/panel/client/MyUtil.java) and the [module XML file](http://code.google.com/p/gwt-debug-panel/source/browse/trunk/src/example/java/com/example/panel/MyDebugPanel.gwt.xml).

### Write Your Implementation ###

You will need to write a class that implements the `Utils.Util` interface. The simplest way is to extend `Utils.DefaultUtil` and override the methods you wish to change. Here is a list of the different methods and a short explanation of what they are for and how `Utils.DefaultUtil` implements them:

  * `String getStylePrefix()` - returns a string that is used to prefix all the style classes. Default is `"DebugPanel"`
  * `double currentTimeMillis()` - returns the current time in millis since the epoch as a double ([in GWT the long data type is emulated and slow](http://code.google.com/p/google-web-toolkit/source/browse/trunk/user/src/com/google/gwt/core/client/Duration.java)). Default is `com.google.gwt.core.client.Duration.currentTimeMillis()`
  * `String formatDate(double time)` - returns the date `time` as a formatted String. The default is to use the `HH:mm:ss.SSS` format.
  * `String formatClassName(String className)` - allows you to change the class names displayed (e.g. shortening them by stripping of common prefixes). Default is to leave the names unchanged
  * `ButtonBase createTextButton(String text, ClickHandler handler)` - create a button with the given text as label and the given handler to handle clicks. Default is a standard GWT button
  * `ButtonBase createMenuButton(String text, ClickHandler handler)` - create a button with the given text as label and the given handler to handler clicks. This button is used to show the drop-down menu for the filters. Default is a standard GWT button


### Update the Module XML File ###

The GWT Debug Panel uses [deferred binding](http://code.google.com/webtoolkit/doc/1.6/DevGuideCodingBasics.html#DevGuideDeferredBinding) to get an instance of the `Utils.Util` interface. In order to tell the GWT compiler to use your own implementation you will need to add a rule(s) to your module XML file. Here is an example of a simple replace rule that is used by the example:
```
 <replace-with class="com.example.panel.client.MyUtil">
    <when-type-assignable class="com.google.gwt.debugpanel.common.Utils.Util"/>
  </replace-with>
```
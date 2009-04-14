<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<!-- 
Copyright 2009 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->
<%
// Decide whether to show the debug panel. You may want to check the client
// IP, cookies, etc. Here, we show it by default, but turn it off if the
// debug URL parameter is set to false.
boolean showDebugPanel = !"false".equalsIgnoreCase(request.getParameter("debug"));
%>
<html>
<head>
  <title>Debug Panel Example</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <style type="text/css">
    body {
      font-family: arial, sans-serif;
      font-size: 81.25%;
    }
  </style>
  <% if (showDebugPanel) { %>
    <link type="text/css" rel="stylesheet" href="debug-panel.css" />
    <!-- this snippet should be automatically included in all the HTML that wishes to use the debug panel. -->
    <script type="text/javascript" language="javascript">
    var stats = window.__stats = [];
    window.__gwtStatsEvent = function(evt) {
      stats[stats.length] = evt;
      var listener = window.__stats_listener;
      listener && listener(evt);
      return true;
    }
    </script>
    <script language="javascript" type="text/javascript" src="DebugPanel/DebugPanel.nocache.js"></script>
<% } %>

  <script language="javascript" type="text/javascript" src="MainApp/MainApp.nocache.js"></script>
</head>
<body>

<div id="contents"></div>

<% if (showDebugPanel) { %>
  <div id="debug-panel"></div>
  <a href="?debug=false">Without Panel</a>
<% } else {%>
  <a href="?debug=true">With Panel</a>
<% } %>

</body>
</html>

/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.example.panel.client;

import com.google.gwt.debugpanel.common.Utils;

/**
 * Shows how to override some of the debug panel utilities.
 */
public class MyUtil extends Utils.DefaultUtil {
  private static final String PREFIX = "com.example.";

  @Override
  public String formatClassName(String className) {
    if (className.startsWith(PREFIX)) {
      className = className.substring(PREFIX.length());
    }
    return super.formatClassName(className);
  }
}

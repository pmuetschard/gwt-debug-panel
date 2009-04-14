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
package com.example.app.client;

import com.google.gwt.debugpanel.common.ExceptionSerializer;
import com.google.gwt.debugpanel.common.StatisticsEvent;
import com.google.gwt.debugpanel.common.StatisticsEventDispatcher;
import com.google.gwt.debugpanel.common.Utils;

/**
 * Handles exception by sending an event them to the debug panel, which will
 * show the exception details in the exception log widget.
 */
public class ExceptionHandler {
  private StatisticsEventDispatcher dispatcher;
  private ExceptionSerializer serializer;

  public ExceptionHandler(StatisticsEventDispatcher dispatcher, ExceptionSerializer serializer) {
    this.dispatcher = dispatcher;
    this.serializer = serializer;
  }

  public void handle(Throwable t) {
    if (dispatcher.enabled()) {
      dispatch(t);
    }
  }

  private void dispatch(Throwable t) {
    double now = Utils.currentTimeMillis();
    StatisticsEvent event = dispatcher.newEvent("error", "error", now, "error");
    dispatcher.setExtraParameter(event, "error", serializer.serialize(t));
    dispatcher.dispatch(event);
  }
}

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
package com.google.gwt.debugpanel.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

/**
 *  Tests the {@link GwtStatisticsEventDispatcher}.
 */
public class GwtStatisticsEventDispatcherTest extends AbstractDebugPanelGwtTestCase {
  private GwtStatisticsEventDispatcher dispatcher;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    dispatcher = new GwtStatisticsEventDispatcher();
  }

  public void testNotEnabledWhenNoStatsFunctionSet() {
    assertFalse(dispatcher.enabled());
  }

  public void testCreateEvent() {
    StatisticsEvent event = dispatcher.newEvent("the_system", "the_group", 123, "the_type");
    dispatcher.dispatch(event);
    assertNotNull(event);
    assertEquals(GWT.getModuleName(), event.getModuleName());
    assertEquals("the_system", event.getSubSystem());
    assertEquals("the_group", event.getEventGroupKey());
    assertEquals(123.0, event.getMillis());
    assertEquals("the_type", event.getExtraParameter("type"));
  }

  public void testSetExtraParameter() {
    StatisticsEvent event = dispatcher.newEvent("the_system", "the_group", 123, "the_type");
    dispatcher.setExtraParameter(event, "param1", "value1");
    dispatcher.setExtraParameter(event, "param2", AValue.create("value2"));
    dispatcher.dispatch(event);
    assertEquals("value1", event.getExtraParameter("param1"));
    assertEquals("value2", ((AValue) event.getExtraParameter("param2")).get());
  }

  private static class AValue extends JavaScriptObject {
    protected AValue() {
    }

    public final native String get() /*-{
      return this.value;
    }-*/;

    public static final native AValue create(String value) /*-{
      return {
        value: value
      };
    }-*/;
  }
}

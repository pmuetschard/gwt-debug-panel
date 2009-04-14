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

import java.util.Arrays;
import java.util.Iterator;

/**
 * Tests the Java overlay object {@link GwtStatisticsEvent}.
 */
public class GwtStatisticsEventTest extends AbstractDebugPanelGwtTestCase {
  public void testAccessorsWithoutNull() {
    GwtStatisticsEvent event = create("module", "system", "group", 12345, "type", "class");
    assertEquals("module", event.getModuleName());
    assertEquals("system", event.getSubSystem());
    assertEquals("group", event.getEventGroupKey());
    assertEquals(12345.0, event.getMillis());
    assertEquals("type", event.getExtraParameter("type"));
    assertEquals("class", event.getExtraParameter("className"));
  }

  public void testAccessorsWithNull() {
    GwtStatisticsEvent event = createNull();
    assertNull(event.getModuleName());
    assertNull(event.getSubSystem());
    assertNull(event.getEventGroupKey());
    assertEquals(0.0, event.getMillis());
    assertNull(event.getExtraParameter("type"));
    assertNull(event.getExtraParameter("className"));
  }

  public void testLongMillis() {
    GwtStatisticsEvent event = createLongMillis();
    assertTrue(event.getMillis() > Integer.MAX_VALUE);
  }

  public void testAsEventWithEmptyValues() {
    StatisticsEvent evt = createNull().asEvent();
    assertNull(evt.getModuleName());
    assertNull(evt.getSubSystem());
    assertNull(evt.getEventGroupKey());
    assertEquals(0.0, evt.getMillis());
    assertFalse(evt.getExtraParameterNames().hasNext());
  }

  public void testAsEventWithNonEmptyValues() {
    StatisticsEvent evt = create("module", "system", "group", 123, "type", "class").asEvent();
    assertEquals("module", evt.getModuleName());
    assertEquals("system", evt.getSubSystem());
    assertEquals("group", evt.getEventGroupKey());
    assertEquals(123.0, evt.getMillis());
    Iterator<String> names = evt.getExtraParameterNames();
    assertTrue(names.hasNext());
    String first = names.next();
    assertTrue(names.hasNext());
    String second = names.next();
    assertFalse(names.hasNext());
    assertTrue(("type".equals(first) && "className".equals(second))
        || ("type".equals(second) && "className".equals(first)));
    assertEquals("type", evt.getExtraParameter("type"));
    assertEquals("class", evt.getExtraParameter("className"));
  }

  public void testParameterNamesIteratorIsReadOnly() {
    StatisticsEvent evt = create("module", "system", "group", 123, "type", "class").asEvent();
    Iterator<String> names = evt.getExtraParameterNames();
    assertTrue(names.hasNext());
    names.next();
    try {
      names.remove();
      fail("expected exception");
    } catch (RuntimeException ex) {
      // Expected.
    }
  }

  public void testFromEvent() {
    GwtStatisticsEvent evt = GwtStatisticsEvent.fromEvent(new StatisticsEvent() {
      @Override
      public String getModuleName() {
        return "module";
      }

      @Override
      public String getSubSystem() {
        return "system";
      }

      @Override
      public String getEventGroupKey() {
        return "group";
      }

      @Override
      public double getMillis() {
        return 123;
      }

      @Override
      public Iterator<String> getExtraParameterNames() {
        return Arrays.asList("type", "className").iterator();
      }

      @Override
      public Object getExtraParameter(String name) {
        return name;
      }
    });

    assertEquals("module", evt.getModuleName());
    assertEquals("system", evt.getSubSystem());
    assertEquals("group", evt.getEventGroupKey());
    assertEquals(123.0, evt.getMillis());
    Iterator<String> names = evt.asEvent().getExtraParameterNames();
    assertTrue(names.hasNext());
    String first = names.next();
    assertTrue(names.hasNext());
    String second = names.next();
    assertFalse(names.hasNext());
    assertTrue(("type".equals(first) && "className".equals(second))
        || ("type".equals(second) && "className".equals(first)));
    assertEquals("type", evt.getExtraParameter("type"));
    assertEquals("className", evt.getExtraParameter("className"));
  }

  public void testAutoBoxing() {
    GwtStatisticsEvent event = createForAutoBoxing();
    assertEquals(new Double(123.45), event.getExtraParameter("a"));
    assertEquals(Boolean.TRUE, event.getExtraParameter("b"));
    assertEquals("string", event.getExtraParameter("c"));
    assertNull(event.getExtraParameter("d"));
    assertNull(event.getExtraParameter("e"));
  }

  private native GwtStatisticsEvent create(String module, String system, String group, int millis,
      String type, String className) /*-{
    return {
      moduleName: module,
      subSystem: system,
      evtGroup: group,
      millis: millis,
      type: type,
      className: className
    }
  }-*/;

  private native GwtStatisticsEvent createNull() /*-{
    return {
      moduleName: undefined,
      subSystem: undefined
    };
  }-*/;

  private native GwtStatisticsEvent createLongMillis() /*-{
    return {
      millis: 0xFFFFFFFF + 1
    };
  }-*/;

  private native GwtStatisticsEvent createForAutoBoxing() /*-{
    return {
      a: 123.45,
      b: true,
      c: "string",
      d: null,
      e: undefined
    }
  }-*/;
}

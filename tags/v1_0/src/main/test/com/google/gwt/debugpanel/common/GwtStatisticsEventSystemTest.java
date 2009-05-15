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

import java.util.Iterator;

/**
 * Tests the {@link GwtStatisticsEventSystem}.
 */
public class GwtStatisticsEventSystemTest extends AbstractDebugPanelGwtTestCase {
  private GwtStatisticsEventSystem system;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    setup();
    system = new GwtStatisticsEventSystem();
    system.enable(true);
  }

  // This is necessary as the tests are not run in isolation.
  private native void setup() /*-{
    $wnd.__stats = new Array();
    $wnd.__stats_listener = null;
  }-*/;

  public void testRegisterAndDispatch() {
    final int[] invokeCount = new int[1];
    system.addListener(new StatisticsEventListener() {
      //@Override
      public void onStatisticsEvent(StatisticsEvent event) {
        assertEquals("event1", event.getModuleName());
        invokeCount[0]++;
      }
    }, false);

    dispatch(event("event1"));
    assertEquals(1, invokeCount[0]);
  }

  public void testReplay() {
    dispatch(event("event1"));
    dispatch(event("event2"));

    final int[] invokeCount = new int[1];
    system.addListener(new StatisticsEventListener() {
      //@Override
      public void onStatisticsEvent(StatisticsEvent event) {
        if (invokeCount[0] == 0) {
          assertEquals("event1", event.getModuleName());
        } else if (invokeCount[0] == 1) {
          assertEquals("event2", event.getModuleName());
        }
        invokeCount[0]++;
      }
    }, true);
    assertEquals(2, invokeCount[0]);
  }

  public void testNoReplay() {
    dispatch(event("event1"));
    dispatch(event("event2"));
    system.addListener(new StatisticsEventListener() {
      //@Override
      public void onStatisticsEvent(StatisticsEvent event) {
        fail("No invocation expected when replay is false.");
      }
    }, false);
  }

  public void testClearHistory() {
    dispatch(event("event1"));
    assertTrue(system.pastEvents().hasNext());
    system.clearEventHistory();
    assertFalse(system.pastEvents().hasNext());
  }

  public void testEventIterator() {
    Object event1 = event("event1"), event2 = event("event2");
    dispatch(event1);
    dispatch(event2);
    Iterator<StatisticsEvent> iterator = system.pastEvents();
    assertTrue(iterator.hasNext());
    assertEquals("event1", iterator.next().getModuleName());
    assertTrue(iterator.hasNext());
    assertEquals("event2", iterator.next().getModuleName());
    assertFalse(iterator.hasNext());
  }

  public void testHooksIntoGwtStatsSystem() {
    final int[] invokeCount = new int[1];
    system.addListener(new StatisticsEventListener() {
      //@Override
      public void onStatisticsEvent(StatisticsEvent event) {
        invokeCount[0]++;
      }
    }, false);
    assertEquals(0, invokeCount[0]);

    // Simulate an event sent by the GWT system via JavaScript.
    dispatch(null);
    assertEquals(1, invokeCount[0]);
  }

  public void testEventsAreIgnoredWhenDisabled() {
    system = new GwtStatisticsEventSystem();
    system.addListener(new StatisticsEventListener() {
      //@Override
      public void onStatisticsEvent(StatisticsEvent event) {
        fail("system is disabled, not expecting any events");
      }
    }, false);

    dispatch(null);
  }

  public void testIteratorIsReadOnly() {
    dispatch(event("event"));
    Iterator<StatisticsEvent> iterator = system.pastEvents();
    assertTrue(iterator.hasNext());
    iterator.next();
    try {
      iterator.remove();
      fail("Iterator should not all remove");
    } catch (RuntimeException e) {
      // Expected.
    }
  }

  public void testNoEventsAreReceivedAfterRemovingListener() {
    StatisticsEventListener listener = new StatisticsEventListener() {
      //@Override
      public void onStatisticsEvent(StatisticsEvent event) {
        fail("did not expect any events");
      }
    };
    system.addListener(listener, false);
    system.removeListener(listener);
    dispatch(event("event"));
  }

  private native void dispatch(Object evt) /*-{
    var stats = $wnd.__stats;
    if (stats) {
      stats[stats.length] = evt;
    }
    var listener = $wnd.__stats_listener;
    if (listener) {
      listener(evt);
    }
  }-*/;

  private native Object event(String module) /*-{
    return {
      moduleName: module
    };
  }-*/;
}

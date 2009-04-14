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
package com.google.gwt.debugpanel.models;

import junit.framework.TestCase;

/**
 * Tests the {@link GwtDebugStatisticsValue}.
 */
public class GwtDebugStatisticsValueTest extends TestCase {
  public void testRpcMethodTranslation() {
    GwtDebugStatisticsValue value = new GwtDebugStatisticsValue("label", "module", 0, 1);
    value.setRpcMethod("SomeService_Proxy.method");
    assertTrue(value.hasRpcMethod());
    assertEquals("SomeService.method", value.getRpcMethod());
  }

  public void testWithChildTime() {
    GwtDebugStatisticsValue value = new GwtDebugStatisticsValue("label", "module", 2, 4);
    assertTiming(value.withChildTime(1), 1, 4);
    assertTiming(value.withChildTime(2), 2, 4);
    assertTiming(value.withChildTime(3), 2, 4);
    assertTiming(value.withChildTime(4), 2, 4);
    assertTiming(value.withChildTime(5), 2, 5);
  }

  public void testWithEndTime() {
    GwtDebugStatisticsValue value = new GwtDebugStatisticsValue("label", "module", 2, 4);
    assertTiming(value.withEndTime(1), 2, 4);
    assertTiming(value.withEndTime(2), 2, 4);
    assertTiming(value.withEndTime(3), 2, 4);
    assertTiming(value.withEndTime(4), 2, 4);
    assertTiming(value.withEndTime(5), 2, 5);
  }

  private void assertTiming(GwtDebugStatisticsValue value, double start, double end) {
    assertEquals(start, value.getStartTime());
    assertEquals(end, value.getEndTime());
  }

  public void testResponse() {
    GwtDebugStatisticsValue value = new GwtDebugStatisticsValue("label", "module", 2, 4);
    assertFalse(value.hasResponse());
    value.setResponse("response");
    assertTrue(value.hasResponse());
    assertEquals("response", value.getResponse());
  }
}

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

/**
 * Tests the {@link ExceptionData}.
 */
public class ExceptionDataTest extends AbstractDebugPanelGwtTestCase {
  public void testConstructionAndGetters() {
    ExceptionData data = ExceptionData.create("type", "message", "trace", 
        ExceptionData.create("causeType", "causeMessage", "causeTrace", null));
    assertNotNull(data);
    assertEquals("type", data.getType());
    assertEquals("message", data.getMessage());
    assertEquals("trace", data.getTrace());
    data = data.getCause();
    assertNotNull(data);
    assertEquals("causeType", data.getType());
    assertEquals("causeMessage", data.getMessage());
    assertEquals("causeTrace", data.getTrace());
    data = data.getCause();
    assertNull(data);
  }

  public void testToStringWithUnknownValues() {
    assertEquals("<unknown type>", ExceptionData.create(null, null, null, null).toString());
  }

  public void testToStringWithOnlyTypeAndMessage() {
    assertEquals("type: message", ExceptionData.create("type", "message", null, null).toString());
  }

  public void testToStringWithTrace() {
    assertEquals("type: message\ntrace", 
        ExceptionData.create("type", "message", "trace", null).toString());
  }

  public void testToStringWithCause() {
    assertEquals("type: message\ntrace\nCaused by: causeType: causeMessage\ncauseTrace", 
        ExceptionData.create("type", "message", "trace", 
            ExceptionData.create("causeType", "causeMessage", "causeTrace", null)).toString());
  }
}

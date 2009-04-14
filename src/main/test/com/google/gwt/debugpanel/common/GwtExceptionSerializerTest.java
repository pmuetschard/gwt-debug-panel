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
 * Tests the {@link GwtExceptionSerializer}.
 */
public class GwtExceptionSerializerTest extends AbstractDebugPanelGwtTestCase {
  private GwtExceptionSerializer serializer;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    serializer = new GwtExceptionSerializer();
  }


  public void testBasicValues() {
    Throwable error = new Throwable("a message");
    ExceptionData data = serializer.serialize(error);
    assertEquals(error.getClass().getName(), data.getType());
    assertEquals(error.getMessage(), data.getMessage());
    assertNotNull(data.getTrace());
    assertNull(data.getCause());
  }

  public void testWithNullStackTrace() {
    ExceptionData data = serializer.serialize(new Throwable("message") {
      @Override
      public StackTraceElement[] getStackTrace() {
        return null;
      }
    });
    assertNull(data.getTrace());
  }

  public void testWithEmptyStackTrace() {
    ExceptionData data = serializer.serialize(new Throwable("message") {
      @Override
      public StackTraceElement[] getStackTrace() {
        return new StackTraceElement[0];
      }
    });
    assertNull(data.getTrace());
  }

  public void testWithErrorCausingStackTrace() {
    ExceptionData data = serializer.serialize(new Throwable("message") {
      @Override
      public StackTraceElement[] getStackTrace() {
        throw new RuntimeException("fail");
      }
    });
    assertNull(data.getTrace());
  }

  public void testWithSingleCause() {
    ExceptionData data = serializer.serialize(new Throwable("message", new Throwable("cause")));
    assertEquals("message", data.getMessage());
    assertEquals("cause", data.getCause().getMessage());
  }

  public void testWithTwoCauses() {
    ExceptionData data = serializer.serialize(
        new Throwable("message", new Throwable("cause0", new Throwable("cause1"))));
    assertEquals("message", data.getMessage());
    assertEquals("cause0", data.getCause().getMessage());
    assertEquals("cause1", data.getCause().getCause().getMessage());
  }
}

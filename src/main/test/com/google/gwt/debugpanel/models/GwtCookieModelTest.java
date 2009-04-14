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

import com.google.gwt.debugpanel.common.AbstractDebugPanelGwtTestCase;
import com.google.gwt.user.client.Cookies;

/**
 * Tests the {@link GwtCookieModel}.
 */
public class GwtCookieModelTest extends AbstractDebugPanelGwtTestCase {
  private GwtCookieModel model;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    model = new GwtCookieModel(false);
    for (String cookie : model.cookieNames()) {
      model.removeCookie(cookie);
    }
  }

  public void testCookieNamesAreNotNull() {
    assertNotNull(model.cookieNames());
  }

  public void testAddingOfCookie() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    String[] cookies = model.cookieNames();
    assertEquals(1, cookies.length);
    assertEquals("a_cookie", cookies[0]);
    assertEquals("a_value", model.getCookie(cookies[0]));
  }

  public void testUpdatingOfCookie() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    String[] cookies = model.cookieNames();
    assertEquals(1, cookies.length);
    model.setCookie("a_cookie", "second_value", null, null, null, false);
    cookies = model.cookieNames();
    assertEquals(1, cookies.length);
    assertEquals("second_value", model.getCookie(cookies[0]));
  }

  public void testRemovingOfCookie() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    String[] cookies = model.cookieNames();
    assertEquals(1, cookies.length);
    model.removeCookie("a_cookie");
    cookies = model.cookieNames();
    assertEquals(0, cookies.length);
  }

  public void testSettingToNullRemovesCookie() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    String[] cookies = model.cookieNames();
    assertEquals(1, cookies.length);
    model.setCookie("a_cookie", null, null, null, null, false);
    cookies = model.cookieNames();
    assertEquals(0, cookies.length);
  }

  public void testAddingOfCookieFiresEvent() {
    final String[] values = new String[2];
    model.addCookieListener(new Listener() {
      @Override
      public void cookieAdded(String name, String value) {
        values[0] = name;
        values[1] = value;
      }
    });
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    assertEquals("a_cookie", values[0]);
    assertEquals("a_value", values[1]);
  }

  public void testUpdatingOfCookieFiresEvent() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    final String[] values = new String[2];
    model.addCookieListener(new Listener() {
      @Override
      public void cookieChanged(String name, String value) {
        values[0] = name;
        values[1] = value;
      }
    });
    model.setCookie("a_cookie", "second_value", null, null, null, false);
    assertEquals("a_cookie", values[0]);
    assertEquals("second_value", values[1]);
  }

  public void testRemovingOfCookieFiresEvent() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    final String[] values = new String[1];
    model.addCookieListener(new Listener() {
      @Override
      public void cookieRemoved(String name) {
        values[0] = name;
      }
    });
    model.removeCookie("a_cookie");
    assertEquals("a_cookie", values[0]);
  }

  public void testRemovingOfEventHandler() {
    CookieModelListener listener = new Listener();
    model.addCookieListener(listener);
    model.removeCookieListener(listener);
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    model.setCookie("a_cookie", "second_value", null, null, null, false);
    model.removeCookie("a_cookie");
  }

  public void testRefresh() {
    model = new GwtCookieModel(true);
    model.setCookie("cookie1", "value1", null, null, null, false);
    model.setCookie("cookie2", "value2", null, null, null, false);

    final String[] addValues = new String[2];
    final String[] changeValues = new String[2];
    final String[] removeValues = new String[1];
    model.addCookieListener(new CookieModelListener() {
      @Override
      public void cookieAdded(String name, String value) {
        assertNull(addValues[0]);
        assertNull(addValues[1]);
        addValues[0] = name;
        addValues[1] = value;
      }

      @Override
      public void cookieChanged(String name, String value) {
        assertNull(changeValues[0]);
        assertNull(changeValues[1]);
        changeValues[0] = name;
        changeValues[1] = value;
      }

      @Override
      public void cookieRemoved(String name) {
        assertNull(removeValues[0]);
        removeValues[0] = name;
      }
    });

    // Change cookies underneath the model and then perform refresh.
    Cookies.setCookie("cookie1", "new_value");
    Cookies.removeCookie("cookie2");
    Cookies.setCookie("cookie3", "new_cookie");
    model.refresh();

    assertEquals("cookie3", addValues[0]);
    assertEquals("new_cookie", addValues[1]);
    assertEquals("cookie1", changeValues[0]);
    assertEquals("new_value", changeValues[1]);
    assertEquals("cookie2", removeValues[0]);
  }

  private static class Listener implements CookieModelListener {
    @Override
    public void cookieAdded(String name, String value) {
      fail("Unexpected call to cookieAdded: " + name + ", " + value);
    }

    @Override
    public void cookieChanged(String name, String value) {
      fail("Unexpected call to cookieChanged: " + name + ", " + value);
    }

    @Override
    public void cookieRemoved(String name) {
      fail("Unexpected call to cookieRemoved: " + name);
    }
  }
}

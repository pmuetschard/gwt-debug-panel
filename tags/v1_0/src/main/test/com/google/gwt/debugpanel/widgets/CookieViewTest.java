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
package com.google.gwt.debugpanel.widgets;

import com.google.gwt.debugpanel.common.AbstractDebugPanelGwtTestCase;
import com.google.gwt.debugpanel.models.CookieModel;
import com.google.gwt.debugpanel.models.GwtCookieModel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Tests the {@link CookieView}.
 */
public class CookieViewTest extends AbstractDebugPanelGwtTestCase {
  private GwtCookieModel model;
  private TestingGwtCookieView view;
  private FlexTable table;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    model = new GwtCookieModel(false);
    for (String cookie : model.cookieNames()) {
      model.removeCookie(cookie);
    }
    view = new TestingGwtCookieView(model);
    table = (FlexTable) view.getWidget();
  }

  public void testConstructionWithEmptyModel() {
    assertEquals(2, table.getRowCount());
    assertEquals(3, table.getCellCount(0));
    assertEquals(1, table.getCellCount(1));
    assertEquals("Cookie", table.getText(0, 0));
    assertEquals("Value", table.getText(0, 1));
    Widget widget = table.getWidget(1, 0);
    assertTrue(widget instanceof CommandLink);
    assertEquals("Add a Cookie", ((CommandLink) widget).getText());
  }

  public void testConstructionWithNonEmptyModel() {
    model.setCookie("one_cookie", "one_value", null, null, null, false);
    model.setCookie("two_cookie", "two_value", null, null, null, false);
    view = new TestingGwtCookieView(model);
    table = (FlexTable) view.getWidget();

    assertEquals(2 + 2, table.getRowCount());
    assertRow(1, "one_cookie", "one_value");
    assertRow(2, "two_cookie", "two_value");
  }

  public void testConstructionWithNonEmptyModelOutOfOrderValues() {
    model.setCookie("two_cookie", "two_value", null, null, null, false);
    model.setCookie("one_cookie", "one_value", null, null, null, false);
    view = new TestingGwtCookieView(model);
    table = (FlexTable) view.getWidget();

    assertEquals(2 + 2, table.getRowCount());
    assertRow(1, "one_cookie", "one_value");
    assertRow(2, "two_cookie", "two_value");
  }

  public void testAddingOfSingleNewCookie() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    assertEquals(2 + 1, table.getRowCount());
    assertRow(1, "a_cookie", "a_value");
  }

  public void testAddingOfMultipleNewCookies() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    model.setCookie("b_cookie", "b_value", null, null, null, false);
    assertEquals(2 + 2, table.getRowCount());
    assertRow(1, "a_cookie", "a_value");
    assertRow(2, "b_cookie", "b_value");
  }

  public void testAddingOfMultipleOutOfOrderNewCookies() {
    model.setCookie("b_cookie", "b_value", null, null, null, false);
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    assertEquals(2 + 2, table.getRowCount());
    assertRow(1, "a_cookie", "a_value");
    assertRow(2, "b_cookie", "b_value");
  }

  public void testAddedCanDealWithUpdates() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    assertEquals(2 + 1, table.getRowCount());
    view.added("a_cookie", "second_value");
    assertEquals(2 + 1, table.getRowCount());
    assertRow(1, "a_cookie", "second_value");
  }

  public void testUpdatingOfCookie() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    assertEquals(2 + 1, table.getRowCount());
    model.setCookie("a_cookie", "second_value", null, null, null, false);
    assertEquals(2 + 1, table.getRowCount());
    assertRow(1, "a_cookie", "second_value");
  }

  public void testUpdatedCanDealWithAdds() {
    view.updated("a_cookie", "a_value");
    assertEquals(2 + 1, table.getRowCount());
    assertRow(1, "a_cookie", "a_value");
  }

  public void testRemovingOfCookie() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    assertEquals(2 + 1, table.getRowCount());
    model.removeCookie("a_cookie");
    assertEquals(2 + 0, table.getRowCount());
  }

  public void testRemovingOfCookieFromMultipleCookies() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    model.setCookie("b_cookie", "b_value", null, null, null, false);
    assertEquals(2 + 2, table.getRowCount());
    model.removeCookie("a_cookie");
    assertEquals(2 + 1, table.getRowCount());
    assertRow(1, "b_cookie", "b_value");
  }

  public void testRemovedCanDealWithNonExistingCookie() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    assertEquals(2 + 1, table.getRowCount());
    view.removed("invalid");
    assertEquals(2 + 1, table.getRowCount());
    assertRow(1, "a_cookie", "a_value");
  }

  public void testRemoveLinkRemovesCookie() {
    model.setCookie("a_cookie", "a_value", null, null, null, false);
    assertEquals(2 + 1, table.getRowCount());
    ((CommandLink) table.getWidget(1, 2)).execute();
    assertEquals(2 + 0, table.getRowCount());
  }

  public void testAddLinkTransformsRowIntoForm() {
    int row = table.getRowCount() - 1;
    CommandLink link = (CommandLink) table.getWidget(row, 0);
    link.execute();
    assertEquals(row + 1, table.getRowCount());
    assertEquals(3, table.getCellCount(row));
    assertTrue(table.getWidget(row, 0) instanceof TextBox);
    assertTrue(table.getWidget(row, 1) instanceof TextBox);
    assertTrue(table.getWidget(row, 2) instanceof ButtonBase);

    ((ButtonBase) table.getWidget(row, 2)).fireEvent(new ClickEvent() {});
  }

  private void assertRow(int row, String name, String value) {
    assertEquals(3, table.getCellCount(row));
    assertEquals(name, table.getText(row, 0));
    assertEquals(value, table.getText(row, 1));
    Widget widget = table.getWidget(row, 2);
    assertTrue(widget instanceof CommandLink);
    assertEquals("Remove", ((CommandLink) widget).getText());
  }

  /**
   * To get access to the underlying flex table.
   */
  public static class TestingGwtCookieView extends CookieView {
    public TestingGwtCookieView(CookieModel model) {
      super(model);
    }

    @Override
    public Widget getWidget() {
      return super.getWidget();
    }
  }
}

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

import com.google.gwt.core.client.Duration;
import com.google.gwt.debugpanel.common.AbstractDebugPanelGwtTestCase;
import com.google.gwt.debugpanel.common.ExceptionData;
import com.google.gwt.debugpanel.common.Utils;
import com.google.gwt.debugpanel.models.ExceptionModel;
import com.google.gwt.debugpanel.models.GwtExceptionModel;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

/**
 * Tests the {@link ExceptionView}.
 */
public class ExceptionViewTest extends AbstractDebugPanelGwtTestCase {
  private DateTimeFormat format;
  private TestingGwtExceptionView view;
  private GwtExceptionModel model;
  private FlexTable table;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    Utils.setInstance(new Utils.DefaultUtil());
    format = DateTimeFormat.getFormat("HH:mm:ss.SSS");
    model = new GwtExceptionModel();
    view = new TestingGwtExceptionView(model);
    table = (FlexTable) view.getWidget();
  }

  @Override
  protected void gwtTearDown() throws Exception {
    super.gwtTearDown();
    Utils.setInstance(null);
  }

  public void testWithEmptyModel() {
    assertHeader();
    assertEquals(1, table.getRowCount());
  }

  public void testConstruction() {
    double start = Duration.currentTimeMillis();
    ExceptionData ex = data(new Throwable());
    model.add(event(start + 0, ex));
    model.add(event(start + 1, ex));
    model.add(event(start + 2, ex));
    view = new TestingGwtExceptionView(model);
    table = (FlexTable) view.getWidget();

    assertHeader();
    assertRow(0, start + 2, ex);
    assertRow(1, start + 1, ex);
    assertRow(2, start + 0, ex);
  }

  public void testData() {
    double start = Duration.currentTimeMillis();
    ExceptionData ex0 = data(new Throwable());
    ExceptionData ex1 = data(new Throwable("message"));
    ExceptionData ex2 = data(new Throwable("message", new Throwable()));
    model.add(event(start + 0, ex0));
    model.add(event(start + 1, ex1));
    model.add(event(start + 2, ex2));
    view = new TestingGwtExceptionView(model);
    table = (FlexTable) view.getWidget();

    assertHeader();
    assertRow(0, start + 2, ex2);
    assertRow(1, start + 1, ex1);
    assertRow(2, start + 0, ex0);
  }

  public void testInsertion() {
    double start = Duration.currentTimeMillis();
    ExceptionData ex = data(new Throwable());
    model.add(event(start + 0, ex));
    model.add(event(start + 1, ex));
    model.add(event(start + 2, ex));

    assertHeader();
    assertRow(0, start + 2, ex);
    assertRow(1, start + 1, ex);
    assertRow(2, start + 0, ex);
  }

  public void testRemoval() {
    double start = Duration.currentTimeMillis();
    ExceptionData ex = data(new Throwable());
    model.add(event(start + 0, ex));
    model.add(event(start + 1, ex));
    model.add(event(start + 2, ex));
    model.removeExceptionEvent(1);

    assertHeader();
    assertRow(0, start + 2, ex);
    assertRow(1, start + 0, ex);
  }

  private void assertHeader() {
    assertTrue(table.getRowCount() >= 1);
    assertEquals("Module", table.getText(0, 0));
    assertEquals("Time", table.getText(0, 1));
    assertEquals("Exception", table.getText(0, 2));
    assertEquals(Utils.style() + "-errorsHeader", table.getRowFormatter().getStyleName(0));
  }

  private void assertRow(int row, double time, ExceptionData ex) {
    assertTrue(table.getRowCount() > row++);
    assertEquals("module", table.getText(row, 0));
    assertEquals(format.format(new Date((long) time)), table.getText(row, 1));
    Widget w = table.getWidget(row, 2);
    assertEquals(String.valueOf(ex), w.getElement().getInnerText());
    assertEquals(Utils.style() + "-codePre", w.getStyleName());
  }

  private static ExceptionModel.ExceptionEvent event(double time, ExceptionData ex) {
    return new ExceptionModel.ExceptionEvent("module", time, ex);
  }

  private static ExceptionData data(Throwable t) {
    return (t == null) ? null : ExceptionData.create(
        t.getClass().getName(), t.getMessage(), "--trace--", data(t.getCause()));
  }

  /**
   * To get access to the underlying flex table.
   */
  private static class TestingGwtExceptionView extends ExceptionView {
    public TestingGwtExceptionView(ExceptionModel model) {
      super(model);
    }

    @Override
    public Widget getWidget() {
      return super.getWidget();
    }
  }
}

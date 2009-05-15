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
import com.google.gwt.debugpanel.common.StatisticsEvent;
import com.google.gwt.debugpanel.common.Utils;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Tests the {@link DebugPanelRawLogWidget}.
 */
public class DebugPanelRawLogWidgetTest extends AbstractDebugPanelGwtTestCase {
  private DebugPanelRawLogWidget widget;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    Utils.setInstance(new Utils.DefaultUtil());
    widget = new DebugPanelRawLogWidget();
  }

  @Override
  protected void gwtTearDown() throws Exception {
    super.gwtTearDown();
    Utils.setInstance(null);
  }

  public void testHeaderRowStyleIsSet() {
    assertEquals(Utils.style() + "-logHeader", widget.getRowFormatter().getStyleName(0));
  }

  public void testNewEventAddsNewRow() {
    int count = widget.getRowCount();
    widget.onStatisticsEvent(new DummyEvent("module"));
    assertEquals(count + 1, widget.getRowCount());
  }

  public void testExtraParametersWithEmptyNames() {
    widget.onStatisticsEvent(new DummyEvent("module", new String[2][0]));
    assertEquals("{}", getExtraParametersCell());
  }

  public void testExtraParametersWithSingleName() {
    widget.onStatisticsEvent(new DummyEvent("module", new String[][] {{ "name" }, { "value" }}));
    assertEquals("{name = value}", getExtraParametersCell());
  }

  public void testExtraParametersWithMultipleNames() {
    widget.onStatisticsEvent(new DummyEvent("module", 
        new String[][] {{ "name1", "name2" }, { "value1", "value2" }}));
    assertEquals("{name1 = value1, name2 = value2}", getExtraParametersCell());
  }

  private String getExtraParametersCell() {
    int row = widget.getRowCount() - 1;
    return widget.getText(row, widget.getCellCount(row) - 1);
  }

  private static class DummyEvent implements StatisticsEvent {
    private String moduleName;
    private String[][] extraParameters;

    public DummyEvent(String moduleName) {
      this(moduleName, new String[2][0]);
    }

    public DummyEvent(String moduleName, String[][] extraParameters) {
      this.moduleName = moduleName;
      this.extraParameters = extraParameters;
    }

    //@Override
    public String getModuleName() {
      return moduleName;
    }

    //@Override
    public String getSubSystem() {
      return null;
    }

    //@Override
    public String getEventGroupKey() {
      return null;
    }

    //@Override
    public double getMillis() {
      return 0;
    }

    //@Override
    public Iterator<String> getExtraParameterNames() {
      return Arrays.asList(extraParameters[0]).iterator();
    }

    //@Override
    public String getExtraParameter(String name) {
      for (int i = 0; i < extraParameters[0].length; i++) {
        if (extraParameters[0][i].equals(name)) {
          return extraParameters[1][i];
        }
      }
      fail("Invalid extra parameter requested: " + name);
      return null;
    }
  }
}

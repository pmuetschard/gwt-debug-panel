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
package com.google.gwt.debugpanel.client;

import com.google.gwt.debugpanel.common.StatisticsEvent;
import com.google.gwt.debugpanel.common.Utils;
import com.google.gwt.debugpanel.models.DebugStatisticsModel;
import com.google.gwt.debugpanel.models.DebugStatisticsValue;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsValue;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ButtonBase;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Base class for tests of the {@link GwtDebugStatisticsModel.EventHandler handlers}.
 */
public abstract class AbstractStatisticsModelEventHandlerTestCase extends TestCase {
  protected GwtDebugStatisticsModel model;
  protected DebugStatisticsModel.Node<GwtDebugStatisticsValue> root;
  protected String module;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    model = new GwtDebugStatisticsModel(getHandlers());
    root = model.getRoot();
    module = "module";
    Utils.setInstance(new Utils.Util() {
      //@Override
      public String getStylePrefix() {
        return "";
      }

      //@Override
      public double currentTimeMillis() {
        return System.currentTimeMillis();
      }

      //@Override
      public String formatDate(double time) {
        fail("Unexpected call to formatAsDate");
        return null;
      }

      //@Override
      public String formatClassName(String className) {
        return className;
      }

      //@Override
      public ButtonBase createTextButton(String text, ClickHandler handler) {
        fail("Unexpected call to createTextButton");
        return null;
      }

      //@Override
      public ButtonBase createMenuButton(String text, ClickHandler handler) {
        fail("Unexpected call to createMenuButton");
        return null;
      }
    });
  }

  @Override
  protected void tearDown() throws Exception {
    Utils.setInstance(null);
  }

  protected abstract GwtDebugStatisticsModel.EventHandler[] getHandlers();

  protected StatisticsEvent event(String system, String group, double millis, String type) {
    return new TestStatisticsEvent(module, system, group, millis, type);
  }

  protected void assertNode(
      String label, double start, double end, DebugStatisticsModel.Node<?> node) {
    assertEquals(label, node.getValue().getLabel());
    assertEquals(start, node.getValue().getStartTime());
    assertEquals(end, node.getValue().getEndTime());
  }

  protected void deepCompare(DebugStatisticsModel.Node<GwtDebugStatisticsValue> expected,
      DebugStatisticsModel.Node<GwtDebugStatisticsValue> actual) {
    assertEquals(expected.getValue().getLabel() + " childcount",
        expected.getChildCount(), actual.getChildCount());
    for (int i = 0; i < expected.getChildCount(); i++) {
      deepCompare(expected.getChild(i), actual.getChild(i));
      DebugStatisticsValue expectedValue = expected.getChild(i).getValue();
      DebugStatisticsValue actualValue = actual.getChild(i).getValue();
      assertEquals(expected.getValue().getLabel() + " child " + i,
          expectedValue.getLabel(), actualValue.getLabel());
      assertEquals(expected.getValue().getLabel() + " child " + i,
          expectedValue.getStartTime(), actualValue.getStartTime());
      assertEquals(expected.getValue().getLabel() + " child " + i,
          expectedValue.getEndTime(), actualValue.getEndTime());
    }
  }

  /**
   * Testing event.
   *
   */
  protected static class TestStatisticsEvent implements StatisticsEvent {
    private static final Set<String> acceptedNames = new HashSet<String>() {{
      add("error");
      add("response");
      add("rpc");
    }};
    private String moduleName;
    private String subSystem;
    private String groupKey;
    private double millis;
    private String type;

    public TestStatisticsEvent(
        String moduleName, String subSystem, String groupKey, double millis, String type) {
      this.moduleName = moduleName;
      this.subSystem = subSystem;
      this.groupKey = groupKey;
      this.millis = millis;
      this.type = type;
    }

    //@Override
    public String getModuleName() {
      return moduleName;
    }

    //@Override
    public String getSubSystem() {
      return subSystem;
    }

    //@Override
    public String getEventGroupKey() {
      return groupKey;
    }

    //@Override
    public double getMillis() {
      return millis;
    }

    //@Override
    public Iterator<String> getExtraParameterNames() {
      return Arrays.asList("type", "className").iterator();
    }

    //@Override
    public String getExtraParameter(String name) {
      if ("type".equals(name)) {
        return type;
      } else if ("className".equals(name)) {
        return "class";
      } else if ("method".equals(name) || "service".equals(name) || "params".equals(name)) {
        return name;
      } else if (acceptedNames.contains(name)) {
        return null;
      }
      fail("invalid extra parameter name: " + name);
      return null;
    }
  }
}

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
import com.google.gwt.debugpanel.models.DebugPanelFilter;
import com.google.gwt.debugpanel.models.DebugPanelFilterModel;
import com.google.gwt.debugpanel.models.DebugPanelFilterModelListener;
import com.google.gwt.debugpanel.models.DebugStatisticsValue;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Tests the {@link DebugPanelFilterConfigWidget}.
 */
public class DebugPanelFilterConfigWidgetTest extends AbstractDebugPanelGwtTestCase {
  private static final int MENU_ITEM = 0;
  private static final int SETTINGS_TITLE = 1;
  private static final int DESCRIPTION = 2;
  private static final int GET_WIDGET = 3;
  private static final int ON_APPLY = 4;
  private static final int ON_REMOVE = 5;
  private static final int NUM_INVOKES = 6;

  private DebugPanelFilter filter;
  private DebugPanelFilterModel filterModel;
  private DebugPanelFilterConfigWidget widget;

  public void testConstruction() {
    int[] invokeCounts = initializeWidget(new boolean[] { true });
    assertEquals(0, invokeCounts[MENU_ITEM]);
    assertEquals(1, invokeCounts[SETTINGS_TITLE]);
    assertEquals(1, invokeCounts[DESCRIPTION]);
    assertEquals(1, invokeCounts[GET_WIDGET]);
    assertEquals(0, invokeCounts[ON_APPLY]);
    assertEquals(0, invokeCounts[ON_REMOVE]);
  }

  public void testAdd() {
    int[] invokeCounts = initializeWidget(new boolean[] { true });
    triggerOnClick(widget.addButton);
    assertEquals(1, invokeCounts[ON_APPLY]);
    assertEquals(0, invokeCounts[ON_REMOVE]);
    assertTrue(filterModel.isFilterActive(0));
  }

  public void testVetoedAdd() {
    int[] invokeCounts = initializeWidget(new boolean[] { false });
    triggerOnClick(widget.addButton);
    assertEquals(1, invokeCounts[ON_APPLY]);
    assertEquals(0, invokeCounts[ON_REMOVE]);
    assertFalse(filterModel.isFilterActive(0));
  }

  public void testApply() {
    int[] invokeCounts = initializeWidget(new boolean[] { true });
    triggerOnClick(widget.addButton);
    triggerOnClick(widget.applyButton);
    assertEquals(2, invokeCounts[ON_APPLY]);
    assertEquals(0, invokeCounts[ON_REMOVE]);
    assertTrue(filterModel.isFilterActive(0));
  }

  public void testVetoedApply() {
    boolean[] applyResult = new boolean[] { true };
    int[] invokeCounts = initializeWidget(applyResult);
    triggerOnClick(widget.addButton);
    applyResult[0] = false;
    triggerOnClick(widget.applyButton);
    assertEquals(2, invokeCounts[ON_APPLY]);
    assertEquals(0, invokeCounts[ON_REMOVE]);
    assertFalse(filterModel.isFilterActive(0));
  }

  public void testRemove() {
    int[] invokeCounts = initializeWidget(new boolean[] { true });
    triggerOnClick(widget.addButton);
    triggerOnClick(widget.removeButton);
    assertEquals(1, invokeCounts[ON_APPLY]);
    assertEquals(1, invokeCounts[ON_REMOVE]);
    assertFalse(filterModel.isFilterActive(0));
  }

  private void triggerOnClick(HasHandlers source) {
    source.fireEvent(new ClickEvent() {});
  }

  private int[] initializeWidget(final boolean[] onApplyResult) {
    final int[] invokeCounts = new int[NUM_INVOKES];
    filter = new DebugPanelFilter() {
      //@Override
      public String getMenuItemLabel() {
        invokeCounts[MENU_ITEM]++;
        return "menuItemLabel";
      }

      //@Override
      public String getSettingsTitle() {
        invokeCounts[SETTINGS_TITLE]++;
        return "settingsTitle";
      }

      //@Override
      public String getDescription() {
        invokeCounts[DESCRIPTION]++;
        return "description";
      }

      //@Override
      public Config getConfig() {
        return new Config() {

          @Override
          public View getView() {
            return new View() {
              //@Override
              public Widget getWidget() {
                invokeCounts[GET_WIDGET]++;
                return new Label("widget");
              }

              //@Override
              public boolean onApply() {
                invokeCounts[ON_APPLY]++;
                return onApplyResult[0];
              }

              //@Override
              public void onRemove() {
                invokeCounts[ON_REMOVE]++;
              }
            };
          }
        };
      }

      //@Override
      public boolean include(DebugStatisticsValue value) {
        fail("Unexpected call to include!");
        return false;
      }

      //@Override
      public boolean processChildren() {
        fail("Unexpected call to processChildren!");
        return false;
      }
    };
    filterModel = new DebugPanelFilterModel() {
      private boolean active;

      //@Override
      public int getCountOfAvailableFilters() {
        return 1;
      }

      //@Override
      public DebugPanelFilter getFilter(int idx) {
        if (idx != 0) {
          fail("Invalid index: " + idx);
        }
        return filter;
      }

      //@Override
      public DebugPanelFilter.Config getFilterConfig(int idx) {
        if (idx != 0) {
          fail("Invalid index: " + idx);
        }
        return filter.getConfig();
      }

      //@Override
      public boolean isFilterActive(int idx) {
        if (idx != 0) {
          fail("Invalid index: " + idx);
        }
        return active;
      }

      //@Override
      public void setFilterActive(int idx, boolean active) {
        if (idx != 0) {
          fail("Invalid index: " + idx);
        }
        this.active = active;
      }

      //@Override
      public void addListener(DebugPanelFilterModelListener listener) {
      }

      //@Override
      public void removeListener(DebugPanelFilterModelListener listener) {
      }
    };
    widget = new DebugPanelFilterConfigWidget(filterModel, 0);
    return invokeCounts;
  }
}

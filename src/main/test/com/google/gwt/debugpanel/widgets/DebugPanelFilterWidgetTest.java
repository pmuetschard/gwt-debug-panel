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
import com.google.gwt.debugpanel.models.DebugPanelFilterModelListenerChain;
import com.google.gwt.debugpanel.models.DebugStatisticsValue;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Tests the {@link DebugPanelFilterWidget}.
 */
public class DebugPanelFilterWidgetTest extends AbstractDebugPanelGwtTestCase {
  private static final int MENU_ITEM = 0;
  private static final int SETTINGS_TITLE = 1;
  private static final int DESCRIPTION = 2;
  private static final int NUM_INVOKES = 3;

  private DebugPanelFilter filter;
  private DebugPanelFilterModel filterModel;
  private DebugPanelFilterWidget widget;

  public void testConstruction() {
    int[] invokeCounts = initializeWidget();
    assertEquals(2, invokeCounts[MENU_ITEM]);
    assertEquals(0, invokeCounts[SETTINGS_TITLE]);
    assertEquals(0, invokeCounts[DESCRIPTION]);
  }

  public void testPopupIsShown() {
    initializeWidget();
    widget.button.fireEvent(new ClickEvent() {});
    assertTrue(widget.popup.isVisible());
  }

  public void testActivatingFilterChangesMenuItem() {
    initializeWidget();
    filterModel.setFilterActive(0, true);
    assertTrue(widget.popup.isItemActive(0));
    filterModel.setFilterActive(0, false);
    assertFalse(widget.popup.isItemActive(0));
  }

  private int[] initializeWidget() {
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
                fail("Unexpected call to getWidget");
                return new Label("widget");
              }

              //@Override
              public boolean onApply() {
                fail("Unexpected call to onApply");
                return true;
              }

              //@Override
              public void onRemove() {
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
      private DebugPanelFilterModelListenerChain listener;

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
        if (listener != null) {
          listener.filterStatusChanged(filter, 0, active);
        }
      }

      //@Override
      public void addListener(DebugPanelFilterModelListener listener) {
        this.listener = new DebugPanelFilterModelListenerChain(this.listener, listener);
      }

      //@Override
      public void removeListener(DebugPanelFilterModelListener listener) {
        this.listener = this.listener.remove(listener);
      }
    };
    widget = new DebugPanelFilterWidget(filterModel);
    return invokeCounts;
  }
}

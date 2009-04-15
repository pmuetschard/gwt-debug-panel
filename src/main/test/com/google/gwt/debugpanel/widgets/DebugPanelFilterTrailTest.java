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

/**
 * Tests the {@link DebugPanelFilterTrail}.
 */
public class DebugPanelFilterTrailTest extends AbstractDebugPanelGwtTestCase {
  private DebugPanelFilterModel model;
  private DebugPanelFilterTrail trail;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    final DebugPanelFilter[] filters = GwtDebugPanelFilters.getFilters();

    // This test assumes at least two filters.
    assertTrue(filters.length >= 2);
    model = new DebugPanelFilterModel() {
      private DebugPanelFilterModelListenerChain listener;
      private boolean[] status = new boolean[filters.length];

      //@Override
      public int getCountOfAvailableFilters() {
        return filters.length;
      }

      //@Override
      public DebugPanelFilter getFilter(int idx) {
        return filters[idx];
      }

      //@Override
      public DebugPanelFilter.Config getFilterConfig(int idx) {
        return filters[idx].getConfig();
      }

      //@Override
      public boolean isFilterActive(int idx) {
        return status[idx];
      }

      //@Override
      public void setFilterActive(int idx, boolean active) {
        if (status[idx] != active) {
          status[idx] = active;
          if (listener != null) {
            listener.filterStatusChanged(filters[idx], idx, active);
          }
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
    trail = new DebugPanelFilterTrail(model);
  }

  public void testActivatingAFilterAddsACrumbAndLabel() {
    model.setFilterActive(0, true);
    assertTrue(trail.panel.getWidget(0).isVisible());
    assertTrue(trail.panel.getWidget(1).isVisible());
    assertFalse(trail.panel.getWidget(2).isVisible());
  }

  public void testActivatingTwoFiltersAddsTwoCrumbsAndLabel() {
    model.setFilterActive(1, true);
    assertTrue(trail.panel.getWidget(0).isVisible());
    assertFalse(trail.panel.getWidget(1).isVisible());
    assertTrue(trail.panel.getWidget(2).isVisible());
  }

  public void testBuildWithAnActiveFilterAddsLabelCorrectly() {
    model.setFilterActive(1, true);
    trail = new DebugPanelFilterTrail(model);
    assertTrue(trail.panel.getWidget(0).isVisible());
    assertFalse(trail.panel.getWidget(1).isVisible());
    assertTrue(trail.panel.getWidget(2).isVisible());
  }

  public void testDisablingLastFilterRemovesLabel() {
    model.setFilterActive(1, true);
    assertTrue(trail.panel.getWidget(0).isVisible());

    model.setFilterActive(1, false);
    assertFalse(trail.panel.getWidget(0).isVisible());
  }

  public void testClickingCrumbShowsPopup() {
    model.setFilterActive(0, true);
    ((CommandLink) trail.panel.getWidget(1)).execute();
    assertTrue(trail.isPopupVisible(0));
  }

  public void testDisablingAVisibleFilterClosesPopup() {
    model.setFilterActive(0, true);
    ((CommandLink) trail.panel.getWidget(1)).execute();
    assertTrue(trail.isPopupVisible(0));

    model.setFilterActive(0, false);
    assertFalse(trail.isPopupVisible(0));
  }
}

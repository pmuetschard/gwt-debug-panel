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

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;

/**
 * Tests the {@link GwtDebugPanelFilterModel}.
 */
public class GwtDebugPanelFilterModelTest extends TestCase {
  private Mockery mockery;
  private DebugPanelFilter[] filters;
  private GwtDebugPanelFilterModel model;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mockery = new Mockery();
    filters = new DebugPanelFilter[3];
    for (int i = 0; i < filters.length; i++) {
      filters[i] = mockery.mock(DebugPanelFilter.class, "filterMock" + i);
    }
    model = new GwtDebugPanelFilterModel(filters);
  }

  public void testCountOfAvailableFilters() {
    assertEquals(3, model.getCountOfAvailableFilters());
  }

  public void testGetFilter() {
    assertEquals(filters[0], model.getFilter(0));
    assertEquals(filters[1], model.getFilter(1));
    assertEquals(filters[2], model.getFilter(2));
  }

  public void testFiltersAreDisabledByDefault() {
    for (int i = 0; i < filters.length; i++) {
      assertFalse("filter " + i + " should be inactive", model.isFilterActive(i));
    }
  }

  public void testSetFilterActive() {
    for (int i = 0; i < filters.length; i++) {
      model.setFilterActive(i, true);
      for (int j = 0; j <= i; j++) {
        assertTrue("filter " + j + " should be active", model.isFilterActive(j));
      }
      for (int j = i + 1; j < filters.length; j++) {
        assertFalse("filter " + j + " should be inactive", model.isFilterActive(j));
      }
    }
  }

  public void testSetFilterInactive() {
    for (int i = 0; i < filters.length; i++) {
      model.setFilterActive(i, true);
    }
    for (int i = filters.length - 1; i >= 0; i--) {
      model.setFilterActive(i, false);
      for (int j = 0; j < i; j++) {
        assertTrue("filter " + j + " should be active", model.isFilterActive(j));
      }
      for (int j = i; j < filters.length; j++) {
        assertFalse("filter " + j + " should be inactive", model.isFilterActive(j));
      }
    }
  }

  public void testGetFilterConfig() {
    final DebugPanelFilter.Config config1 = new MockConfig(
        mockery.mock(DebugPanelFilter.Config.View.class, "viewMock1"));
    final DebugPanelFilter.Config config2 = new MockConfig(
        mockery.mock(DebugPanelFilter.Config.View.class, "viewMock2"));
    final DebugPanelFilter.Config config3 = new MockConfig(
        mockery.mock(DebugPanelFilter.Config.View.class, "viewMock3"));
    mockery.checking(new Expectations() {{
      oneOf(filters[0]).getConfig(); will(returnValue(config1));
      oneOf(filters[1]).getConfig(); will(returnValue(config2));
      oneOf(filters[2]).getConfig(); will(returnValue(config3));
    }});

    assertEquals(config1, model.getFilterConfig(0));
    assertEquals(config2, model.getFilterConfig(1));
    assertEquals(config3, model.getFilterConfig(2));

    mockery.assertIsSatisfied();
  }

  public void testEvents() {
    final DebugPanelFilterModelListener l = mockery.mock(DebugPanelFilterModelListener.class);
    final Sequence sequence = mockery.sequence("sequence");
    mockery.checking(new Expectations() {{
      oneOf(l).filterStatusChanged(filters[0], 0, true); inSequence(sequence);
      oneOf(l).filterStatusChanged(filters[0], 0, false); inSequence(sequence);
      oneOf(l).filterStatusChanged(filters[1], 1, true); inSequence(sequence);
      oneOf(l).filterStatusChanged(filters[2], 2, true); inSequence(sequence);
      oneOf(l).filterStatusChanged(filters[2], 2, false); inSequence(sequence);
      oneOf(l).filterStatusChanged(filters[1], 1, false); inSequence(sequence);
    }});

    model.addListener(l);
    model.setFilterActive(0, true);
    model.setFilterActive(0, false);
    model.setFilterActive(1, true);
    model.setFilterActive(2, true);
    model.setFilterActive(2, false);
    model.setFilterActive(1, false);

    model.removeListener(l);
    model.setFilterActive(0, true);
    model.setFilterActive(0, false);
    model.setFilterActive(1, true);
    model.setFilterActive(2, true);
    model.setFilterActive(2, false);
    model.setFilterActive(1, false);

    mockery.assertIsSatisfied();
  }

  private static class MockConfig extends DebugPanelFilter.Config {
    private View view;

    public MockConfig(View view) {
      this.view = view;
    }

    @Override
    public View getView() {
      return view;
    }
  }
}

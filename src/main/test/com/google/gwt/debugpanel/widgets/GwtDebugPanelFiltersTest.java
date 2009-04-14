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
import com.google.gwt.debugpanel.common.Utils;
import com.google.gwt.debugpanel.models.DebugPanelFilter;
import com.google.gwt.debugpanel.models.DebugStatisticsValue;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsValue;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Tests the GWT {@link DebugPanelFilter filters}.
 */
public class GwtDebugPanelFiltersTest extends AbstractDebugPanelGwtTestCase {
  private GwtDebugPanelFilters.TimeFilter timeFilter;
  private GwtDebugPanelFilters.TimeFilter.TimeFilterConfig timeConfig;
  private GwtDebugPanelFilters.DurationFilter durationFilter;
  private GwtDebugPanelFilters.DurationFilter.DurationFilterConfig durationConfig;
  private GwtDebugPanelFilters.RpcFilter rpcFilter;
  private GwtDebugPanelFilters.RpcFilter.RpcFilterConfig rpcConfig;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    Utils.setInstance(new Utils.DefaultUtil());
    timeFilter = new GwtDebugPanelFilters.TimeFilter();
    timeConfig = (GwtDebugPanelFilters.TimeFilter.TimeFilterConfig) timeFilter.getConfig();
    durationFilter = new GwtDebugPanelFilters.DurationFilter();
    durationConfig = 
      (GwtDebugPanelFilters.DurationFilter.DurationFilterConfig) durationFilter.getConfig();
    rpcFilter = new GwtDebugPanelFilters.RpcFilter();
    rpcConfig = (GwtDebugPanelFilters.RpcFilter.RpcFilterConfig) rpcFilter.getConfig();
  }

  @Override
  protected void gwtTearDown() throws Exception {
    super.gwtTearDown();
    Utils.setInstance(null);
  }

  public void testGetFilters() {
    DebugPanelFilter[] filters = GwtDebugPanelFilters.getFilters();
    assertNotNull(filters);
    assertTrue(filters.length > 0);
  }

  public void testTimeFilterStrings() {
    assertNotNull(timeFilter.getMenuItemLabel());
    assertNotNull(timeFilter.getSettingsTitle());
    assertNotNull(timeFilter.getDescription());
  }

  public void testTimeFilterSettings() {
    assertNotNull(timeConfig);
    assertNotNull(timeConfig.getView());
    assertNotNull(timeConfig.getView().getWidget());
  }

  public void testTimeFilterParseWithMillis() {
    Date start = new Date(), end = new Date(start.getTime() + 10);
    timeConfig.startDate.setText(GwtDebugPanelFilters.FORMAT.format(start));
    timeConfig.endDate.setText(GwtDebugPanelFilters.FORMAT.format(end));
    assertTrue(timeConfig.getView().onApply());
    assertEquals(start, timeFilter.getStart());
    assertEquals(end, timeFilter.getEnd());
  }

  public void testTimeFilterParseWithoutMillis() {
    long time = (System.currentTimeMillis() / 1000) * 1000;
    Date start = new Date(time), end = new Date(start.getTime() + 1000);
    DateTimeFormat format = DateTimeFormat.getFormat("HH:mm:ss");
    timeConfig.startDate.setText(format.format(start));
    timeConfig.endDate.setText(format.format(end));
    assertTrue(timeConfig.getView().onApply());
    assertEquals(start, timeFilter.getStart());
    assertEquals(end, timeFilter.getEnd());
  }

  public void testTimeFilterParseInvalidTimeIsResetToNull() {
    timeConfig.startDate.setText(GwtDebugPanelFilters.FORMAT.format(new Date()));
    timeConfig.endDate.setText("abc");
    assertTrue(timeConfig.getView().onApply());
    assertNotNull(timeFilter.getStart());
    assertNull(timeFilter.getEnd());
  }

  public void testTimeFilterParseTwoBlanksDisablesFilter() {
    timeConfig.startDate.setText("");
    timeConfig.endDate.setText("");
    assertFalse(timeConfig.getView().onApply());
    assertNull(timeFilter.getStart());
    assertNull(timeFilter.getEnd());
  }

  public void testTimeFilterParseTwoInvalidsDisablesFilter() {
    timeConfig.startDate.setText("abc");
    timeConfig.endDate.setText("abc");
    assertFalse(timeConfig.getView().onApply());
    assertNull(timeFilter.getStart());
    assertNull(timeFilter.getEnd());
  }

  public void testTimeFilterOnRemoveResetsToNulls() {
    timeFilter.setTime(new Date(), new Date());
    timeConfig.onRemove();
    assertNull(timeFilter.getStart());
    assertNull(timeFilter.getEnd());
    assertEquals("", timeConfig.startDate.getText());
    assertEquals("", timeConfig.endDate.getText());
  }

  public void testTimeFilterNoRangeIncludesAll() {
    timeFilter.setTime(null, null);
    assertTrue(timeFilter.include(null));
  }

  public void testTimeFilterRangeWithoutStart() {
    Date end = new Date();
    timeFilter.setTime(null, end);
    assertTrue(timeFilter.include(value(0, 1000)));
    assertTrue(timeFilter.include(value(0, end.getTime() - 1)));
    assertTrue(timeFilter.include(value(end.getTime(), end.getTime())));
    assertFalse(timeFilter.include(value(0, end.getTime() + 1)));
  }

  public void testTimeFilterRangeWithoutEnd() {
    Date start = new Date();
    timeFilter.setTime(start, null);
    assertTrue(timeFilter.include(value(Long.MAX_VALUE - 1, Long.MAX_VALUE)));
    assertTrue(timeFilter.include(value(start.getTime() + 1, Long.MAX_VALUE)));
    assertTrue(timeFilter.include(value(start.getTime(), start.getTime())));
    assertFalse(timeFilter.include(value(start.getTime() - 1, Long.MAX_VALUE)));
  }

  public void testTimeFilterRangeWithStartAndEnd() {
    Date start = new Date(), end = new Date(start.getTime() + 10);
    timeFilter.setTime(start, end);
    assertTrue(timeFilter.include(value(start.getTime() + 1, end.getTime() - 1)));
    assertTrue(timeFilter.include(value(start.getTime() + 1, end.getTime())));
    assertTrue(timeFilter.include(value(start.getTime(), end.getTime() - 1)));
    assertTrue(timeFilter.include(value(start.getTime(), end.getTime())));

    assertFalse(timeFilter.include(value(start.getTime() - 1, end.getTime() + 1)));
    assertFalse(timeFilter.include(value(start.getTime() - 1, end.getTime())));
    assertFalse(timeFilter.include(value(start.getTime(), end.getTime() + 1)));
  }

  public void testDurationFilterStrings() {
    assertNotNull(durationFilter.getMenuItemLabel());
    assertNotNull(durationFilter.getSettingsTitle());
    assertNotNull(durationFilter.getDescription());
  }

  public void testDurationFilterSettings() {
    assertNotNull(durationConfig);
    assertNotNull(durationConfig.getView());
    assertNotNull(durationConfig.getView().getWidget());
  }

  public void testDurationFilterParseValidPositive() {
    durationConfig.min.setText("1");
    durationConfig.max.setText("2");
    assertTrue(durationConfig.getView().onApply());
    assertEquals(1, durationFilter.getMinDuration());
    assertEquals(2, durationFilter.getMaxDuration());
  }

  public void testDurationFilterParseNegativeValuesAreSameAsZero() {
    durationConfig.min.setText("-1");
    durationConfig.max.setText("2");
    assertTrue(durationConfig.getView().onApply());
    assertEquals(0, durationFilter.getMinDuration());
    assertEquals(2, durationFilter.getMaxDuration());
  }

  public void testDurationFilterParseInvlidValuesAreSameAsZero() {
    durationConfig.min.setText("1");
    durationConfig.max.setText("abc");
    assertTrue(durationConfig.getView().onApply());
    assertEquals(1, durationFilter.getMinDuration());
    assertEquals(0, durationFilter.getMaxDuration());
  }

  public void testDurationFilterParseZeroValuesDisablesFilter() {
    durationConfig.min.setText("0");
    durationConfig.max.setText("-19");
    assertFalse(durationConfig.getView().onApply());
    assertEquals(0, durationFilter.getMinDuration());
    assertEquals(0, durationFilter.getMaxDuration());
  }

  public void testDurationFilterParseInvalidValuesDisablesFilter() {
    durationConfig.min.setText("abc");
    durationConfig.max.setText("def");
    assertFalse(durationConfig.getView().onApply());
    assertEquals(0, durationFilter.getMinDuration());
    assertEquals(0, durationFilter.getMaxDuration());
  }

  public void testDurationFilterOnRemoveResetsToZeros() {
    durationFilter.setDuration(10, 20);
    durationConfig.onRemove();
    assertEquals(0, durationFilter.getMinDuration());
    assertEquals(0, durationFilter.getMaxDuration());
    assertEquals("0", durationConfig.min.getText());
    assertEquals("0", durationConfig.max.getText());
  }

  public void testDurationFilterNoRangeIncludesAll() {
    durationFilter.setDuration(0, 0);
    assertTrue(durationFilter.include(null));
  }

  public void testDurationFilterRangeWithoutStart() {
    durationFilter.setDuration(0, 10);
    assertTrue(durationFilter.include(value(0, 0)));
    assertTrue(durationFilter.include(value(10, 19)));
    assertTrue(durationFilter.include(value(20, 30)));
    assertFalse(durationFilter.include(value(30, 41)));
  }

  public void testDurationFilterRangeWithoutEnd() {
    durationFilter.setDuration(10, 0);
    assertTrue(durationFilter.include(value(0, 1000)));
    assertTrue(durationFilter.include(value(10, 21)));
    assertTrue(durationFilter.include(value(30, 40)));
    assertFalse(durationFilter.include(value(40, 49)));
  }

  public void testDurationFilterRangeWithStartAndEnd() {
    durationFilter.setDuration(10, 20);
    assertTrue(durationFilter.include(value(0, 15)));
    assertTrue(durationFilter.include(value(20, 39)));
    assertTrue(durationFilter.include(value(40, 51)));
    assertTrue(durationFilter.include(value(50, 60)));
    assertTrue(timeFilter.include(value(60, 80)));

    assertFalse(durationFilter.include(value(0, 9)));
    assertFalse(durationFilter.include(value(10, 31)));
  }

  public void testRpcFilterStrings() {
    assertNotNull(rpcFilter.getMenuItemLabel());
    assertNotNull(rpcFilter.getSettingsTitle());
    assertNotNull(rpcFilter.getDescription());
  }

  public void testRpcFilterSettings() {
    assertNotNull(rpcConfig);
    assertNotNull(rpcConfig.getView());
    assertNotNull(rpcConfig.getView().getWidget());
  }

  public void testRpcFilterDefaultPatternIsEmpty() {
    assertEquals("", rpcFilter.getPattern());
  }

  public void testRpcFilterPatternIsTrimmed() {
    rpcConfig.textbox.setText(" abc ");
    assertTrue(rpcConfig.onApply());
    assertEquals("abc", rpcFilter.getPattern());
  }

  public void testRpcFilterEmptyPatternDisablesFilter() {
    rpcConfig.textbox.setText("   ");
    assertFalse(rpcConfig.onApply());
    assertEquals("", rpcFilter.getPattern());
  }

  public void testRpcFilterOnRemoveResetsToEmptyPattern() {
    rpcConfig.textbox.setText("abc");
    rpcConfig.onApply();
    rpcConfig.onRemove();
    assertEquals("", rpcFilter.getPattern());
    assertEquals("", rpcConfig.textbox.getText());
  }

  public void testRpcFilterDefaultPatternIncludesAll() {
    assertTrue(rpcFilter.include(null));
  }

  public void testRpcFilterEmptyPatternIncludesAll() {
    rpcFilter.setPattern("");
    assertTrue(rpcFilter.include(null));
  }

  public void testRpcFilterDoesNotIncludeNullValues() {
    rpcFilter.setPattern("abc");
    assertFalse(rpcFilter.include(null));
  }

  public void testRpcFilterDoesNotPassNonGwtEvents() {
    rpcFilter.setPattern("abc");
    assertFalse(rpcFilter.include(value(10, 20)));
  }

  public void testRpcFilterDoesNotPassEventsWithoutRpcDetail() {
    rpcFilter.setPattern("abc");
    assertFalse(rpcFilter.include(value(null)));
  }

  public void testRpcFilterPassesEventsWithSameRpcAsPattern() {
    rpcFilter.setPattern("abc");
    assertTrue(rpcFilter.include(value("abc")));
  }

  public void testRpcFilterPassesEventsMatchingASimpleRegExPattern() {
    rpcFilter.setPattern("abc*");
    assertTrue(rpcFilter.include(value("ab")));
    assertTrue(rpcFilter.include(value("abc")));
    assertTrue(rpcFilter.include(value("abccc")));
    assertFalse(rpcFilter.include(value("cab")));
    assertFalse(rpcFilter.include(value("abcd")));
  }

  public void testRpcFilterPassesEventsMatchingADotStarpattern() {
    rpcFilter.setPattern("a.*c");
    assertTrue(rpcFilter.include(value("ac")));
    assertTrue(rpcFilter.include(value("abc")));
    assertTrue(rpcFilter.include(value("a_d_c")));
    assertFalse(rpcFilter.include(value("_abc")));
    assertFalse(rpcFilter.include(value("abc_")));
  }

  private static DebugStatisticsValue value(final long start, final long end) {
    return new DebugStatisticsValue("name", "module", start, end) {};
  }

  private static DebugStatisticsValue value(String rpc) {
    GwtDebugStatisticsValue value = new GwtDebugStatisticsValue("name", "module", 10, 20);
    if (rpc != null) {
      value.setRpcMethod(rpc);
    }
    return value;
  }
}

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

import com.google.gwt.debugpanel.common.ExceptionData;
import com.google.gwt.debugpanel.common.StatisticsEvent;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;

/**
 * Tests the {@link GwtExceptionModel}.
 */
public class GwtExceptionModelTest extends TestCase {
  private Mockery mockery;
  private GwtExceptionModel model;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mockery = new Mockery();
    model = new GwtExceptionModel();
  }

  public void testModelIsInitiallyEmpty() {
    assertEquals(0, model.getExceptionEventCount());
  }

  public void testNonErrorSubSystemEventsAreIgnored() {
    final StatisticsEvent event = mockery.mock(StatisticsEvent.class);
    mockery.checking(new Expectations() {{
      oneOf(event).getSubSystem(); will(returnValue("system"));
    }});

    model.onStatisticsEvent(event);
    assertEquals(0, model.getExceptionEventCount());

    mockery.assertIsSatisfied();
  }

  public void testNonErrorEventGroupEventsAreIgnored() {
    final StatisticsEvent event = mockery.mock(StatisticsEvent.class);
    mockery.checking(new Expectations() {{
      oneOf(event).getSubSystem(); will(returnValue("error"));
      oneOf(event).getEventGroupKey(); will(returnValue("group"));
    }});

    model.onStatisticsEvent(event);
    assertEquals(0, model.getExceptionEventCount());

    mockery.assertIsSatisfied();
  }

  public void testNonErrorTypeEventsAreIgnored() {
    final StatisticsEvent event = mockery.mock(StatisticsEvent.class);
    mockery.checking(new Expectations() {{
      oneOf(event).getSubSystem(); will(returnValue("error"));
      oneOf(event).getEventGroupKey(); will(returnValue("error"));
      oneOf(event).getExtraParameter("type"); will(returnValue("type"));
    }});

    model.onStatisticsEvent(event);
    assertEquals(0, model.getExceptionEventCount());

    mockery.assertIsSatisfied();
  }

  public void testEventsWithEmptyDataAreIgnored() {
    final StatisticsEvent event = mockery.mock(StatisticsEvent.class);
    mockery.checking(new Expectations() {{
      oneOf(event).getSubSystem(); will(returnValue("error"));
      oneOf(event).getEventGroupKey(); will(returnValue("error"));
      oneOf(event).getExtraParameter("type"); will(returnValue("error"));
      oneOf(event).getExtraParameter("error"); will(returnValue(null));
    }});

    model.onStatisticsEvent(event);
    assertEquals(0, model.getExceptionEventCount());

    mockery.assertIsSatisfied();
  }

  public void testNonJavaSrciptObjectsAreIgnored() {
    final StatisticsEvent event = mockery.mock(StatisticsEvent.class);
    mockery.checking(new Expectations() {{
      oneOf(event).getSubSystem(); will(returnValue("error"));
      oneOf(event).getEventGroupKey(); will(returnValue("error"));
      oneOf(event).getExtraParameter("type"); will(returnValue("error"));
      oneOf(event).getExtraParameter("error"); will(returnValue("aString"));
    }});

    model.onStatisticsEvent(event);
    assertEquals(0, model.getExceptionEventCount());

    mockery.assertIsSatisfied();
  }

  public void testJavaScriptObjectIsNotIgnored() {
    final StatisticsEvent event = mockery.mock(StatisticsEvent.class);
    mockery.checking(new Expectations() {{
      oneOf(event).getSubSystem(); will(returnValue("error"));
      oneOf(event).getEventGroupKey(); will(returnValue("error"));
      oneOf(event).getExtraParameter("type"); will(returnValue("error"));
      oneOf(event).getExtraParameter("error"); will(returnValue(new ExceptionData() {}));
      oneOf(event).getModuleName(); will(returnValue("module"));
      oneOf(event).getMillis(); will(returnValue(123.0));
    }});

    model.onStatisticsEvent(event);
    assertEquals(1, model.getExceptionEventCount());

    mockery.assertIsSatisfied();
  }

  public void testRemoveOfOnlyEvent() {
    model.add(new ExceptionModel.ExceptionEvent(null, 0, null));
    assertEquals(1, model.getExceptionEventCount());
    model.removeExceptionEvent(0);
    assertEquals(0, model.getExceptionEventCount());
  }

  public void testRemoveOfEvent() {
    model.add(new ExceptionModel.ExceptionEvent(null, 0, null));
    model.add(new ExceptionModel.ExceptionEvent(null, 1, null));
    model.add(new ExceptionModel.ExceptionEvent(null, 2, null));
    assertEquals(3, model.getExceptionEventCount());
    model.removeExceptionEvent(1);
    assertEquals(2, model.getExceptionEventCount());
    assertEquals(0.0, model.getExceptionEvent(0).time);
    assertEquals(2.0, model.getExceptionEvent(1).time);
  }

  public void testAddFiresEvent() {
    final ExceptionModelListener listener = mockery.mock(ExceptionModelListener.class);
    final ExceptionModel.ExceptionEvent ev0 = new ExceptionModel.ExceptionEvent(null, 0, null);
    final ExceptionModel.ExceptionEvent ev1 = new ExceptionModel.ExceptionEvent(null, 1, null);
    final ExceptionModel.ExceptionEvent ev2 = new ExceptionModel.ExceptionEvent(null, 2, null);
    mockery.checking(new Expectations() {{
      oneOf(listener).exceptionAdded(0, ev0);
      oneOf(listener).exceptionAdded(1, ev1);
      oneOf(listener).exceptionAdded(2, ev2);
    }});

    model.addListener(listener);
    model.add(ev0);
    model.add(ev1);
    model.add(ev2);

    assertEquals(3, model.getExceptionEventCount());
    assertEquals(0.0, model.getExceptionEvent(0).time);
    assertEquals(1.0, model.getExceptionEvent(1).time);
    assertEquals(2.0, model.getExceptionEvent(2).time);
    mockery.assertIsSatisfied();
  }

  public void testRemoveFiresEvent() {
    final ExceptionModelListener listener = mockery.mock(ExceptionModelListener.class);
    final ExceptionModel.ExceptionEvent ev0 = new ExceptionModel.ExceptionEvent(null, 0, null);
    final ExceptionModel.ExceptionEvent ev1 = new ExceptionModel.ExceptionEvent(null, 1, null);
    final ExceptionModel.ExceptionEvent ev2 = new ExceptionModel.ExceptionEvent(null, 2, null);
    mockery.checking(new Expectations() {{
      allowing(listener).exceptionAdded(
          with(any(Integer.class)), with(any(ExceptionModel.ExceptionEvent.class)));
      oneOf(listener).exceptionRemoved(1);
    }});

    model.addListener(listener);
    model.add(ev0);
    model.add(ev1);
    model.add(ev2);
    model.removeExceptionEvent(1);

    assertEquals(2, model.getExceptionEventCount());
    assertEquals(0.0, model.getExceptionEvent(0).time);
    assertEquals(2.0, model.getExceptionEvent(1).time);
    mockery.assertIsSatisfied();
  }

  public void testRemovedListenerNoLongerReceivesEvents() {
    final ExceptionModelListener listener = mockery.mock(ExceptionModelListener.class);
    model.addListener(listener);
    model.removeListener(listener);
    model.add(new ExceptionModel.ExceptionEvent(null, 0, null));
    model.add(new ExceptionModel.ExceptionEvent(null, 1, null));
    model.add(new ExceptionModel.ExceptionEvent(null, 2, null));
    model.removeExceptionEvent(1);

    mockery.assertIsSatisfied();
  }
}

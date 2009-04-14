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

import com.google.gwt.debugpanel.common.StatisticsEvent;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;

/**
 * Tests the {@link GwtDebugStatisticsModel}.
 */
public class GwtDebugStatisticsModelTest extends TestCase {
  private Mockery mockery;
  private GwtDebugStatisticsModel model;
  private GwtDebugStatisticsModel.EventHandler handler1, handler2, handler3;
  private DebugStatisticsModelListener<GwtDebugStatisticsValue> listener;
  private DebugStatisticsModel.Node<GwtDebugStatisticsValue> root;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mockery = new Mockery();
    handler1 = mockery.mock(GwtDebugStatisticsModel.EventHandler.class, "handler1");
    handler2 = mockery.mock(GwtDebugStatisticsModel.EventHandler.class, "handler2");
    handler3 = mockery.mock(GwtDebugStatisticsModel.EventHandler.class, "handler3");
    model = new GwtDebugStatisticsModel(handler1, handler2, handler3);
    listener = mockery.mock(DebugStatisticsModelListener.class);
    root = model.getRoot();
    model.addDebugStatisticsModelListener(listener);
  }

  public void testThatHandlersAreCalledInSequence() {
    final StatisticsEvent event = mockery.mock(StatisticsEvent.class);
    final Sequence seq = mockery.sequence("handlers");
    mockery.checking(new Expectations() {{
      allowing(handler1).handle(model, event); will(returnValue(false)); inSequence(seq);
      allowing(handler2).handle(model, event); will(returnValue(false)); inSequence(seq);
      allowing(handler3).handle(model, event); will(returnValue(false)); inSequence(seq);
    }});

    model.onStatisticsEvent(event);
    mockery.assertIsSatisfied();
  }

  public void testThatIfNoHandlerAnswersWithANodeNothingHappens() {
    final StatisticsEvent event = mockery.mock(StatisticsEvent.class);
    mockery.checking(new Expectations() {{
      allowing(handler1).handle(model, event); will(returnValue(false));
      allowing(handler2).handle(model, event); will(returnValue(false));
      allowing(handler3).handle(model, event); will(returnValue(false));
    }});

    model.onStatisticsEvent(event);

    assertEquals(0, root.getChildCount());
    mockery.assertIsSatisfied();
  }

  public void testThatIfEarlierHandlerReturnsANodeLaterHandlersAreNotInvoked() {
    final StatisticsEvent event = mockery.mock(StatisticsEvent.class);
    mockery.checking(new Expectations() {{
      oneOf(handler1).handle(model, event); will(returnValue(true));
    }});

    model.onStatisticsEvent(event);

    mockery.assertIsSatisfied();
  }

  public void testThatForUpdateWithParentsIfARootIsUpdatedNoOtherNodeHasItsValuesChanged() {
    final GwtDebugStatisticsModel.GwtNode child = node(11, 19);
    mockery.checking(new Expectations() {{
      oneOf(listener).nodeAdded(root, child, 0);
      oneOf(listener).nodeChanged(with(child), with(any(GwtDebugStatisticsValue.class)));
    }});

    model.addNode(root, child, 0);
    model.updateNodeAndItsParents(child, value(10, 20));
    assertEquals(10.0, child.getValue().getStartTime());
    assertEquals(20.0, child.getValue().getEndTime());

    mockery.assertIsSatisfied();
  }

  public void testThatForUpdateWithParentsIfChildOfARootIsUpdatedTheCorrespondingRootIsUpdated() {
    final GwtDebugStatisticsModel.GwtNode parent = node(11, 19);
    final GwtDebugStatisticsModel.GwtNode child = node(12, 18);
    mockery.checking(new Expectations() {{
      oneOf(listener).nodeAdded(root, parent, 0);
      oneOf(listener).nodeAdded(parent, child, 0);
      oneOf(listener).nodeChanged(with(child), with(any(GwtDebugStatisticsValue.class)));
      oneOf(listener).nodeChanged(with(parent), with(any(GwtDebugStatisticsValue.class)));
    }});

    model.addNode(root, parent, 0);
    model.addNode(parent, child, 0);
    model.updateNodeAndItsParents(child, value(10, 20));
    assertEquals(10.0, parent.getValue().getStartTime());
    assertEquals(20.0, parent.getValue().getEndTime());

    mockery.assertIsSatisfied();
  }

  public void testThatForUpdateWithParentsIfGrandChildIsUpdatedTheCorrespondingNodesAreUpdated() {
    final GwtDebugStatisticsModel.GwtNode grandParent = node(11, 19);
    final GwtDebugStatisticsModel.GwtNode parent = node(12, 18);
    final GwtDebugStatisticsModel.GwtNode child = node(13, 17);
    mockery.checking(new Expectations() {{
      oneOf(listener).nodeAdded(root, grandParent, 0);
      oneOf(listener).nodeAdded(grandParent, parent, 0);
      oneOf(listener).nodeAdded(parent, child, 0);
      oneOf(listener).nodeChanged(with(child), with(any(GwtDebugStatisticsValue.class)));
      oneOf(listener).nodeChanged(with(parent), with(any(GwtDebugStatisticsValue.class)));
      oneOf(listener).nodeChanged(with(grandParent), with(any(GwtDebugStatisticsValue.class)));
    }});

    model.addNode(root, grandParent, 0);
    model.addNode(grandParent, parent, 0);
    model.addNode(parent, child, 0);
    model.updateNodeAndItsParents(child, value(10, 20));
    assertEquals(10.0, grandParent.getValue().getStartTime());
    assertEquals(20.0, grandParent.getValue().getEndTime());
    assertEquals(10.0, parent.getValue().getStartTime());
    assertEquals(20.0, parent.getValue().getEndTime());

    mockery.assertIsSatisfied();
  }

  public void testThatForAddWithParentsIfARootIsUpdatedNoOtherNodeHasItsValuesChanged() {
    final GwtDebugStatisticsModel.GwtNode child = node(10, 20);
    mockery.checking(new Expectations() {{
      oneOf(listener).nodeAdded(root, child, 0);
    }});

    model.addNodeAndUpdateItsParents(null, child, 0);

    mockery.assertIsSatisfied();
  }

  public void testThatForAddWithParentsIfChildOfARootIsUpdatedTheCorrespondingRootIsUpdated() {
    final GwtDebugStatisticsModel.GwtNode parent = node(11, 19);
    final GwtDebugStatisticsModel.GwtNode child = node(10, 20);
    mockery.checking(new Expectations() {{
      oneOf(listener).nodeAdded(root, parent, 0);
      oneOf(listener).nodeAdded(parent, child, 0);
      oneOf(listener).nodeChanged(with(parent), with(any(GwtDebugStatisticsValue.class)));
    }});

    model.addNode(root, parent, 0);
    model.addNodeAndUpdateItsParents(parent, child, 0);
    assertEquals(10.0, parent.getValue().getStartTime());
    assertEquals(20.0, parent.getValue().getEndTime());

    mockery.assertIsSatisfied();
  }

  public void testThatForAddWithParentsIfGrandChildIsUpdatedTheCorrespondingNodesAreUpdated() {
    final GwtDebugStatisticsModel.GwtNode grandParent = node(11, 19);
    final GwtDebugStatisticsModel.GwtNode parent = node(12, 18);
    final GwtDebugStatisticsModel.GwtNode child = node(10, 20);
    mockery.checking(new Expectations() {{
      oneOf(listener).nodeAdded(root, grandParent, 0);
      oneOf(listener).nodeAdded(grandParent, parent, 0);
      oneOf(listener).nodeAdded(parent, child, 0);
      oneOf(listener).nodeChanged(with(parent), with(any(GwtDebugStatisticsValue.class)));
      oneOf(listener).nodeChanged(with(grandParent), with(any(GwtDebugStatisticsValue.class)));
    }});

    model.addNode(root, grandParent, 0);
    model.addNode(grandParent, parent, 0);
    model.addNodeAndUpdateItsParents(parent, child, 0);
    assertEquals(10.0, grandParent.getValue().getStartTime());
    assertEquals(20.0, grandParent.getValue().getEndTime());
    assertEquals(10.0, parent.getValue().getStartTime());
    assertEquals(20.0, parent.getValue().getEndTime());

    mockery.assertIsSatisfied();
  }

  private GwtDebugStatisticsModel.GwtNode node(double start, double end) {
    return new GwtDebugStatisticsModel.GwtNode("label", "module", start, end);
  }
  private GwtDebugStatisticsValue value(double start, double end) {
    return new GwtDebugStatisticsValue("label", "module", start, end);
  }
}

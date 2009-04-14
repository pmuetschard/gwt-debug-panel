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
import com.google.gwt.debugpanel.models.DebugStatisticsModel.Node;

import junit.framework.TestCase;

/**
 * Tests the {@link DebugStatisticsModel}.
 */
public class DebugStatisticsModelTest extends TestCase {
  private DebugStatisticsModel<Value> model;
  private TestModelListener listener;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    model = new DebugStatisticsModel<Value>() {
      @Override
      public void onStatisticsEvent(StatisticsEvent event) {
        fail("Unexpected call to onStatisticsEvent");
      }
    };
    model.addDebugStatisticsModelListener(listener = new TestModelListener());
  }

  public void testAddingSingleChildToRoot() {
    model.addNode(model.getRoot(), node(), 0);
    assertEquals(1, model.getRoot().getChildCount());
    assertNotNull(model.getRoot().getChild(0));
    assertTrue(listener.newValueInvoked);
    assertEquals(model.getRoot(), listener.parent);
    assertEquals(model.getRoot().getChild(0), listener.node);
  }

  public void testAddingMultipleChildrenToRoot() {
    model.addNode(model.getRoot(), node(), 0);
    model.addNode(model.getRoot(), node(), 1);
    model.addNode(model.getRoot(), node(), 2);
    assertEquals(3, model.getRoot().getChildCount());
    assertNotNull(model.getRoot().getChild(0));
    assertNotNull(model.getRoot().getChild(1));
    assertNotNull(model.getRoot().getChild(2));
  }

  public void testAddingInReverseOrder() {
    Node<Value> n1 = node(), n2 = node(), n3 = node();
    model.addNode(model.getRoot(), n1, 0);
    model.addNode(model.getRoot(), n2, 0);
    model.addNode(model.getRoot(), n3, 0);
    assertEquals(3, model.getRoot().getChildCount());
    assertEquals(n3, model.getRoot().getChild(0));
    assertEquals(n2, model.getRoot().getChild(1));
    assertEquals(n1, model.getRoot().getChild(2));
  }

  public void testAddingSingleChildToNonRoot() {
    model.addNode(model.getRoot(), node(), 0);
    model.addNode(model.getRoot().getChild(0), node(), 0);
    assertEquals(1, model.getRoot().getChildCount());
    assertEquals(1, model.getRoot().getChild(0).getChildCount());
    assertNotNull(model.getRoot().getChild(0).getChild(0));
    assertTrue(listener.newValueInvoked);
    assertEquals(model.getRoot().getChild(0), listener.parent);
    assertEquals(model.getRoot().getChild(0).getChild(0), listener.node);
  }

  public void testMultipleChildrenToNonRoot() {
    model.addNode(model.getRoot(), node(), 0);
    model.addNode(model.getRoot().getChild(0), node(), 0);
    model.addNode(model.getRoot().getChild(0), node(), 1);
    assertEquals(1, model.getRoot().getChildCount());
    assertEquals(2, model.getRoot().getChild(0).getChildCount());
    assertNotNull(model.getRoot().getChild(0).getChild(0));
    assertNotNull(model.getRoot().getChild(0).getChild(1));
  }

  public void testUpdateOfNode() {
    model.addNode(model.getRoot(), node(), 0);
    Value value = value();
    model.updateNode(model.getRoot().getChild(0), value);
    assertEquals(value, model.getRoot().getChild(0).getValue());
    assertTrue(listener.valueChangedInvoked);
    assertEquals(model.getRoot().getChild(0), listener.node);
    assertEquals(value, listener.value);
  }

  public void testUpdateOfNodeWithSameValueIsNoOp() {
    Value value = value();
    model.addNode(model.getRoot(), node(value), 0);
    model.updateNode(model.getRoot().getChild(0), value);
    assertFalse(listener.valueChangedInvoked);
  }

  public void testRemoveListener() {
    model.removeDebugStatisticsModelListener(listener);
    model.addNode(model.getRoot(), node(), 0);
    model.updateNode(model.getRoot().getChild(0), value());
    assertFalse(listener.newValueInvoked);
    assertFalse(listener.valueChangedInvoked);
  }

  private static Value value() {
    return new Value("label", "module1", 0, 1); 
  }

  private static Node<Value> node() {
    return node(value());
  }

  private static Node<Value> node(Value value) {
    return new Node<Value>(value);
  }

  private static class TestModelListener implements DebugStatisticsModelListener<Value> {
    public Node<Value> parent, node;
    public Value value;
    public boolean newValueInvoked, valueChangedInvoked;

    @Override
    public void nodeAdded(Node<Value> p, Node<Value> n, int idx) {
      parent = p;
      node = n;
      newValueInvoked = true;
    }

    @Override
    public void nodeChanged(Node<Value> n, Value v) {
      node = n;
      value = v;
      valueChangedInvoked = true;
    }

    public void reset() {
      parent = node = null;
      value = null;
      newValueInvoked = valueChangedInvoked = false;
    }
  }

  private static class Value extends DebugStatisticsValue {
    public Value(String label, String moduleName, double startTime, double endTime) {
      super(label, moduleName, startTime, endTime);
    }
  }
}

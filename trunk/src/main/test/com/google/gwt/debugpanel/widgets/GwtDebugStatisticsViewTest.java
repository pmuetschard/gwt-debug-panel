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
import com.google.gwt.debugpanel.models.GwtDebugPanelFilterModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsValue;
import com.google.gwt.debugpanel.models.DebugStatisticsModel.Node;

/**
 * Tests the {@link GwtDebugStatisticsView}.
 */
public class GwtDebugStatisticsViewTest extends AbstractDebugPanelGwtTestCase {
  private GwtDebugStatisticsView view;
  private MockListener listener;
  private Object root;
  private Node<GwtDebugStatisticsValue> myRoot;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    Utils.setInstance(new Utils.DefaultUtil());
    GwtDebugStatisticsModel model = new GwtDebugStatisticsModel();
    view = new GwtDebugStatisticsView(model, new GwtDebugPanelFilterModel(new DebugPanelFilter[0]));
    view.addTreeTableModelListener(listener = new MockListener());
    root = view.getRoot();
    myRoot = model.getRoot();
  }

  @Override
  protected void gwtTearDown() throws Exception {
    super.gwtTearDown();
    Utils.setInstance(null);
  }

  public void testNewRootGetsAddedCorrectly() {
    view.nodeAdded(myRoot, new Node<GwtDebugStatisticsValue>(value()), 0);
    assertEquals(1, view.getChildCount(root));
    listener.assertAddedAndReset(root, view.getChild(root, 0), 0);
  }

  public void testAddingOfMultipleRootsOutOfOrder() {
    view.nodeAdded(myRoot, new Node<GwtDebugStatisticsValue>(value()), 0);
    assertEquals(1, view.getChildCount(root));
    listener.assertAddedAndReset(root, view.getChild(root, 0), 0);

    view.nodeAdded(myRoot, new Node<GwtDebugStatisticsValue>(value()), 0);
    assertEquals(2, view.getChildCount(root));
    listener.assertAddedAndReset(root, view.getChild(root, 0), 0);
  }

  public void testNewNodeGetsAddedCorrectly() {
    Node<GwtDebugStatisticsValue> myParent = new Node<GwtDebugStatisticsValue>(value());
    view.nodeAdded(myRoot, myParent, 0);
    Object parent = view.getChild(root, 0);
    listener.reset();

    view.nodeAdded(myParent, new Node<GwtDebugStatisticsValue>(value()), 0);
    assertEquals(1, view.getChildCount(root));
    assertEquals(1, view.getChildCount(parent));
    listener.assertAddedAndReset(parent, view.getChild(parent, 0), 0);
  }

  public void testAddingOfMultipleNodesAddsSecondNodeAtBottom() {
    Node<GwtDebugStatisticsValue> myParent = new Node<GwtDebugStatisticsValue>(value());
    view.nodeAdded(myRoot, myParent, 0);
    Object parent = view.getChild(root, 0);
    listener.reset();

    view.nodeAdded(myParent, new Node<GwtDebugStatisticsValue>(value()), 0);
    assertEquals(1, view.getChildCount(root));
    assertEquals(1, view.getChildCount(parent));
    listener.assertAddedAndReset(parent, view.getChild(parent, 0), 0);

    view.nodeAdded(myParent, new Node<GwtDebugStatisticsValue>(value()), 1);
    assertEquals(1, view.getChildCount(root));
    assertEquals(2, view.getChildCount(parent));
    listener.assertAddedAndReset(parent, view.getChild(parent, 1), 1);
  }

  public void testChangeInValueIsPropagated() {
    GwtDebugStatisticsValue value1 = value(), value2 = value();
    Node<GwtDebugStatisticsValue> myParent = new Node<GwtDebugStatisticsValue>(value1);
    view.nodeAdded(myRoot, myParent, 0);
    Object node = view.getChild(root, 0);
    listener.reset();

    view.nodeChanged(myParent, value2);
    listener.assertChangedAndReset(node, view.getColumnCount());
  }

  public void testInvalidColumnIndexes() {
    try {
      view.getColumnName(view.getColumnCount());
      fail("Invalid column index should throw exception.");
    } catch (IllegalArgumentException e) {
      // Expected.
    }

    try {
      view.getValueAt(root, view.getColumnCount());
      fail("Invalid column index should throw exception.");
    } catch (IllegalArgumentException e) {
      // Expected.
    }
  }

  public void testRemoveOfListener() {
    Node<GwtDebugStatisticsValue> myParent = new Node<GwtDebugStatisticsValue>(value());
    view.removeTreeTableModelListener(listener);
    view.nodeAdded(myRoot, myParent, 0);
    view.nodeAdded(myParent, new Node<GwtDebugStatisticsValue>(value()), 0);
    view.nodeChanged(myParent, value());
    assertEquals(0, listener.added);
    assertEquals(0, listener.removed);
    assertEquals(0, listener.changed);
  }

  private static GwtDebugStatisticsValue value() {
    return new GwtDebugStatisticsValue("label", "module", 0, 1);
  }

  private static class MockListener implements TreeTableModelListener {
    public int added, removed, changed;
    public Object parent, node;
    public int index = -1;

    @Override
    public void nodeAdded(Object parentNode, Object childNode, int nodeIndex) {
      parent = parentNode;
      node = childNode;
      index = nodeIndex;
      added++;
    }

    @Override
    public void nodeRemoved(Object parentNode, Object childNode, int nodeIndex) {
      parent = parentNode;
      node = childNode;
      index = nodeIndex;
      removed++;
    }

    @Override
    public void valueChanged(Object childNode, int columnIndex) {
      node = childNode;
      index = columnIndex;
      changed++;
    }

    public void reset() {
      added = removed = changed = 0;
      parent = node = null;
      index = -1;
    }

    public void assertAddedAndReset(Object parentNode, Object childNode, int nodeIndex) {
      assertEquals(1, added);
      assertEquals(0, removed);
      assertEquals(0, changed);
      assertEquals(parentNode, parent);
      assertEquals(childNode, node);
      assertEquals(nodeIndex, index);
      reset();
    }

    public void assertRemoveAndReset(Object parentNode, Object childNode, int nodeIndex) {
      assertEquals(0, added);
      assertEquals(1, removed);
      assertEquals(0, changed);
      assertEquals(parentNode, parent);
      assertEquals(childNode, node);
      assertEquals(nodeIndex, index);
      reset();
    }

    public void assertChangedAndReset(Object childNode, int expectedChanges) {
      assertEquals(0, added);
      assertEquals(0, removed);
      assertEquals(expectedChanges, changed);
      assertEquals(childNode, node);
      reset();
    }
  }
}

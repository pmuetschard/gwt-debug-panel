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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the {@link TreeTable}.
 */
public class TreeTableTest extends AbstractDebugPanelGwtTestCase {
  private MyModel.Tree tree;
  private MyModel model;
  private TreeTable table;
  private TreeTable.TreeTableItem root;

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();
    Utils.setInstance(new Utils.DefaultUtil());
    tree = new MyModel.Tree("Root", new MyModel.Tree[] {
        new MyModel.Tree("Node 1", new MyModel.Tree[] {
            new MyModel.Tree("Node 1.1", null),
            new MyModel.Tree("Node 1.2", null)
        }),
        new MyModel.Tree("Node 2", null),
        new MyModel.Tree("Node 3", new MyModel.Tree[] {
            new MyModel.Tree("Node 3.1", new MyModel.Tree[] {
                new MyModel.Tree("Node 3.1.1", null)
            })
        })
    });
    model = new MyModel(tree);

    tree.toggle();
    table = new TreeTable(model, null, null, false);
    root = table.getRoot();
  }

  @Override
  protected void gwtTearDown() throws Exception {
    super.gwtTearDown();
    Utils.setInstance(null);
  }

  public void testTreeConstruction() {
    tree.compare(root);
  }

  public void testTreeModificationSingleNewItem() {
    model.add(tree.child(1), "Node 2.1", 0);
    tree.compare(root);
  }

  public void testTreeModificationSingleNewItemOnTop() {
    model.add(tree.child(0), "Node 1.0", 0);
    tree.compare(root);
  }

  public void testTreeModificationSingleNewItemOnBottom() {
    model.add(tree.child(0), "Node 1.3", 2);
    tree.compare(root);
  }

  public void testTreeModificationSingleNewItemInMiddle() {
    model.add(tree.child(0), "Node 1.1_5", 1);
    tree.compare(root);
  }

  public void testTreeModificationMultipleAdd() {
    model.add(tree.child(1), "Node 2.1", 0);
    model.add(tree.child(0), "Node 1.0", 0);
    model.add(tree.child(0), "Node 1.3", 3);
    tree.compare(root);
  }

  public void testTreeModificationSingleRemoval() {
    model.remove(tree, 1);
    tree.compare(root);
  }

  public void testTreeModificationSingleRemovalFromTop() {
    model.remove(tree.child(0), 0);
    tree.compare(root);
  }

  public void testTreeModificationSingleRemovalFromBottom() {
    model.remove(tree.child(0), 1);
    tree.compare(root);
  }

  public void testTreeModificationRemovalOfSubTree() {
    model.remove(tree, 2);
    tree.compare(root);
  }

  public void testTreeModificationValueChange() {
    model.setName(tree.child(1), "NewName");
    tree.compare(root);
  }

  public void testTreeToogle() {
    root.getChild(0).toggle();
    tree.child(0).toggle();
    tree.compare(root);
  }

  public void testTreeClosingClosesSubtrees() {

    // First, let's open them up.
    root.getChild(2).toggle();
    root.getChild(2).getChild(0).toggle();
    root.getChild(2).getChild(0).getChild(0).toggle();
    tree.child(2).toggle();
    tree.child(2).child(0).toggle();
    tree.child(2).child(0).child(0).toggle();
    tree.compare(root);

    // Now close the top and check children were closed.
    root.getChild(2).toggle();
    tree.child(2).toggle();
    tree.compare(root);
  }

  public void testRemovingAllChildrenOfRootDoesNotThrowAnExceptionIfRootIsNotShown() {

    // A bug caused this to throw a NullPointerException.
    for (int i = model.getChildCount(model.getRoot()) - 1; i >= 0; i--) {
      model.remove(tree, i);
    }
    assertEquals(0, model.getChildCount(model.getRoot()));
    assertEquals(0, root.getChildCount());
  }

  public void testRowAccountingTable() {
    TreeTable.RowAccountingTable rat = new TreeTable.RowAccountingTable();
    TreeTable.RowAccountingTable.Row row10 = rat.newRow();
    TreeTable.RowAccountingTable.Row row20 = rat.newRow();
    TreeTable.RowAccountingTable.Row row30 = rat.newRow();

    assertEquals(row20, row10.next());
    assertEquals(row30, row20.next());
    assertNull(row30.next());

    assertNull(row10.previous());
    assertEquals(row10, row20.previous());
    assertEquals(row20, row30.previous());

    TreeTable.RowAccountingTable.Row row05 = rat.newRow(row10);
    TreeTable.RowAccountingTable.Row row15 = rat.newRow(row20);
    TreeTable.RowAccountingTable.Row row25 = rat.newRow(row30);

    assertEquals(row10, row05.next());
    assertEquals(row15, row10.next());
    assertEquals(row20, row15.next());
    assertEquals(row25, row20.next());
    assertEquals(row30, row25.next());
    assertNull(row30.next());

    assertNull(row05.previous());
    assertEquals(row05, row10.previous());
    assertEquals(row10, row15.previous());
    assertEquals(row15, row20.previous());
    assertEquals(row20, row25.previous());
    assertEquals(row25, row30.previous());
  }

  private static class MyModel implements TreeTableModel {
    private TreeTableModelListener listener;
    private Tree root;

    public MyModel(Tree root) {
      this.root = root;
    }

    @Override
    public Object getRoot() {
      return root;
    }

    @Override
    public int getChildCount(Object parent) {
      return ((Tree) parent).children.size();
    }

    @Override
    public Object getChild(Object parent, int index) {
      return ((Tree) parent).children.get(index);
    }

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
      return "Column " + columnIndex;
    }

    @Override
    public Object getValueAt(Object node, int columnIndex) {
      return ((Tree) node).name + columnIndex;
    }

    @Override
    public void addTreeTableModelListener(TreeTableModelListener l) {
      if (listener != null) {
        fail("Sorry, I only support a single listener");
      }
      listener = l;
    }

    @Override
    public void removeTreeTableModelListener(TreeTableModelListener l) {
      if (listener == l) {
        listener = null;
      }
    }

    public void add(Tree parent, String node, int index) {
      Tree child = new Tree(node, null);
      parent.children.add(index, child);
      if (listener != null) {
        listener.nodeAdded(parent, child, index);
      }
    }

    public void remove(Tree parent, int index) {
      Tree node = parent.children.remove(index);
      if (listener != null) {
        listener.nodeRemoved(parent, node, index);
      }
    }

    public void setName(Tree node, String name) {
      node.name = name;
      if (listener != null) {
        listener.valueChanged(node, 0);
      }
    }

    public static class Tree {
      public String name;
      public List<Tree> children;
      public boolean open;

      public Tree(String name, Tree[] children) {
        this.name = name;
        this.children = new ArrayList<Tree>();
        if (children != null) {
          this.children.addAll(Arrays.asList(children));
        }
        this.open = false;
      }

      public void compare(TreeTable.TreeTableItem item) {
        if (!"Root".equals(name)) {
          item.show();
          assertEquals(name + "0", item.getWidget().getLabel());
        }
        assertEquals(open, item.isOpen());
        assertEquals(children.size(), item.getChildCount());
        for (int i = 0; i < children.size(); i++) {
          children.get(i).compare(item.getChild(i));
        }
      }

      public void toggle() {
        if (children.size() > 0) {
          open = !open;
          if (!open) {
            for (Tree child : children) {
              if (child.open) {
                child.toggle();
              }
            }
          }
        }
      }

      public Tree child(int index) {
        return children.get(index);
      }
    }
  }
}

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

/**
 * A chain of {@link DebugPanelFilterModelListener listeners}.
 */
public class DebugPanelFilterModelListenerChain implements DebugPanelFilterModelListener {
  private DebugPanelFilterModelListenerChain parent;
  private DebugPanelFilterModelListener listener;

  public DebugPanelFilterModelListenerChain(
      DebugPanelFilterModelListenerChain parent, DebugPanelFilterModelListener listener) {
    this.parent = parent;
    this.listener = listener;
  }

  //@Override
  public void filterStatusChanged(DebugPanelFilter filter, int idx, boolean active) {
    listener.filterStatusChanged(filter, idx, active);
    if (parent != null) {
      parent.filterStatusChanged(filter, idx, active);
    }
  }

  public DebugPanelFilterModelListenerChain remove(DebugPanelFilterModelListener l) {
    if (l == listener) {
      return parent;
    } else if (parent == null) {
      return this;
    } else {
      return new DebugPanelFilterModelListenerChain(parent.remove(l), listener);
    }
  }
}

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
package com.google.gwt.debugpanel.client;

import com.google.gwt.debugpanel.models.DebugStatisticsModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsValue;

/**
 * Tests the {@link DefaultStatisticsModelStartupEventHandler}.
 */
public class DefaultStatisticsModelStartupEventHandlerTest
  extends AbstractStatisticsModelEventHandlerTestCase {

  @Override
  protected GwtDebugStatisticsModel.EventHandler[] getHandlers() {
    return new GwtDebugStatisticsModel.EventHandler[] {
        new DefaultStatisticsModelStartupEventHandler()
    };
  }

  public void testStartupEventsCreateCorrectTree() {
    model.onStatisticsEvent(event("startup", "bootstrap", 1, "begin"));
    model.onStatisticsEvent(event("startup", "bootstrap", 2, "selectingPermutation"));
    model.onStatisticsEvent(event("startup", "bootstrap", 3, "end"));
    model.onStatisticsEvent(event("startup", "loadExternalRefs", 4, "begin"));
    model.onStatisticsEvent(event("startup", "loadExternalRefs", 5, "end"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 6, "moduleRequested"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 7, "moduleEvalStart"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 8, "moduleEvalEnd"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 9, "onModuleLoadStart"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 10, "end"));

    assertNotNull(root);
    assertEquals(1, root.getChildCount());

    DebugStatisticsModel.Node<GwtDebugStatisticsValue> node = root.getChild(0);
    assertNode("startup", 1, 10, node);
    assertEquals(3, node.getChildCount());

    DebugStatisticsModel.Node<GwtDebugStatisticsValue> bootstrap = node.getChild(0);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> extRefs = node.getChild(1);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> startup = node.getChild(2);

    assertNode("bootstrap", 1, 3, bootstrap);
    assertEquals(1, bootstrap.getChildCount());
    assertNode("loadExternalRefs", 4, 5, extRefs);
    assertEquals(0, extRefs.getChildCount());
    assertNode("moduleStartup", 5, 10, startup);
    assertEquals(5, startup.getChildCount());

    DebugStatisticsModel.Node<GwtDebugStatisticsValue> perms = bootstrap.getChild(0);
    assertNode("selectingPermutation", 2, 3, perms);
    assertEquals(0, perms.getChildCount());

    DebugStatisticsModel.Node<GwtDebugStatisticsValue> requested = startup.getChild(0);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> received = startup.getChild(1);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> eval = startup.getChild(2);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> injection = startup.getChild(3);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> load = startup.getChild(4);

    assertNode("moduleRequested", 5, 6, requested);
    assertEquals(0, requested.getChildCount());
    assertNode("moduleReceived", 6, 7, received);
    assertEquals(0, received.getChildCount());
    assertNode("moduleEval", 7, 8, eval);
    assertEquals(0, eval.getChildCount());
    assertNode("injection", 8, 9, injection);
    assertEquals(0, injection.getChildCount());
    assertNode("onModuleLoad", 9, 10, load);
    assertEquals(1, load.getChildCount());

    DebugStatisticsModel.Node<GwtDebugStatisticsValue> module = load.getChild(0);
    assertNode("class", 9, 10, module);
    assertEquals(0, module.getChildCount());
  }

  public void testOtherEventsAreIgnored() {
    model.onStatisticsEvent(event("other", "1", 1, "begin"));
    model.onStatisticsEvent(event("other", "1", 2, "child"));
    model.onStatisticsEvent(event("other", "1", 3 , "end"));

    assertEquals(0, root.getChildCount());
  }

  public void testOutOfOrderEvents() {
    module = "module1";
    model.onStatisticsEvent(event("startup", "bootstrap", 1, "begin"));
    model.onStatisticsEvent(event("startup", "bootstrap", 2, "selectingPermutation"));
    model.onStatisticsEvent(event("startup", "bootstrap", 3, "end"));
    model.onStatisticsEvent(event("startup", "loadExternalRefs", 4, "begin"));
    model.onStatisticsEvent(event("startup", "loadExternalRefs", 5, "end"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 6, "moduleRequested"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 7, "moduleEvalStart"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 8, "moduleEvalEnd"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 9, "onModuleLoadStart"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 10, "end"));

    module = "module2";
    model.onStatisticsEvent(event("startup", "moduleStartup", 10, "end"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 9, "onModuleLoadStart"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 8, "moduleEvalEnd"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 7, "moduleEvalStart"));
    model.onStatisticsEvent(event("startup", "moduleStartup", 6, "moduleRequested"));
    model.onStatisticsEvent(event("startup", "loadExternalRefs", 5, "end"));
    model.onStatisticsEvent(event("startup", "loadExternalRefs", 4, "begin"));
    model.onStatisticsEvent(event("startup", "bootstrap", 3, "end"));
    model.onStatisticsEvent(event("startup", "bootstrap", 2, "selectingPermutation"));
    model.onStatisticsEvent(event("startup", "bootstrap", 1, "begin"));

    assertEquals(2, root.getChildCount());
    deepCompare(root.getChild(1), root.getChild(0));
  }
}

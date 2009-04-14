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
 * Tests the {@link DefaultStatisticsModelRpcEventHandler}.
 */
public class DefaultStatisticsModelRpcEventHandlerTest
    extends AbstractStatisticsModelEventHandlerTestCase {

  @Override
  protected GwtDebugStatisticsModel.EventHandler[] getHandlers() {
    return new GwtDebugStatisticsModel.EventHandler[] {
        new DefaultStatisticsModelRpcEventHandler()
    };
  }

  public void testRpcEventsCreateCorrectTree() {
    model.onStatisticsEvent(event("rpc", "1", 1, "begin"));
    model.onStatisticsEvent(event("rpc", "1", 2, "requestSerialized"));
    model.onStatisticsEvent(event("rpc", "1", 3, "requestSent"));
    model.onStatisticsEvent(event("rpc", "1", 4, "responseReceived"));
    model.onStatisticsEvent(event("rpc", "1", 5, "responseDeserialized"));
    model.onStatisticsEvent(event("rpc", "1", 6, "end"));

    assertNotNull(root);
    assertEquals(1, root.getChildCount());

    DebugStatisticsModel.Node<GwtDebugStatisticsValue> node = root.getChild(0);
    assertNode("rpc1", 1, 6, node);
    assertEquals(5, node.getChildCount());

    DebugStatisticsModel.Node<GwtDebugStatisticsValue> serialized = node.getChild(0);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> sent = node.getChild(1);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> received = node.getChild(2);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> deserialized = node.getChild(3);
    DebugStatisticsModel.Node<GwtDebugStatisticsValue> callback = node.getChild(4);

    assertNode("requestSerialized", 1, 2, serialized);
    assertEquals(0, serialized.getChildCount());
    assertNode("requestSent", 2, 3, sent);
    assertEquals(0, sent.getChildCount());
    assertNode("responseReceived", 3, 4, received);
    assertEquals(0, received.getChildCount());
    assertNode("responseDeserialized", 4, 5, deserialized);
    assertEquals(0, deserialized.getChildCount());
    assertNode("callback", 5, 6, callback);
    assertEquals(0, callback.getChildCount());
  }

  public void testOtherEventsAreIgnored() {
    model.onStatisticsEvent(event("other", "1", 1, "begin"));
    model.onStatisticsEvent(event("other", "1", 2, "child"));
    model.onStatisticsEvent(event("other", "1", 3 , "end"));

    assertEquals(0, root.getChildCount());
  }  

  public void testOutOfOrderEvents() {
    model.onStatisticsEvent(event("rpc", "1", 1, "begin"));
    model.onStatisticsEvent(event("rpc", "1", 2, "requestSerialized"));
    model.onStatisticsEvent(event("rpc", "1", 3, "requestSent"));
    model.onStatisticsEvent(event("rpc", "1", 4, "responseReceived"));
    model.onStatisticsEvent(event("rpc", "1", 5, "responseDeserialized"));
    model.onStatisticsEvent(event("rpc", "1", 6, "end"));

    model.onStatisticsEvent(event("rpc", "2", 6, "end"));
    model.onStatisticsEvent(event("rpc", "2", 5, "responseDeserialized"));
    model.onStatisticsEvent(event("rpc", "2", 4, "responseReceived"));
    model.onStatisticsEvent(event("rpc", "2", 3, "requestSent"));
    model.onStatisticsEvent(event("rpc", "2", 2, "requestSerialized"));
    model.onStatisticsEvent(event("rpc", "2", 1, "begin"));

    assertEquals(2, root.getChildCount());
    deepCompare(root.getChild(1), root.getChild(0));
  }
}

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
package com.example.app.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import com.example.app.shared.EchoService;
import com.example.app.shared.EchoServiceAsync;

/**
 * A simple widget that allows the invocation of the {@link EchoService}.
 */
public class EchoWidget extends Composite {
  public EchoWidget(final ExceptionHandler handler) {
    final EchoServiceAsync service = GWT.create(EchoService.class);
    final Grid grid = new Grid(3, 2);
    final TextBox box = new TextBox();

    grid.setWidget(0, 0, label("Message"));
    grid.setWidget(0, 1, box);
    grid.setWidget(1, 0, label("Result"));
    grid.setWidget(2, 1, new Button("Invoke", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        service.echo(box.getText(), new AsyncCallback<String>() {
          @Override
          public void onFailure(Throwable caught) {
            grid.setText(1, 1, "ERROR: " + caught.toString());
            handler.handle(caught);
          }

          @Override
          public void onSuccess(String result) {
            grid.setText(1, 1, result);
          }
        });
      }
    }));
    initWidget(grid);
  }

  private Widget label(String text) {
    return new HTML("<b>" + text + ":</b> ");
  }
}

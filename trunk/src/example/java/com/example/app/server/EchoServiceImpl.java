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
package com.example.app.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.example.app.shared.EchoService;

/**
 * Simple implementation of {@link EchoService}. If the message is "error", it
 * will throw an exception.
 */
public class EchoServiceImpl extends RemoteServiceServlet implements EchoService {
  @Override
  public String echo(String s) {
    if ("error".equalsIgnoreCase(s)) {
      throw new RuntimeException("A Server side error!");
    }
    return "Echo: " + s;
  }
}

// Copyright 2009 Google Inc. All Rights Reserved.

package com.example.app.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;

import com.example.app.shared.EchoServiceAsync;

/**
 * An implementation of {@link EchoServiceAsync} that simulates the acutal RPC
 * call (at least from the view of the Debug Panel). 
 *
 * @author pmuetschard@google.com (Pascal Muetschard)
 */
public class SimulatedEchoService implements EchoServiceAsync {
  protected static final String METHOD = "EchoService.echo";

  public void echo(final String s, final AsyncCallback<String> callback) {
    final int id = getRpcId();
    s(RemoteServiceProxy.timeStat(METHOD, id, "begin"));
    new Timer() {
      @Override
      public void run() {
        s(RemoteServiceProxy.timeStat(METHOD, id, "requestSerialized"));

        new Timer() {
          @Override
          public void run() {
            s(RemoteServiceProxy.bytesStat(METHOD, id, 42 + s.length(), "requestSent"));

            new Timer() {
              @Override
              public void run() {
                s(RemoteServiceProxy.bytesStat(METHOD, id, 12 + s.length(), "responseReceived"));

                new Timer() {
                  @Override
                  public void run() {
                    s(RemoteServiceProxy.timeStat(METHOD, id, "responseDeserialized"));

                    if ("error".equals(s)) {
                      callback.onFailure(new Exception("Server Error"));
                    } else {
                      callback.onSuccess("Echo: " + s);
                    }

                    s(RemoteServiceProxy.timeStat(METHOD, id, "end"));
                  }
                }.schedule(17);
              }
            }.schedule(120);
          }
        }.schedule(23);
      }
    }.schedule(13);
  }

  protected static void s(JavaScriptObject data) {
    if (RemoteServiceProxy.isStatsAvailable()) {
      RemoteServiceProxy.stats(data);
    }
  }

  private native int getRpcId() /*-{
    return @com.google.gwt.user.client.rpc.impl.RemoteServiceProxy::getNextRequestId()();
  }-*/;
}

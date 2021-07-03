/*
 * Copyright (c) 2021-present deepinthink. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deepinthink.magoko.broker.server.handler.rsocket;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.deepinthink.magoko.boot.bootstrap.BootstrapIdentity;
import org.deepinthink.magoko.broker.server.handler.BrokerMessageHandlerRegistry;
import reactor.core.publisher.Mono;

public class RSocketBrokerMessageHandlerRegistry implements BrokerMessageHandlerRegistry {

  private Map<BootstrapIdentity, RSocketBrokerMessageHandler> messageHandlerMap =
      new ConcurrentHashMap<>();

  @Override
  public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
    RSocketBrokerMessageHandler messageHandler = new RSocketBrokerMessageHandler(this);
    return Mono.just(messageHandler);
  }
}

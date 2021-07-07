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
package org.deepinthink.magoko.broker.client.rsocket;

import java.util.Objects;
import org.deepinthink.magoko.broker.client.rsocket.loadbalance.BrokerClientLoadbalancedRSocket;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeType;

public final class BrokerClientRSocketRequesterWrapBuilder {
  private final BrokerClientLoadbalancedRSocket loadbalancedRSocket;
  private MimeType dataMimeType;
  private MimeType metadataMimeType;
  private RSocketStrategies rSocketStrategies;

  public static BrokerClientRSocketRequesterWrapBuilder newBuilder(
      BrokerClientLoadbalancedRSocket loadbalancedRSocket) {
    return new BrokerClientRSocketRequesterWrapBuilder(loadbalancedRSocket);
  }

  BrokerClientRSocketRequesterWrapBuilder(BrokerClientLoadbalancedRSocket loadbalancedRSocket) {
    this.loadbalancedRSocket = Objects.requireNonNull(loadbalancedRSocket);
  }

  public BrokerClientRSocketRequesterWrapBuilder dataMimeType(MimeType dataMimeType) {
    this.dataMimeType = Objects.requireNonNull(dataMimeType);
    return this;
  }

  public BrokerClientRSocketRequesterWrapBuilder metadataMimeType(MimeType metadataMimeType) {
    this.metadataMimeType = Objects.requireNonNull(metadataMimeType);
    return this;
  }

  public BrokerClientRSocketRequesterWrapBuilder rsocketStrategies(
      RSocketStrategies rSocketStrategies) {
    this.rSocketStrategies = rSocketStrategies;
    return this;
  }

  public RSocketRequester build() {
    return RSocketRequester.wrap(
        this.loadbalancedRSocket, this.dataMimeType, this.metadataMimeType, this.rSocketStrategies);
  }
}

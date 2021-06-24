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
package org.deepinthink.magoko.broker.client.context;

import java.util.Objects;
import org.springframework.context.SmartLifecycle;
import org.springframework.messaging.rsocket.RSocketRequester;

public final class BrokerClientRSocketRequesterBootstrap implements SmartLifecycle {

  private final RSocketRequester requester;

  public BrokerClientRSocketRequesterBootstrap(RSocketRequester requester) {
    this.requester = Objects.requireNonNull(requester);
  }

  @Override
  public void start() {
    this.connectLoop();
  }

  private void connectLoop() {
    this.requester.rsocketClient().source().doOnTerminate(this::connectLoop).subscribe();
  }

  @Override
  public void stop() {
    this.requester.rsocketClient().dispose();
  }

  @Override
  public boolean isRunning() {
    return !this.requester.isDisposed();
  }
}

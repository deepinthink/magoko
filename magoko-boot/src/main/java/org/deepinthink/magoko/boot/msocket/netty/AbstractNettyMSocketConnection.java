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
package org.deepinthink.magoko.boot.msocket.netty;

import io.netty.buffer.ByteBuf;
import java.util.Objects;
import reactor.core.Scannable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.netty.Connection;

public abstract class AbstractNettyMSocketConnection implements NettyMSocketConnection {
  protected Sinks.Empty<Void> onClose = Sinks.empty();
  protected final Connection connection;
  protected final Sinks.Many<ByteBuf> sender;

  public AbstractNettyMSocketConnection(Connection connection) {
    this.connection = Objects.requireNonNull(connection);
    this.sender = Sinks.many().unicast().onBackpressureBuffer();
    this.onDispose().doFinally(__ -> doOnDispose()).subscribe();
    this.connection
        .channel()
        .closeFuture()
        .addListener(
            future -> {
              if (!isDisposed()) {
                dispose();
              }
            });
  }

  protected void doOnDispose() {
    this.sender.tryEmitComplete().orThrow();
    this.connection.dispose();
  }

  @Override
  public Connection getConnection() {
    return this.connection;
  }

  @Override
  public void sendFrame(ByteBuf frame) {
    this.sender.tryEmitNext(frame).orThrow();
  }

  @Override
  public Mono<Void> onDispose() {
    return this.onClose.asMono();
  }

  @Override
  public void dispose() {
    this.onClose.tryEmitEmpty().orThrow();
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public boolean isDisposed() {
    return onClose.scan(Scannable.Attr.TERMINATED) || onClose.scan(Scannable.Attr.CANCELLED);
  }
}

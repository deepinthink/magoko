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
import io.netty.buffer.ByteBufAllocator;
import java.net.SocketAddress;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

public interface NettyMSocketConnection extends Disposable {
  Connection getConnection();

  default ByteBufAllocator alloc() {
    return getConnection().channel().alloc();
  }

  default SocketAddress remoteAddress() {
    return getConnection().channel().remoteAddress();
  }

  Flux<ByteBuf> receiveFrames();

  void sendFrame(ByteBuf frame);

  Mono<Void> onDispose();
}

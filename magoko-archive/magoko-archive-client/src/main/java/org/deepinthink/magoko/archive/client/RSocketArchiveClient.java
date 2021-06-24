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
package org.deepinthink.magoko.archive.client;

import java.util.Objects;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;

final class RSocketArchiveClient implements ArchiveClient {

  private final RSocketRequester requester;

  RSocketArchiveClient(RSocketRequester requester) {
    this.requester = Objects.requireNonNull(requester);
  }

  @Override
  public Flux<DataBuffer> getObject(Object id) {
    return this.requester.route("magoko.archive.getObject").data(id).retrieveFlux(DataBuffer.class);
  }
}

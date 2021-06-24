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
package org.deepinthink.magoko.archive.server.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.deepinthink.magoko.boot.bootstrap.BootstrapIdentity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;

@MessageMapping("magoko.archive.standalone")
@Controller
public class ArchiveServerRSocketStandaloneController {

  private final Map<BootstrapIdentity, RSocketRequester> clientStore;

  public ArchiveServerRSocketStandaloneController() {
    this.clientStore = new ConcurrentHashMap<>();
  }

  @ConnectMapping("connect")
  public void onArchiveClientConnect(
      RSocketRequester requester, @Payload BootstrapIdentity identity) {
    this.clientStore.putIfAbsent(identity, requester);
  }
}

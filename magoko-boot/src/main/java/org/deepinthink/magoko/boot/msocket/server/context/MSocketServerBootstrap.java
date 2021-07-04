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
package org.deepinthink.magoko.boot.msocket.server.context;

import java.util.Objects;
import org.deepinthink.magoko.boot.msocket.server.MSocketServer;
import org.deepinthink.magoko.boot.msocket.server.MSocketServerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

public class MSocketServerBootstrap implements SmartLifecycle, ApplicationContextAware {

  private final MSocketServer server;
  private ApplicationContext applicationContext;

  public MSocketServerBootstrap(MSocketServerFactory serverFactory) {
    this.server = Objects.requireNonNull(serverFactory.createServer());
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void start() {
    this.server.start();
    this.applicationContext.publishEvent(new MSocketServerInitializedEvent(this.server));
  }

  @Override
  public void stop() {
    this.server.stop();
  }

  @Override
  public boolean isRunning() {
    return this.server.isRunning();
  }
}

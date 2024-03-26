/*
 * Copyright (c) 2022-present DeepInThink. All rights reserved.
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
package org.deepinthink.magoko.boot.bootstrap.guava;

import com.google.common.util.concurrent.ServiceManager;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.context.SmartLifecycle;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ServiceManagerBootstrap implements SmartLifecycle {
  AtomicBoolean running = new AtomicBoolean(false);
  ServiceManager serviceManager;
  Duration startTimeout;
  Duration stopTimeout;

  @SneakyThrows
  @Override
  public void start() {
    if (running.compareAndSet(false, true)) {
      serviceManager.startAsync().awaitHealthy(startTimeout);
    }
  }

  @SneakyThrows
  @Override
  public void stop() {
    if (running.compareAndSet(true, false)) {
      serviceManager.stopAsync().awaitStopped(stopTimeout);
    }
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }
}

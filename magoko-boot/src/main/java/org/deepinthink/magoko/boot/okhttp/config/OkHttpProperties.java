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
package org.deepinthink.magoko.boot.okhttp.config;

import static org.deepinthink.magoko.boot.okhttp.OkHttpConstants.*;

import java.util.concurrent.TimeUnit;
import lombok.Data;
import org.deepinthink.magoko.boot.okhttp.OkHttpConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = OkHttpConstants.PREFIX)
public class OkHttpProperties {
  private final Dispatcher dispatcher = new Dispatcher();
  private final ConnectionPool connectionPool = new ConnectionPool();

  @Data
  public static class Dispatcher {
    private int corePoolSize = DEFAULT_OKHTTP_DISPATCHER_CORE_POOL_SIZE;
    private int maximumPoolSize = DEFAULT_OKHTTP_DISPATCHER_MAXIMUM_POOL_SIZE;
    private long keepAliveTime = DEFAULT_OKHTTP_DISPATCHER_KEEP_ALIVE_TIME;
    private TimeUnit timeUnit = DEFAULT_OKHTTP_DISPATCHER_TIMEUNIT;
    private String threadName = DEFAULT_OKHTTP_DISPATCHER_THREAD_NAME;
    private boolean daemon = DEFAULT_OKHTTP_DISPATCHER_DAEMON;
  }

  @Data
  public static class ConnectionPool {
    private int maxIdleConnections = DEFAULT_OKHTTP_CONNECTION_POOL_MAX_IDLE_CONNECTIONS;
    private long keepAliveDuration = DEFAULT_OKHTTP_CONNECTION_POOL_KEEP_ALIVE_DURATION;
    private TimeUnit timeUnit = DEFAULT_OKHTTP_DISPATCHER_TIMEUNIT;
  }
}

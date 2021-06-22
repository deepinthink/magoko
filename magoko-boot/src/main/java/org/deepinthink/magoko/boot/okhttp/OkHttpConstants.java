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
package org.deepinthink.magoko.boot.okhttp;

import java.util.concurrent.TimeUnit;

public final class OkHttpConstants {
  public static final String PREFIX = "magoko.boot.okhttp";

  public static final int DEFAULT_OKHTTP_DISPATCHER_CORE_POOL_SIZE =
      Integer.getInteger(PREFIX + ".dispatcher.core-pool-size", 0);
  public static final int DEFAULT_OKHTTP_DISPATCHER_MAXIMUM_POOL_SIZE =
      Integer.getInteger(PREFIX + ".dispatcher.maximum-pool-size", Integer.MAX_VALUE);
  public static final int DEFAULT_OKHTTP_DISPATCHER_KEEP_ALIVE_TIME =
      Integer.getInteger(PREFIX + ".dispatcher.keep-alive-time", 60);
  public static final TimeUnit DEFAULT_OKHTTP_DISPATCHER_TIMEUNIT =
      TimeUnit.valueOf(System.getProperty(PREFIX + ".dispatcher.timeunit", "SECONDS"));
  public static final String DEFAULT_OKHTTP_DISPATCHER_THREAD_NAME =
      System.getProperty(PREFIX + ".dispatcher.thread-name", "OkHttp Dispatcher");
  public static final boolean DEFAULT_OKHTTP_DISPATCHER_DAEMON =
      Boolean.getBoolean(PREFIX + ".dispatcher.daemon");

  public static final int DEFAULT_OKHTTP_CONNECTION_POOL_MAX_IDLE_CONNECTIONS =
      Integer.getInteger(PREFIX + ".connection-pool.max-idle-connections", 5);
  public static final long DEFAULT_OKHTTP_CONNECTION_POOL_KEEP_ALIVE_DURATION =
      Long.getLong(PREFIX + ".connection-pool.keep-alive-duration", 5);
  public static final TimeUnit DEFAULT_OKHTTP_CONNECTION_POOL_TIMEUNIT =
      TimeUnit.valueOf(System.getProperty(PREFIX + ".connection-pool.timeunit", "MINUTES"));

  private OkHttpConstants() {}
}

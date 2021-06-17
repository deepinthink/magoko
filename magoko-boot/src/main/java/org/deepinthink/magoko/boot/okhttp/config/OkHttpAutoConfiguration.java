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

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;
import org.deepinthink.magoko.boot.okhttp.OkHttpClientBuilderCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass(OkHttpClient.class)
@EnableConfigurationProperties(OkHttpProperties.class)
public class OkHttpAutoConfiguration {

  public static final String OKHTTP_DISPATCHER_THREAD_NAME = "OkHttp Dispatcher";

  /**
   * OKHttpClients Should Be Shared.
   *
   * <p>https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/
   */
  private final OkHttpClient sharedClient;

  @Autowired
  public OkHttpAutoConfiguration(OkHttpProperties properties) {
    this.sharedClient =
        new OkHttpClient.Builder()
            .dispatcher(
                new Dispatcher(
                    new ThreadPoolExecutor(
                        properties.getDispatcher().getCorePoolSize(),
                        properties.getDispatcher().getMaximumPoolSize(),
                        properties.getDispatcher().getKeepAliveTime(),
                        properties.getDispatcher().getTimeUnit(),
                        new SynchronousQueue<>(),
                        Util.threadFactory(OKHTTP_DISPATCHER_THREAD_NAME, false))))
            .connectionPool(
                new ConnectionPool(
                    properties.getConnectionPool().getMaxIdleConnections(),
                    properties.getConnectionPool().getKeepAliveDuration(),
                    properties.getConnectionPool().getTimeUnit()))
            .build();
  }

  @Bean
  @Scope("prototype")
  @ConditionalOnMissingBean
  public OkHttpClient.Builder okHttpClientBuilder(
      ObjectProvider<OkHttpClientBuilderCustomizer> customizers) {
    OkHttpClient.Builder builder = this.sharedClient.newBuilder();
    customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return builder;
  }
}

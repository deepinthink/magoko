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
package org.deepinthink.magoko.boot.minio.config;

import com.google.common.base.MoreObjects;
import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.deepinthink.magoko.boot.minio.MinioClientBuilderCustomizer;
import org.deepinthink.magoko.boot.minio.MinioOkHttpClientBuilderProvider;
import org.deepinthink.magoko.boot.okhttp.config.OkHttpAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootConfiguration(proxyBeanMethods = false)
@ConditionalOnClass({MinioClient.class, OkHttpClient.class, MoreObjects.class})
@AutoConfigureAfter(OkHttpAutoConfiguration.class)
@EnableConfigurationProperties(MinioProperties.class)
public class MinioAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public MinioOkHttpClientBuilderProvider minioOkHttpClientBuilderProvider(
      @Autowired(required = false) OkHttpClient.Builder builder) {
    return () -> MoreObjects.firstNonNull(builder, new OkHttpClient.Builder());
  }

  @Bean
  @Scope("prototype")
  @ConditionalOnMissingBean
  public MinioClient.Builder minioClientBuilder(
      MinioProperties properties,
      ObjectProvider<MinioOkHttpClientBuilderProvider> providers,
      ObjectProvider<MinioClientBuilderCustomizer> customizers) {
    MinioOkHttpClientBuilderProvider provider =
        providers.getIfAvailable(() -> OkHttpClient.Builder::new);
    MinioProperties.Endpoint endpoint = properties.getEndpoint();
    MinioProperties.Credentials credentials = properties.getCredentials();
    MinioClient.Builder builder =
        new MinioClient.Builder()
            .httpClient(provider.get().build())
            .endpoint(endpoint.getEndpoint(), endpoint.getPort(), endpoint.isSecure())
            .credentials(credentials.getAccessKey(), credentials.getSecretKey());
    customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
    return builder;
  }
}

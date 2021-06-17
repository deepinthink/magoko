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

import static org.deepinthink.magoko.boot.minio.MinioConstants.*;

import lombok.Data;
import org.deepinthink.magoko.boot.minio.MinioConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = MinioConstants.PREFIX)
public class MinioProperties {

  private final Endpoint endpoint = new Endpoint();
  private final Credentials credentials = new Credentials();

  @Data
  public static class Endpoint {
    private String endpoint = DEFAULT_MINIO_ENDPOINT_ENDPOINT;
    private int port = DEFAULT_MINIO_ENDPOINT_PORT;
    private boolean secure = DEFAULT_MINIO_ENDPOINT_SECURE;
  }

  @Data
  public static class Credentials {
    private String accessKey = DEFAULT_MINIO_CREDENTIALS_ACCESS_KEY;
    private String secretKey = DEFAULT_MINIO_CREDENTIALS_SECRET_KEY;
  }
}

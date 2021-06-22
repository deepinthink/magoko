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
package org.deepinthink.magoko.archive.client.config;

import static org.deepinthink.magoko.archive.client.ArchiveClientConstants.DEFAULT_ARCHIVE_CLIENT_LAUNCH_MODE;

import lombok.Data;
import org.deepinthink.magoko.archive.client.ArchiveClientConstants;
import org.deepinthink.magoko.boot.bootstrap.BootstrapLaunchMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = ArchiveClientConstants.PREFIX)
public class ArchiveClientProperties {

  private BootstrapLaunchMode launchMode = DEFAULT_ARCHIVE_CLIENT_LAUNCH_MODE;
}

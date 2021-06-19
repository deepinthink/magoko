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
package org.deepinthink.magoko.boot.bootstrap;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class BootstrapIdentity {
  private int uid;
  private int type;
  private int id;

  public static BootstrapIdentity from(BootstrapInstance instance) {
    int type = instance.getType();
    int id = instance.getId();
    if (type <= 0 || type > 0x0000007F || id <= 0 || id > 0x00FFFFFF) {
      throw new IllegalArgumentException(
          "BootstrapInstance parameter type should be in range (0, 0x0000007F], id should be in range (0, 0x00FFFFFF].");
    }
    int uid = (type << 24) | id;
    return new BootstrapIdentityBuilder().uid(uid).type(type).id(id).build();
  }

  public static BootstrapIdentity from(int uid) {
    int type = uid >>> 24;
    int id = uid ^ (type << 24);
    return new BootstrapIdentityBuilder().uid(uid).type(type).id(id).build();
  }
}

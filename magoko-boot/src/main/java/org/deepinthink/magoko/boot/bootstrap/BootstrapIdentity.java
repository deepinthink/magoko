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

import static org.deepinthink.magoko.boot.bootstrap.BootstrapConstants.DEFAULT_BOOTSTRAP_IDENTITY_SID_BITS;

import java.util.Objects;

public class BootstrapIdentity {
  private final int uid;
  private final BootstrapInstance instance;

  public static BootstrapIdentity from(BootstrapInstance instance) {
    return new BootstrapIdentity(instance);
  }

  public static BootstrapIdentity from(int uid) {
    int type = uid >>> DEFAULT_BOOTSTRAP_IDENTITY_SID_BITS;
    int sid = uid ^ (type << DEFAULT_BOOTSTRAP_IDENTITY_SID_BITS);
    BootstrapInstance instance = new BootstrapInstance();
    instance.setType(type);
    instance.setSid(sid);
    return from(instance);
  }

  private BootstrapIdentity(BootstrapInstance instance) {
    this.instance = Objects.requireNonNull(instance);
    this.uid = (instance.getType() << DEFAULT_BOOTSTRAP_IDENTITY_SID_BITS) | instance.getSid();
  }

  public int getUid() {
    return uid;
  }

  public BootstrapInstance getInstance() {
    return instance;
  }

  @Override
  public String toString() {
    return "BootstrapIdentity{" + "uid=" + uid + ", instance=" + instance + '}';
  }
}

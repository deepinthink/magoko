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
package org.deepinthink.magoko.boot.module.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.annotation.ReflectiveProcessor;

class ModuleReflectiveProcessor implements ReflectiveProcessor {

  @Override
  public void registerReflectionHints(ReflectionHints hints, AnnotatedElement element) {
    if (element instanceof Class<?> type) {
      registerTypeHints(hints, type);
    } else if (element instanceof Method method) {
      registerMethodHints(hints, method);
    }
  }

  void registerTypeHints(ReflectionHints hints, Class<?> type) {
    hints.registerType(type);
  }

  void registerMethodHints(ReflectionHints hints, Method method) {
    hints.registerMethod(method, ExecutableMode.INVOKE);
  }
}

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
package org.deepinthink.magoko.boot.module;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.deepinthink.magoko.boot.module.annotation.Module;
import org.deepinthink.magoko.boot.module.annotation.ModuleExceptionHandlerMethodResolver;
import org.deepinthink.magoko.boot.module.annotation.ModuleMapping;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.CompositeMessageCondition;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.AbstractMethodMessageHandler;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.RouteMatcher;
import org.springframework.util.SimpleRouteMatcher;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ModuleMethodMessageHandler
    extends AbstractMethodMessageHandler<CompositeMessageCondition> {
  final Class<? extends Annotation> type;

  Predicate<Class<?>> handlerPredicate;

  RouteMatcher routeMatcher;

  public ModuleMethodMessageHandler() {
    this(Module.class);
  }

  public ModuleMethodMessageHandler(Class<? extends Annotation> type) {
    this(type, (beanType) -> AnnotatedElementUtils.hasAnnotation(beanType, type));
  }

  public ModuleMethodMessageHandler(
      Class<? extends Annotation> type, Predicate<Class<?>> handlerPredicate) {
    this.type = type;
    this.handlerPredicate = handlerPredicate;
  }

  protected RouteMatcher obtainRouteMatcher() {
    RouteMatcher routeMatcher = getRouteMatcher();
    Assert.state(routeMatcher != null, "No RouteMatcher set");
    return routeMatcher;
  }

  @Override
  public void afterPropertiesSet() {

    if (this.routeMatcher == null) {
      AntPathMatcher pathMatcher = new AntPathMatcher();
      pathMatcher.setPathSeparator(".");
      this.routeMatcher = new SimpleRouteMatcher(pathMatcher);
    }

    super.afterPropertiesSet();
  }

  @Override
  protected List<? extends HandlerMethodArgumentResolver> initArgumentResolvers() {
    return Collections.emptyList();
  }

  @Override
  protected List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers() {
    return Collections.emptyList();
  }

  @Override
  protected boolean isHandler(Class<?> beanType) {
    return handlerPredicate.test(beanType);
  }

  @Override
  protected CompositeMessageCondition getMappingForMethod(Method method, Class<?> handlerType) {
    CompositeMessageCondition methodCondition = getCondition(method);
    if (methodCondition != null) {
      CompositeMessageCondition typeCondition = getCondition(handlerType);
      if (typeCondition != null) {
        return typeCondition.combine(methodCondition);
      }
    }
    return methodCondition;
  }

  protected CompositeMessageCondition getCondition(AnnotatedElement element) {
    Annotation typeAnnotation = AnnotatedElementUtils.findMergedAnnotation(element, this.type);
    if (typeAnnotation != null)
      return new CompositeMessageCondition(
          new DestinationPatternsMessageCondition(((Class<?>) element).getName()));

    ModuleMapping methodAnnotation =
        AnnotatedElementUtils.findMergedAnnotation(element, ModuleMapping.class);
    if (methodAnnotation == null || methodAnnotation.value().length == 0) {
      return null;
    }

    return new CompositeMessageCondition(
        new DestinationPatternsMessageCondition(methodAnnotation.value(), obtainRouteMatcher()));
  }

  @Override
  protected Set<String> getDirectLookupDestinations(CompositeMessageCondition mapping) {
    Set<String> result = new LinkedHashSet<>();
    for (String pattern :
        mapping.getCondition(DestinationPatternsMessageCondition.class).getPatterns()) {
      if (!obtainRouteMatcher().isPattern(pattern)) {
        result.add(pattern);
      }
    }
    return result;
  }

  @Override
  protected String getDestination(Message<?> message) {
    RouteMatcher.Route route = getDestionationRoute(message);
    return route != null ? route.value() : null;
  }

  public RouteMatcher.Route getDestionationRoute(Message<?> message) {
    return (RouteMatcher.Route)
        message.getHeaders().get(DestinationPatternsMessageCondition.LOOKUP_DESTINATION_HEADER);
  }

  @Override
  protected CompositeMessageCondition getMatchingMapping(
      CompositeMessageCondition mapping, Message<?> message) {
    return mapping.getMatchingCondition(message);
  }

  @Override
  protected Comparator<CompositeMessageCondition> getMappingComparator(Message<?> message) {
    return (info1, info2) -> info1.compareTo(info2, message);
  }

  @Override
  protected AbstractExceptionHandlerMethodResolver createExceptionHandlerMethodResolverFor(
      Class<?> beanType) {
    return new ModuleExceptionHandlerMethodResolver(beanType);
  }
}

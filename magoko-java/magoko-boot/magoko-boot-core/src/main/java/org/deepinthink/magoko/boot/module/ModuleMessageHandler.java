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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleMessageHandler
    implements MessageHandler, ApplicationContextAware, InitializingBean, BeanPostProcessor {
  public static final String FILTERING_DESTINATION_HEADER = "filteringDestination";
  List<Class<?>> handleTypes = new ArrayList<>();
  ModuleMethodMessageHandler messageHandler;

  public ModuleMessageHandler(
      Class<? extends Annotation> type, Predicate<Class<?>> handlePredicate) {
    this.messageHandler = new ModuleMethodMessageHandler(type, handlePredicate);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    this.messageHandler.afterPropertiesSet();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.messageHandler.setApplicationContext(applicationContext);
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    Class<?> beanType = bean.getClass();
    if (this.messageHandler.getHandlerPredicate().test(beanType)) {
      this.handleTypes.add(beanType);
    }
    return bean;
  }

  @Override
  public void handleMessage(Message<?> message) throws MessagingException {
    String destination = this.messageHandler.getDestination(message);
    if (StringUtils.hasLength(destination)) {
      List<Class<?>> destinations = getDestination(message);
      if (!CollectionUtils.isEmpty(destinations)) {
        MessageBuilder<?> builder = MessageBuilder.fromMessage(message);
        for (Class<?> handleType : destinations) {
          handleMessage(
              builder
                  .setHeader(
                      DestinationPatternsMessageCondition.LOOKUP_DESTINATION_HEADER,
                      this.messageHandler
                          .getRouteMatcher()
                          .parseRoute("/" + handleType.getName() + "/" + destination))
                  .build());
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private List<Class<?>> getDestination(Message<?> message) {
    if (CollectionUtils.isEmpty(handleTypes)) {
      return Collections.emptyList();
    }
    Predicate<Class<?>> filter =
        (Predicate<Class<?>>) message.getHeaders().get(FILTERING_DESTINATION_HEADER);

    return filter != null ? handleTypes.stream().filter(filter).toList() : Collections.emptyList();
  }
}

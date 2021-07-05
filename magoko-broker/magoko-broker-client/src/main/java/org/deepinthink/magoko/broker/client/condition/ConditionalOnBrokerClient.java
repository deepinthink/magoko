package org.deepinthink.magoko.broker.client.condition;

import org.deepinthink.magoko.boot.bootstrap.condition.ConditionalOnBootstrapLaunchMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.lang.annotation.*;

import static org.deepinthink.magoko.boot.bootstrap.BootstrapLaunchMode.BROKER;
import static org.deepinthink.magoko.broker.client.BrokerClientConstants.PREFIX;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnClass(RSocketRequester.class)
@ConditionalOnBootstrapLaunchMode(BROKER)
@ConditionalOnProperty(prefix = PREFIX, name = "port")
public @interface ConditionalOnBrokerClient {}

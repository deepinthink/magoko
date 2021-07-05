package org.deepinthink.magoko.broker.client.context;

import org.springframework.context.SmartLifecycle;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.util.Objects;

public class BrokerClientBootstrap implements SmartLifecycle {

  private final RSocketRequester rSocketRequester;

  public BrokerClientBootstrap(RSocketRequester rSocketRequester) {
    this.rSocketRequester = Objects.requireNonNull(rSocketRequester);
  }

  @Override
  public void start() {
    this.rSocketRequester.rsocketClient().source().doOnTerminate(this::start).subscribe();
  }

  @Override
  public void stop() {
    this.rSocketRequester.dispose();
  }

  @Override
  public boolean isRunning() {
    return !this.rSocketRequester.isDisposed();
  }
}

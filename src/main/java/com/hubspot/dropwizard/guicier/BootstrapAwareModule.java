package com.hubspot.dropwizard.guicier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Binder;
import com.google.inject.Module;

import io.dropwizard.setup.Bootstrap;

public abstract class BootstrapAwareModule implements Module {
  private volatile Bootstrap<?> bootstrap = null;

  @Override
  public void configure(Binder binder) {
    configure(decorate(binder), getBootstrap());
  }

  protected Bootstrap<?> getBootstrap() {
    return checkNotNull(this.bootstrap, "bootstrap was not set!");
  }

  public void setBootstrap(Bootstrap<?> bootstrap) {
    checkState(this.bootstrap == null, "bootstrap was already set!");
    this.bootstrap = checkNotNull(bootstrap, "bootstrap is null");
  }

  protected abstract void configure(Binder binder, Bootstrap<?> bootstrap);

  private Binder decorate(final Binder binder) {
    return new ForwardingBinder() {

      @Override
      protected Binder getDelegate() {
        return binder;
      }

      @Override
      public void install(Module module) {
        if (module instanceof BootstrapAwareModule) {
          ((BootstrapAwareModule) module).setBootstrap(getBootstrap());
        }

        super.install(module);
      }
    };
  }
}
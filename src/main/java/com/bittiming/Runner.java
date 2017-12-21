package com.bittiming;

import io.vertx.core.Vertx;
import io.vertx.core.impl.resolver.DnsResolverProvider;

public class Runner {

    static {
        System.setProperty(DnsResolverProvider.DISABLE_DNS_RESOLVER_PROP_NAME, "true");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(LauncherVerticle.class.getName());
    }
}

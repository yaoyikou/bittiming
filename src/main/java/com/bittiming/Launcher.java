package com.bittiming;

import io.vertx.core.impl.resolver.DnsResolverProvider;

public class Launcher extends io.vertx.core.Launcher {

    static {
        System.setProperty(DnsResolverProvider.DISABLE_DNS_RESOLVER_PROP_NAME, "true");
    }

    public static void main(String[] args) {
        new Launcher().dispatch(args);
    }
}

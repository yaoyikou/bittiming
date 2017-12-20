package com.bittiming.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class WebServerVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {

        Router router = Router.router(vertx);
        router.route().handler(StaticHandler.create().setDefaultContentEncoding("UTF-8"));

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        PermittedOptions outBoundPermitOptions = new PermittedOptions().setAddressRegex("client\\..+");
        BridgeOptions options = new BridgeOptions().addOutboundPermitted(outBoundPermitOptions);
        sockJSHandler.bridge(options);
        router.route("/eb/*").handler(sockJSHandler);

        vertx.createHttpServer().requestHandler(router::accept).listen(80, ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }
}

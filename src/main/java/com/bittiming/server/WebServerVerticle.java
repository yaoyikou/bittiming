package com.bittiming.server;

import com.bittiming.utils.CacheVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class WebServerVerticle extends CacheVerticle {

    @Override
    public void start(Future<Void> startFuture) {

        Router router = Router.router(vertx);
        router.route().handler(StaticHandler.create().setDefaultContentEncoding("UTF-8"));

        router.get().handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
            ctx.next();
        });

        router.get("/topics").handler(ctx -> {
            ctx.response().end(new JsonArray(cache().keySet().stream().collect(Collectors.toList())).toString());
        });

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        PermittedOptions outBoundPermitOptions = new PermittedOptions().setAddressRegex("client\\..+");
        BridgeOptions options = new BridgeOptions().addOutboundPermitted(outBoundPermitOptions);
        sockJSHandler.bridge(options, be -> {
            if (be.type() == BridgeEventType.REGISTER) {
                String address = be.getRawMessage().getString("address");
                JsonObject json = cache().get(address);

                be.socket().write(new JsonObject().put("type", "rec").put("address", address).put("body", json == null ? new JsonObject() : json).toString());
            }

            be.complete(true);
        });

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

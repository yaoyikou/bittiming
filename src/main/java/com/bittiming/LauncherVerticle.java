package com.bittiming;

import com.bittiming.client.WebSocketClientVerticle;
import com.bittiming.server.WebServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class LauncherVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        Future.<String>future(future -> vertx.deployVerticle(WebSocketClientVerticle.class.getName(), future))
                .compose(id -> Future.<String>future(future -> vertx.deployVerticle(WebServerVerticle.class.getName(), future)))
                .setHandler(ar -> {
                    if (ar.succeeded()) {
                        startFuture.complete();
                    } else {
                        startFuture.fail(ar.cause());
                    }
                });
    }
}

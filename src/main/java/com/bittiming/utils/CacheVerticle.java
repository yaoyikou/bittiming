package com.bittiming.utils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;

public class CacheVerticle extends AbstractVerticle {

    private LocalMap<String, JsonObject> cache;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        cache = vertx.sharedData().getLocalMap("1b5f718ae55b11e780c19a214cf093ae");
    }

    public LocalMap<String, JsonObject> cache() {
        return cache;
    }
}

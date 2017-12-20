package com.bittiming.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import com.bittiming.utils.Methods;

public class WebSocketClientVerticle extends AbstractVerticle {

	private HttpClient client;

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		client = vertx.createHttpClient(new HttpClientOptions());
		webSocketPull();

		super.start(startFuture);
	}

	public void webSocketPull() {
		client.websocket(new RequestOptions().setHost("api.huobi.pro").setURI("/ws").setSsl(true).setPort(443), wss -> {

			System.out.println("connected");

			wss.writeFinalTextFrame(new JsonObject().put("sub", "market.btcusdt.depth.percent10")
					.put("id", "depth" + System.currentTimeMillis()).toString());


			wss.closeHandler(v -> {
				System.out.println("disconnected");
				vertx.setTimer(10000, l -> {
					webSocketPull();
				});
			});

			wss.binaryMessageHandler(buffer -> {
				String message = Methods.uncompress(buffer.getBytes());

				JsonObject json = new JsonObject(message);

				Long ping = json.getLong("ping");

				if (ping != null) {
					System.out.println("ping: " + ping);
					String pong = new JsonObject().put("pong", ping).toString();
					wss.writeFinalTextFrame(pong);
					return;
				}

				vertx.eventBus().publish("client.test", json);
			});
		});
	}
}

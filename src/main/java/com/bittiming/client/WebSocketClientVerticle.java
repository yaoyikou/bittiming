package com.bittiming.client;

import com.bittiming.utils.CacheVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import com.bittiming.utils.Methods;

import java.util.UUID;

public class WebSocketClientVerticle extends CacheVerticle {

	private HttpClient client;

	public static final String DETAIL_TOPIC_FORMAT = "market.%s.detail";
	public static final String[] DETAIL_SYMBOLS = {"btcusdt", "ethusdt", "ltcusdt", "etcusdt", "bccusdt", "ethbtc", "ltcbtc", "etcbtc", "bccbtc"};

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		client = vertx.createHttpClient(new HttpClientOptions());
		webSocketPull();

		super.start(startFuture);
	}

	public void webSocketPull() {
		client.websocket(new RequestOptions().setHost("api.huobi.pro").setURI("/ws").setSsl(true).setPort(443), wss -> {

			System.out.println("connected");

			for (String symbol: DETAIL_SYMBOLS) {
				String detailTopic = String.format(DETAIL_TOPIC_FORMAT, symbol);
				wss.writeFinalTextFrame(new JsonObject().put("sub", detailTopic)
						.put("id", UUID.randomUUID().toString().replace("-", "")).toString());
			}


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


				String ch = json.getString("ch");
				System.out.println(ch);
				if (ch != null) {
					String key = "client." + ch;

					vertx.eventBus().publish(key, json);

					cache().put(key, json);
				}
			});
		});
	}
}

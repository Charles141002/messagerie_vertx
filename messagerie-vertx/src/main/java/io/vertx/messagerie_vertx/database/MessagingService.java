package io.vertx.messagerie_vertx.database;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;

@ProxyGen
@VertxGen
public interface MessagingService {

    static MessagingService create(JDBCClient dbClient) {
        return new MessagingServiceImpl(dbClient);
    }

    static MessagingService createProxy(Vertx vertx, String address) {
        return new io.vertx.serviceproxy.ServiceProxyBuilder(vertx)
                .setAddress(address)
                .build(MessagingService.class);
    }

    void getLastMessages(Handler<AsyncResult<List<JsonObject>>> resultHandler);

    void addMessage(JsonObject message, Handler<AsyncResult<Void>> resultHandler);
}

package io.vertx.messagerie_vertx.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;

public class MessagingServiceImpl implements MessagingService {

    private final JDBCClient dbClient;

    public MessagingServiceImpl(JDBCClient dbClient) {
        this.dbClient = dbClient;
    }

    @Override
    public void getLastMessages(Handler<AsyncResult<List<JsonObject>>> resultHandler) {
        String sql = "SELECT * FROM messages ORDER BY timestamp DESC LIMIT 20";
        dbClient.query(sql, res -> {
            if (res.succeeded()) {
                List<JsonObject> messages = res.result().getRows();
                resultHandler.handle(Future.succeededFuture(messages));
            } else {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    @Override
    public void addMessage(JsonObject message, Handler<AsyncResult<Void>> resultHandler) {
        String sql = "INSERT INTO messages (user_name, content, timestamp) VALUES (?, ?, ?)";
        JsonArray params = new JsonArray()
                .add(message.getString("user_name"))
                .add(message.getString("content"))
                .add(System.currentTimeMillis());

        dbClient.updateWithParams(sql, params, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }
}

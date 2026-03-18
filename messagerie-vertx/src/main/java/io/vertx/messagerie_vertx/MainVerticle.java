package io.vertx.messagerie_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new io.vertx.messagerie_vertx.database.DatabaseVerticle(), res -> {
      if (res.succeeded()) {
        vertx.createHttpServer().requestHandler(req -> {
          req.response()
              .putHeader("content-type", "text/plain")
              .end("Hello from Vert.x with Database!");
        }).listen(8888).onComplete(http -> {
          if (http.succeeded()) {
            startPromise.complete();
            System.out.println("HTTP server started on port 8888");
          } else {
            startPromise.fail(http.cause());
          }
        });
      } else {
        startPromise.fail(res.cause());
      }
    });
  }
}

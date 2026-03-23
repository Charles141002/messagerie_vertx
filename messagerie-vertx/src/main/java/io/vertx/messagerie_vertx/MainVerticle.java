package io.vertx.messagerie_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new io.vertx.messagerie_vertx.database.DatabaseVerticle(), res -> {
      if (res.succeeded()) {
        vertx.deployVerticle(new io.vertx.messagerie_vertx.http.HttpServerVerticle(), http -> {
          if (http.succeeded()) {
            startPromise.complete();
            System.out.println("Application started successfully");
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

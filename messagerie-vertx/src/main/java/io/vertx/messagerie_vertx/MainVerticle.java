package io.vertx.messagerie_vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.messagerie_vertx.database.DatabaseVerticle;
import io.vertx.messagerie_vertx.http.HttpServerVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new DatabaseVerticle(), res -> {
      if (res.succeeded()) {
        vertx.deployVerticle(new HttpServerVerticle(), http -> {
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

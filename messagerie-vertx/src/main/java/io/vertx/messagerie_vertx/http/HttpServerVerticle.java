where javapackage io.vertx.messagerie_vertx.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.messagerie_vertx.database.MessagingService;

public class HttpServerVerticle extends AbstractVerticle {

    private MessagingService messagingService;

    @Override
    public void start(Promise<Void> startPromise) {
        messagingService = MessagingService.createProxy(vertx, "database-service-address");

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        // Body handler for POST requests
        router.route().handler(BodyHandler.create());

        // Configure SockJS bridge for real-time updates
        SockJSHandlerOptions sockJSOptions = new SockJSHandlerOptions().setHeartbeatInterval(1000);
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx, sockJSOptions);
        
        SockJSBridgeOptions bridgeOptions = new SockJSBridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("chat.updates"))
                .addOutboundPermitted(new PermittedOptions().setAddress("chat.updates"));
        
        sockJSHandler.bridge(bridgeOptions);
        router.route("/chat/*").handler(sockJSHandler);

        // API Routes
        router.get("/api/messages").handler(this::getMessages);
        router.post("/api/messages").handler(this::addMessage);

        // Static files handler
        router.route("/*").handler(StaticHandler.create().setCachingEnabled(false));

        server.requestHandler(router).listen(8888, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8888");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    private void getMessages(RoutingContext context) {
        messagingService.getLastMessages(res -> {
            if (res.succeeded()) {
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(new JsonArray(res.result()).encode());
            } else {
                context.fail(res.cause());
            }
        });
    }

    private void addMessage(RoutingContext context) {
        JsonObject message = context.getBodyAsJson();
        if (message == null || !message.containsKey("user_name") || !message.containsKey("content")) {
            context.response().setStatusCode(400).end("Invalid message format");
            return;
        }

        messagingService.addMessage(message, res -> {
            if (res.succeeded()) {
                // Publish update to event bus for real-time (Step 4 preview)
                vertx.eventBus().publish("chat.updates", message);
                
                context.response()
                        .setStatusCode(201)
                        .putHeader("content-type", "application/json")
                        .end(message.encode());
            } else {
                context.fail(res.cause());
            }
        });
    }
}

package org.code13k.helios.service.pub;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.code13k.helios.business.message.MessageSender;
import org.code13k.helios.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubHttpServer extends AbstractVerticle {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(PubHttpServer.class);

    // Const
    public static final int PORT = AppConfig.getInstance().getPort().getPubHttp();

    /**
     * start()
     */
    @Override
    public void start() throws Exception {
        mLogger.trace("start()");

        // Init
        HttpServerOptions httpServerOptions = new HttpServerOptions();
        httpServerOptions.setCompressionSupported(true);
        httpServerOptions.setPort(PORT);
        httpServerOptions.setIdleTimeout(5); // seconds
        HttpServer httpServer = vertx.createHttpServer(httpServerOptions);

        // Routing
        Router router = Router.router(vertx);
        setRouter(router);

        // Listen
        httpServer.requestHandler(router::accept).listen();

        // End
        logging(httpServerOptions, router);
    }

    /**
     * Logging
     */
    private void logging(HttpServerOptions httpServerOptions, Router router) {
        synchronized (mLogger) {
            // Begin
            mLogger.info("------------------------------------------------------------------------");
            mLogger.info("API HTTP Server");
            mLogger.info("------------------------------------------------------------------------");

            // Vert.x
            mLogger.info("Vert.x clustered = " + getVertx().isClustered());
            mLogger.info("Vert.x deployment ID = " + deploymentID());

            // Http Server Options
            mLogger.info("Port = " + httpServerOptions.getPort());
            mLogger.info("Idle timeout (second) = " + httpServerOptions.getIdleTimeout());
            mLogger.info("Compression supported = " + httpServerOptions.isCompressionSupported());
            mLogger.info("Compression level = " + httpServerOptions.getCompressionLevel());

            // Route
            router.getRoutes().forEach(r -> {
                mLogger.info("Routing path = " + r.getPath());
            });

            // End
            mLogger.info("------------------------------------------------------------------------");
        }
    }


    /**
     * Set app router
     */
    private void setRouter(Router router) {
        // POST /pub/:topic
        router.route().method(HttpMethod.POST).path("/pub/:topic").handler(routingContext -> {
            routingContext.request().setExpectMultipart(true);
            routingContext.request().bodyHandler(new Handler<Buffer>() {
                @Override
                public void handle(Buffer event) {
                    final String topic = routingContext.request().getParam("topic");
                    final String body = event.toString();

                    if(StringUtils.isEmpty(topic)==true){
                        response(routingContext, 400, "Bad Request (Invalid Topic)");
                        return;
                    }
                    if(StringUtils.isEmpty(body)==true){
                        response(routingContext, 400, "Bad Request (Invalid Body)");
                        return;
                    }
                    MessageSender.getInstance().sendMessageToTopic(topic, body);
                    response(routingContext, 200, "OK");
                }
            });
        });

        // TODO It will be deleted when migration is finished
        // Temporary API for migration
        // GET,POST /api/v1/message/pub?topic={TOPIC}&message={메세지}
        router.route().method(HttpMethod.GET).path("/api/v1/message/pub").handler(routingContext -> {
            routingContext.request().endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                    String topic = routingContext.request().getParam("topic");
                    String message = routingContext.request().getParam("message");
                    if (StringUtils.isNotEmpty(topic) && StringUtils.isNotEmpty(message)) {
                        response(routingContext, 400, "Bad Request");
                    } else {
                        MessageSender.getInstance().sendMessageToTopic(topic, message);
                        response(routingContext, 200, "OK");
                    }
                }
            });
        });
        router.route().method(HttpMethod.POST).path("/api/v1/message/pub").handler(routingContext -> {
            routingContext.request().setExpectMultipart(true);
            routingContext.request().endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                    MultiMap form = routingContext.request().formAttributes();
                    String topic = form.get("topic");
                    String message = form.get("message");
                    if (StringUtils.isNotEmpty(topic) && StringUtils.isNotEmpty(message)) {
                        response(routingContext, 400, "Bad Request");
                    } else {
                        MessageSender.getInstance().sendMessageToTopic(topic, message);
                        response(routingContext, 200, "OK");
                    }
                }
            });
        });
    }

    /**
     * Response HTTP error status
     */
    private void response(RoutingContext routingContext, int statusCode, String message) {
        HttpServerResponse response = routingContext.response();
        response.putHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
        response.setStatusCode(statusCode);
        response.setStatusMessage(message);
        response.end(message);
        response.close();
    }
}
package org.code13k.helios.service.api;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.code13k.helios.config.AppConfig;
import org.code13k.helios.service.api.controller.AppAPI;
import org.code13k.helios.service.api.controller.TopicAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiHttpServer extends AbstractVerticle {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(ApiHttpServer.class);

    // Const
    public static final int PORT = AppConfig.getInstance().getPort().getApiHttp();

    // Data
    private AppAPI mAppAPI = new AppAPI();
    private TopicAPI mTopicAPI = new TopicAPI();

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
        setAppRouter(router);
        setTopicRouter(router);

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
    private void setAppRouter(Router router) {
        // GET /app/env
        router.route().method(HttpMethod.GET).path("/app/env").handler(routingContext -> {
            routingContext.request().endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                    responseHttpOK(routingContext, mAppAPI.env());
                }
            });
        });
        // GET /app/status
        router.route().method(HttpMethod.GET).path("/app/status").handler(routingContext -> {
            routingContext.request().endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                    responseHttpOK(routingContext, mAppAPI.status());
                }
            });
        });
        // GET /app/hello
        router.route().method(HttpMethod.GET).path("/app/hello").handler(routingContext -> {
            routingContext.request().endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                    responseHttpOK(routingContext, mAppAPI.hello());
                }
            });
        });
        // GET /app/ping
        router.route().method(HttpMethod.GET).path("/app/ping").handler(routingContext -> {
            routingContext.request().endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                    responseHttpOK(routingContext, mAppAPI.ping());
                }
            });
        });
    }

    /**
     * Set topic router
     */
    private void setTopicRouter(Router router) {
        // GET /topic/count
        router.route().method(HttpMethod.GET).path("/topic/count").handler(routingContext -> {
            routingContext.request().endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                    responseHttpOK(routingContext, mTopicAPI.count());
                }
            });
        });
        // GET /topic/all
        router.route().method(HttpMethod.GET).path("/topic/all").handler(routingContext -> {
            routingContext.request().endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                    responseHttpOK(routingContext, mTopicAPI.all());
                }
            });
        });
        // GET /topic/search?keyword={KEYWORD}
        router.route().method(HttpMethod.GET).path("/topic/search").handler(routingContext -> {
            routingContext.request().endHandler(new Handler<Void>() {
                @Override
                public void handle(Void event) {
                    String keyword = routingContext.request().getParam("keyword");
                    if (StringUtils.isNotEmpty(keyword)) {
                        responseHttpOK(routingContext, mTopicAPI.search(keyword));
                    } else {
                        responseHttpError(routingContext, 400, "Bad Request (Invalid Keyword)");
                    }
                }
            });
        });
    }

    /**
     * Response HTTP 200 OK
     */
    private void responseHttpOK(RoutingContext routingContext, String message) {
        HttpServerResponse response = routingContext.response();
        response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        response.setStatusCode(200);
        response.setStatusMessage("OK");
        response.end(message);
        response.close();
    }

    /**
     * Response HTTP error status
     */
    private void responseHttpError(RoutingContext routingContext, int statusCode, String message) {
        HttpServerResponse response = routingContext.response();
        response.putHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
        response.setStatusCode(statusCode);
        response.setStatusMessage(message);
        response.end(message);
        response.close();
    }
}
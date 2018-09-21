package org.code13k.helios.service.pub;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.code13k.helios.business.message.MessageSender;
import org.code13k.helios.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PubWsServer extends AbstractVerticle {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(PubWsServer.class);

    // Server Port
    public static final int PORT = AppConfig.getInstance().getPort().getPubWs();
    private static final Pattern PATH_PATTERN = Pattern.compile("/pub/(\\w.+)");

    /**
     * start()
     */
    @Override
    public void start() throws Exception {
        super.start();
        mLogger.info("start()");

        /**
         * Http Server
         */
        HttpServerOptions options = new HttpServerOptions();
        options.setCompressionSupported(true);
        HttpServer httpServer = vertx.createHttpServer(options);

        /**
         * Listen WebSocket
         */
        httpServer.websocketHandler(new Handler<ServerWebSocket>() {
            @Override
            public void handle(final ServerWebSocket ws) {
                Matcher matcher = null;

                // /pub/{TOPIC}
                matcher = PATH_PATTERN.matcher(ws.path());
                if (matcher.matches() == true) {
                    final String topic = matcher.group(1);
                    if (StringUtils.isEmpty(topic) == false) {
                        handleMessagePub(ws, topic);
                        return;
                    }
                }

                // Nothing
                ws.reject();
            }
        }).listen(PORT);
    }

    /**
     * handleMessagePub()
     */
    protected void handleMessagePub(final ServerWebSocket ws, final String topic) {
        ws.frameHandler(new Handler<WebSocketFrame>() {
            @Override
            public void handle(WebSocketFrame event) {
                String frameMessage = event.textData();
                mLogger.trace("handle : " + frameMessage);
                MessageSender.getInstance().sendMessageToTopic(topic, frameMessage);
            }
        });

        ws.drainHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                mLogger.trace("drainHandler()");
            }
        });

        ws.closeHandler(new Handler<Void>() {
            @Override
            public void handle(final Void event) {
                mLogger.trace("closeHandler()");
            }
        });

        ws.endHandler(new Handler<Void>() {
            @Override
            public void handle(Void event) {
                mLogger.trace("endHandler()");
            }
        });

        ws.exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable event) {
                mLogger.trace("exceptionHandler()", event);
            }
        });
    }
}
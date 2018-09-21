package org.code13k.helios.service.sub;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class SubWsServerHandler extends SimpleChannelInboundHandler<Object> {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(SubWsServerHandler.class);

    // Path
    private static final String ROOT_PATH = "/sub";

    /**
     * Constructor
     */
    SubWsServerHandler() {

    }

    /**
     * channelRead0()
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * channelReadComplete()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * exceptionCaught()
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        mLogger.error("exceptionCaught()", cause);
    }

    /**
     * channelActive()
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * channelInactive()
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SubWsServer.getInstance().onDisconnected(ctx.channel());
        super.channelInactive(ctx);
    }

    /**
     * userEventTriggered()
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                ctx.writeAndFlush(new PingWebSocketFrame());
                mLogger.trace("userEventTriggered() :: READER_IDLE :: sendMessageToChannel ping");
            } else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.fireChannelInactive();
                ctx.close();
            }
        }
    }

    /**
     * handleHttpRequest()
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        String requestUri = req.uri();

        // Log
        if (mLogger.isTraceEnabled() == true) {
            mLogger.trace("------------------------------------------------------------------------");
            mLogger.trace("Request Headers");
            mLogger.trace("------------------------------------------------------------------------");
            mLogger.trace("URL # " + req.uri());
            mLogger.trace("CHANNEL # " + ctx.channel().id());
            req.headers().forEach(header -> mLogger.debug(header.getKey() + "=" + header.getValue()));
            mLogger.trace("------------------------------------------------------------------------");
        }

        // Handle a bad request.
        if (!req.decoderResult().isSuccess()) {
            responseHttpError(ctx, req, BAD_REQUEST);
            mLogger.debug("FAILED :: Handle a bad request :: " + requestUri);
            return;
        }

        // Allow only GET methods.
        if (req.method() != GET) {
            responseHttpError(ctx, req, FORBIDDEN);
            mLogger.debug("FAILED :: Allow only GET methods :: " + requestUri);
            return;
        }

        // Not Supported Favicon
        if ("/favicon.ico".equals(requestUri)) {
            responseHttpError(ctx, req, NOT_FOUND);
            mLogger.debug("FAILED :: Not Supported Favicon :: " + requestUri);
            return;
        }

        // Check valid url
        int pathIndex = StringUtils.indexOf(requestUri, ROOT_PATH);
        if (pathIndex != 0) {
            responseHttpError(ctx, req, NOT_FOUND);
            mLogger.debug("FAILED :: Invalid url :: " + requestUri);
            return;
        }

        // Handshake For WebSocket
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, true);
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            mLogger.error("sendUnsupportedVersionResponse()");
        } else {
            ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
            handshakeFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    mLogger.trace("WebSocket handshake future : " + future);

                    if (true == future.isSuccess()) {

                        // Connected
                        boolean connected = SubWsServer.getInstance().onConnected(ctx.channel());
                        if (connected == false) {
                            String channelId = ctx.channel().id() + "";
                            mLogger.error("Disallow connect : " + channelId);
                            ctx.close();
                            return;
                        }
                    } else {
                        mLogger.error("WebSocket handshake is not succeeded : " + future);
                    }
                }
            });
        }
    }

    /**
     * handleWebSocketFrame()
     */
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            ctx.close();
            mLogger.trace("WebSocket Closed : " + ctx.channel());
            return;
        }

        // Check for ping frame
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            mLogger.trace("WebSocket Ping : " + ctx.channel());
            return;
        }

        // Check for pong frame
        if (frame instanceof PongWebSocketFrame) {
            mLogger.trace("WebSocket Pong : " + ctx.channel());
            return;
        }

        // Check for text frame
        if (frame instanceof TextWebSocketFrame) {
            boolean succeeded = false;
            String receivedMessage = ((TextWebSocketFrame) frame).text();
            if (StringUtils.isBlank(receivedMessage) == false) {
                succeeded = SubWsServer.getInstance().onMessage(ctx.channel(), receivedMessage);
            }
            if (succeeded == false) {
                String channelId = ctx.channel().id() + "";
                mLogger.error("Not Supported Message Received : " + channelId + ", " + receivedMessage);
                ctx.close();
            }
            return;
        }

        // Not supported frame
        throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
    }

    /**
     * responseHttpError()
     */
    private void responseHttpError(ChannelHandlerContext ctx, FullHttpRequest req, HttpResponseStatus status) {
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, status);
        res.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
        ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
        res.content().writeBytes(buf);
        buf.release();
        HttpUtil.setContentLength(res, res.content().readableBytes());

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        f.addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * getWebSocketLocation()
     */
    private String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + ROOT_PATH;
        return "ws://" + location;
    }
}
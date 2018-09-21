package org.code13k.helios.service.sub;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubWsServerInitializer extends ChannelInitializer<SocketChannel> {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(SubWsServerInitializer.class);

    // Channel Timeout In Seconds
    private static final int TIMEOUT = 60;

    /**
     * Construct
     */
    public SubWsServerInitializer(){

    }

    /**
     * initChannel()
     */
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new IdleStateHandler(TIMEOUT - 10, TIMEOUT, 0));
        pipeline.addLast(new SubWsServerHandler());
    }
}
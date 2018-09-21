package org.code13k.helios.service.sub;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.StringUtils;
import org.code13k.helios.app.Const;
import org.code13k.helios.business.ChannelManager;
import org.code13k.helios.business.message.MessageSender;
import org.code13k.helios.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class SubWsServer {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(SubWsServer.class);

    // Port
    public static final int PORT = AppConfig.getInstance().getPort().getSubWs();


    /**
     * Singleton
     */
    private static class SingletonHolder {
        static final SubWsServer INSTANCE = new SubWsServer();
    }

    public static SubWsServer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Constructor
     */
    private SubWsServer() {
        mLogger.trace("MessageServer()");
    }

    /**
     * Run
     */
    public void run() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup();
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    // WebSocket
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .childHandler(new SubWsServerInitializer());
                    // Port
                    Channel channel = bootstrap.bind(PORT).sync().channel();
                    mLogger.info("Run, SubWsServer");
                    channel.closeFuture().sync();
                } catch (Exception e) {
                    mLogger.error("Exception, SubWsServer", e);
                } finally {
                    mLogger.info("Finally, SubWsServer");
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        });
        thread.setName("SubWsServer");
        thread.start();
    }

    /**
     * onConnected()
     */
    public boolean onConnected(Channel channel) {
        if (channel == null) {
            return false;
        }
        ChannelManager.getInstance().opened(channel);
        return true;
    }

    /**
     * onMessage()
     */
    public boolean onMessage(Channel channel, String message) {
        if (channel != null && StringUtils.isNotEmpty(message)) {
            mLogger.debug("MESSAGE : " + message);
            String[] tempArray = StringUtils.split(message, " ");
            if (tempArray != null && tempArray.length > 0) {
                String command = tempArray[0];
                ArrayList<String> params = new ArrayList();
                if (tempArray.length > 1) {
                    for (int k = 1; k < tempArray.length; k++) {
                        params.add(tempArray[k]);
                    }
                }

                /**
                 * Command 처리
                 */
                mLogger.debug("COMMAND=" + command);
                mLogger.debug("PARAMS=" + params);
                if (StringUtils.isNotEmpty(command)) {
                    // SUB
                    if (command.equalsIgnoreCase(Const.Command.Request.SUB)) {
                        int paramsSize = params.size();
                        if (paramsSize > 0) {
                            for (int i = 0; i < paramsSize; i++) {
                                String topic = params.get(i);
                                topic = StringUtils.trim(topic);
                                ChannelManager.getInstance().add(channel, topic);
                            }
                            MessageSender.getInstance().sendMessageToChannel(channel, Const.Command.Response.OK);
                        }
                    }
                    // UNSUB
                    else if (command.equalsIgnoreCase(Const.Command.Request.UNSUB)) {
                        int paramsSize = params.size();
                        if (paramsSize > 0) {
                            for (int i = 0; i < paramsSize; i++) {
                                String topic = params.get(i);
                                topic = StringUtils.trim(topic);
                                ChannelManager.getInstance().remove(channel, topic);
                            }
                            MessageSender.getInstance().sendMessageToChannel(channel, Const.Command.Response.OK);
                        }
                    }
                    // DISCONNECT
                    else if (command.equalsIgnoreCase(Const.Command.Request.DISCONNECT)) {
                        channel.close();
                    }
                    // PING
                    else if (command.equalsIgnoreCase(Const.Command.Request.PING)) {
                        MessageSender.getInstance().sendMessageToChannel(channel, Const.Command.Response.PONG);
                    }
                    // PONG
                    else if (command.equalsIgnoreCase(Const.Command.Request.PONG)) {
                        // Nothing
                    }
                    // [Not Supported]
                    else {
                        channel.close();
                    }
                }
                return true;
            }
        }
        return true;
    }

    /**
     * onDisconnected()
     */
    public boolean onDisconnected(Channel channel) {
        ChannelManager.getInstance().closed(channel);
        return true;
    }

}
package org.code13k.helios.business.message;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.code13k.helios.app.Env;
import org.code13k.helios.business.ChannelManager;
import org.code13k.helios.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageSender {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(MessageSender.class);

    // Data
    private MessageQueue mMessageQueue = null;

    /**
     * Singleton
     */
    private static class SingletonHolder {
        static final MessageSender INSTANCE = new MessageSender();
    }

    public static MessageSender getInstance() {
        return MessageSender.SingletonHolder.INSTANCE;
    }

    /**
     * Constructor
     */
    private MessageSender() {
        mLogger.info("MessageSender()");
        init();
    }

    /**
     * Initialize
     */
    synchronized public void init() {
        if (mMessageQueue == null) {
            mMessageQueue = new MessageQueue();
            runMessageSender();
        } else {
            mLogger.info("Duplicated initializing");
        }
    }

    /**
     * Message count in queue
     */
    public int messageCountInQueue(){
        return mMessageQueue.size();
    }

    /**
     * Send message to topic
     */
    public boolean sendMessageToTopic(String topic, String message) {
        // 예외처리
        if (StringUtils.isBlank(topic) == true) {
            return false;
        }
        if (StringUtils.isBlank(message) == true) {
            return false;
        }

        // Add message to queue
        Message messageObject = new Message();
        messageObject.setTopic(topic);
        messageObject.setMessage(message);
        mMessageQueue.add(messageObject);
        return true;
    }

    /**
     * Send message to channel
     */
    public boolean sendMessageToChannel(Channel channel, String message) {
        // 예외처리
        if (StringUtils.isBlank(message) == true) {
            return false;
        }
        if (channel == null) {
            return false;
        }

        // 메세지 전송
        if (channel != null && channel.isOpen() && channel.isWritable()) {
            try {
                channel.writeAndFlush(new TextWebSocketFrame(message));
            } catch (Exception e) {
                // Nothing
            }
            return true;
        }
        return false;
    }

    /**
     * Run message sender
     */
    private void runMessageSender() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message sendingMessage = null;

                    try {
                        // Start to handle message
                        sendingMessage = mMessageQueue.startToHandle();

                        // Process message
                        if (sendingMessage != null) {
                            ChannelGroup channelGroup = ChannelManager.getInstance().getChannelGroup(sendingMessage.getTopic());
                            if (channelGroup != null) {
                                try {
                                    TextWebSocketFrame messageFrame = new TextWebSocketFrame(sendingMessage.getMessage());
                                    channelGroup.writeAndFlush(messageFrame);
                                } catch (Exception e) {
                                    mLogger.error("Error occurred", e);
                                }
                            }
                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                // Nothing
                            }
                        }
                    } catch (Exception e) {
                        mLogger.error("Error occurred", e);
                    } finally {
                        // End to handle message
                        if (sendingMessage != null) {
                            mMessageQueue.endToHandle(sendingMessage);
                        }
                    }
                }
            }
        };
        int count = Env.getInstance().getProcessorCount();
        for (int i = 0; i < count; i++) {
            Thread thread = new Thread(runnable);
            thread.setName("helios-message-sender-" + i);
            thread.start();
        }
    }
}

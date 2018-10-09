package org.code13k.helios.business.message;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.apache.commons.lang3.StringUtils;
import org.code13k.helios.app.Cluster;
import org.code13k.helios.model.TopicMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusteredMessage {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(ClusteredMessage.class);

    // Const
    private static final String TOPIC = "Code13k-Helios-Clustered-Message-Topic";

    // Data
    private ITopic<TopicMessage> mTopic = null;

    /**
     * Singleton
     */
    private static class SingletonHolder {
        static final ClusteredMessage INSTANCE = new ClusteredMessage();
    }

    public static ClusteredMessage getInstance() {
        return ClusteredMessage.SingletonHolder.INSTANCE;
    }

    /**
     * Constructor
     */
    private ClusteredMessage() {
        mLogger.info("ClusteredMessage()");
    }

    /**
     * Initialize
     */
    synchronized public void init() {
        if (mTopic == null) {
            mTopic = Cluster.getInstance().getHazelcastInstance().getTopic(TOPIC);
            mTopic.addMessageListener(new MessageListener<TopicMessage>() {
                @Override
                public void onMessage(Message<TopicMessage> message) {
                    mLogger.trace("onMessage() : " + message.getMessageObject());
                    TopicMessage topicMessage = message.getMessageObject();
                    MessageSender.getInstance().sendMessage(topicMessage);
                }
            });
        } else {
            mLogger.info("Duplicated initializing");
        }
    }

    /**
     * Send message to topic
     */
    public void sendMessage(TopicMessage topicMessage) {
        mTopic.publish(topicMessage);
    }

    public void sendMessageToTopic(String topic, String message) {
        // Exception
        if (StringUtils.isBlank(topic) == true) {
            return;
        }
        if (StringUtils.isBlank(message) == true) {
            return;
        }

        // Send
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setMessage(message);
        mTopic.publish(topicMessage);
    }
}

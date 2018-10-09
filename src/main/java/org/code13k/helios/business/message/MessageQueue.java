package org.code13k.helios.business.message;


import org.code13k.helios.model.TopicMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;

public class MessageQueue {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(MessageQueue.class);

    // Message Queue
    private final LinkedList<TopicMessage> mQueue = new LinkedList<>();
    private final HashSet<String> mHandlingTopic = new HashSet<>();

    /**
     * Constructor
     */
    public MessageQueue() {
        mLogger.info("MessageQueue()");
    }

    /**
     * Size
     */
    public int size(){
        return mQueue.size();
    }

    /**
     * Add message to queue
     */
    synchronized public void add(TopicMessage topicMessage) {
        try {
            mQueue.add(topicMessage);
            mLogger.trace("add() : " + topicMessage);
        } catch (Exception e) {
            String msg = "Failed to add() : " + topicMessage;
            mLogger.error(msg, e);
        }
    }

    /**
     * Start to handle message
     */
    synchronized public TopicMessage startToHandle() {
        try {
            int size = mQueue.size();
            for (int index = 0; index < size; index++) {
                TopicMessage topicMessage = mQueue.get(index);
                if (mHandlingTopic.contains(topicMessage.getTopic()) == false) {
                    mHandlingTopic.add(topicMessage.getTopic());
                    mLogger.trace("startToHandle() : " + topicMessage);
                    return topicMessage;
                }
            }
            return null;
        } catch (Exception e) {
            String msg = "Failed to startToHandle()";
            mLogger.error(msg, e);
            return null;
        }
    }

    /**
     * End to handle message
     */
    synchronized public void endToHandle(TopicMessage topicMessage) {
        try {
            mQueue.remove(topicMessage);
            mHandlingTopic.remove(topicMessage.getTopic());
            mLogger.trace("endToHandle() : " + topicMessage);
        } catch (Exception e) {
            String msg = "Failed to endToHandle() : " + topicMessage;
            mLogger.error(msg, e);
        }
    }
}

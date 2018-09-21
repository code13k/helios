package org.code13k.helios.business.message;


import org.code13k.helios.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;

public class MessageQueue {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(MessageQueue.class);

    // Message Queue
    private final LinkedList<Message> mQueue = new LinkedList<>();
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
    synchronized public void add(Message message) {
        try {
            mQueue.add(message);
            mLogger.trace("add() : " + message);
        } catch (Exception e) {
            String msg = "Failed to add() : " + message;
            mLogger.error(msg, e);
        }
    }

    /**
     * Start to handle message
     */
    synchronized public Message startToHandle() {
        try {
            int size = mQueue.size();
            for (int index = 0; index < size; index++) {
                Message message = mQueue.get(index);
                if (mHandlingTopic.contains(message.getTopic()) == false) {
                    mHandlingTopic.add(message.getTopic());
                    mLogger.trace("startToHandle() : " + message);
                    return message;
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
    synchronized public void endToHandle(Message message) {
        try {
            mQueue.remove(message);
            mHandlingTopic.remove(message.getTopic());
            mLogger.trace("endToHandle() : " + message);
        } catch (Exception e) {
            String msg = "Failed to endToHandle() : " + message;
            mLogger.error(msg, e);
        }
    }
}

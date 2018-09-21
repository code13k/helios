package org.code13k.helios.business;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.vertx.core.impl.ConcurrentHashSet;
import org.apache.commons.lang3.StringUtils;
import org.code13k.helios.app.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(ChannelManager.class);

    // Data
    private ConcurrentHashMap<String, ChannelGroup> mChannelGroupByTopic = null;

    /**
     * Singleton
     */
    private static class SingletonHolder {
        static final ChannelManager INSTANCE = new ChannelManager();
    }

    public static ChannelManager getInstance() {
        return ChannelManager.SingletonHolder.INSTANCE;
    }

    /**
     * Constructor
     */
    private ChannelManager() {
        mLogger.trace("ChannelManager()");
    }

    /**
     * Initialize
     */
    synchronized public void init() {
        if (mChannelGroupByTopic == null) {
            mChannelGroupByTopic = new ConcurrentHashMap<>();
            ChannelGroup allChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            mChannelGroupByTopic.put(Const.PrimitiveTopic.ALL, allChannelGroup);
        } else {
            mLogger.info("Already initialized");
        }
    }

    /**
     * Channel is opened
     */
    public void opened(Channel channel) {
        if (channel != null) {
            addChannelToChannelGroup(channel, Const.PrimitiveTopic.ALL);
            addTopicToChannelAttr(channel, Const.PrimitiveTopic.ALL);
        }
    }

    /**
     * Channel is closed
     */
    public void closed(Channel channel) {
        if (channel != null) {
            AttributeKey attrKey = AttributeKey.valueOf(Const.ChannelAttribute.KEY_TOPICS);
            if (true == channel.hasAttr(attrKey)) {
                ConcurrentHashSet<String> channelAttr = (ConcurrentHashSet<String>) channel.attr(attrKey).get();
                channelAttr.forEach(topic -> {
                    removeChannelFromChannelGroup(channel, topic);
                });
            }
        }
    }

    /**
     * Add channel to channel group and update channel attribute
     */
    public void add(Channel channel, String topic) {
        if (channel == null || StringUtils.isEmpty(topic) == true) {
            return;
        }
        addChannelToChannelGroup(channel, topic);
        addTopicToChannelAttr(channel, topic);
    }

    /**
     * Remove channel from channel group and update channel attribute
     */
    public void remove(Channel channel, String topic) {
        if (channel == null || StringUtils.isEmpty(topic) == true) {
            return;
        }
        removeChannelFromChannelGroup(channel, topic);
        removeTopicFromChannelAttr(channel, topic);
    }

    /**
     * Get channel group
     */
    public ChannelGroup getChannelGroup(String topic) {
        return mChannelGroupByTopic.get(topic);
    }

    /**
     * Get topic list
     */
    public List<String> getTopicList(){
        if (mChannelGroupByTopic != null) {
            Enumeration<String> keys = mChannelGroupByTopic.keys();
            List<String> keyList = Collections.list(keys);
            return keyList;
        }
        return null;
    }

    /**
     * Find topic list with keyword
     */
    public List<String> findTopicListWithKeyword(String keyword){
        ArrayList<String> result = new ArrayList();
        if (StringUtils.isNotEmpty(keyword)) {
            try {
                List<String> topicList = getTopicList();
                for (String topic : topicList) {
                    if (topic.contains(keyword)) {
                        result.add(topic);
                    }
                }
            } catch (Exception e) {
                // Nothing
            }
        }
        return result;
    }

    /**
     * Get topic count
     */
    public int getTopicCount(){
        return mChannelGroupByTopic.size();
    }

    /**
     * Get channel count
     */
    public int getChannelCount(String topic){
        ChannelGroup channelGroup = getChannelGroup(topic);
        if(channelGroup!=null){
            return channelGroup.size();
        }
        return 0;
    }

    /**
     * Get channel count
     */
    public int getChannelCount(){
        return getChannelCount(Const.PrimitiveTopic.ALL);
    }

    /**
     * Add channel to channel group
     */
    private void addChannelToChannelGroup(Channel channel, String topic) {
        ChannelGroup channelGroup = mChannelGroupByTopic.get(topic);
        if (channelGroup == null) {
            channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            mChannelGroupByTopic.put(topic, channelGroup);
        }
        channelGroup.add(channel);
    }

    /**
     * Remove channel from channel group
     */
    private void removeChannelFromChannelGroup(Channel channel, String topic) {
        ChannelGroup channelGroup = mChannelGroupByTopic.get(topic);
        if (channelGroup == null) {
            return;
        }
        channelGroup.remove(channel);
    }

    /**
     * Add topic to channel attribute
     */
    private void addTopicToChannelAttr(Channel channel, String topic) {
        AttributeKey attrKey = AttributeKey.valueOf(Const.ChannelAttribute.KEY_TOPICS);
        if (false == channel.hasAttr(attrKey)) {
            channel.attr(attrKey).set(new ConcurrentHashSet<String>());
        }
        ConcurrentHashSet<String> channelAttr = (ConcurrentHashSet<String>) channel.attr(attrKey).get();
        channelAttr.add(topic);
    }

    /**
     * Remove topic from channel attribute
     */
    private void removeTopicFromChannelAttr(Channel channel, String topic) {
        AttributeKey attrKey = AttributeKey.valueOf(Const.ChannelAttribute.KEY_TOPICS);
        if (false == channel.hasAttr(attrKey)) {
            return;
        }
        ConcurrentHashSet<String> channelAttr = (ConcurrentHashSet<String>) channel.attr(attrKey).get();
        channelAttr.remove(topic);
    }
}

















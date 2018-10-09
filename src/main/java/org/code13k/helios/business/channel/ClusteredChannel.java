package org.code13k.helios.business.channel;

import com.hazelcast.core.*;
import io.netty.channel.group.ChannelGroup;
import org.apache.commons.lang3.StringUtils;
import org.code13k.helios.app.Cluster;
import org.code13k.helios.app.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ClusteredChannel {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(ClusteredChannel.class);

    // Const
    private static final String DATA = "Code13k-Helios-Clustered-Channel-Data";

    // Data
    private Member mLocalMember = null;
    private IMap<String, Object> mData = null;
    private ConcurrentHashMap<String, Integer> mChannelCountByTopic = null;

    /**
     * Singleton
     */
    private static class SingletonHolder {
        static final ClusteredChannel INSTANCE = new ClusteredChannel();
    }

    public static ClusteredChannel getInstance() {
        return ClusteredChannel.SingletonHolder.INSTANCE;
    }

    /**
     * Constructor
     */
    private ClusteredChannel() {
        mLogger.info("ClusteredChannel()");
    }

    /**
     * Initialize
     */
    synchronized public void init() {
        if (mLocalMember == null) {
            mLocalMember = Cluster.getInstance().getHazelcastInstance().getCluster().getLocalMember();
            mData = Cluster.getInstance().getHazelcastInstance().getMap(DATA);
            mChannelCountByTopic = new ConcurrentHashMap<>();
            runSyncData();
        } else {
            mLogger.info("Duplicated initializing");
        }
    }

    /**
     * Get topic list
     */
    public List<String> getTopicList() {
        Enumeration<String> keys = mChannelCountByTopic.keys();
        List<String> keyList = Collections.list(keys);
        return keyList;
    }

    /**
     * Find topic list with keyword
     */
    public List<String> findTopicListWithKeyword(String keyword) {
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
    public int getTopicCount() {
        return mChannelCountByTopic.size();
    }

    /**
     * Get channel count
     */
    public int getChannelCount(String topic) {
        Integer value = mChannelCountByTopic.get(topic);
        if (value == null) {
            return 0;
        }
        return value;
    }

    /**
     * Get channel count
     */
    public int getChannelCount() {
        return getChannelCount(Const.PrimitiveTopic.ALL);
    }

    /**
     * Put data for sync
     */
    private void putData() {
        final Map<String, Integer> data = new HashMap<>();
        final List<String> topicList = ChannelManager.getInstance().getTopicList();
        if (topicList != null) {
            topicList.forEach(topic -> {
                ChannelGroup channelGroup = ChannelManager.getInstance().getChannelGroup(topic);
                if (channelGroup == null) {
                    data.put(topic, 0);
                } else {
                    data.put(topic, channelGroup.size());
                }
            });
        }
        mData.putAsync(mLocalMember.getUuid(), data, 30, TimeUnit.SECONDS);
    }

    /**
     * Get data for sync
     */
    private void getData() {
        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        Set<Member> memberSet = Cluster.getInstance().getHazelcastInstance().getCluster().getMembers();
        memberSet.forEach(member -> {
            final Map<String, Integer> data = (Map<String, Integer>) mData.get(member.getUuid());
            if (data != null) {
                data.forEach((topic, count) -> {
                    Integer presentValue = result.get(topic);
                    if (presentValue == null) {
                        result.put(topic, count);
                    } else {
                        result.put(topic, presentValue + count);
                    }
                });
            }
        });
        mChannelCountByTopic = result;
    }

    /**
     * Run sync data
     */
    private void runSyncData() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        putData();
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                            // Nothing
                        }
                        getData();
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                            // Nothing
                        }
                    } catch (Exception e) {
                        mLogger.error("Error occurred", e);
                    } finally {
                        // Nothing
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.setName("helios-sync-clustered-channel-data");
        thread.start();
    }
}

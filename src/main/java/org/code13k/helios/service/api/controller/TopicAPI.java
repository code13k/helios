package org.code13k.helios.service.api.controller;

import org.code13k.helios.business.channel.ClusteredChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TopicAPI extends BasicAPI {
    /**
     * Get topic count
     */
    public String count() {
        int count = ClusteredChannel.getInstance().getTopicCount();
        return toResultJsonString(count);
    }

    /**
     * Get all topics
     */
    public String all(){
        List<String> list = ClusteredChannel.getInstance().getTopicList();
        return toResultJsonString(addInfoWithTopicList(list));
    }

    /**
     * Find topics with keyword
     */
    public String search(String keyword){
        List<String> list = ClusteredChannel.getInstance().findTopicListWithKeyword(keyword);
        return toResultJsonString(addInfoWithTopicList(list));
    }

    /**
     * Add additional info to topic list
     */
    private List addInfoWithTopicList(List<String> topicList) {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        if (topicList != null && topicList.size() > 0) {
            topicList.forEach(topic -> {
                int channelCount = ClusteredChannel.getInstance().getChannelCount(topic);
                HashMap<String, Object> resultItem = new HashMap<>();
                resultItem.put("topic", topic);
                resultItem.put("channelCount", channelCount);
                resultList.add(resultItem);
            });
        }
        return resultList;
    }
}

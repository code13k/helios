package org.code13k.helios.app;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.*;
import com.hazelcast.nio.Address;

import org.code13k.helios.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Cluster {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(Cluster.class);

    // Data
    private HazelcastInstance mHazelcastInstance = null;

    /**
     * Singleton
     */
    private static class SingletonHolder {
        static final Cluster INSTANCE = new Cluster();
    }

    public static Cluster getInstance() {
        return Cluster.SingletonHolder.INSTANCE;
    }

    /**
     * Constructor
     */
    private Cluster() {
        mLogger.trace("Cluster()");
    }

    /**
     * Initialize
     */
    public void init() {
        ArrayList<String> nodes = AppConfig.getInstance().getCluster().getNodes();
        Config config = new Config();

        // Init
        NetworkConfig networkConfig = config.getNetworkConfig();
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        if (nodes != null && nodes.size() > 0) {

            // Port
            networkConfig.setPort(AppConfig.getInstance().getCluster().getPort());

            // TcpIpConfig
            TcpIpConfig tcpIpConfig = joinConfig.getTcpIpConfig();
            tcpIpConfig.setMembers(nodes);
            tcpIpConfig.setRequiredMember(null);
            tcpIpConfig.setEnabled(true);
        }

        // Instance
        mHazelcastInstance = Hazelcast.newHazelcastInstance(config);
        mHazelcastInstance.getCluster().addMembershipListener(new MembershipListener() {
            @Override
            public void memberAdded(MembershipEvent membershipEvent) {
                mLogger.info("Cluster # Member added : " + membershipEvent.toString());
            }

            @Override
            public void memberRemoved(MembershipEvent membershipEvent) {
                mLogger.error("Cluster # Member removed : " + membershipEvent.toString());
            }

            @Override
            public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
                mLogger.trace("Cluster # Member attribute changed : " + memberAttributeEvent.toString());
            }
        });
    }

    /**
     * Get Hazelcast Instance
     */
    public HazelcastInstance getHazelcastInstance() {
        return mHazelcastInstance;
    }


    /**
     * Get all values
     */
    public Map<String, Object> values() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("version", getClusterVersion());
        result.put("count", getMemberCount());
        result.put("info", getMemberInfo());

        return result;
    }

    /**
     * Get cluster version
     */
    public String getClusterVersion(){
        return mHazelcastInstance.getCluster().getClusterVersion().toString();
    }

    /**
     * Get clustered member count
     */
    public int getMemberCount() {
        try {
            return mHazelcastInstance.getCluster().getMembers().size();
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Get clustered member info
     */
    public List<Map<String, String>> getMemberInfo(){
        ArrayList<Map<String, String>> result = new ArrayList<>();
        mHazelcastInstance.getCluster().getMembers().forEach(member -> {
            HashMap<String, String> item = new HashMap<>();
            Address memberAddress = member.getAddress();
            item.put("version", member.getVersion().toString());
            item.put("uuid", member.getUuid());
            item.put("address", memberAddress.getHost()+":"+memberAddress.getPort());
            result.add(item);
        });
        return result;
    }

}

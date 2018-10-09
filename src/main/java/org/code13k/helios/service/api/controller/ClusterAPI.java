package org.code13k.helios.service.api.controller;

import org.code13k.helios.app.Cluster;

public class ClusterAPI extends BasicAPI {
    /**
     * Status
     */
    public String status() {
        return toResultJsonString(Cluster.getInstance().values());
    }
}

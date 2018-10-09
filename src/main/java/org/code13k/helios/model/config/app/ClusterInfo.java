package org.code13k.helios.model.config.app;

import org.code13k.helios.model.BasicModel;

import java.util.ArrayList;

public class ClusterInfo extends BasicModel {
    private int port;
    private ArrayList<String> nodes;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ArrayList<String> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<String> nodes) {
        this.nodes = nodes;
    }
}

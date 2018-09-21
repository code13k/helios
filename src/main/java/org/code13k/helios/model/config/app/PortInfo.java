package org.code13k.helios.model.config.app;

import org.code13k.helios.model.BasicModel;

public class PortInfo extends BasicModel {
    private int subWs;
    private int pubWs;
    private int pubHttp;
    private int apiHttp;

    public int getSubWs() {
        return subWs;
    }

    public void setSubWs(int subWs) {
        this.subWs = subWs;
    }

    public int getPubWs() {
        return pubWs;
    }

    public void setPubWs(int pubWs) {
        this.pubWs = pubWs;
    }

    public int getPubHttp() {
        return pubHttp;
    }

    public void setPubHttp(int pubHttp) {
        this.pubHttp = pubHttp;
    }

    public int getApiHttp() {
        return apiHttp;
    }

    public void setApiHttp(int apiHttp) {
        this.apiHttp = apiHttp;
    }
}

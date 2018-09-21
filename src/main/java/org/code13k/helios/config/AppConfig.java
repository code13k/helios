package org.code13k.helios.config;

import org.code13k.helios.lib.Util;
import org.code13k.helios.model.config.app.PortInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;

public class AppConfig extends BasicConfig {
    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(AppConfig.class);

    // Data
    private PortInfo mPortInfo = new PortInfo();

    /**
     * Singleton
     */
    private static class SingletonHolder {
        static final AppConfig INSTANCE = new AppConfig();
    }

    public static AppConfig getInstance() {
        return AppConfig.SingletonHolder.INSTANCE;
    }

    /**
     * Constructor
     */
    private AppConfig() {
        mLogger.trace("AppConfig()");
    }

    @Override
    protected String getDefaultConfigFilename() {
        return "default_app_config.yml";
    }

    @Override
    protected String getConfigFilename() {
        return "app_config.yaml";
    }

    @Override
    protected boolean loadConfig(final String content, final String filePath) {
        try {
            Yaml yaml = new Yaml();
            LinkedHashMap yamlObject = yaml.load(content);
            mLogger.trace("yamlObject class name = " + yamlObject.getClass().getName());
            mLogger.trace("yamlObject = " + yamlObject);

            // PortInfo
            LinkedHashMap portObject = (LinkedHashMap) yamlObject.get("port");
            mLogger.trace("portObject class name = " + portObject.getClass().getName());
            mLogger.trace("portObject = " + portObject);
            Integer portSubWs = (Integer) portObject.get("sub_ws");
            if (Util.isValidPortNumber(portSubWs) == false) {
                mLogger.error("Invalid sub_ws of port : " + portSubWs);
                return false;
            }
            Integer portPubWs = (Integer) portObject.get("pub_ws");
            if (Util.isValidPortNumber(portPubWs) == false) {
                mLogger.error("Invalid pub_ws of port : " + portPubWs);
                return false;
            }
            Integer portPubHttp = (Integer) portObject.get("pub_http");
            if (Util.isValidPortNumber(portPubHttp) == false) {
                mLogger.error("Invalid pub_http of port : " + portPubHttp);
                return false;
            }
            Integer portApiHttp = (Integer) portObject.get("api_http");
            if (Util.isValidPortNumber(portApiHttp) == false) {
                mLogger.error("Invalid api_http of port : " + portApiHttp);
                return false;
            }
            mPortInfo.setSubWs(portSubWs);
            mPortInfo.setPubWs(portPubWs);
            mPortInfo.setPubHttp(portPubHttp);
            mPortInfo.setApiHttp(portApiHttp);
        } catch (Exception e) {
            mLogger.error("Failed to load config file", e);
            return false;
        }
        return true;
    }

    @Override
    public void logging() {
        // Begin
        mLogger.info("------------------------------------------------------------------------");
        mLogger.info("Application Configuration");
        mLogger.info("------------------------------------------------------------------------");

        // Config File Path
        mLogger.info("Config file path = " + getConfigFilename());

        // PortInfo
        mLogger.info("sub_ws of PortInfo = " + mPortInfo.getSubWs());
        mLogger.info("pub_ws of PortInfo = " + mPortInfo.getPubWs());
        mLogger.info("pub_http of PortInfo = " + mPortInfo.getPubHttp());
        mLogger.info("api_http of PortInfo = " + mPortInfo.getApiHttp());

        // End
        mLogger.info("------------------------------------------------------------------------");
    }

    /**
     * Get port
     */
    public PortInfo getPort() {
        return mPortInfo;
    }
}

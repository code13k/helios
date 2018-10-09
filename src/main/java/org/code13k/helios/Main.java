package org.code13k.helios;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.code13k.helios.app.Cluster;
import org.code13k.helios.business.channel.ChannelManager;
import org.code13k.helios.business.channel.ClusteredChannel;
import org.code13k.helios.business.message.ClusteredMessage;
import org.code13k.helios.business.message.MessageSender;
import org.code13k.helios.config.AppConfig;
import org.code13k.helios.config.LogConfig;
import org.code13k.helios.app.Env;
import org.code13k.helios.app.Status;
import org.code13k.helios.service.api.ApiHttpServer;
import org.code13k.helios.service.pub.PubHttpServer;
import org.code13k.helios.service.pub.PubWsServer;
import org.code13k.helios.service.sub.SubWsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    /**
     * This is a exceptional code for logging.
     * It depends on LogConfig class.
     * If you modified it, you must modify LogConfig class.
     *
     * @see org.code13k.helios.config.LogConfig
     */
    static {
        System.setProperty("logback.configurationFile", "config/logback.xml");
    }

    // Logger
    private static final Logger mLogger = LoggerFactory.getLogger(Main.class);

    /**
     * Main
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        // Logs
        mLogger.trace("This is TRACE Log!");
        mLogger.debug("This is DEBUG Log!");
        mLogger.info("This is INFO Log!");
        mLogger.warn("This is WARN Log!");
        mLogger.error("This is ERROR Log!");

        // Arguments
        if (args != null) {
            int argsLength = args.length;
            if (argsLength > 0) {
                mLogger.info("------------------------------------------------------------------------");
                mLogger.info("Arguments");
                mLogger.info("------------------------------------------------------------------------");
                for (int i = 0; i < argsLength; i++) {
                    mLogger.info("Args " + i + " = " + args[i]);
                }
                mLogger.info("------------------------------------------------------------------------");

            }
        }

        // System Properties
        mLogger.debug("------------------------------------------------------------------------");
        mLogger.debug("System Property");
        mLogger.debug("------------------------------------------------------------------------");
        System.getProperties().forEach((key, value) -> {
            mLogger.debug(key + " = " + value);
        });
        mLogger.debug("------------------------------------------------------------------------");

        // Initialize
        try {
            LogConfig.getInstance().init();
            AppConfig.getInstance().init();
            Env.getInstance().init();
            Status.getInstance().init();
            Cluster.getInstance().init();
            ChannelManager.getInstance().init();
            MessageSender.getInstance().init();
            ClusteredMessage.getInstance().init();
            ClusteredChannel.getInstance().init();
        } catch (Exception e) {
            mLogger.error("Failed to initialize", e);
            System.exit(1);
        }

        // Deploy SubWsServer
        try {
            SubWsServer.getInstance().run();
            Thread.sleep(1000);
        } catch (Exception e) {
            mLogger.error("Failed to deploy SubWsServer", e);
            System.exit(2);
        }

        // Deploy PubWsServer
        try {
            DeploymentOptions options = new DeploymentOptions();
            options.setInstances(Math.max(1, Env.getInstance().getProcessorCount() / 2));
            Vertx.vertx().deployVerticle(PubWsServer.class.getName(), options);
            Thread.sleep(1000);
        } catch (Exception e) {
            mLogger.error("Failed to deploy PubWsServer", e);
            System.exit(3);
        }

        // Deploy PubHttpServer
        try {
            DeploymentOptions options = new DeploymentOptions();
            options.setInstances(Math.max(1, Env.getInstance().getProcessorCount() / 2));
            Vertx.vertx().deployVerticle(PubHttpServer.class.getName(), options);
            Thread.sleep(1000);
        } catch (Exception e) {
            mLogger.error("Failed to deploy PubHttpServer", e);
            System.exit(4);
        }

        // Deploy APIHttpServer
        try {
            DeploymentOptions options = new DeploymentOptions();
            options.setInstances(Math.max(1, Env.getInstance().getProcessorCount() / 2));
            Vertx.vertx().deployVerticle(ApiHttpServer.class.getName(), options);
            Thread.sleep(1000);
        } catch (Exception e) {
            mLogger.error("Failed to deploy ApiHttpServer", e);
            System.exit(5);
        }

        // End
        mLogger.info("Running application is successful.");
    }
}

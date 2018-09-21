package org.code13k.helios.app;

/**
 * Constant
 */
public class Const {
    /**
     * COMMAND
     */
    public class Command {
        public class Request {
            public static final String SUB = "SUB";
            public static final String UNSUB = "UNSUB";
            public static final String DISCONNECT = "DISCONNECT";
            public static final String PING = "PING";
            public static final String PONG = "PONG";
        }

        public class Response {
            public static final String PONG = "PONG";
            public static final String OK = "OK";
        }
    }

    /**
     * Channel Attribute
     */
    public class ChannelAttribute {
        public static final String KEY_TOPICS = "CHANNEL_ATTRIBUTE_KEY_TOPICS";
    }

    /**
     * Primitive Topic
     */
    public class PrimitiveTopic {
        public static final String ALL = "primitive.topic.all";
    }
}
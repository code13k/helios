package org.code13k.helios.lib;


public class Util {

    /**
     * Check if the given number is valid port number.
     */
    public static boolean isValidPortNumber(Integer portNumber) {
        if (portNumber == null || portNumber < 1 || portNumber > 65535) {
            return false;
        }
        return true;
    }
}

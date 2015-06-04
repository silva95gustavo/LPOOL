package com.lpool.client.Network;

/**
 * Created by André on 03/06/2015.
 */
public class Utilities {

    public static final int MAX_PORT = 65535;

    public static boolean isValidIP(String ip)
    {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if(ip.endsWith(".")) {
                return false;
            }

            return true;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    public static Boolean isValidPort(int port) {
        if(port < MAX_PORT && port > 0) return true;
        return false;
    }
}

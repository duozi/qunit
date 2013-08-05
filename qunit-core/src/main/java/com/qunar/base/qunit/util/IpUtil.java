package com.qunar.base.qunit.util;

import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IpUtil {
    static private final char COLON = ':';

    public static String getLocalNetworkAddress() {
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                String address = getAddressForNic(ni);
                if (StringUtils.isNotBlank(address)) return address;
            }
            return StringUtils.EMPTY;
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    private static String getAddressForNic(NetworkInterface nic) {
        Enumeration<InetAddress> addresses = nic.getInetAddresses();
        while (addresses.hasMoreElements()) {
            InetAddress ip = addresses.nextElement();
            if (!ip.isLoopbackAddress() && !isIPv6(ip)) {
                return ip.getHostAddress();
            }
        }
        return StringUtils.EMPTY;
    }

    private static boolean isIPv6(InetAddress ip) {
        return ip.getHostAddress().indexOf(COLON) != -1;
    }
}

package ehealth.group1.backend.helper.wlan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Component
public class WlanConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConfigurableServletWebServerFactory webServerFactory;
    private final ServerProperties serverProperties;

    public WlanConnector(ConfigurableServletWebServerFactory webServerFactory, ServerProperties serverProperties) {
        this.webServerFactory = webServerFactory;
        this.serverProperties = serverProperties;
    }

    public void changeAddressToWlan() {
        InetAddress wlanIp = getWlanIpAddress();

        if(wlanIp != null) {
            LOGGER.info("Wlan address found: " + getWlanIpAddress());
        } else {
            LOGGER.info("Could not find valid wlan address!");
            return;
        }

        try {
            serverProperties.setAddress(wlanIp);
            LOGGER.info("Address changed successfully to " + wlanIp);
        } catch (Exception e) {
            e.printStackTrace();

            try {
                webServerFactory.setAddress(wlanIp);
                LOGGER.info("Address changed successfully to " + wlanIp);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private InetAddress getWlanIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while(networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();

                if(ni.isUp() && ni.getName().startsWith("wlan")) {
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();

                    while(addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();

                        if(!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                            LOGGER.info("InetAddress: " + address.toString());
                            return address;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }
}

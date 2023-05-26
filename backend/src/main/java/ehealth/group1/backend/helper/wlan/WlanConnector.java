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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Scanner;

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
        ArrayList<InetAddress> wlanIps = getWlanIpAddress();

        if(wlanIps != null && wlanIps.size() != 0) {
            StringBuilder addresses = new StringBuilder();

            for(InetAddress a : wlanIps) {
                addresses.append(a.getHostAddress()).append("\n");
            }

            LOGGER.info("Wlan addresses found: " + addresses);
        } else {
            LOGGER.info("Could not find valid wlan address!");
            return;
        }

        InetAddress wlanIp = null;

        Scanner sc = new Scanner(System.in);

        while(wlanIp == null) {
            System.out.println();
            System.out.println("Choose the wlan ip to host the server on:\n");

            for(int i = 0; i < wlanIps.size(); i++) {
                System.out.println("\t" + (i+1) + ". " + wlanIps.get(i).getHostAddress());
            }

            System.out.println();
            System.out.print(":> ");

            int userInput = sc.nextInt();

            if(userInput > 0 && userInput <= wlanIps.size()) {
                wlanIp = wlanIps.get(userInput-1);
            }
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
                return;
            }
        }

        serverProperties.setPort(8080);

        LOGGER.info("New server address: " + serverProperties.getAddress() + ":" + serverProperties.getPort());
    }

    private ArrayList<InetAddress> getWlanIpAddress() {
        ArrayList<InetAddress> result = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while(networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();

                if(ni.isUp() && ni.getName().startsWith("wlan")) {
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();

                    while(addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();

                        if(!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                            result.add(address);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return result;
    }
}

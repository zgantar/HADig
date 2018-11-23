package org.gantar.hadig;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.ResourceBundle;

import static org.gantar.hadig.Util.executeHTTPGETRequest;

public class Main {

    public static void main(String[] args) {
        ResourceBundle resources;
        IPConnection ipcon;

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            System.out.println("Current IP is: " + socket.getLocalAddress().toString());
            if (StringUtils.substring(socket.getLocalAddress().toString(), 9, 10).equals("1")) {
                resources = ResourceBundle.getBundle("org.gantar.hadig.elements");
            } else {
                resources = ResourceBundle.getBundle("org.gantar.hadig.elements_test");
            }

//            executeHTTPGETRequest(resources.getString("reset_counter"));

            ipcon = new IPConnection(); // Create IP connection
            ipcon.connect(resources.getString("HOST"), new Integer(resources.getString("PORT")));

            HashMap<String, HashMap<String, Device>> devices = Util.getDevices(ipcon);

            Util.startDiagnose(devices, resources, ipcon);
        } catch (AlreadyConnectedException | NetworkException e) {
            System.out.println("Napaka pri začenjanju diagnoze");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Napaka pri iskanju lokalnega IP naslova");
            e.printStackTrace();
        }

    }
}

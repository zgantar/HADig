package org.gantar.hadig.tinkerforge;

import com.tinkerforge.*;
import org.gantar.hadig.DiagnosticInterface;
import org.gantar.hadig.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by zgantar on 4. 05. 2017.
 */
public class IOSubroutine implements DiagnosticInterface {

    @Override
    public void diagnose(HashMap<String, HashMap<String, Device>> deviceTree, ResourceBundle resources, IPConnection ipcon) {
        HashMap<String, Device> devices = deviceTree.get(BrickletIO4.DEVICE_DISPLAY_NAME);
        String[] io4s = new String[0];

        try {
            io4s = resources.getString("IO4").split("_");
        } catch (MissingResourceException e) {
            System.out.println("Napaka pri iskanju nastavitev: " + BrickletIO4.DEVICE_DISPLAY_NAME + " ali " + BrickletIO16.DEVICE_DISPLAY_NAME + "!");
            e.printStackTrace();
        }

        HashMap<String, Boolean> deviceVisits = new HashMap<>();
        for (String uid : io4s) {
            deviceVisits.put(uid, false);
        }

        try {
            if (devices != null && devices.size() > 0) {
//                System.out.println("Preverjam število IO4, ki so odgovoril");
                int i = io4s.length;
                for (Device device : devices.values()) {
                    for (String io4UID : io4s) {
                        if (device.getIdentity().uid.equals(io4UID)) {
//                            System.out.println("Našel IO4 " + device.getIdentity().uid);
                            deviceVisits.put(io4UID, true);
                            i--;
                            break;
                        }
                    }
                }

                if (i != 0) {
                    System.out.println("!!!!!!Število zapisov v nastavitvah ne ustreza številu IO4!!!!");
                    for (Map.Entry entry : deviceVisits.entrySet()) {
                        if (!(boolean) entry.getValue()) {
                            if (!Util.resetTinkerforge(resources, "/")) {
                                return;
                            }
                        }
                    }
                    //tukaj potrebno dodat reset elektrike!!!!!
                    return;
                }


                if (devices.size() > 0) {
                    for (Map.Entry<String, Device> entry : devices.entrySet()) {
                        BrickletIO4 device = (BrickletIO4) entry.getValue();
                        Device.Identity identity = device.getIdentity();

                        BrickletIO4.Configuration conf = device.getConfiguration();
                        if (conf.directionMask != Integer.parseInt(resources.getString("IO4_" + identity.uid))) {
                            System.out.println("!!!!!!Trenutne nastavitve niso pravilne za IO4 " + identity.uid + "!!!!");
                            //first the input side
                            String settingsInput = resources.getString("Conf_" + identity.uid + "_i");
                            if (!settingsInput.equals("")) {
                                String[] elements = settingsInput.split("_");
                                System.out.println("!!!!!!Nastavljam input");
                                device.setConfiguration(Short.parseShort(elements[0]), elements[1].toCharArray()[0], Boolean.parseBoolean(elements[2]));
                            }
                            //then the output side
                            String settingsOutput = resources.getString("Conf_" + identity.uid + "_o");
                            if (!settingsOutput.equals("")) {
                                String[] elements = settingsOutput.split("_");
                                System.out.println("!!!!!!Nastavljam output");
                                device.setConfiguration(Short.parseShort(elements[0]), elements[1].toCharArray()[0], Boolean.parseBoolean(elements[2]));
                            }
//                    device.setValue((short) Integer.parseInt(resources.getString("IO4_" + identity.uid)));
//                    Util.reset(ipcon, identity);
                        }
                    }
                }
            }

            devices = deviceTree.get(BrickletIO16.DEVICE_DISPLAY_NAME);
            String[] io16s = new String[0];

            try {
                io16s = resources.getString("IO16").split("_");
            } catch (MissingResourceException e) {
                System.out.println("Napaka pri iskanju nastavitev: " + BrickletIO4.DEVICE_DISPLAY_NAME + " ali " + BrickletIO16.DEVICE_DISPLAY_NAME + "!");
                e.printStackTrace();
            }

            deviceVisits = new HashMap<>();
            for (String uid : io16s) {
                deviceVisits.put(uid, false);
            }

            if (devices != null) {
//                        System.out.println("Preverjam število IO16, ki so odgovoril");
                int n = io16s.length;
                for (Device device : devices.values()) {
                    for (String io16UID : io16s) {
                        if (device.getIdentity().uid.equals(io16UID)) {
//                                    System.out.println("Našel IO16 " + device.getIdentity().uid);
                            deviceVisits.put(io16UID, true);
                            n--;
                            break;
                        }
                    }
                }

                if (n != 0) {
                    System.out.println("Število zapisov v nastavitvah ne ustreza številu IO16!!!!");
                    for (Map.Entry entry : deviceVisits.entrySet()) {
                        if (!(boolean) entry.getValue()) {
                            if (!Util.resetTinkerforge(resources, "/")) {
                                return;
                            }
                        }
                    }
                    //tukaj potrebno dodat reset elektrike!!!!!
                    return;
                }

                for (Map.Entry<String, Device> entry : devices.entrySet()) {
                    BrickletIO16 device = (BrickletIO16) entry.getValue();
                    Device.Identity identity = device.getIdentity();
                    String settingsInput, settingsOutput;
                    BrickletIO16.PortConfiguration conf;

                    conf = device.getPortConfiguration('A');
                    if (conf.directionMask != Integer.parseInt(resources.getString("IO16_" + identity.uid + "_a"))) {
                        System.out.println("!!!!!!Trenutne nastavitve niso pravilne za A port na IO16 " + identity.uid + "!!!!");
                        //first the input side
                        //port a
                        settingsInput = resources.getString("Conf_" + identity.uid + "_i_a");
                        if (!settingsInput.equals("")) {
                            String[] elements = settingsInput.split("_");
                            System.out.println("!!!!!!Nastavljam input a");
                            device.setPortConfiguration('a', Short.parseShort(elements[0]), elements[1].toCharArray()[0], Boolean.parseBoolean(elements[2]));
                        }
                        //then the output
                        //port a
                        settingsOutput = resources.getString("Conf_" + identity.uid + "_o_a");
                        if (!settingsOutput.equals("")) {
                            String[] elements = settingsOutput.split("_");
                            System.out.println("!!!!!!Nastavljam output a");
                            device.setPortConfiguration('a', Short.parseShort(elements[0]), elements[1].toCharArray()[0], Boolean.parseBoolean(elements[2]));
                        }
                    }
                    conf = device.getPortConfiguration('B');
                    if (conf.directionMask != Integer.parseInt(resources.getString("IO16_" + identity.uid + "_b"))) {
                        System.out.println("!!!!!!Trenutne nastavitve niso pravilne za B port na IO16 " + identity.uid + "!!!!");
                        //first the input side
                        //port b
                        settingsInput = resources.getString("Conf_" + identity.uid + "_i_b");
                        if (!settingsInput.equals("")) {
                            String[] elements = settingsInput.split("_");
                            System.out.println("!!!!!!Nastavljam input b");
                            device.setPortConfiguration('b', Short.parseShort(elements[0]), elements[1].toCharArray()[0], Boolean.parseBoolean(elements[2]));
                        }
                        //then the output
                        //port b
                        settingsOutput = resources.getString("Conf_" + identity.uid + "_o_b");
                        if (!settingsOutput.equals("")) {
                            String[] elements = settingsOutput.split("_");
                            System.out.println("!!!!!!Nastavljam output b");
                            device.setPortConfiguration('b', Short.parseShort(elements[0]), elements[1].toCharArray()[0], Boolean.parseBoolean(elements[2]));
                        }
                    }
                }
            } else {
                System.out.println("Nimamo seznama naprav!");
            }
        } catch (TimeoutException | NotConnectedException e) {
            System.out.println("Napaka pri diagnozi: " + BrickletIO4.DEVICE_DISPLAY_NAME + " ali " + BrickletIO16.DEVICE_DISPLAY_NAME + "!");
            e.printStackTrace();
        } catch (MissingResourceException e) {
            System.out.println("Napaka pri iskanju nastavitev: " + BrickletIO4.DEVICE_DISPLAY_NAME + " ali " + BrickletIO16.DEVICE_DISPLAY_NAME + "!");
            e.printStackTrace();
        }
    }

    public IOSubroutine() {
    }
}

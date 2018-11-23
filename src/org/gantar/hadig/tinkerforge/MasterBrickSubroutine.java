    package org.gantar.hadig.tinkerforge;

    import com.tinkerforge.*;
    import org.gantar.hadig.DiagnosticInterface;

    import java.util.HashMap;
    import java.util.Map;
    import java.util.ResourceBundle;


    /**
     * Created by zgantar on 4. 05. 2017.
     */
    public class MasterBrickSubroutine implements DiagnosticInterface {

        @Override
        public void diagnose(HashMap<String, HashMap<String, Device>> deviceTree, ResourceBundle resources, IPConnection ipcon) {
            HashMap<String, Device> devices = deviceTree.get(BrickMaster.DEVICE_DISPLAY_NAME);
            String[] masterBricks = resources.getString("BrickMaster").split("_");

            try {
                if (devices != null) {
//                    System.out.println("Preverjam število master brickov, ki so odgovorili");
                    int i = masterBricks.length;
                    for (Device device : devices.values()) {
                        for (String masterBrickUID : masterBricks) {
                            if (device.getIdentity().uid.equals(masterBrickUID)) {
//                                System.out.println("Našel master brick " + device.getIdentity().uid);
                                i--;
                                break;
                            }
                        }
                    }

                if (i != 0) {
                    System.out.println("Število zapisov v nastavitvah ne ustreza številu master brickov!!!!");
                    return;
                }

                for (Map.Entry<String, Device> entry : devices.entrySet()) {
                    BrickMaster device = (BrickMaster) entry.getValue();
//                    System.out.println("Preverjam master brick " + device.getIdentity().uid);

                    if (device.isStatusLEDEnabled()) {
                        System.out.println("Ugašam status LED");
                        device.disableStatusLED();
                    }
                    if (!device.isRS485Present()) {
                        System.out.println("Pri testiranju " + BrickMaster.DEVICE_DISPLAY_NAME + " " + device.getIdentity().uid + ", nisem zaznal RS485 razširitve!");
                        device.reset();
                    } else {
                        String deviceAddress = resources.getString("BrickMaster_" + device.getIdentity().uid + "_address");
//                        System.out.println("Preverjam RS485 nastavitve");
                        short address;
                        if (deviceAddress != null) {
                            address = Short.parseShort(deviceAddress);
                        } else {
                            System.out.println("Nismo dobil zapisa za BrickMaster " + device.getIdentity().uid);
                            return;
                        }
                        //preverjamo ali je master ali slave
                        if (address == 0) {
//                            System.out.println("Preverjam master master brick");
                            if (device.getRS485Address() != 0) {
                                    System.out.println("Master master brick nima nastavljen master naslov, nastavljam");
                                    device.setRS485Address((short) 0);
                                }
                                if (device.getRS485SlaveAddress((short) 1) == 0) {
                                    System.out.println("Master master brick nima nastavljenih slaveou");
                                    String[] slaves = resources.getString("BrickMaster_" + device.getIdentity().uid + "_slave").split("_");
                                    for (short j = 0; slaves.length - 1 == j; j++) {
                                        System.out.println("Nastavljam slave naslov " + slaves[j]);
                                        device.setRS485SlaveAddress(j, Short.parseShort(slaves[j]));
                                    }
                                }
                            } else {
//                                System.out.println("Preverjam slave master brick ");
                                if (device.getRS485Address() != Short.parseShort(deviceAddress)) {
                                    System.out.println("Nastavljam slave naslov " + deviceAddress);
                                    device.setRS485Address(Short.parseShort(deviceAddress));
                                }
                            }
                        }
                    }
                }
            } catch (NotConnectedException | TimeoutException e) {
                System.out.println("Prišlo do napake pri komuniciranju z BrisckMaster!!");
                e.printStackTrace();
            }
        }

        public MasterBrickSubroutine() {
        }
    }

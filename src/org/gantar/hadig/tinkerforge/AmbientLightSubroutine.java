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
public class AmbientLightSubroutine implements DiagnosticInterface {

    @Override
    public void diagnose(HashMap<String, HashMap<String, Device>> deviceTree, ResourceBundle resources, IPConnection ipcon) {
        HashMap<String, Device> devices = deviceTree.get(BrickletAmbientLight.DEVICE_DISPLAY_NAME);
        try {
            String[] ambBricks = resources.getString("BrickletAmbientLight").split("_");

            HashMap<String, Boolean> deviceVisits = new HashMap<>();
            for (String uid : ambBricks) {
                deviceVisits.put(uid, false);
            }

//            System.out.println("Preverjam število amb senzorjev, ki so odgovoril");
            int i = ambBricks.length;
            for (Device device : devices.values()) {
                for (String ambUID : ambBricks) {
                    if (device.getIdentity().uid.equals(ambUID)) {
//                        System.out.println("Našel amb " + device.getIdentity().uid);
                        deviceVisits.put(ambUID, true);
                        i--;
                        break;
                    }
                }
            }

//            if (i != 0) {
//                System.out.println("!!!!!!Število zapisov v nastavitvah ne ustreza številu amb senzorjev!!!!");
//                for (Map.Entry entry : deviceVisits.entrySet()) {
//                    if (!(boolean) entry.getValue()) {
//                        if (!Util.resetTinkerforge(resources, "/")) {
//                            return;
//                        }
//                    }
//                }
//                //tukaj potrebno dodat reset elektrike!!!!!
//                return;
//            }

            for (Map.Entry<String, Device> entry : devices.entrySet()) {
                BrickletAmbientLight device = (BrickletAmbientLight) entry.getValue();

//                System.out.println("Preverjam poročano svetlost");
                if (device.getIlluminance() < 0 || device.getIlluminance() > 10000) {
                    String parent = device.getIdentity().connectedUid;
                    System.out.println("Poročana svetlost je izven meja normale, resetiram Master brick - " + parent);
                    if (parent.equals(resources.getString("BrickletAmbientLight_" + device.getIdentity().uid))) {
                        System.out.println("Poročan in nastavljen UID očeta sta enaka, resetiram!!!!!!!!!!!!!!!!");
                        if (!Util.resetTinkerforge(resources, device.getIdentity().uid)) {
                            return;
                        }
                    } else {
                        System.out.println("Poročan in nastavljen UID očeta NISTA enaka, PREVERI!!!!!!!");
                    }
                }
            }
        } catch (TimeoutException | NotConnectedException e) {
            e.printStackTrace();
        } catch (MissingResourceException e) {
            System.out.println("Napaka pri iskanju nastavitev: " + BrickletAmbientLight.DEVICE_DISPLAY_NAME + "!");
            e.printStackTrace();
        }
    }

    public AmbientLightSubroutine() {
    }
}

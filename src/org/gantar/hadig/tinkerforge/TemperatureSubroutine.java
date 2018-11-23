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
public class TemperatureSubroutine implements DiagnosticInterface {

    public void diagnose(HashMap<String, HashMap<String, Device>> deviceTree, ResourceBundle resources, IPConnection ipcon) {
        HashMap<String, Device> devices = deviceTree.get(BrickletTemperature.DEVICE_DISPLAY_NAME);
        try {
            String[] tempBricks = resources.getString("BrickletTemperature").split("_");

            HashMap<String, Boolean> deviceVisits = new HashMap<>();
            for (String uid : tempBricks) {
                deviceVisits.put(uid, false);
            }

//            System.out.println("Preverjam število temp senzorjev, ki so odgovoril");
            int i = tempBricks.length;
            for (Device device : devices.values()) {
                for (String tempUID : tempBricks) {
                    if (device.getIdentity().uid.equals(tempUID)) {
//                        System.out.println("Našel temp " + device.getIdentity().uid);
                        deviceVisits.put(tempUID, true);
                        i--;
                        break;
                    }
                }
            }

            if (i != 0) {
                System.out.println("!!!!!!Število zapisov v nastavitvah ne ustreza številu temp senzorjev!!!!");
                for (Map.Entry entry : deviceVisits.entrySet()) {
                    if (!(boolean)entry.getValue()) {
                        if (!Util.resetTinkerforge(resources, "/")) {
                            return;
                        }
                    }
                }
                //tukaj potrebno dodat reset elektrike!!!!!
                return;
            }

            for (Map.Entry<String, Device> entry : devices.entrySet()) {
                BrickletTemperature device = (BrickletTemperature) entry.getValue();
//                System.out.println("Preverjam I2C nastavitev");
                if (device.getI2CMode() != BrickletTemperature.I2C_MODE_SLOW) {
                    System.out.println("Nastavljam I2C_MODE_SLOW");
                    device.setI2CMode(BrickletTemperature.I2C_MODE_SLOW);
                }
//                System.out.println("Preverjam poročano temperaturo");
                if (device.getTemperature() <= 1000 || device.getTemperature() >= 3500) {
                    String parent = device.getIdentity().connectedUid;
                    System.out.println("Poročana temperatura je izven meja normale, resetiram Master brick - " + parent);
                    if (parent.equals(resources.getString("BrickletTemperature_" + device.getIdentity().uid))) {
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
            System.out.println("Napaka pri preverjanju senzorja temperature!");
            e.printStackTrace();
        } catch (MissingResourceException e) {
            System.out.println("Napaka pri iskanju nastavitev: " + BrickletTemperature.DEVICE_DISPLAY_NAME + "!");
            e.printStackTrace();
        }
    }

    public TemperatureSubroutine() {
    }
}

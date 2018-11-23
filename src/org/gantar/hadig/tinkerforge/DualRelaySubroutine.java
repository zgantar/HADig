package org.gantar.hadig.tinkerforge;

import com.tinkerforge.BrickMaster;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import org.gantar.hadig.DiagnosticInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by zgantar on 4. 05. 2017.
 */
public class DualRelaySubroutine implements DiagnosticInterface {

    @Override
    public void diagnose(HashMap<String, HashMap<String, Device>> deviceTree, ResourceBundle resources, IPConnection ipcon) {
        HashMap<String, Device> devices = deviceTree.get(BrickletDualRelay.DEVICE_DISPLAY_NAME);
        HashMap<String, Device> parentDevices = deviceTree.get(BrickMaster.DEVICE_DISPLAY_NAME);

        for (Map.Entry<String, Device> entry : devices.entrySet()) {
            BrickletDualRelay device = (BrickletDualRelay) entry.getValue();
/**            try {

//            } catch (TimeoutException e) {
//                e.printStackTrace();
            } catch (NotConnectedException e) {
                e.printStackTrace();
            }
*/
        }
    }

    public DualRelaySubroutine() {
    }
}

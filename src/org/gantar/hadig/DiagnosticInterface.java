package org.gantar.hadig;

import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;

import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by zgantar on 4. 05. 2017.
 */
public interface DiagnosticInterface {
    void diagnose(HashMap<String, HashMap<String, Device>> deviceTree, ResourceBundle resources, IPConnection ipcon);
}

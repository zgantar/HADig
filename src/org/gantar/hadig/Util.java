package org.gantar.hadig;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tinkerforge.*;
import jdk.nashorn.internal.parser.JSONParser;

import org.gantar.hadig.tinkerforge.AmbientLightSubroutine;
import org.gantar.hadig.tinkerforge.HumiditySubroutine;
import org.gantar.hadig.tinkerforge.IOSubroutine;
import org.gantar.hadig.tinkerforge.MasterBrickSubroutine;
import org.gantar.hadig.tinkerforge.TemperatureSubroutine;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TimeZone;

//import org.reflections.Reflections;

/**
 * Created by zgantar on 5. 05. 2017.
 */
public class Util {

    static HashMap<String, HashMap<String, Device>> getDevices(IPConnection ipConnection) {
        HashMap<String, HashMap<String, Device>> result = new HashMap<>();

        ipConnection.addEnumerateListener((uid, connectedUid, position, hardwareVersion, firmwareVersion, deviceIdentifier, enumerationType) -> {
//            System.out.println("Dobili klic naprave - " + uid);
            switch (deviceIdentifier) {
                case BrickMaster.DEVICE_IDENTIFIER:
                    addBrickMaster(result, uid, ipConnection);
                    break;
                case BrickletIO4.DEVICE_IDENTIFIER:
                    addBrickletIO4(result, uid, ipConnection);
                    break;
                case BrickletIO16.DEVICE_IDENTIFIER:
                    addBrickletIO16(result, uid, ipConnection);
                    break;
                case BrickletTemperature.DEVICE_IDENTIFIER:
                    addBrickletTemperature(result, uid, ipConnection);
                    break;
                case BrickletAmbientLight.DEVICE_IDENTIFIER:
                    addBrickletAmbientLight(result, uid, ipConnection);
                    break;
                case BrickletHumidity.DEVICE_IDENTIFIER:
                    addBrickletHumidity(result, uid, ipConnection);
                    break;
                case BrickletDualRelay.DEVICE_IDENTIFIER:
                    addBrickletDualRelay(result, uid, ipConnection);
                    break;
                default:
                    System.out.println("Neki je narobe, ker te naprave pa še nimam v sistemu");
            }

            if (enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED) {
                System.out.println("");
            }
        });

        try {
            ipConnection.enumerate();
            Thread.sleep(1500);
        } catch (NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void addBrickletDualRelay(HashMap<String, HashMap<String, Device>> result, String uid, IPConnection ipConnection) {
        HashMap<String, Device> devices = result.computeIfAbsent(BrickletDualRelay.DEVICE_DISPLAY_NAME, k -> new HashMap<>());

        devices.put(uid, new BrickletDualRelay(uid, ipConnection));
    }

    private static void addBrickletHumidity(HashMap<String, HashMap<String, Device>> result, String uid, IPConnection ipConnection) {
        HashMap<String, Device> devices = result.computeIfAbsent(BrickletHumidity.DEVICE_DISPLAY_NAME, k -> new HashMap<>());

        devices.put(uid, new BrickletHumidity(uid, ipConnection));
    }

    private static void addBrickletAmbientLight(HashMap<String, HashMap<String, Device>> result, String uid, IPConnection ipConnection) {
        HashMap<String, Device> devices = result.computeIfAbsent(BrickletAmbientLight.DEVICE_DISPLAY_NAME, k -> new HashMap<>());

        devices.put(uid, new BrickletAmbientLight(uid, ipConnection));
    }

    private static void addBrickletTemperature(HashMap<String, HashMap<String, Device>> result, String uid, IPConnection ipConnection) {
        HashMap<String, Device> devices = result.computeIfAbsent(BrickletTemperature.DEVICE_DISPLAY_NAME, k -> new HashMap<>());

        devices.put(uid, new BrickletTemperature(uid, ipConnection));
    }

    private static void addBrickletIO16(HashMap<String, HashMap<String, Device>> result, String uid, IPConnection ipConnection) {
        HashMap<String, Device> devices = result.computeIfAbsent(BrickletIO16.DEVICE_DISPLAY_NAME, k -> new HashMap<>());

        devices.put(uid, new BrickletIO16(uid, ipConnection));
    }

    private static void addBrickletIO4(HashMap<String, HashMap<String, Device>> result, String uid, IPConnection ipConnection) {
        HashMap<String, Device> devices = result.computeIfAbsent(BrickletIO4.DEVICE_DISPLAY_NAME, k -> new HashMap<>());

        devices.put(uid, new BrickletIO4(uid, ipConnection));
    }

    private static void addBrickMaster(HashMap<String, HashMap<String, Device>> result, String uid, IPConnection ipConnection) {
        HashMap<String, Device> devices = result.computeIfAbsent(BrickMaster.DEVICE_DISPLAY_NAME, k -> new HashMap<>());

        devices.put(uid, new BrickMaster(uid, ipConnection));
    }

    static void startDiagnose(HashMap<String, HashMap<String, Device>> devices, ResourceBundle resources, IPConnection ipcon) {


        MasterBrickSubroutine masterBrickSubroutine = new MasterBrickSubroutine();
        masterBrickSubroutine.diagnose(devices, resources, ipcon);

        IOSubroutine ioSubroutine = new IOSubroutine();
        ioSubroutine.diagnose(devices, resources, ipcon);

        TemperatureSubroutine temperatureSubroutine = new TemperatureSubroutine();
        temperatureSubroutine.diagnose(devices, resources, ipcon);

        HumiditySubroutine humiditySubroutine = new HumiditySubroutine();
        humiditySubroutine.diagnose(devices, resources, ipcon);

        AmbientLightSubroutine ambientLightSubroutine = new AmbientLightSubroutine();
        ambientLightSubroutine.diagnose(devices, resources, ipcon);

//        DualRelaySubroutine dualRelaytSubroutine = new DualRelaySubroutine();
//        dualRelaytSubroutine.diagnose(devices, resources, ipcon);
    }


    private static String executeCommand(String command) {

        StringBuilder output = new StringBuilder();
        Process p;

        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine())!= null) {
                output.append(line).append("\n");
            }
            System.out.println(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public static String executeHTTPGETRequest(String command) {
        String result = new String();
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(command).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "text/plain");
            con.setDoOutput(false);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = in.readLine();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean executeHTTPPOSTRequest(String command, String state) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(command).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "text/plain");
            con.setDoOutput(true);

            String data =  state;
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(data);
            out.close();

            if (con.getResponseCode() == 200) {
                return true;
            } else return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean resetTinkerforge(ResourceBundle resources, String source) {
        System.out.println("Sem v RESET_TINKERFORGE metodi zaradi " + source);
        String returnString;
        System.out.println("Preverjam ali je že tretja zahteva zaporedoma");
        JsonElement jelementInt = new JsonParser().parse(executeHTTPGETRequest(resources.getString("reset_counter")));
        JsonObject jobjectInt = jelementInt.getAsJsonObject();
        int i = jobjectInt.get("state").getAsInt();
        if (i > 0) {
            JsonElement jelementDate = new JsonParser().parse(executeHTTPGETRequest(resources.getString("reset_counter")));
            JsonObject jobjectDate = jelementDate.getAsJsonObject();
            String date = jobjectDate.get("state").getAsString();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        System.out.println("Kličem stikalo za izklop elektrike Tinkerfog modulom - " + resources.getString("reset_switch"));
        if (!executeHTTPPOSTRequest(resources.getString("reset_switch"), "OFF")) {
            System.out.println("Napaka pri izklopu elektrike Tinkerforge modulom!");
            return false;
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Kličem stikalo za vklop elektrike Tinkerfog modulom - " + resources.getString("reset_switch"));

        if (!executeHTTPPOSTRequest(resources.getString("reset_switch"), "ON")) {
            System.out.println("Napaka pri vklopu elektrike Tinkerforge modulom!");
            return false;
        } else {
            returnString = executeCommand("sudo service openhab2 restart");
            if (returnString.equals("")) {
                return true;
            } else {
                System.out.println("NAPAKA pri resetiranju openHABa!!!!!");
                System.out.println(returnString);
                return false;
            }
        }

    }
}
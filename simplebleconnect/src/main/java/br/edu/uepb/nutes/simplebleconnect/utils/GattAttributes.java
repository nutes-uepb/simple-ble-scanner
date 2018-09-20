package br.edu.uepb.nutes.simplebleconnect.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains GATT mapping of services and features of supported devices.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES/UEPB
 */
public class GattAttributes {
    private static Map<String, String> attributes = new HashMap();
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String SERVICE_GLUCOSE = "00001808-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_GLUCOSE_CUSTOM = "00000000-0000-1000-1000-000000000000";
    public static String SERVICE_HEALTH_THERMOMETER = "00001809-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_BLOOD_PRESSURE = "00001810-0000-1000-8000-00805f9b34fb";
    public static String SERBICE_DEVICE_INFOR = "0000180a-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_GENERIC_ACCESS = "00001800-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_SCALE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_SCALE_USER_DATA = "0000ffe5-0000-1000-8000-00805f9b34fb";

    public static String SERVICE_HEART_RATE = "0000180d-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_USER_DATA = "0000181c-0000-1000-8000-00805f9b34fb";
    public static String SERVICE_STEPS_DISTANCE_CALORIES = "0000fee0-0000-1000-8000-00805f9b34fb";

    public static String CHARACTERISTIC_TEMPERATURE_MEASUREMENT = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_GLUSOSE_MEASUREMENT = "00002a18-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_GLUSOSE_MEASUREMENT_CONTEXT = "00002a34-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL = "00002a52-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_GLUSOSE_CUSTOM = "00000000-0000-1000-1000-000000000001";
    public static String CHARACTERISTIC_BLOOD_PRESSURE_MEASUREMENT = "00002a35-0000-1000-1000-000000000001";
    public static String CHARACTERISTIC_BLOOD_PRESSURE_INTERMEDIATE_CUFF = "00002a36-0000-1000-1000-000000000001";
    public static String CHARACTERISTIC_BLOOD_PRESSURE_FEATURE = "00002a49-0000-1000-1000-000000000001";
    public static String CHARACTERISTIC_SCALE_MEASUREMENT = "0000ffe4-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_SCALE_USER_DATA = "0000ffe9-0000-1000-8000-00805f9b34fb";

    public static String CHARACTERISTIC_HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_HEART_RATE_CONTROL_POINT = "00002a39-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_DATABASE_CHANGE_INCREMENT = "00002a99-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_INDEX = "00002a9a-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_USER_CONTROL_POINT = "00002a9f-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_FIRST_NAME = "00002a8a-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_LAST_NAME = "00002a90-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_AGE = "00002a80-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_GENDER = "00002a8c-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_WEIGHT = "00002a98-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_HEIGHT = "00002a8e-0000-1000-8000-00805f9b34fb";
    public static String CHARACTERISTIC_USER_DATA_LANGUAGE = "00002aa2-0000-1000-8000-00805f9b34fb";

    public static String CHARACTERISTIC_STEPS_DISTANCE_CALORIES = "00000007-0000-3512-2118-0009af100700";

    static {
        // Services
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("00001805-0000-1000-8000-00805f9b34fb", "Current Time Service");
        attributes.put(SERVICE_GLUCOSE, "MeasurementGlucose");
        attributes.put(SERVICE_GLUCOSE_CUSTOM, "Custom Service");
        attributes.put(SERVICE_HEALTH_THERMOMETER, "Health Thermometer");
        attributes.put(SERBICE_DEVICE_INFOR, "Device Information");
        attributes.put(SERVICE_GENERIC_ACCESS, "Generic Access");
        attributes.put(SERVICE_SCALE, "Scale Service");
        attributes.put(SERVICE_SCALE_USER_DATA, "Scale User Data Service");
        attributes.put(SERVICE_HEART_RATE, "Heart Rate");
        attributes.put(SERVICE_USER_DATA, "User Data");

        /**
         * Characteristics Battery
         */
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level");

        /**
         * Characteristics MeasurementGlucose
         */
        attributes.put(CHARACTERISTIC_GLUSOSE_MEASUREMENT, "MeasurementGlucose Measurement");
        attributes.put(CHARACTERISTIC_GLUSOSE_MEASUREMENT_CONTEXT, "MeasurementGlucose Measurement ContextMeasurement");
        attributes.put("00002a51-0000-1000-8000-00805f9b34fb", "MeasurementGlucose Feature");
        attributes.put(CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL, "Record Access Control Point");
        attributes.put(CHARACTERISTIC_GLUSOSE_CUSTOM, "Custom Charactetistic");

        /**
         * Characteristics Health Thermometer
         */
        attributes.put(CHARACTERISTIC_TEMPERATURE_MEASUREMENT, "Temperature Measurement");
        attributes.put("00002a1d-0000-1000-8000-00805f9b34fb", "Temperature Type");
        attributes.put("00002a1e-0000-1000-8000-00805f9b34fb", "Intermediate Temperature");
        attributes.put("00002a21-0000-1000-8000-00805f9b34fb", "Measurement Interval");

        /**
         * Characteristics Blood Pressure
         */
        attributes.put(CHARACTERISTIC_BLOOD_PRESSURE_MEASUREMENT, "Blood Pressure Measurement");
        attributes.put(CHARACTERISTIC_BLOOD_PRESSURE_INTERMEDIATE_CUFF, "Intermediate Cuff Pressure");
        attributes.put(CHARACTERISTIC_BLOOD_PRESSURE_FEATURE, "Blood Pressure Feature");

        /**
         * Characteristics Scale
         */
        attributes.put(CHARACTERISTIC_SCALE_MEASUREMENT, "Scale Measurement");
        attributes.put(CHARACTERISTIC_SCALE_USER_DATA, "Scale User Date");

        /**
         * Characteristics Heart Rate
         */
        attributes.put(CHARACTERISTIC_HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(CHARACTERISTIC_HEART_RATE_MEASUREMENT, "Body Sensor Location");

        /**
         * Characteristics Device Information
         */
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
        attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        attributes.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID");
        attributes.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE 11073-20601 Regulatory Certification Data List");
        attributes.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID");

        /**
         * Characteristics Generic Access
         */
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag");
        attributes.put("00002a03-0000-1000-8000-00805f9b34fb", "Reconnection Address");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
        attributes.put("00002a08-0000-1000-8000-00805f9b34fb", "Date Time");

        /**
         * Characteristics Generic Attribute
         */
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");

        /**
         * Characteristics Current Time Service
         */
        attributes.put("00002a2b-0000-1000-8000-00805f9b34fb", "Current Time");
        attributes.put("00002a0f-0000-1000-8000-00805f9b34fb", "Local Time Information");
        attributes.put("00002a14-0000-1000-8000-00805f9b34fb", "Reference Time Information");

        /**
         * Characteristics Generic Access
         */
        attributes.put(CHARACTERISTIC_USER_DATA_DATABASE_CHANGE_INCREMENT, "Database Change Increment");
        attributes.put(CHARACTERISTIC_USER_DATA_INDEX, "User Index");
        attributes.put(CHARACTERISTIC_USER_DATA_USER_CONTROL_POINT, "User Control Point");
        attributes.put(CHARACTERISTIC_USER_DATA_FIRST_NAME, "First Name");
        attributes.put(CHARACTERISTIC_USER_DATA_LAST_NAME, "Last Name");
        attributes.put(CHARACTERISTIC_USER_DATA_AGE, "Age");
        attributes.put(CHARACTERISTIC_USER_DATA_GENDER, "Gender");
        attributes.put(CHARACTERISTIC_USER_DATA_WEIGHT, "Weight");
        attributes.put(CHARACTERISTIC_USER_DATA_HEIGHT, "Height");
        attributes.put(CHARACTERISTIC_USER_DATA_LANGUAGE, "Language");

    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}

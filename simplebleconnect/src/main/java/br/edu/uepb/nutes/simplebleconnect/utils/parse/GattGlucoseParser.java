package br.edu.uepb.nutes.simplebleconnect.utils.parse;

import android.bluetooth.BluetoothGattCharacteristic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


/**
 * Parse for blood glucose.
 * Copyright (c) 2018 NUTES/UEPB
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
public class GattGlucoseParser {
    private static final int UNIT_kgpl = 0;
    private static final int UNIT_molpl = 1;
    private static final int UNIT_kg = 0;
    private static final int UNIT_l = 1;

    private static final byte TIMESTAMP_FLAG = 0x01; // 1 bit

    /**
     * Parse for the ACCU-CHEK device, according to GATT.
     * Supported Models: Accu-Chek Performa Connect.
     *
     * {@link <https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.glucose_measurement.xml>}
     *
     * @param characteristic
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(final BluetoothGattCharacteristic characteristic) throws JSONException {
        JSONObject result = new JSONObject();
        float glucoseConcentration = 0;
        Calendar calendar = null;
        String unit = "";

        int offset = 0;
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);
        final StringBuilder builder = new StringBuilder();

        /**
         * false 	No Timestamp in the packet
         * true 	There is a timestamp information
         */
        final boolean timestampIncluded = (flags & TIMESTAMP_FLAG) > 0;

        final boolean timeOffsetPresent = (flags & 0x01) > 0;
        final boolean typeAndLocationPresent = (flags & 0x02) > 0;
        final int concentrationUnit = (flags & 0x04) > 0 ? UNIT_molpl : UNIT_kgpl;
        final boolean sensorStatusAnnunciationPresent = (flags & 0x08) > 0;
        final boolean contextInfoFollows = (flags & 0x10) > 0;

        int sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        offset += 2;

        if (timestampIncluded) {
            calendar = GattDateTimeParser.parse(characteristic, offset);
            offset += 7;
        }

        if (timeOffsetPresent) {
            // time offset is ignored in the current release
            final int timeOffset = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, offset);
            offset += 2;
        }

        if (typeAndLocationPresent) {
            glucoseConcentration = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
            final int typeAndLocation = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
            int type = (typeAndLocation & 0x0F);
            int sampleLocation = (typeAndLocation & 0xF0) >> 4;
            unit = concentrationUnit == UNIT_kgpl ? "kg/L" : "mol/L";
            offset += 3;

            /**
             * Id of Type and Sample Location
             */
            result.put("glucoseTypeId", type); // Type Id
            result.put("glucoseLocationId", type); // Sample Location Id
        }

        if (sensorStatusAnnunciationPresent) {
            int status = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);

            /**
             * Id of Sensor Status Annunciation
             */
            result.put("sensorStatusAnnunciationId", status);
        }

        result.put("glucose", glucoseConcentration);
        result.put("glucoseUnit", unit);
        result.put("sequenceNumber", sequenceNumber);

        return result;
    }

    /**
     * Parse for the ACCU-CHEK device, according to GATT.
     * Supported Models: Accu-Chek Performa Connect.
     * <p>
     * {@link <https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.glucose_measurement_context.xml>}
     *
     * @param characteristic
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject contextParse(final BluetoothGattCharacteristic characteristic) throws JSONException {
        JSONObject result = new JSONObject();

        int offset = 0;
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

        final boolean carbohydratePresent = (flags & 0x01) > 0;
        final boolean mealPresent = (flags & 0x02) > 0;
        final boolean testerHealthPresent = (flags & 0x04) > 0;
        final boolean exercisePresent = (flags & 0x08) > 0;
        final boolean medicationPresent = (flags & 0x10) > 0;
        final int medicationUnit = (flags & 0x20) > 0 ? UNIT_l : UNIT_kg;
        final boolean hbA1cPresent = (flags & 0x40) > 0;
        final boolean moreFlagsPresent = (flags & 0x80) > 0;

        int sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        offset += 2;

        if (moreFlagsPresent) // not supported yet
            offset += 1;

        if (carbohydratePresent) {
            int carbohydrateId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            float carbohydrateUnits = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 1);
            offset += 3;

            /**
             * Carbohydrate ID and
             * Carbohydrate - units of kilograms
             */
            result.put("glucoseCarbohydrateId", carbohydrateId); // Carbohydrate Id
            result.put("glucoseCarbohydrateUnits", carbohydrateUnits); // Carbohydrate Units
        }

        if (mealPresent) {
            int meal = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            /**
             * Meal ID
             */
            result.put("glucoseMealId", meal);
        }

        if (testerHealthPresent) {
            int testerHealth = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            int tester = (testerHealth & 0xF0) >> 4;
            int health = (testerHealth & 0x0F);
            // TODO handle data when you need it

            offset += 1;
        }

        if (exercisePresent) {
            int exerciseDuration = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            int exerciseIntensity = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
            // TODO handle data when you need it

            offset += 3;
        }

        if (medicationPresent) {
            int medicationId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            float medicationQuantity = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 1);
            // TODO handle data when you need it

            offset += 3;
        }

        if (hbA1cPresent) {
            float HbA1c = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
            // TODO handle data when you need it
        }

        result.put("sequenceNumber", sequenceNumber);

        return result;
    }
}

package br.edu.uepb.nutes.simplebleconnect.utils.parse;

import android.bluetooth.BluetoothGattCharacteristic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


/**
 * Parse for temperature.
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
public class GattHTParser {
    private static final byte TEMPERATURE_UNIT_FLAG = 0x01; // 1 bit
    private static final byte TIMESTAMP_FLAG = 0x02; // 1 bit
    private static final byte TEMPERATURE_TYPE_FLAG = 0x04; // 1 bit

    /**
     * Parse for the PHILIPS device, according to GATT.
     * Supported Models: DL8740.
     *
     * {@link <https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.temperature_measurement.xml>}
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(final BluetoothGattCharacteristic characteristic) throws JSONException {
        JSONObject result = new JSONObject();

        int offset = 0;
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

        /**
         * false 	Temperature is in Celsius degrees
         * true 	Temperature is in Fahrenheit degrees
         */
        final boolean fahrenheit = (flags & TEMPERATURE_UNIT_FLAG) > 0;

        /**
         * false 	No Timestamp in the packet
         * true 	There is a timestamp information
         */
        final boolean timestampIncluded = (flags & TIMESTAMP_FLAG) > 0;

        /**
         * false 	Temperature type is not included
         * true 	Temperature type included in the packet
         */
        final boolean temperatureTypeIncluded = (flags & TEMPERATURE_TYPE_FLAG) > 0;

        final float tempValue = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, offset);
        offset += 4;

        Calendar timestamp = null;
        if (timestampIncluded) {
            timestamp = GattDateTimeParser.parse(characteristic, offset);
            offset += 7;
        }

        int type = -1;
        if (temperatureTypeIncluded) {
            type = characteristic.getValue()[offset];
        }

        String unit = fahrenheit ? "°F" : "°C";

        /**
         * Populating the JSON
         */
        result.put("temperature", tempValue);
        result.put("temperatureUnit", unit);
      //  result.put("timestamp", DateUtils.getCurrentDatetime());

        return result;
    }
}

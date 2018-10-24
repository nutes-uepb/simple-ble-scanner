package br.edu.uepb.nutes.simplebleconnect.utils.parse;

import android.bluetooth.BluetoothGattCharacteristic;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.uepb.nutes.simplebleconnect.utils.DateUtils;


/**
 * Parse for heart rate.
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
public class GattHRParser {
    private static final byte HEART_RATE_VALUE_FORMAT = 0x01; // 1 bit

    /**
     * Parse for the POLAR device, according to GATT.
     * Supported Models: H7, H10.
     *
     * {@link <https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.heart_rate_measurement.xml>}
     *
     * @param characteristic
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(final BluetoothGattCharacteristic characteristic) throws JSONException {
        JSONObject result = new JSONObject();
        int offset = 0;
        int heartRateValue = 0;

        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

		/*
         * false 	Heart Rate Value Format is set to UINT8. Units: beats per minute (bpm)
		 * true 	Heart Rate Value Format is set to UINT16. Units: beats per minute (bpm)
		 */
        final boolean value16bit = (flags & HEART_RATE_VALUE_FORMAT) > 0;

        // heart rate value is 8 or 16 bit long
        heartRateValue = characteristic.getIntValue(value16bit ? BluetoothGattCharacteristic.FORMAT_UINT16 :
                BluetoothGattCharacteristic.FORMAT_UINT8, offset++); // bits per minute

        /**
         * Populating the JSON
         */
        result.put("heartRate", heartRateValue);
        result.put("heartRateUnit", "bpm");
        result.put("timestamp", DateUtils.getCurrentDatetime());

        return result;
    }
}

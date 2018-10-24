package br.edu.uepb.nutes.simplebleconnect.utils.parse;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.uepb.nutes.simplebleconnect.utils.DateUtils;


/**
 * Parse for body composition.
 * Copyright (c) 2018 NUTES/UEPB
 *
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
public class YunmaiParser {

    /**
     * Parse for YUNMAI device.
     * Supported Models: smart scale (M1301, M1302, M1303, M1501).
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(@NonNull final BluetoothGattCharacteristic characteristic) throws JSONException {
        final byte[] data = characteristic.getValue();
        JSONObject result = new JSONObject();
        double bodyMass = 0f;

        if (data.length > 0) {
            /**
             * Timestamp current
             */
            result.put("timestamp", DateUtils.getCurrentDatetime());

            /**
             * 03: response type
             *     01 - unfinished weighing
             *     02 - finished weighing
             */
            boolean isFinalized = String.format("%02X", data[3]).equals("02");
            result.put("isFinalized", isFinalized);

            /**
             * unfinished weighing
             * 08-09: weight - BE uint16 times 0.01
             */
            bodyMass = Integer.valueOf(String.format("%02X", data[8]) + String.format("%02X", data[9]), 16) * 0.01f;
            result.put("bodyMass", bodyMass);

            // Body Mass Unit default
            result.put("bodyMassUnit", "kg");

            // Finalized
            if (isFinalized) {
                /**
                 * finished weighing
                 * 13-14: weight - BE uint16 times 0.01
                 */
                bodyMass = Integer.valueOf(String.format("%02X", data[13]) + String.format("%02X", data[14]), 16) * 0.01f;
                result.put("bodyMass", bodyMass);

                /**
                 * 15-16: resistance - BE uint 16
                 */
                final double resistance = Integer.valueOf(String.format("%02X", data[15]) + String.format("%02X", data[16]), 16);
                result.put("resistance", resistance);

                /**
                 * Body Fat in percentage
                 *
                 * 17-18: - BE uint16 times 0.01
                 */
                final double bodyFat = Integer.valueOf(String.format("%02X", data[17]) + String.format("%02X", data[18]), 16) * 0.01f;
                result.put("bodyFat", bodyFat);
                result.put("bodyFatUnit", "%"); // value fixed

                /**
                 * USER ID
                 * 09-12: recognized userID - BE uint32
                 */
//                Integer userID = Integer.valueOf(String.format("%02X", data[9]) +
//                        String.format("%02X", data[10]) +
//                        String.format("%02X", data[11]) +
//                        String.format("%02X", data[12]), 32
//                );
//
//                result.put("userID", userID);
            }
        }

        return result;
    }
}
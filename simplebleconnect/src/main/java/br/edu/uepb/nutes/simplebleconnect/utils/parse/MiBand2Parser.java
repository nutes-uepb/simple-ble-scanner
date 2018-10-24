package br.edu.uepb.nutes.simplebleconnect.utils.parse;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Parse for Smart Band.
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
public class MiBand2Parser {

    /**
     * Parse for smart band.
     * Supported Models: (MI BAND 2).
     *
     * @param characteristic
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(@NonNull BluetoothGattCharacteristic characteristic) throws JSONException {
        JSONObject result = new JSONObject();
        byte[] data = characteristic.getValue();

        /* Parse data */
        double stepsValue = (double) (((data[1] & 255) | ((data[2] & 255) << 8)));
        double distanceValue = (double) ((((data[5] & 255) | ((data[6] & 255) << 8)) | (data[7] & 16711680)) | ((data[8] & 255) << 24));
        double caloriesValue = (double) ((((data[9] & 255) | ((data[10] & 255) << 8)) | (data[11] & 16711680)) | ((data[12] & 255) << 24));

        /**
         * Populating the JSON
         */
        result.put("steps", stepsValue);
        result.put("stepsUnit", "");
        result.put("distance", distanceValue);
        result.put("distanceUnit", "m");
        result.put("calories", caloriesValue);
        result.put("caloriesUnit", "kcal");

        return result;
    }
}
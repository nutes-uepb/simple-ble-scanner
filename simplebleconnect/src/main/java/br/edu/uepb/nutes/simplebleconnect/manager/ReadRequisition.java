package br.edu.uepb.nutes.simplebleconnect.manager;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

/**
 * Class of requisition for characteristic type Read.
 * Copyright (c) 2018 NUTES/UEPB
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class ReadRequisition {
    private final String TAG = "ReadRequisition";
    public final int STATE_ONHOLD = 0;
    public final int STATE_ONGOING = 1;
    private final BluetoothGatt gatt;
    private final BluetoothGattCharacteristic characteristic;
    private int state;

    /**
     * Constructor of Read Requisition.
     *
     * @param gatt {@link BluetoothGatt}
     * @param characteristic {@link BluetoothGattCharacteristic}
     */
    public ReadRequisition(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        this.gatt = gatt;
        this.characteristic = characteristic;
        this.state = STATE_ONHOLD;
    }

    /**
     * Start requisition.
     */
    public boolean start() {
        Log.i(TAG, "start()");
        if (gatt.readCharacteristic(characteristic)) {
            state = STATE_ONGOING;
            return true;
        }
        return false;
    }

    /**
     * Get Bluetooth Gatt of requisition.
     *
     * @return gatt {@link BluetoothGatt}
     */
    public BluetoothGatt getGatt() {
        return gatt;
    }

    /**
     * Get characteristic of requisition.
     *
     * @return characteristic {@link BluetoothGattCharacteristic}
     */
    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    /**
     * Get status of requisition.
     *
     * @return state
     */
    public int getState() {
        return state;
    }
}
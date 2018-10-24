package br.edu.uepb.nutes.simplebleconnect.manager;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/*
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
public interface BLECallbacks {

    /**
     * Called when the device has been connected. This does not mean that the application may start communication.
     *
     * @param gatt {@link BluetoothGatt}
     */
    void onDeviceConnected(final BluetoothGatt gatt);

    /**
     * Called when the device has disconnected.
     *
     * @param gatt {@link BluetoothGatt}
     */
    void onDeviceDisconnected(final BluetoothGatt gatt);

    /**
     * Called when the Android device started connecting to given device.
     *
     * @param gatt {@link BluetoothGatt}
     */
    void onDeviceConnecting(final BluetoothGatt gatt);

    /**
     * Called when user initialized disconnection.
     *
     * @param gatt {@link BluetoothGatt}
     */
    void onDeviceDisconnecting(final BluetoothGatt gatt);

    /**
     * Called when service discovery has finished and primary services has been found.
     *
     * @param gatt {@link BluetoothGatt}
     */
    void onServicesDiscovered(BluetoothGatt gatt);

    /**
     * Method called when all initialization requests has been completed.
     *
     * @param gatt {@link BluetoothGatt}
     */
    void onDeviceReady(BluetoothGatt gatt);

    /**
     * Called when a BLE error has occurred.
     *
     * @param device    {@link BluetoothDevice}
     * @param message   {@link String}
     * @param errorCode
     */
    void onError(final BluetoothDevice device, final String message, final int errorCode);

    /**
     * Callback indicating a notification has been received.
     *
     * @param characteristic {@link BluetoothGattCharacteristic}
     * @param gatt           {@link BluetoothGatt}
     */
    void onCharacteristicNotified(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt);

    /**
     * Callback indicating an indication has been received.
     *
     * @param characteristic {@link BluetoothGattCharacteristic}
     * @param gatt           {@link BluetoothGatt}
     */
    void onCharacteristicIndicated(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt);

    /**
     * Callback reporting the result of a characteristic read operation.
     *
     * @param characteristic {@link BluetoothGattCharacteristic}
     * @param gatt           {@link BluetoothGatt}
     */
    void onCharacteristicRead(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt);

    /**
     * Callback indicating the result of a characteristic write operation.
     *
     * @param characteristic {@link BluetoothGattCharacteristic}
     * @param gatt           {@link BluetoothGatt}
     */
    void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt);

}

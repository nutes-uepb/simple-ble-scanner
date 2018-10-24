package br.edu.uepb.nutes.simplebleconnect.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.edu.uepb.nutes.simplebleconnect.utils.GattAttributes;


/**
 * Class of Bluetooth Manager.
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
public class BLEManager<E extends BLECallbacks> {
    private final String METHOD = "MethodCalled";
    private final String TAG = "BLEManager";
    private final String CONNECTION_ERROR = "Connection error";
    private final String DISCOVER_ERROR = "Discover error";
    private final String READ_ERROR = "Read error";
    private final String WRITE_ERROR = "Write error";
    private Context context;
    private E mCallback;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private Map<String, BluetoothGatt> gattConnected;
    private List<ReadRequisition> queueReadRequisitions;

    /**
     * Constructor of BLEManager.
     *
     * @param context {@link Context}
     */
    public BLEManager(Context context) {
        this.context = context;
        initResources();
    }

    /**
     * Init resources.
     */
    private void initResources() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        gattConnected = new HashMap<>();
        queueReadRequisitions = new ArrayList<>();
    }

    /**
     * Connect on device.
     *
     * @param device {@link BluetoothDevice}
     * @param mCallback {@link BLECallbacks}
     * @param autoConnect
     * @throws NullPointerException
     * @throws NullPointerException
     */
    public void connect(final BluetoothDevice device, final E mCallback, final boolean autoConnect) {
        if (mCallback == null)
            throw new NullPointerException("You have to set callback for connection");
        if (device == null) throw new NullPointerException("You have to set device for connection");
        this.mCallback = mCallback;
        Log.i(METHOD, "connect()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Connected on " + device.getName() + " - " + device.getAddress());
                device.connectGatt(context, autoConnect, gattCallback);
            }
        }).start();
    }

    /**
     * add gatt in list device.
     *
     * @param gatt {@link BluetoothGatt}
     */
    private void connectedGatt(BluetoothGatt gatt) {
        Log.i(METHOD, "connectedGatt()");
        gattConnected.put(gatt.getDevice().getAddress(), gatt);
    }

    /**
     * Disconnect on device.
     * @param address {@link String}
     */
    private void disconnectedGatt(String address) {
        Log.i(METHOD, "disconnect()");
        gattConnected.remove(address);
    }

    /**
     * Setup characteristic passing the state by parameter.
     *
     * @param gatt {@link BluetoothGatt}
     * @param serviceUuid {@link String}
     * @param characteristicUuid {@link String}
     * @param enabled
     */
    public void setupCharacteristic(BluetoothGatt gatt, String serviceUuid, String characteristicUuid, boolean enabled) {
        internalSetupCharacteristic(gatt, serviceUuid, characteristicUuid, enabled);
    }

    /**
     * Setup characteristic not passing the state by parameter.
     *
     * @param gatt {@link BluetoothGatt}
     * @param serviceUuid {@link String}
     * @param characteristicUuid {@link String}
     */
    public void setupCharacteristic(BluetoothGatt gatt, String serviceUuid, String characteristicUuid) {
        internalSetupCharacteristic(gatt, serviceUuid, characteristicUuid, true);
    }

    /**
     * Setup characteristic of Gatt.
     *
     * @param gatt {@link BluetoothGatt}
     * @param serviceUuid {@link String}
     * @param characteristicUuid {@link String}
     * @param enabled
     */
    private void internalSetupCharacteristic(BluetoothGatt gatt, String serviceUuid, String characteristicUuid, boolean enabled) {
        Log.i(METHOD, " setupCharacteristic()");
        BluetoothGattService gattService = getGattService(UUID.fromString(serviceUuid));
        if (gattService != null) {
            Log.i(TAG, "Service found:" + gattService.getUuid());
            byte[] type;
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(characteristicUuid));
            if (characteristic != null) {
                Log.i(TAG, "Characteristic found: " + characteristic.getUuid());
                if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT))
                        || characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_MEASUREMENT))
                        || characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL))
                        || characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT)))
                    type = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
                else
                    type = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;

                final int charaProp = characteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
                    setCharacteristicRead(gatt, characteristic);
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
                    setCharacteristicNotifIndic(characteristic, gatt, type, enabled);
            } else Log.i(TAG, "Invalid characteristic");
        }
    }

    /**
     * Setup read characteristic.
     *
     * @param characteristic {@link BluetoothGattCharacteristic}
     * @param gatt {@link BluetoothGatt}
     */
    private void setCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        Log.i(METHOD, " setCharacteristicRead()");
        Log.i(TAG, "Setting characteristic READ");
        queueReadRequisitions.add(new ReadRequisition(gatt, characteristic));
    }

    /**
     * Setup notification/indication characteristic.
     *
     * @param characteristic {@link BluetoothGattCharacteristic}
     * @param gatt {@link BluetoothGatt}
     * @param type
     * @param enabled
     * @return
     */
    private boolean setCharacteristicNotifIndic(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt, byte[] type, boolean enabled) {
        Log.i(METHOD, "setCharacteristicNotifIndic()");
        if (characteristic != null) {
            gatt.setCharacteristicNotification(characteristic, enabled);
            return writeDescriptorClient(characteristic, gatt, type);
        }
        return false;
    }

    /**
     * Write descriptor of client.
     *
     * @param characteristic {@link BluetoothGattCharacteristic}
     * @param type
     * @param gatt {@link BluetoothGatt}
     * @return
     */
    private boolean writeDescriptorClient(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt, byte[] type) {
        if (characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) != null) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(type);
            return writeDescriptor(descriptor, gatt);
        }
        return false;
    }

    /**
     * Write descriptor.
     *
     * @param descriptor {@link BluetoothGattDescriptor}
     * @param mBluetoothGatt {@link BluetoothGatt}
     * @return
     */
    private boolean writeDescriptor(BluetoothGattDescriptor descriptor, BluetoothGatt mBluetoothGatt) {
        Log.i(METHOD, "writeDescriptor()");
        return descriptor != null && mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * Get instance of Gatt Service of device.
     *
     * @param uuid {@link UUID}
     * @return {@link BluetoothGattService}
     */
    public BluetoothGattService getGattService(UUID uuid) {
        Log.i(METHOD, "getGattService()");
        if (!gattConnected.isEmpty()) {
            for (BluetoothGatt bluetoothGatt : gattConnected.values()) {
                for (BluetoothGattService gattService : bluetoothGatt.getServices()) {
                    if (gattService.getUuid().equals(uuid)) {
                        Log.i(METHOD, "Service found: " + gattService.getUuid());
                        return gattService;
                    }
                }
                Log.i(TAG, "Service not found");
            }
        } else Log.i(TAG, "No connected devices");
        return null;
    }

    /**
     * Primary gatt callback.
     */
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, "onConnectionStateChange()");
            Log.i(TAG, "onConnectionStateChange() - Status: " + status);
            Log.i(TAG, "onConnectionStateChange() - New Status: " + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectedGatt(gatt);
                mCallback.onDeviceConnected(gatt);
                Log.i(TAG, "Conectado a " + gatt.getDevice().getName());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                disconnectedGatt(gatt.getDevice().getAddress());
                mCallback.onDeviceDisconnected(gatt);
                Log.i(TAG, "Desconectado a " + gatt.getDevice().getName());
            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                Log.i(TAG, "Conectando a " + gatt.getDevice().getName());
                mCallback.onDeviceConnecting(gatt);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                Log.i(TAG, "Desconectando a " + gatt.getDevice().getName());
                mCallback.onDeviceDisconnecting(gatt);
            } else {
                mCallback.onError(gatt.getDevice(), CONNECTION_ERROR, newState);
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onServicesDiscovered()");
                mCallback.onServicesDiscovered(gatt);
            } else {
                mCallback.onError(gatt.getDevice(), DISCOVER_ERROR, status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicRead()");
            onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mCallback.onCharacteristicRead(characteristic, gatt);
                if (!nextOfQueue()) mCallback.onDeviceReady(gatt);
            } else {
                mCallback.onError(gatt.getDevice(), READ_ERROR, status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.i(TAG, "onCharacteristicChanged()");
            BLEManager.this.onCharacteristicChanged(gatt, characteristic);
            final BluetoothGattDescriptor cccd = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            final boolean notifications = cccd == null || cccd.getValue() == null || cccd.getValue().length != 2 || cccd.getValue()[0] == 0x01;
            if (notifications) mCallback.onCharacteristicNotified(characteristic, gatt);
            else mCallback.onCharacteristicIndicated(characteristic, gatt);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicWrite()");
            if (status == BluetoothGatt.GATT_SUCCESS)
                mCallback.onCharacteristicWrite(characteristic, gatt);
            else mCallback.onError(gatt.getDevice(), WRITE_ERROR, status);
        }
    };

    /**
     * Callback reporting the result of a descriptor read operation.
     *
     * @param gatt GATT client {@link BluetoothGatt}
     * @param characteristic {@link BluetoothGattCharacteristic}
     */
    protected void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        // do nothing
    }

    /**
     * Callback reporting the result of a descriptor indication/notification operation.
     *
     * @param gatt           GATT client {@link BluetoothGatt}
     * @param characteristic Gatt characteristic {@link BluetoothGattCharacteristic}
     */
    protected void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
        // do nothing
    }

    /**
     * Start queue of read requisitions.
     */
    public void startRead() {
        nextOfQueue();
    }

    /**
     * Get next requisition of queueTemp.
     */
    public boolean nextOfQueue() {
        Log.i(TAG, "nextOfQueue()");
        if (!queueReadRequisitions.isEmpty()) {
            queueReadRequisitions.get(0).start();
            queueReadRequisitions.remove(0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get callback.
     *
     * @return mCallback
     */
    public E getmCallback() {
        return mCallback;
    }

    /**
     * Get Bluetooh Device instance.
     *
     * @param mac {@link String}
     * @return {@link BluetoothDevice}
     */
    public BluetoothDevice getDevice(String mac) {
        return bluetoothAdapter.getRemoteDevice(mac);
    }

    /**
     * Verify if device is connected.
     *
     * @param mac {@link String}
     * @return
     */
    public boolean isConnected(String mac) {
        return getGatt(mac) != null;
    }

    /**
     * Show all devices connected in Log.
     */
    public void showConnectedLog() {
        Log.i(METHOD, "showConnected()");
        Log.i(TAG, "Devices connected:");
        for (BluetoothGatt gatt : gattConnected.values())
            Log.i(TAG, gatt.getDevice().getName() + "\n");
    }

    /**
     * Get list gatt of devices connected.
     *
     * @return {@link List<BluetoothGatt>}
     */
    public List<BluetoothGatt> getGattConnected() {
        return (List<BluetoothGatt>) gattConnected.values();
    }

    /**
     * Get context.
     *
     * @return context {@link Context}
     */
    public Context getContext() {
        return context;
    }

    /**
     * Get Bluetooth Adapter.
     *
     * @return bluetoothAdapter {@link BluetoothAdapter}
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    /**
     * Get Bluetooth Manager.
     *
     * @return getBluetoothManager {@link BluetoothManager}
     */
    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    /**
     * Get gatt from list of connected.
     *
     * @param mac {@link String}
     * @return {@link BluetoothGatt}
     */
    public BluetoothGatt getGatt(String mac) {
        return gattConnected.get(mac);
    }
}

package br.edu.uepb.nutes.simplebleconnect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.edu.uepb.nutes.simplebleconnect.manager.BLECallbacks;
import br.edu.uepb.nutes.simplebleconnect.manager.BLEManager;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleBleScanner;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleScanCallback;
import br.edu.uepb.nutes.simplebleconnect.utils.GattAttributes;
import br.edu.uepb.nutes.simplebleconnect.utils.parse.GattHRParser;
import br.edu.uepb.nutes.simplebleconnect.utils.parse.GattHTParser;
import br.edu.uepb.nutes.simplebleconnect.utils.parse.YunmaiParser;

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
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private FloatingActionButton fab;
    private TextView state, deviceName;
    private BLEManager bleManager;
    private SimpleBleScanner bleScanner;
    private RecyclerView mRecyclerViewScan;
    private RecyclerView mRecyclerViewConnected;
    private RecyclerView.LayoutManager mLayoutManagerScan;
    private RecyclerView.LayoutManager mLayoutManagerConnected;
    DeviceAdapter adapterScan;
    DeviceAdapter adapterConnected;
    private List<Device> listDeviceScan;
    private List<Device> listDeviceConnected;
    private List<BluetoothDevice> devicesScan;
    private TextView status;
    private boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();

        bleScanner = new SimpleBleScanner.Builder()
                .addScanPeriod(9000000)
                .addFilterServiceUuid(GattAttributes.SERVICE_HEALTH_THERMOMETER)
                .addFilterServiceUuid(GattAttributes.SERVICE_HEART_RATE)
                .addFilterAddress("D4:36:39:91:75:71")
                .build();

        bleManager = new BLEManager(this);
    }

    public void initViews() {

        state = (TextView) findViewById(R.id.list_devices_scanned_title);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        status = (TextView) findViewById(R.id.status_manager);
        deviceName = (TextView) findViewById(R.id.device_name);
        devicesScan = new ArrayList<>();
        listDeviceScan = new ArrayList<>();
        listDeviceConnected = new ArrayList<>();
        mScanning = false;
        initRecyclerView();
        listenersButtons();
    }

    public void initRecyclerView() {
        //Aqui é instanciado o Recyclerview
        mRecyclerViewScan = (RecyclerView) findViewById(R.id.list_device_scanned);
        mLayoutManagerScan = new LinearLayoutManager(this);
        mRecyclerViewScan.setLayoutManager(mLayoutManagerScan);
        adapterScan = new DeviceAdapter(this, listDeviceScan, click, 0);
        mRecyclerViewScan.setAdapter(adapterScan);

        mRecyclerViewConnected = (RecyclerView) findViewById(R.id.list_device_connected);
        mLayoutManagerConnected = new LinearLayoutManager(this);
        mRecyclerViewConnected.setLayoutManager(mLayoutManagerConnected);
        adapterConnected = new DeviceAdapter(this, listDeviceConnected, click, 1);
        mRecyclerViewConnected.setAdapter(adapterConnected);
    }

    ClickDevice click = new ClickDevice() {
        @Override
        public void onClickDevice(Object object, int type) {
            if (type == 0) {
                Log.i(TAG, "onClickDevice() of scan");
                status.setText("Conectando a " + ((Device) object).getName() + " - " + ((Device) object).getAddress());
                // Log.i(TAG, bleManager.getDevice(((Device) object).getName())+"");
                bleManager.connect(bleManager.getDevice(((Device) object).getAddress()), bleCallbacks, true);
            } else {
                status.setText("");
                Log.i(TAG, "onClickDevice() of manager");
                BluetoothGatt bluetoothGatt = bleManager.getGatt(((Device) object).getAddress());
                bleManager.setupCharacteristic(bluetoothGatt, GattAttributes.SERVICE_GENERIC_ACCESS, "00002a00-0000-1000-8000-00805f9b34fb");
                bleManager.setupCharacteristic(bluetoothGatt, GattAttributes.SERVICE_GENERIC_ACCESS, "00002a01-0000-1000-8000-00805f9b34fb");
                bleManager.setupCharacteristic(bluetoothGatt, GattAttributes.SERBICE_DEVICE_INFOR, "00002a29-0000-1000-8000-00805f9b34fb");
                bleManager.startRead();
            }
        }
    };

    BLECallbacks bleCallbacks = new BLECallbacks() {
        @Override
        public void onDeviceConnected(BluetoothGatt gatt) {
            gatt.discoverServices();
            changeStatus("Conectado a " + gatt.getDevice().getName());
            deviceConnected(gatt.getDevice());
        }

        @Override
        public void onDeviceDisconnected(BluetoothGatt gatt) {
            changeStatus("Desconectado a " + gatt.getDevice().getName());
            deviceDisconnected(gatt.getDevice());
        }

        @Override
        public void onDeviceConnecting(BluetoothGatt gatt) {
            changeStatus("Conectando a " + gatt.getDevice().getName());
        }

        @Override
        public void onDeviceDisconnecting(BluetoothGatt gatt) {
            changeStatus("Desconectando a " + gatt.getDevice().getName());
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt) {
            changeStatus("Serviços de " + gatt.getDevice().getName() + " descobertos");
            if (gatt.getServices().contains(bleManager.getGattService(UUID.fromString(GattAttributes.SERVICE_SCALE)))) {
                Log.i(TAG, "Descobertos os serviços da Balança");
                bleManager.setupCharacteristic(gatt, GattAttributes.SERVICE_SCALE, GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT);
            }
            if (gatt.getServices().contains(bleManager.getGattService(UUID.fromString(GattAttributes.SERVICE_HEART_RATE)))) {
                Log.i(TAG, "Descobertos os serviços do HeartRate");
                bleManager.setupCharacteristic(gatt, GattAttributes.SERVICE_HEART_RATE, GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT);
            }
            if (gatt.getServices().contains(bleManager.getGattService(UUID.fromString(GattAttributes.SERVICE_HEALTH_THERMOMETER)))) {
                Log.i(TAG, "Descobertos os serviços do HeartRate");
                bleManager.setupCharacteristic(gatt, GattAttributes.SERVICE_HEALTH_THERMOMETER, GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT);
            }
        }

        @Override
        public void onDeviceReady(BluetoothGatt gatt) {
            Log.i(TAG, gatt.getDevice().getName() + " finalizou todas as leituras");
        }

        @Override
        public void onError(BluetoothDevice device, String message, int errorCode) {
            Log.i("Error", message);
        }

        @Override
        public void onCharacteristicNotified(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
            try {
                if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT))) {
                    Log.i("DADOS", "Recebendo dados do HeartRate");
                    Log.i("DADOS", GattHRParser.parse(characteristic).toString());
                    updateDeviceValue(GattHRParser.parse(characteristic).toString(), gatt.getDevice().getAddress());
                } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT))) {
                    Log.i("DADOS", "Recebendo dados da Balança");
                    Log.i("DADOS", YunmaiParser.parse(characteristic).toString());
                    updateDeviceValue(YunmaiParser.parse(characteristic).toString(), gatt.getDevice().getAddress());
                } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT))) {
                    Log.i("DADOS", "Recebendo dados do Termômetro");
                    Log.i("DADOS", GattHTParser.parse(characteristic).toString());
                    updateDeviceValue(GattHTParser.parse(characteristic).toString(), gatt.getDevice().getAddress());
                    //   Log.i("DADOS", " READ " + characteristic.getStringValue(0));
                    //  bleManager.nextOfQueue();
                    //showDialogInfo(characteristic.getStringValue(0));
                } else Log.i("DADOS", characteristic.getStringValue(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCharacteristicIndicated(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
            try {
                if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT))) {
                    Log.i("DADOS", "Recebendo dados do HeartRate");
                    Log.i("DADOS", GattHRParser.parse(characteristic).toString());
                    updateDeviceValue(GattHRParser.parse(characteristic).toString(), gatt.getDevice().getAddress());
                } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT))) {
                    Log.i("DADOS", "Recebendo dados da Balança");
                    Log.i("DADOS", YunmaiParser.parse(characteristic).toString());
                    updateDeviceValue(YunmaiParser.parse(characteristic).toString(), gatt.getDevice().getAddress());
                } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT))) {
                    Log.i("DADOS", "Recebendo dados do Termômetro");
                    Log.i("DADOS", GattHTParser.parse(characteristic).toString());
                    updateDeviceValue(GattHTParser.parse(characteristic).toString(), gatt.getDevice().getAddress());
                    //   Log.i("DADOS", " READ " + characteristic.getStringValue(0));
                    //  bleManager.nextOfQueue();
                    //showDialogInfo(characteristic.getStringValue(0));
                } else Log.i("DADOS", characteristic.getStringValue(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
            try {
                if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT))) {
                    Log.i(TAG, "Recebendo dados do HeartRate");
                    Log.i(TAG, GattHRParser.parse(characteristic).toString());
                } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT)))
                    Log.i("DADOS", "Recebendo dados da Balança");
                else {
                    Log.i(TAG, " READ " + characteristic.getStringValue(0));
                    showDialogInfo(characteristic.getStringValue(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {

        }
    };

    /**
     * Seta os listeners para os botões
     */
    public void listenersButtons() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (!mScanning) {
                    mScanning = true;
                    Log.i(TAG, "Starting scan...");
                    bleScanner.startScan(simpleScanCallback);
                    status.setText("Escaneando...");
                } else {
                    Log.i(TAG, "Stoping scan...");
                    bleScanner.stopScan();
                    mScanning = false;
                    status.setText("");
                }
            }
        });
    }

    public void updateRssi(final Device mDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= listDeviceScan.size(); i++)
                    if (listDeviceScan.get(i).equals(mDevice)) {
                        listDeviceScan.remove(i);
                        listDeviceScan.add(i, mDevice);
                        adapterScan.notifyDataSetChanged();
                    }
            }
        });
    }

    SimpleScanCallback simpleScanCallback = new SimpleScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult scanResult) {
            Log.v(TAG + " - Scan", scanResult.getDevice().getName() + " " + scanResult.getDevice().getAddress());

            scanResult.getRssi();
            BluetoothDevice bluetoothDevice = scanResult.getDevice();
            Device device = new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress(), String.valueOf(scanResult.getRssi()));
            if (!devicesScan.contains(bluetoothDevice)) {
                listDeviceScan.add(device);
                devicesScan.add(bluetoothDevice);
            } else {
                updateRssi(device);
            }
            adapterScan.notifyDataSetChanged();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> scanResults) {

        }

        @Override
        public void onScanFailed(int errorCode) {

        }

        @Override
        public void onFinish() {

        }
    };


    public void deviceConnected(final BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Device newDevice = new Device(device.getName(), device.getAddress());
                if (!listDeviceConnected.contains(newDevice)) {
                    listDeviceConnected.add(newDevice);
                    adapterConnected.notifyDataSetChanged();
                }
            }
        });
    }

    public void deviceDisconnected(final BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listDeviceConnected.remove(new Device(device.getName(), device.getAddress()));
                adapterConnected.notifyDataSetChanged();
            }
        });
    }

    public void changeStatus(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText(msg);
            }
        });
    }

    public void updateDeviceValue(final String info, final String addressDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listDeviceConnected.contains(new Device(null, addressDevice))) {
                    for (Device device : listDeviceConnected)
                        if (device.getAddress().equals(addressDevice))
                            device.setValue(info);
                    adapterConnected.notifyDataSetChanged();
                }
            }
        });
    }

    public BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, "onConnectionStateChange()");
            Log.i(TAG, "onConnectionStateChange() - Status: " + status);
            Log.i(TAG, "onConnectionStateChange() - New Status: " + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Conectado a " + gatt.getDevice().getName());
                gatt.discoverServices();
                changeStatus("Conectado a " + gatt.getDevice().getName());
                deviceConnected(gatt.getDevice());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Desconectado a " + gatt.getDevice().getName());
                changeStatus("Desconectado a " + gatt.getDevice().getName());
                deviceDisconnected(gatt.getDevice());
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.i(TAG, "onServicesDiscovered()");
            changeStatus("Serviços de " + gatt.getDevice().getName() + " descobertos");

            if (gatt.getServices().contains(bleManager.getGattService(UUID.fromString(GattAttributes.SERVICE_SCALE)))) {
                Log.i(TAG, "Descobertos os serviços da Balança");
                bleManager.setupCharacteristic(gatt, GattAttributes.SERVICE_SCALE, GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT);
            }
            if (gatt.getServices().contains(bleManager.getGattService(UUID.fromString(GattAttributes.SERVICE_HEART_RATE)))) {
                Log.i(TAG, "Descobertos os serviços do HeartRate");
                bleManager.setupCharacteristic(gatt, GattAttributes.SERVICE_HEART_RATE, GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT);
            }
            if (gatt.getServices().contains(bleManager.getGattService(UUID.fromString(GattAttributes.SERVICE_HEALTH_THERMOMETER)))) {
                Log.i(TAG, "Descobertos os serviços do HeartRate");
                bleManager.setupCharacteristic(gatt, GattAttributes.SERVICE_HEALTH_THERMOMETER, GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicRead()");

            try {
                if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT))) {
                    Log.i(TAG, "Recebendo dados do HeartRate");
                    Log.i(TAG, GattHRParser.parse(characteristic).toString());
                } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT)))
                    Log.i("DADOS", "Recebendo dados da Balança");
                else {
                    Log.i(TAG, " READ " + characteristic.getStringValue(0));
                    bleManager.nextOfQueue();
                    showDialogInfo(characteristic.getStringValue(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.i(TAG, "onCharacteristicChanged()");

            try {
                if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT))) {
                    Log.i("DADOS", "Recebendo dados do HeartRate");
                    Log.i("DADOS", GattHRParser.parse(characteristic).toString());
                    updateDeviceValue(GattHRParser.parse(characteristic).toString(), gatt.getDevice().getAddress());
                } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT))) {
                    Log.i("DADOS", "Recebendo dados da Balança");
                    Log.i("DADOS", YunmaiParser.parse(characteristic).toString());
                    updateDeviceValue(YunmaiParser.parse(characteristic).toString(), gatt.getDevice().getAddress());
                } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT))) {
                    Log.i("DADOS", "Recebendo dados do Termômetro");
                    Log.i("DADOS", GattHTParser.parse(characteristic).toString());
                    updateDeviceValue(GattHTParser.parse(characteristic).toString(), gatt.getDevice().getAddress());
                    //   Log.i("DADOS", " READ " + characteristic.getStringValue(0));
                    //  bleManager.nextOfQueue();
                    //showDialogInfo(characteristic.getStringValue(0));
                } else Log.i("DADOS", characteristic.getStringValue(0));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicWrite()");
        }

    };

    public void showDialogInfo(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Informações")
                        .setMessage(msg)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (mScanning) bleScanner.stopScan();
            devicesScan.clear();
            listDeviceConnected.clear();
            listDeviceScan.clear();
            adapterConnected.notifyDataSetChanged();
            adapterScan.notifyDataSetChanged();
            status.setText("");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
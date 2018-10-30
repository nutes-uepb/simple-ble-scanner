package br.edu.uepb.nutes.simplebleconnect.examples;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.simplebleconnect.ClickDevice;
import br.edu.uepb.nutes.simplebleconnect.Device;
import br.edu.uepb.nutes.simplebleconnect.DeviceAdapter;
import br.edu.uepb.nutes.simplebleconnect.R;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleBleScanner;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleScanCallback;
import br.edu.uepb.nutes.simplebleconnect.utils.GattAttributes;

public class ExampleBLEScanner extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final String TAG = "BLEScanner";
    private FloatingActionButton fab;
    private RecyclerView mRecyclerViewScan;
    private RecyclerView.LayoutManager mLayoutManagerScan;
    private List<Device> listDeviceScan;
    private List<BluetoothDevice> devicesScan;
    DeviceAdapter adapterScan;
    private TextView status;
    private Spinner services;
    private EditText nameDevice, address, timeScan;
    private SimpleBleScanner bleScanner;
    private boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_blescanner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BLEScanner");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initViews();

    }

    public void initViews() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        devicesScan = new ArrayList<>();
        listDeviceScan = new ArrayList<>();
        status = findViewById(R.id.status_manager);
        services = findViewById(R.id.services);
        address = findViewById(R.id.adress);
        nameDevice = findViewById(R.id.name_device);
        timeScan = findViewById(R.id.time_scan);
        initRecyclerView();
        initFilters();
        listenersButtons();

    }

    public void initFilters() {

        // Spinner click listener
        services.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> servicesList = new ArrayList<String>();
        servicesList.add("All Services");
        servicesList.add("Thermometer");
        servicesList.add("Scale");
        servicesList.add("Heart Rate");
        servicesList.add("Glucose");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, servicesList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        services.setAdapter(dataAdapter);
    }

    public void initRecyclerView() {
        //Aqui é instanciado o Recyclerview
        mRecyclerViewScan = (RecyclerView) findViewById(R.id.list_device_scanned);
        mLayoutManagerScan = new LinearLayoutManager(this);
        mRecyclerViewScan.setLayoutManager(mLayoutManagerScan);
        adapterScan = new DeviceAdapter(this, listDeviceScan, click, 0);
        mRecyclerViewScan.setAdapter(adapterScan);

    }

    ClickDevice click = new ClickDevice() {
        @Override
        public void onClickDevice(Object object, int type) {
            Log.i(TAG, "onClickDevice() of scan");
            String msg = "Conectando a " + ((Device) object).getName() + " - " + ((Device) object).getAddress();
            status.setText(msg);
            // Log.i(TAG, bleManager.getDevice(((Device) object).getName())+"");
            //bleManager.connect(bleManager.getDevice(((Device) object).getAddress()), bleCallbacks, true);
        }
    };

    SimpleScanCallback simpleScanCallback = new SimpleScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult scanResult) {
            Log.v(TAG + " - Scan", scanResult.getDevice().getName() + " " + scanResult.getDevice().getAddress());
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
            changeStatus("Error scan");
        }

        @Override
        public void onFinish() {
            changeStatus("Scan finish");
        }
    };

    public void updateRssi(final Device mDevice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < listDeviceScan.size(); i++)
                    if (listDeviceScan.get(i).equals(mDevice)) {
                        // listDeviceScan.remove(i);
                        //listDeviceScan.add(i, mDevice);
                        listDeviceScan.get(i).setRSSI(mDevice.getRSSI());
                        adapterScan.notifyDataSetChanged();
                    }
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

    /**
     * Seta os listeners para os botões
     */
    public void listenersButtons() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBuilder();
                if (ActivityCompat.checkSelfPermission(ExampleBLEScanner.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ExampleBLEScanner.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (!mScanning) {
                    listDeviceScan.clear();
                    adapterScan.notifyDataSetChanged();
                    mScanning = true;
                    Log.i(TAG, "Starting scan...");
                    bleScanner.startScan(simpleScanCallback);
                    status.setText("Scanning...");
                } else {
                    Log.i(TAG, "Stoping scan...");
                    bleScanner.stopScan();
                    mScanning = false;
                    status.setText("");
                }
            }
        });
    }

    public void makeBuilder() {
        SimpleBleScanner.Builder builder = new SimpleBleScanner.Builder();

        if (services.getSelectedItem().equals("Thermometer"))
            builder.addFilterServiceUuid(GattAttributes.SERVICE_HEALTH_THERMOMETER);
        else if (services.getSelectedItem().equals("Scale"))
            builder.addFilterServiceUuid(GattAttributes.SERVICE_SCALE);
        else if (services.getSelectedItem().equals("Heart Rate"))
            builder.addFilterServiceUuid(GattAttributes.SERVICE_HEART_RATE);
        else if (services.getSelectedItem().equals("Glucose"))
            builder.addFilterServiceUuid(GattAttributes.SERVICE_GLUCOSE);

        if (!address.getText().toString().equals("Address"))
            builder.addFilterAddress(address.getText().toString());
        if (!nameDevice.getText().toString().equals("Name Device"))
            builder.addFilterName(nameDevice.getText().toString());
        if (!timeScan.getText().toString().equals("Time Scan"))
            builder.addScanPeriod(Integer.valueOf(timeScan.getText().toString()));

        bleScanner = builder.build();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
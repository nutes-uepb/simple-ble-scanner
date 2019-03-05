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
 */
package br.edu.uepb.nutes.simpleblescanner;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SimpleBleScannerLollipopImpl extends SimpleBleScanner {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private SimpleScannerCallback mSimpleScanCallback;
    private Handler handler;
    private Runnable runnable;

    /**
     * Constructor.
     *
     * @param builder
     */
    protected SimpleBleScannerLollipopImpl(SimpleBleScanner.Builder builder) {
        super(builder);
        initBluetoothLeScanner();
    }

    /**
     * Init Bluetooth Low Energy Scanner.
     */
    private void initBluetoothLeScanner() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Start scan.
     *
     * @param callback
     */
    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION})
    public void startScan(SimpleScannerCallback callback) {
        if (callback == null) throw new IllegalArgumentException("Callback is null");

        mSimpleScanCallback = callback;
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner == null) {
            throw new IllegalArgumentException("Bluetooth LE not available");
        }

        mBluetoothLeScanner.startScan(scanFilters, scanSettings, bleScanCallback);
        mScanning = true;
        handler = new Handler();

        runnable = new Runnable() {
            @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN})
            @Override
            public void run() {
                stopScan();
            }
        };
        handler.postDelayed(runnable, scanPeriod);
    }

    /**
     * Stop scan.
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN})
    @Override
    public void stopScan() {
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner == null || mSimpleScanCallback == null) return;
        mBluetoothLeScanner.stopScan(bleScanCallback);
        handler.removeCallbacks(runnable);
        mSimpleScanCallback.onFinish();
        mSimpleScanCallback = null;
        mBluetoothLeScanner = null;
        mScanning = false;
    }

    final ScanCallback bleScanCallback = new android.bluetooth.le.ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            mSimpleScanCallback.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            if (mSimpleScanCallback == null) return;

            mSimpleScanCallback.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            if (mSimpleScanCallback == null) return;

            mSimpleScanCallback.onScanFailed(errorCode);
        }
    };
}

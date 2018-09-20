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
package br.edu.uepb.nutes.simplebleconnect.scanner;

import android.Manifest;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * Class of Bluetooth Scanner.
 */
public abstract class SimpleBleScanner {
    protected boolean mScanning;
    protected int scanPeriod;
    protected List<ScanFilter> scanFilters;
    protected ScanSettings scanSettings;

    /**
     * Constructor.
     */
    protected SimpleBleScanner(Builder builder) {
        this.scanPeriod = builder.scanPeriod;
        this.scanFilters = builder.scanFilters;
        this.scanSettings = builder.scanSettings;
        initResources();
    }

    /**
     * Init resources.
     */
    private void initResources() {
        this.mScanning = false;
    }

    /**
     * Start scan.
     *
     * @param callback {@link SimpleScanCallback} Callback used to deliver scan results.
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    public abstract void startScan(SimpleScanCallback callback);

    /**
     * Stop scan.
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN})
    public abstract void stopScan();

    /**
     * Reset settings to default.
     */
    public void resetSettings() {
        this.scanPeriod = 1000; // 10s
        this.scanFilters = null;
        this.scanSettings = null;
    }

    /**
     * Return status scan.
     *
     * @return mScanning
     */
    public boolean isScanStarted() {
        return mScanning;
    }

    public static class Builder {
        private int scanPeriod;
        private List<ScanFilter> scanFilters;
        private ScanSettings scanSettings;

        public Builder() {
            this.scanPeriod = 1000;
            this.scanFilters = new ArrayList<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.scanSettings = new ScanSettings.Builder().build();
            }
        }

        /**
         * Add period of scan.
         * Period in Milliseconds.
         *
         * @param period {@link Integer}
         * @return {@link Builder}
         */
        public Builder addScanPeriod(int period) {
            scanPeriod = period;
            return this;
        }

        /**
         * Add filter adress.
         *
         * @param deviceAddress {@link String}
         * @return {@link Builder}
         * @throws UnsupportedOperationException If version sdk < lollipop.
         */
        public Builder addFilterAddress(String... deviceAddress) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                throw new UnsupportedOperationException();
            } else {
                for (String address : deviceAddress) {
                    scanFilters.add(new ScanFilter.Builder()
                            .setDeviceAddress(address)
                            .build());
                }
            }

            return this;
        }

        /**
         * Add filter service uuid.
         *
         * @param serviceUuid {@link String}
         * @return {@link Builder}
         * @throws UnsupportedOperationException If version sdk < lollipop.
         */
        public Builder addFilterServiceUuid(String... serviceUuid) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                throw new UnsupportedOperationException();
            } else {
                for (String uuid : serviceUuid) {
                    scanFilters.add(new ScanFilter.Builder()
                            .setServiceUuid(ParcelUuid.fromString(uuid))
                            .build());
                }
            }

            return this;
        }

        /**
         * Add filter device name.
         *
         * @param deviceName {@link String}
         * @return {@link Builder}
         * @throws UnsupportedOperationException If version sdk < lollipop.
         */
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        public Builder addFilterName(String... deviceName) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                throw new UnsupportedOperationException();
            } else {
                for (String name : deviceName) {
                    scanFilters.add(new ScanFilter.Builder()
                            .setDeviceName(name)
                            .build());
                }
            }

            return this;
        }

        /**
         * Add settings scan.
         *
         * @param scanSettings {@link ScanSettings}
         * @return {@link Builder}
         * @throws UnsupportedOperationException If version sdk < lollipop.
         */
        public Builder addSettingsScan(ScanSettings scanSettings) {
            this.scanSettings = scanSettings;
            return this;
        }

        /**
         * Build instance of SimpleBleScanner
         *
         * @return {@link SimpleBleScanner}
         * @throws UnsupportedOperationException If version sdk < lollipop.
         */
        public SimpleBleScanner build() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                throw new UnsupportedOperationException();
            } else {
                return new SimpleBleScannerLollipopImpl(this);
            }
        }
    }
}

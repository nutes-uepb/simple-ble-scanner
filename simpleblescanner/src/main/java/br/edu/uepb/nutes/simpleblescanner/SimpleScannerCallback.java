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

import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.support.annotation.NonNull;

import java.util.List;

public interface SimpleScannerCallback {
    /**
     * Fails to start scan as BLE scan with the same
     * settings is already started by the app.
     */
    public static final int SCAN_FAILED_ALREADY_STARTED = 1;

    /**
     * Fails to start scan as app cannot be registered.
     */
    public static final int SCAN_FAILED_APPLICATION_REGISTRATION_FAILED = 2;

    /**
     * Fails to start scan due an internal error
     */
    public static final int SCAN_FAILED_INTERNAL_ERROR = 3;

    /**
     * Fails to start power optimized scan as this feature
     * is not supported.
     */
    public static final int SCAN_FAILED_FEATURE_UNSUPPORTED = 4;

    /**
     * Fails to start scan as it is out of hardware resources.
     */
    public static final int SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES = 5;

    /**
     * Fails to start scan as application tries to scan too frequently.
     */
    public static final int SCAN_FAILED_SCANNING_TOO_FREQUENTLY = 6;

    public static final int NO_ERROR = 0;

    /**
     * Callback when a BLE advertisement has been found.
     *
     * @param callbackType Determines how this callback was triggered. Could be one of
     *                     {@link ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
     *                     {@link ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
     *                     {@link ScanSettings#CALLBACK_TYPE_MATCH_LOST}
     * @param result       A Bluetooth LE scan result.
     */
    void onScanResult(final int callbackType, @NonNull final ScanResult result);

    /**
     * Callback when batch results are delivered.
     *
     * @param results List of scan results that are previously scanned.
     */
    void onBatchScanResults(@NonNull final List<ScanResult> results);

    /**
     * Callback when scan could not be started.
     *
     * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
     */
    void onScanFailed(final int errorCode);

    /**
     * Callback when the period set for the scanner has expired,
     * or when the scanner is stopped.
     */
    void onFinish();
}

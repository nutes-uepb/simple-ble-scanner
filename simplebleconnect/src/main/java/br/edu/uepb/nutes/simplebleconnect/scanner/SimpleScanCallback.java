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

import android.bluetooth.le.ScanResult;

import java.util.List;

public interface SimpleScanCallback {
    /**
     * Callback when a BLE advertisement has been found.
     *
     * @param callbackType Determines how this callback was triggered. Could be one of
     *                     {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
     *                     {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
     *                     {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_MATCH_LOST}
     * @param scanResult   {@link ScanResult}.
     */
    void onScanResult(final int callbackType, final ScanResult scanResult);

    /**
     * Callback when batch results are delivered.
     *
     * @param scanResults List of scan results that are previously scanned. {@link ScanResult}.
     */
    void onBatchScanResults(final List<ScanResult> scanResults);

    /**
     * Callback when scan could not be started.
     *
     * @param errorCode Error code for scan failure.
     */
    void onScanFailed(final int errorCode);

    /**
     * Callback when the period set for the scanner has expired,
     * or when the scanner is stopped.
     */
    void onFinish();
}

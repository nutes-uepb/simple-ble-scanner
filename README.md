
# Simple BLE Scanner
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/MIT)

Library to scan Bluetooth Low Energy devices in a simple way.

_***This version works for Android SDK equal to or higher than 21. We will implement for lower versions when necessary.**_

## Features
* **Scanning** for BLE devices by name, UUID service or MAC address.

## Installation
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```
dependencies {
	 implementation 'com.github.nutes-uepb:simple-ble-scanner:v1.0.1'
}
```

## Using

#### Initialize settings
```java
SimpleBleScanner mScanner = new SimpleBleScanner.Builder()  
        .addFilterServiceUuid(  
            "00001809-0000-1000-8000-00805f9b34fb", // Health Thermometer Service  
            "0000180d-0000-1000-8000-00805f9b34fb" //  Heart Rate Service  
        )  
        .addFilterName("MI Band 2")  
        .addScanPeriod(15000) // 15s in milliseconds
        .build();
```        
#### Initialize scanning
```java        
mScanner.startScan(new SimpleScannerCallback() {  
    @Override  
	public void onScanResult(int callbackType, ScanResult scanResult) {  
        BluetoothDevice device = scanResult.getDevice();  
        Log.d("MainActivity", "Found Device: " + device.toString());  
    }  
  
    @Override  
    public void onBatchScanResults(List<ScanResult> scanResults) {  
        Log.d("MainActivity", "onBatchScanResults(): "+ Arrays.toString(scanResults.toArray()));
    }  
  
    @Override  
    public void onFinish() {  
        Log.d("MainActivity", "onFinish()");
    }  
  
    @Override  
    public void onScanFailed(int errorCode) {  
        Log.d("MainActivity", "onScanFailed() " + errorCode);  
    }  
});
```
#### Stop scanning
```java        
mScanner.stopScan();
```
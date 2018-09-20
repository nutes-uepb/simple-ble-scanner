# Simple BLE Connect
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/MIT)

Library for scanning and manipulation of devices with bluetooth low energy.
_**This version works for Android SDK equal to or higher than 21**_

## Features
* **Scanning** for BLE devices by name, UUID or MAC service.
* **Establish connection** to BLE devices _(not implemented)_
* **Perform pairing** with BLE devices _(not implemented)_
* **Read** feature data for a service _(not implemented)_
* **Write** data in characteristics of a service _(not implemented)_

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
	implementation 'com.github.User:Repo:Tag'
}
```

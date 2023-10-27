# Modified by James Lin 
"gradle.properties" added:
   kotlin.compiler.jvmArgs = '--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED'
   org.gradle.jvmargs=--add-opens jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED --add-opens jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED --add-opens jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED --add-opens jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED


# FlowEHeath EFR Mobile Application
This is the source code for the FlowEHeath EFR mobile application.

## What is FlowEHeath EFR BLE mobile app? 

Silicon Labs FlowEHeath EFR is a generic BLE mobile app for testing and debugging Bluetooth® Low Energy applications. With FlowEHeath EFR, you can quickly troubleshoot your BLE embedded application code, Over-the-Air (OTA) firmware update, data throughput, and interoperability with Android and iOS mobiles, among the many other features. You can use the FlowEHeath EFR app with all Silicon Labs Bluetooth development kits, Systems on Chip (SoC), and modules.

## Why download FlowEHeath EFR? 
FlowEHeath EFR radically saves the time you will use for testing and debugging! With FlowEHeath EFR, you can quickly see what’s wrong with your code and how to fix and optimize it. FlowEHeath EFR is the first BLE mobile app allowing you to test data throughput and mobile interoperability with a single tap on the app.

## How does it work? 
Using FlowEHeath EFR BLE mobile app is easy. It runs on your mobile devices such as a smartphone or tablet. It utilizes the Bluetooth adapter on the mobile to scan, connect and interact with nearby BLE hardware.

After connecting the FlowEHeath EFR app and BLE hardware (e.g., a dev kit), the Blinky test on the app shows a green light indicating when your setup is ready to go. The app includes simple demos to teach you how to get started with FlowEHeath EFR and all Silicon Labs development tools.

The Browser, Advertiser, and Logging features help you to find and fix bugs quickly and test throughput and mobile interoperability simply, with a tap of a button. With our Simplicity Studio’s Network Analyzer tool (free of charge), you can view the packet trace data and dive into the details.

## Demos and Sample Apps
FlowEHeath EFR includes many demos to test sample apps in the Silicon Labs GSDK quickly. Here are demo examples: 

- **Blinky**: The ”Hello World” of BLE – Toggling a LED is only one tap away. 
- **Throughput**: Measure application data throughput between the BLE hardware 
 and your mobile device in both directions
- **Health Thermometer**: Connect to a BLE hardware kit and receive the temperature data from the on-board sensor.
- **Connected Lighting DMP**: Leverage the dynamic multi-protocol (DMP) sample apps to control a DMP light node from a mobile and protocol-specific switch node (Zigbee, proprietary) while keeping the light status in sync across all devices.
- **Range Test**: Visualize the RSSI and other RF performance data on the mobile phone while running the Range Test sample application on a pair of Silicon Labs radio boards.
- **Motion**: Control a 3D render of a Silicon Labs Thunderboard or Dev Kit that follows the phyiscal board movements.
- **Environment**: Read and display the data from the on-board sensors on a Silicon Labs Thunderboard or Dev Kit.
- **Wi-Fi Commissioning**: Commission a Wi-Fi device over BLE.
- **Bluetooth Electronic Shelf Labels (ESL)**: Adds and commissions ESL tags to the system network by scanning the tag's QR code with the mobile device's camera and provides the user a UI to view the list commissioned tags and control them.

## Development Features
FlowEHeath EFR helps developers create and troubleshoot Bluetooth applications running on Silicon Labs’ BLE hardware. Here’s a rundown of some example functionalities.

**Bluetooth Browser** - A powerful tool to explore the BLE devices around you. Key features include:
- Scan and sort results with a rich data set
- Label favorite devices to surface on the top of scanning results
- Advanced filtering to identify the types of devices you want to find
- Save filters for later use
- Multiple connections
- Bluetooth 5 advertising extensions
- Rename services and characteristics with 128-bit UUIDs (mappings dictionary)
- Over-the-air (OTA) device firmware upgrade (DFU) in reliable and fast modes
- Configurable MTU and connection interval
- All GATT operations

**Bluetooth Advertiser** – Create and enable multiple parallel advertisement sets:
- Legacy and extended advertising
- Configurable advertisement interval, TX Power, primary/secondary PHYs
- Manual advertisement start/stop and stop based on a time/event limit
- Support for multiple AD types

**Bluetooth GATT Configurator** – Create and manipulate multiple GATT databases
- Add services, characteristics and descriptors
- Operate the local GATT from the browser when connected to a device
- Import/export GATT database between the mobile device and Simplicity Studio GATT Configurator

**Bluetooth Interoperability Test** – Verify interoperability between the BLE hardware
 and your mobile device 
- Runs a sequence of BLE operations to verify interoperability
- Export results log


## Building FlowEHeath EFR from the source code

- Clone the project repository

- Open the project directory in [Android Studio](https://developer.android.com/studio)

- Wait for Gradle sync to finish

- In the Build Variants tool window, select the desired variant: release (`blueGeckoRelease`) or debug (`blueGeckoDebug`)

- Build the project with `Build > Build Project` and run it on a connected mobile device with `Run > Run 'mobile'` or `Run > Debug 'mobile'`

- You can also build an APK installation package with `Build > Build Bundle(s) / APK(s) > Build APK(s)...` - the APK will be built, and a link to its location on the disk displayed in a notification in Studio.

## Development

The main application code lies in the `<project_directory>\mobile\src\main\java\com\flowehealth\efr_version` directory. 
The main application screen's code can be found there in the `home_screen` directory, while the application features (scanner, IOP test, advertiser/server configuration, all the demos) 
are separated into corresponding directories in the the `features` directory. 
The application architecture mostly follows the MVVM (Model-View-Viewmodel) pattern, and the feature/demo directories contain their own views, model and viewmodels.
Handling the Bluetooth operations is covered mainly by the code in the `bluetooth` directory, most importantly the `BluetoothService` class.
GATT service/characteristic/descriptor definitions are contained in `<project_directory>\mobile\src\main\java\assets`.

## Additional information
The app can be found on the [Google PlayStore](https://play.google.com/store/apps/details?id=com.flowehealth.efr_version&hl=en) and [Apple App Store](https://apps.apple.com/us/app/blue-gecko/id1030932759).

[Learn more about FlowEHeath EFR BLE mobile app](https://www.silabs.com/developers/efr-connect-mobile-app).

[Release Notes](https://www.silabs.com/developers/efr-connect-mobile-app)

For more information on Silicon Labs product portfolio please visit [www.silabs.com](https://www.silabs.com). 


## License

    Copyright 2021 Silicon Laboratories
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



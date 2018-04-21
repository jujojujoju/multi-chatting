# Multi-chatting

This program is a multi-chat program using Android applications.
Users are managed through Sign up, and users can send and receive text and files in a chat room where multiple users are connected at the same time.

## Function

* Server
    - TCP socket
    - Multithread
    - Message delivery

* Client
    - Create user profile (login and password creation and authentication).
    - Register to server and connect.
    - Retrieve chat history.
    - Check peer status (online, offline, busy).
    - Send and receive message.
## Requirement

> You need to have some libraries installed before development settings

__You should install Android SDK Version 15+__

* Server
    - mysql_connector_c++
    - jsoncpp

* Client
    - Gradle
    - Android device IceCreamSandwich(Android 4.0)+

## Development setting
#### Server

Move to Server Project
```sh
$ cd Server
```

Create Makefile
```sh
$ cmake .
```

Build
```sh
$ make
```
Run Server of Multichatting
```sh
$ ./multichatting
```

#### Client

Move to Client Project
```sh
$ cd Client
```

Uses project's gradle wrapper to execute clean and build task
```sh
$ ./gradlew clean build
$ ./gradlew :app:dependencies
```

## Run Application
* You can click __Run__ Button in Android Studio

#### Running on a Device
> Before you can run your application on a device, you must perform some basic setup for your device:

* Enable USB debugging on your device.
- On most devices running Android 3.2 or older, you can find the option under Settings > Applications > Development.
- On Android 4.0 and newer, it's in Settings > Developer options.
> Note: On Android 4.2 and newer, Developer options is hidden by default. To make it available, go to Settings > About phone and tap Build number seven times. Return to the previous screen to find Developer options.

* Ensure that your development computer can detect your device when connected via USB

Read Setting up a Device for Development for more information.

Once your device is set up and connected via USB, navigate to your SDK's platform-tools/ directory and install the .apk on the device:
```sh
$ adb -d install path/to/your/app.apk
```
The -d flag specifies that you want to use the attached device (in case you also have an emulator running).


#### Running on the Emulator
> Before you can run your application on the Android Emulator, you must create an AVD.

Please reference here :
[Android Emulator](https://stuff.mit.edu/afs/sipb/project/android/docs/tools/devices/emulator.html)

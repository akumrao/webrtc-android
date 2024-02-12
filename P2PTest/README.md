# Android Webrtc APP

## Enable USB debugger of Android

1. Enable Android setting, about and then tap version detail for 6 & 7 times till debug option is enable
2. Click USB Debugger


## login to linux and windows connect USB 

adb devices  

List of devices attached

* daemon started successfully

2447cae5	device

 adb -s  2447cae5 shell

 adb logcat -s "libjingle"

 adb uninstall adappt.ar.webrtccodelab

adb install app-debug.apk


# Browse 
https://192.168.0.19:1794/

add room


## To compile 

export PATH=/export/webrtc/depot_tools:$PATH
tools_webrtc/android/build_aar.py

gn gen out/Android --args='target_os="android" target_cpu="arm"'

ninja -C out/Android signaler






git clone https://chromium.googlesource.com/chromium/tools/depot_tools
export PATH=/export/webrtc/depot_tools:$PATH

mkdir andriodunix
cd andriodunix
fetch --nohooks webrtc_android.
Then type gclient sync

Image for post
After downloading sources you need to install required software for building libraries(.so and .jar). Enter to src directory and execute next script ./build/install-build-deps.sh
cd src/
 ./build/install-build-deps.sh
 ./build/install-build-deps-android.sh


/*Generate compilation script*/
src/
gn gen out/Debug --args='target_os="android" target_cpu="arm"'

gn gen out/arm64 --args='target_os="android" target_cpu="arm64"'

ninja -C out/arm64/
ninja -C out/arm64/  AppRTCMobile


Generate the project files:

build/android/gradle/generate_gradle.py --output-directory $PWD/out/arm64 \
--target "//examples:AppRTCMobile" --use-gradle-process-resources \
--split-projects 


This is tells for build system that we want .so library for arm architecture.Then type ninja -C out/Debug and wait...
Image for post
Also you can specify a directory of your own choice instead of out/Debug, to enable managing multiple configurations in parallel.
To build for ARM64: use target_cpu="arm64"
To build for 32-bit x86: use target_cpu="x86"
To build for 64-bit x64: use target_cpu="x64"
After successful compilation enter to out/Debug. You will see a lot of stuff, but we need only this things:
libjingle_peerconnection_so.so lib.java/webrtc/sdk/android/libjingle_peerconnection_java.jar
lib.java/webrtc/modules/audio_device/audio_device_java.jar
Step — 5: Add libraries to project
In your project in app folder create a folder and name it “jniLibs” then inside create a folder with name “armeabi-v7a”, put inside libjingle_peerconnection_so.so. Also you need to do some modifications to gradle file.
android {
    ...
    sourceSets {
        main {
            // let gradle pack the shared library into apk
            jniLibs.srcDirs = ['jniLibs']
        }
    }
}
Also you need to add lib.java/webrtc/sdk/android/libjingle_peerconnection_java.jar and lib.java/webrtc/modules/audio_device/audio_device_java.jar to your project.
In the next part I explain theory of WebRTC.
Thanks)))



echo "start copying jar files"
mkdir -p ../libs/armeabi-v7a/

cp out/Debug/lib.java/sdk/android/libjingle_peerconnection_java.jar ../libs/libjingle_peerconnection_java.jar 
cp out/Debug/lib.java/rtc_base/base_java.jar ../libs/base_java.jar 
cp out/Debug/gen/modules/audio_device/audio_device_java__compile_java.javac.jar ../libs/audio_device_java__compile_java.javac.jar
cp out/Debug/lib.java/examples/androidapp/third_party/autobanh/autobanh.jar ../libs/autobanh.jar

echo "start copying so files"

cp out/Debug/libjingle_peerconnection_so.so ../libs/armeabi-v7a/libjingle_peerconnection_so.so




echo "start copying jar files"
mkdir -p ../libs/arm64-v8a/

cp out/arm64/lib.java/sdk/android/libjingle_peerconnection_java.jar ../libs/libjingle_peerconnection_java.jar 
cp out/arm64/lib.java/rtc_base/base_java.jar ../libs/base_java.jar 
cp out/arm64/obj/modules/audio_device/audio_device_java.javac.jar ../libs/audio_device_java.javac.jar
cp out/arm64/lib.java/examples/androidapp/third_party/autobanh/autobanh.jar ../libs/autobanh.jar

echo "start copying so files"

cp out/arm64/libjingle_peerconnection_so.so ../libs/arm64-v8a/libjingle_peerconnection_so.so

## Similar links 

https://vivekc.xyz/getting-started-with-webrtc-for-android-daab1e268ff4

http://leadtosilverlining.blogspot.com/2018/04/how-to-build-android-webrtc-mobile-app.html

https://github.com/njovy/AppRTCDemo

http://webrtc.github.io/webrtc-org/native-code/android/

https://github.com/SD810/webrtc_example_android_app

https://github.com/ISBX/apprtc-node-server

https://github.com/Androidhacks7/AppRTC-Android

This is working example of WebRTC app from [official webrtc src](https://webrtc.googlesource.com/src/+/refs/heads/master/examples/androidapp/) which can be built with the latest Android Studio(3.6.3).

This app uses a dependency to latest webrtc Android library: org.webrtc:google-webrtc:1.0.32006


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




apt  install golang-go

git clone https://chromium.googlesource.com/chromium/tools/depot_tools

git checkout  remotes/origin/chrome/3904 or remotes/origin/chrome/3865  not sure 

https://chromium.googlesource.com/chromium/tools/depot_tools 


 export PATH=/export/webrtc/depot_tools:$PATH

 fetch --nohooks webrtc_android 

 git checkout branch-heads/m76

 gclient sync -D 



 cd /workspace/webrtc/src/sdk  


 apt install python


export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_131

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

export PATH=$JAVA_HOME/bin:$PATH







gn gen out/arm64 --args='target_os="android" target_cpu="arm64"'

ninja -C out/arm64/
ninja -C out/arm64/  AppRTCMobile



arvind  git@github.com:akumrao/webrtcwithsocketio.git (fetch)
arvind  git@github.com:akumrao/webrtcwithsocketio.git (push)
origin  https://webrtc.googlesource.com/src.git (fetch)



git remote add arvind git@github.com:akumrao/webrtcwithsocketio.git

git remote update

git checkout

1 * (HEAD detached at arvind/multiplex-video)

2 master cd src

./build/install-build-deps.sh

./build/install-build-deps-android.sh

/Generate compilation script/

src/

gn gen out/arm --args='target_os="android" target_cpu="arm"'

ninja -C out/arm/

cp ./out/arm/clang_x64/protoc ./out

delete our rm -rf out/arm

open android studio







 export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/


  cp ./out/arm64/clang_x64/protoc ./out then oly do following


 export PATH=$JAVA_HOME/bin:$PATH
./gradlew genWebrtcSrc

root@slt-pdte-lab01-41:/workspace/webrtc-android/P2PTest# ./gradlew genWebrtcSrc



`./gradlew genWebrtcSrc`, and "Refresh Linked C++ Projects" (note that "Sync Project with Gradle Files" won't work) before your build and debug, otherwise the generated sources may not be compiled, undefined reference error will happen, e.g. `webrtc::rtclog::Event` related references;






Incase if you need mulitplex then add socketiowithwebrtc with following branch 

   multiplex_codec_with_java -> arvind/multiplex_codec_with_java






# for future project  


https://juejin.cn/post/7221454955265556540  


mediasoup and webrtc android app


Compile mediasoup-client-android based on WebRTC m94 android version
Compile webrtc
Preparation


Compilation environment Ubuntu 18.04


webrtc needs to surf the Internet scientifically, and the proxy must be stable!!! The proxy must be stable!!! The proxy must be stable!!!


Install depot_tools (you need to use the depot_tools tool to download the webrtc source code)


bashCopy codegit clone https://chromium.googlesource.com/chromium/tools/depot_tools.git


Add depot_tools to the path and append the path of depot_tools to the .bashrc file in the user directory. Replace xxx with your user name.

bashCopy codeexport WEBRTC_DEPOT_TOOLS=/home/xxx/webRTC_Source/depot_tools
export PATH=$PATH:$WEBRTC_DEPOT_TOOLS


Make variables effective, command line execution
source ~/.bashrc 


Download source code

Download
 reference official website Development | WebRTC

bashCopy codemkdir webrtc_android
cd webrtc_android
fetch --nohooks webrtc_android
gclient sync

If fetch --nohooks webrtc_android fails to execute, just execute gclient sync.

Switch to stable branch
 I switched to m94 branch

cssCopy codecd src
git checkout -b m94 branch-heads/4606
/*同步代码*/
cd ..
gclient sync --nohooks
gclient runhooks


If there is an execution failure, which one of gclient sync --nohooks and gclient runhooks fails, re-execute it

Install the dependencies required to compile WebRTC

bashCopy codecd src
./build/install-build-deps.sh
./build/install-build-deps-android.sh

compile

compile aar
bashCopy code./tools_webrtc/android/build_aar.py  

./tools_webrtc/android/build_aar.py -h : Check what compilation parameters are available
Compile libwebrtc.a and libwebrtc.jar

The webrtc module will not be compiled by default, we need to add ':webrtc' to the /tools_webrtc/android/build_aar.py file

iniCopy code   TARGETS = [
    ':webrtc',
    'sdk/android:libwebrtc',
    'sdk/android:libjingle_peerconnection_so',
]


If you need to enable h264, you need to add licenses


Modify tools_webrtc/libs/generate_licenses.py as follows, add LICENSE

arduinoCopy code    'openh264':['third_party/openh264/src/LICENSE'],
    'ffmpeg':['third_party/ffmpeg/LICENSE.md'],

The location is as follows
bashCopy code'g722': ['modules/third_party/g722/LICENSE'],
'fft4g': ['common_audio/third_party/fft4g/LICENSE'],
'spl_sqrt_floor': ['common_audio/third_party/spl_sqrt_floor/LICENSE'],
+    'openh264':['third_party/openh264/src/LICENSE'],
+    'ffmpeg':['third_party/ffmpeg/LICENSE.md'],
# TODO(bugs.webrtc.org/1110): Remove this hack. This is not a lib.


Modify compilation ffmpeg configuration items

iniCopy codethird_party/ffmpeg/ffmpeg_generated.gni
-use_linux_config = is_linux || is_fuchsia
+use_linux_config = is_linux || is_fuchsia || is_android

arduinoCopy code third_party/ffmpeg/chromium/config/Chrome/android/{ABI}/config.h
-#define CONFIG_H264_DECODER 0
+#define CONFIG_H264_DECODER 1

arduinoCopy codethird_party/ffmpeg/chromium/config/Chrome/android/{ABI}/libavcodec/codec_list.c
// 增加 ff_h264_decoder
+    &ff_h264_decoder,
    NULL};

arduinoCopy codethird_party/ffmpeg/chromium/config/Chrome/android/{ABI}/libavcodec/parser_list.c
// 增加 ff_h264_parser
+    &ff_h264_parser,
     NULL};


Modify compilation OpenH264 configuration items

arduinoCopy codemodules/video_coding/codecs/h264/include/h264.h
 class RTC_EXPORT H264Encoder : public VideoEncoder {
  public:
+
+  static std::unique_ptr<H264Encoder> Create();


cCopy codemodules/video_coding/codecs/h264/h264.cc

std::vector<SdpVideoFormat> SupportedH264Codecs() {
                        "0")};
 }

+std::unique_ptr<H264Encoder> H264Encoder::Create() {
+#if defined(WEBRTC_USE_H264)
+RTC_LOG(LS_INFO) << "Creating H264EncoderImpl.";
+return std::make_unique<H264EncoderImpl>(cricket::VideoCodec("H264"));
+#else
+RTC_NOTREACHED();
+return nullptr;
+#endif
+}

scalaCopy code增加文件 sdk/android/api/org/webrtc/LibH264Decoder.java
+
+package org.webrtc;
+
+public class LibH264Decoder extends WrappedNativeVideoDecoder {
+  @Override
+  public long createNativeVideoDecoder() {
+    return nativeCreateDecoder();
+  }
+
+  static native long nativeCreateDecoder();
+}

javaCopy codesdk/android/api/org/webrtc/LibH264Encoder.java
+package org.webrtc;
+
+public class LibH264Encoder extends WrappedNativeVideoEncoder {
+  @Override
+  public long createNativeVideoEncoder() {
+    return nativeCreateEncoder();
+  }
+
+  static native long nativeCreateEncoder();
+
+  @Override
+  public boolean isHardwareEncoder() {
+    return false;
+  }
+}

arduinoCopy code增加  sdk/android/src/jni/h264_codec.cc
+#include <jni.h>
+#include "modules/video_coding/codecs/h264/include/h264.h"
+#include "sdk/android/generated_libH264_jni/LibH264Decoder_jni.h"
+#include "sdk/android/generated_libH264_jni/LibH264Encoder_jni.h"
+#include "sdk/android/src/jni/jni_helpers.h"
+namespace webrtc {
+namespace jni {
+static jlong JNI_LibH264Encoder_CreateEncoder(JNIEnv* jni) {
+  return jlongFromPointer(H264Encoder::Create().release());
+}
+static jlong JNI_LibH264Decoder_CreateDecoder(JNIEnv* jni) {
+  return jlongFromPointer(H264Decoder::Create().release());
+}
+}  // namespace jni


SoftwareVideoDecoderFactory.java Add LibH264Decoder
lessCopy codepublic class SoftwareVideoDecoderFactory implements VideoDecoderFactory {
   @Nullable
   @Override
   public VideoDecoder createDecoder(VideoCodecInfo codecType) {
+    Logging.d("VideoDecoder", "createDecoder: "+codecType.getName()+" params =  "+codecType.getParams());
+    if(codecType.getName().equalsIgnoreCase("H264")){
+      return new LibH264Decoder();
+    }
     if (codecType.getName().equalsIgnoreCase("VP8")) {
       return new LibvpxVp8Decoder();
     }
@@ -43,7 +47,7 @@ public class SoftwareVideoDecoderFactory implements VideoDecoderFactory {

   static VideoCodecInfo[] supportedCodecs() {
     List<VideoCodecInfo> codecs = new ArrayList<VideoCodecInfo>();
-
+    codecs.add(new VideoCodecInfo("H264", new HashMap<>()));
     codecs.add(new VideoCodecInfo("VP8", new HashMap<>()));
     if (LibvpxVp9Decoder.nativeIsSupported()) {
       codecs.add(new VideoCodecInfo("VP9", new HashMap<>()));

SoftwareVideoEncoderFactory.java Add LibH264Encoder
typescriptCopy codepublic class SoftwareVideoEncoderFactory implements VideoEncoderFactory {
   @Nullable
   @Override
   public VideoEncoder createEncoder(VideoCodecInfo info) {
+    Logging.d("VideoEncoder", "createEncoder: "+info.getName()+" params =  "+info.getParams());
+    if (info.name.equalsIgnoreCase("H264")) {
+      return new LibH264Encoder();
+    }
     if (info.name.equalsIgnoreCase("VP8")) {
       return new LibvpxVp8Encoder();
     }
@@ -36,7 +40,7 @@ public class SoftwareVideoEncoderFactory implements VideoEncoderFactory {

   static VideoCodecInfo[] supportedCodecs() {
     List<VideoCodecInfo> codecs = new ArrayList<VideoCodecInfo>();
-
+    codecs.add(new VideoCodecInfo("H264", new HashMap<>()));
     codecs.add(new VideoCodecInfo("VP8", new HashMap<>()));
     if (LibvpxVp9Encoder.nativeIsSupported()) {
       codecs.add(new VideoCodecInfo("VP9", new HashMap<>()));


diffCopy codesdk/android/BUILD.gn
--- a/sdk/android/BUILD.gn
+++ b/sdk/android/BUILD.gn
@@ -46,6 +46,7 @@ if (is_android) {
       ":libjingle_peerconnection_java",
       ":libjingle_peerconnection_metrics_default_java",
       ":libvpx_vp8_java",
+      ":libH264_java",
       ":libvpx_vp9_java",
       ":logging_java",
       ":peerconnection_java",
@@ -489,6 +490,20 @@ if (is_android) {
     ]
   }

+   rtc_android_library("libH264_java") {
+     visibility = [ "*" ]
+     sources = [
+       "api/org/webrtc/LibH264Decoder.java",
+       "api/org/webrtc/LibH264Encoder.java",
+     ]
+     deps = [
+       ":base_java",
+       ":video_api_java",
+       ":video_java",
+       "//rtc_base:base_java",
+     ]
+   }
+
   rtc_android_library("libvpx_vp9_java") {
     visibility = [ "*" ]
     sources = [
@@ -512,6 +527,7 @@ if (is_android) {

     deps = [
       ":base_java",
+      ":libH264_java",
       ":libvpx_vp8_java",
       ":libvpx_vp9_java",
+       ":video_api_java",
+       ":video_java",
+       "//rtc_base:base_java",
+     ]
+   }
+
   rtc_android_library("libvpx_vp9_java") {
     visibility = [ "*" ]
     sources = [
@@ -512,6 +527,7 @@ if (is_android) {

     deps = [
       ":base_java",
+      ":libH264_java",
       ":libvpx_vp8_java",
       ":libvpx_vp9_java",
       ":video_api_java",
@@ -783,6 +799,18 @@ if (current_os == "linux" || is_android) {
     ]
   }

+  rtc_library("libH264_jni") {
+     visibility = [ "*" ]
+     allow_poison = [ "software_video_codecs" ]
+     sources = [ "src/jni/h264_codec.cc" ]
+     deps = [
+       ":base_jni",
+       ":generated_libH264_jni",
+       ":video_jni",
+       "../../modules/video_coding:webrtc_h264",
+     ]
+   }
+
   rtc_library("libvpx_vp9_jni") {
     visibility = [ "*" ]
     allow_poison = [ "software_video_codecs" ]
@@ -799,6 +827,7 @@ if (current_os == "linux" || is_android) {
     visibility = [ "*" ]
     allow_poison = [ "software_video_codecs" ]
     deps = [
+      ":libH264_jni",
       ":libvpx_vp8_jni",
       ":libvpx_vp9_jni",
     ]
@@ -1203,6 +1232,16 @@ if (current_os == "linux" || is_android) {
     jni_generator_include = "//sdk/android/src/jni/jni_generator_helper.h"
   }

+   generate_jni("generated_libH264_jni") {
+     sources = [
+       "api/org/webrtc/LibH264Decoder.java",
+       "api/org/webrtc/LibH264Encoder.java",
+     ]
+
+     namespace = "webrtc::jni"
+     jni_generator_include = "//sdk/android/src/jni/jni_generator_helper.h"
+   }
+



If you need to support h264 simulcast resolution is not supported, you need to modify the file modules/video_coding/utility/simulcast_utility.cc

arduinoCopy codebool SimulcastUtility::ValidSimulcastParameters(const VideoCodec& codec,
   } else {
     // TODO(mirtad): H264 encoder implementation still assumes the default
     // resolution downscaling is used.
-    for (int i = 1; i < num_streams; ++i) {
-      if (codec.simulcastStream[i].width !=
-          codec.simulcastStream[i - 1].width * 2) {
-        return false;
-      }
-    }
+//    for (int i = 1; i < num_streams; ++i) {
+//      if (codec.simulcastStream[i].width !=
+//          codec.simulcastStream[i - 1].width * 2) {
+//        return false;
+//      }
+//    }
   }




Compilation command

iniCopy code./tools_webrtc/android/build_aar.py --extra-gn-args 'is_debug=false is_component_build=false is_clang=true rtc_include_tests=false rtc_use_h264 = true ffmpeg_branding = "Chrome" rtc_enable_protobuf=false use_rtti=true use_custom_libcxx=false' --build-dir ./out/release-build/m94/




Problems you may encounter

vbnetCopy codeModuleNotFoundError: No module named 'dataclasses'

missing python dependency
arduinoCopy code//python2
  sudo apt install python-pip
  pip install dataclasses

//python3
  sudo apt install python3-pip
  pip3 install dataclasses


Output path

bashCopy code out/release-build/m94/armeabi-v7a/obj/libwebrtc.a
 out/release-build/m94/armeabi-v7a/lib.java/sdk/android/libwebrtc.jar


At this point, WebRTC compilation is completed, copy libwebrtc.a libwebrtc.jar for later use

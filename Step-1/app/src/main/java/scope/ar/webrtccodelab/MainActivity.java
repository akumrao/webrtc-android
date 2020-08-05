package scope.ar.webrtccodelab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.VideoSink;
import org.webrtc.VideoFrame;
import org.webrtc.SurfaceTextureHelper;

//import org.webrtc.VideoRenderer;

//import android.support.test.InstrumentationRegistry;
//import android.support.test.filters.SmallTest;
//import android.support.test.runner.AndroidJUnit4;
import java.util.ArrayList;
//import org.junit.Test;
//import org.junit.runner.RunWith;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.Logging.Severity;
import org.webrtc.Loggable;

import java.util.logging.Level;
import java.util.logging.Logger;





public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    //private static String TAG = "LoggableTest";
    private static String NATIVE_FILENAME_TAG = "loggable_test.cc";

    private final static Logger LOGGER =  Logger.getLogger("arvind");  //Logger.GLOBAL_LOGGER_NAME

    private static class MockLoggable implements Loggable {
        private ArrayList<String> messages = new ArrayList<>();
        private ArrayList<Severity> sevs = new ArrayList<>();
        private ArrayList<String> tags = new ArrayList<>();

        @Override
        public void onLogMessage(String message, Severity sev, String tag) {
           // messages.add(message);
            sevs.add(sev);
            tags.add(tag);
            LOGGER.log(Level.INFO, tag+ ":" + message);
        }

//        public boolean isMessageReceived(String message) {
//            for (int i = 0; i < messages.size(); i++) {
//                if (messages.get(i).contains(message)) {
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        public boolean isMessageReceived(String message, Severity sev, String tag) {
//            for (int i = 0; i < messages.size(); i++) {
//                if (messages.get(i).contains(message) && sevs.get(i) == sev && tags.get(i).equals(tag)) {
//                    return true;
//                }
//            }
//            return false;
//        }

    }
        private final MockLoggable mockLoggable = new MockLoggable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);

        //Initialize PeerConnectionFactory globals.
        //Params are context, initAudio,initVideo and videoCodecHwAcceleration
        //PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        //PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions());

        PeerConnectionFactory.initialize(
                org.webrtc.PeerConnectionFactory.InitializationOptions.builder(this)
                        .setInjectableLogger(mockLoggable, Logging.Severity.LS_VERBOSE)
                        .setEnableInternalTracer(false)
                        .createInitializationOptions());

        org.webrtc.PeerConnectionFactory.Options options = new org.webrtc.PeerConnectionFactory.Options();




        //Create a new PeerConnectionFactory instance.
        //PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        PeerConnectionFactory peerConnectionFactory = PeerConnectionFactory.builder().setOptions(options).createPeerConnectionFactory();


        //Now create a VideoCapturer instance. Callback methods are there if you want to do something! Duh!
        VideoCapturer videoCapturerAndroid = createVideoCapturer();
        //Create MediaConstraints - Will be useful for specifying video and audio constraints. More on this later!
        MediaConstraints constraints = new MediaConstraints();

        //Create a VideoSource instance
        VideoSource videoSource = peerConnectionFactory.createVideoSource(false);
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);

        //create an AudioSource instance
        AudioSource audioSource = peerConnectionFactory.createAudioSource(constraints);
        AudioTrack localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);

        //arvind

        EglBase rootEglBase = EglBase.create();

        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());


        videoCapturerAndroid.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());


        //we will start capturing the video from the camera
        //width,height and fps

        videoCapturerAndroid.startCapture(1000, 1000, 30);

        //create surface renderer, init it and add the renderer to the track
        SurfaceViewRenderer videoView = (SurfaceViewRenderer) findViewById(R.id.surface_rendeer);
        videoView.setMirror(true);


        videoView.init(rootEglBase.getEglBaseContext(), null);


        //localVideoTrack.addRenderer(new VideoRenderer(videoView));

        ProxyVideoSink localVideoSink = new ProxyVideoSink();
        localVideoTrack.addSink(localVideoSink);
        localVideoSink.setTarget(videoView);



    }


    private static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                Logging.d("TAG", "Dropping frame in proxy because target is null.");
                return;
            }

            target.onFrame(frame);
        }

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }


    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        Logging.d(TAG, "Creating capturer using camera1 API.");
        videoCapturer = createCameraCapturer(new Camera1Enumerator(false));

        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }
}

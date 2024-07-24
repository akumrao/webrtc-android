package scope.ar.webrtccodelab;

import android.media.MediaCodecInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
//import org.webrtc.MultiplexVideoDecoderFactory;
//import org.webrtc.MultiplexVideoEncoderFactory;
//import org.webrtc.VideoCodecInfo;
//import org.webrtc.VideoDecoderFactory;
//import org.webrtc.VideoEncoderFactory;
//import org.webrtc.BuiltinAudioEncoderFactoryFactory;


import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.HardwareVideoDecoderFactory;
import org.webrtc.HardwareVideoEncoderFactory;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;

import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
//import org.webrtc.VideoCapturerAndroid;
//import org.webrtc.VideoRenderer;
import org.webrtc.VideoCodecInfo;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.Logging;


import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;

import org.webrtc.VideoSink;
import org.webrtc.VideoFrame;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.webrtc.Logging.Severity;
import org.webrtc.Loggable;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    PeerConnectionFactory peerConnectionFactory;
    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    MediaConstraints sdpConstraints;
    VideoSource videoSource;
    VideoTrack localVideoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;

    SurfaceViewRenderer localVideoView;
    SurfaceViewRenderer remoteVideoView;

    SurfaceTextureHelper surfaceTextureHelper;
    EglBase rootEglBase;
    private static final String TAG = "MainActivity";


    //VideoRenderer localRenderer;
   // VideoRenderer remoteRenderer;

    PeerConnection localPeer, remotePeer;
    Button start, call, hangup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initVideos();
    }


    private void initViews() {
        start = (Button) findViewById(R.id.start_call);
        call = (Button) findViewById(R.id.init_call);
        hangup = (Button) findViewById(R.id.end_call);
        localVideoView = (SurfaceViewRenderer) findViewById(R.id.local_gl_surface_view);
        remoteVideoView = (SurfaceViewRenderer) findViewById(R.id.remote_gl_surface_view);

        start.setOnClickListener(this);
        call.setOnClickListener(this);
        hangup.setOnClickListener(this);
    }

    private void initVideos() {
        rootEglBase = EglBase.create();
        localVideoView.init(rootEglBase.getEglBaseContext(), null);
        remoteVideoView.init(rootEglBase.getEglBaseContext(), null);
        localVideoView.setZOrderMediaOverlay(true);
        remoteVideoView.setZOrderMediaOverlay(true);
    }


    // Cycle through likely device names for the camera and return the first
    // capturer that works, or crash if none do.
//    private VideoCapturer getVideoCapturer(CameraVideoCapturer.CameraEventsHandler eventsHandler) {
//        String[] cameraFacing = {"front", "back"};
//        int[] cameraIndex = {0, 1};
//        int[] cameraOrientation = {0, 90, 180, 270};
//        for (String facing : cameraFacing) {
//            for (int index : cameraIndex) {
//                for (int orientation : cameraOrientation) {
//                    String name = "Camera " + index + ", Facing " + facing +
//                            ", Orientation " + orientation;
//                    VideoCapturer capturer = VideoCapturerAndroid.create(name, eventsHandler);
//                    if (capturer != null) {
//                        Log.d("Using camera: ", name);
//                        return capturer;
//                    }
//                }
//            }
//        }
//        throw new RuntimeException("Failed to open capture");
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_call: {
                start();
                break;
            }
            case R.id.init_call: {
                call();
                break;
            }
            case R.id.end_call: {
                hangup();
                break;
            }
        }
    }

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

    private static final boolean ENABLE_INTEL_VP8_ENCODER = false;
    private static final boolean ENABLE_H264_HIGH_PROFILE = false;

    public void start() {
        start.setEnabled(false);
        call.setEnabled(true);
        //Initialize PeerConnectionFactory globals.
        //Params are context, initAudio,initVideo and videoCodecHwAcceleration
//        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
//
//        //Create a new PeerConnectionFactory instance.
//        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
//        peerConnectionFactory = new PeerConnectionFactory(options);

        //Initialize PeerConnectionFactory globals.
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.









       // DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(
      //          rootEglBase.getEglBaseContext(),  /* enableIntelVp8Encoder */true,  /* enableH264HighProfile */true);

        //DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
//        // arvind
    //    MultiplexVideoEncoderFactory mVideoEncoderFactory = new MultiplexVideoEncoderFactory(defaultVideoEncoderFactory);
     //  MultiplexVideoDecoderFactory mtVideoDecoderFactory = new MultiplexVideoDecoderFactory(defaultVideoEncoderFactory);
        //arvind

   //      ArrayList<MediaCodecInfo> codecs = new ArrayList<>();




//        VideoEncoderFactory encoderFactory = new MultiplexVideoEncoderFactory(
//                rootEglBase.getEglBaseContext(), ENABLE_H264_HIGH_PROFILE);
//
//        VideoDecoderFactory decoderFactory = new MultiplexVideoDecoderFactory(rootEglBase.getEglBaseContext());




        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(),  /* enableIntelVp8Encoder */true,  /* enableH264HighProfile */true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();



//
//        peerConnectionFactory = PeerConnectionFactory.builder()
//                .setOptions(options)
//                .setVideoEncoderFactory(encoderFactory)
//                .setVideoDecoderFactory(decoderFactory)
//                .createPeerConnectionFactory();


        //Now create a VideoCapturer instance. Callback methods are there if you want to do something! Duh!
        VideoCapturer videoCapturerAndroid;
        videoCapturerAndroid = createCameraCapturer(new Camera1Enumerator(false));




        //Create MediaConstraints - Will be useful for specifying video and audio constraints.
        audioConstraints = new MediaConstraints();
        videoConstraints = new MediaConstraints();

        //Create a VideoSource instance
        //Create a VideoSource instance
        if (videoCapturerAndroid != null) {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
            videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid.isScreencast());
            videoCapturerAndroid.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
        }
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);

        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
        localVideoView.setVisibility(View.VISIBLE);

        //create a videoRenderer based on SurfaceViewRenderer instance
        if (videoCapturerAndroid != null) {
            videoCapturerAndroid.startCapture(1024, 720, 30);
        }

        localVideoView.setVisibility(View.VISIBLE);
        // And finally, with our VideoRenderer ready, we
        // can add our renderer to the VideoTrack.

       // localVideoTrack.addSink(localVideoView);

        ProxyVideoSink localVideoSink = new ProxyVideoSink();
        localVideoTrack.addSink(localVideoSink);
        localVideoSink.setTarget(localVideoView);


        localVideoView.setMirror(true);
        remoteVideoView.setMirror(true);


    }


    private static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                Logging.d("TAG", "Dropping frame in proxy because target is null.");
                return;
            }

            Logging.d("TAG", " w=" + Integer.toString(frame.getBuffer().getWidth()) + " h="  + Integer.toString(frame.getBuffer().getHeight())        );


            target.onFrame(frame);
        }

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
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
    private void call() {
        start.setEnabled(false);
        call.setEnabled(false);
        hangup.setEnabled(true);
        //we already have video and audio tracks. Now create peerconnections
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();


        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(iceServers);
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        //creating localPeer

        //create sdpConstraints
        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiveVideo", "true"));



        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver("localPeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                onIceCandidateReceived(localPeer, iceCandidate);
            }
        });

        //creating remotePeer
        remotePeer = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver("remotePeerCreation") {

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                onIceCandidateReceived(remotePeer, iceCandidate);
            }

            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                gotRemoteStream(mediaStream);
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                super.onIceGatheringChange(iceGatheringState);

            }
        });

        //creating local mediastream
        MediaStream stream = peerConnectionFactory.createLocalMediaStream("102");
        stream.addTrack(localAudioTrack);
        stream.addTrack(localVideoTrack);
        localPeer.addStream(stream);

        //creating Offer
        localPeer.createOffer(new CustomSdpObserver("localCreateOffer"){
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                //we have localOffer. Set it as local desc for localpeer and remote desc for remote peer.
                //try to create answer from the remote peer.
                super.onCreateSuccess(sessionDescription);
                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocalDesc"), sessionDescription);
                remotePeer.setRemoteDescription(new CustomSdpObserver("remoteSetRemoteDesc"), sessionDescription);
                remotePeer.createAnswer(new CustomSdpObserver("remoteCreateOffer") {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        //remote answer generated. Now set it as local desc for remote peer and remote desc for local peer.
                        super.onCreateSuccess(sessionDescription);
                        remotePeer.setLocalDescription(new CustomSdpObserver("remoteSetLocalDesc"), sessionDescription);
                        localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemoteDesc"), sessionDescription);

                    }
                },new MediaConstraints());
            }
        },sdpConstraints);
    }


    private void hangup() {
        localPeer.close();
        remotePeer.close();
        localPeer = null;
        remotePeer = null;
        start.setEnabled(true);
        call.setEnabled(false);
        hangup.setEnabled(false);
    }


    /**
     * Received remote peer's media stream. we will get the first video track and render it
     */
    private void gotRemoteStream(MediaStream stream) {
        //we have remote video stream. add to the renderer.
        final VideoTrack videoTrack = stream.videoTracks.get(0);
        runOnUiThread(() -> {
            try {
                remoteVideoView.setVisibility(View.VISIBLE);
                videoTrack.addSink(remoteVideoView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void onIceCandidateReceived(PeerConnection peer, IceCandidate iceCandidate) {
        //we have received ice candidate. We can set it to the other peer.
        if (peer == localPeer) {
            remotePeer.addIceCandidate(iceCandidate);
        } else {
            localPeer.addIceCandidate(iceCandidate);
        }
    }



}

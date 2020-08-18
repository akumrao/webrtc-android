package scope.ar.webrtccodelab;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;

public class HTTPSignalling {

    private static final String TAG = "HTTPSignalling";

    private  String localUserId;
    private  String remoteUserId;

    private SignalingInterface callback;
    private static HTTPSignalling instance;

//    HTTPSignalling(SignalingInterface signalingInterface, String localUserId, String remoteUserId )
//    {
//        this.localUserId = localUserId;
//        this.remoteUserId = remoteUserId;
//
//        this.callback = signalingInterface;
//    }

    public void init(SignalingInterface signalingInterface, String localUserId, String remoteUserId) {
        this.callback = signalingInterface;

        this.localUserId = localUserId;
        this.remoteUserId = remoteUserId;

        startCapture(352 , 288, 1);

    }

    public static HTTPSignalling getInstance() {
        if (instance == null) {
            instance = new HTTPSignalling();
        }
//        if (instance.roomName == null) {
//            //set the room name here
//            instance.roomName = "room1";
//        }
        return instance;
    }


    public class HTTPGet extends AsyncTask<String, Void, String> {

        private String ret;

        public HTTPGet(String textView) {
            this.ret = textView;
        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

                int code = urlConnection.getResponseCode();
                StringBuilder builder = new StringBuilder();
                if (code == 200) {

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));


                    String inputString;
                    while ((inputString = bufferedReader.readLine()) != null) {
                        builder.append(inputString);
                    }

                    return  builder.toString();

                }
                urlConnection.disconnect();
            } catch (IOException e) {
               // e.printStackTrace();
              //  Log.e(TAG, e.toString());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String temp) {

            Log.e(TAG, temp);

            if(!temp.isEmpty()) {

                try {
                    JSONObject topLevel = new JSONObject(temp);

                    int type = topLevel.optInt( "MessageType");

                    String data = topLevel.optString("Data");

                    Log.e( TAG, "Received SDP message: type=" + type);
                    switch (type)
                    {
                        case 1:
                            callback.onOfferReceived(data);
                            // if we get an offer, we immediately send an answer
                            break;
                        case 2:
                            //connectionWrangler.SetRemoteDescription("answer", msg.Data);
                            callback.onAnswerReceived(data);
                            break;
                        case 3:
                            // this "parts" protocol is defined above, in OnIceCandiateReadyToSend listener
                            //var parts = msg.Data.Split(new string[] { msg.IceDataSeparator }, StringSplitOptions.RemoveEmptyEntries);
                            //connectionWrangler.AddIceCandidate(parts[0], int.Parse(parts[1]), parts[2]);
                            String sep = topLevel.optString("IceDataSeparator");
                            String[] separated = data.split("[" + sep + "]");

                            callback.onIceCandidateReceived(separated);

                            break;
                        default:
                            Log.e( TAG, "Unknown message: " +  temp);
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }




    public class HTTPpost extends AsyncTask<String, Void, String> {


        public HTTPpost() {

        }


        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

               // InputStream stream = new BufferedInputStream(urlConnection.getInputStream());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        out, "UTF-8"));
                writer.write(strings[1]);
                writer.flush();

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
        //}

//        @Override
//        protected void onPostExecute(String temp) {
//            textView = temp;
//
//            Log.e(TAG, textView);
//        }

    }



    private final Timer timer = new Timer();

    private final TimerTask tickTask = new TimerTask() {
        @Override
        public void run() {
            tick();
        }
    };


    public void tick() {

        String units = "/data/" + localUserId;
        String url = String.format("http://192.168.0.16:3000%s",units);

        String textView = "test";
        new HTTPGet(textView).execute(url);

    }


    public void post( String msg) {

        String units = "/data/" + remoteUserId;
        String url = String.format("http://192.168.0.16:3000%s",units);


        new HTTPpost().execute(url, msg);
    }

    public void emitMessage(SessionDescription message) {
        try {
            Log.i("SignallingClient", "emitMessage() called with: message = [" + message + "]");
            JSONObject obj = new JSONObject();

            int msgType=9;
            if (  message.type.canonicalForm().equals("offer"))
            {
                msgType = 1;
            }
            else if (  message.type.canonicalForm().equals("answer"))
            {
                msgType = 2;
            }
            else{
                Log.e("TAG", "Invalid state, this conditon should not come");
            }


            obj.put("MessageType", msgType);
            obj.put("Data", message.description);
            Log.i("emitMessage", obj.toString());
            post( obj.toString() );
            Log.i("room194", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void emitIceCandidate(IceCandidate iceCandidate) {
        try {

            String Data = iceCandidate.sdp + "|" +  Integer.toString(iceCandidate.sdpMLineIndex)  + "|" + iceCandidate.sdpMid;

            JSONObject object = new JSONObject();
            object.put("MessageType", 3);
            object.put("Data", Data);
            object.put("IceDataSeparator",  "|");
            post( object.toString() );
            //socket.emit("message", object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startCapture(int width, int height, int framerate) {
        timer.schedule(tickTask, 0, 1000 / framerate);
    }

    public void stopCapture() throws InterruptedException {
        timer.cancel();
    }


    public void close() {
        timer.cancel();
    }

    interface SignalingInterface {
        void onRemoteHangUp(String msg);

        void onOfferReceived(String data);

        void onAnswerReceived(String data);

        void onIceCandidateReceived( String[] data);

        void onTryToStart();

        void onCreatedRoom();

        void onJoinedRoom();

        void onNewPeerJoined();
    }
}
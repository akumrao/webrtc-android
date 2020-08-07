package scope.ar.webrtccodelab;

import android.util.Log;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

/**
 * Webrtc_Step2
 * Created by arvind
 */

class CustomSdpObserver implements SdpObserver {


    private String tag;

    CustomSdpObserver(String logTag) {
        tag = this.getClass().getCanonicalName();
        this.tag = this.tag + " " + logTag;
    }


    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        Log.d(tag, sessionDescription.description);
    }

    @Override
    public void onSetSuccess() {
        Log.d(tag, "onSetSuccess() called");
    }

    @Override
    public void onCreateFailure(String s) {
        Log.d(tag, "onCreateFailure() called with: s = [" + s + "]");
    }

    @Override
    public void onSetFailure(String s) {
        Log.d(tag, "onSetFailure() called with: s = [" + s + "]");
    }

}

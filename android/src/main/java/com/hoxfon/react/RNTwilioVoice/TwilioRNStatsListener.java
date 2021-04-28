package com.hoxfon.react.RNTwilioVoice;

import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.twilio.voice.*;

import java.util.List;

import static com.hoxfon.react.RNTwilioVoice.EventManager.ON_STATS_RECEIVED;

public class TwilioRNStatsListener implements StatsListener {

    EventManager eventManager;

    TwilioRNStatsListener(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void onStats(@NonNull List<StatsReport> statsReports) {

        WritableMap event = new WritableNativeMap();
        for (StatsReport sr : statsReports) {
            WritableMap connectionStats = new WritableNativeMap();
            WritableArray as = new WritableNativeArray();
            for (RemoteAudioTrackStats s : sr.getRemoteAudioTrackStats()) {
                as.pushMap(convertAudioTrackStats(s));
            }
            connectionStats.putArray("remoteAudioTrackStats", as);

            WritableArray vs = new WritableNativeArray();

            WritableArray las = new WritableNativeArray();
            for (LocalAudioTrackStats s : sr.getLocalAudioTrackStats()) {
                las.pushMap(convertLocalAudioTrackStats(s));
            }
            connectionStats.putArray("localAudioTrackStats", las);

            event.putMap(sr.getPeerConnectionId(), connectionStats);
        }

        if(eventManager != null) {
            eventManager.sendEvent(ON_STATS_RECEIVED, event);
            Log.d(TwilioVoiceModule.TAG, "Sending OnStatsReceived Event to RNBridge");
        }
    }

    private WritableMap convertAudioTrackStats(RemoteAudioTrackStats as) {
        WritableMap result = new WritableNativeMap();
        result.putInt("audioLevel", as.audioLevel);
        result.putInt("jitter", as.jitter);
        convertBaseTrackStats(as, result);
        convertRemoteTrackStats(as, result);
        return result;
    }

    private void convertRemoteTrackStats(RemoteTrackStats ts, WritableMap result) {
        result.putDouble("bytesReceived", ts.bytesReceived);
        result.putInt("packetsReceived", ts.packetsReceived);
    }

    private WritableMap convertLocalAudioTrackStats(LocalAudioTrackStats as) {
        WritableMap result = new WritableNativeMap();
        result.putInt("audioLevel", as.audioLevel);
        result.putInt("jitter", as.jitter);
        convertBaseTrackStats(as, result);
        convertLocalTrackStats(as, result);
        return result;
    }

    private void convertBaseTrackStats(BaseTrackStats bs, WritableMap result) {
        result.putString("codec", bs.codec);
        result.putInt("packetsLost", bs.packetsLost);
        result.putString("ssrc", bs.ssrc);
        result.putDouble("timestamp", bs.timestamp);
    }

    private void convertLocalTrackStats(LocalTrackStats ts, WritableMap result) {
        result.putDouble("bytesSent", ts.bytesSent);
        result.putInt("packetsSent", ts.packetsSent);
        result.putDouble("roundTripTime", ts.roundTripTime);
    }

}

package com.hoxfon.react.RNTwilioVoice;

import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.WritableMap;
import com.twilio.voice.StatsListener;
import com.twilio.voice.StatsReport;

import java.util.List;

import static com.hoxfon.react.RNTwilioVoice.EventManager.ON_STATS_RECEIVED;

public class TwilioRNStatsListener implements StatsListener {

    EventManager eventManager;

    TwilioRNStatsListener(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void onStats(@NonNull List<StatsReport> statsReports) {

        WritableMap event =  TwilioRNStatsManager.getStats(statsReports);
        if(eventManager != null) {
            eventManager.sendEvent(ON_STATS_RECEIVED, event);
            Log.d(TwilioVoiceModule.TAG, "Sending OnStatsReceived Event to RNBridge");
        }
    }
}

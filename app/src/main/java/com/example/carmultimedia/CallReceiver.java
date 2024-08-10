package com.example.carmultimedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

    private static CallStateListener listener;
    private String lastIncomingNumber;

    public static void setCallStateListener(CallStateListener listener) {
        CallReceiver.listener = listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CallReceiver", "onReceive called with intent: " + intent);
        if (intent != null && intent.getAction() != null) {
            Log.d("CallReceiver", "Intent Action: " + intent.getAction());

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            // Log the current state and incoming number
            Log.d("CallReceiver", "State: " + state + ", Incoming Number: " + incomingNumber);

            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                lastIncomingNumber = incomingNumber;
                Log.d("CallReceiver", "RINGING state, Incoming Number: " + incomingNumber);
            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                if (incomingNumber == null) {
                    incomingNumber = lastIncomingNumber;
                }
                Log.d("CallReceiver", "OFFHOOK state, Using last Incoming Number: " + incomingNumber);
            }

            if (state != null && listener != null) {
                Log.d("CallReceiver", "Hereee");
                listener.onCallStateChanged(state, incomingNumber);
            } else if (state == null) {
                Log.e("CallReceiver", "Call state is null.");
            }
        } else {
            Log.e("CallReceiver", "Intent or Action is null.");
        }
    }


}

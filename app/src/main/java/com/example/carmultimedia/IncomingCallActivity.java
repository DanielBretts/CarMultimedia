package com.example.carmultimedia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carmultimedia.databinding.ActivityIncomingCallBinding;
import com.example.carmultimedia.databinding.ActivityMainBinding;

public class IncomingCallActivity extends AppCompatActivity {
    private ActivityIncomingCallBinding binding;
    private Chronometer callDuration;
    private TextView callInfo;
    private TelecomManager telecomManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomingCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        callDuration = findViewById(R.id.callDuration);
        callInfo = findViewById(R.id.callInfo);

        String incomingNumber = getIntent().getStringExtra("INCOMING_NUMBER");
        callInfo.setText("Incoming call from: " + incomingNumber);

        binding.answerButton.setOnClickListener(v -> answerCall());
        binding.hangupButton.setOnClickListener(v -> hangupCall());
    }

    private void answerCall() {
        // Implement answer call logic
        callDuration.setVisibility(View.VISIBLE);
        callDuration.setBase(SystemClock.elapsedRealtime());
        callDuration.start();
        binding.answerButton.setEnabled(false);
        binding.hangupButton.setEnabled(true);
        callInfo.setText("Call in progress...");
        if (telecomManager != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                telecomManager.acceptRingingCall();
                Toast.makeText(this, "Call answered", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to answer call", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hangupCall() {
        callDuration.stop();
        callInfo.setText("Call ended.");
        binding.hangupButton.setEnabled(false);
        if (telecomManager != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    telecomManager.endCall();
                }
                Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show();
                handleIncomingCall(TelephonyManager.EXTRA_STATE_IDLE, null);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to end call", Toast.LENGTH_SHORT).show();
            }
        }
        finish();  // Close the activity after hanging up
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void handleIncomingCall(String state, String incomingNumber) {
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            Intent intent = new Intent(this, IncomingCallActivity.class);
            intent.putExtra("INCOMING_NUMBER", incomingNumber);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            // You could send a broadcast or use another method to update the UI in the IncomingCallActivity
            // For example, send a broadcast to notify that the call is in progress
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            // Send a broadcast or intent to notify that the call has ended
        }
    }
}

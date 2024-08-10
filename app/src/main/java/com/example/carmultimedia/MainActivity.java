package com.example.carmultimedia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.carmultimedia.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements CallStateListener {
    private static final int REQUEST_PERMISSION_CODE = 100;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.radioButton.setOnClickListener(view -> openRadioActivity());

        // Check and request permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PERMISSION_CODE);
        }
            CallReceiver.setCallStateListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                // Permission denied, show a message and possibly close the app
                Toast.makeText(this, "Permissions are required to handle calls.", Toast.LENGTH_SHORT).show();
                finish(); // Close the app if permissions are critical
            }
        }
    }

//    public void openNavActivity(View view) {
//        Intent intent = new Intent(this, NavActivity.class);
//        startActivity(intent);
//    }
//
//    public void openMusicActivity(View view) {
//        Intent intent = new Intent(this, MusicActivity.class);
//        startActivity(intent);
//    }

    public void openRadioActivity() {
        Intent intent = new Intent(this, RadioActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCallStateChanged(String state, String incomingNumber) {
        handleIncomingCall(state, incomingNumber);
    }

    // Method to handle incoming call states
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


//    public void openPhoneActivity(View view) {
//        Intent intent = new Intent(this, PhoneActivity.class);
//        startActivity(intent);
//    }
}


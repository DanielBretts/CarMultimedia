package com.example.carmultimedia;

import static com.example.carmultimedia.Utils.LocationUtil.getCurrentLocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.example.carmultimedia.Utils.LocationUtil;
import com.example.carmultimedia.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements CallStateListener, LocationUtil.LocationResultListener {
    private static final int REQUEST_PERMISSION_CODE = 100;
    private static final String API_KEY = BuildConfig.WEATHER_API_KEY;
    private static final String API_HOST = "yahoo-weather5.p.rapidapi.com";
    ActivityMainBinding binding;
    private Location currentLocation;
    private AppPermissions appPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        LocationUtil.getCurrentLocation(this,this);
        binding.radioButton.setOnClickListener(view -> openRadioActivity());
        binding.navButton.setOnClickListener(view-> openNavActivity());
        binding.wazeButton.setOnClickListener(view-> openWaze());


        appPermissions = new AppPermissions();

        // Check and request permissions
        if (appPermissions.isLocationOk(this)) {
            getCurrentLocation(this,this);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("CarMultimedia required location permission to show you near by places")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    appPermissions.requestLocationPermission(MainActivity.this);
                                }
                            }
                        })
                        .create().show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    appPermissions.requestLocationPermission(this);
                }
            }
        }
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
        if (requestCode == AppPermissions.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NavigationActivity.isLocationPermissionOk = true;
                getCurrentLocation(this,this);
            } else {
                NavigationActivity.isLocationPermissionOk = false;
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getWeatherData(double latitude, double longitude) {
        WeatherApiService apiService = RetrofitClientWeather.getWeatherApiService();

        Call<WeatherResponse> call = apiService.getWeather(latitude, longitude, "json", "c", API_HOST, API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if(weatherResponse != null){
                        binding.temperatureTextView.setText(weatherResponse.getCurrentObservation().getCondition().getTemperature()+"°c");
                    }
                } else {
                    // Handle the error
                    Log.e("Weather", "Request failed. Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Handle the failure (e.g., no network connection)
                Log.e("Weather", "Request failed. Error: " + t.getMessage());
            }
        });
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

//    private void openWaze() {
//        String packageName = "com.waze"; // Replace with the actual package name of the app you want to launch
//
//        // Create an intent to launch the app
//        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(packageName);
//
//        if (intent != null) {
//            // If the app is installed, launch it
//            getBaseContext().startActivity(intent);
//        } else {
//            // If the app is not installed, show a toast
//            Toast.makeText(getBaseContext(), "App is not installed", Toast.LENGTH_LONG).show();
//
//            // Optionally, redirect to Play Store to install the app
//            try {
//                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getBaseContext().startActivity(intent);
//            } catch (android.content.ActivityNotFoundException e) {
//                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getBaseContext().startActivity(intent);
//            }
//        }
//    }

    private void openWaze() {
        String url = String.format("waze://?q=%s", currentLocation.getLatitude()+","+currentLocation.getLongitude());
        Intent wazeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(wazeIntent);
    }

    public void openRadioActivity() {
        Intent intent = new Intent(this, RadioActivity.class);
        startActivity(intent);
    }

    public void openNavActivity() {
        Intent intent = new Intent(this, NavigationActivity.class);
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

    @Override
    public void onLocationResult(Location location, String address) {
        currentLocation = location;
        Log.d("TAG", "current location: "+location.toString());
        getWeatherData(currentLocation.getLatitude(),currentLocation.getLongitude());
    }

    @Override
    public void onLocationError(String errorMsg) {

    }


//    public void openPhoneActivity(View view) {
//        Intent intent = new Intent(this, PhoneActivity.class);
//        startActivity(intent);
//    }
}


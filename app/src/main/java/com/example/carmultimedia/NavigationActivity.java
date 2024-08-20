package com.example.carmultimedia;

import static com.example.carmultimedia.Utils.LocationUtil.getCurrentLocation;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carmultimedia.Model.DirectionPlaceModel.DirectionLegModel;
import com.example.carmultimedia.Model.DirectionPlaceModel.DirectionResponseModel;
import com.example.carmultimedia.Model.DirectionPlaceModel.DirectionRouteModel;
import com.example.carmultimedia.Model.DirectionPlaceModel.DirectionStepModel;
import com.example.carmultimedia.Utils.LocationUtil;
import com.example.carmultimedia.databinding.ActivityNavigationBinding;
import com.example.carmultimedia.databinding.BottomSheetLayoutBinding;
import com.example.carmultimedia.databinding.ToolbarLayoutBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback, LocationUtil.LocationResultListener {

    private ActivityNavigationBinding binding;
    private GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    public static boolean isLocationPermissionOk, isTrafficEnable;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private BottomSheetLayoutBinding bottomSheetLayoutBinding;
    private RetrofitAPI retrofitAPI;
    private LoadingDialog loadingDialog;
    private Location currentLocation;
    private String currentAddress;
    private Double endLat, endLng;
    private String placeId;
    private int currentMode;
    private DirectionStepAdapter adapter;
    private ToolbarLayoutBinding toolbarLayoutBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        placeId = getIntent().getStringExtra("placeId");

//        setSupportActionBar(toolbarLayoutBinding.toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        appPermissions = new AppPermissions();
        loadingDialog = new LoadingDialog(this);

        retrofitAPI = RetrofitClientMaps.getRetrofitClient().create(RetrofitAPI.class);

        bottomSheetLayoutBinding = binding.bottomSheet;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.getRoot());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        adapter = new DirectionStepAdapter();

        bottomSheetLayoutBinding.stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomSheetLayoutBinding.stepRecyclerView.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        binding.txtEndLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("here","here");
                mGoogleMap.clear();
                String location = binding.txtEndLocation.getQuery().toString();
                List<Address> addresses = null;
                if(!location.isEmpty()){
                    Geocoder geocoder = new Geocoder(getBaseContext());
                    try{
                        addresses = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if(addresses.size()>0){
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        endLat = address.getLatitude();
                        endLng = address.getLongitude();

                        setupGoogleMap();

                    }else {
                        Toast.makeText(getBaseContext(),location.concat(" Not found"),Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.backBtn.setOnClickListener(view -> finish());

        if (appPermissions.isLocationOk(this)) {
            getCurrentLocation(this,this);
            isLocationPermissionOk = true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("CarMultimedia required location permission to show you near by places")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    appPermissions.requestLocationPermission(NavigationActivity.this);
                                }
                            }
                        })
                        .create().show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    appPermissions.requestLocationPermission(NavigationActivity.this);
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppPermissions.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionOk = true;
                getCurrentLocation(this,this);
            } else {
                isLocationPermissionOk = false;
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupGoogleMap() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);

        getDirection();
    }

//    private void getCurrentLocation(LocationListener locationListener) {
//
//        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    currentLocation = location;
//                    String startAddress = getAddressFromLocation(currentLocation);
//                    binding.txtStartLocation.setText(startAddress);
//
//                } else {
//                    Toast.makeText(NavigationActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    public String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                address = strReturnedAddress.toString();
            } else {
                address = "No Address returned!";
            }
        } catch (IOException e) {
            e.printStackTrace();
            address = "Cannot get Address!";
        }

        return address;
    }

    private void getDirection() {

        if (isLocationPermissionOk) {
//            loadingDialog.startLoading();
            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
                    "&destination=" + endLat + "," + endLng +
                    "&mode=" + "driving" +
                    "&key=" + BuildConfig.GOOGLE_API_KEY;

            retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                @Override
                public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
                    Gson gson = new Gson();
                    String res = gson.toJson(response.body());

                    Log.d("TAG", "onResponse:" + res + currentLocation.toString());

                    if (response.errorBody() == null) {
                        if (response.body() != null) {
                            clearUI();

                            if (!response.body().getDirectionRouteModels().isEmpty()) {
                                DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);

//                                getSupportActionBar().setTitle(routeModel.getSummary());

                                DirectionLegModel legModel = routeModel.getLegs().get(0);
                                binding.txtStartLocation.setText(legModel.getStartAddress());
//                                binding.txtEndLocation.setText(legModel.getEndAddress());

                                bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());
                                bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());



                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng()))
                                        .title("End Location"));

                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng()))
                                        .title("Start Location"));

                                adapter.setDirectionStepModels(legModel.getSteps());


                                List<LatLng> stepList = new ArrayList<>();

                                PolylineOptions options = new PolylineOptions()
                                        .width(25)
                                        .color(Color.BLUE)
                                        .geodesic(true)
                                        .clickable(true)
                                        .visible(true);

                                List<PatternItem> pattern;

                                    pattern = Arrays.asList(
                                            new Dash(30));


                                options.pattern(pattern);

                                for (DirectionStepModel stepModel : legModel.getSteps()) {
                                    List<LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
                                    for (LatLng latLng : decodedLatLng) {
                                        stepList.add(new LatLng(latLng.latitude, latLng.longitude));
                                    }
                                }

                                options.addAll(stepList);

                                Polyline polyline = mGoogleMap.addPolyline(options);

                                LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
                                LatLng endLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());


                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 17));
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation,14));

//                                loadingDialog.stopLoading();
                                binding.cardLayout.setVisibility(View.INVISIBLE);
                            } else {
                                Toast.makeText(NavigationActivity.this, "No route found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(NavigationActivity.this, "No route found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("TAG", "onResponse: " + response);
                    }


                }



                @Override
                public void onFailure(Call<DirectionResponseModel> call, Throwable t) {

                }
            });


        }

    }

    private void clearUI() {

        mGoogleMap.clear();
        binding.txtStartLocation.setText("");
//        binding.txtEndLocation.setText("");
//        getSupportActionBar().setTitle("");
        bottomSheetLayoutBinding.txtSheetDistance.setText("");
        bottomSheetLayoutBinding.txtSheetTime.setText("");

    }

    private List<LatLng> decode(String points) {

        int len = points.length();

        final List<LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if (appPermissions.isLocationOk(this)) {
            Log.d("TAG", "onMapReady: heree");
            getCurrentLocation(this,this);
            isLocationPermissionOk = true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("CarMultimedia required location permission to show you near by places")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    appPermissions.requestLocationPermission(NavigationActivity.this);
                                }
                            }
                        })
                        .create().show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    appPermissions.requestLocationPermission(NavigationActivity.this);
                }
            }
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();
    }


    @Override
    public void onLocationResult(Location location, String address) {
        currentAddress = address;
        currentLocation = location;
        binding.txtStartLocation.setText(currentAddress);
    }

    @Override
    public void onLocationError(String errorMsg) {
        Toast.makeText(getBaseContext(),"Address not found",Toast.LENGTH_LONG).show();
        finish();
    }
}
package com.example.vvdn.offlinemapapplication.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vvdn.offlinemapapplication.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


public class CustomWindowMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        View.OnClickListener
        {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    private double lat[] = {28.430856, 28.431445, 28.429971, 28.4338455};
    private double lng[] = {77.015784, 77.015533, 77.012372, 77.0097724};
    private ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    private ArrayList<LatLng> latLngArrayListFinal = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createLatLngArrayList();
    }


    private void createLatLngArrayList() {
        for (int i = 0; i < lat.length; i++) {
            LatLng latLng = new LatLng(lat[i], lng[i]);
            latLngArrayList.add(latLng);
            latLngArrayListFinal = latLngArrayList;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        addMarkersToMap();
    }

    private void addMarkersToMap() {
        for (LatLng item : latLngArrayList) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(item);
            markerOptions.title("Value index");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mMap.addMarker(markerOptions);
        }
        CustomMarkerInfoWindow customInfoWindow = new CustomMarkerInfoWindow();
        mMap.setInfoWindowAdapter(customInfoWindow);

        drawAllLines();
    }


    private void drawAllLines() {
        LatLng origin, destination;

        try {

            if (latLngArrayListFinal != null) {
                if (latLngArrayListFinal.size() >= 2) {
                    origin = latLngArrayListFinal.get(0);
                    destination = latLngArrayListFinal.get(1);
                    drawLine(origin, destination, 0);


                    for (int i = 2; i < latLngArrayListFinal.size(); i++) {
                        origin = destination;
                        destination = latLngArrayListFinal.get(i);
                        drawLine(origin, destination, 0);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void drawLine(LatLng origin, LatLng destination, int difference) {

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(origin)
                .width(16)
                .color(getResources().getColor(R.color.colorAccent))
                .add(destination);
//        float angle = CalculateBearingAngle(origin.latitude, origin.longitude, destination.latitude, destination.longitude);

        mMap.addPolyline(polylineOptions);

        //set arrow to line center
        setCenterArrowOnLineAccurate(origin, destination, polylineOptions, difference);
    }


    private void setCenterArrowOnLineAccurate(LatLng origin, LatLng
            destination, PolylineOptions polylineOptions, int difference) {

        Location location = new Location("service Provider");
        location.setLatitude(origin.latitude);
        location.setLongitude(origin.longitude);
        Location previousLoc = location;

        Location locationDes = new Location("service Provider");
        locationDes.setLatitude(destination.latitude);
        locationDes.setLongitude(destination.longitude);
        Location currentLoc = locationDes;

        float bearing = previousLoc.bearingTo(currentLoc);

        if (bearing < 0) {
            bearing = bearing - 10;
        }


        Polyline polyline = mMap.addPolyline(polylineOptions);

        MarkerOptions options = new MarkerOptions();
        options.position(getPolylineCenter(polyline, difference));
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow));
        options.anchor(0.5f, 0.5f);
        options.rotation(bearing);
        options.flat(true);
        mMap.addMarker(options);
    }


    private LatLng getPolylineCenter(Polyline polyline, int difference) {
        LatLng latLngPosition, latLng, finalLatLng;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLngBounds.Builder builderDiff = new LatLngBounds.Builder();

        for (int i = 0; i < polyline.getPoints().size(); i++) {
            builder.include(polyline.getPoints().get(i));
        }

        LatLngBounds bounds = builder.build();
        latLngPosition = bounds.getCenter();


        builderDiff.include(polyline.getPoints().get(0));
        builderDiff.include(latLngPosition);

        LatLngBounds boundsDiff = builderDiff.build();
        latLng = boundsDiff.getCenter();
        if (difference == 0)
            finalLatLng = latLngPosition;
        else
            finalLatLng = latLng;
        return finalLatLng;

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        latLngArrayListFinal.add(latLng);


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(280)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#66CF1C22")));

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }




    class CustomMarkerInfoWindow implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View view = getLayoutInflater()
                    .inflate(R.layout.info_window, null);

            TextView name_tv = view.findViewById(R.id.name);
            TextView details_tv = view.findViewById(R.id.details);
            ImageView img = view.findViewById(R.id.pic);

            TextView hotel_tv = view.findViewById(R.id.hotels);
            TextView food_tv = view.findViewById(R.id.food);
            TextView transport_tv = view.findViewById(R.id.transport);

            name_tv.setText(marker.getTitle());
            details_tv.setText(marker.getSnippet());

            img.setImageResource(R.drawable.arrow);

            hotel_tv.setText("Hotel Title");
            food_tv.setText("Food Title");
            transport_tv.setText("Transport Title");

            return view;
        }
    }
}

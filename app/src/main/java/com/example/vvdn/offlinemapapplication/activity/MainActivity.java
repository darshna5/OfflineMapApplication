package com.example.vvdn.offlinemapapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.vvdn.offlinemapapplication.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout ll_downloadMap, ll_myLocation, ll_drawRoute, ll_addMarkers,
            ll_navigateUser, ll_customInfoWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initViews();
    }

    private void initViews() {
        ll_downloadMap = (LinearLayout) findViewById(R.id.ll_downloadMap);
        ll_myLocation = (LinearLayout) findViewById(R.id.ll_myLocation);
        ll_addMarkers = (LinearLayout) findViewById(R.id.ll_addMarkers);
        ll_drawRoute = (LinearLayout) findViewById(R.id.ll_drawRoute);
        ll_navigateUser = (LinearLayout) findViewById(R.id.ll_navigateUser);
        ll_customInfoWindow = (LinearLayout) findViewById(R.id.ll_customInfoWindow);

        ll_downloadMap.setOnClickListener(this);
        ll_myLocation.setOnClickListener(this);
        ll_addMarkers.setOnClickListener(this);
        ll_drawRoute.setOnClickListener(this);
        ll_navigateUser.setOnClickListener(this);
        ll_customInfoWindow.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ll_customInfoWindow:
                Intent intent6 = new Intent(MainActivity.this, CustomWindowMapsActivity.class);
                startActivity(intent6);
                break;
            case R.id.ll_navigateUser:
                Intent intent5 = new Intent(MainActivity.this, NavigationMapsActivity.class);
                startActivity(intent5);
                break;

            case R.id.ll_addMarkers:
                Intent intent1 = new Intent(MainActivity.this, AddMarkersMapsActivity.class);
                startActivity(intent1);
                break;

            case R.id.ll_downloadMap:
                openActivity("downloadMap");

                break;

            case R.id.ll_myLocation:
                Intent intent = new Intent(MainActivity.this, LocationByAddressMapsActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_drawRoute:
                Intent intent4 = new Intent(MainActivity.this, RouteMapMapsActivity.class);
                startActivity(intent4);
                break;

        }
    }

    private void openActivity(String clickType) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra("type", clickType);
        startActivity(intent);
    }
}

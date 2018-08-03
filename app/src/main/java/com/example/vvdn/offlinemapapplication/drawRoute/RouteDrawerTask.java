package com.example.vvdn.offlinemapapplication.drawRoute;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

import com.example.vvdn.offlinemapapplication.R;

/**
 * Created by ocittwo on 11/14/16.
 *
 * @Author Ahmad Rosid
 * @Email ocittwo@gmail.com
 * @Github https://github.com/ar-android
 * @Web http://ahmadrosid.com
 */
public class RouteDrawerTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    private PolylineOptions lineOptions;
    private GoogleMap mMap;
    private int routeColor;
    private Context context;

    public RouteDrawerTask(GoogleMap mMap, Context context) {
        this.mMap = mMap;
        this.context = context;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("RouteDrawerTask", jsonData[0]);
            DataRouteParser parser = new DataRouteParser();
            Log.d("RouteDrawerTask", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("RouteDrawerTask", "Executing routes");
            Log.d("RouteDrawerTask", routes.toString());

        } catch (Exception e) {
            Log.d("RouteDrawerTask", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        if (result != null) {
            drawPolyLine(result);
//            drawPolyLine11(result);
        }
    }

    private void drawPolyLine(List<List<HashMap<String, String>>> result) {
        System.out.println("RouteDrawerTask.drawPolyLine result=" + result.size());
        ArrayList<LatLng> points;
        lineOptions = null;

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            if (i == result.size() - 1)
                System.out.println("RouteDrawerTask.drawPolyLine hjhj=" + points.size());
            lineOptions.addAll(points);
            lineOptions.width(10);
            routeColor = ContextCompat.getColor(DrawRouteMaps.getContext(), R.color.colorAccent);
            if (routeColor == 0)
                lineOptions.color(0xFF0A8F08);
            else
                lineOptions.color(routeColor);
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null && mMap != null) {
            mMap.addPolyline(lineOptions);

        } else {
            Log.d("onPostExecute", "without Polylines draw");
        }
    }

    Location previousLoc, currentLoc;

    private void drawPolyLine11(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        lineOptions = null;

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }


            Location location1 = new Location("service Provider");

            location1.setLatitude(points.get(0).latitude);
            location1.setLongitude(points.get(0).longitude);
            previousLoc = location1;

            for (int j = 30; j < points.size(); ) {

                Log.d(TAG, "lat pre : " + previousLoc);
                Location location = new Location("service Provider");

                location.setLatitude(points.get(j).latitude);
                location.setLongitude(points.get(j).longitude);
                currentLoc = location;

                Log.d(TAG, "lat cur : " + currentLoc);

                float bearing = previousLoc.bearingTo(currentLoc);

                if (bearing < 0) {
                    bearing = bearing - 10;
                }


                Log.d(TAG, "Bearing : " + bearing + " pre : " + previousLoc + "cur: " + currentLoc);
                MarkerOptions options = new MarkerOptions();
                options.position(points.get(j));
                Log.d(TAG, "data recieved lati: " + points.get(j).latitude);
                Log.d(TAG, "data recieved longti: " + points.get(j).longitude);
//                options.icon(BitmapDescriptorFactory.defaultMarker());
                options.icon(getMarkerIconFromDrawable11(context, R.drawable.arrow));
                options.anchor(0.5f, 0.5f);
                options.rotation(bearing);
                options.flat(true);
                mMap.addMarker(options);

                j = j + 60;
                previousLoc = currentLoc;


            }


            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            routeColor = ContextCompat.getColor(DrawRouteMaps.getContext(), R.color.colorAccent);
            if (routeColor == 0)
                lineOptions.color(0xFF0A8F08);
            else
                lineOptions.color(routeColor);
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null && mMap != null) {
            mMap.addPolyline(lineOptions);


        } else {
            Log.d("onPostExecute", "without Polylines draw");
        }
    }

    public BitmapDescriptor getMarkerIconFromDrawable11(Context context, int resDrawable) {
        Drawable drawable = ContextCompat.getDrawable(context, resDrawable);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}

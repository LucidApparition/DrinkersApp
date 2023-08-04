package com.example.drinkersapp;

import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;

import androidx.annotation.NonNull;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

import java.util.List;

public class DriverNavigationFragment extends Fragment implements OnMapReadyCallback, LocationEngineListener,
        PermissionsListener, MapboxMap.OnMapClickListener {

    //Variable type MapView
    private MapView mapView;
    public MapboxMap map;
    private Button startNavigationButton;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    Point originPosition;
    Point destinationPosition;
    private Marker destinationMarker;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_driver_navigation, container, false);
        // Take information that needs for the MapBox context
        Mapbox.getInstance(getActivity(), getString(R.string.access_token));

        //Display the map by calling the ID
        mapView = v.findViewById(R.id.mapView);
        startNavigationButton = v.findViewById(R.id.startNavigationButton);
        //Overide the containing activity-
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        startNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch Navigation UI
            }
        });

        return v;
    }

        @Override
        public void onMapReady (MapboxMap mapboxMap){
            map = mapboxMap;
            map.addOnMapClickListener(this);
            enableLocation();

        }

        private void enableLocation () {
            if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
                initializeLocationEngine();
                initializeLocationLayer();
            } else {
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(getActivity());
            }
        }

        @SuppressWarnings("MissingPermission")
        private void initializeLocationEngine () {
            locationEngine = new LocationEngineProvider(getActivity()).obtainBestLocationEngineAvailable();
            locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
            locationEngine.activate();

            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                originLocation = lastLocation;
                setCameraPosition(lastLocation);
            } else {
                locationEngine.addLocationEngineListener(this);
            }

        }

        @SuppressWarnings("MissingPermission")
        private void initializeLocationLayer () {
            locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationLayerPlugin.setLocationLayerEnabled(true);
            locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
            locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
        }

        private void setCameraPosition (Location location){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),
                    13.0));
        }

        @Override
        public void onMapClick (@NonNull LatLng point){
            //setting a destination position when we tap

            //if a marker already exist. replace its location
            if (destinationMarker != null) {
                map.removeMarker(destinationMarker);
            }

            destinationMarker = map.addMarker(new MarkerOptions().position(point));
            destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());
            originPosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

            startNavigationButton.setEnabled(true);
            startNavigationButton.setBackgroundResource(com.mapbox.mapboxsdk.R.color.mapbox_blue);
        }

        @Override
        @SuppressWarnings("MissingPermission")
        public void onConnected () {
            //we want provider to begin sent updates
            locationEngine.requestLocationUpdates();

        }

        @Override
        public void onLocationChanged (Location location){
            // when ever location change. new location is change on the camara
            if (location != null) {
                originLocation = location;
                setCameraPosition(location);
            }

        }

        @Override
        // When user denise access and a promt come up explaning why is this permission needed.
        public void onExplanationNeeded (List < String > permissionsToExplain) {
            Toast.makeText(getActivity(), "YOU MUST ALLOW ACCESS", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onPermissionResult ( boolean granted){
            if (granted) {
                enableLocation();
            }
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        //Start displaying the Map
        @Override
        @SuppressWarnings("MissingPermission")
        public void onStart () {
            super.onStart();
            if (locationEngine != null) {
                locationEngine.requestLocationUpdates();

            }
            if (locationLayerPlugin != null) {
                locationLayerPlugin.onStart();

            }
            mapView.onStart();
        }

        @Override
        public void onResume () {
            super.onResume();
            mapView.onResume();
        }

        //Pause the view of the map -activity lifecycles
        @Override
        public void onPause () {
            super.onPause();
            mapView.onPause();
        }

        //-activity lifecycles
        @Override
        public void onStop () {
            super.onStop();
            if (locationEngine != null) {
                locationEngine.removeLocationUpdates();
            }
            if (locationLayerPlugin != null) {
                locationLayerPlugin.onStop();
            }
            mapView.onStop();
        }

        //-activity lifecycles
        @Override
        public void onSaveInstanceState (@NonNull Bundle outState){
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }

        //-activity lifecycles
        @Override
        public void onLowMemory () {
            super.onLowMemory();
            mapView.onLowMemory();
        }

        //-activity lifecycles
        @Override
        public void onDestroy () {
            super.onDestroy();
            if (locationEngine != null) {
                locationEngine.deactivate();
            }
            mapView.onDestroy();
        }



}
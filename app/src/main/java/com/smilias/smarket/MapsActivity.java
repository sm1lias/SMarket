package com.smilias.smarket;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    Double x,y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        permission();
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

        // Add a marker in Sydney and move the camera
        LatLng SKLAVENITIS = new LatLng(37.9282742, 23.7613769);
        LatLng LIDL = new LatLng(37.9270036, 23.7170961);
        LatLng METRO = new LatLng(37.9297351, 23.7370946);
        LatLng MY_MARKET = new LatLng(37.9298601, 23.7286832);
        LatLng AB_VASILOPOYLOS = new LatLng(37.9391139, 23.6699681);
        mMap.addMarker(new MarkerOptions().position(SKLAVENITIS).title("SUPERMARKET SKLAVENITIS"));
        mMap.addMarker(new MarkerOptions().position(LIDL).title("SUPERMARKET LIDL"));
        mMap.addMarker(new MarkerOptions().position(METRO).title("SUPERMARKET METRO"));
        mMap.addMarker(new MarkerOptions().position(MY_MARKET).title("SUPERMARKET MY_MARKET"));
        mMap.addMarker(new MarkerOptions().position(AB_VASILOPOYLOS).title("SUPERMARKET AB_VASILOPOYLOS"));
    }

    @Override
    public void onLocationChanged(Location location) {
        x = location.getLatitude();
        y = location.getLongitude();
        LatLng youAreHere=new LatLng(x,y);
        mMap.addMarker(new MarkerOptions().position(youAreHere).title("youAreHere"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(youAreHere));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    public void permission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                this);
        //locationManager.removeUpdates(this);
    }
}
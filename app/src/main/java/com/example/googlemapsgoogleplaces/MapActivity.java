package com.example.googlemapsgoogleplaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.os.Build.ID;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {


    @Override
    public void onMapReady(final GoogleMap googleMap) {



        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        //nuevo
        mMap.setOnInfoWindowClickListener(this);

        // Setting a custom info window adapter for the google map

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }


        //cada vez que cambien valores de Firebase este metodo se va a lanzar
        mDatabase.child("locacion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (Marker marker : realTimeMarkers) {
                    //marker.remove();
                }

                //obetener all that is below usuarios del primer elemento o sea los numeritos
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    MapsPojo mp = snapshot.getValue(MapsPojo.class);
                    Double latitud = mp.getLatitud();
                    Double longitud = mp.getLongitud();
                    String nomnbre = mp.getNombre(); //nuevo

                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.bano));
                    markerOptions.position(new LatLng(latitud, longitud)); //ponemos posicion de ese marker
                    tmpRealTimeMarker.add(mMap.addMarker(markerOptions));

                }

                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealTimeMarker);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private static final String TAG = "MapActivity";

    DatabaseReference mDatabase;
    //private FirebaseDatabase firebaseDatabase;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    private FusedLocationProviderClient fusedLocationClient;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>(); //marcadores viejitos
    private ArrayList<Marker> tmpRealTimeMarker = new ArrayList<>(); // para que muestre solo los marcadores nuevos en el for


    //widgets
    ImageView mGps;
    ImageButton mMarker;
    //private EditText mSearchText;
    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "empezó a correr");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //mSearchText = (EditText)findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mMarker = findViewById(R.id.mMarker);
        mDatabase = FirebaseDatabase.getInstance().getReference(); // llama a la base de datos de firebase
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //mDatabase = db.getReference("ubicacion");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ubicacion");

        mMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirLatLongFirebase();
            }
        });

                getLocationPermission();
    }

    private void subirLatLongFirebase(){

        Toast.makeText(this, "Ubicación enviada", Toast.LENGTH_SHORT).show();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.e("Latitud: ",  + location.getLatitude() + "Longitud: " + location.getLongitude());

                            Map<String, Object> latlang = new HashMap<>();
                            latlang.put("latitud",location.getLatitude());
                            latlang.put("longitud",location.getLongitude());

                            mDatabase.child("locacion").push().setValue(latlang);
                        }
                    }
                });
    }



    private void init() {
        Log.d(TAG, "init: initializing");


        mGps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");

            }
        });



    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                                                   @Override
                                                   public void onComplete(@NonNull Task task) {
                                                       if(task.isSuccessful()){
                                                           Log.d(TAG, "onComplete: found location! ");
                                                           Location currentLocation = (Location)task.getResult();

                                                           //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ubicacion"); //recien lo agreggue

                                                           moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                                                   DEFAULT_ZOOM,
                                                                   "My Location");

                                                       }else{
                                                           Log.d(TAG, "onComplete: current location is null");
                                                           Toast.makeText(MapActivity.this, "unable to get current" +
                                                                   "location", Toast.LENGTH_SHORT).show();
                                                       }

                                                   }
                                               }
                );

            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }




    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        /*if(!title.equals("My location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }*/


    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);

    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions ={Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0) {
                    for( int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }
}

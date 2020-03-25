package com.example.googlemapsgoogleplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    //private FusedLocationProviderClient fusedLocationClient;
   // private DatabaseReference mDatabase;
    //private FirebaseDatabase firebaseDatabase;
    //private ImageButton mMarker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (isServicesOK()) {
            init();
            //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            //mDatabase = FirebaseDatabase.getInstance().getReference().child("ubicacion");
            FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
            //DatabaseReference ImageView = firebaseDatabase.getReference("ImageView");
            //subirLatLongFirebase();



        }

    }




    /*private void subirLatLongFirebase(){

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

                            mDatabase.child("ubicacion").push().setValue(latlang);
                        }
                    }
                });
    }*/




    private void init (){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });

    }
    public boolean isServicesOK() {
        Log.d(TAG, "isServices Ok: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and user can make map requests
            Log.d(TAG, "isServicesOk: checking google services version");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error ocurre but we can resolve it
            Log.d(TAG, " isServicesOk: an error ocurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can´t make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}


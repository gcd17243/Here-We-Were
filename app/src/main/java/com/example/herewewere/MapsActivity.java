package com.example.herewewere;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herewewere.activities.CreateOrShowNoteActivity;
import com.example.herewewere.activities.MainActivity;
import com.example.herewewere.databases.MyNoteDbManager;
import com.example.herewewere.models.MyNote;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.herewewere.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap Map;
    private MyNoteDbManager myNoteDbManager;
    private List<MyNote> myNotesList;

    SupportMapFragment mapFragment;
    SearchView searchView;
    FusedLocationProviderClient client;
    FloatingActionButton floatingActionButton,floatingActionButton2;
    private String title, note, imagePath = null,latid,longid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        floatingActionButton = findViewById(R.id.floatingActionButton3);
        floatingActionButton2 = findViewById(R.id.floatingActionButton4);

        myNoteDbManager = new MyNoteDbManager(this);



        //Assign Variable
        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //Initialize Fused Location
        client = LocationServices.getFusedLocationProviderClient(this);


        //Check permission
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            //When permission Granted
            //Call method
            getCurrentLocation();
        }else {
            //When permission Denied
            //Request permission
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

        //Searching function
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location =searchView.getQuery().toString();

                List<Address> addressList = null;

                if (location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    Map.addMarker(new MarkerOptions().position(latLng).title(location));
                    Map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);
    }
    private void getCurrentLocation() {
        //Initialize task Location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            //When success
            public void onSuccess(final Location location) {
                if (location !=null){
                    //Sync map
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            //Initialize lat Lng
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                            //Zoom Map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                        }
                    });

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //When permission Granted
                //Call method
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Map = googleMap;
        double valuelat;
        double valuelong;

        Cursor res = myNoteDbManager.getAllData();
        if(res.getCount() == 0) {
            // show message
            Toast.makeText(this, "Error Nothing found", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {
            valuelat=0;
            valuelong=0;
            title = res.getString(1);
            imagePath = res.getString(4);
            latid = res.getString(5);
            longid = res.getString(6);

            if(latid != null||longid != null){
                try
                {   valuelat = Double.parseDouble(latid);
                    valuelong = Double.parseDouble(longid);
                    // it means it is double
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }

            LatLng latLng = new LatLng(valuelat, valuelong);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

            // Add a marker in Sydney and move the camera
            if (imagePath==null){
                Map.addMarker(new MarkerOptions().position(latLng).title(title));

            }else{
                Map.addMarker(new MarkerOptions().position(latLng).title(title).icon(BitmapDescriptorFactory.fromPath(imagePath)));
            }

        }


    }


    public void gotToMapActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void gotToCreateNewNoteActivity(View view) {

        Intent intent = new Intent(this, CreateOrShowNoteActivity.class);
        intent.putExtra("our_note_key", "create_note_here");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
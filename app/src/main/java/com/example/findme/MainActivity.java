package com.example.findme;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.location.LocationManager;
import android.location.Location;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    Button sendButton;

    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    String phoneNumber = "07864276696";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.send_location_button);

        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                if (checkLocationPermission() && checkMessagePermission()) {
                    getLocation();
                }else{
                    requestLocationPermission();
                    requestMessagePermission();
                }
            }
        });

    }

    // Check for Permissions
    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean checkMessagePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    // Request Permissions
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }
    private void requestMessagePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, LOCATION_PERMISSION_REQUEST_CODE);
    }


    // Methods to change page
    public void launchSettings(View v) {
        //launch new activity
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
    public void launchContacts(View v) {
        //launch new activity
        Intent i = new Intent(this, ContactsActivity.class);
        startActivity(i);
    }

    private void getLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check for Fine_Location Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            sendSMS(phoneNumber, latitude, longitude);
        }

    }

    private void sendSMS(String phoneNumber, double latitude, double longitude) {

        String message = "I am in need of immediate assistance!" + "http://maps.google.com?q=" + latitude + "," + longitude;

        try {
            SmsManager smsManager = SmsManager.getDefault(); // Creates smsManager object
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            // Toast Message, confirming message sending
            Toast.makeText(MainActivity.this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                // Handle case where permission denied
                Toast.makeText(this, "Location permission is required to send location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
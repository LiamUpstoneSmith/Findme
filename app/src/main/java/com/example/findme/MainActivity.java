package com.example.findme;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;

// for location permission
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.location.LocationManager;
import android.location.Location;
import android.location.Geocoder; // For more user-friendly location description (optional)
import android.telephony.SmsManager;
import android.widget.Button;

// for pop up window
import android.app.AlertDialog;
import android.content.DialogInterface;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_CODE = 1;
    private static final int REQUEST_SMS_CODE = 2;

    private LocationManager locationManager;
    private Button sendLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sendLocationButton = findViewById(R.id.send_location_button); // Replace with your button ID

        // Request location permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);

        // Request SMS permission (newer versions of Android require this)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_CODE);

        sendLocationButton.setOnClickListener(new View.OnClickListener() {

            Button myButton = findViewById(R.id.send_location_button);

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = getBuilder();
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle Cancel button click
                        dialog.dismiss();  // Dismiss the pop-up
                    }
                });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            private AlertDialog.Builder getBuilder() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


                builder.setTitle("Send Location");  // Set title
                builder.setMessage("Do you want to send an SOS Location Alert?");  // Set message

                // Add buttons (optional)
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle OK button click
                        sendLocationSMS("07864276696"); // Replace with recipient phone number
                    }
                });
                return builder;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with location access
                } else {
                    // Handle permission denied case
                }
                break;
            case REQUEST_SMS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with SMS sending
                } else {
                    // Handle permission denied case
                }
                break;
        }
    }

    private void sendLocationSMS(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle permission denied case
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String message = "My current location is: \n(" + latitude + ", " + longitude + ")";

            // Check SMS permission again
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                // Handle permission denied case
                return;
            }

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } else {
            // Handle location unavailable case
        }

    }

    public void launchSettings(View v){

        //launch new activity
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void launchContacts(View v){

        //launch new activity
        Intent i = new Intent(this, ContactsActivity.class);
        startActivity(i);
    }

}
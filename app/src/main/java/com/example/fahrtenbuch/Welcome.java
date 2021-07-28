package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Welcome extends AppCompatActivity {
    private Button fleetBtn;
    private Button myReservationBtn;
    private Button newReservationBtn;
    private Button newDriveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        fleetBtn = findViewById(R.id.fleet);
        fleetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFleet();
            }
        });

        myReservationBtn = findViewById(R.id.myReservation);
        myReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMyReservation();
            }
        });

        newReservationBtn = findViewById(R.id.newReservation);
        newReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToNewReservation();
            }
        });

        newDriveBtn = findViewById(R.id.newDrive);
        newDriveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToNewDrive();
            }
        });

        }
        private void switchToFleet(){
            Intent switchActivityIntent = new Intent(this, Fleet.class);
            startActivity(switchActivityIntent);
        }
        private void switchToMyReservation(){
            String eingeloggterUser ="";
            Intent switchActivityIntent = new Intent(this, myReservation.class);
            if(getIntent().hasExtra("public_id") == true) {
                eingeloggterUser = getIntent().getExtras().getString("public_id");
            }
            switchActivityIntent.putExtra("public_id", eingeloggterUser);
            startActivity(switchActivityIntent);
        }
        private void switchToNewReservation(){
            String eingeloggterUser ="";
            Intent switchActivityIntent = new Intent(this, newReservation.class);
            if(getIntent().hasExtra("public_id") == true) {
                eingeloggterUser = getIntent().getExtras().getString("public_id");
            }
            switchActivityIntent.putExtra("public_id", eingeloggterUser);
            startActivity(switchActivityIntent);
        }
        private void switchToNewDrive(){
            Intent switchActivityIntent = new Intent(this, NewDrive.class);
            startActivity(switchActivityIntent);
        }


    }

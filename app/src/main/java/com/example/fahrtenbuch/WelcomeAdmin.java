package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_admin);

        Button button_fahrtenbuch = findViewById(R.id.button_fahrtenbuch);

        button_fahrtenbuch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivity();
            }
        });
    }

    private void switchActivity(){
        Intent switchActivityIntent = new Intent(this, Fahrtenbuch.class);
        startActivity(switchActivityIntent);
    }
}
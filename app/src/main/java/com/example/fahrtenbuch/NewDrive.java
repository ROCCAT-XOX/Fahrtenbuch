package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewDrive extends AppCompatActivity {

    private Spinner car_drive_spinner;
    private Spinner spinner_reservation;

    private EditText et_drive_start;
    private EditText et_drive_ziel;
    private EditText et_drive_strecke;

    List<String> cars = new ArrayList<String>();
    List<Integer> cars_id = new ArrayList<Integer>();

    List<String> reservations = new ArrayList<String>();
    List<Integer> reservations_id = new ArrayList<Integer>();
    int reservation_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drive);

        et_drive_start = findViewById(R.id.et_drive_start);
        et_drive_ziel = findViewById(R.id.et_drive_ziel);
        et_drive_strecke = findViewById(R.id.et_drive_strecke);


        String eingeloggterUser ="";
        Intent switchActivityIntent = new Intent(this, myReservation.class);
        if(getIntent().hasExtra("public_id") == true) {
            eingeloggterUser = getIntent().getExtras().getString("public_id");
        }
        Log.d("Fahrtenbuch", eingeloggterUser);
        Log.d("Fahrtenbuch", "HALLO");

        car_drive_spinner = findViewById(R.id.car_drive_spinner);
        spinner_reservation = findViewById(R.id.spinner_reservation);

        spinner_reservation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                reservation_id = position -1;
                Log.d("Fahrtenbuch", spinner_reservation.getSelectedItem().toString());
                if(!spinner_reservation.getSelectedItem().toString().equals("Wähle eine Reservierung")){
                    try {
                        getSelectedReservation("http://10.0.2.2:5000/onereservierung/" + reservations_id.get(reservation_id).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    et_drive_start.setText("");
                    et_drive_ziel.setText("");
                    et_drive_strecke.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cars.add(0, "Wähle ein Auto");
        reservations.add(0, "Wähle eine Reservierung");

        try {
            getAvailableCars("http://10.0.2.2:5000/verfügbarCar");
            getMyReservations("http://10.0.2.2:5000/reservierung/" + eingeloggterUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAvailableCars(String url)throws IOException {

        OkHttpClient client_cars = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client_cars.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();


                    NewDrive.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jobject = new JSONObject(myResponse);
                                JSONArray jsonArray = jobject.getJSONArray("Free Cars");

                                for (int i=0; i < jsonArray.length(); i++)
                                {
                                    try {
                                        JSONObject oneObject = jsonArray.getJSONObject(i);
                                        Integer id = oneObject.getInt("id");
                                        String modell = oneObject.getString("model");
                                        String marke = oneObject.getString("marke");


                                        cars.add("ID:" + id.toString() + ", " + "Marke: " + marke + ", " + "Modell: " + modell );
                                        cars_id.add(id);

                                    } catch (JSONException e) {
                                        // Oops
                                    }
                                }
                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, cars);
                                car_drive_spinner.setAdapter(spinnerAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void getMyReservations(String url)throws IOException {

        OkHttpClient client_reservations = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client_reservations.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();


                    NewDrive.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jobject = new JSONObject(myResponse);
                                JSONArray jsonArray = jobject.getJSONArray("reservierungen");

                                for (int i=0; i < jsonArray.length(); i++)
                                {
                                    try {
                                        JSONObject oneObject = jsonArray.getJSONObject(i);
                                        Integer id = oneObject.getInt("reservierungs_id");
                                        String start = oneObject.getString("start");
                                        String ziel = oneObject.getString("ende");


                                        reservations.add("ID:" + id.toString() + ", " + "Start: " + start + ", " + "Ziel: " + ziel );
                                        reservations_id.add(id);


                                    } catch (JSONException e) {
                                        // Oops
                                    }
                                }
                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, reservations);
                                spinner_reservation.setAdapter(spinnerAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void getSelectedReservation(String url) throws IOException {

        OkHttpClient client_one_reservation = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client_one_reservation.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    Log.d("Fahrtenbuch", myResponse);


                    NewDrive.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jObject = new JSONObject(myResponse);

                                String start = jObject.getString("start");
                                String ziel = jObject.getString("ende");
                                String strecke = jObject.getString("meter");
                                Integer car_id = jObject.getInt("fahrzeug_id");

                                Log.d("Fahrtenbuch", car_id.toString());
                                et_drive_start.setText(start);
                                et_drive_ziel.setText(ziel);
                                et_drive_strecke.setText(strecke);

                                for(int i = 0; i < cars_id.size(); i++)
                                {

                                    if(cars_id.get(i) == car_id){
                                        car_drive_spinner.setSelection(car_id);
                                    }
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }
}
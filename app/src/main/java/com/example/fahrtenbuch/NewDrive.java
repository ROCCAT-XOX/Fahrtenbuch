package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewDrive extends AppCompatActivity {

    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");



    private String eingeloggterUser;

    String carid = "";

    private Spinner car_drive_spinner;
    private Spinner spinner_reservation;

    private Button button_submit_drive;

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

        button_submit_drive = findViewById(R.id.button_submit_drive);

        et_drive_start = findViewById(R.id.et_drive_start);
        et_drive_ziel = findViewById(R.id.et_drive_ziel);
        et_drive_strecke = findViewById(R.id.et_drive_strecke);


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
                    car_drive_spinner.setEnabled(true);
                    car_drive_spinner.setSelection(0);
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

        button_submit_drive.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                if(spinner_reservation.getSelectedItem().equals("Wähle eine Reservierung")){
                    if(et_drive_start.getText().toString() != "" && et_drive_ziel.getText().toString() != "" && et_drive_strecke.getText().toString() != "" && car_drive_spinner.getSelectedItemPosition() > 0){
                        try {
                            Integer carid= cars_id.get(car_drive_spinner.getSelectedItemPosition()-1);

                            SharedPreferences settingsP = getSharedPreferences("Car", 0);
                            SharedPreferences.Editor editor = settingsP.edit();
                            editor.putString("car_id", carid.toString());
                            editor.apply();

                            String json = "{\"public_id\":" + "\"" + eingeloggterUser + "\"" + ", \"fahrzeug_id\":" + carid + ", \"start\" : " + "\"" + et_drive_start.getText().toString() + "\"" + ", \"ende\": " + "\"" + et_drive_ziel.getText().toString() + "\"" + ", \"meter\" :" + et_drive_strecke.getText().toString() +"}";
                            addFahrtWithoutReservation("http://10.0.2.2:5000/fahrtwithoutreservation", json);
                            makeCarUnavailable("http://10.0.2.2:5000/unavailablecar/" + carid.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        switchActivity();
                    }
                    else{
                        Toast.makeText(NewDrive.this, "Fill out the fields!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if(et_drive_start.getText().toString() != "" && et_drive_ziel.getText().toString() != "" && et_drive_strecke.getText().toString() != "" && car_drive_spinner.getSelectedItemPosition() > 0){

                        //MISTAKE SOMEWHERE

                        //reservations_id.get(spinner_reservation.getSelectedItemPosition()-1);



                        try {
                            getCarId("http://10.0.2.2:5000/reservierung/car_id/" + reservations_id.get(spinner_reservation.getSelectedItemPosition()-1));
                            Log.d("Fahrtenbuch", "ReservierungsID: " + reservations_id.get(spinner_reservation.getSelectedItemPosition()-1).toString());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        switchActivity();
                    }
                    else{
                        Toast.makeText(NewDrive.this, "Fill out the fields!", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }


    private void getCarId(String url) throws IOException{
        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    carid = response.body().string();
                    SharedPreferences settingsP = getSharedPreferences("Car", 0);
                    SharedPreferences.Editor editor = settingsP.edit();
                    editor.putString("car_id", carid);
                    editor.apply();
                    Log.d("Fahrtenbuch", carid);
                    String jsonCarID = "{\"public_id\":" + "\"" + eingeloggterUser + "\"" + ", \"reservierungs_id\": " +reservations_id.get(spinner_reservation.getSelectedItemPosition()-1)  + ", \"fahrzeug_id\":" + carid + ", \"start\" : " + "\"" + et_drive_start.getText().toString() + "\"" + ", \"ende\": " + "\"" + et_drive_ziel.getText().toString() + "\"" + ", \"meter\" :" + et_drive_strecke.getText().toString() +"}";
                    addFahrtWithReservation("http://10.0.2.2:5000/fahrtwithreservation", jsonCarID);


                    NewDrive.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
        });
    }


    private void makeCarUnavailable(String url) throws IOException{
        OkHttpClient client_makeCarUnavailable = new OkHttpClient();

        String json = "";

        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        client_makeCarUnavailable.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {


                    NewDrive.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }
        });

    }

    private void addFahrtWithReservation(String url, String json) throws IOException {
        OkHttpClient client_fahrtwithoutreservation = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client_fahrtwithoutreservation.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    deleteReservierung("http://10.0.2.2:5000/reservierung/" + reservations_id.get(spinner_reservation.getSelectedItemPosition()-1));


                    NewDrive.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }
        });
    }

    private void addFahrtWithoutReservation(String url, String json) throws IOException {
        OkHttpClient client_fahrtwithoutreservation = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client_fahrtwithoutreservation.newCall(request).enqueue(new Callback() {
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

                        }
                    });
                }
            }
        });
    }


    private void switchActivity(){
        Intent switchActivityIntent = new Intent(this, correction.class);
        switchActivityIntent.putExtra("public_id", eingeloggterUser);
        startActivity(switchActivityIntent);
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
                                        Integer id = oneObject.getInt("fahrzeug_id");
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

                                cars.add(1, "Auto bereits reserviert");
                                car_drive_spinner.setSelection(1);
                                car_drive_spinner.setEnabled(false);






                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void deleteReservierung(String url)throws IOException {
        OkHttpClient client_delete = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        Log.d("Fahrtenbuch", "test");
        client_delete.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Fahrtenbuch", "test");
                    final String myResponse = response.body().string();


                    NewDrive.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }
        });
    }
}
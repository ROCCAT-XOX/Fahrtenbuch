package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

public class newReservation extends AppCompatActivity {

    private Spinner car_spinner;
    List<String> cars = new ArrayList<String>();
    List<Integer> cars_id = new ArrayList<Integer>();
    int car_id;
    private String eingeloggterUser;

    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    EditText et_start;
    EditText et_ziel;
    EditText et_strecke;

    Button btnAddReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reservation);

        car_spinner =findViewById(R.id.spinner);


        et_start = findViewById(R.id.et_start);
        et_ziel = findViewById(R.id.et_ziel);
        et_strecke = findViewById(R.id.et_strecke);

        btnAddReservation = findViewById(R.id.btnReservation);

        btnAddReservation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(getIntent().hasExtra("public_id") == true) {
                    eingeloggterUser = getIntent().getExtras().getString("public_id");
                    car_id = car_spinner.getSelectedItemPosition();

                }

                String json = "{\"fahrzeug_id\":" + "\"" + cars_id.get(car_id) + "\"" + ",\"public_id\":" + "\"" + eingeloggterUser + "\"" + ",\"start\":" + "\"" + et_start.getText().toString()+ "\"" + ",\"ende\":" + "\"" +et_ziel.getText().toString()+ "\"" + ",\"meter\":" + "\"" + et_strecke.getText().toString() + "\"" + "}";
                try {
                    addNewReservation("http://10.0.2.2:5000/reservierung", json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            getAvailableCars("http://10.0.2.2:5000/car");
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


                    newReservation.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jobject = new JSONObject(myResponse);
                                JSONArray jsonArray = jobject.getJSONArray("cars");

                                for (int i=0; i < jsonArray.length(); i++)
                                {
                                    try {
                                        JSONObject oneObject = jsonArray.getJSONObject(i);
                                        Integer id = oneObject.getInt("id");
                                        String modell = oneObject.getString("model");
                                        String marke = oneObject.getString("marke");


                                        cars.add("ID:" + id.toString() + " " + "Marke: " + marke + " " + "Modell: " + modell );
                                        cars_id.add(id);

                                    } catch (JSONException e) {
                                        // Oops
                                    }
                                }
                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, cars);
                                car_spinner.setAdapter(spinnerAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void addNewReservation(String url, String json) throws IOException{

        OkHttpClient client_reservation = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client_reservation.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    newReservation.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast message für erfolgreichen Login
                            Toast.makeText(newReservation.this, "Reservation successfull!", Toast.LENGTH_SHORT).show();
                            //Kurz warten mit dem switchen der Activity, um Toast Message vollständig zu zeigen
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    //Activity wechseln
                                    switchActivity();
                                }
                            }, 1200);
                        }
                    });
                }
            }
        });

    }

    private void switchActivity(){
        Intent switchActivityIntent = new Intent(this, Welcome.class);
        switchActivityIntent.putExtra("public_id", eingeloggterUser);
        startActivity(switchActivityIntent);
    }
}
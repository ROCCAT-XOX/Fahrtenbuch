package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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

public class newReservation extends AppCompatActivity {

    private Spinner car_spinner;
    List<String> cars = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reservation);

        car_spinner =findViewById(R.id.spinner);
        try {
            doGetRequest("http://10.0.2.2:5000/car");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void doGetRequest(String url)throws IOException {

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
}
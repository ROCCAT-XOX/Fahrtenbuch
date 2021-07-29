package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class myReservation extends AppCompatActivity {

    final ArrayList<ListItem_MyReservations> myreservation_list = new ArrayList<ListItem_MyReservations>();

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);

        lv = (ListView) findViewById(R.id.list_myreservation);



        String eingeloggterUser ="";
        Intent switchActivityIntent = new Intent(this, myReservation.class);
        if(getIntent().hasExtra("public_id") == true) {
            eingeloggterUser = getIntent().getExtras().getString("public_id");
        }

        Log.d("Fahrtenbuch", eingeloggterUser);

        try {
            doGetRequest("http://10.0.2.2:5000/reservierung/" + eingeloggterUser);


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


                    myReservation.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jobject = new JSONObject(myResponse);
                                JSONArray jsonArray = jobject.getJSONArray("reservierungen");

                                for (int i=0; i < jsonArray.length(); i++)
                                {
                                    try {
                                        JSONObject oneObject = jsonArray.getJSONObject(i);
                                        Integer reservation_id = oneObject.getInt("reservierungs_id");
                                        Integer car_id = oneObject.getInt("fahrzeug_id");
                                        String start = oneObject.getString("start");
                                        String ziel = oneObject.getString("ende");
                                        Double entfernung = oneObject.getDouble("meter");

                                        myreservation_list.add(new ListItem_MyReservations(reservation_id, start, ziel, entfernung.floatValue(), car_id));

                                    } catch (JSONException e) {
                                        // Oops
                                    }
                                }

                                AdvancedAdapter_MyReservations advancedAdapter = new AdvancedAdapter_MyReservations(getBaseContext(), myreservation_list);
                                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                        Log.d("Fahrtenbuch", myreservation_list.get(position).getReservation_id().toString());
                                        try {
                                            deleteReservierung("http://10.0.2.2:5000/reservierung/" + myreservation_list.get(position).getReservation_id().toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        myreservation_list.remove(position);
                                        advancedAdapter.notifyDataSetChanged();
                                        return true;
                                    }
                                });

                                lv.setAdapter(advancedAdapter);



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


                    myReservation.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }
        });
    }


}
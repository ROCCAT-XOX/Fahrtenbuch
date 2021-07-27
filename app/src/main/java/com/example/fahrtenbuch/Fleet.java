package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Fleet extends AppCompatActivity {

    final ArrayList<ListItem> eintraege_liste = new ArrayList<ListItem>();
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet);

        lv = (ListView) findViewById(R.id.list);

        /*final ArrayList<ListItem> eintraege_liste = new ArrayList<ListItem>();
        for (int i=0; i<15; i++){
            eintraege_liste.add(new ListItem(i,"mercedes", "aklasse", 0f));
        }
        final AdvancedAdapter advancedAdapter = new AdvancedAdapter(this, eintraege_liste);
        lv.setAdapter(advancedAdapter);
         */


        try {
            doGetRequest("http://10.0.2.2:5000/fahrzeug");
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


                    Fleet.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jobject = new JSONObject(myResponse);
                                JSONArray jsonArray = jobject.getJSONArray("fahrzeuge");

                                for (int i=0; i < jsonArray.length(); i++)
                                {
                                    try {
                                        JSONObject oneObject = jsonArray.getJSONObject(i);
                                        String marke = oneObject.getString("marke");
                                        String modell = oneObject.getString("modell");
                                        Integer id = oneObject.getInt("fahrzeug_id");
                                        Double kilometerstand = oneObject.getDouble("kilometerstand");

                                        eintraege_liste.add(new ListItem(id,marke, modell, kilometerstand.floatValue()));
                                        Log.d("Fahrtenbuch", eintraege_liste.get(i).getModell());
                                        Log.d("Fahrtenbuch", eintraege_liste.get(i).getModell());
                                    } catch (JSONException e) {
                                        // Oops
                                    }
                                }
                                final AdvancedAdapter advancedAdapter = new AdvancedAdapter(getBaseContext(), eintraege_liste);
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
}
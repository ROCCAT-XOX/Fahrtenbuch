package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

public class Fleet extends AppCompatActivity {

    final ArrayList<ListItem_Fleet> car_list = new ArrayList<ListItem_Fleet>();
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet);

        lv = (ListView) findViewById(R.id.list_fleet);

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


                    Fleet.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jobject = new JSONObject(myResponse);
                                JSONArray jsonArray = jobject.getJSONArray("cars");

                                for (int i=0; i < jsonArray.length(); i++)
                                {
                                    try {
                                        JSONObject oneObject = jsonArray.getJSONObject(i);
                                        String marke = oneObject.getString("marke");
                                        String modell = oneObject.getString("model");
                                        Integer id = oneObject.getInt("id");
                                        Double kilometerstand = oneObject.getDouble("kilometerstand");
                                        Integer ps = oneObject.getInt("ps");
                                        Boolean verfügbar = oneObject.getBoolean("verfügbar");
                                        String verfügbar2;
                                        if(verfügbar==true){
                                            verfügbar2 = "ja";
                                        }
                                        else{
                                            verfügbar2 = "nein";
                                        }

                                        car_list.add(new ListItem_Fleet(id,marke, modell, kilometerstand.floatValue(),ps,verfügbar2));
                                    } catch (JSONException e) {
                                        // Oops
                                    }
                                }
                                final AdvancedAdapter_Fleet advancedAdapter = new AdvancedAdapter_Fleet(getBaseContext(), car_list);
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
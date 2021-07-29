package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class correction extends AppCompatActivity {

    String car_id;
    private TextView tv_kilometerstand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);

        tv_kilometerstand = findViewById(R.id.tv_kilometerstand);

        SharedPreferences settingsP = getSharedPreferences("Car", 0);
        String car_id = settingsP.getString("car_id", "");

        try {
            getKilometerstand("http://10.0.2.2:5000/kilometerstandcar/" + car_id);
            Log.d("Fahrtenbuch", "correctcar_id: " + car_id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getKilometerstand(String url) throws IOException{
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
                    String kilometerstand = response.body().string();
                    correction.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_kilometerstand.setText(kilometerstand);
                        }
                    });
                }
            }
        });
    }
}
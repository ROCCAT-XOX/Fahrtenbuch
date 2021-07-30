package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class correction extends AppCompatActivity {

    String car_id;
    private TextView tv_kilometerstand;
    private EditText et_correction;
    private Button button_correction_submit;

    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);

        tv_kilometerstand = findViewById(R.id.tv_kilometerstand);
        et_correction = findViewById(R.id.et_correction);
        button_correction_submit = findViewById(R.id.button_correction_submit);

        SharedPreferences settingsP = getSharedPreferences("Car", 0);
        car_id = settingsP.getString("car_id", "");

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
                            SharedPreferences settingsP = getSharedPreferences("Car", 0);
                            SharedPreferences.Editor editor = settingsP.edit();
                            editor.putString("kilometerstand", kilometerstand);
                            editor.apply();
                            tv_kilometerstand.setText(kilometerstand);
                        }
                    });
                }
            }
        });
        button_correction_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_correction.getText().toString().equals("")){
                    switchActivity();
                }
                else{
                    try {
                        korrigiereKilometerstand("http://10.0.2.2:5000/carKilometerstand/" + car_id);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void korrigiereKilometerstand(String url) throws IOException{
        OkHttpClient client_makeCarUnavailable = new OkHttpClient();

        String json = "{\"kilometerstand\" :" + "\"" + et_correction.getText().toString() + "\"" + "}";
        Log.d("Fahrtenbuch", json);

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


                    correction.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switchActivity();
                        }
                    });
                }
            }
        });
    }

    private void switchActivity(){
        Intent switchActivityIntent = new Intent(this, Geotracking.class);
        startActivity(switchActivityIntent);
    }


}
package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
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

public class Geotracking extends AppCompatActivity {

    ArrayList<Double> distances = new ArrayList<>();

    LocationListener l;

    double l1;
    double l2;

    double b1;
    double b2;

    double d1;
    double d2;
    double d3;

    double r1;
    double r2;
    double r3;

    int i;

    String car_id;

    Button button_start_fahrt;
    Button button_end_fahrt;
    Button button_submit_eingetragen;
    Button button_submit_ermittelt;

    TextView tv_info;
    TextView tv_eingetragene_entfernung;
    TextView tv_ermittelte_entfernung;

    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    String strecke;
    String alter_kilometerstand;

    Double result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 123;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geotracking);

        button_start_fahrt = findViewById(R.id.button_start_fahrt);
        button_end_fahrt = findViewById(R.id.button_end_fahrt);
        button_submit_eingetragen = findViewById(R.id.button_submit_eingetragen);
        button_submit_ermittelt = findViewById(R.id.button_submit_ermittelt);

        tv_info = findViewById(R.id.tv_info);
        tv_eingetragene_entfernung = findViewById(R.id.tv_eingetragene_entfernung);
        tv_ermittelte_entfernung = findViewById(R.id.tv_ermittelte_entfernung);

        SharedPreferences settingsP = getSharedPreferences("Car", 0);
        car_id = settingsP.getString("car_id", "");
        alter_kilometerstand = settingsP.getString("kilometerstand", "");

        try {
            getKilometerstand("http://10.0.2.2:5000/kilometerstandcar/" + car_id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        button_start_fahrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_info.setText("Geotracking läuft...");

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
                } else {
                    LocationManager m = getSystemService(LocationManager.class);
                    List<String> providers = m.getAllProviders();
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    String p = m.getBestProvider(criteria, true);


                    l = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            if(i>3) b1=b2;
                            b2= location.getLatitude();
                            if(i>3) l1=l2;
                            l2= location.getLongitude();
                            d1=b2-b1;
                            d2=l2-l1;

                            r1 = 111.3 * (l2-l1);
                            r2 = 71.5 * (b2-b1);

                            if(i>3) {
                                d3 = Math.sqrt((d1*d1)+(d2*d2));
                                r3 = Math.sqrt((d1*d1)+(d2*d2));

                                distances.add((double) Math.round(d3*(40000000/360)));
                            }
                            i++;
                        }
                    };
                    button_end_fahrt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Fahrtenbuch", "beende fahrt");
                            m.removeUpdates(l);
                            l = null;
                            result = 0.0;
                            for(int i = 0; i < distances.size(); i++){
                                if(distances.get(i) > 1000){
                                    distances.remove(i);
                                }
                                result += distances.get(i);
                            }
                            tv_ermittelte_entfernung.setText(String.valueOf(result));
                        }
                    });
                    m.requestLocationUpdates(p, 2000, 0, l);
                }
            }
        });

        button_submit_eingetragen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Log.d("Fahrtenbuch", car_id);
                    Log.d("Fahrtenbuch", strecke);
                    Log.d("Fahrtenbuch", alter_kilometerstand);
                    Float newkilometerstand = Float.parseFloat(strecke) + Float.parseFloat(alter_kilometerstand);
                    String json = "{\"kilometerstand\":" + newkilometerstand + "}";
                    korriegiereKilometerstand("http://10.0.2.2:5000/carKilometerstand/" + car_id, json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        button_submit_ermittelt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Float newkilometerstand = (result.floatValue() + Float.parseFloat(alter_kilometerstand));
                    String json = "{\"kilometerstand\":" + newkilometerstand + "}";
                    korriegiereKilometerstand("http://10.0.2.2:5000/carKilometerstand/" + car_id, json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void korriegiereKilometerstand(String url, String json) throws IOException{

        OkHttpClient client_correction = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        client_correction.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    Geotracking.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Geotracking.this, "Fahrt eingetragen", Toast.LENGTH_SHORT).show();
                            switchActivity();
                        }
                    });
                }
            }
        });

    }

    private void switchActivity(){
        Intent switchActivityIntent = new Intent(this, Welcome.class);
        startActivity(switchActivityIntent);
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


                    Geotracking.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences settingsP = getSharedPreferences("Car", 0);
                            strecke = settingsP.getString("strecke", "");
                            tv_eingetragene_entfernung.setText("Eingetragene Entfernung \n" + strecke);
                        }
                    });
                }
            }
        });
    }
}
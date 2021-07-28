package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class Login extends AppCompatActivity {
    private Button loginBtn;
    private String eingeloggterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.et_email);
        EditText password = findViewById(R.id.et_password);

        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fetch("http://10.0.2.2:5000/login", email.getText().toString(), password.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //Der http Request benötigt für die BasicAuth einen Header mit den Credentials, der hier erstellt wird
    private OkHttpClient createAuthenticatedClient(final String username,
                                                   final String password) {
        // build client with authentication information.
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();

        return httpClient;
    }


    //hier findet der eigentliche http request statt, dabei wird der httpClient übergeben, der schon mit dem header erstellt worden ist sowie die url
    private void doRequest(OkHttpClient httpClient, String anyURL) throws Exception {

        Request request = new Request.Builder()
                .url(anyURL)
                .build();

        //es wird bewusst enqueue verwendet, um einen neuen thread zu starten
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Login.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast message, wenn credentials falsch sind
                        Toast.makeText(Login.this, "Login failed!", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    eingeloggterUser = response.body().string();

                    Login.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast message für erfolgreichen Login
                            Toast.makeText(Login.this, "Login successfull!", Toast.LENGTH_SHORT).show();
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


    //die fetch methode dient zur übersicht: sie ruft die Methode zur erstellung des http clients mit header auf, sowie die methode, die den request ausführt
    public void fetch(String url, String username, String password) throws Exception {

        OkHttpClient httpClient = createAuthenticatedClient(username, password);
        // execute request
        doRequest(httpClient, url);
    }


    private void switchActivity(){
        Intent switchActivityIntent = new Intent(this, Welcome.class);
        switchActivityIntent.putExtra("public_id", eingeloggterUser);
        startActivity(switchActivityIntent);
    }

}
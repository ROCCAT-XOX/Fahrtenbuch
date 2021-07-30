package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fahrtenbuch extends AppCompatActivity {

    final ArrayList<ListItem_Fahrten> fahrt_list = new ArrayList<ListItem_Fahrten>();
    final ArrayList<String> list_for_export = new ArrayList<String>();

    ListView lv;

    Button button_export;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fahrtenbuch);

        button_export = findViewById(R.id.button_export);

        lv = (ListView) findViewById(R.id.list_fahrten);

        try {
            getAlleFahrten("http://10.0.2.2:5000/fahrt");
        } catch (IOException e) {
            e.printStackTrace();
        }


        button_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCSV(lv);
            }
        });
    }

    public void processCSV(View view) {

        try {
            boolean writePermissionStatus = checkStoragePermission(false);
            //Check for permission
            if (!writePermissionStatus) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            } else {
                boolean writePermissionStatusAgain = checkStoragePermission(true);
                if (!writePermissionStatusAgain) {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    //Permission Granted. Export
                    exportDataToCSV();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String toCSV(String[] array) {
        String result = "";
        if (array.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : array) {
                sb.append(s.trim()).append(",");
            }
            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }


    private void exportDataToCSV() throws IOException {


        String csvData = "";

        for (int i = 0; i < fahrt_list.size(); i++) {

            String currentLIne = list_for_export.get(i);
            String[] cells = currentLIne.split(",");

            csvData += toCSV(cells) + "\n";

        }


        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String uniqueFileName = "FileName.csv";
        File file = new File(directory, uniqueFileName);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(csvData);
        fileWriter.flush();
        fileWriter.close();

        Toast.makeText(Fahrtenbuch.this, "File Exported Successfully", Toast.LENGTH_SHORT).show();

    }


    private boolean checkStoragePermission(boolean showNotification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (showNotification) showNotificationAlertToAllowPermission();
                return false;
            }
        } else {
            return true;
        }
    }


    private void showNotificationAlertToAllowPermission() {
        new AlertDialog.Builder(this).setMessage("Please allow Storage Read/Write permission for this app to function properly.").setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).setNegativeButton("Cancel", null).show();

    }

    private void getAlleFahrten(String url)throws IOException {
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


                    Fahrtenbuch.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jobject = new JSONObject(myResponse);
                                JSONArray jsonArray = jobject.getJSONArray("fahrten");

                                for (int i=0; i < jsonArray.length(); i++)
                                {
                                    try {
                                        JSONObject oneObject = jsonArray.getJSONObject(i);

                                        Integer id = oneObject.getInt("fahrt_id");
                                        Integer reservierungs_id = oneObject.getInt("reservierungs_id");
                                        Integer fahrzeug_id = oneObject.getInt("fahrzeug_id");
                                        String public_id = oneObject.getString("public_id");
                                        String start = oneObject.getString("start");
                                        String ziel = oneObject.getString("ende");
                                        Double strecke = oneObject.getDouble("meter");


                                        fahrt_list.add(new ListItem_Fahrten(id,reservierungs_id, public_id, start, ziel, strecke.floatValue(), fahrzeug_id));
                                    } catch (JSONException e) {
                                        // Oops
                                    }
                                }
                                for(int i = 0; i<fahrt_list.size(); i++){
                                    list_for_export.add(fahrt_list.get(i).getFahrt_id().toString() + "," + fahrt_list.get(i).getReservierungs_id().toString() + "," +
                                            fahrt_list.get(i).getFahrzeug_id().toString() + "," + fahrt_list.get(i).getPublic_id() + "," +
                                            fahrt_list.get(i).getEntfernung() + "," + fahrt_list.get(i).getStart() + "," + fahrt_list.get(i).getZiel());
                                }
                                final AdvancedAdapter_Fahrten advancedAdapter = new AdvancedAdapter_Fahrten(getBaseContext(), fahrt_list);
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
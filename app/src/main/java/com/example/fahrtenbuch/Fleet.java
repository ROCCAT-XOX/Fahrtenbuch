package com.example.fahrtenbuch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class Fleet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet);

        ListView lv = (ListView) findViewById(R.id.list);

        final ArrayList<ListItem> eintraege_liste = new ArrayList<ListItem>();
        for (int i=0; i<15; i++){
            eintraege_liste.add(new ListItem(1,"mercedes", "aklasse", 0f));
        }

        final AdvancedAdapter advancedAdapter = new AdvancedAdapter(this, eintraege_liste);
        lv.setAdapter(advancedAdapter);
    }
}
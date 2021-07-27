package com.example.fahrtenbuch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdvancedAdapter_MyReservations extends ArrayAdapter<ListItem_MyReservations> {

    public AdvancedAdapter_MyReservations(Context context, ArrayList<ListItem_MyReservations> list){
        super(context, 0, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View element = convertView;
        if (element == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            element = inflater.inflate(R.layout.advanced_list_item_myreservations, null);
        }



        TextView tv_list_myRId = element.findViewById(R.id.tv_list_myRId);
        tv_list_myRId.setText("ID: " + getItem(position).getReservation_id().toString());

        TextView tv_list_start = element.findViewById(R.id.tv_list_start);
        tv_list_start.setText("Start: " + getItem(position).getStart());

        TextView tv_list_ziel = element.findViewById(R.id.tv_list_ziel);
        tv_list_ziel.setText("Ziel: " + getItem(position).getZiel());

        TextView tv_list_entfernung = element.findViewById(R.id.tv_list_entfernung);
        tv_list_entfernung.setText("Entfernung: " + getItem(position).getEntfernung().toString());

        TextView tv_list_fahrzeug = element.findViewById(R.id.tv_list_fahrzeug);
        tv_list_fahrzeug.setText("Fahrzeug: " + getItem(position).getCar_id().toString());

        return element;

    }
}

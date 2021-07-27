package com.example.fahrtenbuch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdvancedAdapter_Fleet extends ArrayAdapter<ListItem_Fleet> {

    public AdvancedAdapter_Fleet(Context context, ArrayList<ListItem_Fleet> list){
        super(context, 0, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View element = convertView;
        if (element == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            element = inflater.inflate(R.layout.advanced_list_item_fleet, null);
        }



        TextView tv_list_id = element.findViewById(R.id.tv_list_id);
        tv_list_id.setText("ID:" +"\n" +getItem(position).getCar_id().toString());

        TextView tv_list_marke = element.findViewById(R.id.tv_list_marke);
        tv_list_marke.setText("Marke:" + "\n" +getItem(position).getMarke());

        TextView tv_list_modell = element.findViewById(R.id.tv_list_modell);
        tv_list_modell.setText("Modell:" + "\n" +getItem(position).getModell());

        TextView tv_list_kilometerstand = element.findViewById(R.id.tv_list_kilometerstand);
        tv_list_kilometerstand.setText("Kilometerstand:" + "\n" +getItem(position).getKilometerstand().toString());

        TextView tv_list_ps = element.findViewById(R.id.tv_list_ps);
        tv_list_ps.setText("PS:" + "\n" + getItem(position).getPs());

        TextView tv_list_verfügbar = element.findViewById(R.id.tv_list_verfügbar);
        tv_list_verfügbar.setText("Verfügbar:" + "\n" + getItem(position).getVerfügbar());




        return element;

    }
}

package com.example.fahrtenbuch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdvancedAdapter_Fahrten extends ArrayAdapter<ListItem_Fahrten> {

    public AdvancedAdapter_Fahrten(Context context, ArrayList<ListItem_Fahrten> list){
        super(context, 0, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View element = convertView;
        if (element == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            element = inflater.inflate(R.layout.advanced_list_item_fahrten, null);
        }



        TextView tv_fa_id = element.findViewById(R.id.tv_fa_id);
        tv_fa_id.setText("ID:" +"\n" + getItem(position).getFahrt_id().toString());

        TextView tv_rev_id = element.findViewById(R.id.tv_rev_id);
        tv_rev_id.setText("Reservierungs_ID:" + "\n" +getItem(position).getReservierungs_id().toString());

        TextView tv_fz_id = element.findViewById(R.id.tv_fz_id);
        tv_fz_id.setText("Fahrzeug_ID:" + "\n" +getItem(position).getFahrzeug_id().toString());

        TextView tv_pu_id = element.findViewById(R.id.tv_pu_id);
        tv_pu_id.setText("Public_ID:" + "\n" +getItem(position).getPublic_id());

        TextView tv_fahrt_strecke = element.findViewById(R.id.tv_fahrt_strecke);
        tv_fahrt_strecke.setText("Strecke:" + "\n" + getItem(position).getEntfernung());

        TextView tv_fahrt_start = element.findViewById(R.id.tv_fahrt_start);
        tv_fahrt_start.setText("Start:" + "\n" + getItem(position).getStart());

        TextView tv_fahrt_ziel = element.findViewById(R.id.tv_fahrt_ziel);
        tv_fahrt_ziel.setText("Ziel:" + "\n" + getItem(position).getZiel());

        return element;

    }

}

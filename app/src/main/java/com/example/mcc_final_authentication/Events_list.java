package com.example.mcc_final_authentication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class Events_list extends ArrayAdapter<Events> {

    private Activity context;
    private List<Events> eventsList;

    public Events_list(Activity context,List<Events>eventsList){

        super(context,R.layout.att_list_layout,eventsList);
       this.context = context;
       this.eventsList = eventsList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.att_list_layout,null,true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.tv1);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.tv2);

        Events events = eventsList.get(position);
        textViewName.setText(events.getEventname());
        textViewDate.setText(events.getEventdate());
        return listViewItem;

    }


}

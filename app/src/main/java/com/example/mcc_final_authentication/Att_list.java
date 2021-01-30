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

public class Att_list extends ArrayAdapter<Student_info> {

    private Activity context;
    private List<Student_info> att_list;

    public Att_list(Activity context,List<Student_info> att_list){

        super(context,R.layout.att_list_layout,att_list);
        this.context = context;
        this.att_list= att_list;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.att_list_layout,null,true);

        TextView textviewName = (TextView) listViewItem.findViewById(R.id.tv1);
        TextView textviewcid = (TextView) listViewItem.findViewById(R.id.tv2);

        Student_info student = att_list.get(position);

        textviewName.setText(student.getFullname());
        textviewcid.setText(student.getCollegeid());

        return listViewItem;


    }
}


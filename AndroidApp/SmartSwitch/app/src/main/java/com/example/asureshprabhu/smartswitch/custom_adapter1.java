package com.example.asureshprabhu.smartswitch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ASureshPrabhu on 7/5/2017.
 */

public class custom_adapter1 extends ArrayAdapter<String> {
    private ArrayList<String> assigned_switches;
    private ArrayList<Integer> switch_status;
    private Activity context;
    public custom_adapter1(Activity context, ArrayList<String> assigned_switches, ArrayList<Integer> switch_status) {
        super(context, R.layout.device, assigned_switches);
        this.context=context;
        this.assigned_switches = assigned_switches;
        this.switch_status = switch_status;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.device, null, true);
        TextView deviceName = (TextView) listViewItem.findViewById(R.id.device_name);
        deviceName.setText(assigned_switches.get(position));
        if(switch_status.get(position)==1)
            listViewItem.setBackgroundColor(Color.GREEN);
        else if(switch_status.get(position)==2)
            listViewItem.setBackgroundColor(Color.YELLOW);
        else
            listViewItem.setBackgroundColor(Color.WHITE);
        return  listViewItem;
    }
}

package com.example.asureshprabhu.smartswitch;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ASureshPrabhu on 7/6/2017.
 */

public class custom_adapter2 extends ArrayAdapter<String> {
    private ArrayList<String> deviceNames;
    private ArrayList<String> startTimes;
    private ArrayList<String> endTimes;
    private ArrayList<String> unames;
    private Activity context;
    private int current_time;
    public custom_adapter2(Activity context, ArrayList<String> deviceNames, ArrayList<String> startTimes,ArrayList<String> endTimes ) {
        super(context, R.layout.device, deviceNames);
        this.context=context;
        this.deviceNames = deviceNames;
        this.startTimes = startTimes;
        this.endTimes = endTimes;
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        current_time = Integer.parseInt(sdf.format(new Date()));
        this.unames = new ArrayList<String>();
    }
    public custom_adapter2(Activity context, ArrayList<String> deviceNames, ArrayList<String> startTimes,ArrayList<String> endTimes, ArrayList<String> unames) {
        super(context, R.layout.device, deviceNames);
        this.context=context;
        this.deviceNames = deviceNames;
        this.startTimes = startTimes;
        this.endTimes = endTimes;
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        current_time = Integer.parseInt(sdf.format(new Date()));
        this.unames = unames;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.task, null, true);
        TextView deviceName = (TextView) listViewItem.findViewById(R.id.deviceName);
        TextView startTime = (TextView) listViewItem.findViewById(R.id.startTime);
        TextView endTime = (TextView) listViewItem.findViewById(R.id.endTime);
        TextView uname = (TextView)listViewItem.findViewById(R.id.uname);
        int stime = Integer.parseInt(startTimes.get(position));
        int etime = Integer.parseInt(endTimes.get(position));
        if(current_time>=stime&&current_time<etime)
            listViewItem.setBackgroundColor(Color.GREEN);
        else
            listViewItem.setBackgroundColor(Color.WHITE);
        deviceName.setText("SWITCH " + deviceNames.get(position));
        String st = startTimes.get(position).substring(0, 2) + ":" + startTimes.get(position).substring(2, startTimes.get(position).length());
        String et = endTimes.get(position).substring(0, 2) + ":" + endTimes.get(position).substring(2, endTimes.get(position).length());
        startTime.setText("Start Time : " + st);
        if(et.equals("99:99"))
        endTime.setText("End Time : Unspecified");
        else
        endTime.setText("End Time : " + et);
        if(unames.size()>0)
        uname.setText("User : " +unames.get(position));
        return  listViewItem;
    }
}

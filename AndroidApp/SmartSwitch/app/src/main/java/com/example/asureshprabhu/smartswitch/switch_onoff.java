package com.example.asureshprabhu.smartswitch;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;


public class switch_onoff extends AppCompatActivity {
    ListView switch_list;
    ArrayList<String> device_names;
    MqttAndroidClient client = null;
    ArrayList<Integer> assigned_switches;
    ArrayList<String> unames;
    ArrayList<Integer> switches, switch_status, start_times,end_times;
    RequestHandler rh;
    String uname = "";
    custom_adapter1 cust_adap;
    HashMap<Integer,Integer> status_map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_onoff);
        start_times = new ArrayList<Integer>();
        end_times = new ArrayList<Integer>();
        unames = new ArrayList<String>();
        switches = new ArrayList<Integer>();
        switch_status = new ArrayList<Integer>();
        uname = getIntent().getStringExtra("uname");
        rh = new RequestHandler(switch_onoff.this);
        switch_list = (ListView) findViewById(R.id.switch_list);
        HashMap<Integer, String> hmap = new HashMap<Integer, String>();
        hmap.put(1, "SWITCH 1");
        hmap.put(2, "SWITCH 2");
        hmap.put(3, "SWITCH 3");
        hmap.put(4, "SWITCH 4");
        device_names = new ArrayList<String>();
        assigned_switches = new ArrayList<Integer>();
        status_map = new HashMap<Integer, Integer>();
        final String switch_acess = getIntent().getStringExtra("switch_access");
        for(int i=0;i<switch_acess.length();i++) {
            if(Character.getNumericValue(switch_acess.charAt(i))==1) {
                assigned_switches.add(i+1);
                device_names.add(hmap.get(i+1));
                status_map.put(i+1,3);
            }
            else
                status_map.put(i+1,4);
        }
        if(assigned_switches.size()==0) {
            Toast.makeText(this, "You do not have permission for any switches", Toast.LENGTH_SHORT).show();
            return;
        }
        // cust_adap = new custom_adapter1(this, device_names, switch_status);
        connect_mqtt();
        new fetchtasks().execute();
        //switch_list.setAdapter(cust_adap);
        switch_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long id) {
                if (switch_status.get(position)==3) {
                    new switch_device(position,1,String.valueOf(assigned_switches.get(position))).execute(uname);
                } else if (switch_status.get(position)==2) {
                    Toast.makeText(switch_onoff.this, "Access Denied", Toast.LENGTH_SHORT).show();
                }else{
                    new switch_device(position,0,String.valueOf(assigned_switches.get(position))).execute(uname,String.valueOf(assigned_switches.get(position)));
                }
            }
        });
    }
    public void connect_mqtt() {
        final ProgressDialog pd = new ProgressDialog(switch_onoff.this);
        pd.setMessage("Connecting to mqtt...");
        pd.setCancelable(false);
        pd.show();
        String MQTTHOST = "tcp://SERVER:PORT";
        String USERNAME = "USERNAME";
        String PASSWORD = "PASSWORD";
        String topicStr = "topic/smartswitch";
        MqttConnectOptions options;

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST,
                clientId);
        options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    pd.dismiss();
                    Toast.makeText(switch_onoff.this, "Connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(switch_onoff.this, "Failed", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void onBackPressed() {
        finish();
    }
    private class fetchtasks extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(switch_onoff.this);
        @Override
        protected String doInBackground(String... id) {
            HashMap<String, String> data = new HashMap<String, String>();
            return rh.sendPostRequest("GetAllHistory.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting current status....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            parse_data(s);
        }
    }
    public void parse_data(String jsonstr){
        Log.e("TAG", jsonstr);
        try {
            JSONObject jo = new JSONObject(jsonstr);
            JSONArray ja = jo.getJSONArray("Results");
            if (ja.length() > 0) {
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject c = ja.getJSONObject(i);
                    switches.add(c.getInt("switch"));
                    start_times.add(c.getInt("stime"));
                    end_times.add(c.getInt("etime"));
                    unames.add(c.getString("uname"));
                }
            } else {
                Toast.makeText(this,"No Tasks Set",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this,"Connection Error:Something Went Wrong. Try again later.",Toast.LENGTH_SHORT).show();
        }
        display_status();
    }
    public void display_status(){
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        int current_time = Integer.parseInt(sdf.format(new Date()));
        for(int i=0;i<switches.size();i++){
            if(current_time>= start_times.get(i) && current_time < end_times.get(i))
                if(unames.get(i).equals(uname))
                    status_map.put(switches.get(i),1);
                else
                    status_map.put(switches.get(i),2);
            }
        for(int i=0;i<assigned_switches.size();i++) {
            switch_status.add(status_map.get(assigned_switches.get(i)));
        }
        cust_adap = new custom_adapter1(switch_onoff.this,device_names,switch_status);
        switch_list.setAdapter(cust_adap);
    }

    private class switch_device extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(switch_onoff.this);
        private int position;
        private int operation;
        private String switch_index;

        public switch_device(int position,int operation, String switch_index){
            this.position = position;
            this.operation = operation;
            this.switch_index = switch_index;
        }
        @Override
        protected String doInBackground(String... id) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("username",id[0]);
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
            String current_time = sdf.format(new Date());
            if(operation == 1) {
                data.put("stime", current_time);
                data.put("etime","9999");
            }
            else {
                data.put("etime", current_time);
                data.put("stime","9999");
            }
            data.put("switch",switch_index);
            sdf = new SimpleDateFormat("dd");
            String day = sdf.format(new Date());
            data.put("date",day);
            return rh.sendPostRequest("SwitchOnOff.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Sending signal....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s.equals("500")) {
                String topic = "topic/hello";
                String message = switch_index + String.valueOf(operation);
                try {
                   client.publish(topic, message.getBytes(), 0, false);
                } catch (MqttException e) {
                    Toast.makeText(switch_onoff.this,"Encountered an error",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(operation==1) {
                    switch_status.set(position, 1);
                    switch_list.getChildAt(position).setBackgroundColor(Color.GREEN);
                }else{
                    switch_status.set(position, 3);
                    switch_list.getChildAt(position).setBackgroundColor(Color.WHITE);
                }
            }else{
                Toast.makeText(switch_onoff.this,"Encountered an error",Toast.LENGTH_SHORT).show();
            }
        }
    }


}


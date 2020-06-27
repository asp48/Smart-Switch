package com.example.asureshprabhu.smartswitch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class dashboard extends AppCompatActivity {
    RequestHandler rh;
    Button profile;
    Button switch_on_off;
    Button timer;
    Button settings;
    MqttAndroidClient client = null;
    String uname = "", pswd ="";
    String asigned_switches = "0000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        profile = (Button) findViewById(R.id.all_tasks);
        switch_on_off = (Button) findViewById(R.id.switch_on_off);
        timer = (Button) findViewById(R.id.timer);
        settings = (Button) findViewById(R.id.settings);
        uname = getIntent().getStringExtra("uname");
        pswd = getIntent().getStringExtra("pswd");
        rh = new RequestHandler(dashboard.this);
        new fetchSwitchList().execute(uname);
    }

    public void open_all_tasks(View view){
        Intent i = new Intent(this,AllTasks.class);
        startActivity(i);
    }
    public void open_switch_on_off(View view){
        Intent i = new Intent(this,switch_onoff.class);
        i.putExtra("switch_access",asigned_switches);
        i.putExtra("uname",uname);
        startActivity(i);
    }
    public void open_timer(View view){
        Intent i = new Intent(this,timer.class);
        i.putExtra("switch_access",asigned_switches);
        i.putExtra("uname",uname);
        startActivity(i);
    }
    public void open_settings(View view){
        Intent i = new Intent(this,settings.class);
        i.putExtra("uname",uname);
        i.putExtra("switch_access",asigned_switches);
        i.putExtra("pswd",pswd);
        startActivity(i);
    }
    private class fetchSwitchList extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(dashboard.this);
        @Override
        protected String doInBackground(String... id) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("uname",uname);
            return rh.sendPostRequest("switch_list.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting Access Details....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s.contains("4"))
                Toast.makeText(dashboard.this,"encountered error while fetching access details",Toast.LENGTH_SHORT).show();
            else
                asigned_switches = s;

        }
    }

}

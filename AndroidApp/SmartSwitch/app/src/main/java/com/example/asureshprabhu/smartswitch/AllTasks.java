package com.example.asureshprabhu.smartswitch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AllTasks extends AppCompatActivity {
    ArrayList<String> device_names;
    ArrayList<String> start_times;
    ArrayList<String> end_times;
    ArrayList<String> unames;
    ArrayList<String> ids;
    RequestHandler rh;
    custom_adapter2 cust_adap2;
    ListView task_list ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
        rh = new RequestHandler(AllTasks.this);
        device_names = new ArrayList<String>();
        start_times = new ArrayList<String>();
        end_times = new ArrayList<String>();
        ids = new ArrayList<String>();
        unames = new ArrayList<String>();
        task_list = (ListView)findViewById(R.id.task_list);
        cust_adap2 = new custom_adapter2(this, device_names, start_times, end_times, unames);
        task_list.setAdapter(cust_adap2);
        new fetchtasks().execute();
    }
    private class fetchtasks extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(AllTasks.this);
        @Override
        protected String doInBackground(String... id) {
            HashMap<String, String> data = new HashMap<String, String>();
            return rh.sendPostRequest("GetAllHistory.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Tasks....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            load_history(s);
        }
    }
    public void load_history(String jsonstr){
        Log.e("TAG", jsonstr);
        try {
            JSONObject jo = new JSONObject(jsonstr);
            JSONArray ja = jo.getJSONArray("Results");
            if (ja.length() > 0) {
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject c = ja.getJSONObject(i);
                    ids.add(c.getString("id"));
                    device_names.add(String.valueOf(c.getInt("switch")));
                    int tmp = c.getInt("stime");
                    String tmp_str="";
                    if(tmp<10) tmp_str = "000"+ String.valueOf(tmp);
                    else if(tmp<100) tmp_str = "00"+ String.valueOf(tmp);
                    else if(tmp<1000) tmp_str = "0"+ String.valueOf(tmp);
                    else tmp_str = String.valueOf(tmp);
                    start_times.add(tmp_str);
                    tmp = c.getInt("etime");
                    tmp_str = "";
                    if(tmp<10) tmp_str = "000"+ String.valueOf(tmp);
                    else if(tmp<100) tmp_str = "00"+ String.valueOf(tmp);
                    else if(tmp<1000) tmp_str = "0"+ String.valueOf(tmp);
                    else tmp_str = String.valueOf(tmp);
                    end_times.add(tmp_str);
                    unames.add(c.getString("uname"));
                }
                cust_adap2.notifyDataSetChanged();
            } else {
                Toast.makeText(this,"No Tasks",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this,"Connection Error:Something Went Wrong. Try again later.",Toast.LENGTH_SHORT).show();
        }
    }
}

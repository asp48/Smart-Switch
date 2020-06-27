package com.example.asureshprabhu.smartswitch;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import org.eclipse.paho.client.mqttv3.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class timer extends AppCompatActivity {
    ListView task_list;
    Button add_task;
    ArrayList<String> deviceNames;
    ArrayList<String> device_names;
    ArrayList<String> start_times;
    ArrayList<String> end_times;
    ArrayList<String> ids;
    MqttAndroidClient client = null;
    String uname = "",stime="",etime="",topic="topic/hello";
    custom_adapter2 cust_adap2;
    Spinner spinner;
    EditText start_time,end_time;
    RequestHandler rh;
    ArrayList<Integer> assigned_switches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        rh = new RequestHandler(timer.this);
        device_names = new ArrayList<String>();
        start_times = new ArrayList<String>();
        end_times = new ArrayList<String>();
        ids = new ArrayList<String>();
        assigned_switches = new ArrayList<Integer>();
        deviceNames = new ArrayList<String>();
        cust_adap2 = new custom_adapter2(this, device_names, start_times, end_times);
        String switch_acess = getIntent().getStringExtra("switch_access");
        uname = getIntent().getStringExtra("uname");
        task_list = (ListView) findViewById(R.id.task_list);
        add_task = (Button) findViewById(R.id.add_task);
        for(int i=0;i<switch_acess.length();i++) {
            if(Character.getNumericValue(switch_acess.charAt(i))==1) {
                assigned_switches.add(i+1);
                deviceNames.add("SWITCH " + String.valueOf(i+1));
            }
        }
        if(assigned_switches.size()==0) {
            Toast.makeText(this, "You do not have permission for any switches", Toast.LENGTH_SHORT).show();
            return;
        }
        connect_mqtt();
        new fetchhistory().execute(uname);
        task_list.setAdapter(cust_adap2);
        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(timer.this);
                dialog.setContentView(R.layout.task_entry);
                dialog.setTitle("Enter Task");
                spinner = (Spinner) dialog.findViewById(R.id.switch_list);
                Button submit = (Button) dialog.findViewById(R.id.submit);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                start_time = (EditText) dialog.findViewById(R.id.startTime);
                end_time = (EditText) dialog.findViewById(R.id.endTime);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(timer.this, android.R.layout.simple_spinner_item, deviceNames);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
                dialog.show();
                start_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker = new TimePickerDialog(timer.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String time = (selectedHour < 10 ? "0" : "") + selectedHour + ":" + (selectedMinute < 10 ? "0" : "") + selectedMinute;
                                start_time.setText(time);
                            }
                        }, hour, minute, false);
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                });
                end_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker = new TimePickerDialog(timer.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String time = (selectedHour < 10 ? "0" : "") + selectedHour + ":" + (selectedMinute < 10 ? "0" : "") + selectedMinute;
                                end_time.setText(time);
                            }
                        }, hour, minute, false);
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stime = start_time.getText().toString().replace(":","");
                        etime = end_time.getText().toString().replace(":","");
                        dialog.dismiss();
                        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
                        int current_time = Integer.parseInt(sdf.format(new Date()));
                        sdf = new SimpleDateFormat("dd");
                        int current_day = Integer.parseInt(sdf.format(new Date()));
                        String day = "";
                        if(current_time < Integer.parseInt(stime))
                            day = String.valueOf(current_day);
                        else
                            day = String.valueOf(current_day + 1);
                        String switch_no = Integer.toString(assigned_switches.get(spinner.getSelectedItemPosition()));
                        check_timer(uname,stime,etime,switch_no,day);
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        task_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, final int position,
                                    long id) {
                Drawable background = task_list.getChildAt(position).getBackground();
                if (background instanceof ColorDrawable) {
                    int color = ((ColorDrawable) background).getColor();
                    if(color == Color.GREEN) {
                        Toast.makeText(timer.this, "Active Task cannot be changed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                final Dialog dialog = new Dialog(timer.this);
                dialog.setContentView(R.layout.options);
                Button modify = (Button) dialog.findViewById(R.id.modify);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                dialog.show();
                modify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        open_modify_dialog(position);
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        new modify_cancel(2,position,"NNNN","NNNN").execute(ids.get(position));
                    }
                });

            }
        });

    }
    public void open_modify_dialog(final int position){
        final Dialog dialog = new Dialog(timer.this);
        dialog.setContentView(R.layout.modify);
        final EditText s_time = (EditText) dialog.findViewById(R.id.s_time);
        final EditText e_time = (EditText) dialog.findViewById(R.id.e_time);
        Button query_submit = (Button) dialog.findViewById(R.id.m_submit);
        dialog.show();
        s_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(timer.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time = (selectedHour < 10 ? "0" : "") + selectedHour + ":" + (selectedMinute < 10 ? "0" : "") + selectedMinute;
                        s_time.setText(time);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        e_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker = new TimePickerDialog(timer.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String time = (selectedHour < 10 ? "0" : "") + selectedHour + ":" + (selectedMinute < 10 ? "0" : "") + selectedMinute;
                        e_time.setText(time);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        query_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG",position+" "+s_time.getText().toString()+" "+e_time.getText().toString()+" "+ids.get(position));
                new modify_cancel(1,position,s_time.getText().toString(),e_time.getText().toString()).execute(ids.get(position));
                dialog.dismiss();
            }
        });
    }

       public void check_timer(String uname, final String stime, final String etime, final String switch_no, String day){
           class access_control extends AsyncTask<String, Void, String> {
               private ProgressDialog Dialog = new ProgressDialog(timer.this);

               @Override
               protected String doInBackground(String... unm) {
                   HashMap<String, String> data = new HashMap<String, String>();
                   data.put("username",unm[0]);
                   data.put("stime",unm[1]);
                   data.put("etime",unm[2]);
                   data.put("switch", unm[3]);
                   data.put("date",unm[4]);
                   return rh.sendPostRequest("AddTimer.php", data);
               }

               @Override
               protected void onPreExecute() {
                   Dialog.setMessage("Verifying access.....");
                   Dialog.setCancelable(false);
                   Dialog.show();
               }

               @Override
               protected void onPostExecute(String s) {
                   Dialog.dismiss();
                   Log.e("TAG", s);
                   switch (s) {
                       case "300":
                           Toast.makeText(getBaseContext(), "Couldn't Connect. Check your Internet Settings", Toast.LENGTH_SHORT).show();
                           break;
                       case "310":
                           Toast.makeText(getBaseContext(), "Connection Timeout: Taking longer than usual. Try again later", Toast.LENGTH_SHORT).show();
                           break;
                       case "400":
                           Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();
                           break;
                       case "410":
                           Toast.makeText(getBaseContext(), "Connection Error:Something Went Wrong.Try again later", Toast.LENGTH_SHORT).show();
                           break;
                       default:
                           authenticate(s,switch_no+stime+etime);
                   }
               }


           }

           access_control l = new access_control();
           l.execute(uname,stime,etime,switch_no,day);
       }


        public void connect_mqtt() {
        final ProgressDialog pd = new ProgressDialog(timer.this);
        pd.setMessage("Connecting to mqtt...");
        pd.setCancelable(false);
        pd.show();
        String MQTTHOST = "tcp://m10.cloudmqtt.com:12477";
        String USERNAME = "dmwykdya";
        String PASSWORD = "6rV3g_aiiQyF";

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
                    Toast.makeText(timer.this, "Connected", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    pd.dismiss();
                    Toast.makeText(timer.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        finish();
    }

    public void authenticate(String s, String message){
        if(s.contains("500")){
            device_names.add(String.valueOf(assigned_switches.get(spinner.getSelectedItemPosition())));
            start_times.add(start_time.getText().toString().replace(":",""));
            end_times.add(end_time.getText().toString().replace(":",""));
            cust_adap2.notifyDataSetChanged();
            int id;
            if(s.split("_").length > 1)
                id = Integer.parseInt(s.split("_")[1]);
            else{
                Toast.makeText(timer.this,"Encountered an error",Toast.LENGTH_SHORT).show();
                return;
            }
            ids.add(String.valueOf(id));
            String task_id;
            if(id<10) task_id = "00"+String.valueOf(id);
            else if(id<100) task_id = "0"+String.valueOf(id);
            else task_id = String.valueOf(id);
            message = task_id + message;
            topic = "topic/hello";
            try {
                Log.e("TAG2",message);
                client.publish(topic, message.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this,"Timer is already set for the given time",Toast.LENGTH_SHORT).show();
        }
    }

    private class fetchhistory extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(timer.this);
        @Override
        protected String doInBackground(String... id) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("uname", id[0]);
            return rh.sendPostRequest("GetActivityHistory.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching History....");
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
                    device_names.add(c.getString("switch"));
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
                }
                cust_adap2.notifyDataSetChanged();
            } else {
                Toast.makeText(this,"No Tasks",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this,"Connection Error:Something Went Wrong. Try again later.",Toast.LENGTH_SHORT).show();
        }
    }
    private class modify_cancel extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(timer.this);
        private int operation;
        private int position;
        private String stime;
        private String etime;
        public modify_cancel(int opertation, int position, String stime, String etime){
            this.operation = opertation;
            this.position = position;
            this.stime = stime.replace(":","");
            this.etime = etime.replace(":","");
        }
        @Override
        protected String doInBackground(String... id) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("id",id[0]);
            data.put("stime",stime);
            data.put("etime",etime);
            data.put("flag",String.valueOf(operation));
            return rh.sendPostRequest("TimerModifyCancel.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Applying Changes....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s.equals("500")){
                Toast.makeText(timer.this,"Changes Applied Successfully",Toast.LENGTH_SHORT).show();
                String task_id, message;
                int id = Integer.parseInt(ids.get(position));
                if(id<10) task_id = "00"+String.valueOf(id);
                else if(id<100) task_id = "0"+String.valueOf(id);
                else task_id = String.valueOf(id);
                if(operation == 1){
                    message = task_id + device_names.get(position)+ stime+etime;
                    start_times.set(position,stime);
                    end_times.set(position,etime);
                    topic = "topic/hello";
                    try {
                        Log.e("TAG2",message);
                        client.publish(topic, message.getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                        return;
                    }
                }else if(operation == 2){
                    message = task_id + device_names.get(position)+"NNNNNNNN";
                    ids.remove(position);
                    device_names.remove(position);
                    start_times.remove(position);
                    end_times.remove(position);
                    topic = "topic/hello";
                    try {
                        Log.e("TAG2",message);
                        client.publish(topic, message.getBytes(), 0, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                cust_adap2.notifyDataSetChanged();
            }else
                Toast.makeText(timer.this,"Encountered an error",Toast.LENGTH_SHORT).show();
        }
    }
}


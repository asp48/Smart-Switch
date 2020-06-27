package com.example.asureshprabhu.smartswitch;

import android.app.ProgressDialog;
import android.location.SettingInjectorService;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class settings extends AppCompatActivity {
    EditText cur_pswd, cnf_pswd, new_pswd;
    Button submit_pswd, ch_pswd;
    TextView uname, switch_access;
    String username, pswd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cur_pswd = (EditText)findViewById(R.id.cur_pswd);
        new_pswd = (EditText)findViewById(R.id.new_pswd);
        cnf_pswd = (EditText)findViewById(R.id.cnf_pswd);
        submit_pswd = (Button)findViewById(R.id.submit_pswd);
        ch_pswd = (Button)findViewById(R.id.ch_pswd);
        uname = (TextView)findViewById(R.id.uname);
        switch_access = (TextView)findViewById(R.id.switch_access);
        username = getIntent().getStringExtra("uname");
        pswd = getIntent().getStringExtra("pswd");
        String assigned_switches = getIntent().getStringExtra("switch_access");
        String temp_str = "";
        for( int i=0;i<assigned_switches.length();i++)
        {
            if(assigned_switches.charAt(i)=='1')
                temp_str += "S" + String.valueOf(i+1) + "  ";
        }
        uname.setText(username);
        if(temp_str.equals(""))
            switch_access.setText("No permissions");
        else
            switch_access.setText(temp_str);
        ch_pswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                cur_pswd.setText("");
                new_pswd.setText("");
                cnf_pswd.setText("");
                cur_pswd.setVisibility(View.VISIBLE);
                new_pswd.setVisibility(View.VISIBLE);
                cnf_pswd.setVisibility(View.VISIBLE);
                submit_pswd.setVisibility(View.VISIBLE);
            }
        });
        submit_pswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cur_pswd.getText().toString().equals("") || cnf_pswd.getText().toString().equals("") || new_pswd.getText().toString().equals(""))
                    Toast.makeText(settings.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else if(!cur_pswd.getText().toString().equals(pswd))
                    Toast.makeText(settings.this, "Current Password is Incorrect", Toast.LENGTH_SHORT).show();
                else if(!new_pswd.getText().toString().equals(cnf_pswd.getText().toString()))
                    Toast.makeText(settings.this, "Confirm Password is Incorrect", Toast.LENGTH_SHORT).show();
                else{
                    new updatePswd().execute(username, new_pswd.getText().toString());
                }

            }
        });
    }
    private class updatePswd extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(settings.this);
        @Override
        protected String doInBackground(String... id) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("uname",id[0]);
            data.put("pswd",id[1]);
            RequestHandler rh = new RequestHandler(settings.this);
            return rh.sendPostRequest("UpdatePswd.php", data);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Updating Password....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s.equals("500")) {
                cur_pswd.setVisibility(View.GONE);
                new_pswd.setVisibility(View.GONE);
                cnf_pswd.setVisibility(View.GONE);
                submit_pswd.setVisibility(View.GONE);
                ch_pswd.setVisibility(View.VISIBLE);
                Toast.makeText(settings.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(settings.this, "Failed to update..Please try again later", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.asureshprabhu.smartswitch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class signup extends AppCompatActivity {

    String uname,pswd,ch_pswd;
    Button sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }


    public void reg(View view){
        uname = ((EditText)findViewById(R.id.uname)).getText().toString();
        pswd = ((EditText)findViewById(R.id.pswd)).getText().toString();
        ch_pswd = ((EditText)findViewById(R.id.ch_pswd)).getText().toString();
        sign_up = (Button)findViewById(R.id.signup);
        if(uname.equals("") || pswd.equals("")|| ch_pswd.equals(""))
            Toast.makeText(this,"Please enter all the fields",Toast.LENGTH_SHORT).show();
        else if(!pswd.equals(ch_pswd))
            Toast.makeText(this,"Passwords don't match",Toast.LENGTH_SHORT).show();
        else
            check_register(uname,pswd);
    }

    private void check_register (String uname,String pswd) {

        class register extends AsyncTask<String, Void, String> {
            private ProgressDialog Dialog = new ProgressDialog(signup.this);

            @Override
            protected String doInBackground(String... unm) {
                RequestHandler rh = new RequestHandler(signup.this);
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uname", unm[0]);
                data.put("pswd",unm[1]);

                return rh.sendPostRequest("NewUser.php", data);
            }

            @Override
            protected void onPreExecute() {
                Dialog.setMessage("Signing up.....");
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
                        authenticate(s);
                }
            }


        }


        register l = new register();
        l.execute(uname,pswd);
    }

    public void authenticate(String s) {
        if (!s.equals("500"))
            Toast.makeText(getBaseContext(), "Username already exists ", Toast.LENGTH_SHORT).show();

        else {
            Intent i = new Intent(this,LoginActivity.class);

            Toast.makeText(getBaseContext(), "Successful Registration! Login to continue!!!", Toast.LENGTH_SHORT).show();
            startActivity(i);

        }

    }

}

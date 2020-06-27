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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText uname;
    EditText pswd;
    Button signin, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uname = (EditText) findViewById(R.id.uname);
        pswd = (EditText) findViewById(R.id.pswd);
        signin = (Button) findViewById(R.id.signin);
        signup = (Button) findViewById(R.id.signup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void signin(View view) {
        if (uname.getText().toString().equals("") || pswd.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
        } else
            check_login(uname.getText().toString());
    }

    public void signup(View view) {
        Intent i = new Intent(LoginActivity.this, signup.class);
        startActivity(i);
    }

    private void check_login(String uname) {

        class login extends AsyncTask<String, Void, String> {
            private ProgressDialog Dialog = new ProgressDialog(LoginActivity.this);

            @Override
            protected String doInBackground(String... unm) {
                RequestHandler rh = new RequestHandler(LoginActivity.this);
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uname", unm[0]);
                return rh.sendPostRequest("returnpassword.php", data);
            }

            @Override
            protected void onPreExecute() {
                Dialog.setMessage("Signing in.....");
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


        login l = new login();
        l.execute(uname);
    }

    public void authenticate(String s) {
        if (s.equals("error_username"))
            Toast.makeText(getBaseContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
        else if (!s.equals(pswd.getText().toString()))
            Toast.makeText(getBaseContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
        else {
            Intent i = new Intent(LoginActivity.this, dashboard.class);
            Toast.makeText(getBaseContext(), "Successful login", Toast.LENGTH_SHORT).show();
            i.putExtra("uname", uname.getText().toString());
            i.putExtra("pswd", pswd.getText().toString());
            startActivity(i);

        }

    }
}

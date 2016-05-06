package com.example.sean.dabull;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private TextView mTextStatus;
    private TextView textReg;
    private EditText mPasswordView;
    private EditText mUsernameView;
    private ProgressDialog loading;
    static boolean state =false;//We want to catch whether JSON validated the login
    private String hasmessage="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_login);
        Bundle extras = getIntent().getExtras();

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.editText);
        mPasswordView = (EditText) findViewById(R.id.editText2);
        textReg = (TextView) findViewById(R.id.textView2);
        Button mEmailSignInButton = (Button) findViewById(R.id.SignIn);
        Intent intent = getIntent();
        String tempname = intent.getStringExtra("OtherId");
        mUsernameView.setText(tempname);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsernameView.setEnabled(false);
                mPasswordView.setEnabled(false);
                Login();
                mUsernameView.setEnabled(true);
                mPasswordView.setEnabled(true);
            }
        });
        textReg.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });

    }

    private boolean Login()
    {
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String url = Config.DATA_URL+"db_fetch.php?username="+mUsernameView.getText();

        if (!validateString.isValid(username))
        {
            Toast.makeText(getApplicationContext(), "Username invalid", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!validateString.isValid(password))
        {
            Toast.makeText(getApplicationContext(), "Password invalid", Toast.LENGTH_SHORT).show();
            return false;
        }

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Toast.makeText(getApplicationContext(), "Connection Achieved!", Toast.LENGTH_SHORT).show();
                state=showJSON(response);
            }
        },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error)
        {
            Toast.makeText(getApplicationContext(), "Connection Failure!", Toast.LENGTH_SHORT).show();
        }});

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        return true;
    }
    private boolean showJSON(String response)
    {
        String password="";
        String id ="1";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            password = collegeData.getString(Config.KEY_PASSWORD);
            id = collegeData.getString("Id");
            hasmessage = collegeData.getString("HasMessage");
        } catch (JSONException e) {e.printStackTrace();}

        if (password.equals(EncryptSHA1.SHA1(mPasswordView.getText().toString())))
        {

            //Toast.makeText(getApplicationContext(), "Login complete!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, main_frag.class);
            intent.putExtra("UserId", id);
            //intent.putExtra("HasMessage",hasmessage);
            LoginActivity.this.startActivity(intent);


            return true;
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Login failed, incorrect username or password!", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

}


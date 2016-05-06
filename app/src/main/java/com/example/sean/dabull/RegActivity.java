package com.example.sean.dabull;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sean.dabull.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via username/password.
 */
public class RegActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private TextView mTextStatus;
    private EditText mPasswordView;
    private EditText mPasswordView2;
    private EditText mUsernameView;
    private EditText mEmailView;
    private ImageButton exitButton;
    private ImageButton mailButton;
    private Button mRegisterButton;
    private View mProgressView;
    private View mLoginFormView;
    private ProgressDialog loading;
    static boolean state =false;//We want to catch whether JSON validated the login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fs_reg);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.editText2);
        mUsernameView = (EditText) findViewById(R.id.editText3);
        mPasswordView = (EditText) findViewById(R.id.editText4);
        mPasswordView2 = (EditText) findViewById(R.id.editText5);
        mTextStatus =(TextView) findViewById(R.id.textStatus);
        mRegisterButton = (Button) findViewById(R.id.button);
        exitButton = (ImageButton) findViewById(R.id.imageButton5);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextStatus.setText("Loading...");
                mUsernameView.setEnabled(false);
                mPasswordView.setEnabled(false);
                mPasswordView2.setEnabled(false);
                mEmailView.setEnabled(false);
                Register();
            }
        });
        exitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                intent.putExtra("OtherId", "");
                RegActivity.this.startActivity(intent);
            }
        });


    }

    private boolean Register() {
        final String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String password2 = mPasswordView2.getText().toString().trim();
        String email = mEmailView.getText().toString().trim();

        mUsernameView.setEnabled(true);
        mPasswordView.setEnabled(true);
        mPasswordView2.setEnabled(true);
        mEmailView.setEnabled(true);

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Config.DATA_URL + "db_pull_users.php?id=0";
        final String url2 = Config.DATA_URL + "db_create_user.php?username="+username+"&password="+
                EncryptSHA1.SHA1(password)+
                "&email="+email;
        if (!validateString.isValid(username)) {
            mTextStatus.setText("Username field invalid");
            return false;
        }
        switch (validateString.isValidPassword(password))
        {

            case -2: {mTextStatus.setText("Password invalid"); return false;}
            case -1: {mTextStatus.setText("Password left blank"); return false;}
            case 0: {mTextStatus.setText("Password not between 5 and 14 characters"); return false;}
            case 1: break;
        }
        if(!password.equals(password2))
        {
            mTextStatus.setText("Passwords do not match!");
            return false;
        }
        switch(validateString.isValidEmail(email))
        {
            case -1: {mTextStatus.setText("Email left blank"); return false;}
            case 0: {mTextStatus.setText("Email invalid"); return false;}
            case 1: break;
        }
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if(showJSON(response))
                {
                    StringRequest stringRequest2 = new StringRequest(url2, new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            mTextStatus.setText("Registration complete!");
                            mUsernameView.setEnabled(true);
                            mPasswordView.setEnabled(true);
                            mPasswordView2.setEnabled(true);
                            mEmailView.setEnabled(true);
                            mRegisterButton.setEnabled(true);
                            mUsernameView.setText("");
                            mPasswordView.setText("");
                            mPasswordView2.setText("");
                            mEmailView.setText("");
                            Intent intent = new Intent(RegActivity.this, LoginActivity.class);
                            intent.putExtra("OtherId", username);
                            RegActivity.this.startActivity(intent);
                        }
                    },new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            mTextStatus.setText("Error connecting to database");
                        }});
                    requestQueue.add(stringRequest2);
                }
            }
        },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error)
        {
            mTextStatus.setText("Error connecting to database");
        }});

        requestQueue.add(stringRequest);
        return true;
    }
    private boolean showJSON(String response)
    {
        String username, email = "";
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++)
            {
                try
                {
                    JSONObject jo = result.getJSONObject(i);
                    username = jo.getString("Username");
                    email = jo.getString("Email");
                    if (username.equals(mUsernameView.getText().toString().trim()))
                    {
                        mTextStatus.setText("Username taken!");
                        return false;
                    }
                    if (email.equals(mEmailView.getText().toString().trim()))
                    {
                        mTextStatus.setText("Email already in use");
                        return false;
                    }

                }catch (JSONException e) { e.printStackTrace();return false;}
            }
        } catch (JSONException e) { e.printStackTrace();return false;}
        mTextStatus.setText("Registering...");
        return true;

    }

}


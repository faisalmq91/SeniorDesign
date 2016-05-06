package com.example.sean.dabull;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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
 * A login screen that offers login via email/password.
 */
public class getImageLink{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private ProgressDialog loading;
    private ListView myList;
    private Button button;
    private EditText username;
    private String id2 ="1";
    private String[] images;
    private String[] unames;
    private Context context;
    private int dbSize = 0;
    public getImageLink(Context current)
    {
        context=current;
        getData();
    }
    private void showJSONUsername(String response)
    {
        String password = "";
            String id = "";
            try
            {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray db = jsonObject.getJSONArray(Config.JSON_ARRAY);
                dbSize = db.length();
                for (int i = 0; i < db.length(); i++) {
                    try {
                        JSONObject jo = db.getJSONObject(i);
                        String u1 = jo.getString("Username");
                        String u2 = jo.getString("Image");
                        images[i]=u2;
                        unames[i]=u1;
                        // extract the properties from the JSONObject and use it with the addRow() method below
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        } catch (JSONException e) {e.printStackTrace(); button.setEnabled(true);}
    }

    private boolean getData()
    {
        String url = Config.DATA_URL+"db_pull_users.php?";


        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                showJSONUsername(response);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
            }});

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        return true;
    }
    private String getImageUrl(String Username)
    {
        for(int i=0; i< dbSize; i++)
        {
            if (unames[i].equals(Username))
            {
                return images[i];
            }
        }
        return "NOT FOUND!";
    }

}


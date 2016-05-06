package com.example.sean.dabull;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
public class commentCount {

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
    public int[] count;
    private Context context;
    private int dbSize = 0;
    public boolean loaded=false;
    private RequestQueue requestQueue,requestQueueUs;
    public commentCount(Context current, RequestQueue Queue)
    {
        count = new int[150];
        context=current;
        requestQueue = Queue;
        requestQueue.stop();
        requestQueueUs = Volley.newRequestQueue(context);
        for(int i=0; i<150; i++)
        {
            count[i]=0;
        }
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
                        String u1 = jo.getString("Post_Id");
                        String u2 = jo.getString("count");
                        count[Integer.parseInt(u1)]= Integer.parseInt(u2);
                        // extract the properties from the JSONObject and use it with the addRow() method below
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        } catch (JSONException e) {e.printStackTrace(); button.setEnabled(true);}
        requestQueue.start();
    }

    private boolean getData()
    {
        String url = Config.DATA_URL+"db_pull_commnumb.php?";


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

        requestQueueUs.add(stringRequest);
        return true;
    }


}


package com.example.sean.dabull;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
public class MsgActivity extends AppCompatActivity {

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
    private EditText messageText;
    private TextView topText;
    private Context context;
    private String otherId, otherName;
    private String id = "1";
    private int[] colors = new int[] { Color.parseColor("#F0F0F0"), Color.parseColor("#D2E4FC") };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        context = getApplicationContext();
        Intent intent = getIntent();
        otherId = intent.getStringExtra("OtherId");
        otherName = intent.getStringExtra("OtherName");
        id = intent.getStringExtra("UserId");
        button = (Button) findViewById(R.id.button2);
        messageText = (EditText) findViewById(R.id.editText6);
        topText = (TextView) findViewById(R.id.textView25);
        topText.setText("Chatting with "+otherName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                pushMessage(messageText.getText().toString());
                messageText.setText("");
            }
        });
        // Set up the login form.
        getData();
    }
    private void pushMessage(String text)
    {
        text = text.replaceAll(" ", "_");
        String url = Config.DATA_URL+"db_set.php?message="+text+"&senderId="+id+"&receiverId="+otherId;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                getData();
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {}});

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getData()
    {
        String url = Config.DATA_URL+"db_pull_msg.php?id="+id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                showJSON(response);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {}});

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void showJSON(String response)
    {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            populateLV(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void populateLV(JSONArray db){
        //Matrix cursor necessary for reading JSON data into a listview
        String[] from = new String[] {BaseColumns._ID, "User1_Id", "User2_Id","null"};
        int[] to = {R.id.textView2, R.id.textView11,R.id.textView10,R.id.textView24};

        MatrixCursor mc = new MatrixCursor(from); // properties from the JSONObjects
        for (int i = 0; i < db.length(); i++) {
            try {
                JSONObject jo = db.getJSONObject(i);
                String u1 = jo.getString("User1_Id");
                String u2 = jo.getString("User2_Id");
                if ((u1.equals(id) ||  u2.equals(id)) && (u1.equals(otherId) ||  u2.equals(otherId))) {
                    mc.addRow(new Object[]{0,jo.getString("Username1"),jo.getString("Text").replaceAll("_", " "),""});
                }
                // extract the properties from the JSONObject and use it with the addRow() method below
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        myList = (ListView) findViewById(R.id.listView);
        SpecialAdapter adapter = new SpecialAdapter(this, R.layout.message_layout, mc, from, to,0);
        myList.setAdapter(adapter);
    }

}


package com.example.sean.dabull;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.net.Uri;
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
import android.text.format.DateFormat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.sql.Date;

/**
 * A login screen that offers login via email/password.
 */
public class CommentActivity extends AppCompatActivity {

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
    private TextView title;
    private TextView content;
    private TextView author;
    private EditText commentText;
    private Context context;
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    private String otherId;
    private String Id;
    private String UserId = "1";
    private int[] colors = new int[] { Color.parseColor("#f0d2b5"), Color.parseColor("#fa865f") };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expanded_post);
        context = getApplicationContext();
        Intent intent = getIntent();
        Id = intent.getStringExtra("PostId");
        UserId = intent.getStringExtra("UserId");
        button = (Button) findViewById(R.id.button7);
        title = (TextView) findViewById(R.id.textView20);
        content = (TextView) findViewById(R.id.textView18);
        author = (TextView) findViewById(R.id.textView19);
        commentText = (EditText) findViewById(R.id.editText11);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                button.setEnabled(false);
                pushMessage(commentText.getText().toString());
                commentText.setText("");
                button.setEnabled(true);
            }
        });
        //messageText = (EditText) findViewById(R.id.editText6);
        // Set up the login form.
        getData();
        getData2();
    }
    private void pushMessage(String text)
    {
        text = text.replaceAll(" ", "_");
        String url = Config.DATA_URL+"db_comment.php?message="+commentText.getText().toString().replaceAll(" ", "_").replaceAll("'", "''").replaceAll("\\\n", " ")
                +"&senderId="+UserId+"&postId="+Id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                getData2();
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
        String url = Config.DATA_URL+"db_pull_posts.php?id="+Id;
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
    private void getData2()
    {
        String url = Config.DATA_URL+"db_pull_comments.php?id="+Id;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                showJSON2(response);
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
            JSONObject jo = result.getJSONObject(0);
            title.setText(jo.getString("Title").replaceAll("_", " "));
            content.setText(jo.getString("Content").replaceAll("_", " "));
            author.setText(jo.getString("Username"));
            //populateLV(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void showJSON2(String response)
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
        String[] from = new String[] {BaseColumns._ID, "Username", "Content","Time"};
        int[] to = {R.id.textView2, R.id.textView11,R.id.textView10,R.id.textView24};

        MatrixCursor mc = new MatrixCursor(from); // properties from the JSONObjects
        for (int i = 0; i < db.length(); i++) {
            try {
                JSONObject jo = db.getJSONObject(i);
                mc.addRow(new Object[]{0,jo.getString("Username").toString(),jo.getString("Content").toString().replaceAll("_", " "),jo.getString("Time")});
                // extract the properties from the JSONObject and use it with the addRow() method below
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        myList = (ListView) findViewById(R.id.listView3);
        SpecialAdapter adapter = new SpecialAdapter(this, R.layout.message_layout, mc, from, to,0);
        myList.setAdapter(adapter);
    }

}


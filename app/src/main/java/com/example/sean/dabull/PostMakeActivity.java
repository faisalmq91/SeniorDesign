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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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
public class PostMakeActivity extends AppCompatActivity {

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
    private EditText messageTitle;
    private EditText messageText;
    private Context context;
    private String otherId;
    private String userId ="1";
    private int[] colors = new int[] { Color.parseColor("#f0d2b5"), Color.parseColor("#fa865f") };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);
        context = getApplicationContext();
        Intent intent = getIntent();
        userId = intent.getStringExtra("UserId");
        button = (Button) findViewById(R.id.button6);
       // exitButton = (ImageButton) findViewById(R.id.imageButton7);
        messageTitle = (EditText) findViewById(R.id.editText9);
        messageText = (EditText) findViewById(R.id.editText10);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                pushMessage(otherId,messageTitle.getText().toString(),messageText.getText().toString().replaceAll("\\\n"," "));
                messageText.setText("");
                messageTitle.setText("");
            }
        });
    }
    private void pushMessage(String id, String title, String text)
    {
        text = text.replaceAll(" ", "_").replaceAll("'", "''");
        title = title.replaceAll(" ", "_").replaceAll("'", "''");
        String url = Config.DATA_URL+"db_create_post.php?senderId="+userId+"&message="+text+"&title="+title;
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            { //Assuming post entered correctly, go back to the hub
                Intent intent = new Intent(PostMakeActivity.this, main_frag.class);
                intent.putExtra("UserId", userId);
                PostMakeActivity.this.startActivity(intent);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {}});

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}


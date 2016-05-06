package com.example.sean.dabull;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.NotificationCompat;

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
public class PostHubActivity extends AppCompatActivity {

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
    private ImageButton newPost;
    private ImageButton mailButton;
    private ImageButton userButton;
    private Context context;
    private String id2 ="1";
    private String hasMessage ="0";
    private commentCount commentNum;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_hub);
        requestQueue = Volley.newRequestQueue(this);
        newPost = (ImageButton) findViewById(R.id.imageButton4);
        //mailButton = (ImageButton) findViewById(R.id.imageButton6);
        //userButton = (ImageButton) findViewById(R.id.imageButton8);
        Intent intent = getIntent();
        id2 = intent.getStringExtra("UserId");
        System.out.println(id2);
        /*hasMessage = intent.getStringExtra("HasMessage");
        if(hasMessage.equals("1"))
        {
            String msg;
            Toast.makeText(getApplicationContext(), "Has message", Toast.LENGTH_SHORT).show();
        }
        */
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PostHubActivity.this, PostMakeActivity.class);
                intent.putExtra("UserId", id2);
                PostHubActivity.this.startActivity(intent);
            }
        });
        /*mailButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PostHubActivity.this, MsgHubActivity.class);
                intent.putExtra("UserId", id2);
                PostHubActivity.this.startActivity(intent);
            }
        });
        userButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PostHubActivity.this, OtherProfileActivity.class);
                intent.putExtra("UserId", id2);
                PostHubActivity.this.startActivity(intent);
            }
        });
        context = getApplicationContext();
        // Set up the login form.
        commentNum = new commentCount(this, requestQueue);
        getData();*/
    }
    private void getData()
    {
        String url = Config.DATA_URL+"db_pull_all_posts.php?id=*";
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

        requestQueue.add(stringRequest);
    }
    private void showJSON(String response)
    {
        try
        {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
        populateLV(result);
        } catch (JSONException e) {e.printStackTrace();}
    }

    private void populateLV(JSONArray db)
    {
        //Matrix cursor necessary for reading JSON data into a listview
        String[] from = new String[] {BaseColumns._ID, "Title", "Content","Username","Score","Id","Count"};
        int[] to = {R.id.textView22, R.id.textView10,R.id.textView14,R.id.textView2,R.id.textView13,R.id.textView22,R.id.textView15};

        MatrixCursor mc = new MatrixCursor(from); // properties from the JSONObjects
        String messages[] = new String[900];
        String mostrecent;
        int count=0;
        for (int i = 0; i < db.length(); i++)
        {
            try
            {
                JSONObject jo = db.getJSONObject(i);
                mostrecent = jo.getString("Content");
                if(jo.getString("Content").length()>90)
                    mostrecent = mostrecent.substring(0,90) + "...";
                mc.addRow(new Object[]{0, jo.getString("Title").replaceAll("_", " "), mostrecent.replaceAll("_", " "), jo.getString("Username"), jo.getString("Score"),jo.getString("Id"),commentNum.count[Integer.parseInt(jo.getString("Id"))]+" Comments"});
            } catch (JSONException e1)
            {
                e1.printStackTrace();
            }
        }
        myList = (ListView) findViewById(R.id.listView);
        myList.setClickable(false);
        myList.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView v = (TextView)view.findViewById(R.id.textView22);
                String itemId = v.getText().toString();

                Intent intent = new Intent(PostHubActivity.this, CommentActivity.class);
                intent.putExtra("PostId", itemId);
                intent.putExtra("UserId", id2);
                PostHubActivity.this.startActivity(intent);
                //based on item add info to intent
                //startActivity(intent);
            }


        });

        SpecialAdapter adapter = new SpecialAdapter(this, R.layout.post_layout, mc, from, to,0);
        myList.setAdapter(adapter);
        myList.setSelectionAfterHeaderView();
    }


}


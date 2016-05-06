package com.example.sean.dabull;


/**
 * Created by faisalmq91 on 4/26/16.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pushbots.push.Pushbots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//you will need to import throughout the creation of this code.
//The class needs to extend Fragment
public class post_frag extends Fragment {

    private AutoCompleteTextView mEmailView;
    private ProgressDialog loading;
    private ListView myList;
    private ImageButton newPost, searchButton;
    private ImageButton mailButton;
    private ImageButton userButton;
    private Context context;
    private String id2 ="1";
    private int count;
    private String hasMessage ="0";
    private EditText search;
    private commentCount commentNum;
    private RequestQueue requestQueue;
    //   PostHubActivity activity = (PostHubActivity) getActivity();
    //activity.myMethod();

    //The newInstance() method return the reference to  fragment
    public static post_frag newInstance() {
        post_frag fragment = new post_frag();
        return fragment;
    }
    //MyFragment is the constructor method of our class.
    //this is a java thing. Google it as you this is basic
    //java that you need to know.
    public post_frag() {
    }
    //since we have a button and textview on the xml will use these two
    //variable to connect to them.
    Button button2;
    TextView daveText;
    //this method links the fragment to the layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //connecting the fragment to our layout, new_frag
        //Intent intent = new Intent(getActivity(), PostHubActivity.class);
        //startActivity(intent);
        View rootView = inflater.inflate(R.layout.post_hub, container, false);
        /*Pushbots.sharedInstance().init(inflater.getContext());
       // Intent getIntent = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("message")) {

                String msg = extras.getString("message");
                AlertDialog.Builder alert = new AlertDialog.Builder(inflater.getContext());
                alert.setTitle("Alert Message :");
                alert.setMessage(msg);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });;
                alert.show();

            }
        }
*/
        Intent intent = getActivity().getIntent();
        id2 = intent.getStringExtra("UserId");
        requestQueue = Volley.newRequestQueue(getContext());
        newPost = (ImageButton) rootView.findViewById(R.id.imageButton4);
        searchButton = (ImageButton) rootView.findViewById(R.id.imageButton3);
        search = (EditText) rootView.findViewById(R.id.editText8);

        System.out.println(id2);

        /*hasMessage = intent.getStringExtra("HasMessage");
        if(hasMessage.equals("1"))
        {
            String msg;
            Toast.makeText(getApplicationContext(), "Has message", Toast.LENGTH_SHORT).show();
        }
        */
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(!search.getText().equals(""))
                    getData();
            }
        });
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getContext(), PostMakeActivity.class);
                intent.putExtra("UserId", id2);
                getContext().startActivity(intent);
            }
        });
        context = getContext().getApplicationContext();
        // Set up the login form.
        commentNum = new commentCount(getContext(), requestQueue);
        getData();

//        context = getContext().getApplicationContext();
        // Set up the login form.
        //getData();
        //AppCompatActivity activity = (PostHubActivity) getActivity();

        /*imageView=(ImageView)rootView.findViewById(R.id.share);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = "Here is the share content body";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.hint_search)));
            }
        });
        //then the btn and the textview
        //button2 = (Button) rootView.findViewById(R.id.button2);
        // daveText = (TextView) rootView.findViewById(R.id.textView2);


        // return our View*/
        //Intent myIntent = new Intent(getActivity(), PostHubActivity.class);
        //getActivity().startActivity(myIntent);
        return rootView;
    }

    //public void onClick(View view)




    private void getData()
    {
        search.setEnabled(false);
        search.setEnabled(true);
        loading = ProgressDialog.show(getContext(),"Please wait...","",false,false);

        String url = Config.DATA_URL+"db_pull_all_posts.php?id=*";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                showJSON(response, search.getText().toString());
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {}});

        requestQueue.add(stringRequest);
    }
    private void showJSON(String response,String query)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            populateLV(result,query);
            loading.dismiss();
            search.setHint(search.getText());
            search.setText("");
        } catch (JSONException e) {e.printStackTrace();}
    }
    private void populateLV(JSONArray db, String query)
    {
        //Matrix cursor necessary for reading JSON data into a listview
        String[] from = new String[] {BaseColumns._ID, "Title", "Content","Username","Score","Id","Count"};
        int[] to = {R.id.textView22, R.id.textView10,R.id.textView14,R.id.textView2,R.id.textView13,R.id.textView22,R.id.textView15};
        count = 0;
        MatrixCursor mc = new MatrixCursor(from); // properties from the JSONObjects
        String messages[] = new String[900];
        String mostrecent;
        query = query.toLowerCase();
        for (int i = 0; i < db.length(); i++)
        {
            try
            {
                JSONObject jo = db.getJSONObject(i);
                mostrecent = jo.getString("Content");
                if(jo.getString("Content").length()>100)
                    mostrecent = mostrecent.substring(0,100) + "...";
                if (query.equals("") || mostrecent.toLowerCase().contains(query) || jo.getString("Title").toLowerCase().contains(query))
                mc.addRow(new Object[]{0, jo.getString("Title").replaceAll("_", " "), mostrecent.replaceAll("_", " "), jo.getString("Username"), jo.getString("Score"),jo.getString("Id"),commentNum.count[Integer.parseInt(jo.getString("Id"))]+" Comments"});
            } catch (JSONException e1)
            {
                e1.printStackTrace();
            }
        }

        myList = (ListView) getActivity().findViewById(R.id.listView);
        myList.setClickable(false);
        myList.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView v = (TextView)view.findViewById(R.id.textView22);
                String itemId = v.getText().toString();

                Intent intent = new Intent(getContext(), CommentActivity.class);
                intent.putExtra("UserId", id2);
                intent.putExtra("PostId", itemId);
                getContext().startActivity(intent);
                //based on item add info to intent
                //startActivity(intent);
            }


        });

        SpecialAdapter adapter = new SpecialAdapter(getContext(), R.layout.post_layout, mc, from, to,0);
        myList.setAdapter(adapter);
    }

} // This is the end. But we still have to change the mainactivity.java to work with it.
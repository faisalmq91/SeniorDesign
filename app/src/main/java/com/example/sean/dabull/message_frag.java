package com.example.sean.dabull;

/**
 * Created by faisalmq91 on 4/26/16.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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

//you will need to import throughout the creation of this code.
//The class needs to extend Fragment

public class message_frag extends Fragment
{
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
    private ImageButton refreshButton;
    private EditText username;
    private Context context;
    private String id2 ="1";
    private String userName;

    //you will need to import throughout the creation of this code.
//The class needs to extend Fragment
    //The newInstance() method return the reference to  fragment
    public static message_frag newInstance() {
        message_frag fragmentThree = new message_frag();
        return fragmentThree;
    }

    //MyFragment is the constructor method of our class.
    //this is a java thing. Google it as you this is basic
    //java that you need to know.

    public message_frag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //connecting the fragment to our layout, new_frag
        View rootView = inflater.inflate(R.layout.activity_create_message, container, false);
        button = (Button) rootView.findViewById(R.id.button3);
        username = (EditText) rootView.findViewById(R.id.editText7);
        myList = (ListView) rootView.findViewById(R.id.listViewConv);
        context = getContext();
        Intent intent = getActivity().getIntent();
        id2 = intent.getStringExtra("UserId");
        // Set up the login form.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                button.setEnabled(false);
                startMessage(username.getText().toString());
                getData();
            }
        });
        getData();
        return rootView;
    }
    private void getData()
    {
        loading = ProgressDialog.show(getContext(),"Please wait...","",false,false);

        String url = Config.DATA_URL+"db_pull_msg.php?id="+id2;
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    private void showJSON(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            populateLV(result);
            loading.dismiss();
        } catch (JSONException e) {e.printStackTrace();}
    }
    private boolean showJSONUsername(String response, String username)
    {
        String password = "";
        String id = "";
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            password = collegeData.getString(Config.KEY_PASSWORD);
            id = collegeData.getString("Id");
            loading.dismiss();
        } catch (JSONException e) {e.printStackTrace(); button.setEnabled(true);}
        if (!password.equals("null"))
        {
            button.setEnabled(true);
            Intent intent = new Intent(getActivity(), MsgActivity.class);
            intent.putExtra("UserId", id2);
            intent.putExtra("OtherName",username);
            intent.putExtra("OtherId", id);
            this.startActivity(intent);
            return true;
        }
        else
        {
            Toast.makeText(getContext(), "User does not exist!", Toast.LENGTH_SHORT).show();
            button.setEnabled(true);
            return false;
        }
    }

    private void populateLV(JSONArray db){
        //Matrix cursor necessary for reading JSON data into a listview
        String[] from = new String[] {BaseColumns._ID, "User1_Id", "User2_Id","MainID"};
        int[] to = {R.id.textView2, R.id.textView2,R.id.textView10,R.id.textView12};

        MatrixCursor mc = new MatrixCursor(from); // properties from the JSONObjects
        String messages[] = new String[900];
        String mostrecent = "";
        boolean flag=false;
        int count=0;
        for (int i = 0; i < db.length(); i++) {
            try {
                JSONObject jo = db.getJSONObject(i);
                String u1 = jo.getString("User1_Id");
                String u2 = jo.getString("User2_Id");
                for(int j=0;j<count;j++)
                {
                    if (messages[j].equals(u1) || messages[j].equals(u2))
                        flag=true;
                    if (!u1.equals(id2) && !u2.equals(id2))
                        flag = true;
                }

                if (u1.equals(id2))
                {

                    messages[count] = u2;
                    System.out.println(messages[count]);
                    if(!flag)
                    {
                        System.out.println(u2+" Stored");
                        mostrecent = jo.getString("Text");
                        if(mostrecent.length()>64)
                            mostrecent = mostrecent.substring(0,64) + "...";
                        mc.addRow(new Object[]{0, jo.getString("Username2"), mostrecent.replaceAll("_", " "),jo.getString("User2_Id")});
                    }
                } else
                {
                    messages[count] = u1;
                    System.out.println(messages[count]);
                    if(!flag)
                    {
                        System.out.println("Stored");
                        mostrecent = jo.getString("Text");
                        if(mostrecent.length()>64)
                            mostrecent = mostrecent.substring(0,64) + "...";
                        mc.addRow(new Object[]{0, jo.getString("Username1"), mostrecent.replaceAll("_", " "),jo.getString("User1_Id")});
                    }
                }
                count++;
                flag = false;
                // extract the properties from the JSONObject and use it with the addRow() method below
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView v = (TextView)view.findViewById(R.id.textView12);
                TextView v2 = (TextView)view.findViewById(R.id.textView2);
                String itemId = v.getText().toString();
                String itemName = v2.getText().toString();
                Intent intent = new Intent(getActivity(), MsgActivity.class);
                intent.putExtra("OtherId", itemId);
                intent.putExtra("OtherName",itemName);
                intent.putExtra("UserId", id2);
                getActivity().startActivity(intent);
                //based on item add info to intent
                //startActivity(intent);
            }


        });
        SpecialAdapter adapter = new SpecialAdapter(getContext(), R.layout.conversation_layout, mc, from, to,0);
        myList.setAdapter(adapter);
    }
    public boolean startMessage(String username)
    {
        userName = username;
        String url = Config.DATA_URL+"db_fetch.php?username="+username;

        if (!validateString.isValid(username))
        {
            Toast.makeText(getContext(), "Username invalid!", Toast.LENGTH_SHORT).show();
            button.setEnabled(true);
            return false;
        }

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                showJSONUsername(response,userName);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getContext(), "Connection lost!", Toast.LENGTH_SHORT).show();

            }});

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return true;
    }
} // This is the end. But we still have to change the mainactivity.java to work with it.


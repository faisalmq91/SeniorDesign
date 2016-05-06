package com.example.sean.dabull;

/**
 * Created by faisalmq91 on 4/26/16.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

//you will need to import throughout the creation of this code.
//The class needs to extend Fragment

public class profile_frag extends Fragment {
    CircularImageView circularImageView;
    String imgDecodableString;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private String UPLOAD_URL ="http://dabull.net/upload.php";
    private TextView uname, bio;
    private String KEY_IMAGE = "image";
    private String id2;
    private RequestQueue requestQueue;

    //you will need to import throughout the creation of this code.
//The class needs to extend Fragment
    //The newInstance() method return the reference to  fragment
    public static profile_frag newInstance() {
        profile_frag fragmentTwo = new profile_frag();
        return fragmentTwo;
    }

    //MyFragment is the constructor method of our class.
    //this is a java thing. Google it as you this is basic
    //java that you need to know.

    public profile_frag() {
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
        View rootView = inflater.inflate(R.layout.activity_profile_frag, container, false);
        //then the btn and the textview
        //  button2 = (Button) rootView.findViewById(R.id.button3);
        // daveText = (TextView) rootView.findViewById(R.id.textView3);
//        daveText.setText("Hello Susan");
        circularImageView=(CircularImageView) rootView.findViewById(R.id.user);

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });
        Intent intent = getActivity().getIntent();
        id2 = intent.getStringExtra("UserId");
        uname = (TextView) rootView.findViewById(R.id.editPic);
        bio = (TextView) rootView.findViewById(R.id.editText);
        requestQueue = Volley.newRequestQueue(getContext());
        rtn(rootView);
        // return our View
        getData();
        return rootView;
    }
    private boolean getData()
    {
        String url = "http://www.dabull.net/db_pull_user.php?id="+id2;


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
            {
            }});

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
        return true;
    }
    private void showJSON(String response)
    {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject collegeData = result.getJSONObject(0);
            uname.setText(collegeData.getString("Username"));
            bio.setText(collegeData.getString("Bio").replaceAll("_", " "));
        } catch (JSONException e) {e.printStackTrace();}

    }
    public View rtn(View v){

        return v;
    }
    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getContext(),"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null) {
            super.onActivityResult(requestCode, resultCode, data);
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                circularImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


} // This is the end. But we still have to change the mainactivity.java to work with it.
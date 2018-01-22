package com.decypher.vesselsapp.Notifications;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by trebd on 11/27/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String REG_TOKEN = "REG_TOKEN";
    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";

    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN, recent_token);

        registerToken(recent_token);
        Toast.makeText(this, recent_token, Toast.LENGTH_SHORT).show();
        /*sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        if(!sharedpref.getString("TOKEN","").equals("") && !sharedpref.getString("USERID","").equals("")){
            updatePreviousToken(recent_token);
        }else{
            if( !sharedpref.getString("USERID","").equals("")) {
                registerToken(recent_token);
            }
        }*/

    }

    private void registerToken(String token) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url("http://mobilevessels.000webhostapp.com/fcm/register.php?token=" + token + "&usr_id="+sharedpref.getString("USERID",""))
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public void updatePreviousToken(final String new_token) {

        final StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,"http://mobilevessels.000webhostapp.com/fcm/register.php?oldtoken="+sharedpref.getString("TOKEN","")+"&usr_id="+sharedpref.getString("USERID","")+"&token="+new_token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
                        editor.putString("TOKEN", new_token);
                        editor.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Display records after response

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };
        //Send request
        MySingleton.getInstance(getApplicationContext()).addToRequestque(stringRequest);
    }


}

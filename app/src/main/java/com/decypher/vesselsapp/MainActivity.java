package com.decypher.vesselsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.decypher.vesselsapp.BloodDrive.BloodDriveFragment;
import com.decypher.vesselsapp.BloodDrive.EventBloodMain;
import com.decypher.vesselsapp.Home.AboutUsActivity;
import com.decypher.vesselsapp.Home.HomeFragment;
import com.decypher.vesselsapp.Home.Users;
import com.decypher.vesselsapp.Messages.UserSelectedChatActivity;
import com.decypher.vesselsapp.MyPosts.AddPostActivity;
import com.decypher.vesselsapp.MyPosts.MyPostsFragment;
import com.decypher.vesselsapp.Notifications.MySingleton;
import com.decypher.vesselsapp.Others.BottomNavigationViewEx;
import com.decypher.vesselsapp.Others.FragmentNotAvailable;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.Others.LoginActivity;
import com.decypher.vesselsapp.Others.LoginSelectionActivity;
import com.decypher.vesselsapp.Others.ProfileNotLogged;
import com.decypher.vesselsapp.Others.SignupActivity1;
import com.decypher.vesselsapp.Profile.EditProfileActivity;
import com.decypher.vesselsapp.Search.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    GlobalFunctions globalFunctions;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    String user_id;

    //FIREBASE
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";

    ImageView imgMessage, imgInfo, imgDots;
    TextView txtTitle;

    String currentToken;
    //AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(globalFunctions.isNetworkAvailable()) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
                        txtTitle.setText("Home");
                        imgDots.setVisibility(View.GONE);
                        imgMessage.setVisibility(View.VISIBLE);
                        imgInfo.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigation_drive:
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EventBloodMain()).commit();

                        txtTitle.setText("Events");
                        imgDots.setVisibility(View.GONE);
                        imgMessage.setVisibility(View.VISIBLE);
                        imgInfo.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.navigation_profile:
                        if(sharedpref.getString("USERID","")!="") {
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPostsFragment()).commit();
                            imgMessage.setVisibility(View.GONE);
                            imgInfo.setVisibility(View.GONE);
                            imgDots.setVisibility(View.VISIBLE);
                        }else{
                            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileNotLogged()).commit();
                        }

                        txtTitle.setText("Profile");
                        return true;
                    case R.id.navigation_search:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SearchFragment()).commit();
                        txtTitle.setText("Search Donors");
                        imgMessage.setVisibility(View.VISIBLE);
                        imgInfo.setVisibility(View.VISIBLE);
                        imgDots.setVisibility(View.GONE);
                        return true;
                    case R.id.navigation_add:
                        if(!user_id.equals("")) {
                            Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                            startActivity(intent);
                        }else{
                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                            final View mView = getLayoutInflater().inflate(R.layout.dialog_logged, null);
                            //variables
                            Button btnSign = mView.findViewById(R.id.btnProceed);
                            Button btnRegister = mView.findViewById(R.id.btnRegister);
                            btnSign.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                            btnRegister.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MainActivity.this, SignupActivity1.class);
                                    startActivity(intent);
                                }
                            });
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();

                        }
                        return true;
                }
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentNotAvailable()).commit();
                return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("Event");
        FirebaseInstanceId.getInstance().getToken();

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        imgMessage = (ImageView) findViewById(R.id.imgMessage);
        imgInfo = (ImageView) findViewById(R.id.imgInfo);
        imgDots = (ImageView) findViewById(R.id.imgDots);

        //FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();
        //TOKENS GENERATION
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        /*mUser.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            // ...
                            if(!user_id.equals(null) && !user_id.equals("") && !user_id.equals(0))
                                registerToken(idToken, user_id);
                                if(sharedpref.getString("USERID","").equals("")) {
                                    SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
                                    editor.putString("TOKEN", idToken);
                                    currentToken = idToken;
                                    editor.commit();
                                }
                                Toast.makeText(MainActivity.this, idToken, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainActivity.this, "NO TOKEN", Toast.LENGTH_SHORT).show();
                            // Handle error -> task.getException();
                        }
                    }
                });*/

        //FIREBASE
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);


        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        //CHECKED KUNG LOGGED IN THEN MADIRITSO NA NI SA SHARED PREF
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Toast.makeText(MainActivity.this, "SIGNED IN", Toast.LENGTH_SHORT).show();
                    user_id = user.getUid();

                    savePreferences();

                } else {
                    // User is signed out
                    //Toast.makeText(MainActivity.this, "WALA KA SIGN IN", Toast.LENGTH_SHORT).show();
                    user_id = "";

                }
                // ...
            }
        };


        BottomNavigationViewEx navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.enableAnimation(false);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);
        navigation.setTextSize(0);
        //navigation.setItemHeight(135);
        navigation.setPadding(0,20,0,0);
        navigation.setIconSize(25, 25);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        globalFunctions = new GlobalFunctions(getApplicationContext());


        //default
        if(globalFunctions.isNetworkAvailable()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentNotAvailable()).commit();
        }

        imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!user_id.equals("")) {
                    Intent intent = new Intent(MainActivity.this, UserSelectedChatActivity.class);
                    startActivity(intent);
                }else{
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    final View mView = getLayoutInflater().inflate(R.layout.dialog_message, null);
                    Button btnSign = mView.findViewById(R.id.btnProceed);
                    Button btnRegister = mView.findViewById(R.id.btnRegister);
                    btnSign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    btnRegister.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, SignupActivity1.class);
                            startActivity(intent);
                        }
                    });
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
            }
        });


        imgDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), imgDots);

                    popup.getMenuInflater()
                            .inflate(R.menu.menu_main_users, popup.getMenu());


                popup.setGravity(Gravity.END);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_edit:{
                                Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                                startActivity(intent);
                                break;
                            }
                            case R.id.menu_logout:{
                                FirebaseAuth.getInstance().signOut();
                                SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();

                                //function
                                //deletePreviousToken();

                                editor.putBoolean("LOGGED", false);
                                editor.putString("USERID", "");
                                editor.putString("USERNAME", "");
                                editor.putString("USERPHOTO", "");
                                editor.putString("USERBLOOD", "");
                                editor.putString("USERGENDER", "");
                                editor.putString("USERCITY", "");
                                editor.putString("USERADDRESS", "");
                                editor.putString("USERBIRTHDATE", "");
                                editor.putString("USERTYPE", "");
                                editor.putString("USERCONTACT", "");
                                editor.putString("LASTDONATE", "");
                                editor.commit();

                                Intent intent = new Intent(MainActivity.this, LoginSelectionActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(MainActivity.this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                        return true;
                    }
                });

                popup.show();
            }
        });

        sendtoAll();

    }


    //AUTHENTICATION
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void setToolbarTitle(String title){
        txtTitle.setText(title);
    }

    private void savePreferences(){
       userReference.child(sharedpref.getString("USERID","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Users newUser = dataSnapshot.getValue(Users.class);
                    String user_name = newUser.getName();
                    String user_photo = newUser.getUser_photo();
                    String user_blood = newUser.getBloodtype();

                    SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
                    editor.putBoolean("LOGGED", true);
                    editor.putString("USERID", user_id);
                    editor.putString("USERNAME", user_name);
                    editor.putString("USERPHOTO", user_photo);
                    editor.putString("USERBLOOD", user_blood);
                    editor.putString("USERGENDER", newUser.getGender());
                    editor.putString("USERCITY", newUser.getCity());
                    editor.putString("USERADDRESS", newUser.getAddress());
                    editor.putString("USERBIRTHDATE", newUser.getBirthdate());
                    editor.putString("USERCONTACT", newUser.getContact());
                    editor.putString("LASTDONATE", newUser.getLast_donated());
                    editor.putString("USERTYPE", "user");
                    editor.commit();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public void deletePreviousToken() {
        final StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,"http://mobilevessels.000webhostapp.com/fcm/unregister.php?token="+sharedpref.getString("TOKEN","")+"&usr_id="+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
                        editor.putString("TOKEN", "");
                        editor.putString("USERID", "");
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


    //SEND NOTIF TO ALL
    public void sendtoAll() {
        final StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,"http://mobilevessels.000webhostapp.com/fcm/push_notification.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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

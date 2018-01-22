package com.decypher.vesselsapp.Coordinator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.Coordinator.EventModule.EventMainFragment;
import com.decypher.vesselsapp.Coordinator.EventModule.MyBloodDriveFragment;
import com.decypher.vesselsapp.Coordinator.SearchModule.CoordinatorSearch;
import com.decypher.vesselsapp.Messages.MessagesActivity;
import com.decypher.vesselsapp.Others.BottomNavigationViewEx;
import com.decypher.vesselsapp.Others.LoginSelectionActivity;
import com.decypher.vesselsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class CoordinatorMainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    //FIREBASE
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, corReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String COR_REFERENCE = "coordinators";
    CoordinatorData corValue;
    //AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeCoordinatorFragment()).commit();
                    setTitle("Home");
                    return true;
                case R.id.navigation_search:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new CoordinatorSearch()).commit();
                    setTitle("Search Donor");
                    return true;
                case R.id.navigation_posts:
                    setTitle("My Events");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EventMainFragment()).commit();
                    return true;
                case R.id.navigation_account:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new CoordinatorAccountFragment()).commit();
                    return true;
                case R.id.navigation_add:
                    setTitle("Profile");
                    Intent intent = new Intent(CoordinatorMainActivity.this, AddSelectionActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_main);

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        //FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();
        //FIREBASE
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        corReference = mDatabase.getReference(COR_REFERENCE);

        mTextMessage = (TextView) findViewById(R.id.message);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeCoordinatorFragment()).commit();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //setLoggedIn(user.getUid());
                    savePreferences(user.getUid());
                } else {

                }
            }
        };

        BottomNavigationViewEx navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.enableAnimation(false);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);
        //navigation.setItemHeight(150);
        navigation.setPadding(0, 5,5,0);
        navigation.setIconSize(25, 25);
        navigation.setTextSize(12);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coordinator, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.menu_messages:
                intent = new Intent(CoordinatorMainActivity.this, MessagesActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
                editor.putBoolean("LOGGED", false);
                editor.putString("COR_ID", "");
                editor.putString("COR_NAME","");
                editor.putString("COR_EMAIL", "");
                editor.putString("COR_ADDRESS", "");
                editor.putString("COR_CONTACT", "");
                editor.putString("COR_ASSOCIATION", "");
                editor.putString("COR_PHOTO", "");
                editor.putString("USERTYPE", "");
                editor.commit();
                intent = new Intent(CoordinatorMainActivity.this, LoginSelectionActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void savePreferences(String keyVal){
        corReference.child(keyVal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    corValue = dataSnapshot.getValue(CoordinatorData.class);
                    SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
                    editor.putBoolean("LOGGED", true);
                    editor.putString("COR_ID", dataSnapshot.getKey());
                    editor.putString("COR_NAME", corValue.getName());
                    editor.putString("COR_EMAIL", corValue.getEmail());
                    editor.putString("COR_ADDRESS", corValue.getAddress());
                    editor.putString("COR_CONTACT", corValue.getContact());
                    editor.putString("COR_ASSOCIATION", corValue.getAssociation());
                    editor.putString("COR_PHOTO", corValue.getPhoto());
                    editor.putString("USERTYPE", "coordinator");
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}

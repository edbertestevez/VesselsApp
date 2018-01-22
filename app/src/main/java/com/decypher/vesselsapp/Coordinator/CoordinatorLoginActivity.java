package com.decypher.vesselsapp.Coordinator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.decypher.vesselsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class CoordinatorLoginActivity extends AppCompatActivity {

    Button btnSignIn;
    EditText etEmail, etPassword;
    String email, password;
    CoordinatorData corValue;

    //FIREBASE
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, corReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String COR_REFERENCE = "coordinators";
    //AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    ProgressDialog progDialog;

    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator_login);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        //FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();
        //FIREBASE
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        corReference = mDatabase.getReference(COR_REFERENCE);

        progDialog = new ProgressDialog(CoordinatorLoginActivity.this);
        progDialog.setMessage("Verifying account. Please wait. . .");

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(CoordinatorLoginActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                } else {
                    progDialog.show();
                    checkLogin();
                }
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Toast.makeText(MainActivity.this, "SIGNED IN", Toast.LENGTH_SHORT).show();
                    String user_id = user.getUid();

                    //check account nga ara sa coordinators
                    checkCoordinatorAccount(user_id);

                } else {

                }
            }
        };

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

    public void checkLogin(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            progDialog.dismiss();
                            Toast.makeText(CoordinatorLoginActivity.this, "Incorrect Username or Password",
                                    Toast.LENGTH_SHORT).show();
                        }else{

                        }

                    }
                });

    }

    public void checkCoordinatorAccount(String key){

        corReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progDialog.dismiss();
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

                    Intent intent = new Intent(CoordinatorLoginActivity.this, CoordinatorMainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(CoordinatorLoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
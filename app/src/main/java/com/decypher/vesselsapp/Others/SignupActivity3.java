package com.decypher.vesselsapp.Others;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.decypher.vesselsapp.Home.Users;
import com.decypher.vesselsapp.MainActivity;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignupActivity3 extends AppCompatActivity {

    EditText etDate;
    RadioButton rdoMale, rdoFemale;
    Spinner spnType;
    Button btnCreate;
    String name, password, email, contact, address, city, str_date, gender="Male", bloodtype, newDate="";
    Calendar myCalendar;

    GlobalFunctions globalFunctions;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    ProgressDialog progDialog;

    //save details
    Map<String, Object> newUser = new HashMap<>();

    String key;
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String user_id;

    //AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);

        globalFunctions = new GlobalFunctions(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        name = extras.getString("NAME");
        password = extras.getString("PASSWORD");
        email = extras.getString("EMAIL");
        contact = extras.getString("CONTACT");
        address = extras.getString("ADDRESS");
        city = extras.getString("CITY");

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        etDate = (EditText) findViewById(R.id.etDate);
        rdoMale = (RadioButton) findViewById(R.id.rdoMale);
        rdoFemale = (RadioButton) findViewById(R.id.rdoFemale);
        spnType = (Spinner) findViewById(R.id.spnType);
        btnCreate = (Button) findViewById(R.id.btnCreate);

        progDialog = new ProgressDialog(SignupActivity3.this);
        progDialog.setMessage("Creating account. Please wait. . .");

        //FIREBASE
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);

        ProgressDialog progDialog;

        //FIREBASE AUTH
        mAuth = FirebaseAuth.getInstance();


        rdoMale.setChecked(true);

        rdoMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdoFemale.setChecked(false);
                gender = "Male";
            }
        });

        rdoFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdoMale.setChecked(false);
                gender = "Female";
            }
        });

        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateInput();
            }

        };

        etDate.setKeyListener(null);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SignupActivity3.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_date = etDate.getText().toString();
                bloodtype = spnType.getSelectedItem().toString();
                if(str_date.isEmpty()){
                    Toast.makeText(SignupActivity3.this, "Please fill up all fields!", Toast.LENGTH_SHORT).show();
                }else{
                    //CREATE ACCOUNT AUTH
                    if(globalFunctions.isNetworkAvailable()) {
                        createAccount();
                    }else{
                        Toast.makeText(SignupActivity3.this, "No internet connection found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void createAccount(){
        progDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity3.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                           // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            user_id = user.getUid();

                            newUser.put("address",address);
                            newUser.put("birthdate",newDate);
                            newUser.put("bloodtype",bloodtype);
                            newUser.put("city", city);
                            newUser.put("contact", contact);
                            newUser.put("donation_count","0");
                            newUser.put("email",email);
                            newUser.put("gender",gender);
                            newUser.put("last_donated","");
                            newUser.put("name",name);
                            newUser.put("password",password);
                            newUser.put("user_photo","https://firebasestorage.googleapis.com/v0/b/vessels-app.appspot.com/o/default-user-image.png?alt=media&token=a094cf71-b358-46e9-a03a-497ca2942412");

                            userReference.child(user_id).setValue(newUser, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    progDialog.dismiss();
                                    //SHARED PREFS
                                    savePreferences();
                                    Intent intent = new Intent(SignupActivity3.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity3.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            progDialog.dismiss();
                        }

                    }
                });

    }

    public void updateDateInput(){
        DateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        try {
            newDate = dateFormat.parse(myCalendar.getTime().toString()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(sdf.format(myCalendar.getTime()));
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
                    editor.putString("USERTYPE", "user");
                    editor.commit();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

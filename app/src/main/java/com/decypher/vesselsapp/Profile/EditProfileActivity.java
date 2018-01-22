package com.decypher.vesselsapp.Profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.MyPosts.ImageUploadData;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.Others.SignupActivity3;
import com.decypher.vesselsapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    public String name;
    public String gender;
    public String bloodtype;
    public String birthdate;
    public String city;
    public String address;
    public String contact;
    public String donation_count;
    public String user_photo;
    public String user_id;
    String userPhoto;
    String newDate;

    TextView txtChange;
    EditText etName, etContact, etAddress, etDate;
    Button btnSave;
    ImageView imgUser;
    Spinner spnType, spnCity;


    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    GlobalFunctions globalFunctions;

    Calendar myCalendar;

    private Uri imgUri;
    public final int REQUEST_CODE = 1234;
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        DateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Profile");

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());



        //FIREBASE
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);

        txtChange = (TextView) findViewById(R.id.txtChange);
        etName = (EditText) findViewById(R.id.etName);
        etContact = (EditText) findViewById(R.id.etContact);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDate = (EditText) findViewById(R.id.etDate);
        btnSave = (Button) findViewById(R.id.btnSave);
        imgUser = (ImageView) findViewById(R.id.imgUser);
        spnType = (Spinner) findViewById(R.id.spnType);
        spnCity = (Spinner) findViewById(R.id.spnCity);

        SimpleDateFormat simpleDate =  new SimpleDateFormat("MM/dd/yy");

        etName.setText(sharedpref.getString("USERNAME",""));
        etContact.setText(sharedpref.getString("USERCONTACT",""));
        etAddress.setText(sharedpref.getString("USERADDRESS",""));
        Picasso.with(getApplicationContext()).load(sharedpref.getString("USERPHOTO","")).fit().centerCrop().into(imgUser);
        etDate.setText(simpleDate.format(new Date(sharedpref.getString("USERBIRTHDATE",""))).toString());


        progDialog = new ProgressDialog(EditProfileActivity.this);
        progDialog.setMessage("Updating Profile. . .");

        try {
            newDate = dateFormat.parse(sharedpref.getString("USERBIRTHDATE","")).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int nbr = 0; nbr < spnCity.getCount(); nbr++) {
            String val = spnCity.getItemAtPosition(nbr).toString();
            if (val.equals(sharedpref.getString("USERCITY",""))) {
                spnCity.setSelection(nbr);
            }
        }
        for (int nbr = 0; nbr < spnType.getCount(); nbr++) {
            String val = spnType.getItemAtPosition(nbr).toString();
            if (val.equals(sharedpref.getString("USERTYPE",""))) {
                spnType.setSelection(nbr);
            }
        }


        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateBirthDate();
            }

        };

        etDate.setKeyListener(null);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //IMAGE
        txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select image"), REQUEST_CODE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setMessage("Save changes?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                saveChanges();
                            }

            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    dialog.dismiss();
                }
            });
                builder.create();
            AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null && data.getData() != null){
            imgUri = data.getData();

            try{
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imgUser.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setMessage("Edit Profile")
                .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveChanges();
                    }
                })
                    .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            finish();
                        }
                    });
        builder.create();
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void setPhoto(String val){
        userPhoto = val;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setMessage("Edit Profile")
                    .setPositiveButton("Save Changes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            saveChanges();
                        }
                    })
                    .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            finish();
                        }
                    });
            builder.create();
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateBirthDate(){
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

    public String getImageExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeFilter = MimeTypeMap.getSingleton();
        return mimeTypeFilter.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void saveChanges(){
        if (globalFunctions.isNetworkAvailable()) {
            name = etName.getText().toString();
            contact = etContact.getText().toString();
            bloodtype = spnType.getSelectedItem().toString();
            address = etAddress.getText().toString();
            city = spnCity.getSelectedItem().toString();
            birthdate = etDate.getText().toString();

            if (name.isEmpty() || contact.isEmpty() || bloodtype.isEmpty() || address.isEmpty() || city.isEmpty() || birthdate.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            } else {

                                progDialog.show();

                                SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
                                editor.putString("USERNAME", name);
                                editor.putString("USERBLOOD", bloodtype);
                                editor.putString("USERCITY", city);
                                editor.putString("USERADDRESS", address);
                                editor.putString("USERBIRTHDATE", newDate);
                                editor.putString("USERCONTACT", contact);
                                editor.commit();

                                if (imgUri != null) {
                                    //save image
                                    StorageReference ref = mStorageRef.child("image" + sharedpref.getString("USERID", "") + "." + getImageExt(imgUri));
                                    //add file to reference
                                    ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            ImageUploadData imageUpload = new ImageUploadData(taskSnapshot.getDownloadUrl().toString());
                                            userReference.child(sharedpref.getString("USERID", "")).child("user_photo").setValue(taskSnapshot.getDownloadUrl().toString());
                                            postReference.child(sharedpref.getString("USERID", "")).child("user_photo").setValue(taskSnapshot.getDownloadUrl().toString(), new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    userReference.child(sharedpref.getString("USERID", "")).child("address").setValue(address);
                                                    userReference.child(sharedpref.getString("USERID", "")).child("birthdate").setValue(newDate);
                                                    userReference.child(sharedpref.getString("USERID", "")).child("bloodtype").setValue(bloodtype);
                                                    userReference.child(sharedpref.getString("USERID", "")).child("city").setValue(city);
                                                    userReference.child(sharedpref.getString("USERID", "")).child("contact").setValue(contact);
                                                    userReference.child(sharedpref.getString("USERID", "")).child("name").setValue(name);


                                                    postReference.child(sharedpref.getString("USERID", "")).child("user_name").setValue(name);
                                                    postReference.child(sharedpref.getString("USERID", "")).child("contact").setValue(contact, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            finish();
                                                        }
                                                    });
                                                    progDialog.dismiss();
                                                    Toast.makeText(EditProfileActivity.this, "Profile information successfully updated", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
                                            editor.putString("USERPHOTO", taskSnapshot.getDownloadUrl().toString());
                                            editor.commit();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EditProfileActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                }
                                            });

                                }else{
                                    userReference.child(sharedpref.getString("USERID", "")).child("address").setValue(address);
                                    userReference.child(sharedpref.getString("USERID", "")).child("birthdate").setValue(newDate);
                                    userReference.child(sharedpref.getString("USERID", "")).child("bloodtype").setValue(bloodtype);
                                    userReference.child(sharedpref.getString("USERID", "")).child("city").setValue(city);
                                    userReference.child(sharedpref.getString("USERID", "")).child("contact").setValue(contact);
                                    userReference.child(sharedpref.getString("USERID", "")).child("name").setValue(name);
                                    progDialog.dismiss();

                                    postReference.child(sharedpref.getString("USERID", "")).child("user_name").setValue(name);
                                    postReference.child(sharedpref.getString("USERID", "")).child("contact").setValue(contact, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            finish();
                                        }
                                    });
                                    Toast.makeText(EditProfileActivity.this, "Profile information successfully updated", Toast.LENGTH_SHORT).show();
                                }


            }
        }else{
            Toast.makeText(EditProfileActivity.this, "No internet connection found", Toast.LENGTH_SHORT).show();
        }
    }
}

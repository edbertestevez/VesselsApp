package com.decypher.vesselsapp.MyPosts;

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
import android.widget.Toast;

import com.decypher.vesselsapp.Others.GlobalFunctions;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    ImageView imgPost;
    Button btnPhoto, btnDiscard, btnPost;
    EditText etReceiver,etBags,etDate,etDescription;
    Spinner spnCity,spnBloodtype;

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    String postPhoto;
    String key;
    //save details
    Map<String, Object> newPost = new HashMap<>();
    Map<String, Object> userPost = new HashMap<>();
    Calendar myCalendar;

    ProgressDialog progDialog;

    private Uri imgUri;
    public final int REQUEST_CODE = 1234;
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, homeReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String HOME_REFERENCE = "posts_record";
    String newDate = "";

    GlobalFunctions globalFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        imgPost = (ImageView) findViewById(R.id.imgPost);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnDiscard = (Button) findViewById(R.id.btnDiscard);
        btnPost = (Button) findViewById(R.id.btnPost);
        etReceiver = (EditText) findViewById(R.id.etAddress);
        etBags = (EditText) findViewById(R.id.etEmail);
        etDate = (EditText) findViewById(R.id.etDate);
        etDescription = (EditText) findViewById(R.id.etDescription);
        spnCity = (Spinner) findViewById(R.id.spnCity);
        spnBloodtype = (Spinner) findViewById(R.id.spnBloodtype);

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        progDialog = new ProgressDialog(AddPostActivity.this);
        progDialog.setMessage("Creating post . .");

        globalFunctions = new GlobalFunctions(getApplicationContext());

        //FIREBASE
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        homeReference = mDatabase.getReference(HOME_REFERENCE);

        //ACTIONS
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select image"), REQUEST_CODE);
            }
        });

        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Discard Post?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                                progDialog.dismiss();
                            }
                        });
                builder.create();
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("New Post");



        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateNeededDate();
            }

        };

        etDate.setKeyListener(null);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddPostActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });




        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(globalFunctions.isNetworkAvailable()) {

                    final String str_receiver = etReceiver.getText().toString();
                    final String str_bags = etBags.getText().toString();
                    String str_date = etDate.getText().toString();
                    final String str_description = etDescription.getText().toString();
                    final String str_city = spnCity.getSelectedItem().toString();
                    final String str_bloodtype = spnBloodtype.getSelectedItem().toString();


                    if (str_receiver.isEmpty() || str_bags.isEmpty() || str_date.isEmpty() || str_description.isEmpty() || str_city.isEmpty() || str_bloodtype.isEmpty() || imgUri == null) {
                        Toast.makeText(AddPostActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        progDialog.show();
                        //new id
                        key = postReference.child(sharedpref.getString("USERID", "")).push().getKey();

                        //save image
                        StorageReference ref = mStorageRef.child("image" + key + "." + getImageExt(imgUri));
                        //add file to reference
                        ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                ImageUploadData imageUpload = new ImageUploadData(taskSnapshot.getDownloadUrl().toString());
                                postReference.child(sharedpref.getString("USERID", "")).child(key).child("photo").setValue(taskSnapshot.getDownloadUrl().toString(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        //insert or update sang info
                                        postReference.child(sharedpref.getString("USERID", "")).child("user_name").setValue(sharedpref.getString("USERNAME", ""));
                                        postReference.child(sharedpref.getString("USERID", "")).child("user_photo").setValue(sharedpref.getString("USERPHOTO", ""));
                                        postReference.child(sharedpref.getString("USERID", "")).child("contact").setValue(sharedpref.getString("USERCONTACT", ""));

                                        newPost.put("bags", str_bags);
                                        newPost.put("bloodtype", str_bloodtype);
                                        newPost.put("city", str_city);
                                        newPost.put("date", new Date().toString());
                                        newPost.put("date_needed", newDate);
                                        newPost.put("description", str_description);
                                        newPost.put("location", str_city);
                                        newPost.put("receiver", str_receiver);
                                        newPost.put("status", "0");
                                        newPost.put("response_count", "0");
                                        newPost.put("photo", taskSnapshot.getDownloadUrl().toString());


                                        postReference.child(sharedpref.getString("USERID", "")).child(key).setValue(newPost, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                        homeReference.child(key).child("user_id").setValue(sharedpref.getString("USERID", ""));
                                        homeReference.child(key).child("status").setValue("0", new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        homeReference.child(key).child("date").setValue((new Date().getTime() * -1) / 1000, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                progDialog.dismiss();
                                                finish();
                                                Toast.makeText(AddPostActivity.this, "Post successfully added", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                            }
                                        });
                                            }
                                        });
                                    }
                                });
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddPostActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    }
                                });
                    }
                }else{
                    Toast.makeText(AddPostActivity.this, "No internet connection found", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null && data.getData() != null){
            imgUri = data.getData();

            try{
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imgPost.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPhoto(String val){
            postPhoto = val;
    }

    public String getImageExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeFilter = MimeTypeMap.getSingleton();
        return mimeTypeFilter.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void updateNeededDate(){
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
}

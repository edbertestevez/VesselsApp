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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.decypher.vesselsapp.Home.PostData;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    ImageView imgPost;
    Button btnPhoto, btnDiscard, btnPost;
    EditText etReceiver,etBags,etDate,etDescription;
    Spinner spnCity,spnBloodtype;
    GlobalFunctions globalFunctions;

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    String postPhoto;
    String key, post_id;
    //save details
    Map<String, Object> newPost = new HashMap<>();
    PostData value;
    Calendar myCalendar;

    ProgressDialog progDialog;

    private Uri imgUri;
    public final int REQUEST_CODE = 1234;
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";

    String newDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Post");

        Bundle extras = getIntent().getExtras();
        post_id = extras.getString("POST_ID");

        progDialog = new ProgressDialog(EditPostActivity.this);
        progDialog.setMessage("Updating post. .");

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        globalFunctions = new GlobalFunctions(getApplicationContext());

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

        btnDiscard.setVisibility(View.INVISIBLE);
        btnPost.setText("Save");
        btnPhoto.setText("Change Photo");

        //FIREBASE
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);

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
                new DatePickerDialog(EditPostActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progDialog.show();
                final String str_receiver = etReceiver.getText().toString();
                final String str_bags = etBags.getText().toString();
                final String str_date = etDate.getText().toString();
                final String str_description = etDescription.getText().toString();
                final String str_city = spnCity.getSelectedItem().toString();
                final String str_bloodtype = spnBloodtype.getSelectedItem().toString();
                String new_date;

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Save changes?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //UPDATE STATUS SA POSTS_RECORD

                if(str_receiver.isEmpty()||str_bags.isEmpty()||str_date.isEmpty()||str_description.isEmpty()||str_city.isEmpty()||str_bloodtype.isEmpty()) {
                    Toast.makeText(EditPostActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                }else {
                    if (imgUri != null) {

                        //new id

                        //save image
                        StorageReference ref = mStorageRef.child("image" + post_id + "." + getImageExt(imgUri));

                        //add file to reference
                        ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ImageUploadData imageUpload = new ImageUploadData(taskSnapshot.getDownloadUrl().toString());
                                postReference.child(sharedpref.getString("USERID","")).child(post_id).child("photo").setValue(taskSnapshot.getDownloadUrl().toString(), new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("bags").setValue(str_bags);
                                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("bloodtype").setValue(str_bloodtype);
                                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("city").setValue(str_city);

                                        if(newDate!="") {
                                            postReference.child(sharedpref.getString("USERID","")).child(post_id).child("date_needed").setValue(newDate);
                                        }
                                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("description").setValue(str_description);
                                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("city").setValue(str_city);
                                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("receiver").setValue(str_receiver, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                finish();
                                                Toast.makeText(EditPostActivity.this, "Changes successfully saved", Toast.LENGTH_SHORT).show();

                                            }
                                        });


                                    }
                                });
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditPostActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    }
                                });
                    }else{


                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("bags").setValue(str_bags);
                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("bloodtype").setValue(str_bloodtype);
                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("city").setValue(str_city);

                        if(newDate!="") {
                            postReference.child(sharedpref.getString("USERID","")).child(post_id).child("date_needed").setValue(newDate);
                        }
                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("description").setValue(str_description);
                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("city").setValue(str_city);
                        postReference.child(sharedpref.getString("USERID","")).child(post_id).child("receiver").setValue(str_receiver, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                finish();
                                Toast.makeText(EditPostActivity.this, "Changes successfully saved", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }


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


        //load default values
        loadDefault();
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


    public String getImageExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeFilter = MimeTypeMap.getSingleton();
        return mimeTypeFilter.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void loadDefault(){
        postReference.child(sharedpref.getString("USERID","")).child(post_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SimpleDateFormat simpleDate =  new SimpleDateFormat("MM/dd/yy");

                if(dataSnapshot.exists()) {
                    value = dataSnapshot.getValue(PostData.class);

                    String strNeeded = simpleDate.format(new Date(value.getDate_needed()));
                    String strPostDate = simpleDate.format(new Date(value.getDate()));

                    ArrayAdapter<String> array_spinner=(ArrayAdapter<String>)spnBloodtype.getAdapter();
                    spnBloodtype.setSelection(array_spinner.getPosition(value.getBloodtype()));

                    ArrayAdapter<String> array_spinner1=(ArrayAdapter<String>)spnCity.getAdapter();
                    spnCity.setSelection(array_spinner1.getPosition(value.getCity()));

                    etReceiver.setText(value.getReceiver());
                    etBags.setText(value.getBags());
                    etDate.setText(strNeeded);
                    etDescription.setText(value.getDescription());
                    Picasso.with(getApplicationContext()).load(value.getPhoto()).fit().centerCrop().into(imgPost);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

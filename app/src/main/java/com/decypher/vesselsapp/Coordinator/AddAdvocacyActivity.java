package com.decypher.vesselsapp.Coordinator;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.decypher.vesselsapp.Coordinator.EventModule.MySelectedAdvocacyActivity;
import com.decypher.vesselsapp.MyPosts.ImageUploadData;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddAdvocacyActivity extends AppCompatActivity {

    EditText etEvent, etAddress, etDescription, etDate, etTimeStart, etTimeEnd;
    Button btnPhoto, btnSave;
    ImageView imgEvent;
    Map<String, Object> newPost = new HashMap<>();
    String strMinute;
    String strHour;

    private Uri imgUri;
    public final int REQUEST_CODE = 1234;

    Calendar myCalendar;
    String newDate;
    String name, address, description, event_date, timeStart, timeEnd, key;

    ProgressDialog progDialog;

    //FIREBASE
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, corReference, eventsReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String COR_REFERENCE = "coordinators";
    String DRIVE_REFERENCE = "blood_drives";
    String EVENT_REFERENCE = "events";

    GlobalFunctions globalFunctions;

    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advocacy);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("New Advocacy Event");

        progDialog = new ProgressDialog(AddAdvocacyActivity.this);
        progDialog.setMessage("Creating event. .");

        //FIREBASE
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        corReference = mDatabase.getReference(COR_REFERENCE);
        eventsReference = mDatabase.getReference(DRIVE_REFERENCE);
        eventsReference = mDatabase.getReference(EVENT_REFERENCE);

        globalFunctions = new GlobalFunctions(getApplicationContext());
        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        etEvent = (EditText) findViewById(R.id.etEvent);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etDate = (EditText) findViewById(R.id.etDate);
        etTimeStart = (EditText) findViewById(R.id.etTimeStart);
        etTimeEnd = (EditText) findViewById(R.id.etTimeEnd);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnSave = (Button) findViewById(R.id.btnSave);
        imgEvent = (ImageView) findViewById(R.id.imgEvent);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select image"), REQUEST_CODE);
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
                updateEventDate();
            }

        };

        etDate.setKeyListener(null);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddAdvocacyActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        etTimeStart.setKeyListener(null);
        etTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddAdvocacyActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedMinute==0){
                            strMinute="00";
                        }else{
                            strMinute = String.valueOf(selectedMinute);
                        }

                        if(selectedHour==0){
                            strHour = "00";
                        }else{
                            strHour = String.valueOf(selectedHour);
                        }
                        etTimeStart.setText( strHour + ":" + strMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        etTimeEnd.setKeyListener(null);
        etTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddAdvocacyActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedMinute==0){
                            strMinute="00";
                        }else{
                            strMinute = String.valueOf(selectedMinute);
                        }

                        if(selectedHour==0){
                            strHour = "00";
                        }else{
                            strHour = String.valueOf(selectedHour);
                        }
                        etTimeEnd.setText( strHour + ":" + strMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (globalFunctions.isNetworkAvailable()) {
                    name = etEvent.getText().toString();
                    address = etAddress.getText().toString();
                    description = etDescription.getText().toString();
                    event_date = etDate.getText().toString();
                    timeStart = etTimeStart.getText().toString();
                    timeEnd = etTimeEnd.getText().toString();

                    if(name.isEmpty() || address.isEmpty() || description.isEmpty() || event_date.isEmpty() || timeStart.isEmpty() || timeEnd.isEmpty()||imgUri==null){
                        Toast.makeText(AddAdvocacyActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("Save Post?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        progDialog.show();
                                        //new id
                                        key = eventsReference.child(sharedpref.getString("COR_ID", "")).push().getKey();

                                        //save image
                                        StorageReference ref = mStorageRef.child("image" + key + ".jpg");
                                        //add file to reference
                                        ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                                ImageUploadData imageUpload = new ImageUploadData(taskSnapshot.getDownloadUrl().toString());
                                                eventsReference.child(sharedpref.getString("COR_ID", "")).child(key).child("photo").setValue(taskSnapshot.getDownloadUrl().toString(), new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        newPost.put("name",name);
                                                        newPost.put("address",address);
                                                        newPost.put("description",description);
                                                        newPost.put("date", newDate);
                                                        newPost.put("time_start", timeStart);
                                                        newPost.put("time_end",timeEnd);
                                                        newPost.put("status","0");
                                                        newPost.put("photo",taskSnapshot.getDownloadUrl().toString());

                                                        eventsReference.child(sharedpref.getString("COR_ID","")).child(key).setValue(newPost, new DatabaseReference.CompletionListener() {
                                                            @Override
                                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                progDialog.dismiss();
                                                                finish();
                                                                Toast.makeText(AddAdvocacyActivity.this, "Post successfully added", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AddAdvocacyActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                                    @Override
                                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                    }
                                                });

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


                } else {
                    Toast.makeText(AddAdvocacyActivity.this, "There is no internet connection", Toast.LENGTH_SHORT).show();
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

    public void updateEventDate(){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null && data.getData() != null){
            imgUri = data.getData();

            try{
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imgEvent.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

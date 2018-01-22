package com.decypher.vesselsapp.Coordinator.EventModule;

import android.app.DatePickerDialog;
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

import com.decypher.vesselsapp.BloodDrive.SelectedBloodDriveActivity;
import com.decypher.vesselsapp.MyPosts.ImageUploadData;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditBloodDriveActivity extends AppCompatActivity {

    String drive_id, title, description, date, time, address, photo, time_from, time_to, newPhoto;
    EditText etEvent, etAddress, etDescription, etDate, etTimeStart, etTimeEnd;
    Button btnPhoto, btnSave;
    ImageView imgEvent;
    String strMinute;
    String strHour;
    String formatted_date;


    Map<String, Object> newPost = new HashMap<>();

    private Uri imgUri;
    public final int REQUEST_CODE = 1234;

    Calendar myCalendar;
    String newDate;
    String edit_name, edit_address, edit_description, edit_event_date, edit_timeStart, edit_timeEnd, edit_key, date_orig;

    //FIREBASE
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, corReference, driveReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String COR_REFERENCE = "coordinators";
    String DRIVE_REFERENCE = "blood_drives";
    GlobalFunctions globalFunctions;

    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blood_drive);

        SimpleDateFormat simpleDate =  new SimpleDateFormat("MM/dd/yy");



        //FIREBASE
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        corReference = mDatabase.getReference(COR_REFERENCE);
        driveReference = mDatabase.getReference(DRIVE_REFERENCE);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Blood Drive");

        globalFunctions = new GlobalFunctions(getApplicationContext());
        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        title = extras.getString("DRIVE_TITLE");
        drive_id = extras.getString("DRIVE_ID");
        description = extras.getString("DRIVE_DESC");
        date = extras.getString("DRIVE_DATE");
        address = extras.getString("DRIVE_ADDRESS");
        photo = extras.getString("DRIVE_PHOTO");
        date_orig = extras.getString("DATE_ORIG");

        time_from = extras.getString("DRIVE_TIME_FROM");
        time_to = extras.getString("DRIVE_TIME_TO");


        DateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        try {
            newDate = dateFormat.parse(date_orig).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        etEvent = (EditText) findViewById(R.id.etEvent);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etDate = (EditText) findViewById(R.id.etDate);
        etTimeStart = (EditText) findViewById(R.id.etTimeStart);
        etTimeEnd = (EditText) findViewById(R.id.etTimeEnd);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnSave = (Button) findViewById(R.id.btnSave);
        imgEvent = (ImageView) findViewById(R.id.imgEvent);

        btnPhoto.setText("Change Photo");
        etEvent.setText(title);
        etAddress.setText(address);
        etDescription.setText(description);
        etDate.setText(simpleDate.format(new Date(date)));
        etTimeStart.setText(time_from);
        etTimeEnd.setText(time_to);
        Picasso.with(getApplicationContext()).load(photo).fit().centerCrop().into(imgEvent);

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
                new DatePickerDialog(EditBloodDriveActivity.this, date, myCalendar
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
                mTimePicker = new TimePickerDialog(EditBloodDriveActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                mTimePicker = new TimePickerDialog(EditBloodDriveActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                    edit_name = etEvent.getText().toString();
                    edit_address = etAddress.getText().toString();
                    edit_description = etDescription.getText().toString();
                    edit_event_date = etDate.getText().toString();
                    edit_timeStart = etTimeStart.getText().toString();
                    edit_timeEnd = etTimeEnd.getText().toString();

                    if (edit_name.isEmpty() || edit_address.isEmpty() || edit_description.isEmpty() || edit_event_date.isEmpty() || edit_timeStart.isEmpty() || edit_timeEnd.isEmpty()) {
                        Toast.makeText(EditBloodDriveActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                    }else{
                        if(imgUri!=null){
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Save Changes?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            //save image
                            StorageReference ref = mStorageRef.child("image" + drive_id + ".jpg"); //FIXED
                            //add file to reference
                            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ImageUploadData imageUpload = new ImageUploadData(taskSnapshot.getDownloadUrl().toString());
                                    newPhoto = taskSnapshot.getDownloadUrl().toString();
                                    driveReference.child(sharedpref.getString("COR_ID", "")).child(drive_id).child("photo").setValue(taskSnapshot.getDownloadUrl().toString());

                                    SimpleDateFormat simpleDate = new SimpleDateFormat("MMM d, y");
                                    formatted_date = simpleDate.format(new Date(newDate));


                                    newPost.put("name",edit_name);
                                    newPost.put("address",edit_address);
                                    newPost.put("description",edit_description);
                                    newPost.put("date", newDate);
                                    newPost.put("time_start", edit_timeStart);
                                    newPost.put("time_end",edit_timeEnd);
                                    newPost.put("photo", photo);
                                    newPost.put("status","0");

                                    driveReference.child(sharedpref.getString("COR_ID","")).child(drive_id).setValue(newPost);
                                    finish();
                                    Intent intent = new Intent(EditBloodDriveActivity.this, SelectedBloodDriveActivity.class);
                                    intent.putExtra("DRIVE_ID", drive_id);
                                    intent.putExtra("DRIVE_TITLE", edit_name);
                                    intent.putExtra("DRIVE_DATE", formatted_date);
                                    intent.putExtra("DRIVE_ADDRESS", edit_address);
                                    intent.putExtra("DRIVE_TIME", edit_timeStart+" - "+edit_timeEnd);
                                    intent.putExtra("DRIVE_TIME_FROM", edit_timeStart);
                                    intent.putExtra("DRIVE_TIME_TO", edit_timeEnd);
                                    intent.putExtra("DRIVE_DESC", edit_description);
                                    if(imgUri!=null){
                                        intent.putExtra("DRIVE_PHOTO", newPhoto);
                                    }else{
                                        intent.putExtra("DRIVE_PHOTO", photo);
                                    }
                                    intent.putExtra("COR_ASSOCIATION", sharedpref.getString("COR_ASSOCIATION",""));
                                    intent.putExtra("COR_CONTACT", sharedpref.getString("COR_CONTACT",""));
                                    intent.putExtra("COR_PHOTO", sharedpref.getString("COR_PHOTO",""));
                                    intent.putExtra("DATE_ORIG", newDate);

                                    intent.putExtra("MY_BLOOD_DRIVE", true);
                                    startActivity(intent);
                                    Toast.makeText(EditBloodDriveActivity.this, "Post successfully updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditBloodDriveActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
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
                        }else{

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Save Changes?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                            newPost.put("name",edit_name);
                            newPost.put("address",edit_address);
                            newPost.put("description",edit_description);
                            newPost.put("date", newDate);
                            newPost.put("time_start", edit_timeStart);
                            newPost.put("time_end",edit_timeEnd);
                            newPost.put("photo", photo);
                            newPost.put("status","0");

                            driveReference.child(sharedpref.getString("COR_ID","")).child(drive_id).setValue(newPost);
                            finish();
                            Intent intent = new Intent(EditBloodDriveActivity.this, SelectedBloodDriveActivity.class);
                            intent.putExtra("DRIVE_ID", drive_id);
                            intent.putExtra("DRIVE_TITLE", edit_name);
                            intent.putExtra("DRIVE_DATE", edit_event_date);
                            intent.putExtra("DRIVE_ADDRESS", edit_address);
                            intent.putExtra("DRIVE_TIME", edit_timeStart+" - "+edit_timeEnd);
                            intent.putExtra("DRIVE_TIME_FROM", edit_timeStart);
                            intent.putExtra("DRIVE_TIME_TO", edit_timeEnd);
                            intent.putExtra("DRIVE_DESC", edit_description);
                            if(imgUri!=null){
                                intent.putExtra("DRIVE_PHOTO", newPhoto);
                            }else{
                                intent.putExtra("DRIVE_PHOTO", photo);
                            }
                            intent.putExtra("COR_ASSOCIATION", sharedpref.getString("COR_ASSOCIATION",""));
                            intent.putExtra("COR_CONTACT", sharedpref.getString("COR_CONTACT",""));
                            intent.putExtra("COR_PHOTO", sharedpref.getString("COR_PHOTO",""));
                            intent.putExtra("DATE_ORIG", newDate);

                            intent.putExtra("MY_BLOOD_DRIVE", true);
                            startActivity(intent);
                            Toast.makeText(EditBloodDriveActivity.this, "Post successfully updated", Toast.LENGTH_SHORT).show();

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
                            alertDialog.show();}


                    }


                } else {
                    Toast.makeText(EditBloodDriveActivity.this, "There is no internet connection", Toast.LENGTH_SHORT).show();
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

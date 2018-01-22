package com.decypher.vesselsapp.Coordinator;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.MyPosts.ImageUploadData;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.Profile.EditProfileActivity;
import com.decypher.vesselsapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;

public class EditCoordinatorAccount extends AppCompatActivity {

    TextView txtChange;
    EditText etName, etAssociation, etAddress, etContact;
    Button btnSave;
    ImageView imgPhoto;

    String name, association, address, contact, newPhoto;


    GlobalFunctions globalFunctions;

    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";

    private Uri imgUri;
    public final int REQUEST_CODE = 1234;
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference corReference, driveReference, eventsReference;
    String COR_REFERENCE = "coordinators";
    String DRIVE_REFERENCE = "blood_drives";
    String EVENTS_REFERENCE = "events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coordinator_account);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Profile");

        txtChange = (TextView) findViewById(R.id.txtChange);
        etAssociation = (EditText) findViewById(R.id.etAssociation);
        etName = (EditText) findViewById(R.id.etName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etContact = (EditText) findViewById(R.id.etContact);
        btnSave = (Button) findViewById(R.id.btnSave);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());

        //FIREBASE
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        corReference = mDatabase.getReference(COR_REFERENCE);
        driveReference = mDatabase.getReference(DRIVE_REFERENCE);
        eventsReference = mDatabase.getReference(EVENTS_REFERENCE);

        etAssociation.setText(sharedpref.getString("COR_ASSOCIATION",""));
        etName.setText(sharedpref.getString("COR_NAME",""));
        etAddress.setText(sharedpref.getString("COR_ADDRESS",""));
        etContact.setText(sharedpref.getString("COR_CONTACT",""));
        Picasso.with(getApplicationContext()).load(sharedpref.getString("COR_PHOTO","")).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imgPhoto);


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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditCoordinatorAccount.this);
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
                imgPhoto.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveChanges(){
        name = etName.getText().toString();
        address = etAddress.getText().toString();
        association = etAssociation.getText().toString();
        contact = etContact.getText().toString();

        if(name.isEmpty() || address.isEmpty() || association.isEmpty() || contact.isEmpty()){
            Toast.makeText(EditCoordinatorAccount.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
        }else {
            if (imgUri != null) {

                StorageReference ref = mStorageRef.child("image" + sharedpref.getString("COR_ID","") + "." + getImageExt(imgUri));
                //add file to reference
                ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageUploadData imageUpload = new ImageUploadData(taskSnapshot.getDownloadUrl().toString());
                        newPhoto = taskSnapshot.getDownloadUrl().toString();
                        corReference.child(sharedpref.getString("COR_ID","")).child("photo").setValue(taskSnapshot.getDownloadUrl().toString(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                corReference.child(sharedpref.getString("COR_ID", "")).child("address").setValue(address);
                                corReference.child(sharedpref.getString("COR_ID", "")).child("association").setValue(association);
                                corReference.child(sharedpref.getString("COR_ID", "")).child("contact").setValue(contact);
                                corReference.child(sharedpref.getString("COR_ID", "")).child("name").setValue(name);

                                //UPDATE man ang sa drive
                                driveReference.child(sharedpref.getString("COR_ID", "")).child("association").setValue(association);
                                driveReference.child(sharedpref.getString("COR_ID", "")).child("contact").setValue(contact);
                                driveReference.child(sharedpref.getString("COR_ID", "")).child("photo").setValue(newPhoto);

                                //UPDATE man ang sa event
                                eventsReference.child(sharedpref.getString("COR_ID", "")).child("association").setValue(association);
                                eventsReference.child(sharedpref.getString("COR_ID", "")).child("contact").setValue(contact);
                                eventsReference.child(sharedpref.getString("COR_ID", "")).child("photo").setValue(newPhoto, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        finish();
                                    }
                                });
                            }
                        });


                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditCoordinatorAccount.this, "An Error Occured", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                corReference.child(sharedpref.getString("COR_ID", "")).child("address").setValue(address);
                corReference.child(sharedpref.getString("COR_ID", "")).child("association").setValue(association);
                corReference.child(sharedpref.getString("COR_ID", "")).child("contact").setValue(contact);
                corReference.child(sharedpref.getString("COR_ID", "")).child("name").setValue(name);

                //UPDATE man ang sa drive
                driveReference.child(sharedpref.getString("COR_ID", "")).child("association").setValue(association);
                driveReference.child(sharedpref.getString("COR_ID", "")).child("contact").setValue(contact, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        finish();

                        //save sharedpref
                        SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
                        editor.putString("COR_NAME", name);
                        editor.putString("COR_ADDRESS", address);
                        editor.putString("COR_CONTACT", contact);
                        editor.putString("COR_ASSOCIATION", association);
                        if(imgUri!=null) {
                            editor.putString("COR_PHOTO", newPhoto);
                        }
                        editor.commit();

                        Toast.makeText(EditCoordinatorAccount.this, "Account successfully updated", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    public String getImageExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeFilter = MimeTypeMap.getSingleton();
        return mimeTypeFilter.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            AlertDialog.Builder builder = new AlertDialog.Builder(EditCoordinatorAccount.this);
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

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EditCoordinatorAccount.this);
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
}

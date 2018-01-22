package com.decypher.vesselsapp.MyPosts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.Home.PostData;
import com.decypher.vesselsapp.Home.PosterData;
import com.decypher.vesselsapp.Messages.UserSelectedChatActivity;
import com.decypher.vesselsapp.R;
import com.decypher.vesselsapp.Search.SearchAdapter;
import com.decypher.vesselsapp.Search.SearchData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewMyPostActivity extends AppCompatActivity {

    String post_id, myReceiver, selected_id;
    TextView txtReceiver,txtPoster,txtBags,txtDescription,txtDateNeeded,txtAddress,txtType, txtInstruction, txtNoneOne, txtNoneTwo, txtContact;
    ImageView imgPost;
    Button btnDonate;
    ArrayList<PostData> postList;
    PostData value;
    String username;
    RecyclerView recyclerDonor, recyclerConfirmed;

    ArrayList<SearchData> donorList, confirmList;
    SearchAdapter adapter, confirmAdapter;

    SearchData searchValue;

    RelativeLayout relativeDonate;

    RelativeLayout messageContainer;
    Button btnMessage;

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    int check=0;

    Button btnViewRespondents, btnViewDonations;
    TextView txtRespondentCount, txtConfirmedCount;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, donorReference, homeReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String DONOR_REFERENCE = "posts_donors";
    String HOME_REFERENCE = "posts_record";

    int countRespondent = 0;
    int countConfirmed = 0;

    ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        relativeDonate = (RelativeLayout) findViewById(R.id.relativeDonate);
        btnMessage = (Button) findViewById(R.id.btnMessage);

        Bundle extras = getIntent().getExtras();
        post_id = extras.getString("POST_ID");
        myReceiver = extras.getString("RECEIVER");
        selected_id = extras.getString("SELECTED_ID");

        progDialog = new ProgressDialog(ViewMyPostActivity.this);
        progDialog.setMessage("Loading post information. .");

        btnMessage = (Button) findViewById(R.id.btnMessage);
        messageContainer = (RelativeLayout) findViewById(R.id.messageContainer);

        btnViewDonations = (Button) findViewById(R.id.btnViewDonations);
        btnViewRespondents = (Button) findViewById(R.id.btnViewRespondents);
        txtConfirmedCount = (TextView) findViewById(R.id.txtConfirmedCount);
        txtRespondentCount = (TextView) findViewById(R.id.txtRespondentCount);

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                    final View mView = getLayoutInflater().inflate(R.layout.dialog_message_chat, null);
                    //variables
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    Button btnContinue = mView.findViewById(R.id.btnProceed);
                    btnContinue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ViewMyPostActivity.this, UserSelectedChatActivity.class);
                            intent.putExtra("RECEIVER", myReceiver);
                            startActivity(intent);
                        }
                    });
                }
        });


        txtContact = (TextView) findViewById(R.id.txtContactOrg);
        txtNoneOne = (TextView) findViewById(R.id.txtNoneOne);
        txtNoneTwo = (TextView) findViewById(R.id.txtNoneTwo);
        txtReceiver = (TextView) findViewById(R.id.txtSender);
        txtPoster = (TextView) findViewById(R.id.txtPoster);
        txtBags = (TextView) findViewById(R.id.txtBags);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtDateNeeded = (TextView) findViewById(R.id.txtDateNeeded);
        txtAddress = (TextView) findViewById(R.id.etAddress);
        txtType = (TextView) findViewById(R.id.txtType);
        imgPost = (ImageView) findViewById(R.id.imgPost);
        btnDonate = (Button) findViewById(R.id.btnDonate);
        txtInstruction = (TextView) findViewById(R.id.txtInstruction);


        btnDonate.setText("SET AS FINISHED");

        if(getIntent().hasExtra("STATUS")){
            if(extras.getString("STATUS").equals("1")){
                btnDonate.setText("Post already finished");
                btnDonate.setEnabled(false);
            }else{
                btnDonate.setText("SET AS FINISHED");
            }
        }


        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Confirm that this post is already done. Once confirmed, it will not be seen on public.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //UPDATE STATUS SA POSTS_RECORD
                                postReference.child(selected_id).child(post_id).child("status").setValue("1", new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        homeReference.child(post_id).child("status").setValue("1", new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                Toast.makeText(ViewMyPostActivity.this, "Post successfully finished!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
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
        });

        recyclerDonor = (RecyclerView) findViewById(R.id.recyclerDonor);
        recyclerDonor.setHasFixedSize(true);
        recyclerDonor.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        donorList = new ArrayList<SearchData>();
        adapter = new SearchAdapter(ViewMyPostActivity.this, donorList);
        recyclerDonor.setAdapter(adapter);

        recyclerConfirmed = (RecyclerView) findViewById(R.id.recyclerConfirmed);
        recyclerConfirmed.setHasFixedSize(true);
        recyclerConfirmed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        confirmList = new ArrayList<SearchData>();
        confirmAdapter = new SearchAdapter(ViewMyPostActivity.this, confirmList);
        recyclerConfirmed.setAdapter(confirmAdapter);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(myReceiver);

    //firebase
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        donorReference = mDatabase.getReference(DONOR_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        homeReference = mDatabase.getReference(HOME_REFERENCE);

        //ara sa onResume
        progDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUsername();
        loadInfo();
        loadDonors();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.home:
                finish();
                return true;
            case R.id.menu_edit:
                Intent intent = new Intent(ViewMyPostActivity.this, EditPostActivity.class);
                intent.putExtra("POST_ID",post_id);
                startActivity(intent);
                return true;
            case R.id.menu_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewMyPostActivity.this);
                builder.setMessage("Delete Post?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //UPDATE STATUS SA POSTS_RECORD
                                homeReference.child(post_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        postReference.child(sharedpref.getString("USERID","")).child(post_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(ViewMyPostActivity.this, "Post successfully deleted", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }, 3000);
                                            }
                                        });
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

                return true;
        }
        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDonors(){
        donorList.clear();
        txtNoneTwo.setVisibility(View.VISIBLE);
        txtNoneOne.setVisibility(View.VISIBLE);

        if(sharedpref.getString("COR_ID","").equals("") && sharedpref.getString("USERID","").equals("")){
            btnMessage.setText("Please sign in");
            btnMessage.setEnabled(false);
            txtNoneOne.setVisibility(View.GONE);
            txtNoneTwo.setVisibility(View.GONE);
            recyclerConfirmed.setVisibility(View.GONE);
            recyclerDonor.setVisibility(View.GONE);
            //btnDonate.setVisibility(View.GONE);
            progDialog.dismiss();
        }else {
            donorReference.orderByKey().equalTo(post_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (!dataSnapshot.exists()){
                        btnViewRespondents.setVisibility(View.GONE);
                        btnViewDonations.setVisibility(View.GONE);
                    }else {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.getValue().equals(false)) {
                                countRespondent++;
                                txtRespondentCount.setText("( "+String.valueOf(countRespondent)+" )");

                                //limit 5
                                if(countConfirmed<6) {
                                    loadDonorList(dataSnapshot1.getKey(), false);
                                    btnViewRespondents.setVisibility(View.VISIBLE);
                                }
                            } else {
                                countConfirmed++;
                                txtConfirmedCount.setText("( "+String.valueOf(countConfirmed)+" )");

                                //limit 5
                                if(countConfirmed<6) {
                                    loadDonorList(dataSnapshot1.getKey(), true);
                                    btnViewDonations.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        progDialog.dismiss();
                    }
                    progDialog.dismiss();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    adapter.notifyDataSetChanged();
                    confirmAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    adapter.notifyDataSetChanged();
                    confirmAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void loadDonorList(final String key, final Boolean type){
        userReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    searchValue = dataSnapshot.getValue(SearchData.class);
                    SearchData info = new SearchData();

                    String user_id = key;
                    String name = searchValue.getName();
                    String city = searchValue.getCity();
                    String bloodtype = searchValue.getBloodtype();
                    String donation_count = searchValue.getDonation_count();
                    String user_photo = searchValue.getUser_photo();
                    String contact = searchValue.getGender();


                    info.setUser_id(user_id);
                    info.setName(name);
                    info.setCity(city);
                    info.setBloodtype(bloodtype);
                    info.setDonation_count(donation_count);
                    info.setUser_photo(user_photo);
                    info.setContact(contact);
                    info.setPost_id(post_id);

                    if(type.equals(false)) {
                        donorList.add(info);
                        adapter.notifyDataSetChanged();
                    }else{
                        confirmList.add(info);
                        confirmAdapter.notifyDataSetChanged();
                    }
                }
                if(donorList.size()==0 || donorList.size()<0){
                    txtInstruction.setVisibility(View.INVISIBLE);
                    txtNoneOne.setVisibility(View.VISIBLE);
                }else{
                    txtInstruction.setVisibility(View.VISIBLE);
                    txtNoneOne.setVisibility(View.INVISIBLE);
                }
                if(confirmList.size()==0 || confirmList.size()<0){
                    txtNoneTwo.setVisibility(View.VISIBLE);
                }else{
                    txtNoneTwo.setVisibility(View.INVISIBLE);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadInfo(){
        postReference.child(selected_id).child("contact").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    txtContact.setText(dataSnapshot.getValue().toString());
                }
                progDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        postReference.child(selected_id).child(post_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SimpleDateFormat simpleDate =  new SimpleDateFormat("MMMM d, y");

                if(dataSnapshot.exists()) {
                    postList = new ArrayList<PostData>();
                    value = dataSnapshot.getValue(PostData.class);
                    Picasso.with(getApplicationContext()).load(value.getPhoto()).fit().centerCrop().into(imgPost);
                    String strNeeded = simpleDate.format(new Date(value.getDate_needed()));
                    String strPostDate = simpleDate.format(new Date(value.getDate()));

                    txtType.setText(value.getBloodtype());
                    txtReceiver.setText(value.getReceiver());
                    txtAddress.setText(value.getLocation());
                    txtDescription.setText(value.getDescription());
                    txtBags.setText(value.getBags());
                    txtDateNeeded.setText(strNeeded);
                    txtPoster.setText("Posted by: "+username + ", " + strPostDate);

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUsername(){
        postReference.child(selected_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    PosterData poster = dataSnapshot.getValue(PosterData.class);
                    username = poster.getUser_name();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

package com.decypher.vesselsapp.MyPosts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.Coordinator.HomeModule.ViewPostDonations;
import com.decypher.vesselsapp.Coordinator.HomeModule.ViewPostRespondents;
import com.decypher.vesselsapp.DonationProcess.DonationStep1;
import com.decypher.vesselsapp.Home.PostData;
import com.decypher.vesselsapp.Home.PosterData;
import com.decypher.vesselsapp.Messages.SelectedChatActivity;
import com.decypher.vesselsapp.Messages.UserSelectedChatActivity;
import com.decypher.vesselsapp.Others.LoginActivity;
import com.decypher.vesselsapp.Others.SignupActivity1;
import com.decypher.vesselsapp.Profile.EditProfileActivity;
import com.decypher.vesselsapp.R;
import com.decypher.vesselsapp.Search.SearchAdapterNotLogged;
import com.decypher.vesselsapp.Search.SearchData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewPost extends AppCompatActivity {

    long allowedDate, dateNow;

    String post_id, myReceiver, selected_id;
    TextView txtReceiver,txtPoster,txtBags,txtDescription,txtDateNeeded,txtAddress,txtType, txtNoneOne, txtNoneTwo, txtContact;
    TextView txtPossible, txtConfirm;
    ImageView imgPost;
    RelativeLayout relativeDonate;
    Button btnDonate;
    ArrayList<PostData> postList;
    ArrayList<SearchData> donorList, confirmList;
    SearchAdapterNotLogged adapter, confirmAdapter;

    Button btnViewRespondents, btnViewDonations;
    TextView txtRespondentCount, txtConfirmedCount;

    RelativeLayout messageContainer;
    Button btnMessage;

    String selected_photo;
    String info_allowed;
    String selected_blood;

    int countChat;
    
    SearchData searchValue;
    PostData value;
    String username="";
    RecyclerView recyclerDonor, recyclerConfirmed;
    int countRespondent = 0;
    int countConfirmed = 0;

    ProgressDialog progDialog;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, donorReference, userReference, directoryReference, chatReference;
    String POST_REFERENCE = "posts";
    String DIRECTORY_REFERENCE = "chat_directory";
    String DONOR_REFERENCE = "posts_donors";
    String USER_REFERENCE = "users";
    String CHAT_REFERENCE = "chats";

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    String response_count, selected_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        final Bundle extras = getIntent().getExtras();
        selected_id = extras.getString("SELECTED_ID");
        post_id = extras.getString("POST_ID");
        myReceiver = extras.getString("RECEIVER");
        selected_name = extras.getString("SELECTED_NAME");
        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
        editor.putString("SELECTED_ID", selected_id);
        editor.commit();

        progDialog = new ProgressDialog(ViewPost.this);
        progDialog.setMessage("Loading post information. .");

        txtConfirm = (TextView) findViewById(R.id.txtConfirm);
        txtPossible = (TextView) findViewById(R.id.txtPossible);

        btnViewDonations = (Button) findViewById(R.id.btnViewDonations);
        btnViewRespondents = (Button) findViewById(R.id.btnViewRespondents);

        btnMessage = (Button) findViewById(R.id.btnMessage);
        messageContainer = (RelativeLayout) findViewById(R.id.messageContainer);
        relativeDonate = (RelativeLayout) findViewById(R.id.relativeDonate);

        txtConfirmedCount = (TextView) findViewById(R.id.txtConfirmedCount);
        txtRespondentCount = (TextView) findViewById(R.id.txtRespondentCount);

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(extras.containsKey("COORDINATOR")) {
                    directoryReference.child(sharedpref.getString("COR_ID","")).child(selected_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                Intent intent = new Intent(ViewPost.this, SelectedChatActivity.class);
                                intent.putExtra("RECIPIENT_ID",selected_id);
                                intent.putExtra("RECIPIENT_NAME",selected_name);
                                intent.putExtra("CHAT_ID",dataSnapshot.child("chat_id").getValue().toString());
                                startActivity(intent);
                            }else{
                                final String key = chatReference.push().getKey();
                                directoryReference.child(sharedpref.getString("COR_ID","")).child(selected_id).child("chat_id").setValue(key, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        final String new_key = chatReference.child(key).push().getKey();
                                        chatReference.child(key).child(new_key).child("sender").setValue("");
                                        chatReference.child(key).child(new_key).child("message").setValue("");
                                        chatReference.child(key).child(new_key).child("receiver").setValue("");
                                        chatReference.child(key).child(new_key).child("time").setValue(123);

                                        Intent intent = new Intent(ViewPost.this, SelectedChatActivity.class);
                                        intent.putExtra("RECIPIENT_ID",selected_id);
                                        intent.putExtra("RECIPIENT_NAME",selected_name);
                                        intent.putExtra("CHAT_ID",key);
                                        startActivity(intent);
                                    }
                                });


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }else {
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
                            Intent intent = new Intent(ViewPost.this, UserSelectedChatActivity.class);
                            intent.putExtra("RECEIVER", myReceiver);
                            startActivity(intent);
                        }
                    });
                }
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


        if(extras.containsKey("COORDINATOR")){
            btnDonate.setVisibility(View.INVISIBLE);
            relativeDonate.setVisibility(View.GONE);
            btnMessage.setText("Send a Message");

            //configure btnView
            btnViewRespondents.setText("View All Respondents");
            btnViewRespondents.setTextColor(getResources().getColor(R.color.colorAccent));
            btnViewRespondents.setEnabled(true);

            btnViewDonations.setText("View All Donors");
            btnViewDonations.setTextColor(getResources().getColor(R.color.colorAccent));
            btnViewDonations.setEnabled(true);

            btnViewRespondents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewPost.this, ViewPostRespondents.class);
                    intent.putExtra("RECEIVER", myReceiver);
                    intent.putExtra("POST_ID", post_id);
                    intent.putExtra("SELECTED_ID", selected_id);
                    startActivity(intent);
                }
            });

            btnViewDonations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewPost.this, ViewPostDonations.class);
                    intent.putExtra("RECEIVER", myReceiver);
                    intent.putExtra("POST_ID", post_id);
                    intent.putExtra("SELECTED_ID", selected_id);
                    startActivity(intent);
                }
            });
        }
        recyclerDonor = (RecyclerView) findViewById(R.id.recyclerDonor);
        recyclerDonor.setHasFixedSize(true);
        recyclerDonor.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        donorList = new ArrayList<SearchData>();
        adapter = new SearchAdapterNotLogged(ViewPost.this, donorList);
        recyclerDonor.setAdapter(adapter);

        recyclerConfirmed = (RecyclerView) findViewById(R.id.recyclerConfirmed);
        recyclerConfirmed.setHasFixedSize(true);
        recyclerConfirmed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        confirmList = new ArrayList<SearchData>();
        confirmAdapter = new SearchAdapterNotLogged(ViewPost.this, confirmList);
        recyclerConfirmed.setAdapter(confirmAdapter);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(myReceiver);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        donorReference = mDatabase.getReference(DONOR_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        directoryReference = mDatabase.getReference(DIRECTORY_REFERENCE);
        chatReference = mDatabase.getReference(CHAT_REFERENCE);

        getUsername();
        loadInfo();
        loadDonors();

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpref.getString("USERID", "") != "") {
                    if(!sharedpref.getString("LASTDONATE","").equals("")) {
                        SimpleDateFormat simpleDate = new SimpleDateFormat("MMMM d, y");
                        SimpleDateFormat checkDate = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar c = Calendar.getInstance();
                        Calendar now = Calendar.getInstance();

                        String checkDateString = checkDate.format(new Date(sharedpref.getString("LASTDONATE", "")));
                        try {
                            c.setTime(checkDate.parse(checkDateString));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //3 months rule
                        c.add(Calendar.MONTH, 3);
                        allowedDate = c.getTimeInMillis();
                        dateNow = now.getTimeInMillis();

                        if (allowedDate <= dateNow) {

                            //DONATION PROCESS
                            int ctr_validate = 0;

                            //VERIFICATION PER BLOOD

                            if (selected_blood.equals("O+")) {
                                if (sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("O-")) {
                                    //sulod
                                    ctr_validate++;
                                }
                            } else if (selected_blood.equals("O-")) {
                                if (sharedpref.getString("USERBLOOD", "").equals("O-")) {
                                    //sulod
                                    ctr_validate++;
                                }
                            } else if (selected_blood.equals("A+")) {
                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("A+") || sharedpref.getString("USERBLOOD", "").equals("A-")) {
                                    //sulod
                                    ctr_validate++;
                                }
                            } else if (selected_blood.equals("A-")) {
                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("A-")) {
                                    //sulod
                                    ctr_validate++;
                                }
                            } else if (selected_blood.equals("B+")) {
                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("B+") || sharedpref.getString("USERBLOOD", "").equals("B-")) {
                                    //sulod
                                    ctr_validate++;
                                }
                            } else if (selected_blood.equals("B-")) {
                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("B-")) {
                                    //sulod
                                    ctr_validate++;
                                }
                            } else if (selected_blood.equals("AB+")) {
                                //all types man
                                ctr_validate++;
                            } else if (selected_blood.equals("AB-")) {
                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("B-") || sharedpref.getString("USERBLOOD", "").equals("A-") || sharedpref.getString("USERBLOOD", "").equals("AB-")) {
                                    //sulod
                                    ctr_validate++;
                                }
                            }

                            if (ctr_validate > 0) {
                                postReference.child(selected_id).child(post_id).child("response_count").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Intent intent = new Intent(ViewPost.this, DonationStep1.class);
                                        intent.putExtra("POST_ID", post_id);
                                        intent.putExtra("SELECTED_ID", selected_id);
                                        intent.putExtra("POST_RECEIVER", myReceiver);
                                        intent.putExtra("POST_PHOTO", selected_photo);
                                        intent.putExtra("RESPONSE_COUNT", dataSnapshot.getValue().toString());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                if (selected_blood.equals("O+")) {
                                    info_allowed = "Allowed donors for blood type 'O+' are blood types O+ and O- only";
                                } else if (selected_blood.equals("O-")) {
                                    info_allowed = "Allowed donors for blood type 'O-' is blood type O- only";
                                } else if (selected_blood.equals("A+")) {
                                    info_allowed = "Allowed donors for blood type 'A+' are blood types O+ , O- , A+ and A- only";
                                } else if (selected_blood.equals("A-")) {
                                    info_allowed = "Allowed donors for blood type 'A-' are blood types O- and A- only";
                                } else if (selected_blood.equals("B+")) {
                                    info_allowed = "Allowed donors for blood type 'B+' are blood types O+ , O- , B+ and B- only";
                                } else if (selected_blood.equals("B-")) {
                                    info_allowed = "Allowed donors for blood type 'B-' are blood types B- and O- only";
                                } else if (selected_blood.equals("AB-")) {
                                    info_allowed = "Allowed donors for blood type 'AB-' are blood types AB- , A- , B- and O- only";
                                }

                                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                                final View mView = getLayoutInflater().inflate(R.layout.dialog_allowed, null);
                                //variables
                                mBuilder.setView(mView);
                                final AlertDialog dialog = mBuilder.create();
                                dialog.show();
                                Button btnClose = mView.findViewById(R.id.btnProceed);
                                btnClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                TextView txtUserblood = mView.findViewById(R.id.txtUserblood);
                                TextView txtRecipientBlood = mView.findViewById(R.id.txtRecipientBlood);
                                TextView txtInfo = mView.findViewById(R.id.txtInfo);
                                txtUserblood.setText(sharedpref.getString("USERBLOOD", ""));
                                txtRecipientBlood.setText(selected_blood);
                                txtInfo.setText(info_allowed);
                            }
                        } else {
                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                            final View mView = getLayoutInflater().inflate(R.layout.dialog_nodate, null);
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
                            //variables
                            Button btnClose = mView.findViewById(R.id.btnProceed);
                            TextView txtDaysRemain = mView.findViewById(R.id.txtDaysRemain);
                            TextView txtLastDonate = mView.findViewById(R.id.txtDateDonated);

                            txtLastDonate.setText("Last Donated: " + simpleDate.format(new Date(sharedpref.getString("LASTDONATE", ""))));
                            int days = (int) (((allowedDate - dateNow) / (1000 * 60 * 60 * 24)));

                            txtDaysRemain.setText(String.valueOf(days) + " days remaining");
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                        }
                    }else{

                        //DONATION PROCESS
                        int ctr_validate = 0;

                        //VERIFICATION PER BLOOD

                        if (selected_blood.equals("O+")) {
                            if (sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("O-")) {
                                //sulod
                                ctr_validate++;
                            }
                        } else if (selected_blood.equals("O-")) {
                            if (sharedpref.getString("USERBLOOD", "").equals("O-")) {
                                //sulod
                                ctr_validate++;
                            }
                        } else if (selected_blood.equals("A+")) {
                            if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("A+") || sharedpref.getString("USERBLOOD", "").equals("A-")) {
                                //sulod
                                ctr_validate++;
                            }
                        } else if (selected_blood.equals("A-")) {
                            if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("A-")) {
                                //sulod
                                ctr_validate++;
                            }
                        } else if (selected_blood.equals("B+")) {
                            if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("B+") || sharedpref.getString("USERBLOOD", "").equals("B-")) {
                                //sulod
                                ctr_validate++;
                            }
                        } else if (selected_blood.equals("B-")) {
                            if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("B-")) {
                                //sulod
                                ctr_validate++;
                            }
                        } else if (selected_blood.equals("AB+")) {
                            //all types man
                            ctr_validate++;
                        } else if (selected_blood.equals("AB-")) {
                            if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("B-") || sharedpref.getString("USERBLOOD", "").equals("A-") || sharedpref.getString("USERBLOOD", "").equals("AB-")) {
                                //sulod
                                ctr_validate++;
                            }
                        }

                        if (ctr_validate > 0) {
                            postReference.child(selected_id).child(post_id).child("response_count").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent intent = new Intent(ViewPost.this, DonationStep1.class);
                                    intent.putExtra("POST_ID", post_id);
                                    intent.putExtra("SELECTED_ID", selected_id);
                                    intent.putExtra("POST_RECEIVER", myReceiver);
                                    intent.putExtra("POST_PHOTO", selected_photo);
                                    intent.putExtra("RESPONSE_COUNT", dataSnapshot.getValue().toString());
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            if (selected_blood.equals("O+")) {
                                info_allowed = "Allowed donors for blood type 'O+' are blood types O+ and O- only";
                            } else if (selected_blood.equals("O-")) {
                                info_allowed = "Allowed donors for blood type 'O-' is blood type O- only";
                            } else if (selected_blood.equals("A+")) {
                                info_allowed = "Allowed donors for blood type 'A+' are blood types O+ , O- , A+ and A- only";
                            } else if (selected_blood.equals("A-")) {
                                info_allowed = "Allowed donors for blood type 'A-' are blood types O- and A- only";
                            } else if (selected_blood.equals("B+")) {
                                info_allowed = "Allowed donors for blood type 'B+' are blood types O+ , O- , B+ and B- only";
                            } else if (selected_blood.equals("B-")) {
                                info_allowed = "Allowed donors for blood type 'B-' are blood types B- and O- only";
                            } else if (selected_blood.equals("AB-")) {
                                info_allowed = "Allowed donors for blood type 'AB-' are blood types AB- , A- , B- and O- only";
                            }

                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                            final View mView = getLayoutInflater().inflate(R.layout.dialog_allowed, null);
                            //variables
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
                            Button btnClose = mView.findViewById(R.id.btnProceed);
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            TextView txtUserblood = mView.findViewById(R.id.txtUserblood);
                            TextView txtRecipientBlood = mView.findViewById(R.id.txtRecipientBlood);
                            TextView txtInfo = mView.findViewById(R.id.txtInfo);
                            txtUserblood.setText(sharedpref.getString("USERBLOOD", ""));
                            txtRecipientBlood.setText(selected_blood);
                            txtInfo.setText(info_allowed);
                        }
                    }
                }else{
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                    final View mView = getLayoutInflater().inflate(R.layout.dialog_donate, null);
                    //variables
                    Button btnSign = mView.findViewById(R.id.btnProceed);
                    Button btnRegister = mView.findViewById(R.id.btnRegister);
                    btnSign.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    btnRegister.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), SignupActivity1.class);
                            startActivity(intent);
                        }
                    });
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
            }
        });
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        switch (id) {
            case R.id.menu_message:
                countChat = 0;
                //searh kng may existing chat or wala
                directoryReference.child(sharedpref.getString("USERID","")).child(selected_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Intent intent = new Intent(ViewPost.this, SelectedChatActivity.class);
                        intent.putExtra("CHAT_ID", dataSnapshot.getValue().toString());
                        intent.putExtra("RECIPIENT_ID", selected_id);
                        intent.putExtra("RECIPIENT_NAME", selected_name);

                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        donorList.clear();
    }

    private void loadDonors(){
        donorList.clear();
        txtNoneTwo.setVisibility(View.VISIBLE);
        txtNoneOne.setVisibility(View.VISIBLE);
        if(sharedpref.getString("COR_ID","").equals("") && sharedpref.getString("USERID","").equals("")){
            btnMessage.setText("Please sign in");
            btnMessage.setEnabled(false);
            /*txtNoneOne.setVisibility(View.GONE);
            txtNoneTwo.setVisibility(View.GONE);
            txtPossible.setVisibility(View.GONE);
            txtConfirm.setVisibility(View.GONE);
            recyclerConfirmed.setVisibility(View.GONE);
            recyclerDonor.setVisibility(View.GONE);
            *///btnDonate.setVisibility(View.GONE);
        }
            donorReference.orderByKey().equalTo(post_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (!dataSnapshot.exists()){

                        btnViewRespondents.setVisibility(View.GONE);
                        btnViewDonations.setVisibility(View.GONE);
                        progDialog.dismiss();
                    }else {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if(dataSnapshot1.getKey().equals(sharedpref.getString("USERID",""))){
                                btnDonate.setText("You have already responded");
                                btnDonate.setEnabled(false);
                            }
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

    private void loadDonorList(final String key, final Boolean type){
        userReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()) {
                   searchValue = dataSnapshot.getValue(SearchData.class);
                   SearchData info = new SearchData();
                   String name;
                   String user_id = key;
                   if(sharedpref.getString("COR_ID","").equals("")){
                       name = "Anonymous";
                   }else{
                       name = searchValue.getName();
                   }
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
                   txtNoneOne.setVisibility(View.VISIBLE);
               }else{
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
        progDialog.show();
        postReference.child(selected_id).child("contact").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(!sharedpref.getString("COR_ID","").equals("")) {
                        txtContact.setText(dataSnapshot.getValue().toString());
                    }else{
                        txtContact.setText("(034)441-6313 (Blood Bank)");
                    }
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

                    selected_photo = value.getPhoto();
                    selected_blood = value.getBloodtype();
                    
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

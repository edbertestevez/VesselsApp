package com.decypher.vesselsapp.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.DonationProcess.DonationStep1;
import com.decypher.vesselsapp.MyPosts.ViewMyPostActivity;
import com.decypher.vesselsapp.MyPosts.ViewPost;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.Others.LoginActivity;
import com.decypher.vesselsapp.Others.SignupActivity1;
import com.decypher.vesselsapp.R;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by trebd on 10/17/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    long allowedDate, dateNow;

    GlobalFunctions globalFunctions;
    private ArrayList<PostData> postList;
    private Context context;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, donorReference, userReference, directoryReference, chatReference;
    String POST_REFERENCE = "posts";
    String DIRECTORY_REFERENCE = "chat_directory";
    String DONOR_REFERENCE = "posts_donors";
    String USER_REFERENCE = "users";
    String CHAT_REFERENCE = "chats";
    int count_confirmed = 0;


    public PostAdapter(Context context, ArrayList<PostData> postList) {
        this.context = context;
        this.postList = postList;
        sharedpref = context.getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(context);
        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        donorReference = mDatabase.getReference(DONOR_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        directoryReference = mDatabase.getReference(DIRECTORY_REFERENCE);
        chatReference = mDatabase.getReference(CHAT_REFERENCE);
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_itemv2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostAdapter.ViewHolder holder, int position) {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("MMM d, y");

        final PostData currImage = postList.get(position);

        if(sharedpref.getString("USERID","").equals(currImage.getUser_id())){
            holder.btnDonate.setText("Manage Post");
        }else{
            holder.btnDonate.setText("Donate");
        }

        count_confirmed=0;

        holder.txtUsername.setText(currImage.getUser_name());
        holder.txtPostDate.setText(simpleDate.format(new Date(currImage.getDate())));
        holder.txtBloodtype.setText(currImage.getBloodtype());
        holder.txtReceiver.setText(currImage.getReceiver());
        holder.txtAddress.setText(currImage.getLocation());
        holder.txtDescription.setText(currImage.getDescription());
        holder.txtNeeded.setText(simpleDate.format(new Date(currImage.getDate_needed())));
        if(currImage.getBags().equals("1")){
            holder.txtBags.setText(currImage.getBags() + " Bag");
        }else {
            holder.txtBags.setText(currImage.getBags() + " Bags");
        }
        Picasso.with(context).load(currImage.getPhoto()).fit().centerCrop().into(holder.imgPost);
        Picasso.with(context).load(currImage.getUser_photo()).placeholder(R.drawable.default_user).into(holder.imgUser);
        holder.txtResponse.setText(String.valueOf(currImage.getResponse_count()));

        /*
        donorReference.child(currImage.getPost_id()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    if (dataSnapshot.getValue().equals(true)) {
                        count_confirmed++;
                        holder.txtResponse.setText(String.valueOf(count_confirmed));
                    }
                }else{
                    holder.txtResponse.setText("0");
                }
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
        });*/

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgUser, imgPost;
        public TextView txtUsername, txtPostDate, txtBloodtype, txtReceiver, txtAddress, txtDescription, txtBags, txtNeeded, txtResponse;
        public Button btnDonate;

        public ViewHolder(final View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            imgPost = itemView.findViewById(R.id.imgPost);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtPostDate = itemView.findViewById(R.id.txtPostDate);
            txtBloodtype = itemView.findViewById(R.id.txtBloodType);
            txtReceiver = itemView.findViewById(R.id.txtSender);
            txtAddress = itemView.findViewById(R.id.etAddress);
            txtBags = itemView.findViewById(R.id.txtBags);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtNeeded = itemView.findViewById(R.id.txtNeeded);
            btnDonate = itemView.findViewById(R.id.btnDonate);
            txtResponse = itemView.findViewById(R.id.txtResponse);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final PostData selectedPost = postList.get(position);
                    if(!sharedpref.getString("USERID","").equals(selectedPost.getUser_id())) {
                        Intent intent = new Intent(context, ViewPost.class);
                        intent.putExtra("POST_ID", selectedPost.getPost_id());
                        intent.putExtra("RECEIVER", selectedPost.getReceiver());
                        intent.putExtra("SELECTED_ID", selectedPost.getUser_id());
                        intent.putExtra("SELECTED_NAME", selectedPost.getUser_name());
                        intent.putExtra("STATUS",selectedPost.getStatus());
                        Log.i("POST ID => ", selectedPost.getPost_id());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }else{
                        Intent intent = new Intent(context, ViewMyPostActivity.class);
                        intent.putExtra("POST_ID", selectedPost.getPost_id());
                        intent.putExtra("RECEIVER", selectedPost.getReceiver());
                        intent.putExtra("SELECTED_ID", selectedPost.getUser_id());
                        Log.i("POST ID => ", selectedPost.getPost_id());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });

            btnDonate.setOnClickListener(new View.OnClickListener() {
                String info_allowed;
                @Override
                public void onClick(final View view) {
                    int position = getAdapterPosition();
                    final PostData selectedPost = postList.get(position);
                    if(sharedpref.getString("USERID","").equals(selectedPost.getUser_id())) {
                        Intent intent = new Intent(context, ViewMyPostActivity.class);
                        intent.putExtra("POST_ID", selectedPost.getPost_id());
                        intent.putExtra("RECEIVER", selectedPost.getReceiver());
                        intent.putExtra("SELECTED_ID", selectedPost.getUser_id());
                        Log.i("POST ID => ", selectedPost.getPost_id());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent);
                    }else{
                        if(sharedpref.getString("USERID","").equals("")){
                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                                final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_donate, null);
                            //variables
                            Button btnSign = mView.findViewById(R.id.btnProceed);
                            Button btnRegister = mView.findViewById(R.id.btnRegister);
                            btnSign.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            });
                            btnRegister.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, SignupActivity1.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            });
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
                        }else {

                            //DIRI ANG CHAKTO PRE
                            donorReference.child(selectedPost.getPost_id()).child(sharedpref.getString("USERID","")).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()){
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

                                                if (selectedPost.getBloodtype().equals("O+")) {
                                                    if (sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("O-")) {
                                                        //sulod
                                                        ctr_validate++;
                                                    }
                                                } else if (selectedPost.getBloodtype().equals("O-")) {
                                                    if (sharedpref.getString("USERBLOOD", "").equals("O-")) {
                                                        //sulod
                                                        ctr_validate++;
                                                    }
                                                } else if (selectedPost.getBloodtype().equals("A+")) {
                                                    if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("A+") || sharedpref.getString("USERBLOOD", "").equals("A-")) {
                                                        //sulod
                                                        ctr_validate++;
                                                    }
                                                } else if (selectedPost.getBloodtype().equals("A-")) {
                                                    if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("A-")) {
                                                        //sulod
                                                        ctr_validate++;
                                                    }
                                                } else if (selectedPost.getBloodtype().equals("B+")) {
                                                    if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("B+") || sharedpref.getString("USERBLOOD", "").equals("B-")) {
                                                        //sulod
                                                        ctr_validate++;
                                                    }
                                                } else if (selectedPost.getBloodtype().equals("B-")) {
                                                    if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("B-")) {
                                                        //sulod
                                                        ctr_validate++;
                                                    }
                                                } else if (selectedPost.getBloodtype().equals("AB+")) {
                                                    //all types man
                                                    ctr_validate++;
                                                } else if (selectedPost.getBloodtype().equals("AB-")) {
                                                    if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("B-") || sharedpref.getString("USERBLOOD", "").equals("A-") || sharedpref.getString("USERBLOOD", "").equals("AB-")) {
                                                        //sulod
                                                        ctr_validate++;
                                                    }
                                                }

                                                if (ctr_validate > 0) {
                                                    Intent intent = new Intent(context, DonationStep1.class);
                                                    intent.putExtra("POST_ID", selectedPost.getPost_id());
                                                    intent.putExtra("POST_RECEIVER", selectedPost.getReceiver());
                                                    intent.putExtra("POST_PHOTO", selectedPost.getPhoto());
                                                    intent.putExtra("SELECTED_ID", selectedPost.getUser_id());
                                                    intent.putExtra("RESPONSE_COUNT", selectedPost.getResponse_count());
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    context.startActivity(intent);
                                                } else {
                                                    if (selectedPost.getBloodtype().equals("O+")) {
                                                        info_allowed = "Allowed donors for blood type 'O+' are blood types O+ and O- only";
                                                    } else if (selectedPost.getBloodtype().equals("O-")) {
                                                        info_allowed = "Allowed donors for blood type 'O-' is blood type O- only";
                                                    } else if (selectedPost.getBloodtype().equals("A+")) {
                                                        info_allowed = "Allowed donors for blood type 'A+' are blood types O+ , O- , A+ and A- only";
                                                    } else if (selectedPost.getBloodtype().equals("A-")) {
                                                        info_allowed = "Allowed donors for blood type 'A-' are blood types O- and A- only";
                                                    } else if (selectedPost.getBloodtype().equals("B+")) {
                                                        info_allowed = "Allowed donors for blood type 'B+' are blood types O+ , O- , B+ and B- only";
                                                    } else if (selectedPost.getBloodtype().equals("B-")) {
                                                        info_allowed = "Allowed donors for blood type 'B-' are blood types B- and O- only";
                                                    } else if (selectedPost.getBloodtype().equals("AB-")) {
                                                        info_allowed = "Allowed donors for blood type 'AB-' are blood types AB- , A- , B- and O- only";
                                                    }

                                                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                                                    final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_allowed, null);
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
                                                    txtRecipientBlood.setText(selectedPost.getBloodtype());
                                                    txtInfo.setText(info_allowed);
                                                }
                                            } else {
                                                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                                                final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_nodate, null);
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

                                            if (selectedPost.getBloodtype().equals("O+")) {
                                                if (sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("O-")) {
                                                    //sulod
                                                    ctr_validate++;
                                                }
                                            } else if (selectedPost.getBloodtype().equals("O-")) {
                                                if (sharedpref.getString("USERBLOOD", "").equals("O-")) {
                                                    //sulod
                                                    ctr_validate++;
                                                }
                                            } else if (selectedPost.getBloodtype().equals("A+")) {
                                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("A+") || sharedpref.getString("USERBLOOD", "").equals("A-")) {
                                                    //sulod
                                                    ctr_validate++;
                                                }
                                            } else if (selectedPost.getBloodtype().equals("A-")) {
                                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("A-")) {
                                                    //sulod
                                                    ctr_validate++;
                                                }
                                            } else if (selectedPost.getBloodtype().equals("B+")) {
                                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("O+") || sharedpref.getString("USERBLOOD", "").equals("B+") || sharedpref.getString("USERBLOOD", "").equals("B-")) {
                                                    //sulod
                                                    ctr_validate++;
                                                }
                                            } else if (selectedPost.getBloodtype().equals("B-")) {
                                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("B-")) {
                                                    //sulod
                                                    ctr_validate++;
                                                }
                                            } else if (selectedPost.getBloodtype().equals("AB+")) {
                                                //all types man
                                                ctr_validate++;
                                            } else if (selectedPost.getBloodtype().equals("AB-")) {
                                                if (sharedpref.getString("USERBLOOD", "").equals("O-") || sharedpref.getString("USERBLOOD", "").equals("B-") || sharedpref.getString("USERBLOOD", "").equals("A-") || sharedpref.getString("USERBLOOD", "").equals("AB-")) {
                                                    //sulod
                                                    ctr_validate++;
                                                }
                                            }

                                            if (ctr_validate > 0) {
                                                Intent intent = new Intent(context, DonationStep1.class);
                                                intent.putExtra("POST_ID", selectedPost.getPost_id());
                                                intent.putExtra("POST_RECEIVER", selectedPost.getReceiver());
                                                intent.putExtra("POST_PHOTO", selectedPost.getPhoto());
                                                intent.putExtra("SELECTED_ID", selectedPost.getUser_id());
                                                intent.putExtra("RESPONSE_COUNT", selectedPost.getResponse_count());
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                            } else {
                                                if (selectedPost.getBloodtype().equals("O+")) {
                                                    info_allowed = "Allowed donors for blood type 'O+' are blood types O+ and O- only";
                                                } else if (selectedPost.getBloodtype().equals("O-")) {
                                                    info_allowed = "Allowed donors for blood type 'O-' is blood type O- only";
                                                } else if (selectedPost.getBloodtype().equals("A+")) {
                                                    info_allowed = "Allowed donors for blood type 'A+' are blood types O+ , O- , A+ and A- only";
                                                } else if (selectedPost.getBloodtype().equals("A-")) {
                                                    info_allowed = "Allowed donors for blood type 'A-' are blood types O- and A- only";
                                                } else if (selectedPost.getBloodtype().equals("B+")) {
                                                    info_allowed = "Allowed donors for blood type 'B+' are blood types O+ , O- , B+ and B- only";
                                                } else if (selectedPost.getBloodtype().equals("B-")) {
                                                    info_allowed = "Allowed donors for blood type 'B-' are blood types B- and O- only";
                                                } else if (selectedPost.getBloodtype().equals("AB-")) {
                                                    info_allowed = "Allowed donors for blood type 'AB-' are blood types AB- , A- , B- and O- only";
                                                }

                                                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                                                final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_allowed, null);
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
                                                txtRecipientBlood.setText(selectedPost.getBloodtype());
                                                txtInfo.setText(info_allowed);
                                            }
                                        }
                                    }else{
                                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                                        final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_nodate, null);
                                        mBuilder.setView(mView);
                                        final AlertDialog dialog = mBuilder.create();
                                        dialog.show();
                                        //variables
                                        Button btnClose = mView.findViewById(R.id.btnProceed);
                                        TextView txtDaysRemain = mView.findViewById(R.id.txtDaysRemain);
                                        TextView txtLastDonate = mView.findViewById(R.id.txtDateDonated);
                                        TextView txtInfo = mView.findViewById(R.id.txtInfo);
                                        txtDaysRemain.setVisibility(View.GONE);
                                        txtLastDonate.setVisibility(View.GONE);
                                        txtInfo.setText("You have already responded to this post");
                                        btnClose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }

                    }
                }
            });


        }
    }
    public void clear() {
        // TODO Auto-generated method stub
        postList.clear();

    }
}
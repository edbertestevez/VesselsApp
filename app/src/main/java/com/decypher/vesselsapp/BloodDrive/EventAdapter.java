package com.decypher.vesselsapp.BloodDrive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.Others.LoginActivity;
import com.decypher.vesselsapp.Others.SignupActivity1;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by trebd on 11/12/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    GlobalFunctions globalFunctions;
    private ArrayList<BloodDriveData> driveList;
    private Context context;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference eventsGoingReference, postReference, userReference, corReference, driveReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String EVENTS_GOING_REFERENCE = "events_going";
    String COR_REFERENCE = "coordinators";
    String DRIVE_REFERENCE = "blood_drives";
    String formatted_date;

    public EventAdapter(Context context, ArrayList<BloodDriveData> driveList) {
        this.context = context;
        this.driveList = driveList;
        sharedpref = context.getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(context);
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        corReference = mDatabase.getReference(COR_REFERENCE);
        eventsGoingReference = mDatabase.getReference(EVENTS_GOING_REFERENCE);
        driveReference = mDatabase.getReference(DRIVE_REFERENCE);
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list, parent, false);
        return new EventAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(EventAdapter.ViewHolder holder, int position) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("MMM d, y");
        BloodDriveData currImage = driveList.get(position);

        holder.txtDate.setText(simpleDate.format(new Date(currImage.getDate())));
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(currImage.getTime_start());
            final Date dateObj1 = sdf.parse(currImage.getTime_end());
            holder.txtTime.setText(new SimpleDateFormat("K:mm a").format(dateObj) + " - "+new SimpleDateFormat("K:mm a").format(dateObj1));
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        holder.txtTime.setText(currImage.getTime_start()+" - "+currImage.getTime_end());
        holder.txtName.setText(currImage.getName());
        holder.txtLocation.setText(currImage.getAddress());
        holder.txtDescription.setText(currImage.getDescription());

        formatted_date = simpleDate.format(new Date(currImage.getDate()));
    }

    @Override
    public int getItemCount() {
        return driveList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDate, txtTime, txtName, txtLocation, txtDescription;
        public Button btnGoing;

        public ViewHolder(final View itemView) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtName = itemView.findViewById(R.id.txtAssociation);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            btnGoing = itemView.findViewById(R.id.btnGoing);

            btnGoing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    int position = getAdapterPosition();
                    final BloodDriveData selectedPost = driveList.get(position);

                    if(globalFunctions.isNetworkAvailable()){
                        if(sharedpref.getString("USERID","").equals("")){
                            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                            final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_drive, null);
                            mBuilder.setView(mView);
                            final AlertDialog dialog = mBuilder.create();
                            dialog.show();
                            TextView txtDonorName = mView.findViewById(R.id.txtDonorName);
                            TextView txtInfo = mView.findViewById(R.id.txtInfo);
                            txtInfo.setText("You need to sign in to join this event");
                            TextView txtTitle = mView.findViewById(R.id.txtTitle);
                            txtTitle.setText("Confirmation");
                            Button btnProceed = mView.findViewById(R.id.btnProceed);
                            Button btnClose = mView.findViewById(R.id.btnProceed);
                            btnProceed.setText("Sign In");
                            btnClose.setText("Register");
                            txtDonorName.setText("You are not logged in");
                            btnProceed.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    context.startActivity(intent);
                                }
                            });
                            btnClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, SignupActivity1.class);
                                    context.startActivity(intent);
                                }
                            });
                        }else {

                            eventsGoingReference.child(selectedPost.getDrive_id()).child(sharedpref.getString("USERID", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                                        final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_drive, null);
                                        mBuilder.setView(mView);
                                        final AlertDialog dialog = mBuilder.create();
                                        dialog.show();
                                        TextView txtDonorName = mView.findViewById(R.id.txtDonorName);
                                        TextView txtInfo = mView.findViewById(R.id.txtInfo);
                                        txtInfo.setText("Once confirmed you're data will be part of the list of attendees of this event. Proceed?");
                                        TextView txtTitle = mView.findViewById(R.id.txtTitle);
                                        txtTitle.setText("Confirmation");
                                        Button btnProceed = mView.findViewById(R.id.btnProceed);
                                        Button btnClose = mView.findViewById(R.id.btnClose);
                                        txtDonorName.setText(sharedpref.getString("USERNAME", ""));

                                        btnProceed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //update donation count ni toto
                                                mDatabase.getReference("events_going").child(selectedPost.getDrive_id()).child(sharedpref.getString("USERID", "")).setValue(true);
                                                Toast.makeText(context, "You have been successfully added in the list", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });

                                        btnClose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                            }
                                        });

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
                                        txtInfo.setText("You have already confirmed participation to this event");
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
                    }else{
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final BloodDriveData selectedPost = driveList.get(position);

                    Intent intent = new Intent(context, SelectedEventActivity.class);
                    intent.putExtra("DRIVE_ID", selectedPost.getDrive_id());
                    intent.putExtra("DRIVE_TITLE", selectedPost.getName());
                    intent.putExtra("DRIVE_DATE", formatted_date);
                    intent.putExtra("DRIVE_ADDRESS", selectedPost.getAddress());
                    intent.putExtra("DRIVE_TIME", selectedPost.getTime_start()+" - "+selectedPost.getTime_end());
                    intent.putExtra("DRIVE_TIME_FROM", selectedPost.getTime_start());
                    intent.putExtra("DRIVE_TIME_TO", selectedPost.getTime_end());
                    intent.putExtra("DRIVE_DESC", selectedPost.getDescription());
                    intent.putExtra("DRIVE_PHOTO", selectedPost.getPhoto());
                    intent.putExtra("COR_ASSOCIATION", selectedPost.getAssociation());
                    intent.putExtra("COR_CONTACT", selectedPost.getContact());
                    intent.putExtra("COR_PHOTO", selectedPost.getCor_photo());
                    intent.putExtra("DATE_ORIG", selectedPost.getDate());
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("MY_BLOOD_DRIVE", false);
                    context.startActivity(intent);
                }
            });



        }
    }
}
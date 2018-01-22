package com.decypher.vesselsapp.Coordinator.EventModule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.decypher.vesselsapp.BloodDrive.BloodDriveData;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by trebd on 10/27/2017.
 */

public class MyBloodDriveAdapter extends RecyclerView.Adapter<MyBloodDriveAdapter.ViewHolder> {

    GlobalFunctions globalFunctions;
    private ArrayList<BloodDriveData> driveList;
    private Context context;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, corReference, driveReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String COR_REFERENCE = "coordinators";
    String DRIVE_REFERENCE = "blood_drives";
    String formatted_date;

    public MyBloodDriveAdapter(Context context, ArrayList<BloodDriveData> driveList) {
        this.context = context;
        this.driveList = driveList;
        sharedpref = context.getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(context);
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        corReference = mDatabase.getReference(COR_REFERENCE);
        driveReference = mDatabase.getReference(DRIVE_REFERENCE);
    }

    @Override
    public MyBloodDriveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_blooddrive_item, parent, false);
        return new MyBloodDriveAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(MyBloodDriveAdapter.ViewHolder holder, int position) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("MMM d, y");
        BloodDriveData currImage = driveList.get(position);
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(currImage.getTime_start());
            final Date dateObj1 = sdf.parse(currImage.getTime_end());
            holder.txtTime.setText(new SimpleDateFormat("K:mm a").format(dateObj) + " - "+new SimpleDateFormat("K:mm a").format(dateObj1));
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        holder.txtDate.setText(simpleDate.format(new Date(currImage.getDate())));
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
        public Button btnManage;


        public ViewHolder(final View itemView) {
            super(itemView);

            btnManage = itemView.findViewById(R.id.btnManage);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtName = itemView.findViewById(R.id.txtAssociation);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtDescription = itemView.findViewById(R.id.txtDescription);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final BloodDriveData selectedPost = driveList.get(position);

                    Intent intent = new Intent(context, MySelectedDriveActivity.class);
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

                    intent.putExtra("MY_BLOOD_DRIVE", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            btnManage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final BloodDriveData selectedPost = driveList.get(position);

                    Intent intent = new Intent(context, MySelectedDriveActivity.class);
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

                    intent.putExtra("MY_BLOOD_DRIVE", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


        }
    }
}
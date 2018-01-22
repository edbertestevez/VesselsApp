package com.decypher.vesselsapp.Profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.decypher.vesselsapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by trebd on 11/9/2017.
 */

public class DonationListAdapter extends RecyclerView.Adapter<DonationListAdapter.ViewHolder> {

    private ArrayList<DonationData> donationList;
    private Context context;
    private FirebaseDatabase mDatabase;
    private DatabaseReference corReference, donationReference;
    String COR_REFERENCE = "posts";
    String DONATION_REFERENCE = "users";


    public DonationListAdapter(Context context, ArrayList<DonationData> donationList) {
        this.context = context;
        this.donationList = donationList;

        mDatabase = FirebaseDatabase.getInstance();

        corReference = mDatabase.getReference(COR_REFERENCE);
        donationReference = mDatabase.getReference(DONATION_REFERENCE);
    }

    @Override
    public DonationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_list_item, parent, false);
        return new DonationListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DonationListAdapter.ViewHolder holder, int position) {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("MMMM d, y");

        DonationData currImage = donationList.get(position);

        holder.txtAssociation.setText(currImage.getAssociation());
        holder.txtDate.setText(simpleDate.format(new Date(currImage.getDate())));
        Picasso.with(context).load(currImage.getAssociaton_photo()).into(holder.imgAssociation);
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgAssociation;
        public TextView txtAssociation, txtDate;
        
        public ViewHolder(final View itemView) {
            super(itemView);

            imgAssociation = itemView.findViewById(R.id.imgAssociation);
            txtAssociation = itemView.findViewById(R.id.txtAssociation);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
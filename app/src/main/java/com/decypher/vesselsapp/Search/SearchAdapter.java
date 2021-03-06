package com.decypher.vesselsapp.Search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.Profile.DonorProfileActivity;
import com.decypher.vesselsapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by trebd on 10/19/2017.
 */

public class SearchAdapter  extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<SearchData> searchList;
    private Context context;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    public SearchAdapter(Context context, ArrayList<SearchData> searchList) {
        this.context = context;
        this.searchList = searchList;
        sharedpref = context.getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donor_list_item, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        //SimpleDateFormat simpleDate =  new SimpleDateFormat("MMMM d, y");

        SearchData currImage = searchList.get(position);

        if(!sharedpref.getString("COR_ID","").equals("")) {
            holder.txtName.setText(currImage.getName());
        }else{
            holder.txtName.setText("Anonymous");
        }
        holder.txtCity.setText(currImage.getCity());
        holder.txtCount.setText(currImage.getContact());
        holder.txtType.setText(currImage.getBloodtype());

        if(!sharedpref.getString("COR_ID","").equals("")) {
            Picasso.with(context).load(currImage.getUser_photo()).fit().centerCrop().into(holder.imgDonor);
        }else{
            Picasso.with(context).load(R.drawable.default_user).fit().centerCrop().into(holder.imgDonor);
        }
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgDonor;
        public TextView txtName, txtCity, txtCount, txtType;

        public ViewHolder(final View itemView) {
            super(itemView);


            imgDonor = itemView.findViewById(R.id.imgDonor);
            txtName = itemView.findViewById(R.id.txtAssociation);
            txtCity = itemView.findViewById(R.id.spnCity);
            txtCount = itemView.findViewById(R.id.txtCount);
            txtType = itemView.findViewById(R.id.txtType);

            if(!sharedpref.getString("COR_ID","").equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        SearchData selectedDonor = searchList.get(position);

                        Intent intent = new Intent(context, DonorProfileActivity.class);
                        intent.putExtra("DONOR_ID", selectedDonor.getUser_id());
                        intent.putExtra("POST_ID", selectedDonor.getPost_id());
                        intent.putExtra("DONOR_NAME", selectedDonor.getName());
                        context.startActivity(intent);

                    }
                });
            }

            


        }
    }
}
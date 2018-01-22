package com.decypher.vesselsapp.Search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.decypher.vesselsapp.Coordinator.ConfirmDonorActivity;
import com.decypher.vesselsapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by trebd on 11/19/2017.
 */

public class SearchEventAdapterCoordinator extends RecyclerView.Adapter<SearchEventAdapterCoordinator.ViewHolder> {

    private ArrayList<SearchData> searchList;
    private Context context;

    public SearchEventAdapterCoordinator(Context context, ArrayList<SearchData> searchList) {
        this.context = context;
        this.searchList = searchList;
    }

    @Override
    public SearchEventAdapterCoordinator.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donor_list_item, parent, false);
        return new SearchEventAdapterCoordinator.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchEventAdapterCoordinator.ViewHolder holder, int position) {
        //SimpleDateFormat simpleDate =  new SimpleDateFormat("MMMM d, y");

        SearchData currImage = searchList.get(position);

        holder.txtName.setText(currImage.getName());
        holder.txtCity.setText(currImage.getCity());
        holder.txtCount.setText(currImage.getContact());
        holder.txtType.setText(currImage.getBloodtype());

        Picasso.with(context).load(currImage.getUser_photo()).fit().centerCrop().into(holder.imgDonor);
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    SearchData selectedDonor = searchList.get(position);

                    Intent intent = new Intent(context, ConfirmDonorActivity.class);
                    intent.putExtra("DONOR_ID", selectedDonor.getUser_id());
                    intent.putExtra("POST_ID", selectedDonor.getPost_id());
                    intent.putExtra("EVENT", "true");
                    context.startActivity(intent);

                }
            });




        }
    }
}
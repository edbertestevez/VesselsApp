package com.decypher.vesselsapp.Coordinator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.decypher.vesselsapp.Home.PostData;
import com.decypher.vesselsapp.MyPosts.ViewPost;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by trebd on 10/21/2017.
 */

public class HomeCoordinatorAdapter extends RecyclerView.Adapter<HomeCoordinatorAdapter.ViewHolder> {

    GlobalFunctions globalFunctions;
    private ArrayList<PostData> postList;
    private Context context;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    public HomeCoordinatorAdapter(Context context, ArrayList<PostData> postList) {
        this.context = context;
        this.postList = postList;
        sharedpref = context.getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(context);
    }

    @Override
    public HomeCoordinatorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_itemv2, parent, false);
        return new HomeCoordinatorAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(HomeCoordinatorAdapter.ViewHolder holder, int position) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("MMM d, y");

        PostData currImage = postList.get(position);

        if (sharedpref.getString("USERID", "").equals(currImage.getUser_id())) {
            holder.btnDonate.setText("Manage Post");
        } else {
            holder.btnDonate.setText("Donate");
        }

        holder.txtUsername.setText(currImage.getUser_name());
        holder.txtPostDate.setText(simpleDate.format(new Date(currImage.getDate())));
        holder.txtBloodtype.setText(currImage.getBloodtype());
        holder.txtReceiver.setText(currImage.getReceiver());
        holder.txtAddress.setText(currImage.getLocation());
        holder.txtDescription.setText(currImage.getDescription());
        holder.txtNeeded.setText(simpleDate.format(new Date(currImage.getDate_needed())));
        if (currImage.getBags().equals("1")) {
            holder.txtBags.setText(currImage.getBags() + " Bag");
        } else {
            holder.txtBags.setText(currImage.getBags() + " Bags");
        }
        Picasso.with(context).load(currImage.getPhoto()).fit().centerCrop().into(holder.imgPost);
        Picasso.with(context).load(currImage.getUser_photo()).into(holder.imgUser);
        holder.txtResponse.setText(currImage.getResponse_count());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgUser, imgPost;
        public TextView txtUsername, txtPostDate, txtBloodtype, txtReceiver, txtAddress, txtDescription, txtBags, txtNeeded,txtResponse;
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

            btnDonate.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final PostData selectedPost = postList.get(position);

                    Intent intent = new Intent(context, ViewPost.class);
                    intent.putExtra("POST_ID", selectedPost.getPost_id());
                    intent.putExtra("RECEIVER", selectedPost.getReceiver());
                    intent.putExtra("SELECTED_ID", selectedPost.getUser_id());
                    intent.putExtra("SELECTED_NAME", selectedPost.getUser_name());
                    intent.putExtra("COORDINATOR", true);
                    Log.i("POST ID => ", selectedPost.getPost_id());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });



        }
    }
}
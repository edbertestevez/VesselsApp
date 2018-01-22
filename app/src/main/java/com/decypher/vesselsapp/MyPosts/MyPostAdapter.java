package com.decypher.vesselsapp.MyPosts;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.decypher.vesselsapp.Home.PostData;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by trebd on 10/19/2017.
 */

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {

    private ArrayList<PostData> postList;
    private Context context;
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";


    public MyPostAdapter(Context context, ArrayList<PostData> postList) {
        this.context = context;
        this.postList = postList;

        mDatabase = FirebaseDatabase.getInstance();

        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
    }

    @Override
    public MyPostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_itemv2, parent, false);
        return new MyPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyPostAdapter.ViewHolder holder, int position) {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("MMMM d, y");

        PostData currImage = postList.get(position);

        holder.txtUsername.setText(currImage.getUser_name());
        holder.txtPostDate.setText(simpleDate.format(new Date(currImage.getDate())));
        holder.txtBloodtype.setText(currImage.getBloodtype());
        holder.txtReceiver.setText(currImage.getReceiver());
        holder.txtAddress.setText(currImage.getLocation());
        holder.txtDescription.setText(currImage.getDescription());
        if(currImage.getBags().equals("1")){
            holder.txtBags.setText(currImage.getBags() + " Bag");
        }else {
            holder.txtBags.setText(currImage.getBags() + " Bags");
        }
        holder.txtNeeded.setText(simpleDate.format(new Date(currImage.getDate_needed())));
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
            btnDonate = itemView.findViewById(R.id.btnDonate);
            txtNeeded = itemView.findViewById(R.id.txtNeeded);
            txtResponse = itemView.findViewById(R.id.txtResponse);

            btnDonate.setText("Manage Post");

            btnDonate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    PostData selectedPost = postList.get(position);

                    Intent intent = new Intent(context, ViewMyPostActivity.class);
                    intent.putExtra("POST_ID",selectedPost.getPost_id());
                    intent.putExtra("RECEIVER",selectedPost.getReceiver());
                    intent.putExtra("SELECTED_ID",selectedPost.getUser_id());
                    intent.putExtra("STATUS",selectedPost.getStatus());
                    Log.i("POST ID => ",selectedPost.getPost_id());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    PostData selectedPost = postList.get(position);

                    Intent intent = new Intent(context, ViewMyPostActivity.class);
                    intent.putExtra("POST_ID",selectedPost.getPost_id());
                    intent.putExtra("RECEIVER",selectedPost.getReceiver());
                    intent.putExtra("SELECTED_ID",selectedPost.getUser_id());
                    intent.putExtra("STATUS",selectedPost.getStatus());
                    Log.i("POST ID => ",selectedPost.getPost_id());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });



        }
    }
}
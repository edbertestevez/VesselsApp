package com.decypher.vesselsapp.Messages;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.decypher.vesselsapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by trebd on 10/28/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<ChatData> chatList;
    private Context context;
    /*private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
*/

    public ChatAdapter(Context context, ArrayList<ChatData> chatList) {
        this.context = context;
        this.chatList = chatList;

  /*      mDatabase = FirebaseDatabase.getInstance();

        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        */
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
        ChatData currChat = chatList.get(position);

        holder.txtName.setText(currChat.getName());
        Picasso.with(context).load(currChat.getImage()).into(holder.imgUser);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public ImageView imgUser;

        public ViewHolder(final View itemView) {
            super(itemView);


            txtName = itemView.findViewById(R.id.txtName);
            imgUser = itemView.findViewById(R.id.imgUser);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    ChatData selectedPost = chatList.get(position);

                    Intent intent = new Intent(context, SelectedChatActivity.class);
                    intent.putExtra("CHAT_ID",selectedPost.getChat_id());
                    intent.putExtra("RECIPIENT_ID",selectedPost.getRecipient());
                    intent.putExtra("RECIPIENT_NAME",selectedPost.getName());
                    intent.putExtra("RECIPIENT_PHOTO",selectedPost.getImage());

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });



        }
    }
}
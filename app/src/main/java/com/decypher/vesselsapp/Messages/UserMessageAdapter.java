package com.decypher.vesselsapp.Messages;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.decypher.vesselsapp.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by trebd on 11/9/2017.
 */

public class UserMessageAdapter extends RecyclerView.Adapter<UserMessageAdapter.ViewHolder> {

    private ArrayList<MessageData> chatList;
    private Context context;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    String nowUser;
    /*private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
*/

    public UserMessageAdapter(Context context, ArrayList<MessageData> chatList) {
        this.context = context;
        this.chatList = chatList;
        sharedpref = context.getSharedPreferences(SHAREDPREF, MODE_PRIVATE);


  /*      mDatabase = FirebaseDatabase.getInstance();

        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        */
    }

    @Override
    public UserMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
        return new UserMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserMessageAdapter.ViewHolder holder, int position) {
        MessageData currChat = chatList.get(position);

        if(currChat.getSender().equals(sharedpref.getString("USERID",""))) {
            holder.cardReceiver.setVisibility(View.GONE);
            holder.cardSender.setVisibility(View.VISIBLE);
            holder.txtSendMessage.setText(currChat.getMessage());
            holder.txtReceiveMessage.setText(currChat.getMessage());
        }else{
            holder.cardSender.setVisibility(View.GONE);
            holder.cardReceiver.setVisibility(View.VISIBLE);

            holder.txtSendMessage.setText(currChat.getMessage());
            holder.txtReceiveMessage.setText(currChat.getMessage());
        }

        /*holder.txtSendMessage.setText(currChat.getName());
        Picasso.with(context).load(currChat.getImage()).into(holder.imgUser);*/
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtSendMessage, txtReceiveMessage;
        public CardView cardReceiver, cardSender;

        public ViewHolder(final View itemView) {
            super(itemView);


            txtSendMessage = itemView.findViewById(R.id.txtSendMessage);
            txtReceiveMessage = itemView.findViewById(R.id.txtReceiveMessage);
            cardReceiver = itemView.findViewById(R.id.cardReciever);
            cardSender = itemView.findViewById(R.id.cardSender);

        }
    }
}
package com.decypher.vesselsapp.Messages;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.MyPosts.ViewPost;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by trebd on 11/9/2017.
 */

public class UserSelectedChatActivity extends AppCompatActivity {

    String chat_id, recipient_id, recipient_name;
    RecyclerView recyclerChat;
    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference chatReference, directoryReference, userReference;
    String CHAT_REFERENCE = "chats";
    String DIRECTORY_REFERENCE = "chat_directory";
    String USER_REFERENCE = "users";
    String message;
    GlobalFunctions globalFunctions;

    String post_receiver="";

    String lastChat;

    String newMessage;
    String newValue="";

    ArrayList<MessageData> messageList;
    UserMessageAdapter adapter;
    MessageData messageValue;

    LinearLayoutManager layoutManager;

    EditText etMessage;
    Button btnSend;

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_chat);

        Bundle extras = getIntent().getExtras();
        //chat_id = extras.getString("CHAT_ID");
        globalFunctions = new GlobalFunctions(getApplicationContext());
        progDialog = new ProgressDialog(UserSelectedChatActivity.this);
        progDialog.setMessage("Loading messages. .");

        if(getIntent().hasExtra("RECEIVER")){
            post_receiver = extras.getString("RECEIVER");
        }

        //DEFAULT RECIPIENT ID
        recipient_id = "FPBEH7EyHrMkjoAoOFGxWCbSS1Y2";

        recipient_name = "Negros First Provincial Blood Center";

        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(recipient_name);

        recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
        recyclerChat.setHasFixedSize(true);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageList = new ArrayList<MessageData>();
        adapter = new UserMessageAdapter(UserSelectedChatActivity.this, messageList);
        recyclerChat.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);

        //recyclerChat.setStac(true);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        chatReference = mDatabase.getReference(CHAT_REFERENCE);
        directoryReference = mDatabase.getReference(DIRECTORY_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = etMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(UserSelectedChatActivity.this, "Unable to send. Message is empty", Toast.LENGTH_SHORT).show();
                }else{
                    etMessage.setText("");
                    String key = chatReference.child(chat_id).push().getKey();

                    int i = (int) (new Date().getTime()/1000);

                    chatReference.child(chat_id).child(key).child("sender").setValue(sharedpref.getString("USERID",""));

                    if(!post_receiver.equals("")) {
                        chatReference.child(chat_id).child(key).child("message").setValue("Patient: "+post_receiver+"\n\n"+message);
                    }else{
                        chatReference.child(chat_id).child(key).child("message").setValue(message);
                    }
                    chatReference.child(chat_id).child(key).child("receiver").setValue(recipient_id);
                    chatReference.child(chat_id).child(key).child("time").setValue(i);


                    //change status to UNREAD
                    directoryReference.child(recipient_id).child(sharedpref.getString("USERID","")).child("status").setValue("0");
                }

            }
        });

        if(globalFunctions.isNetworkAvailable()) {
            progDialog.show();
            checkChat();
        }else{
            Toast.makeText(this, "Can't load messages. No internet connection", Toast.LENGTH_SHORT).show();
            progDialog.dismiss();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMessages(){
        //get data
        chatReference.child(chat_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    newMessage = "";
                    messageValue = dataSnapshot.getValue(MessageData.class);

                    MessageData data = new MessageData();
                    data.setChat_id(chat_id);

                    if (!messageValue.getSender().equals("") && dataSnapshot.hasChild("time")) {
                        data.setMessage(messageValue.getMessage());
                        data.setSender(messageValue.getSender());
                        data.setReceiver(messageValue.getReceiver());
                        data.setTime(messageValue.getTime());

                            messageList.add(data);
                            adapter.notifyDataSetChanged();

                            newMessage = "";
                    } else {
                        loadNewMessage(dataSnapshot.getKey());
                    }

                    recyclerChat.setLayoutManager(layoutManager);
                }
                progDialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        progDialog.dismiss();

    }

    private void loadNewMessage(final String new_id){
        chatReference.child(chat_id).child(new_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("receiver") && !dataSnapshot.child("message").getValue().toString().equals("")) {
                        messageValue = dataSnapshot.getValue(MessageData.class);
                        MessageData data = new MessageData();

                            data.setMessage(messageValue.getMessage());
                            data.setSender(messageValue.getSender());
                            data.setReceiver(messageValue.getReceiver());
                            data.setTime(messageValue.getTime());


                                messageList.add(data);
                                adapter.notifyDataSetChanged();

                            newValue = "1";
                        }else {
                        loadNewMessage(new_id);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    public void checkChat(){
        directoryReference.child(recipient_id).child(sharedpref.getString("USERID","")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chat_id = dataSnapshot.child("chat_id").getValue().toString();
                    loadMessages();
                }else{
                    final String key = chatReference.push().getKey();
                    chat_id = key;
                    directoryReference.child(recipient_id).child(sharedpref.getString("USERID","")).child("chat_id").setValue(key, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            final String new_key = chatReference.child(key).push().getKey();
                            directoryReference.child(recipient_id).child(sharedpref.getString("USERID","")).child("status").setValue("1");
                            chatReference.child(key).child(new_key).child("sender").setValue("");
                            chatReference.child(key).child(new_key).child("message").setValue("");
                            chatReference.child(key).child(new_key).child("receiver").setValue("");
                            chatReference.child(key).child(new_key).child("time").setValue(123, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    loadMessages();
                                }
                            });


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

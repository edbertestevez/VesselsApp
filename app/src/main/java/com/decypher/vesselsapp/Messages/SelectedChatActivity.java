package com.decypher.vesselsapp.Messages;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class SelectedChatActivity extends AppCompatActivity {

    String chat_id, recipient_id, recipient_name;
    RecyclerView recyclerChat;
    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference chatReference, directoryReference, userReference;
    String CHAT_REFERENCE = "chats";
    String DIRECTORY_REFERENCE = "chat_directory";
    String USER_REFERENCE = "users";
    String message;
    String lastChat;

    String newMessage;
    String xVal;

    ArrayList<MessageData> messageList;
    MessageAdapter adapter;
    MessageData messageValue;

    LinearLayoutManager layoutManager;

    EditText etMessage;
    Button btnSend;

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    ProgressDialog progDialog;

    GlobalFunctions globalFunctions;

    String bank_id = "FPBEH7EyHrMkjoAoOFGxWCbSS1Y2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_chat);

        Bundle extras = getIntent().getExtras();
        chat_id = extras.getString("CHAT_ID");
        recipient_id = extras.getString("RECIPIENT_ID");
        recipient_name = extras.getString("RECIPIENT_NAME");

        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());

        progDialog = new ProgressDialog(SelectedChatActivity.this);
        progDialog.setMessage("Loading messages. .");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(recipient_name);

        recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
        recyclerChat.setHasFixedSize(true);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageList = new ArrayList<MessageData>();
        adapter = new MessageAdapter(SelectedChatActivity.this, messageList);
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
                    Toast.makeText(SelectedChatActivity.this, "Unable to send. Message is empty", Toast.LENGTH_SHORT).show();
                }else{
                    etMessage.setText("");
                    String key = chatReference.child(chat_id).push().getKey();

                    int i = (int) (new Date().getTime()/1000);

                    chatReference.child(chat_id).child(key).child("sender").setValue(sharedpref.getString("COR_ID",""));
                    chatReference.child(chat_id).child(key).child("message").setValue(message);
                    chatReference.child(chat_id).child(key).child("receiver").setValue(recipient_id);
                    chatReference.child(chat_id).child(key).child("time").setValue(i);

                }

            }
        });

        if(globalFunctions.isNetworkAvailable()) {
            progDialog.show();
            loadMessages();
        }else{
            Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
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
        messageList.clear();
        chatReference.child(chat_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                newMessage="";
                messageValue = dataSnapshot.getValue(MessageData.class);

                MessageData data = new MessageData();
                data.setChat_id(chat_id);

                //SET TO SEEN
                directoryReference.child(bank_id).child(recipient_id).child("status").setValue("1");


                if(!messageValue.getSender().equals("") && dataSnapshot.hasChild("time")){
                    data.setMessage(messageValue.getMessage());
                    data.setSender(messageValue.getSender());
                    data.setReceiver(messageValue.getReceiver());
                    data.setTime(messageValue.getTime());
                    messageList.add(data);
                    adapter.notifyDataSetChanged();
                    newMessage="";

                }else{
                    loadNewMessage(dataSnapshot.getKey());
                }
                progDialog.dismiss();
                recyclerChat.setLayoutManager(layoutManager);
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
    }

    private void loadNewMessage(final String new_id){
        chatReference.child(chat_id).child(new_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageValue = dataSnapshot.getValue(MessageData.class);
                MessageData data = new MessageData();
                if(dataSnapshot.hasChild("receiver")&& !dataSnapshot.child("message").getValue().toString().equals("")) {
                    messageValue = dataSnapshot.getValue(MessageData.class);

                    //SET TO SEEN
                    directoryReference.child(bank_id).child(recipient_id).child("status").setValue("1");

                    data.setMessage(messageValue.getMessage());
                    data.setSender(messageValue.getSender());
                    data.setReceiver(messageValue.getReceiver());
                    data.setTime(messageValue.getTime());

                    messageList.add(data);
                    adapter.notifyDataSetChanged();
                }else {
                    loadNewMessage(new_id);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

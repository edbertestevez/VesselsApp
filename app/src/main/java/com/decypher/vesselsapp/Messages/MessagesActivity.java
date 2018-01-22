package com.decypher.vesselsapp.Messages;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.MyPosts.EditPostActivity;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesActivity extends AppCompatActivity {

    RecyclerView recyclerChat, recyclerUnread;
    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference chatReference, directoryReference, userReference;
    String CHAT_REFERENCE = "chats";
    String DIRECTORY_REFERENCE = "chat_directory";
    String USER_REFERENCE = "users";

    String searchVal="";
    ArrayList<ChatData> chatList, unreadList;
    ChatAdapter adapter, adapterUnread;
    ChatData chatValue;

    ProgressDialog progDialog;
    GlobalFunctions globalFunctions;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    TextView txtUnread, txtRecent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Messages");

        progDialog = new ProgressDialog(MessagesActivity.this);
        progDialog.setMessage("Loading message list. .");

        txtUnread = (TextView) findViewById(R.id.txtUnread);
        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());
        recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
        recyclerChat.setHasFixedSize(true);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatList = new ArrayList<ChatData>();
        adapter = new ChatAdapter(MessagesActivity.this, chatList);
        recyclerChat.setAdapter(adapter);

        txtRecent = (TextView) findViewById(R.id.txtRecent);

        recyclerUnread = (RecyclerView) findViewById(R.id.recyclerUnread);
        recyclerUnread.setHasFixedSize(true);
        recyclerUnread.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        unreadList = new ArrayList<ChatData>();
        adapterUnread = new ChatAdapter(MessagesActivity.this, unreadList);
        recyclerUnread.setAdapter(adapterUnread);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        chatReference = mDatabase.getReference(CHAT_REFERENCE);
        directoryReference = mDatabase.getReference(DIRECTORY_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.menuSearch).getActionView();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //loadSearchDonors(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // do your search on change or save the last string in search
                searchVal = s;

                loadChats();
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtUnread.setVisibility(View.GONE);

        if(globalFunctions.isNetworkAvailable()) {
            loadChats();
        }else{
            progDialog.show();
            Toast.makeText(this, "No internet connection found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAdapter(final String chat_id, final String recipient_id){

        userReference.child(recipient_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("user_photo").getValue().toString();
                if(!searchVal.equals("")){

                    if(name.toLowerCase().indexOf(searchVal.toLowerCase()) != -1){
                        chatList.add(new ChatData(chat_id, recipient_id, name, image));
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    chatList.add(new ChatData(chat_id, recipient_id, name, image));
                    adapter.notifyDataSetChanged();
                }
                progDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadUnread(final String chat_id, final String recipient_id){

        userReference.child(recipient_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("user_photo").getValue().toString();
                if(!searchVal.equals("")){
                    //boolean isFound = name.toLowerCase().contains(searchVal.toLowerCase());
                    if(name.toLowerCase().indexOf(searchVal.toLowerCase()) != -1){
                        unreadList.add(new ChatData(chat_id, recipient_id, name, image));
                        adapterUnread.notifyDataSetChanged();

                        if(!searchVal.equals("")) {
                            txtUnread.setVisibility(View.GONE);
                        }else{
                            txtUnread.setVisibility(View.VISIBLE);
                        }
                    }
                }else {
                    unreadList.add(new ChatData(chat_id, recipient_id, name, image));
                    adapterUnread.notifyDataSetChanged();
                    adapterUnread.notifyDataSetChanged();
                    txtUnread.setVisibility(View.VISIBLE);
                }

                progDialog.dismiss();

                adapterUnread.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                adapterUnread.notifyDataSetChanged();
            }
        });
    }

    private void loadChats(){
        if(!searchVal.equals("")){
            txtRecent.setVisibility(View.GONE);
        }else{
            txtRecent.setVisibility(View.VISIBLE);
        }
        chatList.clear();
        unreadList.clear();

        directoryReference.child(sharedpref.getString("COR_ID","")).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    String chat_id = dataSnapshot.child("chat_id").getValue().toString();
                    String recipient_id = dataSnapshot.getKey();
                    String chat_status = dataSnapshot.child("status").getValue().toString();
                    if(chat_status.equals("0")){
                        loadUnread(chat_id, recipient_id);
                    }else {
                        loadAdapter(chat_id, recipient_id);
                    }
                }
                progDialog.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static boolean isContain(String source, String subItem){
        String pattern = "\\b"+subItem+"\\b";
        Pattern p=Pattern.compile(pattern);
        Matcher m=p.matcher(source);
        return m.find();
    }
}

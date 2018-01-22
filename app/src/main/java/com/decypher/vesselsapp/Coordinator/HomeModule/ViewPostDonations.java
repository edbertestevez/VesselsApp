package com.decypher.vesselsapp.Coordinator.HomeModule;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.decypher.vesselsapp.Search.SearchAdapter;
import com.decypher.vesselsapp.Search.SearchData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewPostDonations extends AppCompatActivity {

    RecyclerView recyclerview;
    ArrayList<SearchData> donorList;
    SearchAdapter adapter;
    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";
    String receiver;

    String searchVal;

    int count = 0;

    TextView txtTitle;
    GlobalFunctions globalFunctions;
    String DONOR_REFERENCE = "posts_donors";
    String USER_REFERENCE = "users";
    String POST_REFERENCE = "posts";

    TextView txtCount;

    String post_id, selected_id;

    SearchData searchValue;
    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference donorReference, userReference, postReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_respondents);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        donorList = new ArrayList<SearchData>();
        adapter = new SearchAdapter(ViewPostDonations.this, donorList);
        recyclerview.setAdapter(adapter);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        receiver = extras.getString("RECEIVER");
        post_id = extras.getString("POST_ID");
        selected_id = extras.getString("SELECTED_ID");
        txtTitle.setText("Recipient: " + receiver);

        setTitle("Donors List");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtCount = (TextView) findViewById(R.id.txtCount);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        donorReference = mDatabase.getReference(DONOR_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        postReference = mDatabase.getReference(POST_REFERENCE);

        //load list of respondents
        loadRespondents();

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
                loadSearchRespondents(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // do your search on change or save the last string in search
                searchVal = s;

                loadSearchRespondents(s);
                return false;
            }
        });

        return true;
    }

    private void loadSearchRespondents(final String searchVal){
        count=0;
        donorList.clear();
        donorReference.orderByKey().equalTo(post_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if(dataSnapshot1.getValue().equals(true)){
                        loadSearchDonorList(dataSnapshot1.getKey(), true, searchVal);
                        count++;
                        txtCount.setText("Confirmed Donors: "+String.valueOf(count));
                    }
                }
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

    private void loadSearchDonorList(final String key, final Boolean type, final String searchVal){
        userReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    searchValue = dataSnapshot.getValue(SearchData.class);
                    SearchData info = new SearchData();

                    String user_id = key;
                    String name = searchValue.getName();
                    String city = searchValue.getCity();
                    String bloodtype = searchValue.getBloodtype();
                    String donation_count = searchValue.getDonation_count();
                    String user_photo = searchValue.getUser_photo();
                    String contact = searchValue.getGender();


                    info.setUser_id(user_id);
                    info.setName(name);
                    info.setCity(city);
                    info.setBloodtype(bloodtype);
                    info.setDonation_count(donation_count);
                    info.setUser_photo(user_photo);
                    info.setContact(contact);
                    info.setPost_id(post_id);


                    boolean isFound = name.toLowerCase().contains(searchVal.toLowerCase());
                    if(isFound) {
                        if (type.equals(true)) {
                            donorList.add(info);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //old

    private void loadRespondents(){
        donorList.clear();
        donorReference.orderByKey().equalTo(post_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if(dataSnapshot1.getValue().equals(true)){
                        loadDonorList(dataSnapshot1.getKey(), true);
                        count++;
                        txtCount.setText("Confirmed Donors: "+String.valueOf(count));
                    }
                }
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

    private void loadDonorList(final String key, final Boolean type){
        userReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    searchValue = dataSnapshot.getValue(SearchData.class);
                    SearchData info = new SearchData();

                    String user_id = key;
                    String name = searchValue.getName();
                    String city = searchValue.getCity();
                    String bloodtype = searchValue.getBloodtype();
                    String donation_count = searchValue.getDonation_count();
                    String user_photo = searchValue.getUser_photo();
                    String contact = searchValue.getGender();


                    info.setUser_id(user_id);
                    info.setName(name);
                    info.setCity(city);
                    info.setBloodtype(bloodtype);
                    info.setDonation_count(donation_count);
                    info.setUser_photo(user_photo);
                    info.setContact(contact);
                    info.setPost_id(post_id);

                    if(type.equals(true)) {

                        donorList.add(info);
                        adapter.notifyDataSetChanged();
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

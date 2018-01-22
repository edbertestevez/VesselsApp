package com.decypher.vesselsapp.Home;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.decypher.vesselsapp.MainActivity;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ArrayList<PostData> postList;
    View view;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    PostAdapter adapter;
    PostData value;
    int ctr_count;

    String response_count="0";
    String contact;
    String user_id_post;
    String user_name_post;
    String user_photo_post;

    ArrayList<String> usernameList = new ArrayList<String>();
    ArrayList<String> userphotoList = new ArrayList<String>();
    ArrayList<String> listPost = new ArrayList<String>();

    String username;


    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, homeReference, userReference, donorReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String DONOR_REFERENCE = "posts_donors";
    String HOME_REFERENCE = "posts_record";

    //PROGRESS
    ProgressBar progress;
    TextView txtProgress;

    Toolbar mToolbar;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //SET TITLE
        ((MainActivity)getActivity()).setToolbarTitle("Home");

        progress = view.findViewById(R.id.progress);
        txtProgress = view.findViewById(R.id.txtProgress);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getActivity().setTitle("Home");

        mDatabase = FirebaseDatabase.getInstance();

        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        donorReference = mDatabase.getReference(DONOR_REFERENCE);
        homeReference = mDatabase.getReference(HOME_REFERENCE);

        postList = new ArrayList<PostData>();
        postList.clear();
        adapter = new PostAdapter(getActivity().getApplicationContext(), postList);
        recyclerView.setAdapter(adapter);



        return view;

    }

    @Override
    public void onResume() {

        super.onResume();

        postList.clear();
        displayPosts();
    }

    private void displayPosts() {
       postList.clear();
        adapter.clear();
        homeReference.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    HomeData val = childSnapshot.getValue(HomeData.class);

                    if(childSnapshot.child("status").getValue().equals("0")) {
                        loadPostInfo(val.getUser_id(), childSnapshot.getKey());
                    }
                    progress.setVisibility(View.GONE);
                    txtProgress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        homeReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                displayPosts();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                displayPosts();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void loadPostInfo(final String parent, final String key){
        postReference.child(parent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    PosterData poster = dataSnapshot.getValue(PosterData.class);
                    user_id_post = dataSnapshot.getKey();
                    user_name_post = poster.getUser_name();
                    user_photo_post = poster.getUser_photo();
                    contact = poster.getContact();

                    loadActualPost(parent, key, dataSnapshot.getKey(),poster.getUser_name(),poster.getUser_photo(),poster.getContact());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadActualPost(String parent, String key, final String u_id, final String u_name, final String u_photo, final String u_contact){
        postReference.child(parent).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               value = dataSnapshot.getValue(PostData.class);

                PostData info = new PostData();
                String post_id = dataSnapshot.getKey();

                String bloodtype = value.getBloodtype();
                String user_id = u_id;
                String description = value.getDescription();
                String location = value.getLocation();
                String photo = value.getPhoto();
                String user_photo = u_photo;
                String receiver = value.getReceiver();
                String status = value.getStatus();
                String city = value.getCity();
                String bags = value.getBags();
                String date = value.getDate();
                String date_needed = value.getDate_needed();
                String user_name = u_name;

                //set data to adapter
                info.setPost_id(post_id);
                info.setBloodtype(bloodtype);
                info.setUser_name(user_name);
                info.setUser_id(user_id);
                info.setDescription(description);
                info.setLocation(location);
                info.setPhoto(photo);
                info.setUser_photo(user_photo);
                info.setReceiver(receiver);
                info.setStatus(status);
                info.setCity(city);
                info.setBags(bags);
                info.setDate(date);
                info.setUser_id(user_id);
                info.setDate_needed(date_needed);
                info.setContact(u_contact);
                info.setResponse_count(value.getResponse_count());

                postList.add(info);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
package com.decypher.vesselsapp.Coordinator;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.decypher.vesselsapp.Home.HomeData;
import com.decypher.vesselsapp.Home.PostData;
import com.decypher.vesselsapp.Home.PosterData;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeCoordinatorFragment extends Fragment {

    ArrayList<PostData> postList;
    View view;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    HomeCoordinatorAdapter adapter;
    PostData value;
    int ctr_count;

    String user_id_post;
    String user_name_post;
    String user_photo_post, contact;

    ArrayList<String> usernameList = new ArrayList<String>();
    ArrayList<String> userphotoList = new ArrayList<String>();
    ArrayList<String> listPost = new ArrayList<String>();

    String username;

    //PROGRESS
    ProgressBar progress;
    TextView txtProgress;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, homeReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String HOME_REFERENCE = "posts_record";

    public HomeCoordinatorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        progress = view.findViewById(R.id.progress);
        txtProgress = view.findViewById(R.id.txtProgress);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getActivity().setTitle("Home");

        mDatabase = FirebaseDatabase.getInstance();

        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        homeReference = mDatabase.getReference(HOME_REFERENCE);

        postList = new ArrayList<PostData>();
        adapter = new HomeCoordinatorAdapter(getActivity().getApplicationContext(), postList);
        recyclerView.setAdapter(adapter);

        displayPosts();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void displayPosts() {
        postList.clear();
        homeReference.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    HomeData val = childSnapshot.getValue(HomeData.class);
                    loadPostInfo(val.getUser_id(), childSnapshot.getKey());

                    progress.setVisibility(View.GONE);
                    txtProgress.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void loadPostInfo(final String parent, final String key){
        postReference.child(parent).addValueEventListener(new ValueEventListener() {
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
        postReference.child(parent).child(key).addValueEventListener(new ValueEventListener() {
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
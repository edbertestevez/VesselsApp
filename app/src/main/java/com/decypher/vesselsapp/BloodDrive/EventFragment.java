package com.decypher.vesselsapp.BloodDrive;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {

    ArrayList<BloodDriveData> driveList;
    View view;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    EventAdapter adapter;
    BloodDriveData value;
    int ctr_count;

    String user_id_post;
    String user_name_post;
    String user_photo_post;

    ArrayList<String> usernameList = new ArrayList<String>();
    ArrayList<String> userphotoList = new ArrayList<String>();
    ArrayList<String> listPost = new ArrayList<String>();

    GlobalFunctions globalFunctions;

    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";

    String username;


    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, corReference, eventsReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String COR_REFERENCE = "coordinators";
    String EVENTS_REFERENCE = "events";

    //PROGRESS
    ProgressBar progress;
    TextView txtProgress;


    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Blood Drives");

        progress = view.findViewById(R.id.progress);
        txtProgress = view.findViewById(R.id.txtProgress);
        txtProgress.setText("Loading blood drives. . .");

        //FIREBASE
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        corReference = mDatabase.getReference(COR_REFERENCE);
        eventsReference = mDatabase.getReference(EVENTS_REFERENCE);

        globalFunctions = new GlobalFunctions(getContext());
        sharedpref = getActivity().getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        driveList = new ArrayList<BloodDriveData>();
        adapter = new EventAdapter(getActivity().getApplicationContext(), driveList);
        recyclerView.setAdapter(adapter);

        displayDrives();

        return view;
    }

    private void displayDrives() {
        eventsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String cor_association = dataSnapshot.child("association").getValue().toString();
                String cor_contact = dataSnapshot.child("contact").getValue().toString();
                String cor_photo = dataSnapshot.child("cor_photo").getValue().toString();
                for ( DataSnapshot postDataSnapshot : dataSnapshot.getChildren() ) {
                    if(postDataSnapshot.hasChildren()) {
                        //get data of specific doc
                        value = postDataSnapshot.getValue(BloodDriveData.class);

                        //insert to variables the values
                        BloodDriveData info = new BloodDriveData();
                        String drive_id = postDataSnapshot.getKey();
                        String address = value.getAddress();
                        String date = value.getDate();
                        String description = value.getDescription();
                        String name = value.getName();
                        String photo = value.getPhoto();
                        String status = value.getStatus();
                        String time_end = value.getTime_end();
                        String time_start = value.getTime_start();


                        //set data to adapter
                        info.setDrive_id(drive_id);
                        info.setAddress(address);
                        info.setDate(date);
                        info.setDescription(description);
                        info.setName(name);
                        info.setPhoto(photo);
                        info.setStatus(status);
                        info.setTime_end(time_end);
                        info.setTime_start(time_start);

                        info.setAssociation(cor_association);
                        info.setContact(cor_contact);
                        info.setCor_photo(cor_photo);

                        driveList.add(info);
                        adapter.notifyDataSetChanged();
                    }
                }
                progress.setVisibility(View.GONE);
                txtProgress.setVisibility(View.GONE);
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

}

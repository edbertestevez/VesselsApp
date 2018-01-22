package com.decypher.vesselsapp.MyPosts;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.Home.PostData;
import com.decypher.vesselsapp.Home.PosterData;
import com.decypher.vesselsapp.Home.Users;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.Profile.DonationData;
import com.decypher.vesselsapp.Profile.DonationListAdapter;
import com.decypher.vesselsapp.Profile.UserProfileActivity;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPostsFragment extends Fragment {

    long allowedDate, dateNow;

    ArrayList<PostData> postList;
    ArrayList<DonationData> donationList;
    View view;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    MyPostAdapter adapter;
    DonationListAdapter donationAdapter;

    PostData value;
    Users userValue;
    DonationData donationValue;

    String user_id_post;
    String user_name_post;
    String user_photo_post;
    String last_donated;

    String assoc_id, assoc_name, assoc_photo, assoc_date;

    TextView txtName, txtProfile, txtEmail, txtContact, txtAddress, txtCity, txtBirthdate,txtGender, txtType,txtCount, txtPostCount;
    TextView txtDateDonated, txtStatus, txtCount1, txtPostCount1, txtTabTitle;
    ImageView imgUser;
    ImageButton imgEdit;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference, donationReference, corReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";
    String DONATION_REFERENCE = "users_donation";
    String COR_REFERENCE = "coordinators";

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;
    GlobalFunctions globalFunctions;

    ProgressDialog progDialog;

    public String name;
    public String email;
    public String password;
    public String gender;
    public String bloodtype;
    public String birthdate;
    public String city;
    public String address;
    public String contact;
    public String donation_count;
    public String user_photo;
    public String user_id;

    TextView txtProgress;
    ProgressBar progress;

    public MyPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_posts, container, false);

        progDialog = new ProgressDialog(getActivity());
        progDialog.setMessage("Loading profile. .");

        txtName = (TextView) view.findViewById(R.id.txtAssociation);
        txtEmail = (TextView) view.findViewById(R.id.txtName);
        txtContact = (TextView) view.findViewById(R.id.txtContactOrg);
        txtAddress = (TextView) view.findViewById(R.id.etAddress);
        //txtCity = (TextView) view.findViewById(R.id.txtCity);
        txtBirthdate = (TextView) view.findViewById(R.id.txtBirthdate);
        txtGender = (TextView) view.findViewById(R.id.txtGender);
        txtType = (TextView) view.findViewById(R.id.txtType);
        txtCount = (TextView) view.findViewById(R.id.txtCount);
        txtPostCount = (TextView) view.findViewById(R.id.txtPostCount);
        txtCount1 = (TextView) view.findViewById(R.id.txtCount1);
        txtPostCount1 = (TextView) view.findViewById(R.id.txtPostCount1);
        txtDateDonated = (TextView) view.findViewById(R.id.txtDateDonated);
        txtStatus = (TextView) view.findViewById(R.id.txtStatus);
        txtTabTitle = (TextView) view.findViewById(R.id.txtTabTitle);

        imgUser = (ImageView) view.findViewById(R.id.imgUser);

        progress = view.findViewById(R.id.progress);
        txtProgress = view.findViewById(R.id.txtProgress);
        txtProgress.setText("Loading Profile");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mDatabase = FirebaseDatabase.getInstance();

        sharedpref = getActivity().getSharedPreferences(SHAREDPREF, MODE_PRIVATE);
        globalFunctions = new GlobalFunctions(getActivity().getApplicationContext());

        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);
        donationReference = mDatabase.getReference(DONATION_REFERENCE);
        corReference = mDatabase.getReference(COR_REFERENCE);

        postList = new ArrayList<PostData>();
        adapter = new MyPostAdapter(getActivity().getApplicationContext(), postList);


        donationList = new ArrayList<DonationData>();
        donationAdapter = new DonationListAdapter(getActivity().getApplicationContext(), donationList);


        loadProfile();
        //default
        getPostCount();
        //displayMyPosts();

        txtCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDonationList();
            }
        });
        txtCount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDonationList();
            }
        });

        txtPostCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMyPosts();
            }
        });

        txtPostCount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMyPosts();
            }
        });
        return view;

    }

    private void getPostCount(){
        postReference.child(sharedpref.getString("USERID","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // -3 kay may names
                if (dataSnapshot.getChildrenCount() == 0) {
                    txtPostCount.setText("0");
                } else {
                    txtPostCount.setText(String.valueOf(dataSnapshot.getChildrenCount() - 3));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void displayMyPosts() {
        recyclerView.setAdapter(adapter);
        postList.clear();
        txtTabTitle.setText("My Posts");
        postReference.child(sharedpref.getString("USERID","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // -3 kay may names
                if (dataSnapshot.getChildrenCount() == 0) {
                    txtPostCount.setText("0");
                } else {
                    txtPostCount.setText(String.valueOf(dataSnapshot.getChildrenCount() - 3));
                }
                postList.clear();
                if(dataSnapshot.exists()){
                if(dataSnapshot.hasChild("user_name")) {
                    PosterData poster = dataSnapshot.getValue(PosterData.class);
                    user_id_post = dataSnapshot.getKey();
                    user_name_post = poster.getUser_name();
                    user_photo_post = poster.getUser_photo();
                }
                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()) {
                    //get data of specific doc

                    if (dataSnapshot1.hasChildren()) {
                        value = dataSnapshot1.getValue(PostData.class);

                        //insert to variables the values
                        PostData info = new PostData();
                        String post_id = dataSnapshot1.getKey();
                        String bloodtype = value.getBloodtype();
                        String user_id = user_id_post;
                        String description = value.getDescription();
                        String location = value.getLocation();
                        String photo = value.getPhoto();
                        String user_photo = user_photo_post;
                        String receiver = value.getReceiver();
                        String status = value.getStatus();
                        String city = value.getCity();
                        String bags = value.getBags();
                        String date = value.getDate();
                        String date_needed = value.getDate_needed();
                        String user_name = user_name_post;

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
                        info.setDate_needed(date_needed);
                        info.setResponse_count(value.getResponse_count());

                        postList.add(info);
                        adapter.notifyDataSetChanged();
                    }
                }
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        postReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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

    private void loadProfile() {

        userReference.child(sharedpref.getString("USERID","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userValue = dataSnapshot.getValue(Users.class);

                    SimpleDateFormat simpleDate =  new SimpleDateFormat("MMMM d, y");
                    SimpleDateFormat checkDate =  new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    Calendar now = Calendar.getInstance();

                    user_photo = userValue.getUser_photo();
                    email = userValue.getEmail();
                    gender = userValue.getGender();
                    birthdate = userValue.getBirthdate();
                    address = userValue.getAddress();
                    contact = userValue.getContact();
                    name = userValue.getName();
                    city = userValue.getCity();
                    bloodtype = userValue.getBloodtype();
                    donation_count = userValue.getDonation_count();
                    last_donated = userValue.getLast_donated();

                    Picasso.with(getActivity()).load(user_photo).fit().centerCrop().into(imgUser);
                    txtEmail.setText(email);
                    txtGender.setText(gender);
                    txtBirthdate.setText(simpleDate.format(new Date(birthdate)));
                    txtAddress.setText(address);
                    txtContact.setText(contact);
                    txtName.setText(name);
//                    txtCity.setText(city);
                    txtType.setText(bloodtype);
                    txtCount.setText(donation_count);
                    if(!last_donated.equals("")) {
                        txtDateDonated.setText(simpleDate.format(new Date(last_donated)));
                        String checkDateString = checkDate.format(new Date(last_donated));
                        try {
                            c.setTime(checkDate.parse(checkDateString));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.MONTH, 3);
                        allowedDate = c.getTimeInMillis();
                        dateNow = now.getTimeInMillis();

                        if(allowedDate<=dateNow){
                            txtStatus.setText("Available for Donation");
                        }else{
                            int days = (int)(((allowedDate-dateNow)/(1000*60*60*24)));
                            txtStatus.setText("Not Available ( "+String.valueOf(days)+" days remaining )");
                        }
                    }else{
                        txtDateDonated.setText("No donation made");

                        txtStatus.setText("Available for Donation");
                    }
                }
                loadDonationList();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadDonationList(){
        donationList.clear();
        txtTabTitle.setText("My Donations");
        recyclerView.setAdapter(donationAdapter);

        donationReference.child(sharedpref.getString("USERID","")).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                assoc_date = dataSnapshot.child("date").getValue().toString();
                assoc_id = dataSnapshot.child("association").getValue().toString();
                getDataDonation(assoc_id, assoc_date);

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

        progress.setVisibility(View.GONE);
        txtProgress.setVisibility(View.GONE);
    }

    public void getDataDonation(String assoc_id, final String date_donation){
        corReference.child(assoc_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DonationData info = new DonationData();
                info.setAssociation(dataSnapshot.child("association").getValue().toString());
                info.setAssociaton_photo(dataSnapshot.child("photo").getValue().toString());
                info.setDate(date_donation);

                donationList.add(info);
                donationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

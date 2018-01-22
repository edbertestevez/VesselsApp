package com.decypher.vesselsapp.Coordinator.SearchModule;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.Coordinator.EventModule.MySelectedDriveActivity;
import com.decypher.vesselsapp.R;
import com.decypher.vesselsapp.Search.SearchAdapterCoordinator;
import com.decypher.vesselsapp.Search.SearchData;
import com.decypher.vesselsapp.Search.SearchModuleIndividualAdapter;
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
public class CoordinatorSearch extends Fragment {

    View view;
    Button btnSearch;
    EditText etDonor;
    RecyclerView recyclerResult;
    TextView txtNone;

    SearchModuleIndividualAdapter adapter;
    SearchData searchValue;
    ArrayList<SearchData> donorList;

    String searchVal;

    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference userReference;
    String USER_REFERENCE = "users";

    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    public CoordinatorSearch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_coordinator_search, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        etDonor = (EditText) view.findViewById(R.id.etDonor);
        txtNone = (TextView) view.findViewById(R.id.txtNone);

        recyclerResult = (RecyclerView) view.findViewById(R.id.recyclerResult);
        recyclerResult.setHasFixedSize(true);
        recyclerResult.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        donorList = new ArrayList<SearchData>();
        adapter = new SearchModuleIndividualAdapter(getActivity().getApplicationContext(), donorList);
        recyclerResult.setAdapter(adapter);

        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        userReference = mDatabase.getReference(USER_REFERENCE);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSearch();
            }
        });
        return view;
    }

    private void loadSearch() {
        donorList.clear();
        searchVal = etDonor.getText().toString();

        userReference.orderByKey().startAt(searchVal).endAt(searchVal+"\uf8ff").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.exists()) {
                    searchValue = dataSnapshot.getValue(SearchData.class);
                    SearchData info = new SearchData();

                    Log.d("USER ID---->",dataSnapshot.getKey());
                    String user_id = dataSnapshot.getKey();
                    String name = searchValue.getName();
                    String city = searchValue.getCity();
                    String bloodtype = searchValue.getBloodtype();
                    String donation_count = searchValue.getDonation_count();
                    String user_photo = searchValue.getUser_photo();
                    String contact = searchValue.getContact();

                    info.setUser_id(user_id);
                    info.setName(name);
                    info.setCity(city);
                    info.setBloodtype(bloodtype);
                    info.setDonation_count(donation_count);
                    info.setUser_photo(user_photo);
                    info.setContact(contact);

                    txtNone.setVisibility(View.GONE);
                    donorList.add(info);
                    adapter.notifyDataSetChanged();

                }else{
                    donorList.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "No record exists.", Toast.LENGTH_SHORT).show();
                    txtNone.setVisibility(View.VISIBLE);
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
}

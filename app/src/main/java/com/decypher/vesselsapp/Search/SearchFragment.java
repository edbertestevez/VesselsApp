package com.decypher.vesselsapp.Search;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.decypher.vesselsapp.MainActivity;
import com.decypher.vesselsapp.MyPosts.ViewPost;
import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    View view;

    Spinner spnCity, spnType;
    ImageButton btnSearch;
    RecyclerView recyclerView;
    GlobalFunctions globalFunctions;
    String str_city, str_type;
    SearchData value;
    ArrayList<SearchData> searchList;
    SearchModuleAdapter adapter;
    TextView txtResult, txtType, txtCity, txtMessage, txtNumResult;

    String results;

    int numResult = 0;
    //firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference postReference, userReference;
    String POST_REFERENCE = "posts";
    String USER_REFERENCE = "users";

    ProgressDialog progDialog;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);

        ((MainActivity)getActivity()).setToolbarTitle("Search Donors");

        spnCity = (Spinner) view.findViewById(R.id.spnCity);

        spnType = (Spinner) view.findViewById(R.id.spnType);
        btnSearch = (ImageButton) view.findViewById(R.id.btnSearch);

        getActivity().setTitle("Search Donor");

        progDialog = new ProgressDialog(getActivity());
        progDialog.setMessage("Loading post information. .");

        txtMessage = (TextView) view.findViewById(R.id.txtSendMessage);
        txtResult = (TextView) view.findViewById(R.id.txtResult);
        txtType = (TextView) view.findViewById(R.id.txtType);
        txtCity = (TextView) view.findViewById(R.id.txtCity);
        txtNumResult = (TextView) view.findViewById(R.id.txtNumResult);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        globalFunctions = new GlobalFunctions(getActivity().getApplicationContext());

        searchList = new ArrayList<SearchData>();

        adapter = new SearchModuleAdapter(getActivity().getApplicationContext(), searchList);
        recyclerView.setAdapter(adapter);

        //FIREBASE
        mDatabase = FirebaseDatabase.getInstance();
        postReference = mDatabase.getReference(POST_REFERENCE);
        userReference = mDatabase.getReference(USER_REFERENCE);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(globalFunctions.isNetworkAvailable()) {
                    progDialog.setMessage("Retrieving possible donors for your blood. . ");
                    progDialog.show();
                    searchDonors();
                }else{
                    Toast.makeText(getActivity(), "No internet connection found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void searchDonors() {
        numResult=0;
        searchList.clear();

        txtMessage.setVisibility(View.VISIBLE);
        txtMessage.setText("No possible donor is available as of the moment");
        txtNumResult.setText("(0 possible donors)");
         str_city = spnCity.getSelectedItem().toString();
         str_type = spnType.getSelectedItem().toString();

        if(str_city.equals("All")){
            txtCity.setText("All City/Municipality");
        }else{
            txtCity.setText(str_city);
        }
        if(str_type.equals("All")){
            txtType.setText("All Blood Types");
        }else{
            txtType.setText(str_type);
        }

        txtResult.setText(str_type);

        searchList.clear();
        adapter.notifyDataSetChanged();
        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progDialog.dismiss();
                if(dataSnapshot.exists()) {
                    int ctr_validate=0;
                    //get data of specific doc
                    value = dataSnapshot.getValue(SearchData.class);

                    //insert to variables the values
                    SearchData info = new SearchData();

                    String user_id = dataSnapshot.getKey();
                    String name = value.getName();
                    String city = value.getCity();
                    String bloodtype = value.getBloodtype();
                    String donation_count = value.getDonation_count();
                    String user_photo = value.getUser_photo();
                    String contact = value.getGender();

                    if(str_city.equals("All") && str_type.equals("All")) {
                        //set data to adapter
                        info.setUser_id(user_id);
                        info.setName(name);
                        info.setCity(city);
                        info.setBloodtype(bloodtype);
                        info.setDonation_count(donation_count);
                        info.setUser_photo(user_photo);
                        info.setContact(contact);

                        results="All blood types";
                        txtResult.setText(results);

                        searchList.add(info);
                        adapter.notifyDataSetChanged();
                        numResult++;
                        txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");

                        txtMessage.setVisibility(View.INVISIBLE);
                    }else if(str_city.equals("All") && !str_type.equals("All")){
                            //same sa dalom pero may ara city na condition
                            if (str_type.equals("O+")) {
                                if (bloodtype.equals("O+") || bloodtype.equals("O-")) {
                                    results = "O+ , O-";
                                    ctr_validate++;
                                    txtMessage.setVisibility(View.INVISIBLE);
                                    txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                                }
                            } else if (str_type.equals("O-")) {
                                if (bloodtype.equals("O-")) {
                                    //sulod
                                    results = "O-";
                                    txtResult.setText("O-");
                                    ctr_validate++;
                                    txtMessage.setVisibility(View.INVISIBLE);
                                    txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                                }
                            } else if (str_type.equals("A+")) {
                                if (bloodtype.equals("O-") || bloodtype.equals("O+") || bloodtype.equals("A+") || bloodtype.equals("A-")) {
                                    //sulod
                                    results = "O- , O+ , A+ , A-";
                                    ctr_validate++;
                                    txtMessage.setVisibility(View.INVISIBLE);
                                    txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                                }
                            } else if (str_type.equals("A-")) {
                                if (bloodtype.equals("O-") || bloodtype.equals("A-")) {
                                    //sulod
                                    results = "O- , A-";
                                    ctr_validate++;
                                    txtMessage.setVisibility(View.INVISIBLE);
                                    txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                                }
                            } else if (str_type.equals("B+")) {
                                if (bloodtype.equals("O-") || bloodtype.equals("O+") || bloodtype.equals("B+") || bloodtype.equals("B-")) {
                                    //sulod
                                    results = "O- , O+ , B+ , B-";
                                    ctr_validate++;
                                    txtMessage.setVisibility(View.INVISIBLE);
                                    txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                                }
                            } else if (str_type.equals("B-")) {
                                if (bloodtype.equals("O-") || bloodtype.equals("B-")) {
                                    //sulod
                                    results = "O- , B-";
                                    txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                                    
                                    ctr_validate++;
                                    txtMessage.setVisibility(View.INVISIBLE);}
                            } else if (str_type.equals("AB+")) {
                                //all types man
                                results = "All blood types";
                                ctr_validate++;
                                txtMessage.setVisibility(View.INVISIBLE);
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                            } else if (str_type.equals("AB-")) {
                                if (bloodtype.equals("O-") || bloodtype.equals("B-") || bloodtype.equals("A-") || bloodtype.equals("AB-")) {
                                    //sulod
                                    results = "O- , B-, A- , AB-";
                                    ctr_validate++;
                                    txtMessage.setVisibility(View.INVISIBLE);
                                    txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                                }
                            }
                            if (ctr_validate > 0) {
                                //set data to adapter
                                info.setUser_id(user_id);
                                info.setName(name);
                                info.setCity(city);
                                info.setBloodtype(bloodtype);
                                info.setDonation_count(donation_count);
                                info.setUser_photo(user_photo);
                                info.setContact(contact);

                                txtResult.setText(results);
                                searchList.add(info);
                                adapter.notifyDataSetChanged();

                                txtMessage.setVisibility(View.INVISIBLE);
                                numResult++;
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                            }
                        }else if(!str_city.equals("All") && str_type.equals("All")){
                            //kwaon lng ang city
                            if(city.equals(str_city)){
                                info.setUser_id(user_id);
                                info.setName(name);
                                info.setCity(city);
                                info.setBloodtype(bloodtype);
                                info.setDonation_count(donation_count);
                                info.setUser_photo(user_photo);
                                info.setContact(contact);

                                txtResult.setText(results);
                                searchList.add(info);
                                adapter.notifyDataSetChanged();

                                numResult++;

                                txtMessage.setVisibility(View.INVISIBLE);
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                            }
                        }else if(!str_city.equals("All") && !str_type.equals("All")) {
                            //same sa dalom pero may ara city na condition
                        if (str_type.equals("O+")) {
                            if (bloodtype.equals("O+") || bloodtype.equals("O-")) {
                                results = "O+ , O-";
                                ctr_validate++;

                                txtMessage.setVisibility(View.INVISIBLE);
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                            }
                        } else if (str_type.equals("O-")) {
                            if (bloodtype.equals("O-")) {
                                //sulod
                                results = "O-";
                                txtResult.setText("O-");
                                ctr_validate++;

                                txtMessage.setVisibility(View.INVISIBLE);
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                            }
                        } else if (str_type.equals("A+")) {
                            if (bloodtype.equals("O-") || bloodtype.equals("O+") || bloodtype.equals("A+") || bloodtype.equals("A-")) {
                                //sulod
                                results = "O- , O+ , A+ , A-";

                                txtMessage.setVisibility(View.INVISIBLE);
                                ctr_validate++;
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                            }
                        } else if (str_type.equals("A-")) {
                            if (bloodtype.equals("O-") || bloodtype.equals("A-")) {
                                //sulod
                                results = "O- , A-";
                                ctr_validate++;
                                txtMessage.setVisibility(View.INVISIBLE);
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                            }
                        } else if (str_type.equals("B+")) {
                            if (bloodtype.equals("O-") || bloodtype.equals("O+") || bloodtype.equals("B+") || bloodtype.equals("B-")) {
                                //sulod
                                results = "O- , O+ , B+ , B-";
                                ctr_validate++;
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");

                                txtMessage.setVisibility(View.INVISIBLE);}
                        } else if (str_type.equals("B-")) {
                            if (bloodtype.equals("O-") || bloodtype.equals("B-")) {
                                //sulod
                                results = "O- , B-";
                                ctr_validate++;
                                txtMessage.setVisibility(View.INVISIBLE);
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                            }
                        } else if (str_type.equals("AB+")) {
                            //all types man
                            results = "All blood types";
                            ctr_validate++;
                            txtMessage.setVisibility(View.INVISIBLE);
                            txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                        } else if (str_type.equals("AB-")) {
                            if (bloodtype.equals("O-") || bloodtype.equals("B-") || bloodtype.equals("A-") || bloodtype.equals("AB-")) {
                                //sulod
                                results = "O- , B-, A- , AB-";
                                ctr_validate++;
                                txtMessage.setVisibility(View.INVISIBLE);
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");
                            }
                        }
                            //diri lng butang
                            if (ctr_validate > 0 && city.equals(str_city)) {
                                //set data to adapter
                                info.setUser_id(user_id);
                                info.setName(name);
                                info.setCity(city);
                                info.setBloodtype(bloodtype);
                                info.setDonation_count(donation_count);
                                info.setUser_photo(user_photo);
                                info.setContact(contact);
                                numResult++;
                                txtNumResult.setText("("+String.valueOf(numResult)+" possible donors)");

                                txtResult.setText(results);

                                searchList.add(info);
                                adapter.notifyDataSetChanged();

                                txtMessage.setVisibility(View.INVISIBLE);
                            }

                        }

                }
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
                Toast.makeText(getContext(), "Wala record", Toast.LENGTH_SHORT).show();
            }

        });



    }

}

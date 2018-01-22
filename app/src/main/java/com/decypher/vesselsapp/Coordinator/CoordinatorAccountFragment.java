package com.decypher.vesselsapp.Coordinator;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.decypher.vesselsapp.Others.GlobalFunctions;
import com.decypher.vesselsapp.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CoordinatorAccountFragment extends Fragment {

    View view;
    TextView txtAssociation, txtName, txtAddress, txtContact, txtEmail;
    Button btnEdit;
    ImageView imgPhoto;

    GlobalFunctions globalFunctions;

    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";

    public CoordinatorAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_coordinator_account, container, false);

        getActivity().setTitle("Profile");

        globalFunctions = new GlobalFunctions(getContext());
        sharedpref = getActivity().getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        txtAssociation = (TextView) view.findViewById(R.id.txtAssociation);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtContact = (TextView) view.findViewById(R.id.txtContactOrg);
        txtEmail = (TextView) view.findViewById(R.id.txtEmail);

        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        imgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditCoordinatorAccount.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInfo();
    }

    private void loadInfo(){
        txtAssociation.setText(sharedpref.getString("COR_ASSOCIATION",""));
        txtName.setText(sharedpref.getString("COR_NAME",""));
        txtAddress.setText(sharedpref.getString("COR_ADDRESS",""));
        txtContact.setText(sharedpref.getString("COR_CONTACT",""));
        txtEmail.setText(sharedpref.getString("COR_EMAIL",""));

        Picasso.with(getActivity().getApplicationContext()).load(sharedpref.getString("COR_PHOTO","")).fit().centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imgPhoto);

    }



}

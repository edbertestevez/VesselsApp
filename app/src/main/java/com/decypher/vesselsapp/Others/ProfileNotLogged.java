package com.decypher.vesselsapp.Others;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.decypher.vesselsapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileNotLogged extends Fragment {

    Button btnProceed, btnRegister;
    View view;

    public ProfileNotLogged() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_not_logged, container, false);

        btnProceed = (Button) view.findViewById(R.id.btnProceed);
        btnRegister = (Button) view.findViewById(R.id.btnRegister);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignupActivity1.class);
                startActivity(intent);
            }
        });
        return view;
    }
}

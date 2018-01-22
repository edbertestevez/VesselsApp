package com.decypher.vesselsapp.BloodDrive;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.decypher.vesselsapp.Coordinator.EventModule.MyAdvocacyFragment;
import com.decypher.vesselsapp.Coordinator.EventModule.MyBloodDriveFragment;
import com.decypher.vesselsapp.Others.BottomNavigationViewEx;
import com.decypher.vesselsapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventBloodMain extends Fragment {
    private BottomNavigationViewEx.TabAdapter mTabAdapter;
    private ViewPager mViewPager;
    View view;

    public EventBloodMain() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_event_blood_main, container, false);
        mTabAdapter = new BottomNavigationViewEx.TabAdapter(getFragmentManager());
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Events");

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        BottomNavigationViewEx.TabAdapter adapter = new BottomNavigationViewEx.TabAdapter(getFragmentManager());
        adapter.addFragment(new BloodDriveFragment(), "Blood Drives");
        adapter.addFragment(new EventFragment(), "Others");
        viewPager.setAdapter(adapter);
    }
}

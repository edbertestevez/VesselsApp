package com.decypher.vesselsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.decypher.vesselsapp.Coordinator.CoordinatorMainActivity;
import com.decypher.vesselsapp.Others.LoginSelectionActivity;
import com.decypher.vesselsapp.Walkthrough.WalkActivity;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 3000) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    if(sharedpref.getBoolean("DONE",false)){
                        if(!sharedpref.getString("USERID","").equals("")){
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else if(!sharedpref.getString("COR_ID","").equals("")){
                            Intent intent = new Intent(SplashActivity.this, CoordinatorMainActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(SplashActivity.this, LoginSelectionActivity.class);
                            startActivity(intent);
                        }
                    }else{
                        Intent intent = new Intent(SplashActivity.this, WalkActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
            }
};
        splashThread.start();



    }
}


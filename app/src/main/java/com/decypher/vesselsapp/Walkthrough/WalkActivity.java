package com.decypher.vesselsapp.Walkthrough;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.decypher.vesselsapp.Others.LoginSelectionActivity;
import com.decypher.vesselsapp.R;

public class WalkActivity extends AppCompatActivity {

    int pageCounter=0;
    String headerName;
    Button btnNext;
    Button btnPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walk_main);

         btnNext = (Button) findViewById(R.id.btnNext);
         btnPrev = (Button) findViewById(R.id.btnPrev);

        //default
        btnPrev.setText("Skip");

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Walk_One()).commit();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pageCounter==0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Walk_Two()).commit();
                    btnPrev.setVisibility(View.VISIBLE);
                    btnPrev.setText("Previous");
                    pageCounter++;
                }else if(pageCounter==1){
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Walk_Three()).commit();
                    pageCounter++;
                    btnNext.setText("Get Started");
                    btnPrev.setText("Previous");
                }else if(pageCounter==2){
                    Intent intent = new Intent(WalkActivity.this, LoginSelectionActivity.class);
                    btnPrev.setText("Previous");
                    startActivity(intent);
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pageCounter==0){
                    Intent intent = new Intent(WalkActivity.this, LoginSelectionActivity.class);
                    startActivity(intent);
                }else if(pageCounter==1){
                    btnNext.setText("Next");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Walk_One()).commit();
                    btnPrev.setVisibility(View.VISIBLE);
                    pageCounter--;
                    btnPrev.setText("Skip");
                }else if(pageCounter==2){
                    btnNext.setText("Next");
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Walk_Two()).commit();
                    pageCounter--;
                }
            }
        });


    }

}

package com.decypher.vesselsapp.DonationProcess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.decypher.vesselsapp.R;

public class DonationStep1 extends AppCompatActivity {

    String post_photo, post_id, post_receiver, selected_id, response_ctr;
    Button btnNext, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question1);

        Bundle extras = getIntent().getExtras();
        post_photo = extras.getString("POST_PHOTO");
        post_id = extras.getString("POST_ID");
        post_receiver = extras.getString("POST_RECEIVER");
        selected_id = extras.getString("SELECTED_ID");
        response_ctr = extras.getString("RESPONSE_COUNT");

        btnNext = (Button) findViewById(R.id.btnNext);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DonationStep1.this, DonationStep2.class);
                intent.putExtra("POST_ID",post_id);
                intent.putExtra("POST_RECEIVER",post_receiver);
                intent.putExtra("POST_PHOTO",post_photo);
                intent.putExtra("SELECTED_ID", selected_id);
                intent.putExtra("RESPONSE_COUNT", response_ctr);
                startActivity(intent);
            }
        });
    }

}

package com.decypher.vesselsapp.DonationProcess;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.decypher.vesselsapp.MainActivity;
import com.decypher.vesselsapp.Messages.UserSelectedChatActivity;
import com.decypher.vesselsapp.R;
import com.squareup.picasso.Picasso;

public class DonationStep3 extends AppCompatActivity {
    String post_photo, post_id, post_receiver;
    Button btnContinue, btnMessage;
    ImageView imgPost, imgUser;
    TextView txtThanks;
    String SHAREDPREF = "userInfo";
    SharedPreferences sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question3);

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();
        post_photo = extras.getString("POST_PHOTO");
        post_id = extras.getString("POST_ID");
        post_receiver = extras.getString("POST_RECEIVER");

        btnMessage = (Button) findViewById(R.id.btnMessage);
        btnContinue = (Button) findViewById(R.id.btnProceed);
        imgPost = (ImageView) findViewById(R.id.imgPost);
        imgUser = (ImageView) findViewById(R.id.imgUser);
        txtThanks = (TextView) findViewById(R.id.txtThanks);

        Picasso.with(getApplicationContext()).load(post_photo).fit().centerCrop().into(imgPost);
        Picasso.with(getApplicationContext()).load(sharedpref.getString("USERPHOTO","")).fit().centerCrop().into(imgUser);
        txtThanks.setText("Thank you for responding to the need of '"+post_receiver+"'");
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DonationStep3.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DonationStep3.this, UserSelectedChatActivity.class);
                intent.putExtra("RECEIVER", post_receiver);
                startActivity(intent);
            }
        });
    }
}

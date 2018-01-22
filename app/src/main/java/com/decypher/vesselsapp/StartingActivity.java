package com.decypher.vesselsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartingActivity extends AppCompatActivity {

    Button btnLogin, btnGoogle, btnSignup, btnContinue;
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_starting);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnGoogle = (Button) findViewById(R.id.btnGoogle);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        btnContinue = (Button) findViewById(R.id.btnProceed);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

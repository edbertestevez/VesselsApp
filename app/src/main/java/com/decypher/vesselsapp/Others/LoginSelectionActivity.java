package com.decypher.vesselsapp.Others;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.decypher.vesselsapp.Coordinator.CoordinatorLoginActivity;
import com.decypher.vesselsapp.MainActivity;
import com.decypher.vesselsapp.R;

import org.w3c.dom.Text;

public class LoginSelectionActivity extends AppCompatActivity {

    Button btnUser, btnCoordinator;
    TextView txtGuest, txtRegister;

    SharedPreferences sharedpref;
    String SHAREDPREF = "userInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_selection);

        btnUser = (Button) findViewById(R.id.btnUser);
        btnCoordinator = (Button) findViewById(R.id.btnCoordinator);
        txtGuest = (TextView) findViewById(R.id.txtGuest);
        txtRegister = (TextView) findViewById(R.id.txtRegister);

        sharedpref = getSharedPreferences(SHAREDPREF, MODE_PRIVATE);

        SharedPreferences.Editor editor = getSharedPreferences(SHAREDPREF, MODE_PRIVATE).edit();
        editor.putBoolean("DONE", true);
        editor.commit();

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSelectionActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        txtGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSelectionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnCoordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSelectionActivity.this, CoordinatorLoginActivity.class);
                startActivity(intent);
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginSelectionActivity.this, SignupActivity1.class);
                startActivity(intent);
            }
        });
    }
}

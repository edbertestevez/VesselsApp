package com.decypher.vesselsapp.Others;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.decypher.vesselsapp.R;

public class SignupActivity2 extends AppCompatActivity {

    Button btnNext;
    EditText etAddress;
    Spinner spnCity;

    String name, password, email, contact, address, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        final EditText etAddress = (EditText) findViewById(R.id.etAddress);
        final Spinner spnCity = (Spinner) findViewById(R.id.spnCity);

        Bundle extras = getIntent().getExtras();
        name = extras.getString("NAME");
        password = extras.getString("PASSWORD");
        email = extras.getString("EMAIL");
        contact = extras.getString("CONTACT");


        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = etAddress.getText().toString();
                city = spnCity.getSelectedItem().toString();
                if(address.isEmpty()){
                    Toast.makeText(SignupActivity2.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(SignupActivity2.this,SignupActivity3.class);
                    intent.putExtra("NAME", name);
                    intent.putExtra("PASSWORD", password);
                    intent.putExtra("CONTACT", contact);
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("ADDRESS", address);
                    intent.putExtra("CITY", city);
                    startActivity(intent);
                }

            }
        });
    }
}

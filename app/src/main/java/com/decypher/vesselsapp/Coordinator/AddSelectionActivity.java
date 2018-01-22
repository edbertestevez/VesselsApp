package com.decypher.vesselsapp.Coordinator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.decypher.vesselsapp.R;

public class AddSelectionActivity extends AppCompatActivity {

    Button btnDrive, btnAdvocacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_selection);

        btnAdvocacy = (Button) findViewById(R.id.btnAdvocacy);
        btnDrive = (Button) findViewById(R.id.btnDrive);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("New Event");

        btnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddSelectionActivity.this, AddBloodDriveActivity.class);
                startActivity(intent);
            }
        });

        btnAdvocacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddSelectionActivity.this, AddAdvocacyActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

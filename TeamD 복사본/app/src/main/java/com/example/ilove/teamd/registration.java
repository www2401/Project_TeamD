package com.example.ilove.teamd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class registration extends AppCompatActivity {
    public Button bt1;
    public Button bt2;
    public void init() {
        bt1=(Button)findViewById(R.id.button);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(registration.this, "password confirm", Toast.LENGTH_SHORT).show();
            }
        });
        bt2 = (Button) findViewById(R.id.button1);
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent page = new Intent(registration.this, registration_authentification.class);
                startActivity(page);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
            init();
    }
}

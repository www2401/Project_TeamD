package com.example.ilove.teamd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class reset_password extends AppCompatActivity {
    public Button bt1;
    public void init() {
        bt1=(Button)findViewById(R.id.button);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent page =new Intent(reset_password.this,reset_authentification.class);
                startActivity(page);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        init();
    }
}

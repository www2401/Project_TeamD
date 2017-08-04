package com.example.ilove.teamd.userfage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ilove.teamd.R;

public class reset_authentification extends AppCompatActivity {

    public Button bt1;
    public Button bt2;
    public void init() {
        bt1=(Button)findViewById(R.id.button);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(reset_authentification.this, "authentification code complete!", Toast.LENGTH_SHORT).show();
            }
        });
        bt2=(Button)findViewById(R.id.button1);
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent page =new Intent(reset_authentification.this,new_password.class);
                startActivity(page);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_authentification);
        init();
    }
}

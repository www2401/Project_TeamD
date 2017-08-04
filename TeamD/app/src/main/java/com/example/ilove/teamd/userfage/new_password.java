package com.example.ilove.teamd.userfage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ilove.teamd.R;
import com.example.ilove.teamd.TeamD;

public class new_password extends AppCompatActivity {

    public Button bt1;
    public Button bt2;
    public void init() {
        bt1=(Button)findViewById(R.id.button);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(new_password.this, "password confirm", Toast.LENGTH_SHORT).show();
            }
        });
        bt2 = (Button) findViewById(R.id.button1);
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent page = new Intent(new_password.this, TeamD.class);
                startActivity(page);
                Toast.makeText(new_password.this, "reset-password complete", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        init();
    }
}

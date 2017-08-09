package com.example.ilove.teamd.userfage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ilove.teamd.R;

public class reset_password extends AppCompatActivity {
    public Button bt1,bt2,bt3;
    public EditText et_lname, et_fname, et_code,et_id, et_pw, et_birthday, et_weight, et_height;
    public AlertDialog dialog;
    String resulto, myResult;

    public void init() {
        bt1 = (Button) findViewById(R.id.bt_id);
        bt2= (Button)findViewById(R.id.bt_code);
        bt3=(Button)findViewById(R.id.bt_next);
        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_pw);
        et_code=(EditText)findViewById(R.id.et_code);
        et_fname = (EditText) findViewById(R.id.et_fname);
        et_lname = (EditText) findViewById(R.id.et_lname);
        et_birthday = (EditText) findViewById(R.id.et_birthday);
        et_weight = (EditText) findViewById(R.id.et_weight);
        et_height = (EditText) findViewById(R.id.et_height);
        bt1=(Button)findViewById(R.id.button);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent page =new Intent(reset_password.this,new_password.class);
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

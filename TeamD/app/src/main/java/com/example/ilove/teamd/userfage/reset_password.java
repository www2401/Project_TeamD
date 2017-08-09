package com.example.ilove.teamd.userfage;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

        //이메일로 인증코드 전송
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(reset_password.this);
                builder.setMessage("We send the authentication code. Please check your email address." +
                        " you can write the authentication code at next line.").setPositiveButton("OK", null).create().show();
            }
        });
        //코드 일치하는지 확인
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(reset_password.this);
                builder.setMessage("Correct authentication code. Please check next button.").setPositiveButton("OK", null).create().show();
            }
        });
        //비밀번호 새로 설정하는 activity로 이동
        bt3.setOnClickListener(new View.OnClickListener() {
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

        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}

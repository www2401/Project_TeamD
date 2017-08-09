package com.example.ilove.teamd.userfage;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ilove.teamd.JsonTransfer;
import com.example.ilove.teamd.R;
import com.example.ilove.teamd.TeamD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class new_password extends AppCompatActivity {
    public Button bt1,bt2;
    public EditText et_lname, et_fname, et_code,et_id, et_pw,et_c_pw, et_birthday, et_weight, et_height;
    public AlertDialog dialog;
    String resulto, myResult;
    public void init() {
        bt1 = (Button) findViewById(R.id.bt_pw);
        bt2= (Button)findViewById(R.id.bt_complete);
        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_pw);
        et_c_pw=(EditText)findViewById(R.id.et_c_pw);
        et_fname = (EditText) findViewById(R.id.et_fname);
        et_lname = (EditText) findViewById(R.id.et_lname);
        et_birthday = (EditText) findViewById(R.id.et_birthday);
        et_weight = (EditText) findViewById(R.id.et_weight);
        et_height = (EditText) findViewById(R.id.et_height);

        //비밀번호 중복 체크
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //비밀번호,재입력 둘다 빈칸이 아닐 때
                if((et_pw.getText().toString().getBytes().length>0)&&(et_c_pw.getText().toString().getBytes().length>0)) {
                    if (et_pw.getText().toString().equals(et_c_pw.getText().toString())) {
                        AlertDialog.Builder a = new AlertDialog.Builder(new_password.this);
                        dialog = a.setMessage("correct").setPositiveButton("OK", null).create();
                        dialog.show();
                    }
                    //비밀번호,재입력 두개가 일치하지 않을 때
                    else if(et_pw.getText().toString()!=et_c_pw.getText().toString()) {
                        AlertDialog.Builder a = new AlertDialog.Builder(new_password.this);
                        dialog = a.setMessage("Not correct password. Please Check your password.").setPositiveButton("OK", null).create();
                        dialog.show();
                    }
                }
                //비밀번호, 재입력 중 하나라도 입력값이 없으면 채우라는 메세지가 뜸
                else {
                    AlertDialog.Builder a = new AlertDialog.Builder(new_password.this);
                    dialog = a.setMessage("Please fill out ").setPositiveButton("OK", null).create();
                    dialog.show();
                }
            }
        });
        //비밀번호 서버에 전송,확인완료, 새로운 비밀번호 설정
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        init();

        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}

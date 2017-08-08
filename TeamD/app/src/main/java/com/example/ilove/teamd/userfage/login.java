package com.example.ilove.teamd.userfage;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class login extends AppCompatActivity {
    public Button bt1;
    public EditText et_lname,et_fname,et_id,et_pw,et_gender,et_birthday,et_weight,et_height;
    public AlertDialog dialog;
    String resulto,myResult,loginid,loginpw;

    public void init() {
        bt1 = (Button) findViewById(R.id.bt_login);
        et_id = (EditText)findViewById(R.id.et_id);
        et_pw = (EditText)findViewById(R.id.et_pw);
        et_fname= (EditText)findViewById(R.id.et_fname);
        et_lname= (EditText)findViewById(R.id.et_lname);
        et_birthday= (EditText)findViewById(R.id.et_birthday);
        et_weight= (EditText)findViewById(R.id.et_weight);
        et_height= (EditText)findViewById(R.id.et_height);
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginid =auto.getString("email",null);
        loginpw=auto.getString("password",null);



        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(et_id.getText().toString().equals("")||et_pw.getText().toString().equals("")){
                    AlertDialog.Builder a = new AlertDialog.Builder(login.this);
                    dialog = a.setMessage("Please fill out email ").setPositiveButton("OK", null).create();
                    dialog.show();
                }
                else {
                    try {

                        URL url = new URL("http://teama-iot.calit2.net/slim-api/android-login");
                        HttpURLConnection http = (HttpURLConnection) url.openConnection();

                        http.setDefaultUseCaches(false);
                        http.setDoInput(true);//서버에서 읽기모드지정
                        http.setDoOutput(true); //서버에서 쓰기모드 지정
                        http.setRequestMethod("POST"); //전송방식

                        http.setRequestProperty("content_type", "application/x-www-form-urlencoded");//서버에서 웹에게 FORM으로 값이 넘어온 것과 같은 방식으로 처리한다고알림


                        StringBuffer buffer = new StringBuffer(); //서버에 데이터보낼떄
                        buffer.append("user_id").append("=").append(et_id.getText().toString()).append("&");
                        buffer.append("user_password").append("=").append(et_pw.getText().toString());

                        OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR"); //OutputStream 전송길을 만들어주는거
                        PrintWriter writer = new PrintWriter(outStream);
                        writer.write(buffer.toString());
                        writer.flush();

                        //서버에서 전송받기
                        InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuilder builder = new StringBuilder();
                        String str;

                        while ((str = reader.readLine()) != null) {
                            builder.append(str + "\n");
                        }
                        myResult = builder.toString();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject Json_confirmid = new JSONObject(myResult);
                        resulto = Json_confirmid.getString("result");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (resulto == "true") {
                        AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                        builder.setMessage("log in complete").setPositiveButton("OK", null).create().show();
                        et_id.setEnabled(false); //로그인 가능
                        Intent page = new Intent(login.this, TeamD.class);
                        startActivity(page);

                    }
                    if (resulto == "false") {
                        AlertDialog.Builder builder = new AlertDialog.Builder(login.this);//로그인 불가
                        builder.setMessage("Not log in").setNegativeButton("OK", null).create().show();
                    }
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}

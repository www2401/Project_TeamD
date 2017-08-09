package com.example.ilove.teamd.userfage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

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

import static android.R.attr.id;
import static android.R.attr.password;

public class login extends AppCompatActivity {
    public Button bt1,bt2;
    public EditText et_lname, et_fname, et_id, et_pw, et_birthday, et_weight, et_height;
    public AlertDialog dialog;
    String resulto, myResult;

    public void init() {
        bt1 = (Button) findViewById(R.id.bt_login);
        bt2= (Button)findViewById(R.id.bt_forgot);
        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_pw);
        et_fname = (EditText) findViewById(R.id.et_fname);
        et_lname = (EditText) findViewById(R.id.et_lname);
        et_birthday = (EditText) findViewById(R.id.et_birthday);
        et_weight = (EditText) findViewById(R.id.et_weight);
        et_height = (EditText) findViewById(R.id.et_height);

        //login버튼 클릭했을 때
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //빈칸이 있을 때
                if(et_id.getText().toString().equals("")||et_pw.getText().toString().equals("")){
                    AlertDialog.Builder a = new AlertDialog.Builder(login.this);
                    dialog = a.setMessage("Please fill out email ").setPositiveButton("OK", null).create();
                    dialog.show();
                }//빈칸 없이 모두 채워 졌을 때
                else {
                    try {

                        URL url = new URL("http://teamd-iot.calit2.net/finally/slim-api/login_app");
                        HttpURLConnection http = (HttpURLConnection) url.openConnection();

                        http.setDefaultUseCaches(false);
                        http.setDoInput(true);//서버에서 읽기모드지정
                        http.setDoOutput(true); //서버에서 쓰기모드 지정
                        http.setRequestMethod("POST"); //전송방식

                        http.setRequestProperty("content_type", "application/x-www-form-urlencoded");//서버에서 웹에게 FORM으로 값이 넘어온 것과 같은 방식으로 처리한다고알림

                        StringBuffer buffer = new StringBuffer(); //서버에 데이터보낼떄
                        buffer.append("email").append("=").append(et_id.getText().toString()).append("&");
                        buffer.append("password").append("=").append(et_pw.getText().toString());

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
                        resulto = Json_confirmid.getString("status");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                        if (resulto.equals("femail")) {//로그인 인증 완료 전 로그인 불가
                            AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                            builder.setMessage("Not authentication! Please check your email. you must write here authentication code. ").setPositiveButton("OK", null).create().show();
                        } else if (resulto.equals("flogin")) {//회원가입 안해서 or 아이디 비밀번호 일치하지 않아서 로그인 불가
                            AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                            builder.setMessage("Not log in! Please sign up or check your email and password.").setNegativeButton("OK", null).create().show();
                        } else {//로그인 완료
                            AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                            builder.setMessage("log in complete! you can do this program").setNegativeButton("OK", null).create().show();
                            Intent page = new Intent(login.this, TeamD.class);
                            startActivity(page);
                        }
                }
            }
        });
        //forgot password 버튼 클릭했을때
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent page =new Intent(login.this,reset_password.class);
                startActivity(page);
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

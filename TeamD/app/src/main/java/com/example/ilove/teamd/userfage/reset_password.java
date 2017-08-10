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

public class reset_password extends AppCompatActivity {
    public Button bt1,bt2,bt3;
    public EditText  et_code,et_id, et_pw;
    public AlertDialog dialog;
    String resulto, myResult;
    static public String a ="";

    public void init() {
        bt1 = (Button) findViewById(R.id.bt_id);
        bt2= (Button)findViewById(R.id.bt_code);
        bt3=(Button)findViewById(R.id.bt_next);
        et_id = (EditText) findViewById(R.id.et_id);
        et_pw = (EditText) findViewById(R.id.et_pw);
        et_code=(EditText)findViewById(R.id.et_code);

        //이메일로 인증코드 전송
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //이메일 입력을 하지 않았을 경우
                if(et_id.getText().toString().equals("")){
                    AlertDialog.Builder a = new AlertDialog.Builder(reset_password.this);
                    dialog = a.setMessage("Please fill out email ").setPositiveButton("OK", null).create();
                    dialog.show();
                }
                //이메일 입력한 경우
                else {
                        try {
                            a=et_id.getText().toString();
                            URL url = new URL("http://teamd-iot.calit2.net/finally/slim-api/email_check_for_rp");
                            HttpURLConnection http = (HttpURLConnection) url.openConnection();

                            http.setDefaultUseCaches(false);
                            http.setDoInput(true);//서버에서 읽기모드지정
                            http.setDoOutput(true); //서버에서 쓰기모드 지정
                            http.setRequestMethod("POST"); //전송방식

                            http.setRequestProperty("content_type", "application/x-www-form-urlencoded");//서버에서 웹에게 FORM으로 값이 넘어온 것과 같은 방식으로 처리한다고알림


                            StringBuffer buffer = new StringBuffer(); //서버에 데이터보낼때
                            buffer.append("email").append("=").append(a);

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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (resulto == "true") {//이메일 확인 되고 인증코드 보내졌을 때
                            AlertDialog.Builder builder = new AlertDialog.Builder(reset_password.this);
                            builder.setMessage("We send the authentication code. Please check your email address." +
                                    " you can write the authentication code at next line.").setPositiveButton("OK", null).create().show();
                            et_id.setEnabled(false); //아이디 변경불가

                        }
                        if (resulto == "false") {//이메일이 존재하지 않아서 오류날때
                            AlertDialog.Builder builder = new AlertDialog.Builder(reset_password.this);//알림창이 뜨게한다 여기에
                            builder.setMessage("Not exist email").setNegativeButton("OK", null).create().show();
                        }
                }
            }
        });
        //코드 일치하는지 확인
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //코드를 입력하지 않았을 때
                if(et_code.getText().toString().equals("")){
                    AlertDialog.Builder a = new AlertDialog.Builder(reset_password.this);
                    dialog = a.setMessage("Please fill out authentication code number ").setPositiveButton("OK", null).create();
                    dialog.show();
                }
                //코드를 입력했을 때
                else {
                    try {

                        URL url = new URL("http://teamd-iot.calit2.net/finally/slim-api/code_check_app");
                        HttpURLConnection http = (HttpURLConnection) url.openConnection();

                        http.setDefaultUseCaches(false);
                        http.setDoInput(true);//서버에서 읽기모드지정
                        http.setDoOutput(true); //서버에서 쓰기모드 지정
                        http.setRequestMethod("POST"); //전송방식

                        http.setRequestProperty("content_type", "application/x-www-form-urlencoded");//서버에서 웹에게 FORM으로 값이 넘어온 것과 같은 방식으로 처리한다고알림


                        StringBuffer buffer = new StringBuffer(); //서버에 데이터보낼때
                        buffer.append("email").append("=").append(a);
                        buffer.append("status").append("=").append(et_code.getText().toString());

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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (resulto == "true") {//인증코드가 일치해서 완료되었을 때
                        AlertDialog.Builder builder = new AlertDialog.Builder(reset_password.this);
                        builder.setMessage("Correct authentication code. Please check next button.").setPositiveButton("OK", null).create().show();
                        et_code.setEnabled(false); //코드 변경 불가

                    }
                    if (resulto == "false") {//인증코드가 일치하지 않을 때
                        AlertDialog.Builder builder = new AlertDialog.Builder(reset_password.this);//알림창이 뜨게한다 여기에
                        builder.setMessage("Not correct authentication code.").setNegativeButton("OK", null).create().show();
                    }
                }
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

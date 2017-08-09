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
                //빈칸이 있을 때
                if(et_id.getText().toString().equals("")||et_pw.getText().toString().equals("")){
                    AlertDialog.Builder a = new AlertDialog.Builder(new_password.this);
                    dialog = a.setMessage("Please fill out password ").setPositiveButton("OK", null).create();
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
                    if (resulto.equals("femail")) {//uid에 비밀번호 변경이 되었을 때
                        AlertDialog.Builder builder = new AlertDialog.Builder(new_password.this);
                        builder.setMessage("password change complete  ").setPositiveButton("OK", null).create().show();
                    } else if (resulto.equals("flogin")) {//uid에 비밀번호 변경이 안되었을 때
                        AlertDialog.Builder builder = new AlertDialog.Builder(new_password.this);
                        builder.setMessage("password Not change.").setNegativeButton("OK", null).create().show();
                    }
                }
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

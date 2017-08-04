package com.example.ilove.teamd.userfage;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ilove.teamd.JsonTransfer;
import com.example.ilove.teamd.R;
import com.example.ilove.teamd.TeamD;

import org.json.JSONObject;

public class registration extends AppCompatActivity {
    public Button bt1,bt2,bt3;
    public EditText et_lname,et_fname,et_id,et_pw,et_gender,et_c_pw,et_birthday,et_weight,et_height;

    public void init() {
        bt1 = (Button) findViewById(R.id.bt_sign_up);
        bt2 = (Button)findViewById(R.id.bt_id);
        bt3 = (Button)findViewById(R.id.bt_pw);
        et_id = (EditText)findViewById(R.id.et_id);
        et_pw = (EditText)findViewById(R.id.et_pw);
        et_c_pw = (EditText)findViewById(R.id.et_c_pw);
        et_fname= (EditText)findViewById(R.id.et_fname);
        et_lname= (EditText)findViewById(R.id.et_lname);
        et_birthday= (EditText)findViewById(R.id.et_birthday);
        et_weight= (EditText)findViewById(R.id.et_weight);
        et_height= (EditText)findViewById(R.id.et_height);

        //입력하고 signup 버튼을 눌렀을 때
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JsonTransfer userdata_transfer = new JsonTransfer();
                //빈칸이 있을 경우
                if ((et_pw.getText().toString().getBytes().length <= 0) && (et_c_pw.getText().toString().getBytes().length <= 0))
                    Toast.makeText(registration.this, "please fill out", Toast.LENGTH_SHORT).show();
                //빈칸없이 모두 채워졌을 경우
                else {
                    try {
                        JSONObject json_UserdataTransfer = new JSONObject();  //JSONObject는 JSON을 만들기 위함.
                        //json_dataTransfer에 ("키값" : "보낼데이터") 형식으로 저장한다.
                        json_UserdataTransfer.put("email", et_id.getText().toString());
                        json_UserdataTransfer.put("password", et_pw.getText().toString());
                        json_UserdataTransfer.put("fname", et_fname.getText().toString());
                        json_UserdataTransfer.put("lname", et_lname.getText().toString());
                        json_UserdataTransfer.put("gender", "0");
                        json_UserdataTransfer.put("birthday", et_birthday.getText().toString());
                        json_UserdataTransfer.put("height", et_height.getText().toString());
                        json_UserdataTransfer.put("weight", et_weight.getText().toString());
                        //json_dataTransfer의 데이터들을 하나의 json_string으로 묶는다.
                        String json_Ustring = json_UserdataTransfer.toString();

                        //보내기 전에 json_string 양 쪽 끝에 대괄호를 붙인다. (Object로 처리하기 때문이다. 만약 Array로 처리한다면, 대괄호는 필요없다고 한다.)
                        userdata_transfer.execute("http://teamd-iot.calit2.net/finally/slim-api/signtest", "[" + json_Ustring + "]");  //보낼주소추가

                        Toast.makeText(registration.this, "sign up complete", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent page = new Intent(registration.this, TeamD.class);
                    startActivity(page);
                }
            }
        });
        //이메일이 존재하는지 체크하는 버튼을 눌렀을 때
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                JsonTransfer userdata_transfer = new JsonTransfer();

                    try {
                        JSONObject json_UserdataTransfer = new JSONObject();  //JSONObject는 JSON을 만들기 위함.
                        //json_dataTransfer에 ("키값" : "보낼데이터") 형식으로 저장한다.
                        json_UserdataTransfer.put("email", et_id.getText().toString());
                        json_UserdataTransfer.put("password", "0");
                        json_UserdataTransfer.put("fname", "0");
                        json_UserdataTransfer.put("lname", "0");
                        json_UserdataTransfer.put("gender", "0");
                        json_UserdataTransfer.put("birthday", "0");
                        json_UserdataTransfer.put("height", "0");
                        json_UserdataTransfer.put("weight", "0");
                        //json_dataTransfer의 데이터들을 하나의 json_string으로 묶는다.
                        String json_Ustring = json_UserdataTransfer.toString();

                        //보내기 전에 json_string 양 쪽 끝에 대괄호를 붙인다. (Object로 처리하기 때문이다. 만약 Array로 처리한다면, 대괄호는 필요없다고 한다.)
                        userdata_transfer.execute("http://teamd-iot.calit2.net/finally/slim-api/signtest", "[" + json_Ustring + "]");  //보낼주소추가
                        Toast.makeText(registration.this, "", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        });
        //회원가입 입력 중 비밀번호, 비밀번호 재입력 두개의 비밀번호가 일치하는지 확인하는 버튼
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //비밀번호,재입력 둘다 빈칸이 아닐 때
                if((et_pw.getText().toString().getBytes().length>0)&&(et_c_pw.getText().toString().getBytes().length>0)) {
                    if (et_pw.getText().toString().equals(et_c_pw.getText().toString()))
                        Toast.makeText(registration.this, "correct!", Toast.LENGTH_SHORT).show();
                    else if(et_pw.getText().toString()!=et_c_pw.getText().toString())
                        Toast.makeText(registration.this, "not correct!", Toast.LENGTH_SHORT).show();
                }
                //비밀번호, 재입력 중 하나라도 입력값이 없으면 채우라는 메세지가 뜸
                else
                    Toast.makeText(registration.this, "please fill out", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
            init();

        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}

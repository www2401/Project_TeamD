package com.example.ilove.teamd;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class registration extends AppCompatActivity {
    public Button bt1;
    public Button bt2;
    public EditText editText7 ,editText11;

    public void init() {
        bt1=(Button)findViewById(R.id.button);
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(registration.this, "password confirm", Toast.LENGTH_SHORT).show();
            }
        });
        bt2 = (Button) findViewById(R.id.button1);
        editText7 = (EditText)findViewById(R.id.editText7);
        editText11 = (EditText)findViewById(R.id.editText11);

        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                JsonTransfer userdata_transfer = new JsonTransfer();

                try
                {
                    JSONObject json_UserdataTransfer = new JSONObject();  //JSONObject는 JSON을 만들기 위함.
                    //json_dataTransfer에 ("키값" : "보낼데이터") 형식으로 저장한다.
                    json_UserdataTransfer.put("email", editText7.getText().toString());
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
                    userdata_transfer.execute("http://teamd-iot.calit2.net/finally/slim-api/signtest","["+json_Ustring+"]");  //보낼주소추가

                } catch (Exception e) {
                    Log.d("error :", e.toString());
                    e.printStackTrace();
                }


                Intent page = new Intent(registration.this, registration_authentification.class);
                startActivity(page);
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

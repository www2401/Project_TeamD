package com.example.ilove.teamd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.example.ilove.teamd.userfage.login;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class graph extends AppCompatActivity {

    NumberPicker np_m,np_d,np_y;
    LineChart co,o3,no2,so2,pm;
    Button bt1;
    public float apm[]=new float[8];
    public float aco[]=new float[8];
    public int aso2[]=new int[8];
    public int ano2[]=new int[8];
    public double ao3[]=new double[8];
    int s,n;
    float p,c;
    double o;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        // chart
        co= (LineChart)findViewById(R.id.chart_co);
        o3= (LineChart)findViewById(R.id.chart_o3);
        no2= (LineChart)findViewById(R.id.chart_no2);
        so2 = (LineChart)findViewById(R.id.chart_so2);
        pm = (LineChart)findViewById(R.id.chart_pm);
        bt1=(Button)findViewById(R.id.bt_select);
        //number picker-월
        np_m = (NumberPicker) findViewById(R.id.np_m);
        np_m.setMinValue(1);
        np_m.setMaxValue(1);
        np_m.setMaxValue(12);
        np_m.setValue(1);
        np_m.setWrapSelectorWheel(false);
        //number picker-일
        np_d= (NumberPicker) findViewById(R.id.np_d);
        np_d.setMinValue(1);
        np_d.setMaxValue(1);
        np_d.setMaxValue(31);
        np_d.setValue(1);
        np_d.setWrapSelectorWheel(false);
        //number picker-년도
        np_y = (NumberPicker) findViewById(R.id.np_y);
        np_y.setMinValue(2015);
        np_y.setMaxValue(2015);
        np_y.setMaxValue(2017);
        np_y.setValue(2015);
        np_y.setWrapSelectorWheel(false);
        //select를 클릭했을 때
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for(int i=0;i<apm.length;++i) {
                    //pm2.5
                    apm[i]=(float)(Math.random()*55)+20;
                    //co
                    aco[i]=(float)(Math.random()*14)+3;
                    //so2
                    aso2[i]=(int)(Math.random()*100)+35;
                    //03
                    ao3[i]=(float)(Math.random()*0.08)+0.03;
                    //No2
                    ano2[i]=(int)(Math.random()*200)+40;
                }
                    try {

                            //각 entry에 값을 주는거
                            ArrayList<Entry> entry_co = new ArrayList<Entry>();
                            ArrayList<Entry> entry_o3 = new ArrayList<Entry>();
                            ArrayList<Entry> entry_no2 = new ArrayList<Entry>();
                            ArrayList<Entry> entry_so2 = new ArrayList<Entry>();
                            ArrayList<Entry> entry_pm = new ArrayList<Entry>();

                        for(int i=0;i<8;++i){
                            s=aso2[i];
                            n=ano2[i];
                            p=apm[i];
                            c=aco[i];
                            o=ao3[i];
                            entry_co.add(new Entry(c,i));
                            entry_o3.add(new Entry((float)o,i));
                            entry_no2.add(new Entry(n,i));
                            entry_so2.add(new Entry(s,i));
                            entry_pm.add(new Entry(p,i));
                        }
                            //각 entry이름을 명칭해주는거
                            LineDataSet data_co = new LineDataSet(entry_co, "CO");
                            LineDataSet data_o3 = new LineDataSet(entry_o3, "O3");
                            LineDataSet data_no2 = new LineDataSet(entry_no2, "NO2");
                            LineDataSet data_so2 = new LineDataSet(entry_so2, "SO2");
                            LineDataSet data_pm = new LineDataSet(entry_pm, "PM-2.5");

                            data_co.setAxisDependency(YAxis.AxisDependency.LEFT); //데이터들어올때 마다 왼쪽으로 들어오게 함

                            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();//CO에 대한 데이터 저장 공간
                            ArrayList<LineDataSet> dataSets2 = new ArrayList<LineDataSet>();//O3에 대한 데이터 저장 공간
                            ArrayList<LineDataSet> dataSets3 = new ArrayList<LineDataSet>();//NO2에 대한 데이터 저장 공간
                            ArrayList<LineDataSet> dataSets4 = new ArrayList<LineDataSet>();//SO2에 대한 데이터 저장 공간
                            ArrayList<LineDataSet> dataSets5 = new ArrayList<LineDataSet>();//PM-2.5에 대한 데이터 저장 공간
                            dataSets.add(data_co);
                            dataSets2.add(data_o3);
                            dataSets3.add(data_no2);
                            dataSets4.add(data_so2);
                            dataSets5.add(data_pm);

                            //chart의 가로 수
                            ArrayList<String> xVals = new ArrayList<String>();
                            xVals.add("1");
                            xVals.add("2");
                            xVals.add("3");
                            xVals.add("4");
                            xVals.add("5");
                            xVals.add("6");
                            xVals.add("7");
                            xVals.add("8");

                            //CO
                            LineData data1 = new LineData(xVals,dataSets);
                            co.setData(data1);
                            co.invalidate();
                            data_co.setColor(Color.parseColor("#FFA7A7"));

                            //O3
                            LineData data2 = new LineData(xVals,dataSets2);
                            o3.setData(data2);
                            o3.invalidate();
                            data_o3.setColor(Color.parseColor("#FFE08C"));

                            //NO2
                            LineData data3 = new LineData(xVals,dataSets3);
                            no2.setData(data3);
                            no2.invalidate();
                            data_no2.setColor(Color.parseColor("#CEF279"));

                            //SO2
                            LineData data4 = new LineData(xVals,dataSets4);
                            so2.setData(data4);
                            so2.invalidate();
                            data_so2.setColor(Color.parseColor("#B2EBF4"));

                            //PM-2.5
                            LineData data5 = new LineData(xVals,dataSets5);
                            pm.setData(data5);
                            pm.invalidate();
                            data_pm.setColor(Color.parseColor("#B5B2FF"));

                    }
                    catch (Exception e){

                    }
                }
        });
    }

}

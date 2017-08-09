package com.example.ilove.teamd;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class graph extends AppCompatActivity {

    NumberPicker nPickerYEAR;
    NumberPicker nPickerMONTH;
    NumberPicker nPickerDAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // chart
        LineChart chart1 = (LineChart)findViewById(R.id.chart1);
        LineChart chart2 = (LineChart)findViewById(R.id.chart2);
        LineChart chart3 = (LineChart)findViewById(R.id.chart3);
        LineChart chart4 = (LineChart)findViewById(R.id.chart4);
        LineChart chart5 = (LineChart)findViewById(R.id.chart5);

        //각 entry에 값을 주는거
        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp3 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp4 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp5 = new ArrayList<Entry>();

        //fake data
        valsComp1.add(new Entry(0,0));
        valsComp2.add(new Entry(0,0));
        valsComp3.add(new Entry(0,0));
        valsComp4.add(new Entry(0,0));
        valsComp5.add(new Entry(0,0));

        //각 entry이름을 명칭해주는거
        LineDataSet setComp1 = new LineDataSet(valsComp1, "CO");
        LineDataSet setComp2 = new LineDataSet(valsComp2, "O3");
        LineDataSet setComp3 = new LineDataSet(valsComp3, "NO2");
        LineDataSet setComp4 = new LineDataSet(valsComp4, "SO2");
        LineDataSet setComp5 = new LineDataSet(valsComp5, "PM-2.5");

        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        //CO에 대한 데이터 저장 공간
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        //O3에 대한 데이터 저장 공간
        ArrayList<LineDataSet> dataSets2 = new ArrayList<LineDataSet>();
        dataSets2.add(setComp2);

        //NO2에 대한 데이터 저장 공간
        ArrayList<LineDataSet> dataSets3 = new ArrayList<LineDataSet>();
        dataSets3.add(setComp3);

        //SO2에 대한 데이터 저장 공간
        ArrayList<LineDataSet> dataSets4 = new ArrayList<LineDataSet>();
        dataSets4.add(setComp4);
        
        //PM-2.5에 대한 데이터 저장 공간
        ArrayList<LineDataSet> dataSets5 = new ArrayList<LineDataSet>();
        dataSets5.add(setComp5);

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
        chart1.setData(data1);
        chart1.invalidate();
        setComp1.setColor(Color.parseColor("#FFA7A7"));

        //O3
        LineData data2 = new LineData(xVals,dataSets2);
        chart2.setData(data2);
        chart2.invalidate();
        setComp2.setColor(Color.parseColor("#FFE08C"));

        //NO2
        LineData data3 = new LineData(xVals,dataSets3);
        chart3.setData(data3);
        chart3.invalidate();
        setComp3.setColor(Color.parseColor("#CEF279"));

        //SO2
        LineData data4 = new LineData(xVals,dataSets4);
        chart4.setData(data4);
        chart4.invalidate();
        setComp4.setColor(Color.parseColor("#B2EBF4"));

        //PM-2.5
        LineData data5 = new LineData(xVals,dataSets5);
        chart5.setData(data5);
        chart5.invalidate();
        setComp5.setColor(Color.parseColor("#B5B2FF"));

        //number picker-월
        nPickerMONTH = (NumberPicker) findViewById(R.id.numberPickerMONTH);
        nPickerMONTH.setMinValue(1);
        nPickerMONTH.setMaxValue(1);
        nPickerMONTH.setMaxValue(12);
        nPickerMONTH.setValue(1);
        nPickerMONTH.setWrapSelectorWheel(false);

        //number picker-일
        nPickerDAY = (NumberPicker) findViewById(R.id.numberPickerDAY);
        nPickerDAY.setMinValue(1);
        nPickerDAY.setMaxValue(1);
        nPickerDAY.setMaxValue(31);
        nPickerDAY.setValue(1);
        nPickerDAY.setWrapSelectorWheel(false);

        //number picker-년도
        nPickerYEAR = (NumberPicker) findViewById(R.id.numberPickerYEAR);
        nPickerYEAR.setMinValue(2015);
        nPickerYEAR.setMaxValue(2015);
        nPickerYEAR.setMaxValue(2017);
        nPickerYEAR.setValue(2015);
        nPickerYEAR.setWrapSelectorWheel(false);

    }
}

package com.example.ilove.teamd;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class graph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        LineChart chart1 = (LineChart)findViewById(R.id.chart1);
        LineChart chart2 = (LineChart)findViewById(R.id.chart2);
        LineChart chart3 = (LineChart)findViewById(R.id.chart3);
        LineChart chart4 = (LineChart)findViewById(R.id.chart4);
        LineChart chart5 = (LineChart)findViewById(R.id.chart5);

        chart1.setNoDataText("Chart for CO here.");
        chart2.setNoDataText("Chart for O3 here.");
        chart3.setNoDataText("Chart for SO2 here.");
        chart4.setNoDataText("Chart for NO2 here.");
        chart5.setNoDataText("Chart for PM2.5 here.");


        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp3 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp4 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp5 = new ArrayList<Entry>();
        // ArrayList<Entry> valsComp6 = new ArrayList<Entry>();

        valsComp1.add(new Entry(0f,0));
        valsComp2.add(new Entry(0f,1));
        valsComp3.add(new Entry(0f,2));
        valsComp4.add(new Entry(0f,3));
        valsComp5.add(new Entry(0f,4));
        // valsComp6.add(new Entry(0f,5));


        LineDataSet setComp1 = new LineDataSet(valsComp1, "CO");
        LineDataSet setComp2 = new LineDataSet(valsComp2, "O3");
        LineDataSet setComp3 = new LineDataSet(valsComp3, "NO2");
        LineDataSet setComp4 = new LineDataSet(valsComp4, "SO2");
        LineDataSet setComp5 = new LineDataSet(valsComp5, "PM-2.5");


        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);
        /* dataSets.add(setComp2);
        dataSets.add(setComp3);
        dataSets.add(setComp4);
        dataSets.add(setComp5);
        dataSets.add(setComp6); */

        ArrayList<LineDataSet> dataSets2 = new ArrayList<LineDataSet>();
        dataSets2.add(setComp2);

        ArrayList<LineDataSet> dataSets3 = new ArrayList<LineDataSet>();
        dataSets3.add(setComp3);

        ArrayList<LineDataSet> dataSets4 = new ArrayList<LineDataSet>();
        dataSets4.add(setComp4);

        ArrayList<LineDataSet> dataSets5 = new ArrayList<LineDataSet>();
        dataSets5.add(setComp5);


        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1");
        xVals.add("2");
        xVals.add("3");
        xVals.add("4");
        xVals.add("5");
        xVals.add("6");
        xVals.add("7");
        xVals.add("8");

        LineData data1 = new LineData(xVals,dataSets);
        chart1.setData(data1);
        chart1.invalidate();
        setComp1.setColor(Color.parseColor("#FFA7A7"));

        LineData data2 = new LineData(xVals,dataSets2);
        chart2.setData(data2);
        chart2.invalidate();
        setComp2.setColor(Color.parseColor("#FFE08C"));

        LineData data3 = new LineData(xVals,dataSets3);
        chart3.setData(data3);
        chart3.invalidate();
        setComp3.setColor(Color.parseColor("#CEF279"));

        LineData data4 = new LineData(xVals,dataSets4);
        chart4.setData(data4);
        chart4.invalidate();
        setComp4.setColor(Color.parseColor("#B2EBF4"));

        LineData data5 = new LineData(xVals,dataSets5);
        chart5.setData(data5);
        chart5.invalidate();
        setComp5.setColor(Color.parseColor("#B5B2FF"));

    }
}

package com.example.ilove.teamd;

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
        LineChart chart6 = (LineChart)findViewById(R.id.chart6);

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp3 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp4 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp5 = new ArrayList<Entry>();
        ArrayList<Entry> valsComp6 = new ArrayList<Entry>();

        valsComp1.add(new Entry(0f,0));
        valsComp2.add(new Entry(0f,1));
        valsComp3.add(new Entry(0f,2));
        valsComp4.add(new Entry(0f,3));
        valsComp5.add(new Entry(0f,4));
        valsComp6.add(new Entry(0f,5));


        LineDataSet setComp1 = new LineDataSet(valsComp1, "CO");
        LineDataSet setComp2 = new LineDataSet(valsComp2, "O3");
        LineDataSet setComp3 = new LineDataSet(valsComp3, "NO2");
        LineDataSet setComp4 = new LineDataSet(valsComp4, "SO2");
        LineDataSet setComp5 = new LineDataSet(valsComp5, "PM-2.5");
        LineDataSet setComp6 = new LineDataSet(valsComp6, "PM-10");

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

        ArrayList<LineDataSet> dataSets6 = new ArrayList<LineDataSet>();
        dataSets6.add(setComp6);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1,Q");
        xVals.add("2,Q");
        xVals.add("3,Q");
        xVals.add("4,Q");

        LineData data1 = new LineData(xVals,dataSets);

        chart1.setData(data1);
        chart1.invalidate();

        LineData data2 = new LineData(xVals,dataSets2);
        chart2.setData(data2);
        chart2.invalidate();

        LineData data3 = new LineData(xVals,dataSets3);
        chart3.setData(data3);
        chart3.invalidate();

        LineData data4 = new LineData(xVals,dataSets4);
        chart4.setData(data4);
        chart4.invalidate();

        LineData data5 = new LineData(xVals,dataSets5);
        chart5.setData(data5);
        chart5.invalidate();

        LineData data6 = new LineData(xVals,dataSets6);
        chart6.setData(data6);
        chart6.invalidate();
    }
}

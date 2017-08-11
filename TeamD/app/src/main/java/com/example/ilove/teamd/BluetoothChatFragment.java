/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.ilove.teamd;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {

    public LineChart chart;
    public ArrayList<String> axVals = new ArrayList<String>();

    public ArrayList<Entry> covalue = new ArrayList<Entry>();
    public ArrayList<Entry> no2value = new ArrayList<Entry>();
    public ArrayList<Entry> so2value = new ArrayList<Entry>();
    public ArrayList<Entry> o3value = new ArrayList<Entry>();
    public ArrayList<Entry> pm25value = new ArrayList<Entry>();

    View view;

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private EditText mOutEditText;

    static public TextView temp;
    static public TextView co_air;
    static public TextView o3_air;
    static public TextView so2_air;
    static public TextView no2_air;
    static public TextView pm25_air;

    public float CO, NO2, SO2, O3, PM25, TEM  = 0;
    public float co_avg, no2_avg, so2_avg, o3_avg, pm25_avg = 0;
    public float co_old, no2_old, so2_old, o3_old, pm25_old = 0;
    public float CO_AQI, NO2_AQI, SO2_AQI, O3_AQI, PM25_AQI = 0;

    public static ArrayList<Float> co_bufferArrayList = new ArrayList<Float>();
    public static ArrayList<Float> no2_bufferArrayList = new ArrayList<Float>();
    public static ArrayList<Float> so2_bufferArrayList = new ArrayList<Float>();
    public static ArrayList<Float> o3_bufferArrayList = new ArrayList<Float>();
    public static ArrayList<Float> pm25_bufferArrayList = new ArrayList<Float>();

    int count = -1;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */

    private Handler mTimerHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }else if(mBluetoothAdapter != null)
        {
            TimerTask historyTask = new TimerTask() {
                @Override
                public void run() {
                    mTimerHandler.post(new Runnable(){
                        public void run(){
                            String now_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
                            SimpleDateFormat now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            java.util.Date now_date = null;
                            java.util.Date old_date = null;
                            try {
                                now_date = now.parse(now_time);
                                old_date = now.parse(now_time);
                            }catch (ParseException e){
                                e.printStackTrace();
                            }
                            old_date.setMinutes(old_date.getMinutes()-3);
                            /*
                            Calendar cal;
                            cal = now.getCalendar();
                            cal.add(Calendar.MINUTE,-3);*/
                            long now_epoch = now_date.getTime();
                            long old_epoch = old_date.getTime();
                            String now_epoch_time = String.format("%10d", now_epoch);
                            now_epoch_time = now_epoch_time.substring(0,10);
                            String old_epoch_time = String.format("%10d", old_epoch);
                            old_epoch_time = old_epoch_time.substring(0,10);
                            sendMessage("history " + old_epoch_time + " " + now_epoch_time + "\n");
                        }
                    });
                }
            };
            Timer timer = new Timer();
            timer.schedule(historyTask,0,3*60000);
            };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize the BluetoothChatService to perform bluetooth connections
        //AppController.getinstance().mChatService = new BluetoothChatService(getActivity(), mHandler);
        AppController.getinstance().mChatService.addFragHandler(mHandler);
        view = inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);

        temp = (TextView)view.findViewById(R.id.temptextview);
        co_air = (TextView)view.findViewById(R.id.COtextview);
        o3_air = (TextView)view.findViewById(R.id.O3textview);
        so2_air = (TextView)view.findViewById(R.id.SO2textview);
        no2_air = (TextView)view.findViewById(R.id.NO2textview);
        pm25_air = (TextView)view.findViewById(R.id.PM25textview);

        chart = (LineChart)view.findViewById(R.id.allchart); //차트만듦
        chart.setNoDataText("Chart for all the airquality data.");

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (AppController.getinstance().mChatService == null) {
            //setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
        if (AppController.getinstance().mChatService != null) {
            AppController.getinstance().mChatService.stop();
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (AppController.getinstance().mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (AppController.getinstance().mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                AppController.getinstance().mChatService.start();
            }
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }
    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (AppController.getinstance().mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            AppController.getinstance().mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    // private String [] air_info_split = null;  에어값 나눠주는거

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;


                case Constants.MESSAGE_WRITE:    // 여기서 csv 파일 보내줌
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;


                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //temp.setText(readMessage);

                    Log.w("BT Chat Frag", "Total bytes received: "+readBuf.length);
                    String form_type = readMessage.substring(0,1);
                    String startMessage = readMessage.substring(1);

                    //Toast.makeText(getContext(),form_type,Toast.LENGTH_SHORT).show();
                    if(form_type.equals("h"))
                    {
                        Toast.makeText(getContext(),"history",Toast.LENGTH_SHORT).show();
                            /*  CSV 형식으로 파일 받아올때
                            air_info_split = readMessage.split(",");

                            for(int i=2;i<air_info_split.length;i++)
                            {
                                if(i==2)
                                {
                                    temp.setText(air_info_split[i]);
                                }
                                if(i==3)
                                {
                                    test.setText(air_info_split[i]);
                                }
                                if(i==4)
                                {
                                    test1.setText(air_info_split[i]);
                                }
                                if(i==5)
                                {
                                    test2.setText(air_info_split[i]);
                                }
                                if(i==6)
                                {
                                    test3.setText(air_info_split[i]);
                                }
                                if(i==7)
                                {
                                    test4.setText(air_info_split[i]);
                                }
                            }
                            */
                    }
                    else if(form_type.equals("r"))
                    {
                        //Toast.makeText(getContext(),"realtime",Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject JsonAir = new JSONObject(startMessage);

                            CO = JsonAir.getInt("CO");
                            NO2 = JsonAir.getInt("NO2");
                            SO2 = JsonAir.getInt("SO2");
                            O3 = JsonAir.getInt("O3");
                            PM25 = JsonAir.getInt("PM25");

                            co_bufferArrayList.add(CO);
                            no2_bufferArrayList.add(NO2);
                            so2_bufferArrayList.add(SO2);
                            o3_bufferArrayList.add(O3);
                            pm25_bufferArrayList.add(PM25);

                            CO_AQI = calcurate_co_aqi(calcurate_co_avg());
                            SO2_AQI = calcurate_so2_aqi(calcurate_so2_avg());
                            NO2_AQI = calcurate_no2_aqi(calcurate_no2_avg());
                            O3_AQI = calcurate_o3_aqi(calcurate_o3_avg());
                            PM25_AQI = calcurate_pm25_aqi(calcurate_pm25_avg());

                            /*
                            float co_aqi = calcurate_co_aqi(CO);
                            float no2_aqi = calcurate_no2_aqi(NO2);
                            float so2_aqi = calcurate_so2_aqi(SO2);
                            float o3_aqi = calcurate_o3_aqi(O3);
                            float pm25_aqi = calcurate_pm25_aqi(PM25);
                            */

                            temp.setText(JsonAir.getString("temp"));
                            co_air.setText(String.valueOf(CO_AQI));   //toString 이 뭔가를 String으로 바꿔주는거
                            o3_air.setText(String.valueOf(O3_AQI));
                            so2_air.setText(String.valueOf(SO2_AQI));   //toString 이 뭔가를 String으로 바꿔주는거
                            no2_air.setText(String.valueOf(NO2_AQI));
                            pm25_air.setText(String.valueOf(PM25_AQI));


                            JsonTransfer airdata_transfer = new JsonTransfer();

                            JSONObject json_AirdataTransfer = new JSONObject();  //JSONObject는 JSON을 만들기 위함.

                            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

                        /*
                        json_AirdataTransfer.put("tid", "11");
                        json_AirdataTransfer.put("time", "00:00:00");
                        json_AirdataTransfer.put("type", "asdf");
                        json_AirdataTransfer.put("CO",JsonAir.getString("CO"));
                        json_AirdataTransfer.put("O3",JsonAir.getString("O3"));
                        json_AirdataTransfer.put("SO2",JsonAir.getString("SO2"));
                        json_AirdataTransfer.put("NO2",JsonAir.getString("NO2"));
                        json_AirdataTransfer.put("PM25",JsonAir.getString("PM25"));
                        */

                            json_AirdataTransfer.put("uid",13);
                            json_AirdataTransfer.put("atime",time);
                            json_AirdataTransfer.put("CO",JsonAir.getString("CO"));
                            json_AirdataTransfer.put("O3",JsonAir.getString("O3"));
                            json_AirdataTransfer.put("SO2",JsonAir.getString("SO2"));
                            json_AirdataTransfer.put("PM25",JsonAir.getString("PM25"));
                            json_AirdataTransfer.put("NO2",JsonAir.getString("NO2"));
                            json_AirdataTransfer.put("temp",JsonAir.getString("temp"));
                            json_AirdataTransfer.put("latitude",GPSlocation.lat);
                            json_AirdataTransfer.put("longitude",GPSlocation.lng);
                            //json_AirdataTransfer.put("latitude",String.valueOf(GPSlocation.latLng.latitude));
                            //json_AirdataTransfer.put("longitude",String.valueOf(GPSlocation.latLng.longitude));
                            json_AirdataTransfer.put("wifi_connection",0);

                            // o3_one_min.toString(); 어레이 전체 값을 스트링으로 변환 해서 토스트 값 확인 할 수 있음

                            aqi((int)CO_AQI,(int)NO2_AQI,(int)SO2_AQI,(int)O3_AQI,(int)PM25_AQI);
                            setData();

                            //json_dataTransfer의 데이터들을 하나의 json_string으로 묶는다.
                            String json_Astring = json_AirdataTransfer.toString();

                            airdata_transfer.execute("http://teamd-iot.calit2.net/finally/slim-api/air_data_app","["+json_Astring+"]");
                            // airdata_transfer.execute("http://teama-iot.calit2.net/slim-api/receive-air-data","["+json_Astring+"]");

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }

                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        AppController.getinstance().mChatService.connect(device, secure);
    }

    void setText1(String s){
        try {
            if (temp != null) {
                temp.setText(s);
                Log.v("ttt","ttttt"+temp.getText());

            }
        }catch (Exception e){
            e.printStackTrace();
            Log.v("eror", "salkdjflkd");
        }
    }

    public void setData() {

        count++;

        //ArrayList<String> xVals = new ArrayList<String>(); //x축세팅 위로올림
        axVals.add(String.valueOf(count));


        //ArrayList<Entry> covalue = new ArrayList<Entry>(); //y축세팅 위로올림
        covalue.add(new Entry(CO, count));
        so2value.add(new Entry(SO2, count));
        no2value.add(new Entry(NO2, count));
        o3value.add(new Entry(O3, count));
        pm25value.add(new Entry(PM25, count));

        LineDataSet cochart = new LineDataSet(covalue, "CO");
        LineDataSet so2chart = new LineDataSet(so2value, "SO2");
        LineDataSet no2chart = new LineDataSet(no2value, "NO2");
        LineDataSet o3chart = new LineDataSet(o3value, "O3");
        LineDataSet pm25chart = new LineDataSet(pm25value, "PM25");

        //라인색변경
        cochart.setColor(Color.parseColor("#FFA7A7"));
        so2chart.setColor(Color.parseColor("#CEF279"));
        no2chart.setColor(Color.parseColor("#B2EBF4"));
        o3chart.setColor(Color.parseColor("#FFE08C"));
        pm25chart.setColor(Color.parseColor("#B5B2FF"));

        cochart.setAxisDependency(YAxis.AxisDependency.LEFT); //creat lineDataSet
        so2chart.setAxisDependency(YAxis.AxisDependency.LEFT);
        no2chart.setAxisDependency(YAxis.AxisDependency.LEFT);
        o3chart.setAxisDependency(YAxis.AxisDependency.LEFT);
        pm25chart.setAxisDependency(YAxis.AxisDependency.LEFT);

        cochart.setDrawValues(false);
        so2chart.setDrawValues(false);
        no2chart.setDrawValues(false);
        o3chart.setDrawValues(false);
        pm25chart.setDrawValues(false);
        chart.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false); //오른쪽 숫자 없앰
        chart.setDescription(null); //Description text 없앰
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition( XAxis.XAxisPosition. BOTTOM ); //x축 아래로 내림
        xAxis.setDrawGridLines(true);
        chart.setNoDataText("Chart for Air Quality here.");
        chart.setVisibleXRangeMaximum(5);
        chart.moveViewToX(count);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

        dataSets.add(cochart);
        dataSets.add(so2chart);
        dataSets.add(no2chart);
        dataSets.add(o3chart);
        dataSets.add(pm25chart);


        LineData codata = new LineData(axVals, dataSets);
        LineData so2data = new LineData(axVals, dataSets);
        LineData no2data = new LineData(axVals, dataSets);
        LineData o3data = new LineData(axVals, dataSets);
        LineData pm25data = new LineData(axVals, dataSets);

        chart.setData(codata);
        chart.setData(so2data);
        chart.setData(no2data);
        chart.setData(o3data);
        chart.setData(pm25data);

        chart.invalidate();//  dont forget to refresh the drawing
    }


    public void aqi(int CO, int NO2, int SO2, int O3, int PM25){
        if(CO >= 0 && CO <= 50)
            co_air.setBackgroundResource(R.drawable.g_co);
        if(CO >= 51 && CO <= 100)
            co_air.setBackgroundResource(R.drawable.y_co);
        if(CO >= 101 && CO <= 150)
            co_air.setBackgroundResource(R.drawable.o_co);
        if(CO >= 151 && CO <= 200)
            co_air.setBackgroundResource(R.drawable.r_co);
        if(CO >= 201 && CO <= 300)
            co_air.setBackgroundResource(R.drawable.p_co);
        if(CO >= 301 && CO <= 400)
            co_air.setBackgroundResource(R.drawable.b_co);
        if(CO >= 401 && CO <= 500)
            co_air.setBackgroundResource(R.drawable.b_co);

        if(NO2 >= 0 && NO2 <= 50)
            no2_air.setBackgroundResource(R.drawable.g_no2);
        if(NO2 >= 51 && NO2 <= 100)
            no2_air.setBackgroundResource(R.drawable.y_no2);
        if(NO2 >= 101 && NO2 <= 150)
            no2_air.setBackgroundResource(R.drawable.o_no2);
        if(NO2 >= 151 && NO2 <= 200)
            no2_air.setBackgroundResource(R.drawable.r_no2);
        if(NO2 >= 201 && NO2 <= 300)
            no2_air.setBackgroundResource(R.drawable.p_no2);
        if(NO2 >= 301 && NO2 <= 400)
            no2_air.setBackgroundResource(R.drawable.b_no2);
        if(NO2 >= 401 && NO2 <= 500)
            no2_air.setBackgroundResource(R.drawable.b_no2);

        if(SO2 >= 0 && SO2 <= 50)
            so2_air.setBackgroundResource(R.drawable.g_so2);
        if(SO2 >= 51 && SO2 <= 100)
            so2_air.setBackgroundResource(R.drawable.y_so2);
        if(SO2 >= 101 && SO2 <= 150)
            so2_air.setBackgroundResource(R.drawable.o_so2);
        if(SO2 >= 151 && SO2 <= 200)
            so2_air.setBackgroundResource(R.drawable.r_so2);
        if(SO2 >= 201 && SO2 <= 300)
            so2_air.setBackgroundResource(R.drawable.p_so2);
        if(SO2 >= 301 && SO2 <= 400)
            so2_air.setBackgroundResource(R.drawable.b_so2);
        if(SO2 >= 401 && SO2 <= 500)
            so2_air.setBackgroundResource(R.drawable.b_so2);


        if(O3 >= 0 && O3 <= 50)
            o3_air.setBackgroundResource(R.drawable.g_o3);
        if(O3 >= 51 && O3 <= 100)
            o3_air.setBackgroundResource(R.drawable.y_o3);
        if(O3 >= 101 && O3 <= 150)
            o3_air.setBackgroundResource(R.drawable.o_o3);
        if(O3 >= 151  && O3 <= 200)
            o3_air.setBackgroundResource(R.drawable.r_o3);
        if(O3 >= 201 && O3 <= 300)
            o3_air.setBackgroundResource(R.drawable.p_o3);
        if(O3 >= 301 && O3 <= 400)
            o3_air.setBackgroundResource(R.drawable.b_o3);
        if(O3 >= 401 && O3 <= 500)
            o3_air.setBackgroundResource(R.drawable.b_o3);

        if(PM25 >=0 && PM25 <=50)
            pm25_air.setBackgroundResource(R.drawable.g_pm25);
        if(PM25 >= 51 && PM25 <= 100)
            pm25_air.setBackgroundResource(R.drawable.y_pm25);
        if(PM25 >= 101 && PM25 <= 150)
            pm25_air.setBackgroundResource(R.drawable.o_pm25);
        if(PM25 >= 151 && PM25 <= 200)
            pm25_air.setBackgroundResource(R.drawable.r_pm25);
        if(PM25 >= 201 && PM25 <= 300)
            pm25_air.setBackgroundResource(R.drawable.p_pm25);
        if(PM25 >= 301 && PM25 <= 400)
            pm25_air.setBackgroundResource(R.drawable.b_pm25);
        if(PM25 >= 401 && PM25 <= 500)
            pm25_air.setBackgroundResource(R.drawable.b_pm25);
    }

    // AQI 데이터 계산
    public float calcurate_co_aqi(float CO){
        float co_Aqi=0;
        float C_hight=0;
        float C_low=0;
        float I_hight=0;
        float I_low=0;
        float C = CO;
        float I=0;
        if(CO >= 0.0 && CO <= 4.4){
            C_hight = (float)4.4;
            C_low = (float)0;
            I_hight = (float)50;
            I_low = (float)0;
        }
        if(CO >= 4.5 && CO <= 9.4){
            C_hight = (float)9.4;
            C_low = (float)4.5;
            I_hight = (float)100;
            I_low = (float)51;
        }
        if(CO >= 9.5 && CO <= 12.4){
            C_hight = (float)12.4;
            C_low = (float)9.5;
            I_hight = (float)150;
            I_low = (float)101;
        }
        if(CO >= 12.5 && CO <= 15.4){
            C_hight = (float)15.4;
            C_low = (float)12.5;
            I_hight = (float)200;
            I_low = (float)151;
        }
        if(CO >= 15.5 && CO <= 30.4){
            C_hight = (float)30.4;
            C_low = (float)15.5;
            I_hight = (float)300;
            I_low = (float)201;
        }
        if(CO >= 30.5 && CO <= 40.4){
            C_hight = (float)40.4;
            C_low = (float)30.5;
            I_hight = (float)400;
            I_low = (float)301;
        }
        if(CO >= 40.5 && CO <= 50.4){
            C_hight = (float)50.4;
            C_low = (float)40.5;
            I_hight = (float)500;
            I_low = (float)401;
        }

        co_Aqi = ((I_hight-I_low)/(C_hight-C_low))*(C-C_low)+I_low;
        return co_Aqi;
    }

    public float calcurate_so2_aqi(float SO2){
        float so2_Aqi=0;
        float C_hight=0;
        float C_low=0;
        float I_hight=0;
        float I_low=0;
        float C = SO2;
        float I=0;
        if(SO2 >= 0 && SO2 <= 35){
            C_hight = (float)35;
            C_low = (float)0;
            I_hight = (float)50;
            I_low = (float)0;
        }
        if(SO2 >= 36 && SO2 <= 75){
            C_hight = (float)75;
            C_low = (float)36;
            I_hight = (float)100;
            I_low = (float)51;
        }
        if(SO2 >= 76 && SO2 <= 185){
            C_hight = (float)185;
            C_low = (float)76;
            I_hight = (float)150;
            I_low = (float)101;
        }
        if(SO2 >= 186 && SO2 <= 304){
            C_hight = (float)304;
            C_low = (float)186;
            I_hight = (float)200;
            I_low = (float)151;
        }
        if(SO2 >= 305 && SO2 <= 604){
            C_hight = (float)604;
            C_low = (float)305;
            I_hight = (float)300;
            I_low = (float)201;
        }
        if(SO2 >= 605 && SO2 <= 804){
            C_hight = (float)804;
            C_low = (float)605;
            I_hight = (float)400;
            I_low = (float)301;
        }
        if(SO2 >= 805 && SO2 <= 1004){
            C_hight = (float)1004;
            C_low = (float)805;
            I_hight = (float)500;
            I_low = (float)401;
        }

        so2_Aqi = ((I_hight-I_low)/(C_hight-C_low))*(C-C_low)+I_low;
        return so2_Aqi;
    }

    public float calcurate_no2_aqi(float NO2){
        float no2_Aqi=0;
        float C_hight=0;
        float C_low=0;
        float I_hight=0;
        float I_low=0;
        float C = NO2;
        float I=0;
        if(NO2 >= 0 && NO2 <= 53){
            C_hight = (float)53;
            C_low = (float)0;
            I_hight = (float)50;
            I_low = (float)0;
        }
        if(NO2 >= 54 && NO2 <= 100){
            C_hight = (float)100;
            C_low = (float)54;
            I_hight = (float)100;
            I_low = (float)51;
        }
        if(NO2 >= 101 && NO2 <= 360){
            C_hight = (float)360;
            C_low = (float)101;
            I_hight = (float)150;
            I_low = (float)101;
        }
        if(NO2 >= 361 && NO2 <= 649){
            C_hight = (float)649;
            C_low = (float)361;
            I_hight = (float)200;
            I_low = (float)151;
        }
        if(NO2 >= 650 && NO2 <= 1249){
            C_hight = (float)1249;
            C_low = (float)650;
            I_hight = (float)300;
            I_low = (float)201;
        }
        if(NO2 >= 1250 && NO2 <= 1649){
            C_hight = (float)1649;
            C_low = (float)1250;
            I_hight = (float)400;
            I_low = (float)301;
        }
        if(NO2 >= 1650 && NO2 <= 2049){
            C_hight = (float)2049;
            C_low = (float)1650;
            I_hight = (float)500;
            I_low = (float)401;
        }

        no2_Aqi = ((I_hight-I_low)/(C_hight-C_low))*(C-C_low)+I_low;
        return no2_Aqi;
    }

    public float calcurate_o3_aqi(float O3){
        float o3_Aqi=0;
        float C_hight=0;
        float C_low=0;
        float I_hight=0;
        float I_low=0;
        float C = O3;
        float I=0;
        if(O3 >= 0 && O3 <= 54){
            C_hight = (float)54;
            C_low = (float)0;
            I_hight = (float)50;
            I_low = (float)0;
        }
        if(O3 >= 55 && O3 <= 70){
            C_hight = (float)70;
            C_low = (float)55;
            I_hight = (float)100;
            I_low = (float)51;
        }
        if(O3 >= 71 && O3 <= 85){
            C_hight = (float)85;
            C_low = (float)71;
            I_hight = (float)150;
            I_low = (float)101;
        }
        if(O3 >= 86 && O3 <= 105){
            C_hight = (float)105;
            C_low = (float)86;
            I_hight = (float)200;
            I_low = (float)151;
        }
        if(O3 >= 106 && O3 <= 404){
            C_hight = (float)404;
            C_low = (float)106;
            I_hight = (float)300;
            I_low = (float)201;
        }
        if(O3 >= 405 && O3 <= 504){
            C_hight = (float)504;
            C_low = (float)405;
            I_hight = (float)400;
            I_low = (float)301;
        }
        if(O3 >= 505 && O3 <= 604){
            C_hight = (float)604;
            C_low = (float)505;
            I_hight = (float)500;
            I_low = (float)401;
        }

        o3_Aqi = ((I_hight-I_low)/(C_hight-C_low))*(C-C_low)+I_low;
        return o3_Aqi;
    }

    public float calcurate_pm25_aqi(float PM25){
        float pm_Aqi=0;
        float C_hight=0;
        float C_low=0;
        float I_hight=0;
        float I_low=0;
        float C = PM25;
        float I=0;
        if(PM25 >=0.0 && PM25 <=12.0){
            C_hight = (float)12.0;
            C_low = (float)0.0;
            I_hight = (float)50;
            I_low = (float)0;
        }
        if(PM25 >= 12.1 && PM25 <= 35.4){
            C_hight = (float)35.4;
            C_low = (float)12.1;
            I_hight = (float)100;
            I_low = (float)51;
        }
        if(PM25 >= 35.5 && PM25 <= 55.4){
            C_hight = (float)55.4;
            C_low = (float)35.5;
            I_hight = (float)150;
            I_low = (float)101;
        }
        if(PM25 >= 55.5 && PM25 <= 150.4){
            C_hight = (float)150.4;
            C_low = (float)55.5;
            I_hight = (float)200;
            I_low = (float)151;
        }
        if(PM25 >= 150.5 && PM25 <= 250.4){
            C_hight = (float)250.4;
            C_low = (float)150.5;
            I_hight = (float)300;
            I_low = (float)201;
        }
        if(PM25 >= 250.5 && PM25 <= 350.4){
            C_hight = (float)350.4;
            C_low = (float)250.5;
            I_hight = (float)400;
            I_low = (float)301;
        }
        if(PM25 >= 350.5 && PM25 <= 500.4){
            C_hight = (float)500.4;
            C_low = (float)350.5;
            I_hight = (float)500;
            I_low = (float)401;
        }

        pm_Aqi = ((I_hight-I_low)/(C_hight-C_low))*(C-C_low)+I_low;
        return pm_Aqi;
    }

    public float calcurate_co_avg(){
        float co_sum=0;
        if(count > 9600){
            co_avg = (co_avg * 9600 + co_bufferArrayList.get(count) - co_old)/9600;
            co_bufferArrayList.remove(0);
            co_old = co_bufferArrayList.get(0);
        }
        else if(count == 9600){
            co_avg = CO;
            co_old = co_bufferArrayList.get(0);
        }
        else {
            for(int i = 0; i < co_bufferArrayList.size(); i++) {
                co_sum += co_bufferArrayList.get(i);
            }
            co_avg = co_sum / co_bufferArrayList.size();
        }
        return co_avg;
    }

    public float calcurate_no2_avg(){
        float no2_sum=0;
        if(count > 1200){
            no2_avg = (no2_avg * 1200 + no2_bufferArrayList.get(count) - no2_old)/1200;
            no2_bufferArrayList.remove(0);
            no2_old = no2_bufferArrayList.get(0);
        }
        else if(count == 1200){
            no2_avg = NO2;
            no2_old = no2_bufferArrayList.get(0);
        }
        else {
            for(int i = 0; i < no2_bufferArrayList.size(); i++) {
                no2_sum += no2_bufferArrayList.get(i);
            }
            no2_avg = no2_sum / no2_bufferArrayList.size();
        }
        return no2_avg;
    }

    public float calcurate_so2_avg(){
        float so2_sum=0;
        if(SO2 >= 0 && SO2 <=304){
            if(count > 1200){
                so2_avg = (so2_avg * 1200 + so2_bufferArrayList.get(count) - so2_old)/1200;
                so2_bufferArrayList.remove(0);
                so2_old = so2_bufferArrayList.get(0);
            }
            else if(count == 1200){
                so2_avg = SO2;
                so2_old = so2_bufferArrayList.get(0);
            }
            else {
                for(int i = 0; i < so2_bufferArrayList.size(); i++) {
                    so2_sum += so2_bufferArrayList.get(i);
                }
                so2_avg = so2_sum / so2_bufferArrayList.size();
            }
        }
        else if(SO2 >= 305 && SO2 <= 1004){
            if(count > 28800){
                so2_avg = (so2_avg * 28800 + so2_bufferArrayList.get(count) - so2_old)/28800;
                so2_bufferArrayList.remove(0);
                so2_old = so2_bufferArrayList.get(0);
            }
            else if(count == 28800){
                so2_avg = SO2;
                so2_avg = so2_bufferArrayList.get(0);
            }
            else {
                for(int i = 0; i < so2_bufferArrayList.size(); i++) {
                    so2_sum += so2_bufferArrayList.get(i);
                }
                so2_avg = so2_sum / so2_bufferArrayList.size();
            }
        }
        return so2_avg;
    }

    public float calcurate_o3_avg(){
        float o3_sum=0;
        if(O3 >= 0 && O3 <=200){
            if(count > 9600){
                o3_avg = (o3_avg * 9600 + o3_bufferArrayList.get(count) - o3_old)/9600;
                o3_bufferArrayList.remove(0);
                o3_old = o3_bufferArrayList.get(0);
            }
            else if(count == 9600){
                o3_avg = O3;
                o3_old = o3_bufferArrayList.get(0);
            }
            else {
                for(int i = 0; i < o3_bufferArrayList.size(); i++) {
                    o3_sum += o3_bufferArrayList.get(i);
                }
                o3_avg = o3_sum / o3_bufferArrayList.size();
            }
        }
        else if(O3 >= 201 && O3 <= 604){
            if(count > 1200){
                o3_avg = (o3_avg * 1200 + o3_bufferArrayList.get(count) - o3_old)/1200;
                o3_bufferArrayList.remove(0);
                o3_old = o3_bufferArrayList.get(0);
            }
            else if(count == 1200){
                o3_avg = O3;
                o3_avg = o3_bufferArrayList.get(0);
            }
            else {
                for(int i = 0; i < o3_bufferArrayList.size(); i++) {
                    o3_sum += o3_bufferArrayList.get(i);
                }
                o3_avg = o3_sum / o3_bufferArrayList.size();
            }
        }
        return o3_avg;
    }

    public float calcurate_pm25_avg(){
        float pm25_sum=0;
        if(count > 28800){
            pm25_avg = (pm25_avg * 28800 + pm25_bufferArrayList.get(count) - pm25_old)/28800;
            pm25_bufferArrayList.remove(0);
            pm25_old = pm25_bufferArrayList.get(0);
        }
        else if(count == 28800){
            pm25_avg = PM25;
            pm25_old = pm25_bufferArrayList.get(0);
        }
        else {
            for(int i = 0; i < pm25_bufferArrayList.size(); i++) {
                pm25_sum += pm25_bufferArrayList.get(i);
            }
            pm25_avg = pm25_sum / pm25_bufferArrayList.size();
        }
        return pm25_avg;
    }

}


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

import java.util.ArrayList;


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

    int CO, NO2, SO2, O3, PM25, TEM  = 0;

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
    private BluetoothChatService mChatService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);
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
        } else if (mChatService == null) {
            //setupChat();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
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
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

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
                case Constants.MESSAGE_WRITE:
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

                    try {
                        JSONObject JsonAir = new JSONObject(readMessage);
                        /*temp = setText.String.valueOf(JsonAir.getInt("temp"));
                        co_air = setText.String.valueOf(JsonAir.getInt("CO"));
                        o3_air = setText.JsonAir.getInt("O3");
                        so2_air = setText.JsonAir.getInt("SO2");
                        no2_air = setText.JsonAir.getInt("NO2");
                        pm25_air = setText.JsonAir.getInt("PM2.5");*/

                        CO = JsonAir.getInt("CO");
                        NO2 = JsonAir.getInt("NO2");
                        SO2 = JsonAir.getInt("SO2");
                        O3 = JsonAir.getInt("O3");
                        PM25 = JsonAir.getInt("PM25");

                        temp.setText(JsonAir.getString("temp"));
                        co_air.setText(JsonAir.getString("CO"));   //toString 이 뭔가를 String으로 바꿔주는거
                        o3_air.setText(JsonAir.getString("O3"));
                        so2_air.setText(JsonAir.getString("SO2"));   //toString 이 뭔가를 String으로 바꿔주는거
                        no2_air.setText(JsonAir.getString("NO2"));
                        pm25_air.setText(JsonAir.getString("PM25"));


                        JsonTransfer airdata_transfer = new JsonTransfer();

                        JSONObject json_AirdataTransfer = new JSONObject();  //JSONObject는 JSON을 만들기 위함.

                        /*
                        json_AirdataTransfer.put("macaddress", "00:00:00:00");
                        json_AirdataTransfer.put("datetime", "2017/08/03");
                        json_AirdataTransfer.put("lat", "0");
                        json_AirdataTransfer.put("lng", "0");
                        json_AirdataTransfer.put("co", JsonAir.getString("CO"));
                        json_AirdataTransfer.put("co2", JsonAir.getString("NO2"));
                        json_AirdataTransfer.put("so2", JsonAir.getString("SO2"));
                        json_AirdataTransfer.put("o3", JsonAir.getString("O3"));
                        json_AirdataTransfer.put("pm25", JsonAir.getString("PM25"));
                        json_AirdataTransfer.put("temperature", JsonAir.getString("TEMP"));
                        */

                        json_AirdataTransfer.put("tid", "11");
                        json_AirdataTransfer.put("time", "00:00:00");
                        json_AirdataTransfer.put("type", "asdf");
                        json_AirdataTransfer.put("CO",JsonAir.getString("CO"));
                        json_AirdataTransfer.put("O3",JsonAir.getString("O3"));
                        json_AirdataTransfer.put("SO2",JsonAir.getString("SO2"));
                        json_AirdataTransfer.put("NO2",JsonAir.getString("NO2"));
                        json_AirdataTransfer.put("PM25",JsonAir.getString("PM25"));

                        setData();

                        //json_dataTransfer의 데이터들을 하나의 json_string으로 묶는다.
                        String json_Astring = json_AirdataTransfer.toString();

                        airdata_transfer.execute("http://teamd-iot.calit2.net/finally/slim-api/apptest","["+json_Astring+"]");
                        // airdata_transfer.execute("http://teama-iot.calit2.net/slim-api/receive-air-data","["+json_Astring+"]");

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                    /* air_info_split = readMessage.split(",");

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
                    } */

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
        mChatService.connect(device, secure);
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

}


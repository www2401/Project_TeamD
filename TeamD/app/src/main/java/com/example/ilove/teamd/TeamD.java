package com.example.ilove.teamd;

import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ilove.teamd.Heart.PolarBleService;
import com.example.ilove.teamd.userfage.login;
import com.example.ilove.teamd.userfage.registration;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import static com.example.ilove.teamd.R.id.nav_login;

public class TeamD extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

    login loginn;
    TextView text_input;
    private final String TAG = "TeamD";

    PolarBleService mPolarBleService;
    String mpolarBleDeviceAddress;	//Your need to pass the address
    int batteryLevel=0;

    BluetoothChatFragment bt;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private BluetoothAdapter mBluetoothAdapter = null;
    //Member object for the chat services
    private BluetoothChatService mChatService = null;
    //Name of the connected device
    private String mConnectedDeviceName = null;
    //Array adapter for the conversation thread


    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginn = new login();
        setContentView(R.layout.activity_team_d);
        text_input = (TextView)findViewById(R.id.heartrate);

        Log.w(this.getClass().getName(), "onCreate()");
        activatePolar();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.mapp);
        mapFragment.getMapAsync(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"Try again connection with Bluetooth", Toast.LENGTH_SHORT).show();
        }

        }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();
            if(loginn.flag==1) {
                if ((id == R.id.nav_logout)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamD.this);
                    builder.setMessage("Log out complete").setPositiveButton("OK", null).create().show();
                    loginn.flag=0;
                }
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(TeamD.this);
                builder.setMessage("you can't log out . first , Please log in").setPositiveButton("OK", null).create().show();
            }
        }catch (Exception e){
            Log.v("catched","sdf");
        }
        return true;
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        try {
            int id = item.getItemId();
            //로그인 됬을때 map, graph, air quality, sign up, log in 클릭했을 때
            if(loginn.flag==1) {
                if (id == R.id.nav_current) {
                    Intent page = new Intent(TeamD.this, current.class);
                    startActivity(page);
                } else if (id == R.id.nav_graph) {
                    Intent page = new Intent(TeamD.this, graph.class);
                    startActivity(page);
                } else if (id == R.id.Airquality) {
                    Intent page = new Intent(TeamD.this, Airquality.class);
                    startActivity(page);
                }
                else if (id == R.id.nav_registration) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamD.this);
                    builder.setMessage("you can't do it . first , Please log out").setPositiveButton("OK", null).create().show();
                }
                else if (id == nav_login) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamD.this);
                    builder.setMessage("you can't do it . first , Please log out").setPositiveButton("OK", null).create().show();
                }
            }
            //로그인 안됬는데 map,graph, air qaulity ,sign up, log in 클릭했을 때
            else if(loginn.flag==0) {
                if (id == R.id.nav_current) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamD.this);
                    builder.setMessage("you can't do it . first , Please log in").setPositiveButton("OK", null).create().show();
                } else if (id == R.id.nav_graph) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamD.this);
                    builder.setMessage("you can't do it . first , Please log in").setPositiveButton("OK", null).create().show();
                } else if (id == R.id.Airquality) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeamD.this);
                    builder.setMessage("you can't do it . first , Please log in").setPositiveButton("OK", null).create().show();
                }
                if (id == R.id.nav_registration) {
                    Intent page = new Intent(TeamD.this, registration.class);
                    startActivity(page);
                }
                else if (id == nav_login) {
                    Intent page = new Intent(TeamD.this, login.class);
                    startActivity(page);
                }
            }
        }catch (Exception e){
            Log.v("catched","sdf");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(this.getClass().getName(), "onDestroy()");
        deactivatePolar();
    }

    protected void activatePolar() {
        Log.w(this.getClass().getName(), "** activatePolar()");
        Intent gattactivateClickerServiceIntent = new Intent(this, PolarBleService.class);
        bindService(gattactivateClickerServiceIntent, mPolarBleServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mPolarBleUpdateReceiver, makePolarGattUpdateIntentFilter());
    }

    protected void deactivatePolar() {
        Log.w(this.getClass().getName(), "deactivatePolar()");
        if(mPolarBleService!=null){
            unbindService(mPolarBleServiceConnection);
        }
        unregisterReceiver(mPolarBleUpdateReceiver);
        mPolarBleService.disconnect();
    }

    private final BroadcastReceiver mPolarBleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            final String action = intent.getAction();
            if (PolarBleService.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (PolarBleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //dataFragPolar.stopAnimation();
            } else if (PolarBleService.ACTION_HR_DATA_AVAILABLE.equals(action)) {
                //heartRate+";"+pnnPercentage+";"+pnnCount+";"+rrThreshold+";"+bioHarnessSessionData.totalNN
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int hr = Integer.parseInt(tokens.nextToken());
                text_input.setText(""+ hr);
                Log.w("heart", ""+ hr);

                //심박수 데이터 서버로 전송
                try
                {
                    JsonTransfer heartdata_transfer = new JsonTransfer();

                    JSONObject json_HeartdataTransfer = new JSONObject();

                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

                    json_HeartdataTransfer.put("uid", uidValue.tempUid);
                    json_HeartdataTransfer.put("heart_bit", hr);
                    json_HeartdataTransfer.put("htime", time);

                    String json_Astring = json_HeartdataTransfer.toString();

                    heartdata_transfer.execute("http://teamd-iot.calit2.net/finally/slim-api/heart_send_app","["+json_Astring+"]");

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                int prrPercenteage = Integer.parseInt(tokens.nextToken());
                int prrCount = Integer.parseInt(tokens.nextToken());
                int rrThreshold = Integer.parseInt(tokens.nextToken());	//50%, 30%, etc.
                int rrTotal = Integer.parseInt(tokens.nextToken());
                int rrValue = Integer.parseInt(tokens.nextToken());
                long sid = Long.parseLong(tokens.nextToken());

                //dataFragPolar.settvHR(Integer.toString(hr));
            }else if (PolarBleService.ACTION_BATTERY_DATA_AVAILABLE.equals(action)) {
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                batteryLevel = Integer.parseInt(data);
            }else if (PolarBleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int totalNN = Integer.parseInt(tokens.nextToken());
                long lSessionId = Long.parseLong(tokens.nextToken());

                //Enable your UI
            }
        }
    };

    private static IntentFilter makePolarGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PolarBleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(PolarBleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(PolarBleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(PolarBleService.ACTION_HR_DATA_AVAILABLE);
        intentFilter.addAction(PolarBleService.ACTION_BATTERY_DATA_AVAILABLE);
        return intentFilter;
    }

    private final ServiceConnection mPolarBleServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mPolarBleService = ((PolarBleService.LocalBinder) service).getService();
            if (!mPolarBleService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            //mPolarBleService.connect("00:07:80:78:9F:8B", false); 이거 아까 해봄
            mPolarBleService.connect("00:22:D0:9C:F9:8E", false);  // 인교오빠
            //mPolarBleService.connect("00:22:D0:3D:2E:81", false);  //이게 잘 된대

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
      //      if(app.runtimeLogging)
           //     LOG.warn("onServiceDisconnected() ");

            mPolarBleService = null;
        }
    };


    @Override
    public void onMapReady(final GoogleMap map) {

        LatLng CSE = new LatLng(32.8818010, -117.2335230);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(CSE);
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(CSE));
        map.animateCamera(CameraUpdateFactory.zoomTo(18));

    }

}

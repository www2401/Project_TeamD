package com.example.ilove.teamd;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.ilove.teamd.R.id.map;

public class current extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient = null;
     private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;
    CircleOptions circle;
    //디폴트 위치, Seoul
    private static final LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000; // 1초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;

    private BluetoothAdapter mBluetoothAdapter = null;
    private String mConnectedDeviceName = null;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private Button bt_co, bt_so2, bt_no2, bt_o3, bt_pm;
    LatLng currentLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);
        bt_co = (Button) findViewById(R.id.bt_co);
        bt_so2 = (Button) findViewById(R.id.bt_so2);
        bt_no2 = (Button) findViewById(R.id.bt_no2);
        bt_o3 = (Button) findViewById(R.id.bt_o3);
        bt_pm = (Button) findViewById(R.id.bt_pm);

        //co
        bt_co.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    setCircleColorButton((int)tempValue.CO_AQI_tv);
            }
        });

        //so2
        bt_so2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setCircleColorButton((int)tempValue.SO2_AQI_tv);
            }
        });
        //no2
        bt_no2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setCircleColorButton((int)tempValue.NO2_AQI_tv);
            }
        });
        //o3
        bt_o3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setCircleColorButton((int)tempValue.O3_AQI_tv);
            }
        });
        //pm2.5
        bt_pm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setCircleColorButton((int)tempValue.PM25_AQI_tv);
            }
        });

        Log.w("BT Map Frag", "onCreate()");
        mActivity = this;

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(map);

        mapFragment.getMapAsync(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Try again connection with Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (AppController.getinstance().mChatService != null) {
            Log.w("BT Map Frag", "AppController.getinstance().mChatService.addHandler(pmHandler): " + pmHandler);
            AppController.getinstance().mChatService.addHandler(pmHandler);
        }
    }
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }
    }

    @Override
    protected void onStop() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {

        //위치 업데이트 중지
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            mGoogleApiClient.disconnect();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (mGoogleApiClient != null) {
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);


            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi
                        .removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }

        }
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        this.mGoogleMap = map;
        //내위치 말고 다른 위치에 마커 띄우기(고정된 위치)
        // 1. 마커 옵션 설정 (만드는 과정)
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(new LatLng(34, -118))
                .title("Different location!"); // 타이틀.
        // 2. 마커 생성 (마커를 나타냄)
        map.addMarker(makerOptions);
        // 카메라를 위치로 옮긴다.
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.52487, 126.92723)));

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setCurrentLocation(null, "Can't load location info.",
                "Please check location permission and GPS activation.");

        map.getUiSettings().setCompassEnabled(true);
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.animateCamera(CameraUpdateFactory.zoomTo(22));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //API 23 이상이면 런타임 퍼미션 처리 필요

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            } else {

                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    map.setMyLocationEnabled(true);
                }

            }
        } else {

            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            map.setMyLocationEnabled(true);
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        android.util.Log.d(TAG, "onLocationChanged");
        String markerTitle = getCurrentAddress(location);
        String markerSnippet = "Latitude:" + String.valueOf(location.getLatitude())
                + " Longitude:" + String.valueOf(location.getLongitude());

        GPSlocation.lat = String.valueOf(location.getLatitude());
        GPSlocation.lng = String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성
        setCurrentLocation(location, markerTitle, markerSnippet);
    }


    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }


    @Override
    public void onConnected(Bundle connectionHint) {

        android.util.Log.d(TAG, "onConnected");
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL_MS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi
                        .requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            }
        } else {

            android.util.Log.d(TAG, "onConnected : call FusedLocationApi");
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            mGoogleMap.getUiSettings().setCompassEnabled(true);
            //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Location location = null;
        location.setLatitude(DEFAULT_LOCATION.latitude);
        location.setLongitude(DEFAULT_LOCATION.longitude);

        setCurrentLocation(location, "Can't load location info.",
                "Please check location permission and GPS activation.");
    }


    @Override
    public void onConnectionSuspended(int cause) {
        if (cause == CAUSE_NETWORK_LOST)
            android.util.Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            android.util.Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }


    public String getCurrentAddress(Location location) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "Can't use GEO coder service.", Toast.LENGTH_LONG).show();
            return "Can't use GEO coder service.";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "Wrong GPS coordinate.", Toast.LENGTH_LONG).show();
            return "Wrong GPS location";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "Not found address.", Toast.LENGTH_LONG).show();
            return "Not found address.";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        if (currentMarker != null) currentMarker.remove();


        if (location != null) {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            //마커를 원하는 이미지로 변경해줘야함
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLocation);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentMarker = mGoogleMap.addMarker(markerOptions);

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(DEFAULT_LOCATION));

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("For app activation, check permission.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("permission deny + Don't ask again " +
                    "Please check your permission in setting for checkbox.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }

            mGoogleMap.setMyLocationEnabled(true);


        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {

                if (mGoogleApiClient == null) {
                    buildGoogleApiClient();
                }

                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mGoogleMap.setMyLocationEnabled(true);
                }


            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(current.this);
        builder.setTitle("Alert");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(current.this);
        builder.setTitle("Alert");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(current.this);
        builder.setTitle("Location service inactivate.");
        builder.setMessage("For app use, need location service.\n"
                + "modified location service? ");
        builder.setCancelable(true);
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                                mGoogleMap.setMyLocationEnabled(true);
                            }
                        } else mGoogleMap.setMyLocationEnabled(true);

                        return;
                    }
                } else {
                    setCurrentLocation(null, "Can't load location info.",
                            "Check location permission and GPS activation.");
                }

                break;
        }
    }

    private final Handler pmHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    Log.w("BT Map Frag", "Total bytes received: "+readBuf.length);
                    String start_message = new String(readBuf, 0, msg.arg1);
                    String form_type = start_message.substring(0, 1);
                    String readMessage = start_message.substring(1);
                    //Toast.makeText(getContext(),form_type,Toast.LENGTH_SHORT).show();
                    if (form_type.equals("h")) {
                        Toast.makeText(current.this, "history", Toast.LENGTH_SHORT).show();
                    } else if (form_type.equals("r")) {
                       // Toast.makeText(current.this, "CO", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    break;

            }
        }
    };

    public void setCircleColorButton(int value) {
        if (value <= 50) {
           circle = new CircleOptions().center(currentLocation).radius(500)
                    .strokeWidth(0f)//선너비 0f : 선없음
                    .fillColor(Color.parseColor("#e2ed9b"));
            mGoogleMap.clear();
            mGoogleMap.addCircle(circle);
        }
        if (value > 50 && value <= 100) {
             circle = new CircleOptions().center(currentLocation).radius(500)
                    .strokeWidth(0f)//선너비 0f : 선없음
                    .fillColor(Color.parseColor("#fff59d"));
            mGoogleMap.clear();
            mGoogleMap.addCircle(circle);
        }
        if (value > 100 && value <= 150) {
             circle = new CircleOptions().center(currentLocation).radius(500)
                    .strokeWidth(0f)//선너비 0f : 선없음
                    .fillColor(Color.parseColor("#ffcc80"));
            mGoogleMap.clear();
            mGoogleMap.addCircle(circle);
        }
        if (value > 150 && value <= 200) {
            mGoogleMap.clear();
            circle = new CircleOptions();
            circle.center(currentLocation);
            circle.radius(500);
            circle.strokeWidth(0f);//선너비 0f : 선없음
            circle.fillColor(Color.parseColor("#ef5350"));
         /*   circle = new CircleOptions().center(currentLocation).radius(500)
                    .strokeWidth(0f)//선너비 0f : 선없음
                    .fillColor(Color.parseColor("#7fff0000"));*/

            mGoogleMap.addCircle(circle);
        }
        if (value > 200 && value <= 300) {
             circle = new CircleOptions().center(currentLocation).radius(500)
                    .strokeWidth(0f)//선너비 0f : 선없음
                    .fillColor(Color.parseColor("#ce93d8"));
            mGoogleMap.clear();
            mGoogleMap.addCircle(circle);
        }
        if (value > 300 && value <= 400) {
            circle = new CircleOptions();
            circle.center(currentLocation);
                    circle.radius(500);
                    circle.strokeWidth(0f);//선너비 0f : 선없음
                    circle.fillColor(Color.parseColor("#795548"));
            mGoogleMap.clear();
            mGoogleMap.addCircle(circle);
        }

        if (value > 400 && value <= 500) {
            circle = new CircleOptions().center(currentLocation).radius(500)
                    .strokeWidth(0f)//선너비 0f : 선없음
                    .fillColor(Color.parseColor("#795548"));
            mGoogleMap.clear();
            mGoogleMap.addCircle(circle);
        }
    }
}

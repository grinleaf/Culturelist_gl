package com.grinleaf.tp14librarysearch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.grinleaf.tp14librarysearch.databinding.ActivityMapBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    ArrayList<HallDetailInfo> hallDetailInfos = new ArrayList<HallDetailInfo>();
    //FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (MainActivity.mainContext.checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, 10);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadMapData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //퍼미션 허가받았을 때

        } else {
            //퍼미션 거부 시
            Toast.makeText(MainActivity.mainContext, "위치정보제공 동의가 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    void getDistanceTo(){
        //퍼미션 확인
        if(ActivityCompat.checkSelfPermission(MainActivity.mainContext,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.mainContext,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
        //내위치 위도, 경도 구하기
        LocationManager locationManager= (LocationManager) MainActivity.mainContext.getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation= null;
        String locationProvider= LocationManager.GPS_PROVIDER;
        currentLocation= locationManager.getLastKnownLocation(locationProvider);
        TextView locationGap= findViewById(R.id.map_location_gap);
        if(currentLocation!=null){
            Location hallLocation= new Location(locationProvider);
            hallLocation.setLatitude(latHall);
            hallLocation.setLongitude(lngHall);
            double gap= currentLocation.distanceTo(hallLocation)/1000;
            String num= String.format("%.2f",gap);
            locationGap.setText(num+" km");
        }else locationGap.setText("거리 계산중");
    }

    double latHall= 0;
    double lngHall= 0;

    void loadHallData() {
        TextView hallName, tel, hallAddress, homeUrl, seatScale, hallCount;

        hallName = findViewById(R.id.map_hallName);
        tel = findViewById(R.id.map_tel);
        hallAddress = findViewById(R.id.map_address);
        homeUrl = findViewById(R.id.map_homepage);
        seatScale = findViewById(R.id.seat_map_tv);
        hallCount = findViewById(R.id.hall_map_tv);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hallName.setText(hallDetailInfos.get(0).hallName);
                tel.setText(hallDetailInfos.get(0).tel);
                hallAddress.setText(hallDetailInfos.get(0).hallAddress);
                homeUrl.setText(hallDetailInfos.get(0).homeUrl);
                seatScale.setText("객석수\n" + hallDetailInfos.get(0).seatScale);
                hallCount.setText("공연장수\n" + hallDetailInfos.get(0).hallCount);

                //다른 앱, 웹과 연결
                tel.setOnClickListener(v -> {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);

                    Uri uri = Uri.parse("tel:" + tel.getText());
                    intent.setData(uri);

                    startActivity(intent);
                });
                homeUrl.setOnClickListener(v -> {
                    Uri uri = Uri.parse(homeUrl.getText() + "");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
            }
        });
    }

    void showMap() {

        FragmentManager manager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) manager.findFragmentById(R.id.fragment_map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                //좌표에 따라 지도 움직이기
                latHall = hallDetailInfos.get(0).lat;
                lngHall = hallDetailInfos.get(0).lng;
                LatLng mapSpace = new LatLng(latHall, lngHall);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapSpace, 18));

                findViewById(R.id.map_location_icon).setOnClickListener(v -> {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapSpace, 18));
                });
                getDistanceTo();

                //마커 표시 설정
                MarkerOptions marker = new MarkerOptions();
                marker.position(mapSpace);
                googleMap.addMarker(marker);

                //확대/축소 버튼 설정
                UiSettings settings = googleMap.getUiSettings();
                settings.setZoomControlsEnabled(true);

                //내위치 표시 버튼 설정
                settings.setMyLocationButtonEnabled(true);

                if(ActivityCompat.checkSelfPermission(MainActivity.mainContext,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&
                   ActivityCompat.checkSelfPermission(MainActivity.mainContext,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            }
        });
        loadHallData();
    }

    void loadMapData() {
        Thread t= new Thread() {
            @Override
            public void run() {
                super.run();

                Intent intent = getIntent();
                String hallId = intent.getStringExtra("hallId");
                String apiKey = "c95870c8bfe2437880b4aed38acb6efe";

                String address= "http://www.kopis.or.kr/openApi/restful/prfplc/"
                        + hallId
                        + "?service=" + apiKey;

                try {
                    URL url = new URL(address);
                    InputStream is = url.openStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(isr);

                    int eventType = xpp.getEventType();
                    HallDetailInfo hallDetailInfo = null;
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });
                                break;
                            case XmlPullParser.START_TAG:
                                String tagName = xpp.getName();
                                if (tagName.equals("db")) {
                                    hallDetailInfo = new HallDetailInfo();
                                } else if (tagName.equals("fcltynm")) {
                                    xpp.next();
                                    if (hallDetailInfo != null)
                                        hallDetailInfo.hallName = xpp.getText();
                                } else if (tagName.equals("seatscale")) {
                                    xpp.next();
                                    if (hallDetailInfo != null) hallDetailInfo.seatScale = Integer.parseInt(xpp.getText());
                                } else if (tagName.equals("mt13cnt")) {
                                    xpp.next();
                                    if (hallDetailInfo != null) hallDetailInfo.hallCount = Integer.parseInt(xpp.getText());
                                } else if (tagName.equals("telno")) {
                                    xpp.next();
                                    if (hallDetailInfo != null) hallDetailInfo.tel = xpp.getText();
                                } else if (tagName.equals("relateurl")) {
                                    xpp.next();
                                    if (hallDetailInfo != null) hallDetailInfo.homeUrl = xpp.getText();
                                } else if (tagName.equals("adres")) {
                                    xpp.next();
                                    if (hallDetailInfo != null) hallDetailInfo.hallAddress = xpp.getText();
                                } else if (tagName.equals("la")) {
                                    xpp.next();
                                    if (hallDetailInfo != null) hallDetailInfo.lat = Double.parseDouble(xpp.getText());
                                } else if (tagName.equals("lo")) {
                                    xpp.next();
                                    if (hallDetailInfo != null) hallDetailInfo.lng = Double.parseDouble(xpp.getText());
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                String tagName2 = xpp.getName();
                                if (tagName2.equals("db")) {
                                    if (hallDetailInfo != null) {
                                        hallDetailInfos.add(hallDetailInfo);
                                    }
                                }
                                break;
                        }
                        eventType = xpp.next();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMap();
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}

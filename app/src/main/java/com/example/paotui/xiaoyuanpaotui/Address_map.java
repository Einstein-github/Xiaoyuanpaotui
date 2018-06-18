package com.example.paotui.xiaoyuanpaotui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Colonel on 2018/5/22.
 */

public class Address_map extends AppCompatActivity implements LocationSource,GeocodeSearch.OnGeocodeSearchListener {
    private MapView mapView;
    private AMap aMap;
    public AMapLocationClientOption mLocationOption = null;
    private double lat;
    private double lon;
    private String Street;
    private String StreetNum;
    TextView address_map_text;
    public AMapLocationClient mLocationClient = null;
    private Button address_map_confirm;
    List<String> PerList=new ArrayList<String>();
    GeocodeSearch geocoderSearch;
    private String where;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;
    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器
    private boolean isFirstLoc = true;
    public AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    lat = amapLocation.getLatitude();
                    lon = amapLocation.getLongitude();
                    Street = amapLocation.getStreet();
                    StreetNum = amapLocation.getStreetNum();
                    Log.v("pcw","lat : "+lat+" lon : "+lon);
                    Log.v("pcw", "Country : " + amapLocation.getCountry() +
                            " province : " + amapLocation.getProvince() +
                            " City : " + amapLocation.getCity() +
                            " District : " + amapLocation.getDistrict());
                    Log.v("pcw", "Street : " + amapLocation.getStreet() +
                            " StreetNum : " + amapLocation.getStreetNum());
                    if (isFirstLoc) {
                        //设置缩放级别
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        //将地图移动到定位点
                        aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                        //点击定位按钮 能够将地图的中心移动到定位点
                        mListener.onLocationChanged(amapLocation);

                        Log.e("address------->",amapLocation.getLatitude()+", "+amapLocation.getLongitude());

                        //获取定位信息
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(amapLocation.getLatitude() + "" + amapLocation.getLongitude());
                        //Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                        isFirstLoc = false;
                    }


                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_map);//设置对应的XML布局文件
        getPermission();
        mapView = findViewById(R.id.address_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        address_map_text = findViewById(R.id.address_map_text);
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        init();
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        /*AMap aMap = mapView.getMap();

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。*/
        address_map_confirm = findViewById(R.id.address_map_confirm);
        sharedPreference =this.getSharedPreferences("User_info", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true&&(!Thread.currentThread().isInterrupted()))
                {
                    final LatLng pt=getMapCenterPoint();
                    double x=pt.latitude;
                    double y=pt.longitude;

                    RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(x,y), 15, GeocodeSearch.AMAP);
                    geocoderSearch.getFromLocationAsyn(query);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }

            }
        }).start();

        address_map_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("ADDRESS",where);
                editor.commit();
                startActivity(new Intent(getApplicationContext(),Add_New_Address.class));
                finish();
            }
        });
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
        setUpMap();
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style


    }

    private void setUpMap() {
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public void getPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            PerList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            PerList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            PerList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(!PerList.isEmpty())
        {
            String[]permissions=PerList.toArray(new String[PerList.size()]);
            ActivityCompat.requestPermissions(this,permissions,3);
        }


    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {
        switch (requestCode)
        {
            case 3:
                if(grantResults.length>0)
                {
                    for(int result:grantResults)
                    {
                        if(result!=PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(this,"权限没有给足",Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                }
            default:break;

        }
    }
    public LatLng getMapCenterPoint() {

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int x = dm.widthPixels/2;
        int y = dm.heightPixels/2;
        Projection projection = aMap.getProjection();
        LatLng pt = projection.fromScreenLocation(new Point(x, y));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pt);
        markerOptions.visible(true);
        aMap.clear();
        aMap.addMarker(markerOptions);
        return pt;

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress ree=regeocodeResult.getRegeocodeAddress();
        String fa=ree.getFormatAddress();
        int lastIndex=fa.lastIndexOf("市")+1;
        int length=fa.length();
         where=fa.substring(lastIndex,length);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                address_map_text.setText(where);
            }
        });
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }


}

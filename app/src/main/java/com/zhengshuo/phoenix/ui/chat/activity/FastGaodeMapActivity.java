package com.zhengshuo.phoenix.ui.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.zhengshuo.phoenix.R;
import com.zhengshuo.phoenix.base.BaseActivity;
import com.zhengshuo.phoenix.ui.chat.adapter.MapAddressAdapter;
import com.zhengshuo.phoenix.util.DisplayUtil;
import com.zhengshuo.phoenix.util.LogUtil;
import com.zhengshuo.phoenix.util.ToastUtil;


/**
 * 高德地图
 */
public class FastGaodeMapActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {
    protected double latitude;
    protected double longitude;
    protected String address;
    private MapView mMapView;
    private AMap aMap;
    private Marker screenMarker;
    boolean useMoveToLocationWithMapMode = true;
    private GeocodeSearch geocoderSearch;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private AMapLocation MapLocation;


    private PoiSearch poiSearch;
    private RecyclerView adress_Recycler;
    private EditText seek_edit;
    private String keyWord = "";

    public static void actionStartForResult(Context context, int requestCode) {
        Intent intent = new Intent(context, FastGaodeMapActivity.class);
        context.startActivity(intent);
    }


    public static void actionStart(Context context, double latitude, double longitude, String address) {
        Intent intent = new Intent(context, FastGaodeMapActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_gaodemap;
    }

    @Override
    protected void initLocalData(Intent mIntent) {
        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longtitude", 0);
        address = getIntent().getStringExtra("address");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        if (latitude != 0) {
            titleBar.showRight();
        } else {
            titleBar.showRight();
        }
    }




    @Override
    protected void initView(Bundle savedInstanceState) {
        seek_edit = findViewById(R.id.seek_edit);
        seek_edit.setOnEditorActionListener(new SeekOnEditorListener());
        adress_Recycler = findViewById(R.id.address_Recycler);
        //获取地图控件引用
        mMapView = findViewById(R.id.bmapView);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        setupLocationStyle();
        // 去掉高德地图右下角隐藏的缩放按钮
        aMap.getUiSettings().setZoomControlsEnabled(false);

        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));//地图的缩放级别一共分为 17 级，从 3 到 19。数字越大，展示的图面信息越精细。

        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(mLocationListener);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //获取一次定位结果：
            //该方法默认为false。
            mLocationOption.setOnceLocation(true);
            //获取最近3s内精度最高的一次定位结果：
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            mLocationOption.setOnceLocationLatest(true);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initEvent() {
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                addMarkerInScreenCenter();
            }
        });
        aMap.setOnMyLocationChangeListener(mMyLocationChangeListener);
        // 设置可视范围变化时的回调的接口方法
        aMap.setOnCameraChangeListener(mOnCameraChangeListener);
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(mGeocodeSearchListener);
    }



    /**
     *
     * func:获取屏幕中心的经纬度坐标
     * @return 经纬度
     */
    private LatLng getMapCenterPoint() {
        int left = mMapView.getLeft();
        int top = mMapView.getTop();
        int right = mMapView.getRight();
        int bottom = mMapView.getBottom();
        // 获得屏幕点击的位置
        int x = (int) (mMapView.getX() + (right - left) / 2);
        int y = (int) (mMapView.getY() + (bottom - top) / 2);
        Projection projection = aMap.getProjection();
        LatLng pt = projection.fromScreenLocation(new Point(x, y));
        return pt;
    }


    /**
     * LatLng转LatLonPoint
     * @param latlon
     * @return
     */
    private LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }



    /**
     * 在屏幕中心添加一个Marker
     */
    private void addMarkerInScreenCenter() {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        screenMarker = aMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.lvsedingwei)));
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(screenPosition.x,screenPosition.y);

    }


    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {

        if (screenMarker != null ) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point =  aMap.getProjection().toScreenLocation(latLng);
            point.y -= DisplayUtil.dip2px(this,125);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if(input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f)*(1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();

        } else {
            Log.e("amap","screenMarker is null");
        }
    }


    /**
     * 设置自定义定位蓝点
     */
    private void setupLocationStyle(){
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.mipmap.baisedingwei));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(ContextCompat.getColor(mContext,R.color.transparent));
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(ContextCompat.getColor(mContext,R.color.transparent));

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        // 将自定义的 myLocationStyle 对象添加到地图上
        aMap.setMyLocationStyle(myLocationStyle);
    }


    public AMap.OnMyLocationChangeListener mMyLocationChangeListener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            lp = new LatLonPoint(location.getLatitude(), location.getLongitude());// 116.472995,39.993743
            doSearchQuery();
        }
    };

    public AMap.OnCameraChangeListener mOnCameraChangeListener = new AMap.OnCameraChangeListener(){

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            //屏幕中心的Marker跳动
            startJumpAnimation();
            LatLng mLatLng = getMapCenterPoint();
            if (mLatLng!=null) {
                lp = convertToLatLonPoint(mLatLng);
                doSearchQuery();
                RegeocodeQuery query = new RegeocodeQuery(lp, 200,GeocodeSearch.AMAP);
                geocoderSearch.getFromLocationAsyn(query);
            }
        }
    };



    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        //声明定位回调监听器

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation != null
                        && amapLocation.getErrorCode() == 0) {
                    MapLocation = amapLocation;
                    LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                } else {
                    String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                    Log.e("AmapErr",errText);
                }
            }
        }
    };


    public GeocodeSearch.OnGeocodeSearchListener mGeocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener(){

        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
            if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                if (result != null && result.getRegeocodeAddress() != null
                        && result.getRegeocodeAddress().getFormatAddress() != null) {
                    String addressName = result.getRegeocodeAddress().getFormatAddress()
                            + "附近";
                    LogUtil.d("onRegeocodeSearched","onRegeocodeSearched>>>"+addressName);
                } else {
                    ToastUtil.ss("没有结果");
                }
            } else {
                ToastUtil.ss("error");
            }
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

        }
    };



    private class SeekOnEditorListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {//打开关闭软键盘
                imm.hideSoftInputFromWindow(seek_edit.getWindowToken(), 0);
            }
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyWord = seek_edit.getText().toString().trim();
                doSearchQuery();
            }
            return false;
        }
    }

    /**
     * 开始进行poi搜索
     */
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp;
    protected void doSearchQuery() {

        keyWord = seek_edit.getText().toString().trim();
//        currentPage = 0;
//        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
//        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
//        poiResult.getPois().get(0).getSnippet();

        initList(this, poiResult);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    MapAddressAdapter mAdapter;

    public void initList(final Context context, PoiResult poiResult) {
        LinearLayoutManager mManager = new LinearLayoutManager(context);
        adress_Recycler.setLayoutManager(mManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        adress_Recycler.setHasFixedSize(true);
        mAdapter = new MapAddressAdapter(context, poiResult);
        adress_Recycler.setAdapter(mAdapter);

        mAdapter.setOnSendClickLitener(new MapAddressAdapter.OnSendClickLitener() {
            @Override
            public void onClick(View v, int pos) {
//                sendLocation(poiResult, pos);
            }
        });
    }







    @Override
    public void onRightClick() {
        sendLocation();
    }

    /**
     * 发声位置
     */
    private void sendLocation() {
        if (MapLocation != null) {
            Intent intent = getIntent();
            intent.putExtra("latitude", MapLocation.getLatitude());
            intent.putExtra("longitude", MapLocation.getLongitude());
            intent.putExtra("address", MapLocation.getAddress());
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        useMoveToLocationWithMapMode = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        useMoveToLocationWithMapMode = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        useMoveToLocationWithMapMode = false;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }




}

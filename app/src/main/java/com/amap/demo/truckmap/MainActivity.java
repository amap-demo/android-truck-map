package com.amap.demo.truckmap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.demo.truckmap.utilTools.ToastUtil;
import com.amap.demo.truckmap.view.PoiOverlay;
import com.amap.demo.truckmap.view.ViolationOverlay;
import com.amap.demo.truckmap.view.TruckRouteView;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener , CompoundButton.OnCheckedChangeListener , PoiSearch.OnPoiSearchListener {

    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    TruckRouteView truckRouteView;
    PoiOverlay gasPoiOverlay;
    PoiOverlay repairPoiOverlay;
    ViolationOverlay violationOverlay;
    private Button testBtn;
    private Switch trafficSw;
    private Switch errorSw;
    private Switch gasSw;
    private Switch repairSw;

    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    private final String GAS = "加油站";
    private final String REPAIR = "汽车维修|汽车综合维修";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContext    = this.getApplicationContext();
        mapView     = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        truckRouteView = new TruckRouteView(this,aMap);

        violationOverlay = new ViolationOverlay(this, aMap);


        testBtn =  findViewById(R.id.test);
        testBtn.setOnClickListener(this);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.getUiSettings().setZoomControlsEnabled(false);
        }

        trafficSw   = findViewById(R.id.traffic_switch);
        errorSw     = findViewById(R.id.error_switch);
        gasSw       = findViewById(R.id.gas_switch);
        repairSw    = findViewById(R.id.repair_switch);

        trafficSw.setOnCheckedChangeListener(this);
        errorSw.setOnCheckedChangeListener(this);
        gasSw.setOnCheckedChangeListener(this);
        repairSw.setOnCheckedChangeListener(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        aMap.setTrafficEnabled(true);
        doSearchQuery(GAS);
        doSearchQuery(REPAIR);
        truckRouteView.addToMap();
        violationOverlay.addToMap();
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
    public void onClick(View v) {

        doSearchQuery(GAS);
        doSearchQuery(REPAIR);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int viewID = buttonView.getId();
        if (viewID == R.id.traffic_switch) {
            aMap.setTrafficEnabled(isChecked);
        } else if (viewID == R.id.error_switch) {

            if (violationOverlay == null) {
                return;
            }
            if (isChecked) {
                violationOverlay.addTrafficToMap();
            } else {
                violationOverlay.removeTrafficFormMap();
            }
        } else if (viewID == R.id.gas_switch) {
            if ( gasPoiOverlay == null) {
                return;
            }
            if (isChecked) {
                gasPoiOverlay.addToMap();
            } else {
                gasPoiOverlay.removeFromMap();
            }

        } else if (viewID == R.id.repair_switch) {
            if (repairPoiOverlay == null) {
                return;
            }
            if (isChecked) {
                repairPoiOverlay.addToMap();
            } else {
                repairPoiOverlay.removeFromMap();
            }
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String type) {
        Log.d("qyd", "doSearchQuery type:" + type);
        query = new PoiSearch.Query("", type, "北京");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(5);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**

     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                poiResult = result;
                // 取得搜索到的poiitems有多少页
                List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                List<SuggestionCity> suggestionCities = poiResult
                        .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                if (poiItems != null && poiItems.size() > 0) {
                    if (GAS.equals(result.getQuery().getCategory())) {
                        if (gasPoiOverlay == null) {
                            gasPoiOverlay = new PoiOverlay(aMap, poiItems, 1);
                        } else {
                            gasPoiOverlay.removeFromMap();
                        }
                        gasPoiOverlay.addToMap();
                    } else {
                        if (repairPoiOverlay == null) {
                            repairPoiOverlay = new PoiOverlay(aMap, poiItems, 2);
                        } else {
                            repairPoiOverlay.removeFromMap();
                        }
                        repairPoiOverlay.addToMap();
                    }

                } else {
                    ToastUtil.show(MainActivity.this,
                            R.string.no_result);
                }
            } else {
                ToastUtil.show(MainActivity.this,
                        R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }

    }

    public void onPoiItemSearched(PoiItem poiItem, int errorCode) {

    }

}

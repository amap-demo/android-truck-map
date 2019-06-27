package com.amap.demo.truckmap.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.demo.truckmap.mode.TruckLimitAreaRequest;
import com.amap.demo.truckmap.utilTools.utils;
import com.google.gson.Gson;

import java.util.ArrayList;

public class TruckRouteView {
    private Context mContext;
    protected AMap mAMap;
    ArrayList<ArrayList<LatLng>> areaLists;
    ArrayList<ArrayList<LatLng>> lineLists;

    public TruckRouteView(Context context, AMap amap) {
        mContext = context;
        mAMap = amap;
        areaLists = new ArrayList<ArrayList<LatLng>>();
        lineLists = new ArrayList<ArrayList<LatLng>>();
    }

    public void addToMap() {
        byte[] buffer = utils.openAsset(mContext, "Trucklimit.txt");
        String string = new String(buffer);
        Gson gson = new Gson();
        TruckLimitAreaRequest truckLimitAreaRequest = gson.fromJson(string,TruckLimitAreaRequest.class );

        for (int i = 0; i < truckLimitAreaRequest.getAreas().size(); i++) {
            ArrayList<LatLng> areaList = new ArrayList<LatLng>();
            TruckLimitAreaRequest.AreasBean areasBean = truckLimitAreaRequest.getAreas().get(i);
            String area = areasBean.getArea();
            if (!TextUtils.isEmpty(area)) {
                String[] latlanString = area.split(";");
                for (String s : latlanString) {
                    String[] ll = s.split(",");
                    if (ll.length != 2) {
                        continue;
                    }
                    areaList.add(new LatLng(Double.parseDouble(ll[1]),Double.parseDouble(ll[0])));
                }
            }

            areaLists.add(areaList);

            String lines = areasBean.getLine();
            if (!TextUtils.isEmpty(lines)) {

                if (lines.contains("|")) {
                    String[] lineArray = lines.split("\\|");
                    for ( String strLine : lineArray) {
                        ArrayList<LatLng> lineList = new ArrayList<LatLng>();
                        String[] latlanString = strLine.split(";");
                        for (String s : latlanString) {
                            String[] ll = s.split(",");
                            if (ll.length != 2) {
                                continue;
                            }
                            double test1 = Double.parseDouble(ll[0]);
                            double test2 = Double.parseDouble(ll[1]);
                            lineList.add(new LatLng(test2,test1));
                        }
                        lineLists.add(lineList);
                    }
                } else {
                    ArrayList<LatLng> lineList = new ArrayList<LatLng>();
                    String[] latlanString = lines.split(";");
                    for (String s : latlanString) {
                        String[] ll = s.split(",");
                        if (ll.length != 2) {
                            continue;
                        }
                        double test1 = Double.parseDouble(ll[0]);
                        double test2 = Double.parseDouble(ll[1]);
                        lineList.add(new LatLng(test2,test1));
                    }
                    lineLists.add(lineList);
                }

            }

        }



        for (int i = 0; i < areaLists.size(); i ++) {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(areaLists.get(i));
            polygonOptions.strokeColor(Color.RED);
            polygonOptions.fillColor(Color.parseColor("#88EEAAAA"));
            polygonOptions.strokeWidth(5);
            mAMap.addPolygon(polygonOptions);
        }

        for (int i = 0; i < lineLists.size(); i++) {
            ArrayList<LatLng> line = lineLists.get(i);
            PolylineOptions polylineOptions  = new PolylineOptions();
            polylineOptions.addAll(line);
            polylineOptions.color(Color.RED);
            polylineOptions.width(5);
            mAMap.addPolyline(polylineOptions);
        }
    }
}

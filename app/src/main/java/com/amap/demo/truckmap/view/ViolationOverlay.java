package com.amap.demo.truckmap.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.demo.truckmap.mode.TrafficInfo;
import com.amap.demo.truckmap.utilTools.utils;
import com.google.gson.Gson;
import com.amap.demo.truckmap.R;

import java.util.ArrayList;

public class ViolationOverlay {
    private Context mContext;
    protected AMap mAMap;
    private ArrayList<LatLng> cameraList = new ArrayList<LatLng>();
    private ArrayList<LatLng> speed80List= new ArrayList<LatLng>();
    private ArrayList<LatLng> speed60List= new ArrayList<LatLng>();

    private ArrayList<Marker> mCameraMarks = new ArrayList<Marker>();
    private ArrayList<Marker> mSpped80Marks = new ArrayList<Marker>();
    private ArrayList<Marker> mSpped60Marks = new ArrayList<Marker>();
    private BitmapDescriptor bd80;
    private BitmapDescriptor camera;


    public ViolationOverlay(Context context, AMap amap) {
        mContext    = context;
        mAMap       = amap;
        bd80        = BitmapDescriptorFactory.fromResource(R.drawable.cesupoin);
        camera = BitmapDescriptorFactory.fromResource(R.drawable.camera);
    }

    public void addToMap() {
        byte[] buffer = utils.openAsset(mContext, "traffic.txt");
        String string = new String(buffer);
        Gson gson = new Gson();
        TrafficInfo trafficInfo = gson.fromJson(string, TrafficInfo.class );

        String camera = trafficInfo.getCamera();
        String speed80 = trafficInfo.getLimitspeed().getSpeed80();

        String[] cameraString = camera.split(";");
        for (String s : cameraString) {
            String[] ll = s.split(",");
            if (ll.length != 2) {
                continue;
            }
            cameraList.add(new LatLng(Double.parseDouble(ll[1]),Double.parseDouble(ll[0])));
        }

        String[] speed80String = speed80.split(";");
        for (String s : speed80String) {
            String[] ll = s.split(",");
            if (ll.length != 2) {
                continue;
            }
            speed80List.add(new LatLng(Double.parseDouble(ll[1]),Double.parseDouble(ll[0])));
        }

        for (int i = 0 ; i < cameraList.size(); i++) {
            Marker marker = mAMap.addMarker(getMarkerOptions(cameraList, i, 1));
            marker.setObject(i);

            mCameraMarks.add(marker);
        }

        for (int i = 0 ; i < speed80List.size(); i++) {
            Marker marker = mAMap.addMarker(getMarkerOptions(speed80List, i, 2));
            marker.setObject(i);

            mSpped80Marks.add(marker);
        }
    }

    public void addTrafficToMap() {
        for (int i = 0 ; i < cameraList.size(); i++) {
            Marker marker = mAMap.addMarker(getMarkerOptions(cameraList, i, 1));
            marker.setObject(i);

            mCameraMarks.add(marker);
        }

        for (int i = 0 ; i < speed80List.size(); i++) {
            Marker marker = mAMap.addMarker(getMarkerOptions(speed80List, i, 2));
            marker.setObject(i);

            mSpped80Marks.add(marker);
        }
    }

    public void removeTrafficFormMap() {
        for (Marker mark : mCameraMarks) {
            mark.remove();
        }
        mCameraMarks.clear();

        for (Marker mark : mSpped60Marks) {
            mark.remove();
        }
        mSpped60Marks.clear();

        for (Marker mark : mSpped80Marks) {
            mark.remove();
        }
        mSpped80Marks.clear();
    }


    private MarkerOptions getMarkerOptions(ArrayList<LatLng> latLngs, int index, int type) {
        return new MarkerOptions()
                .position(
                        new LatLng(latLngs.get(index).latitude, latLngs.get(index).longitude))
                .icon(getBitmapDescriptor(index, type));
    }

    protected BitmapDescriptor getBitmapDescriptor(int index, int type) {
        if (type  ==1) {
            return camera;
        } else if (type == 2 ) {
            return bd80;
        } else {
            return camera;
        }

    }
}

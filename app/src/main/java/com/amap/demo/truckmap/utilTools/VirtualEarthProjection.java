package com.amap.demo.truckmap.utilTools;

import android.graphics.Point;

import com.autonavi.amap.mapcore.DPoint;

/**
 * @author zxy
 */
public class VirtualEarthProjection {
    public static final int MAXZOOMLEVEL = 20;
    public static final int PIXELS_PER_TILE = 256;
    public static final double MIN_LATITUDE = -85.0511287798 ;
    public static final double MAX_LATITUDE = 85.0511287798;
    public static final double MIN_LONGITUDE = -180 * 2;
    public static final double MAX_LONGITUDE = 180 * 2;
    public static final int EARTH_RADIUS_IN_METERS = 6378137;
    public static final int TILE_SPLIT_LEVEL = 0;

    /** 256 * 1<<20 */
    private static final int KMA_MAX_MAP_SIZE = 268435456;
    private static final double K_EARTH_CIRCLE =     40075016.6855785724052f;
    private static final double K_EARTH_CIRCLE_2 =    20037508.3427892862026f;

    public static final double EARTH_CIRCUMFERENCE_IN_METERS = 2 * Math.PI
            * EARTH_RADIUS_IN_METERS;

    public VirtualEarthProjection() {
    }

    public static double clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    public static Point latLongToPixels(int longitude, int latitude, int levelOfDetail) {
        return latLongToPixels((double) latitude / 3600000,
                (double) longitude / 3600000, levelOfDetail);
    }

    /**
     * 转角度
     * @param xARc
     * @return
     */
    private static double radToDeg(double xARc) {
        //* 180 / Math.PI
        return xARc* 57.29577951308232;
    }

    private static double degToRad(double deg) {
        // *  Math.PI / 180
        return (deg)*0.0174532925199432958;
    }


    /**
     * 转投影坐标
     * @param latitude
     * @param longitude
     * @param levelOfDetail 使用默认值20
     * @return
     */
    public static Point latLongToPixels(double latitude, double longitude,
                                        int levelOfDetail) {

        Point rPnt = new Point();
        latitude = clip(latitude, MIN_LATITUDE, MAX_LATITUDE);
        longitude = clip(longitude, MIN_LONGITUDE, MAX_LONGITUDE);

        double metersPerPixel = K_EARTH_CIRCLE / (double) KMA_MAX_MAP_SIZE;

        double xMeters = EARTH_RADIUS_IN_METERS * degToRad(longitude);

        double sinY = Math.sin(degToRad(latitude));
        double logY = Math.log((1 + sinY) / (1 - sinY));
        double yMeters = EARTH_RADIUS_IN_METERS * logY / 2.0;

        double xPoint = (K_EARTH_CIRCLE_2 + xMeters) / metersPerPixel;
        double yPoint = (K_EARTH_CIRCLE_2 - yMeters) / metersPerPixel;

        rPnt.x = (int) xPoint;
        rPnt.y = (int) yPoint;
        return rPnt;
    }

    public static DPoint pixelsToLatLong(long xPixel, long yPixel, int levelOfDetail) {
        DPoint rPnt = DPoint.obtain();

        double metersPerPixel = K_EARTH_CIRCLE / (double) KMA_MAX_MAP_SIZE;

        double xMeters = (xPixel * metersPerPixel) - K_EARTH_CIRCLE_2;
        double yMeters = K_EARTH_CIRCLE_2 - (yPixel * metersPerPixel);

        double xArc = xMeters / EARTH_RADIUS_IN_METERS;
        rPnt.x = radToDeg(xArc);

        double yArc = yMeters / EARTH_RADIUS_IN_METERS;
        double fexp = Math.exp(yArc * 2.0);
        double sinx = (fexp - 1.0) / (1.0 + fexp);
        rPnt.y = radToDeg(Math.asin(sinx));

        return rPnt;
    }
}
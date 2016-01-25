package com.zkjinshi.svip.map;


import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;

/**
 * 高德路径规划管理器
 * 开发者：杜健德
 * 日期：2016/01/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class RouteManager {

    public static final String TAG = RouteManager.class.getSimpleName();

    private RouteManager(){}
    private static RouteManager instance;
    private RouteSearch routeSearch;
    private DriveRouteResult driveRouteResult;// 驾车模式查询结果

    public static synchronized RouteManager getInstance(){
        if(null == instance){
            instance = new RouteManager();
        }
        return instance;
    }

    public void init(Context context){
        routeSearch = new RouteSearch(context);
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
                if (rCode == 0) {
                    if (result != null && result.getPaths() != null && result.getPaths().size() > 0)
                    {
                        driveRouteResult = result;
                        DrivePath drivePath = driveRouteResult.getPaths().get(0);
                       float distance = drivePath.getDistance();
                       long time = drivePath.getDuration();
                        LogUtil.getInstance().info(LogLevel.DEBUG, "距离："+distance + "米， 时间："+time+"秒");
                    } else {
                        LogUtil.getInstance().info(LogLevel.DEBUG, "高德地图 无结果");
                    }
                } else if (rCode == 27) {
                    LogUtil.getInstance().info(LogLevel.DEBUG, "高德地图 网络出错");
                } else if (rCode == 32) {
                    LogUtil.getInstance().info(LogLevel.DEBUG, "高德地图KEY错误");
                } else {
                    LogUtil.getInstance().info(LogLevel.DEBUG, "高德地图 其他错误:"+rCode);
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }
        });
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(
                fromAndTo,
                RouteSearch.DrivingDefault,
                null,
                null,
                ""
        );// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        LogUtil.getInstance().info(LogLevel.DEBUG,"高德地图路线规划开启（"+startPoint.toString()+" 到 "+endPoint.toString()+"）");
        routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
    }




}

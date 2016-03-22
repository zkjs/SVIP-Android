package com.zkjinshi.pyxis.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;


import com.zkjinshi.pyxis.utils.DistanceUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


@SuppressLint("NewApi")
public class IBeaconManager {
	public static String TAG = IBeaconManager.class.getSimpleName();

	private static final long SCAN_DELAY_TIME = 1000L;
	private static final float CHECK_DISTANCE = 2F;
	private int scantime_dp = 0;
	private Timer timer;

	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;
	private Context context;
	private HashMap<String,String> filterMap;


	private static IBeaconManager instance;

	private IBeaconManager(){}
	
	public synchronized static IBeaconManager getInstance(){
		if(null == instance){
			instance = new IBeaconManager();
		}
		return instance;
	}

	public IBeaconManager init(Context context){
		this.context = context;
		scantime_dp = 0;
		filterMap = new HashMap<String,String>();
		filterMap.put("fda50693-a4e2-4fb1-afcf-c6eb07647835","1");
		filterMap.put("931ddf8e-10e4-11e5-9493-1697f925ec7b","1");
		return  this;
	}


	public IBeaconManager initScanDevices(){
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			bluetoothManager =
					(BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = bluetoothManager.getAdapter();
		}
		return this;
	}

	public void scanLeDevice() {
		Log.d(TAG,"scanLeDevice");
		cancelScheduleScan();
		TimerTask task = new TimerTask(){
			public void run() {
				scantime_dp = ((scantime_dp + 1) % 2);
				switch (scantime_dp) {
					case 0:
						stopScan();
						break;
					case 1:
						startScan();
						break;
				}
			}
		};
		this.timer = new Timer(true);
		this.timer.schedule(task, SCAN_DELAY_TIME, IBeaconController.scanPeriodTime);
	}

	public void startScan(){
		Log.d(TAG,"startScan");
		if(null == bluetoothAdapter){
			return;
		}
		bluetoothAdapter.enable();
		bluetoothAdapter.startLeScan(mLeScanCallback);
	}

	public void stopScan(){
		Log.d(TAG,"stopScan");
		bluetoothAdapter.stopLeScan(mLeScanCallback);
		if( !IBeaconContext.getInstance().getiBeaconMap().isEmpty()){
			Iterator<Map.Entry<String, IBeaconVo>> iterator = IBeaconContext.getInstance().getiBeaconMap().entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, IBeaconVo> entry = iterator.next();
				IBeaconVo iBeaconVo = entry.getValue();
				//连续5次扫描不到就认为离开该区域
				if(iBeaconVo != null && iBeaconVo.getDisappearTime() > IBeaconController.failScanTime){
					leaveArea(iBeaconVo);
				}else{
					iBeaconVo.setDisappearTime(iBeaconVo.getDisappearTime() + 1);
					IBeaconContext.getInstance().getiBeaconMap().put(iBeaconVo.getBeaconKey(),iBeaconVo);
				}

			}
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {
				@Override
				public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
					IBeaconVo ibeacon = IBeaconHelper.fromScanData(device, rssi, scanRecord);
					if(null != ibeacon){
						//Log.d(TAG,ibeacon.toString());
						if(filterMap.containsKey(ibeacon.getProximityUuid()) ){
							//NetBeaconVo netBeaconVo =  IBeaconContext.getInstance().getNetBeaconMap().get(ibeacon.getBeaconKey());
							long currentTime = System.currentTimeMillis();
							double distance = DistanceUtil.calculateAccuracy(ibeacon.getTxPower(),ibeacon.getRssi());
							//计算得到的属性
							ibeacon.setTimestamp(currentTime);
							ibeacon.setDistance(distance);
							ibeacon.setDisappearTime(0);
							//从API得到的Beacon属性（注释原因：根据UUID做过滤，暂时不需要该实体）
//							ibeacon.setLocid(netBeaconVo.getLocid());
//							ibeacon.setShopid(netBeaconVo.getShopid());
//							ibeacon.setSensorid(netBeaconVo.getSensorid());
//							ibeacon.setUuid(netBeaconVo.getUuid());
//							ibeacon.setMinior(netBeaconVo.getMinior());
//							ibeacon.setLocdesc(netBeaconVo.getLocdesc());
//							ibeacon.setStatus(netBeaconVo.getStatus());
//							ibeacon.setRemark(netBeaconVo.getRemark());

							intoArea(ibeacon);
						}
					}
				}
			};



	private void intoArea(IBeaconVo ibeacon){
		IBeaconSubject.getInstance().notifyObserversScan(ibeacon);
		//第一次扫描到
		if(!IBeaconContext.getInstance().getiBeaconMap().containsKey(ibeacon.getBeaconKey())){
			if(ibeacon.getDistance() <= CHECK_DISTANCE){
				IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
				IBeaconSubject.getInstance().notifyObserversInto(ibeacon);
			}else{
				IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
			}
		}else{//重复扫描到
			IBeaconVo preBeaconVo = IBeaconContext.getInstance().getiBeaconMap().get(ibeacon.getBeaconKey());
			if(preBeaconVo.getDistance() > CHECK_DISTANCE && ibeacon.getDistance() <= CHECK_DISTANCE){
				IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
				IBeaconSubject.getInstance().notifyObserversInto(ibeacon);
			}else{
				IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
			}
		}


	}

	private void leaveArea(IBeaconVo ibeacon){
		IBeaconSubject.getInstance().notifyObserversOut(ibeacon);
		IBeaconContext.getInstance().getiBeaconMap().remove(ibeacon.getBeaconKey());
	}

	public void cancelScheduleScan(){
		Log.d(TAG,"cancelScheduleScan");
		if(this.timer != null){
			this.timer.cancel();
			this.timer = null;
		}
	}

}

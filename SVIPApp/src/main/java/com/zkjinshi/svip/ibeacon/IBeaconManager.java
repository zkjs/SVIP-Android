package com.zkjinshi.svip.ibeacon;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;

import com.zkjinshi.svip.utils.CacheUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时检查用户是否离开某个beacon区域
 */
@SuppressLint("NewApi")
public class IBeaconManager {

	private long intoTimestamp;
	private String regionKey;
	private long currentTimestamp;
	private Timer outCheckTimer;
	public static final int CHECK_AREA_DURATION = 2*60*1000;
	public static final int OUT_AREA_DURATION = 5*60*1000;
	private Map<String, Long> checkCycleMap;
	private Iterator<Map.Entry<String, Long>> iterator;
	private Map.Entry<String, Long> entry;
	private RegionVo regionVo;
	private IBeaconEntity beaconEntity;
	private String shopId;
	private Map<String,RegionVo> regionCycleyMap;

	private boolean scanning;
	private Handler mHandler;
	private static final long SCAN_PERIOD = 30000;

	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;
	private Context context;
	private IBeaconVo ibeacon;
	
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
		return  this;
	}

	public void stopScan(){
		scanLeDevice(false);
		stopOutCheckTimer();
	}

	public IBeaconManager initScanDevices(){
		mHandler = new Handler();
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			bluetoothManager =
					(BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = bluetoothManager.getAdapter();
			if(null != bluetoothAdapter){
				bluetoothAdapter.enable();
			}
		}
		return this;
	}

	public void scanLeDevice(final boolean enable) {
		if (enable) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					scanning = false;
					bluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);
			scanning = true;
			bluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			scanning = false;
			bluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {
				@Override
				public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
					ibeacon = IBeaconHelper.fromScanData(device, rssi, scanRecord);
					if(null != ibeacon){
						if(IBeaconContext.getInstance().getBeanconMap().containsKey(ibeacon.getBeaconKey())){
							IBeaconEntity beaconEntity =  IBeaconContext.getInstance().getBeanconMap().get(ibeacon.getBeaconKey());
							RegionVo.Builder builder = new RegionVo.Builder();
							RegionVo regionVo = builder.setiBeacon(beaconEntity).build();
							long currentTime = System.currentTimeMillis();
							IBeaconContext.getInstance().putCheck(regionVo,currentTime);
							IBeaconContext.getInstance().putRegion(regionVo,currentTime);
						}
					}
				}
			};


	/**
	 * 开始检查定时器
	 */
	public void startCheckOutTimer() {
		if (null != outCheckTimer) {
			outCheckTimer.cancel();
			outCheckTimer = null;
		}
		outCheckTimer = new Timer();
		outCheckTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				checkCycleMap = (ConcurrentHashMap<String, Long>) IBeaconContext.getInstance().getCheckCycleMap();
				if (null != checkCycleMap) {
					iterator = checkCycleMap.entrySet().iterator();
					while (iterator.hasNext()) {
						entry = iterator.next();
						intoTimestamp = entry.getValue();
						regionKey = entry.getKey();
						currentTimestamp = System.currentTimeMillis();
						if (currentTimestamp - intoTimestamp > OUT_AREA_DURATION) {//超过五分钟
							regionCycleyMap = IBeaconContext.getInstance().getRegionCycleyMap();
							regionVo = regionCycleyMap.get(regionKey);
							IBeaconContext.getInstance().removeCheck(regionVo);
							IBeaconSubject.getInstance().notifyObserversOut(regionVo);
							beaconEntity = regionVo.getiBeacon();
							if(null != beaconEntity){
								shopId = beaconEntity.getShopid();
								if(!TextUtils.isEmpty(shopId)){
									CacheUtil.getInstance().setInArea(shopId,false);
								}
							}
						}
					}
				}
			}
		}, CHECK_AREA_DURATION, CHECK_AREA_DURATION);
	}

	/**
	 * 停止检查定时器
	 */
	public void stopOutCheckTimer() {
		if (null != outCheckTimer) {
			outCheckTimer.cancel();
			outCheckTimer = null;
		}
	}
}

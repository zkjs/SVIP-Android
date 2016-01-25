package com.zkjinshi.svip.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.zkjinshi.base.log.LogLevel;
import com.zkjinshi.base.log.LogUtil;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.DistanceUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


@SuppressLint("NewApi")
public class IBeaconManager {

	private static final long SCAN_DELAY_TIME = 1000L;
	private static final long SCAN_PERIOD_TIME = 1500L;
	private static final int FAIL_SCAN_TIME = 4;

	private int scantime_dp = 0;
	private Timer timer;

	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;
	private Context context;


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
		LogUtil.getInstance().info(LogLevel.DEBUG, "scanLeDevice");
		cancelScheduleScan();
		TimerTask task = new TimerTask(){
			public void run() {
				scantime_dp = ((scantime_dp + 1) % 3);
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
		this.timer.schedule(task, SCAN_DELAY_TIME, SCAN_PERIOD_TIME);
	}

	private void startScan(){
		//LogUtil.getInstance().info(LogLevel.DEBUG, "startScan");
		if( !CacheUtil.getInstance().isLogin()){
			return;
		}
		if(null == bluetoothAdapter){
			return;
		}
		bluetoothAdapter.enable();
		bluetoothAdapter.startLeScan(mLeScanCallback);
	}

	private void stopScan(){
		//LogUtil.getInstance().info(LogLevel.DEBUG, "stopScan");
		bluetoothAdapter.stopLeScan(mLeScanCallback);
		if( !IBeaconContext.getInstance().getiBeaconMap().isEmpty()){
			Iterator<Map.Entry<String, IBeaconVo>> iterator = IBeaconContext.getInstance().getiBeaconMap().entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, IBeaconVo> entry = iterator.next();
				IBeaconVo iBeaconVo = entry.getValue();
				//连续5次扫描不到就认为离开该区域
				if(iBeaconVo != null && iBeaconVo.getDisappearTime() > FAIL_SCAN_TIME){
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
						if(IBeaconContext.getInstance().getNetBeaconMap().containsKey(ibeacon.getBeaconKey()) && CacheUtil.getInstance().isLogin()){//只对登录用户进行到店记录
							NetBeaconVo netBeaconVo =  IBeaconContext.getInstance().getNetBeaconMap().get(ibeacon.getBeaconKey());
							long currentTime = System.currentTimeMillis();
							double distance = DistanceUtil.calculateAccuracy(ibeacon.getTxPower(),ibeacon.getRssi());
							//计算得到的属性
							ibeacon.setTimestamp(currentTime);
							ibeacon.setDistance(distance);
							ibeacon.setDisappearTime(0);
							//从API得到的Beacon属性
							ibeacon.setLocid(netBeaconVo.getLocid());
							ibeacon.setShopid(netBeaconVo.getShopid());
							ibeacon.setSensorid(netBeaconVo.getSensorid());
							ibeacon.setUuid(netBeaconVo.getUuid());
							ibeacon.setMinior(netBeaconVo.getMinior());
							ibeacon.setLocdesc(netBeaconVo.getLocdesc());
							ibeacon.setStatus(netBeaconVo.getStatus());
							ibeacon.setRemark(netBeaconVo.getRemark());

							intoArea(ibeacon);
						}
					}
				}
			};



	private void intoArea(IBeaconVo ibeacon){
		if(!IBeaconContext.getInstance().getiBeaconMap().containsKey(ibeacon.getBeaconKey())){
			IBeaconSubject.getInstance().notifyObserversInto(ibeacon);
		}
		IBeaconContext.getInstance().getiBeaconMap().put(ibeacon.getBeaconKey(),ibeacon);
	}

	private void leaveArea(IBeaconVo ibeacon){
		IBeaconSubject.getInstance().notifyObserversOut(ibeacon);
		IBeaconContext.getInstance().getiBeaconMap().remove(ibeacon.getBeaconKey());
	}

	public void cancelScheduleScan(){
		if(this.timer != null){
			this.timer.cancel();
			this.timer = null;
		}
	}

}

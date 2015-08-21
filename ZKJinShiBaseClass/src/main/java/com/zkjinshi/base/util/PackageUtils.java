package com.zkjinshi.base.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 包管理器封装工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class PackageUtils {

	/**
	 * 获取所有已安装的应用包
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getInstalledPackages(Context context) {
		return context.getPackageManager().getInstalledPackages(0);
	}

	/**
	 * 获取所有已安装的系统程序
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getInstalledPackagesOfSystem(Context context) {
		List<PackageInfo> installedPackages = getInstalledPackages(context);
		List<PackageInfo> installedPackagesOfSystems = new ArrayList<PackageInfo>();
		for (PackageInfo info : installedPackages) {
			if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				installedPackagesOfSystems.add(info);
			}
		}
		return installedPackagesOfSystems;
	}

	/**
	 * 获取所有已安装的第三方应用
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getInstalledPackagesOfThirdPart(
			Context context) {
		List<PackageInfo> installedPackages = getInstalledPackages(context);
		List<PackageInfo> installedPackagesOfThirds = new ArrayList<PackageInfo>();
		for (PackageInfo info : installedPackages) {
			if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				installedPackagesOfThirds.add(info);
			}
		}
		return installedPackagesOfThirds;
	}

	/**
	 * 获取所有已安装并且有启动快捷图标的应用程序
	 * 
	 * @param context
	 */
	public static List<PackageInfo> getInstalledAppOfLauncher(Context context) {
		List<PackageInfo> installedList = new ArrayList<PackageInfo>();
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> appList = manager.getInstalledPackages(0);
		for (PackageInfo packageInfo : appList) {
			Intent intent = manager
					.getLaunchIntentForPackage(packageInfo.packageName);
			if (intent != null) {
				installedList.add(packageInfo);
			}
		}

		return installedList;
	}

	/**
	 * 获取所有已安装的并且有启动图标的系统应用
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getInstalledAppOfSystem(Context context) {
		List<PackageInfo> installedAppOfLauncher = getInstalledAppOfLauncher(context);
		List<PackageInfo> installedOfSystems = new ArrayList<PackageInfo>();
		for (PackageInfo info : installedAppOfLauncher) {
			if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				installedOfSystems.add(info);
			}
		}
		return installedOfSystems;
	}

	/**
	 * 获取所有正在运行的程序的包信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getRunningApp(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取正在运行的应用
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		List<PackageInfo> appofLauncher = getInstalledAppOfLauncher(context);
		List<PackageInfo> ls = new ArrayList<PackageInfo>();
		for (RunningAppProcessInfo info : runningAppProcesses) {

			PackageInfo pi = getPackageInfo(context, info.processName);
			if (pi == null) {
				continue;
			}

			for (PackageInfo pInfo : appofLauncher) {
				if (info.processName.equals("com.handaer.lsl")) {
					continue;
				}
				if (info.processName.equals(pInfo.packageName)) {
					ls.add(pInfo);
				}
			}
		}

		return ls;
	}

	/**
	 * 获取正在运行的第三方应用的包信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getRunningAppOfThird(Context context) {
		List<PackageInfo> runningApps = getRunningApp(context);
		List<PackageInfo> runningAppOfThirds = new ArrayList<PackageInfo>();
		for (PackageInfo info : runningApps) {
			if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				runningAppOfThirds.add(info);
			}
		}
		return runningAppOfThirds;
	}

	/**
	 * 获取所有已安装的并且有启动图标的第三方应用
	 * 
	 * @param context
	 * @return
	 */
	public static List<PackageInfo> getInstalledAppOfThirdPart(Context context) {
		List<PackageInfo> installedAppOfLauncher = getInstalledAppOfLauncher(context);
		List<PackageInfo> installedOfThirds = new ArrayList<PackageInfo>();
		for (PackageInfo info : installedAppOfLauncher) {
			if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				installedOfThirds.add(info);
			}
		}
		return installedOfThirds;
	}

	/**
	 * 根据包名判断程序是否已安装
	 *
	 * @param context
	 * @return
	 */
	public static boolean isInstalled(Context context) {
		PackageInfo packageInfo;
		String packageName = context.getPackageName();
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, 0);

		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 根据包名判断程序是否已安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isInstalled(Context context, String packageName) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, 0);

		} catch (NameNotFoundException e) {
			packageInfo = null;
			 e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 根据包名获取包信息
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context, String packageName) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
		}
		return packageInfo;
	}

	/**
	 * 获取已安装应用版本号
	 *
	 * @param context
	 * @return
	 */
	public static int getInstallVersionCode(Context context) {
		return getPackageInfo(context, context.getPackageName()).versionCode;
	}

	public static String getVersionName(Context context) {
		return getPackageInfo(context, context.getPackageName()).versionName;
	}


	/**
	 * 获取已安装应用版本号
	 *
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static int getInstallVersionCode(Context context,String packageName) {
		return getPackageInfo(context,packageName).versionCode;
	}

	public static String getVersionName(Context context,String packageName) {
		return getPackageInfo(context, packageName).versionName;
	}

	/**
	 * 获取应用的显示名
	 * 
	 * @param context
	 * @param info
	 * @return
	 */
	public static String getLoadLabel(Context context, PackageInfo info) {

		return info.applicationInfo.loadLabel(context.getPackageManager())
				.toString();
	}

	/**
	 * 获取应用的启动图标
	 * 
	 * @param context
	 * @param info
	 * @return
	 */
	public static Drawable getLoadIcon(Context context, PackageInfo info) {
		return info.applicationInfo.loadIcon(context.getPackageManager());
	}

	/**
	 * 杀死后台进程
	 * 
	 * @param context
	 */
	public static void killBackgroundProcesses(Context context) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<PackageInfo> runningAppOfThird = getRunningAppOfThird(context);
		try {
			for (PackageInfo info : runningAppOfThird) {
				am.killBackgroundProcesses(info.packageName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得应用启动次数
	 *
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static long getAppUsedCount(Context context, String packageName) {
		int aLaunchCount = 0;
		PackageManager pm = context.getPackageManager();
		Intent i = pm.getLaunchIntentForPackage(packageName);
		ComponentName aName = i.getComponent();
		Log.i("TAG", aName.toString());
		try {
			// 获得ServiceManager类
			Class<?> ServiceManager = Class
					.forName("android.os.ServiceManager");
			// 获得ServiceManager的getService方法
			Method getService = ServiceManager.getMethod("getService",
					String.class);
			// 调用getService获取RemoteService
			Object oRemoteService = getService.invoke(null, "usagestats");
			// 获得IUsageStats.Stub类
			Class<?> cStub = Class
					.forName("com.android.internal.app.IUsageStats$Stub");
			// 获得asInterface方法
			Method asInterface = cStub.getMethod("asInterface",
					android.os.IBinder.class);
			// 调用asInterface方法获取IUsageStats对象
			Object oIUsageStats = asInterface.invoke(null, oRemoteService);
			// 获得getPkgUsageStats(ComponentName)方法
			Method getPkgUsageStats = oIUsageStats.getClass().getMethod(
					"getPkgUsageStats", ComponentName.class);
			getPkgUsageStats.setAccessible(true);
			// 调用getPkgUsageStats 获取PkgUsageStats对象
			Object aStats = getPkgUsageStats.invoke(oIUsageStats, aName);
			// 获得PkgUsageStats类
			Class<?> PkgUsageStats = Class
					.forName("com.android.internal.os.PkgUsageStats");
			aLaunchCount = PkgUsageStats.getDeclaredField("launchCount")
					.getInt(aStats);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return aLaunchCount;
	}

	/**
	 * 启动应用
	 *
	 * @param ctx
	 * @param packageName
	 */
	public static void launcherApp(final Context ctx, String packageName) {
		PackageInfo pi = null;
		try {
			pi = ctx.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pi.packageName);
		PackageManager pm = ctx.getPackageManager();
		List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
		ResolveInfo ri = apps.iterator().next();
		if (ri != null) {
			// 第三方应用
			String className = ri.activityInfo.name;
			Intent intent = new Intent(Intent.ACTION_MAIN);
			ComponentName cn = new ComponentName(packageName, className);
			intent.setComponent(cn);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ctx.startActivity(intent);
		}
	}

}

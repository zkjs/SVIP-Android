package com.zkjinshi.pyxis.bluetooth;

import android.bluetooth.BluetoothDevice;


/**
 * IBeacon解析帮助类
 * 开发者：dujiande
 * 日期：2015/8/5
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class IBeaconHelper {

    /**
     * 解析IBeacon数据
     * @param device
     * @param rssi
     * @param scanData
     * @return
     */
    public static IBeaconVo fromScanData(BluetoothDevice device, int rssi, byte[] scanData) {

        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int)scanData[startByte+2] & 0xff) == 0x02 &&
                    ((int)scanData[startByte+3] & 0xff) == 0x15) {
                patternFound = true;
                break;
            }
            else if (((int)scanData[startByte] & 0xff) == 0x2d &&
                    ((int)scanData[startByte+1] & 0xff) == 0x24 &&
                    ((int)scanData[startByte+2] & 0xff) == 0xbf &&
                    ((int)scanData[startByte+3] & 0xff) == 0x16) {
                IBeaconVo iBeacon = new IBeaconVo();
                iBeacon.setMajor(0);
                iBeacon.setMinor(0);
                iBeacon.setProximityUuid("00000000-0000-0000-0000-000000000000");
                iBeacon.setTxPower(-55);
                return iBeacon;
            }
            else if (((int)scanData[startByte] & 0xff) == 0xad &&
                    ((int)scanData[startByte+1] & 0xff) == 0x77 &&
                    ((int)scanData[startByte+2] & 0xff) == 0x00 &&
                    ((int)scanData[startByte+3] & 0xff) == 0xc6) {

                IBeaconVo iBeacon = new IBeaconVo();
                iBeacon.setMajor(0);
                iBeacon.setMinor(0);
                iBeacon.setProximityUuid("00000000-0000-0000-0000-000000000000");
                iBeacon.setTxPower(-55);
                return iBeacon;
            }
            startByte++;
        }


        if (patternFound == false) {
            // This is not an iBeacon
            return null;
        }

        IBeaconVo iBeacon = new IBeaconVo();
        iBeacon.setMajor((scanData[startByte+20] & 0xff) * 0x100 + (scanData[startByte+21] & 0xff));
        iBeacon.setMinor((scanData[startByte + 22] & 0xff) * 0x100 + (scanData[startByte + 23] & 0xff));
        iBeacon.setTxPower((int)scanData[startByte+24]);
        iBeacon.setRssi(rssi);

        byte[] proximityUuidBytes = new byte[16];
        System.arraycopy(scanData, startByte+4, proximityUuidBytes, 0, 16);
        String hexString = bytesToHexString(proximityUuidBytes);
        StringBuilder sb = new StringBuilder();
        sb.append(hexString.substring(0,8));
        sb.append("-");
        sb.append(hexString.substring(8,12));
        sb.append("-");
        sb.append(hexString.substring(12,16));
        sb.append("-");
        sb.append(hexString.substring(16,20));
        sb.append("-");
        sb.append(hexString.substring(20,32));
        iBeacon.setProximityUuid(sb.toString());

        if (device != null) {
            iBeacon.setBluetoothAddress(device.getAddress());
            iBeacon.setName(device.getName());
        }

        return iBeacon;
    }

    /**
     * btye数组转换为16进制字符串
     * @param src
     * @return
     */
    private static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}

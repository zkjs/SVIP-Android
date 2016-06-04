package com.zkjinshi.ritz.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * @param dateString
     * @return
     */
    public static String switchDate2String(String dateString) {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt        = sdf.parse(dateString);
            String month   = addZero2String(dt.getMonth());
            String date    = addZero2String(dt.getDate());
            String hours   = addZero2String(dt.getHours());
            String minutes = addZero2String(dt.getMinutes());
            sb.append(month).append("-").append(date).append(" ").append(hours).append(":").append(minutes);
        } catch (ParseException e) {
            sb.append(dateString);
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static String addZero2String(int num){
        if(num < 10){
            return "0"+num;
        }
        return ""+num;
    }
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim())
                && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param mobiles
     * @return
     */
    public static boolean isPhoneNumber(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isNormalName(String name){
        Pattern p = Pattern.compile("^([\\u4E00-\\u9FA5A-Za-z]+\\s?)*[\\u4E00-\\u9FA5A-Za-z]$");
        Matcher m = p.matcher(name);
        return m.matches();
    }

    public static boolean isEnglishName(String name){
        Pattern p = Pattern.compile("^([A-Za-z]+\\s?)*[A-Za-z]$");
        Matcher m = p.matcher(name);
        return m.matches();
    }

    public static boolean isChineseName(String name){
        Pattern p = Pattern.compile("[\\u4E00-\\u9FA5]+");
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEquals(String... agrs) {
        String last = null;
        for (int i = 0; i < agrs.length; i++) {
            String str = agrs[i];
            if (isEmpty(str)) {
                return false;
            }
            if (last != null && !str.equalsIgnoreCase(last)) {
                return false;
            }
            last = str;
        }
        return true;
    }

    /**
x     *
     * @param content
     * @param color
     * @param start
     * @param end
     * @return
     */
    public static CharSequence getHighLightText(String content, int color,
                                                int start, int end) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        start = start >= 0 ? start : 0;
        end = end <= content.length() ? end : content.length();
        SpannableString spannable = new SpannableString(content);
        CharacterStyle span = new ForegroundColorSpan(color);
        spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static String formatFileSize(long len) {
        return formatFileSize(len, false);
    }

    public static String formatFileSize(long len, boolean keepZero) {
        String size;
        DecimalFormat formatKeepTwoZero = new DecimalFormat("#.00");
        DecimalFormat formatKeepOneZero = new DecimalFormat("#.0");
        if (len < 1024) {
            size = String.valueOf(len + "B");
        } else if (len < 10 * 1024) {
            size = String.valueOf(len * 100 / 1024 / (float) 100) + "KB";
        } else if (len < 100 * 1024) {
            size = String.valueOf(len * 10 / 1024 / (float) 10) + "KB";
        } else if (len < 1024 * 1024) {
            size = String.valueOf(len / 1024) + "KB";
        } else if (len < 10 * 1024 * 1024) {
            if (keepZero) {
                size = String.valueOf(formatKeepTwoZero.format(len * 100 / 1024
                        / 1024 / (float) 100))
                        + "MB";
            } else {
                size = String.valueOf(len * 100 / 1024 / 1024 / (float) 100)
                        + "MB";
            }
        } else if (len < 100 * 1024 * 1024) {
            // [10MB, 100MB)������һλС��
            if (keepZero) {
                size = String.valueOf(formatKeepOneZero.format(len * 10 / 1024
                        / 1024 / (float) 10))
                        + "MB";
            } else {
                size = String.valueOf(len * 10 / 1024 / 1024 / (float) 10)
                        + "MB";
            }
        } else if (len < 1024 * 1024 * 1024) {
            size = String.valueOf(len / 1024 / 1024) + "MB";
        } else {
            size = String.valueOf(len * 100 / 1024 / 1024 / 1024 / (float) 100)
                    + "GB";
        }
        return size;
    }

}

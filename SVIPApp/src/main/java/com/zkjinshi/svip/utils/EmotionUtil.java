package com.zkjinshi.svip.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

import com.zkjinshi.base.util.DisplayUtil;
import com.zkjinshi.svip.R;

/**
 * 表情图片解析工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/22
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class EmotionUtil {
	
	private EmotionUtil(){}
	private static EmotionUtil instance;
	public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");
	private Map<String, Integer> emotionMap = new LinkedHashMap<String, Integer>();
	public static final int NUM_PAGE = 2;// 总共有多少页
	public static int NUM = 27;// 每页27个表情,还有最后一个删除button
	public synchronized static EmotionUtil getInstance(){
		if(null ==  instance){
			instance = new EmotionUtil();
		}
		return instance;
	}
	/**
	 * 初始化表情库
	 * 
	 */
	public void initEmotion(){
		
//		emotionMap.put("[调皮]", R.mipmap.f001);
//		emotionMap.put("[流汗]", R.mipmap.f002);
//		emotionMap.put("[偷笑]", R.mipmap.f003);
//		emotionMap.put("[再见]", R.mipmap.f004);
//		emotionMap.put("[敲打]", R.mipmap.f005);
//		emotionMap.put("[擦汗]", R.mipmap.f006);
//		emotionMap.put("[猪头]", R.mipmap.f007);
//		emotionMap.put("[玫瑰]", R.mipmap.f008);
//		emotionMap.put("[流泪]", R.mipmap.f009);
//		emotionMap.put("[大哭]", R.mipmap.f010);
//		emotionMap.put("[嘘]", R.mipmap.f011);
//		emotionMap.put("[酷]", R.mipmap.f012);
//		emotionMap.put("[抓狂]", R.mipmap.f013);
//		emotionMap.put("[委屈]", R.mipmap.f014);
//		emotionMap.put("[便便]", R.mipmap.f015);
//		emotionMap.put("[炸弹]", R.mipmap.f016);
//		emotionMap.put("[菜刀]", R.mipmap.f017);
//		emotionMap.put("[可爱]", R.mipmap.f018);
//		emotionMap.put("[色]", R.mipmap.f019);
//		emotionMap.put("[害羞]", R.mipmap.f020);
//
//		emotionMap.put("[得意]", R.mipmap.f021);
//		emotionMap.put("[吐]", R.mipmap.f022);
//		emotionMap.put("[微笑]", R.mipmap.f023);
//		emotionMap.put("[发怒]", R.mipmap.f024);
//		emotionMap.put("[尴尬]", R.mipmap.f025);
//		emotionMap.put("[惊恐]", R.mipmap.f026);
//		emotionMap.put("[冷汗]", R.mipmap.f027);
//		emotionMap.put("[爱心]", R.mipmap.f028);
//		emotionMap.put("[示爱]", R.mipmap.f029);
//		emotionMap.put("[白眼]", R.mipmap.f030);
//		emotionMap.put("[傲慢]", R.mipmap.f031);
//		emotionMap.put("[难过]", R.mipmap.f032);
//		emotionMap.put("[惊讶]", R.mipmap.f033);
//		emotionMap.put("[疑问]", R.mipmap.f034);
//		emotionMap.put("[睡]", R.mipmap.f035);
//		emotionMap.put("[亲亲]", R.mipmap.f036);
//		emotionMap.put("[憨笑]", R.mipmap.f037);
//		emotionMap.put("[爱情]", R.mipmap.f038);
//		emotionMap.put("[衰]", R.mipmap.f039);
//		emotionMap.put("[撇嘴]", R.mipmap.f040);
//		emotionMap.put("[阴险]", R.mipmap.f041);
//
//		emotionMap.put("[奋斗]", R.mipmap.f042);
//		emotionMap.put("[发呆]", R.mipmap.f043);
//		emotionMap.put("[右哼哼]", R.mipmap.f044);
//		emotionMap.put("[拥抱]", R.mipmap.f045);
//		emotionMap.put("[坏笑]", R.mipmap.f046);
//		emotionMap.put("[飞吻]", R.mipmap.f047);
//		emotionMap.put("[鄙视]", R.mipmap.f048);
//		emotionMap.put("[晕]", R.mipmap.f049);
//		emotionMap.put("[大兵]", R.mipmap.f050);
//		emotionMap.put("[可怜]", R.mipmap.f051);
//		emotionMap.put("[强]", R.mipmap.f052);
//		emotionMap.put("[弱]", R.mipmap.f053);
//		emotionMap.put("[握手]", R.mipmap.f054);
//		emotionMap.put("[胜利]", R.mipmap.f055);
//		emotionMap.put("[抱拳]", R.mipmap.f056);
//		emotionMap.put("[凋谢]", R.mipmap.f057);
//		emotionMap.put("[饭]", R.mipmap.f058);
//		emotionMap.put("[蛋糕]", R.mipmap.f059);
//		emotionMap.put("[西瓜]", R.mipmap.f060);
//		emotionMap.put("[啤酒]", R.mipmap.f061);
//		emotionMap.put("[飘虫]", R.mipmap.f062);
//
//		emotionMap.put("[勾引]", R.mipmap.f063);
//		emotionMap.put("[OK]", R.mipmap.f064);
//		emotionMap.put("[爱你]", R.mipmap.f065);
//		emotionMap.put("[咖啡]", R.mipmap.f066);
//		emotionMap.put("[钱]", R.mipmap.f067);
//		emotionMap.put("[月亮]", R.mipmap.f068);
//		emotionMap.put("[美女]", R.mipmap.f069);
//		emotionMap.put("[刀]", R.mipmap.f070);
//		emotionMap.put("[发抖]", R.mipmap.f071);
//		emotionMap.put("[差劲]", R.mipmap.f072);
//		emotionMap.put("[拳头]", R.mipmap.f073);
//		emotionMap.put("[心碎]", R.mipmap.f074);
//		emotionMap.put("[太阳]", R.mipmap.f075);
//		emotionMap.put("[礼物]", R.mipmap.f076);
//		emotionMap.put("[足球]", R.mipmap.f077);
//		emotionMap.put("[骷髅]", R.mipmap.f078);
//		emotionMap.put("[挥手]", R.mipmap.f079);
//		emotionMap.put("[闪电]", R.mipmap.f080);
//		emotionMap.put("[饥饿]", R.mipmap.f081);
//		emotionMap.put("[困]", R.mipmap.f082);
//		emotionMap.put("[咒骂]", R.mipmap.f083);
//
//		emotionMap.put("[折磨]", R.mipmap.f084);
//		emotionMap.put("[抠鼻]", R.mipmap.f085);
//		emotionMap.put("[鼓掌]", R.mipmap.f086);
//		emotionMap.put("[糗大了]", R.mipmap.f087);
//		emotionMap.put("[左哼哼]", R.mipmap.f088);
//		emotionMap.put("[哈欠]", R.mipmap.f089);
//		emotionMap.put("[快哭了]", R.mipmap.f090);
//		emotionMap.put("[吓]", R.mipmap.f091);
//		emotionMap.put("[篮球]", R.mipmap.f092);
//		emotionMap.put("[乒乓球]", R.mipmap.f093);
//		emotionMap.put("[NO]", R.mipmap.f094);
//		emotionMap.put("[跳跳]", R.mipmap.f095);
//		emotionMap.put("[怄火]", R.mipmap.f096);
//		emotionMap.put("[转圈]", R.mipmap.f097);
//		emotionMap.put("[磕头]", R.mipmap.f098);
//		emotionMap.put("[回头]", R.mipmap.f099);
//		emotionMap.put("[跳绳]", R.mipmap.f100);
//		emotionMap.put("[激动]", R.mipmap.f101);
//		emotionMap.put("[街舞]", R.mipmap.f102);
//		emotionMap.put("[献吻]", R.mipmap.f103);
//		emotionMap.put("[左太极]", R.mipmap.f104);
//
//		emotionMap.put("[右太极]", R.mipmap.f105);
//		emotionMap.put("[闭嘴]", R.mipmap.f106);
//		emotionMap.put("[呲牙]", R.mipmap.f107);


		emotionMap.put("[):]", R.mipmap.ee_1);
		emotionMap.put("[:D]", R.mipmap.ee_2);
		emotionMap.put("[;)]", R.mipmap.ee_3);
		emotionMap.put("[:-o]", R.mipmap.ee_4);
		emotionMap.put("[:p]", R.mipmap.ee_5);
		emotionMap.put("[(H)]", R.mipmap.ee_6);
		emotionMap.put("[:@]", R.mipmap.ee_7);
		emotionMap.put("[:s]", R.mipmap.ee_8);
		emotionMap.put("[:$]", R.mipmap.ee_9);
		emotionMap.put("[:(]", R.mipmap.ee_10);
		emotionMap.put("[:'(]", R.mipmap.ee_11);
		emotionMap.put("[:|]", R.mipmap.ee_12);
		emotionMap.put("[(a)]", R.mipmap.ee_13);
		emotionMap.put("[8o|]", R.mipmap.ee_14);
		emotionMap.put("[8-|]", R.mipmap.ee_15);
		emotionMap.put("[+o(]", R.mipmap.ee_16);
		emotionMap.put("[<o)]", R.mipmap.ee_17);
		emotionMap.put("[|-)]", R.mipmap.ee_18);
		emotionMap.put("[*-)]", R.mipmap.ee_19);
		emotionMap.put("[:-#]", R.mipmap.ee_20);
		emotionMap.put("[:-*]", R.mipmap.ee_21);
		emotionMap.put("[^o)]", R.mipmap.ee_22);
		emotionMap.put("[8-)]", R.mipmap.ee_23);
		emotionMap.put("[(|)]", R.mipmap.ee_24);
		emotionMap.put("[(u)]", R.mipmap.ee_25);
		emotionMap.put("[(S)]", R.mipmap.ee_26);
		emotionMap.put("[(*)]", R.mipmap.ee_27);
		emotionMap.put("[(#)]", R.mipmap.ee_28);
		emotionMap.put("[(R)]", R.mipmap.ee_29);
		emotionMap.put("[({)]", R.mipmap.ee_30);
		emotionMap.put("[(})]", R.mipmap.ee_31);
		emotionMap.put("[(k)]", R.mipmap.ee_32);
		emotionMap.put("[(F)]", R.mipmap.ee_33);
		emotionMap.put("[(W)]", R.mipmap.ee_34);
		emotionMap.put("[(D)]", R.mipmap.ee_35);

	}
	
	/**
	 * 根据消息内容解析聊天表情
	 * @param content
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi") public CharSequence convertStringToSpannable(
			Context context	, String content ,EmotionType emotionType) {
		String hackTxt;
		if (content.startsWith("[") && content.endsWith("]")) {
			hackTxt = content + " ";
		} else {
			hackTxt = content;
		}
		SpannableString value = SpannableString.valueOf(hackTxt);
		Matcher localMatcher = EMOTION_URL.matcher(value);
		while (localMatcher.find()) {
			String str2 = localMatcher.group(0);
			int k = localMatcher.start();
			int m = localMatcher.end();
			if (m - k < 8) {
				if (emotionMap
						.containsKey(str2)) {
					int faceRes = emotionMap.get(str2);
					ImageSpan localImageSpan = new ImageSpan(context,
							createEmotionBitmap(context, emotionType, faceRes), ImageSpan.ALIGN_BASELINE);
					value.setSpan(localImageSpan, k, m,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return value;
	}
	
	/**
	 * 对搜索关键字进行高亮显示
	 * 
	 */
	public SpannableStringBuilder getTextStringBuilder(String content,
			String keyword) {
		SpannableStringBuilder builder = new SpannableStringBuilder(content);
		ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
		if (null != keyword && !"".equals(keyword) && keyword.length() > 0) {
			char startKey = keyword.charAt(0);
			int keyLength = keyword.length();
			if (keyLength > 1) {
				char overKey = keyword.charAt(keyLength - 1);
				for (int i = 0; i < content.length(); i++) {
					char startChat = content.charAt(i);
					if (content.length() >= i + keyLength) {
						char overChat = content.charAt(i + keyLength - 1);
						if (startChat == startKey && overChat == overKey) {
							builder.setSpan(redSpan, i, i + keyLength,
									Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				}
			} else {
				for (int i = 0; i < content.length(); i++) {
					char startChat = content.charAt(i);
					if (startChat == startKey) {
						builder.setSpan(redSpan, i, i + 1,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		}
		return builder;
	}
	
	public Bitmap createEmotionBitmap(Context context,EmotionType emotionType,int faceRes){
		Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), faceRes);
		Bitmap newBitmap = null;
		int newHeight = 0;
		int newWidth = 0;
		if (bitmap != null) {
			int rawHeigh = bitmap.getHeight();
			int rawWidth = bitmap.getHeight();

			if(emotionType.equals(EmotionType.FACE_KEYBOARD)){
				newHeight = DisplayUtil.dip2px(context, 30);
				newWidth = DisplayUtil.dip2px(context, 30);
			}else if(emotionType.equals(EmotionType.CHAT_LIST)){
				newHeight = DisplayUtil.dip2px(context, 20);
				newWidth = DisplayUtil.dip2px(context, 20);
			}else if(emotionType.equals(EmotionType.MESSAGE_LIST)){
				newHeight = DisplayUtil.dip2px(context, 30);
				newWidth = DisplayUtil.dip2px(context, 30);
			}else{
				newHeight = DisplayUtil.dip2px(context, 15);
				newWidth = DisplayUtil.dip2px(context, 15);
			}
			float heightScale = ((float) newHeight) / rawHeigh;
			float widthScale = ((float) newWidth) / rawWidth;
			Matrix matrix = new Matrix();
			matrix.postScale(heightScale, widthScale);
			newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					rawWidth, rawHeigh, matrix, true);
		}
		return newBitmap; 
	}

	public Map<String, Integer> getEmotionMap() {
		if (!emotionMap.isEmpty())
			return emotionMap;
		return null;
	}
	
}

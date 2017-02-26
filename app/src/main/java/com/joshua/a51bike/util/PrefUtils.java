package com.joshua.a51bike.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * ============================================================
 * <p/>
 * 版 权 ： 吴奇俊  (c) 2016
 * <p/>
 * 作 者 : 吴奇俊
 * <p/>
 * 版 本 ： 1.0
 * <p/>
 * 创建日期 ： 2016/8/2 17:29
 * <p/>
 * 描 述 ：SharedPreference工具类
 *        config配置文件信息
 * <p/>
 * ============================================================
 **/
public class PrefUtils {

	private static final String PREF_NAME = "config";
	private static SharedPreferences sp;

	public static boolean getBoolean(Context ctx, String key,
			boolean defaultValue) {
		if(sp==null){
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defaultValue);
	}

	public static void setBoolean(Context ctx, String key, boolean value) {
		if(sp==null){
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).apply();
	}

	public static String getString(Context ctx, String key, String defaultValue) {
		if(sp==null){
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return sp.getString(key, defaultValue);
	}

	public static void setString(Context ctx, String key, String value) {
		if(sp==null){
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).apply();
	}

	public static void clear(Context ctx) {
		if(sp==null){
			sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		sp.edit().clear().apply();
	}

}

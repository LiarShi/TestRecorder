package com.liar.testrecorder.utils.sp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.liar.testrecorder.App;

import java.util.Map;
import java.util.Set;

/**
 * SP帮助类

 */
public class SharedPreferencesUtils {

    private SharedPreferencesUtils() {}

    /**
     * 保存String类型数据
     * @param key 键
     * @param value 值
     */
    public static void putString(String key, @Nullable String value) {
        SharedPreferences.Editor editor = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 保存Boolean类型数据
     * @param key 键
     * @param value 值
     */
    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 保存Float类型数据
     * @param key 键
     * @param value 值
     */
    public static void putFloat(String key, float value) {
        SharedPreferences.Editor editor = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * 保存Int类型数据
     * @param key 键
     * @param value 值
     */
    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 保存Long类型数据
     * @param key 键
     * @param value 值
     */
    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 保存StringSet类型数据
     * @param key 键
     * @param values 值
     */
    public static void putStringSet(String key, @Nullable Set<String> values) {
        SharedPreferences.Editor editor = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putStringSet(key, values);
        editor.apply();
    }

    /** 获取全部的sp数据，没有返回null */
    public static <T> Map<String, T>  getAll() {
        SharedPreferences sp = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE);
        try {
            //noinspection unchecked
            return (Map<String, T>) sp.getAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Boolean型数据
     * @param key 键
     * @param defValue 默认值
     */
    public static boolean getBoolean(String key, boolean defValue) {
        SharedPreferences sp = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE);
        try {
            return sp.getBoolean(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * 获取Float型数据
     * @param key 键
     * @param defValue 默认值
     */
    public static float getFloat(String key, float defValue) {
        SharedPreferences sp = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE);
        try {
            return sp.getFloat(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * 获取Int型数据
     * @param key 键
     * @param defValue 默认值
     */
    public static int getInt(String key, int defValue) {
        SharedPreferences sp = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE);
        try {
            return sp.getInt(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * 获取Long型数据
     * @param key 键
     * @param defValue 默认值
     */
    public static long getLong(String key, long defValue) {
        SharedPreferences sp = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE);
        try {
            return sp.getLong(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * 获取String型数据
     * @param key 键
     * @param defValue 默认值
     */
    public static String getString(String key, @Nullable String defValue) {
        SharedPreferences sp = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE);
        try {
            return sp.getString(key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

    /**
     * 获取StringSet型数据
     * @param key 键
     * @param defValues 默认值
     */
    public static Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        SharedPreferences sp = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE);
        try {
            return sp.getStringSet(key, defValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValues;
    }

    /**
     * 删除指定键的数据
     * @param key 键
     */
    public static void remove(String key) {
        SharedPreferences.Editor editor = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.apply();
    }

    /** 清空整个sp数据 */
    public static void clear() {
        SharedPreferences.Editor editor = App.get().getSharedPreferences(SpConfig.SP_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

}

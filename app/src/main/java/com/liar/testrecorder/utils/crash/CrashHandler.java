package com.liar.testrecorder.utils.crash;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.liar.testrecorder.utils.file.FileManager;
import com.lodz.android.component.base.application.BaseApplication;
import com.lodz.android.core.log.PrintLog;
import com.lodz.android.core.utils.AppUtils;
import com.lodz.android.core.utils.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 崩溃处理类
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashTag";

    private static CrashHandler mInstance = new CrashHandler();

    public static CrashHandler get() {
        return mInstance;
    }

    private CrashHandler() {}

    /** 系统默认的UncaughtException处理类 */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /** 是否拦截 */
    private boolean isInterceptor = true;
    /** 提示语 */
    private String mToastTips = "";
    /** 保存的文件夹路径 */
    private String mSaveFolderPath = "";
    /** 日志文件名及后缀 */
    private String mLogFileName = "";
    /** 启动页的Class */
    private Class<?> mClass = null;

    /** 初始化代码 */
     public void init(){
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 设置是否对异常进行拦截
     * @param interceptor 是否拦截
     */
    public CrashHandler setInterceptor(boolean interceptor) {
        isInterceptor = interceptor;
        return this;
    }

    /**
     * 设置异常提示语（不设置使用默认提示语）
     * @param tips 提示语
     */
    public CrashHandler setToastTips(@NonNull String tips) {
        this.mToastTips = tips;
        return this;
    }

    /**
     * 设置保存的文件夹路径
     * @param saveFolderPath 文件夹路径
     */
    public CrashHandler setSaveFolderPath(String saveFolderPath) {
        this.mSaveFolderPath = saveFolderPath;
        return this;
    }

    /**
     * 设置日志文件名及后缀（不设置使用默认文件名）
     * @param logFileName 日志文件名
     */
    public CrashHandler setLogFileName(@NonNull String logFileName) {
        this.mLogFileName = logFileName;
        return this;
    }

    /** 设置启动页的class，app崩溃后会重启该类 */
    public CrashHandler setLauncherClass(@NonNull Class<?> c) {
        this.mClass = c;
        return this;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable t) {
        if (isInterceptor){// 用户处理
            PrintLog.d(TAG, "user handle");
            handleException(t);// 具体处理类
            try {
                Thread.sleep(2500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PrintLog.e(TAG, "error: ", t);
            t.printStackTrace();
            exceptionExit();
            return;
        }

        if (mDefaultHandler != null){
            PrintLog.d(TAG, "system handle");
            mDefaultHandler.uncaughtException(thread, t);
            return;
        }
        PrintLog.d(TAG, "unhandle");
        exceptionExit();
    }

    /** 异常退出 */
    private void exceptionExit() {
        if (mClass != null && BaseApplication.get() != null){
            // 闪退后重新打开启动页而不是当前页
            Intent intent = new Intent(BaseApplication.get(), mClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            BaseApplication.get().getApplicationContext().startActivity(intent);
        }
        if (BaseApplication.get() != null){
            BaseApplication.get().exit();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);  // 非0表示异常退出
    }

    /**
     * 处理异常
     * @param t 异常
     */
    private void handleException(Throwable t) {
        showToast();// 显示提示
        Map<String, String> deviceInfos = getDeviceInfo(BaseApplication.get().getApplicationContext());// 获取设备信息
        String content = getLogContent(deviceInfos, t);// 获取日志内容
        boolean isSaveSuccess = saveCrashLogInFile(content);// 将日志内容保存到内存卡
        PrintLog.d(TAG, "保存崩溃日志 ： " + isSaveSuccess);
        // 自定义操作
//        customHandle(deviceInfos, content, t);
    }

    /** 显示提示语 */
    private void showToast() {
        if (TextUtils.isEmpty(mToastTips)){
            mToastTips = "很抱歉，程序出现异常即将退出";
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                if (BaseApplication.get() != null){
                    Toast.makeText(BaseApplication.get().getApplicationContext(), mToastTips, Toast.LENGTH_LONG).show();
                }
                Looper.loop();
            }
        }.start();
    }

    /**
     * 保存崩溃信息到本地文件
     * @param content 保存信息
     */
    private boolean saveCrashLogInFile(String content) {
        if (TextUtils.isEmpty(mSaveFolderPath)){
            mSaveFolderPath = FileManager.getCrashFolderPath();
        }else {
            if (!mSaveFolderPath.endsWith(File.separator)) {// 判断路径结尾符
                mSaveFolderPath += File.separator;
            }
        }
        if (TextUtils.isEmpty(mSaveFolderPath)) {//保存路径为空时不保存数据
            return false;
        }

        if (TextUtils.isEmpty(mLogFileName)){
            long timestamp = System.currentTimeMillis();
            String time = DateUtils.getFormatString(DateUtils.TYPE_7, new Date(timestamp));
            mLogFileName = "crash-" + time + "-" + timestamp + ".log";
        }

        try (FileOutputStream fos = new FileOutputStream(mSaveFolderPath + mLogFileName)){
            fos.write(content.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取设备信息
     * @param context 上下文
     */
    private Map<String, String> getDeviceInfo(Context context) {
        Map<String, String> infos = new HashMap<>();
        try {
            infos.put("versionName", AppUtils.getVersionName(context));
            infos.put("versionCode", AppUtils.getVersionCode(context) + "");
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            }
            for (Map.Entry<String, String> entry : infos.entrySet()) {
                PrintLog.i(TAG, entry.getKey() + " ：" + entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }

    /**
     * 获取日志内容
     * @param deviceInfos 设备信息
     * @param t 异常
     */
    private String getLogContent(Map<String, String> deviceInfos, Throwable t){
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : deviceInfos.entrySet()) {
            String msg = entry.getKey() + "=" + entry.getValue() + "\n";
            stringBuilder.append(msg);
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        t.printStackTrace(printWriter);
        Throwable cause = t.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        stringBuilder.append(result);
        return stringBuilder.toString();
    }

    /**
     * 自定义操作
     * @param deviceInfos 设备信息
     * @param content 日志内容
     * @param t 异常
     */
//    private void customHandle(Map<String, String> deviceInfos, String content, Throwable t) {
//
//    }
}

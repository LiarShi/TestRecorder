package com.liar.testrecorder.recorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.liar.testrecorder.R;
import com.liar.testrecorder.config.Constant;
import com.liar.testrecorder.event.RecorderEvent;
import com.liar.testrecorder.recorder.listener.RecordStateListener;
import com.liar.testrecorder.ui.MainActivity;
import com.liar.testrecorder.utils.TimeUtils;
import com.lodz.android.core.utils.NotificationUtils;
import com.lodz.android.core.utils.UiHandler;
import com.zlw.main.recorderlib.recorder.RecordConfig;
import com.zlw.main.recorderlib.recorder.listener.RecordDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordFftDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;
import com.zlw.main.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.zlw.main.recorderlib.utils.FileUtils;
import com.zlw.main.recorderlib.utils.Logger;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;



/**
 * 录音服务
 *
 * @author zhaolewei
 */
public class RecordService extends Service {
    private static final String TAG = RecordService.class.getSimpleName();


    /**
     * 录音配置
     */
    private static RecordConfig currentConfig = new RecordConfig();

    private final static String ACTION_NAME = "action_type";

    private final static int ACTION_INVALID = 0;

    private final static int ACTION_START_RECORD = 1;

    private final static int ACTION_STOP_RECORD = 2;

    private final static int ACTION_RESUME_RECORD = 3;

    private final static int ACTION_PAUSE_RECORD = 4;

    private final static String PARAM_PATH = "path";




    //声音分贝
    public int  mSoundSize = 0;
    //更新UI循环时长
    private long duration = 1000L;
    //延时
    private long durationDelay = 50L;
    //录音时长
    public long  timeCounter = 0L;

    private boolean isStart = false;
    private boolean isPause = false;

    // 通知栏  暂停录音和继续录音 广播
    private RecorderReceiver recorderReceiver = new RecorderReceiver();


    public RecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey(ACTION_NAME)) {
            switch (bundle.getInt(ACTION_NAME, ACTION_INVALID)) {
                case ACTION_START_RECORD:
                    doStartRecording(bundle.getString(PARAM_PATH));

                    break;
                case ACTION_STOP_RECORD:
                    doStopRecording();

                    break;
                case ACTION_RESUME_RECORD:
                    doResumeRecording();
                    break;
                case ACTION_PAUSE_RECORD:
                    doPauseRecording();
                    break;
                default:
                    break;
            }
            return START_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }


    public static void startRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_START_RECORD);
        intent.putExtra(PARAM_PATH, getFilePath());
        context.startService(intent);

    }

    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_STOP_RECORD);
        context.startService(intent);
    }

    public static void resumeRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_RESUME_RECORD);
        context.startService(intent);
    }

    public static void pauseRecording(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_PAUSE_RECORD);
        context.startService(intent);
    }

    /**
     * 改变录音格式
     */
    public static boolean changeFormat(RecordConfig.RecordFormat recordFormat) {
        if (getState() == RecordHelper.RecordState.IDLE) {
            currentConfig.setFormat(recordFormat);
            return true;
        }
        return false;
    }

    /**
     * 改变录音配置
     */
    public static boolean changeRecordConfig(RecordConfig recordConfig) {
        if (getState() == RecordHelper.RecordState.IDLE) {
            currentConfig = recordConfig;
            return true;
        }
        return false;
    }

    /**
     * 获取录音配置参数
     */
    public static RecordConfig getRecordConfig() {
        return currentConfig;
    }

    public static void changeRecordDir(String recordDir) {
        currentConfig.setRecordDir(recordDir);
    }

    /**
     * 获取当前的录音状态
     */
    public static RecordHelper.RecordState getState() {
        return RecordHelper.getInstance().getState();
    }

    public static void setRecordStateListener(RecordStateListener recordStateListener) {
        RecordHelper.getInstance().setRecordStateListener(recordStateListener);
    }

    public static void setRecordDataListener(RecordDataListener recordDataListener) {
        RecordHelper.getInstance().setRecordDataListener(recordDataListener);
    }

    public static void setRecordSoundSizeListener(RecordSoundSizeListener recordSoundSizeListener) {
        RecordHelper.getInstance().setRecordSoundSizeListener(recordSoundSizeListener);
    }

    public static void setRecordResultListener(RecordResultListener recordResultListener) {
        RecordHelper.getInstance().setRecordResultListener(recordResultListener);
    }

    public static void setRecordFftDataListener(RecordFftDataListener recordFftDataListener) {
        RecordHelper.getInstance().setRecordFftDataListener(recordFftDataListener);
    }

    /** 开始录音 */
    private void doStartRecording(String path) {
        Logger.v(TAG, "doStartRecording path: %s", path);
        RecordHelper.getInstance().start(path, currentConfig);
        doPlay();
    }
    /** 恢复录音 */
    private void doResumeRecording() {
        Logger.v(TAG, "doResumeRecording");
        RecordHelper.getInstance().resume();
        doResume();
    }

    /** 暂停录音 */
    private void doPauseRecording() {
        Logger.v(TAG, "doResumeRecording");
        RecordHelper.getInstance().pause();
        doPause();
    }

    /** 停止录音 */
    private void doStopRecording() {
        Logger.v(TAG, "doStopRecording");
        RecordHelper.getInstance().stop();
        stopSelf();
        doStop();
    }

    public static RecordConfig getCurrentConfig() {
        return currentConfig;
    }

    public static void setCurrentConfig(RecordConfig currentConfig) {
        RecordService.currentConfig = currentConfig;
    }

    /**
     * 根据当前的时间生成相应的文件名
     * 实例 record_20160101_13_15_12
     */
    private static String getFilePath() {

        String fileDir =
                currentConfig.getRecordDir();
        if (!FileUtils.createOrExistsDir(fileDir)) {
            Logger.w(TAG, "文件夹创建失败：%s", fileDir);
            return null;
        }
        String fileName = String.format(Locale.getDefault(), "record_%s", FileUtils.getNowString(new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.SIMPLIFIED_CHINESE)));
        return String.format(Locale.getDefault(), "%s%s%s", fileDir, fileName, currentConfig.getFormat().getExtension());
    }

    /** 初始化通知通道 */
    private void initNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannelGroup group = new NotificationChannelGroup(Constant.NOTIFI_GROUP_ID, "通知组");
            NotificationUtils.create(this).createNotificationChannelGroup(group);// 设置通知组
            List<NotificationChannel> channels = new ArrayList<>();
            channels.add(getMainChannel());
            NotificationUtils.create(this).createNotificationChannels(channels);// 设置频道
        }
    }



    /** 获取主通道 */
    private NotificationChannel getMainChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(Constant.NOTIFI_CHANNEL_MAIN_ID, "主通知",
                    NotificationManager.IMPORTANCE_LOW);//IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示

            channel.enableLights(true);// 开启指示灯，如果设备有的话。
            channel.setLightColor(Color.GREEN);// 设置指示灯颜色
            channel.setDescription("应用主通知频道");// 通道描述
//            channel.enableVibration(false);// 开启震动
//            channel.setVibrationPattern(new long[]{100, 200, 400, 300, 100});// 设置震动频率
            channel.setGroup(Constant.NOTIFI_GROUP_ID);
            channel.canBypassDnd();// 检测是否绕过免打扰模式
            channel.setBypassDnd(true);// 设置绕过免打扰模式
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.canShowBadge();// 检测是否显示角标
            channel.setShowBadge(true);// 设置是否显示角标
            return channel;
        }
        return null;
    }

    /** 自定义内容样式 */
    private void showCustomNotify(String time) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constant.NOTIFI_CHANNEL_MAIN_ID);// 获取构造器
        builder.setTicker("录音Demo");// 通知栏显示的文字
        builder.setAutoCancel(false);// 设置为true，点击该条通知会自动删除，false时只能通过滑动来删除（一般都是true）
        builder.setSmallIcon(R.drawable.ic_launcher);//通知上面的小图标（必传）
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);//通知默认的声音 震动 呼吸灯
        builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);//统一消除声音和震动
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);//设置优先级，级别高的排在前面

        //常规写法
//        Intent intent = new Intent(getContext(), MainActivity.class);//意图跳转界面
//        PendingIntent pIntent = PendingIntent.getActivity(this, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);//创建一个意图
//        builder.setContentIntent(pIntent);// 将意图设置到通知上
        // 简洁写法
        builder.setContentIntent(MainActivity.startPendingIntent(this, ""));// 将意图设置到通知上

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.view_remote_notification);
        remoteViews.setImageViewResource(R.id.remoteview_icon, R.drawable.ic_launcher);
        remoteViews.setTextViewText(R.id.remoteview_title, String.format(Locale.getDefault(), "声音大小：%s db", mSoundSize));//设置对应id的标题
        if (isStart) {
            remoteViews.setTextViewText(R.id.remoteview_msg, "录音中·"+time);//设置对应id的内容
            remoteViews.setImageViewResource(R.id.imgStart, R.drawable.icon_pause);//正在录音
        }else {
            remoteViews.setTextViewText(R.id.remoteview_msg, "暂停中·"+time);//设置对应id的内容
            remoteViews.setImageViewResource(R.id.imgStart, R.drawable.icon_start_record);//录音暂停
        }

        PendingIntent completePIntent = MainActivity.startPendingIntent(this, Constant.NOTIFI_FINISH_MSG);//设置完成录音 PendingIntent
        remoteViews.setOnClickPendingIntent(R.id.btnFinish,completePIntent);//设置完成录音按钮 点击事件

        Intent pauseIntent = new Intent(Constant.NOTIFICATION_START_ID); //----设置通知栏 暂停或继续录音按钮 广播
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.imgStart, pausePIntent);//设置暂停或继续录音 按钮ID 点击事件

        builder.setContent(remoteViews);
        Notification notification = builder.build();//构建通知
        // 设置常驻 Flag
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationUtils.create(this).send(Constant.NOTIFI_RECORDER_ID,notification);
       // 将Service转成前台服务，规避Android9.0系统禁止闲置APP在后台或息屏时使用麦克风的问题
        startForeground(Constant.NOTIFI_RECORDER_ID,notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /** 初始化通知通道 */
        initNotificationChannel();
        //注册广播
        initRecorderReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timeCounter = 0;
        //服务销毁的时候 清除通知栏
        NotificationUtils.create(this).getManager().cancel(Constant.NOTIFI_RECORDER_ID);
        //销毁广播
        unregisterReceiver(recorderReceiver);
    }



    /**开始录音 **/
    private void doPlay() {
        isStart = true;
        isPause = false;
        startTimer();
    }

    /**暂停录音 **/
    private void doPause() {
        isStart = false;
        isPause = true;
        stopTimer();
        showCustomNotify(TimeUtils.getGapTime(timeCounter));
    }

    /**恢复录音 **/
    private void doResume() {
        isStart = true;
        isPause = false;
        startTimer();
    }

    /**停止  录音 **/
    private void doStop() {
        isPause = false;
        isStart = false;
        stopTimer();
        timeCounter = 0;
        //清除通知栏
        NotificationUtils.create(this).getManager().cancel(Constant.NOTIFI_RECORDER_ID);
    }


    private Timer timer;
    /**
     * 开始计时
     */
    private void startTimer(){
        stopTimer();
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, durationDelay, duration);
    }

    /**
     * 停止计时
     */
    private void stopTimer() {

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer=null;
        }
    }

    /**
     * 更新计时
     */
    private void updateTimer(){

        setRecordSoundSizeListener(new RecordSoundSizeListener() {
            @Override
            public void onSoundSize(int soundSize) {
                mSoundSize=soundSize;
            }
        });

        UiHandler.post(new Runnable() {
            @Override
            public void run() {
                timeCounter += duration;
                showCustomNotify(TimeUtils.getGapTime(timeCounter));
            }
        });
        //发送evenbus更新录音主页UI
        EventBus.getDefault().post(new RecorderEvent("",mSoundSize,timeCounter));
    }

    //注册广播
    private void initRecorderReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.NOTIFICATION_START_ID);
        registerReceiver(recorderReceiver, filter);
    }


    //接收通知栏 暂停录音和继续录音广播
    public class RecorderReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("RecorderReceiver","接收到通知栏 暂停录音和继续录音广播");
            //判断当前 录音状态
            if (isStart) {
                //录音状态就暂停
                doPause();
            }else {
                //暂停状态就恢复录音
                doResume();
            }
            EventBus.getDefault().post(new RecorderEvent("21321",mSoundSize,timeCounter));
            Log.e("RecorderReceiver","发送eventbus");

        }
    }

}

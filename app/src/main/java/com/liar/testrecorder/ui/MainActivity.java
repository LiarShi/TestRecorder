package com.liar.testrecorder.ui;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.liar.testrecorder.App;
import com.liar.testrecorder.BuildConfig;
import com.liar.testrecorder.R;
import com.liar.testrecorder.config.Constant;
import com.liar.testrecorder.event.RecorderEvent;
import com.liar.testrecorder.recorder.RecordHelper;
import com.liar.testrecorder.recorder.RecordManager;
import com.liar.testrecorder.recorder.listener.RecordStateListener;
import com.liar.testrecorder.ui.dialog.InputFileNameDialog;
import com.liar.testrecorder.utils.TimeUtils;
import com.liar.testrecorder.utils.file.FileManager;
import com.liar.testrecorder.widget.AudioView;
import com.lodz.android.component.base.activity.BaseActivity;
import com.lodz.android.component.widget.base.TitleBarLayout;
import com.lodz.android.core.utils.FileUtils;
import com.lodz.android.core.utils.NotificationUtils;
import com.lodz.android.core.utils.ToastUtils;
import com.zlw.main.recorderlib.recorder.RecordConfig;
import com.zlw.main.recorderlib.recorder.listener.RecordFftDataListener;
import com.zlw.main.recorderlib.recorder.listener.RecordResultListener;
import com.zlw.main.recorderlib.recorder.listener.RecordSoundSizeListener;
import com.zlw.main.recorderlib.utils.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();





    /**录音 **/
    @BindView(R.id.btRecord)
    Button btRecord;
    /**停止 **/
    @BindView(R.id.btStop)
    Button btStop;

    /**完成 **/
    @BindView(R.id.btFinish)
    Button btFinish;

    /**状态 **/
    @BindView(R.id.tvState)
    TextView tvState;
    /**声音大小 **/
    @BindView(R.id.tvSoundSize)
    TextView tvSoundSize;
    /**RadioGroup 音频格式 **/
    @BindView(R.id.rgAudioFormat)
    RadioGroup rgAudioFormat;
    /**RadioGroup 音频采样率**/
    @BindView(R.id.rgSimpleRate)
    RadioGroup rgSimpleRate;
    /**RadioGroup 音频位宽**/
    @BindView(R.id.tbEncoding)
    RadioGroup tbEncoding;
    /**AudioView **/
    @BindView(R.id.audioView)
    AudioView audioView;
    /**可视化样式(上)**/
    @BindView(R.id.spUpStyle)
    Spinner spUpStyle;
    /**可视化样式(下) **/
    @BindView(R.id.spDownStyle)
    Spinner spDownStyle;


    /**录音时间更新 **/
    @BindView(R.id.tvRecordTime)
    TextView tvRecordTime;

    //更新UI循环时长
    private long duration = 1000L;
    //延时
    private long durationDelay = 50L;
    //录音时长
    private long  timeCounter = 0L;

    //声音分贝
    private int  mSoundSize = 0;

    private boolean isStart = false;
    private boolean isPause = false;
    private boolean isSave = false;


    final RecordManager recordManager = RecordManager.getInstance();

    private static final String[] STYLE_DATA = new String[]{"STYLE_ALL", "STYLE_NOTHING", "STYLE_WAVE", "STYLE_HOLLOW_LUMP"};

    /**
     * 获取PendingIntent来启动
     * @param context 上下文
     * @param data 数据
     */
    public static PendingIntent startPendingIntent(Context context,String data) {
        Intent intent =  new Intent(context, MainActivity.class);
        intent.putExtra(Constant.EXTRA_MSG_DATA, data);
        return PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /**
     * 启动
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void findViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initTitleBar(getTitleBarLayout());
        showStatusCompleted();
    }

    /**
     * 初始化TitleBar
     */
    private void initTitleBar(TitleBarLayout titleBarLayout) {
        titleBarLayout.setTitleName("录音demo");
        titleBarLayout.needBackButton(false);
    }


    @Override
    protected void initData() {
        super.initData();
        initAudioView();
        initEvent();
        initRecord();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecordEvent();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        /**录音按钮 **/
        btRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPlay();
            }
        });
        /**停止录音**/
        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSave=false;
                doStop();
            }
        });

        /**完成录音**/
        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete();
            }
        });


        /**音频格式 选择**/
        rgAudioFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbPcm:
                        //PCM格式
                        recordManager.changeFormat(RecordConfig.RecordFormat.PCM);
                        break;
                    case R.id.rbMp3:
                        //Mp3格式
                        recordManager.changeFormat(RecordConfig.RecordFormat.MP3);
                        break;
                    case R.id.rbWav:
                        //Wav格式
                        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
                        break;
                    default:
                        break;
                }
            }
        });

        /**音频采样率  选择**/
        rgSimpleRate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb8K:
                        //8k采样率
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(8000));
                        break;
                    case R.id.rb16K:
                        //16K采样率
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(16000));
                        break;
                    case R.id.rb44K:
                        //44K采样率
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(44100));
                        break;
                    default:
                        break;
                }
            }
        });

        /**音频位宽 选择**/
        tbEncoding.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb8Bit:
                        //8Bit位宽
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_8BIT));
                        break;
                    case R.id.rb16Bit:
                        //16Bit位宽
                        recordManager.changeRecordConfig(recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_16BIT));
                        break;
                    default:
                        break;
                }
            }
        });

        /**音乐可视化 上 设置样式**/
        spUpStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                audioView.setStyle(AudioView.ShowStyle.getStyle(STYLE_DATA[position]), audioView.getDownStyle());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**音乐可视化 下 设置样式**/
        spDownStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                audioView.setStyle(audioView.getUpStyle(), AudioView.ShowStyle.getStyle(STYLE_DATA[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**初始化AudioView**/
    private void initAudioView() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, STYLE_DATA);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUpStyle.setAdapter(adapter);
        spDownStyle.setAdapter(adapter);
    }
    private void initEvent() {
        //16K采样率
        recordManager.changeRecordConfig(recordManager.getRecordConfig().setSampleRate(16000));
        //16Bit位宽
        recordManager.changeRecordConfig(recordManager.getRecordConfig().setEncodingConfig(AudioFormat.ENCODING_PCM_16BIT));
    }
    /**初始化RecordManager**/
    private void initRecord() {
        recordManager.init(App.getInstance(), BuildConfig.DEBUG);
//        recordManager.changeFormat(RecordConfig.RecordFormat.WAV);
        //Mp3格式
        recordManager.changeFormat(RecordConfig.RecordFormat.MP3);
//        recordManager.changeRecordDir(recordDir);
        //存储路径
//        String recordDir = String.format(Locale.getDefault(), "%s/Record/Test/",
//                Environment.getExternalStorageDirectory().getAbsolutePath());
        String recordDir = FileManager.getAudioFolderPath();
        recordManager.changeRecordDir(recordDir);
        initRecordEvent();
    }


    /**RecordManager回调**/
    private void initRecordEvent() {
        recordManager.setRecordStateListener(new RecordStateListener() {
            @Override
            public void onStateChange(RecordHelper.RecordState state) {
                Logger.i(TAG, "onStateChange %s", state.name());

                switch (state) {
                    case PAUSE:
                        tvState.setText("暂停中");
                        break;
                    case IDLE:
                        tvState.setText("空闲中");
                        break;
                    case RECORDING:
                        tvState.setText("录音中");
                        break;
                    case STOP:
                        tvState.setText("停止");
                        break;
                    case FINISH:
                        tvState.setText("录音结束");
                        tvSoundSize.setText("---");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(String error) {
                Logger.i(TAG, "onError %s", error);
            }
        });
        recordManager.setRecordSoundSizeListener(new RecordSoundSizeListener() {
            @Override
            public void onSoundSize(int soundSize) {

                mSoundSize=soundSize;
                tvSoundSize.setText(String.format(Locale.getDefault(), "声音大小：%s db", soundSize));
            }
        });


        recordManager.setRecordResultListener(new RecordResultListener() {
            @Override
            public void onResult(File result) {

//                 Toast.makeText(MainActivity.this, "录音文件： " + result.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                if(isSave){//保存
                    showInputFileNameDialog(result);
                }else {//不保存，直接删除文件
                    FileUtils.delFile(result.getAbsolutePath());
                }
            }
        });
        recordManager.setRecordFftDataListener(new RecordFftDataListener() {
            @Override
            public void onFftData(byte[] data) {
                audioView.setWaveData(data);
            }
        });
    }

    //命名录音文件 Dialog
    private void showInputFileNameDialog(File result){

        InputFileNameDialog dialog =new InputFileNameDialog(getContext());
        dialog.setListener(new InputFileNameDialog.Listener() {
            @Override
            public void onCancel(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onConfirm(Dialog dialog, String fileName) {
                //如果录音文件中已存在同名称的文件，展开提示
                if(FileUtils.isFileExists(
                        FileManager.getAudioFolderPath()+fileName+FileUtils.getSuffix(result.getAbsolutePath()))){

                    showCmDialog(result,fileName);
                }else {
                    //无重名直接保存
                    if(FileUtils.renameFile(result.getAbsolutePath(),
                            fileName+FileUtils.getSuffix(result.getAbsolutePath()))){
                        ToastUtils.showShort(getContext(),"保存成功,文件保存在："+FileManager.getAudioFolderPath());
                    }else {
                        ToastUtils.showShort(getContext(),"保存失败");
                    }

                }
                dialog.dismiss();
                isSave=false;
            }
        });
        dialog.show();
    }

    /**文件重名Dialog **/
    public void showCmDialog(File result,String fileName){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("提示：");
        builder.setMessage("录音文件中已存在同名称的文件，是否覆盖？");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setCancelable(true);            //点击对话框以外的区域是否让对话框消失
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                if(FileUtils.renameFile(result.getAbsolutePath(),
                        fileName+FileUtils.getSuffix(result.getAbsolutePath()))){
                    ToastUtils.showShort(getContext(),"保存成功");

                }else {
                    ToastUtils.showShort(getContext(),"保存失败");
                }
                dialog1.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {
                dialog1.dismiss();
                return;
            }
        });
    }

    /**停止  录音 **/
    private void doStop() {
        recordManager.stop();
        btRecord.setText("开始录音");
        isPause = false;
        isStart = false;
        timeCounter = 0;
        tvRecordTime.setText
                ("00:00:00");
        NotificationUtils.create(getContext()).getManager().cancel(Constant.NOTIFI_RECORDER_ID);

    }

    /**开始和暂停  录音 **/
    private void doPlay() {
        if (isStart) {
            recordManager.pause();
            btRecord.setText("开始录音");
            isPause = true;
            isStart = false;

        } else {
            if (isPause) {
                recordManager.resume();
            } else {
                recordManager.start();
            }
            btRecord.setText("暂停录音");
            isStart = true;
        }
//        showCustomNotify(TimeUtils.getGapTime(timeCounter));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeCounter = 0;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /** 处理Intent */
    private void handleIntent(Intent intent) {
        if (intent == null){
            return;
        }
        String data= intent.getStringExtra(Constant.EXTRA_MSG_DATA);
        if (TextUtils.isEmpty(data)){
            return;
        }
        //通知栏 完成录音按钮 事件
        if (data.equals(Constant.NOTIFI_FINISH_MSG)){
            complete();
            return;
        }
    }

    //完成录音操作
    private void complete(){
        isSave=true;
        doStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  onRecorderEvent(RecorderEvent event){
        //暂停录音和开始录音 eventbus
        if(event==null){
            return;
        }

        tvSoundSize.setText(String.format(Locale.getDefault(), "声音大小：%s db", event.soundSize));
        tvRecordTime.setText(TimeUtils.getGapTime(event.timeCounter));

        if(!TextUtils.isEmpty(event.type)){
            doPlay();
            return;
        }

    }


}

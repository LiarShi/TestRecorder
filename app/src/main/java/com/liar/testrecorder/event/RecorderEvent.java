package com.liar.testrecorder.event;

/**
 * Created by LiarShi on 2020/5/29.
 * 暂停录音和继续录音 eventbus
 */
public class RecorderEvent {

    //声音分贝
    public String type="";

    //声音分贝
    public int  soundSize = 0;

    //录音时长
    public long  timeCounter = 0L;

    public RecorderEvent(String type,int mSoundSize, long timeCounter) {
        this.type = type;
        this.soundSize = mSoundSize;
        this.timeCounter = timeCounter;
    }


}

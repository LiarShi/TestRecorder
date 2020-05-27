package com.liar.testrecorder.utils.download;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 下载状态回调

 */
public interface DownloadListener {

    /** 下载地址为空 */
    public static final int URL_EMPTY = 0;
    /** 保存名称为空 */
    public static final int SAVE_NAME_EMPTY = 1;
    /** 保存路径为空 */
    public static final int SAVE_PATH_EMPTY = 2;
    /** 网络错误 */
    public static final int NETWORK_ERROR = 3;
    /** 下在过程错误 */
    public static final int DOWNLOADING_ERROR = 4;

    @IntDef({URL_EMPTY, SAVE_NAME_EMPTY, SAVE_PATH_EMPTY, NETWORK_ERROR, DOWNLOADING_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorType {}

    /** 开始下载 */
    void onStart();

    /**
     * 进度回调
     * @param totalSize 总大小
     * @param progress 已下载大小
     */
    void onProgress(long totalSize, long progress);

    /**
     * 异常回调
     * @param errorType 异常类型，包括：{@link #URL_EMPTY}、{@link #NETWORK_ERROR}、{@link #DOWNLOADING_ERROR}
     * @param t 错误信息
     */
    void onError(@ErrorType int errorType, Throwable t);
    /** 下载完成 */
    void onComplete();
    /** 下载暂停 */
    void onPause();
}

package com.liar.testrecorder.utils.download;//package com.lodz.android.agiledev.utils.download;
//
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.lodz.android.agiledev.utils.file.FileManager;
//import com.lodz.android.component.rx.subscribe.observer.BaseObserver;
//import com.lodz.android.component.rx.utils.RxUtils;
//import com.lodz.android.core.log.PrintLog;
//import com.lodz.android.core.network.NetworkManager;
//import com.lodz.android.core.utils.FileUtils;
//
//import java.io.File;
//
//import io.reactivex.Observable;
//import io.reactivex.disposables.Disposable;
//import zlc.season.rxdownload2.RxDownload;
//import zlc.season.rxdownload2.entity.DownloadRecord;
//import zlc.season.rxdownload2.entity.DownloadStatus;
//
///**
// * 下载帮助类
// * Created by zhouL on 2017/3/28.
// */
//public class Downloader {
//
//    private static final String TAG = "downloader";
//
//    public static Downloader create(){
//        return new Downloader();
//    }
//
//    /**
//     * 初始化
//     * @param context 上下文
//     */
//    public void init(Context context){
//        RxDownload.getInstance(context)
//                .defaultSavePath(FileManager.getDownloadFolderPath()) // 设置默认的下载路径
//                .maxDownloadNumber(5) //设置同时下载数量
//                .maxRetryCount(2)//设置下载失败重试次数
//                .maxThread(3); //设置最大线程
//    }
//
//    /**
//     * 获取下载进度
//     * @param context 上下文
//     * @param url 地址
//     */
//    public Observable<DownloadRecord> getDownloadRecord(Context context, String url){
//        return RxDownload.getInstance(context)
//                .getDownloadRecord(url)
//                .compose(RxUtils.<DownloadRecord>io_main());
//    }
//
//    /**
//     * 删除下载文件
//     * @param context 上下文
//     * @param url 地址
//     */
//    public void deleteFile(Context context, String url) {
//        File file = getRealFile(context, url);
//        if (file != null){
//            FileUtils.delFile(file.getAbsolutePath());
//        }
//    }
//
//    /**
//     * 获取下载文件
//     * @param context 上下文
//     * @param url 下载地址
//     */
//    public File getRealFile(Context context, String url){
//        File[] files = RxDownload.getInstance(context).getRealFiles(url);
//        if (files != null && files.length > 0){
//            return files[0];
//        }
//        return null;
//    }
//
//    /**
//     * 下载后的文件是否存在
//     * @param context 上下文
//     * @param url 下载路径
//     */
//    public boolean realFileExists(Context context, String url) {
//        File[] files = RxDownload.getInstance(context).getRealFiles(url);
//        if (files == null || files.length == 0){
//            return false;
//        }
//        try {
//            File file = files[0];
//            if (file != null){
//                PrintLog.i(TAG, "exists : " + file.exists() + " ; getAbsolutePath : " + file.getAbsolutePath());
//                return file.exists();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 下载（获取Observable对象）
//     * @param context 上下文
//     * @param url 地址
//     */
//    public Observable<DownloadStatus> download(Context context, String url){
//        if (TextUtils.isEmpty(url)){
//            DownloadStatus status = new DownloadStatus(0, 0);
//            return Observable.just(status);
//        }
//        return RxDownload.getInstance(context)
//                .download(url)
//                .compose(RxUtils.<DownloadStatus>io_main());
//    }
//
//    /**
//     * 下载（获取Observable对象）
//     * @param context 上下文
//     * @param url 地址
//     * @param saveName 保存名称
//     */
//    public Observable<DownloadStatus> download(Context context, String url, String saveName){
//        if (TextUtils.isEmpty(url)){
//            DownloadStatus status = new DownloadStatus(0, 0);
//            return Observable.just(status);
//        }
//        return RxDownload.getInstance(context)
//                .download(url, saveName)
//                .compose(RxUtils.<DownloadStatus>io_main());
//    }
//
//    /**
//     * 下载（获取Observable对象）
//     * @param context 上下文
//     * @param url 地址
//     * @param saveName 保存名称
//     * @param savePath 保存路径
//     */
//    public Observable<DownloadStatus> download(Context context, String url, String saveName, String savePath){
//        if (TextUtils.isEmpty(url)){
//            DownloadStatus status = new DownloadStatus(0, 0);
//            return Observable.just(status);
//        }
//        return RxDownload.getInstance(context)
//                .download(url, saveName, savePath)
//                .compose(RxUtils.<DownloadStatus>io_main());
//    }
//
//    /**
//     * 下载（可获取订阅者对象来暂定下载）
//     * @param context 上下文
//     * @param url 地址
//     * @param listener 监听器
//     */
//    public BaseObserver<DownloadStatus> download(Context context, String url, DownloadListener listener){
//        if (TextUtils.isEmpty(url)){
//            if (listener != null){
//                listener.onError(DownloadListener.URL_EMPTY, new NullPointerException("url is null"));
//            }
//            PrintLog.e(TAG, "url is null");
//            return null;
//        }
//        BaseObserver<DownloadStatus> observer = getDownloadObserver(listener);
//        download(context, url).subscribe(observer);
//        return observer;
//    }
//
//    /**
//     * 下载（可获取订阅者对象来暂定下载）
//     * @param context 上下文
//     * @param url 地址
//     * @param saveName 保存名称
//     * @param listener 监听器
//     */
//    public BaseObserver<DownloadStatus> download(Context context, String url, String saveName, final DownloadListener listener){
//        if (TextUtils.isEmpty(url)){
//            if (listener != null){
//                listener.onError(DownloadListener.URL_EMPTY, new NullPointerException("url is null"));
//            }
//            PrintLog.e(TAG, "url is null");
//            return null;
//        }
//
//        if (TextUtils.isEmpty(saveName)){
//            if (listener != null){
//                listener.onError(DownloadListener.SAVE_NAME_EMPTY, new NullPointerException("saveName is null"));
//            }
//            PrintLog.e(TAG, "saveName is null");
//            return null;
//        }
//
//        BaseObserver<DownloadStatus> observer = getDownloadObserver(listener);
//        download(context, url, saveName).subscribe(observer);
//        return observer;
//    }
//
//    /**
//     * 下载（可获取订阅者对象来暂定下载）
//     * @param context 上下文
//     * @param url 地址
//     * @param saveName 保存名称
//     * @param savePath 保存路径
//     * @param listener 监听器
//     */
//    public BaseObserver<DownloadStatus> download(Context context, String url, String saveName, String savePath, final DownloadListener listener){
//        if (TextUtils.isEmpty(url)){
//            if (listener != null){
//                listener.onError(DownloadListener.URL_EMPTY, new NullPointerException("url is null"));
//            }
//            PrintLog.e(TAG, "url is null");
//            return null;
//        }
//
//        if (TextUtils.isEmpty(saveName)){
//            if (listener != null){
//                listener.onError(DownloadListener.SAVE_NAME_EMPTY, new NullPointerException("saveName is null"));
//            }
//            PrintLog.e(TAG, "saveName is null");
//            return null;
//        }
//
//        if (TextUtils.isEmpty(savePath)){
//            if (listener != null){
//                listener.onError(DownloadListener.SAVE_PATH_EMPTY, new NullPointerException("savePath is null"));
//            }
//            PrintLog.e(TAG, "savePath is null");
//            return null;
//        }
//        BaseObserver<DownloadStatus> observer = getDownloadObserver(listener);
//        download(context, url, saveName, savePath).subscribe(observer);
//        return observer;
//    }
//
//    /**
//     * 用监听器处理下载订阅
//     * @param listener 监听器
//     */
//    private BaseObserver<DownloadStatus> getDownloadObserver(final DownloadListener listener) {
//        return new BaseObserver<DownloadStatus>() {
//            @Override
//            public void onBaseSubscribe(Disposable d) {
//                if (d.isDisposed()) {
//                    if (listener != null) {
//                        listener.onPause();
//                    }
//                    PrintLog.d(TAG, "onBaseSubscribe onPause");
//                    return;
//                }
//                if (listener != null) {
//                    listener.onStart();
//                }
//                PrintLog.d(TAG, "onBaseSubscribe onStart");
//            }
//
//            @Override
//            public void onBaseNext(DownloadStatus status) {
//                if (listener != null) {
//                    listener.onProgress(status.getTotalSize(), status.getDownloadSize());
//                }
//                PrintLog.i(TAG, "getTotalSize : " + status.getTotalSize());
//                PrintLog.d(TAG, "getDownloadSize : " + status.getDownloadSize());
//            }
//
//            @Override
//            public void onBaseError(Throwable e) {
//                e.printStackTrace();
//                PrintLog.e(TAG, e.toString());
//                int errorType = DownloadListener.DOWNLOADING_ERROR;
//                if (!NetworkManager.get().isNetworkAvailable()){
//                    errorType = DownloadListener.NETWORK_ERROR;
//                }
//                if (listener != null) {
//                    listener.onError(errorType, e);
//                }
//            }
//
//            @Override
//            public void onBaseComplete() {
//                PrintLog.w(TAG, "onBaseComplete");
//                if (listener != null) {
//                    listener.onComplete();
//                }
//                PrintLog.w(TAG, "onComplete");
//            }
//
//            @Override
//            protected void onDispose() {
//                super.onDispose();
//                if (listener != null) {
//                    listener.onPause();
//                }
//                PrintLog.w(TAG, "onPause");
//            }
//        };
//    }
//
//}

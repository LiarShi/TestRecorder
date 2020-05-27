package com.liar.testrecorder.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.liar.testrecorder.R;
import com.lodz.android.component.widget.dialog.BaseDialog;

/**
 * 核对弹框
 * Created by zhouL on 2017/5/19.
 */

public class CheckDialog extends BaseDialog {

    /** 核对内容 */
    private TextView mContentMsg;
    /** 确认按钮 */
    private TextView mPositiveBtn;
    /** 取消按钮 */
    private TextView mNegativeBtn;

    private String mContentText;
    private String mPositiveText;
    private String mNegativeText;

    private Listener mPositiveListener;
    private Listener mNegativeListener;

    public CheckDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_check_layout;
    }

    @Override
    protected void findViews() {
        mContentMsg = findViewById(R.id.content_msg);
        mPositiveBtn = findViewById(R.id.positive_btn);
        mNegativeBtn = findViewById(R.id.negative_btn);
    }

    private void setBtn(TextView btn, String test, final Listener listener) {
        btn.setVisibility(View.VISIBLE);
        btn.setText(test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(CheckDialog.this);
                }
            }
        });
    }

    /**
     * 设置内容文本
     * @param contentMsg 内容文本
     */
    public void setContentMsg(@NonNull String contentMsg) {
        mContentText = contentMsg;
        mContentMsg.setText(mContentText);
    }

    /**
     * 设置内容文本
     * @param resId 内容文本资源id
     */
    public void setContentMsg(@StringRes int resId) {
        mContentText = getContext().getString(resId);
        mContentMsg.setText(mContentText);
    }

    /**
     * 设置确认按钮文本
     * @param positiveText 确认文本
     * @param listener 点击监听
     */
    public void setPositiveText(String positiveText, final Listener listener) {
        mPositiveText = positiveText;
        mPositiveListener = listener;
        setBtn(mPositiveBtn, mPositiveText, mPositiveListener);
    }

    /**
     * 设置确认按钮文本
     * @param resId 确认文本资源id
     * @param listener 点击监听
     */
    public void setPositiveText(@StringRes int resId, final Listener listener) {
        mPositiveText = getContext().getString(resId);
        mPositiveListener = listener;
        setBtn(mPositiveBtn, mPositiveText, mPositiveListener);
    }

    /**
     * 设置取消按钮文本
     * @param negativeText 取消文本
     * @param listener 点击监听
     */
    public void setNegativeText(String negativeText, final Listener listener) {
        mNegativeText = negativeText;
        mNegativeListener = listener;
        setBtn(mNegativeBtn, mNegativeText, mNegativeListener);
    }

    /**
     * 设置取消按钮文本
     * @param resId 取消文本资源id
     * @param listener 点击监听
     */
    public void setNegativeText(@StringRes int resId, final Listener listener) {
        mNegativeText = getContext().getString(resId);
        mNegativeListener = listener;
        setBtn(mNegativeBtn, mNegativeText, mNegativeListener);
    }

    public interface Listener{
        void onClick(Dialog dialog);
    }
}

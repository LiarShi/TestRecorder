package com.liar.testrecorder.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.liar.testrecorder.R;
import com.lodz.android.component.widget.dialog.BaseCenterDialog;
import com.lodz.android.core.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shizhw on 2019/7/29.

 */
public class InputFileNameDialog extends BaseCenterDialog {


    /**
     * 取消
     */
    @BindView(R.id.tv_cancel_dialog)
    TextView tv_cancel_dialog;
    /**
     * 确定
     */
    @BindView(R.id.tv_confirm_dialog)
    TextView tv_confirm_dialog;

    /**
     * IP地址输入框
     */
    @BindView(R.id.edit_file_name)
    EditText edit_file_name;




    public InputFileNameDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_input_filename;
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void findViews() {
        ButterKnife.bind(this);

        //做Emoji表情过滤
//        edit_url_ip.setFilters(new InputFilter[]{new EmojiFilterUtils()});
//        edit_url_port.setFilters(new InputFilter[]{new EmojiFilterUtils()});
        tv_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onCancel(InputFileNameDialog.this);
                }
            }
        });

        tv_confirm_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edit_file_name.getText().toString())) {
                    ToastUtils.showShort(getContext(), "文件名不能为空");
                    return;

                } else {
                    if (listener != null) {
                        listener.onConfirm(InputFileNameDialog.this,
                                edit_file_name.getText().toString().trim());
                    }
                    return;
                }
            }
        });
    }


    public Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onCancel(Dialog dialog);
        void onConfirm(Dialog dialog, String fileName);
    }


}

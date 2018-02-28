package com.moinapp.wuliao.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.ui.dialog.CommonDialog;

/**
 * 删除dialog
 */
public class DeleteDialog extends CommonDialog implements
        View.OnClickListener {

    private Context context;
    private DeleteCallBack mDeleteCallback;

    private DeleteDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
        this.context = context;
    }

    @SuppressLint("InflateParams")
    private DeleteDialog(Context context, int defStyle) {
        super(context, defStyle);
        this.context = context;
        View shareView = getLayoutInflater().inflate(
                R.layout.dialog_delete, null);
        shareView.findViewById(R.id.ly_delete).setOnClickListener(this);
        shareView.findViewById(R.id.ly_cancel).setOnClickListener(this);

        setContent(shareView, 0);
    }

    public DeleteDialog(Context context) {
        this(context, R.style.dialog_share_bottom);
    }

    public void setDeleteCallback(DeleteCallBack callback) {
        mDeleteCallback = callback;
    }
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_delete:
                mDeleteCallback.onDeleteClick();
                break;
            case R.id.ly_cancel:
                dismiss();
                break;
            default:
                break;
        }
        this.dismiss();
    }

    public interface DeleteCallBack {
        public void onDeleteClick();
    }
}

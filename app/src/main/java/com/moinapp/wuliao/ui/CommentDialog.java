package com.moinapp.wuliao.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.ui.dialog.CommonDialog;

/**
 * 评论点击后的弹出dialog
 */
public class CommentDialog extends CommonDialog implements
        View.OnClickListener {

    private Context context;
    private DeleteCallBack mDeleteCallback;
    private CopyCallBack mCopyCallback;
    private ReplyCallBack mReplyCallback;

    LinearLayout mLyDelete;
    LinearLayout mLyReply;
    LinearLayout mLyCoply;

    private CommentDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
        this.context = context;
    }

    @SuppressLint("InflateParams")
    private CommentDialog(Context context, int defStyle) {
        super(context, defStyle);
        this.context = context;
        View shareView = getLayoutInflater().inflate(
                R.layout.dialog_comment, null);
        mLyDelete = (LinearLayout)shareView.findViewById(R.id.ly_delete);
        mLyDelete.setOnClickListener(this);
        mLyReply = (LinearLayout)shareView.findViewById(R.id.ly_reply);
        mLyReply.setOnClickListener(this);
        mLyCoply = (LinearLayout)shareView.findViewById(R.id.ly_copy);
        mLyCoply.setOnClickListener(this);

        shareView.findViewById(R.id.ly_cancel).setOnClickListener(this);
        setContent(shareView, 0);
    }

    public CommentDialog(Context context) {
        this(context, R.style.dialog_share_bottom);
    }

    public void setDeleteCallback(DeleteCallBack callback) {
        mDeleteCallback = callback;
    }

    public void setCopyCallback(CopyCallBack callback) {
        mCopyCallback = callback;
    }

    public void setReplyCallback(ReplyCallBack callback) {
        mReplyCallback = callback;
    }

    public void hideDeleteButton() {
        mLyDelete.setVisibility(View.GONE);
    }

    public void hideReplyButton() {
        mLyReply.setVisibility(View.GONE);
    }

    public void hideCopyButton() {
        mLyCoply.setVisibility(View.GONE);
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
            case R.id.ly_reply:
                mReplyCallback.onReplyClick();
                break;
            case R.id.ly_copy:
                mCopyCallback.onCopyClick();
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

    public interface CopyCallBack {
        public void onCopyClick();
    }

    public interface ReplyCallBack {
        public void onReplyClick();
    }
}

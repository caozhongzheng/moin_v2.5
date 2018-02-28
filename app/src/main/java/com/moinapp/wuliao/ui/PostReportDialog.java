package com.moinapp.wuliao.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.DeleteOnClickListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.ui.dialog.CommonDialog;
import com.moinapp.wuliao.util.StringUtil;

/**
 * 点击帖子详情右上角弹出的dialog
 */
public class PostReportDialog extends CommonDialog implements
        View.OnClickListener {
    private static ILogger MyLog = LoggerFactory.getLogger(PostReportDialog.class.getSimpleName());

    private Activity context;
    private LinearLayout mDelete;
    private LinearLayout mReport;
    private String mUcid;
    private CosplayInfo mCosplayInfo;
    DeleteOnClickListener deleteOnClickListener;

    private PostReportDialog(Activity context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
        this.context = context;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.6f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @SuppressLint("InflateParams")
    private PostReportDialog(Activity context, int defStyle) {
        super(context, defStyle);
        this.context = context;
        View inviteView = getLayoutInflater().inflate(
                R.layout.dialog_post_report, null);

        mDelete = (LinearLayout) inviteView.findViewById(R.id.ly_delete);
        mDelete.setOnClickListener(this);
        mReport = (LinearLayout) inviteView.findViewById(R.id.ly_report);
        mReport.setOnClickListener(this);

        inviteView.findViewById(R.id.ly_cancel).setOnClickListener(this);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.4f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setContent(inviteView, 0);
    }

    public PostReportDialog(Activity context) {
        this(context, R.style.dialog_share_bottom);
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

    public void setCosplayUcid(String ucid) {
        mUcid = ucid;
    }

    public void setCosplayInfo(CosplayInfo cosplayInfo) {
        mCosplayInfo = cosplayInfo;
    }

    // 设置删除选项是否为可见
    public void setDeleteEnable(boolean enable) {
        if (enable) {
            mDelete.setVisibility(View.VISIBLE);
            mReport.setVisibility(View.GONE);
        } else {
            mDelete.setVisibility(View.GONE);
            mReport.setVisibility(View.VISIBLE);
        }
    }

    public void setDeleteOnClick(DeleteOnClickListener listener) {
        deleteOnClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_delete:
                processDeleteClick();
                break;
            case R.id.ly_report:
                StringBuilder content = new StringBuilder();
                if (mCosplayInfo != null) {
                    content.append(mCosplayInfo.getAuthorName());
                    String postContent = StringUtil.nullToEmpty(mCosplayInfo.getContent());
                    if (postContent.length() > 10) {
                        postContent = postContent.substring(0, 10);
                    }
                    content.append("_").append(postContent);
                }
                MineManager.getInstance().report(null, mUcid, content.toString(), new IListener2() {
                    @Override
                    public void onSuccess(Object obj) {
                        AppContext.showToastShort(R.string.report_text);
                    }

                    @Override
                    public void onErr(Object obj) {

                    }

                    @Override
                    public void onNoNetwork() {

                    }
                });
                break;
            default:
                break;
        }
        this.dismiss();
    }

    /**
     * 删除帖子
     */
    private void processDeleteClick() {
        DialogHelper dialogHelper = new DialogHelper((Activity) context);
        dialogHelper.alert4M(null, "您确定要删除这个帖子吗？",
                "删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (deleteOnClickListener != null) {
                            deleteOnClickListener.onClick(null);
                        }
                        dialogHelper.dialogDismiss();
                    }
                }, "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogHelper.dialogDismiss();
                    }
                }, false);
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }
}

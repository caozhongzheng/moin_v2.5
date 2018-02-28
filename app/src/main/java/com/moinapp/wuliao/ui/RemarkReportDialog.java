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
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.IClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.ui.dialog.CommonDialog;
import com.moinapp.wuliao.util.StringUtil;

/**
 * 邀请好友dialog
 */
public class RemarkReportDialog extends CommonDialog implements
        View.OnClickListener {
    private static ILogger MyLog = LoggerFactory.getLogger("RemarkReportDialog");

    private Activity context;
    private LinearLayout mRemark;
    private UserInfo userInfo;


    private RemarkReportDialog(Activity context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
        this.context = context;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.4f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @SuppressLint("InflateParams")
    private RemarkReportDialog(Activity context, int defStyle) {
        super(context, defStyle);
        this.context = context;
        View inviteView = getLayoutInflater().inflate(
                R.layout.dialog_report_remark, null);

        mRemark = (LinearLayout) inviteView.findViewById(R.id.ly_remark);
        mRemark.setOnClickListener(this);
        inviteView.findViewById(R.id.ly_report).setOnClickListener(this);
        inviteView.findViewById(R.id.ly_cancel).setOnClickListener(this);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.4f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setContent(inviteView, 0);
    }

    public void setIcon(int relation) {
        if (relation < 2) {
            mRemark.setVisibility(View.GONE);
        } else {
            mRemark.setVisibility(View.VISIBLE);
        }
    }

    public RemarkReportDialog(Activity context) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_remark:
                changeAlias(userInfo);
                break;
            case R.id.ly_report:
                MineManager.getInstance().report(userInfo.getUId(), null, userInfo.getUsername(), new IListener2() {
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
     * 修改备注
     */
    private void changeAlias(UserInfo userInfo) {
        DialogHelper dialogHelper = new DialogHelper(context);
        dialogHelper.alert4MWithEditText(
                null,
                context.getString(R.string.alias_title),
                userInfo.getAlias(),
                context.getString(R.string.cancle), v -> dialogHelper.dialogDismiss(),
                context.getString(R.string.ok),
                new IClickListener() {
                    @Override
                    public void OnClick(Object obj) {
                        // 新的备注在这里进行回调
                        String alias = (String) obj;
                        int result = StringUtil.checkAlias(context, userInfo.getAlias(), alias);
                        switch (result) {
                            case UserDefineConstants.ALIAS_UND:
                            case UserDefineConstants.ALIAS_LNG:
                            case UserDefineConstants.ALIAS_INV:
                                break;
                            case UserDefineConstants.ALIAS_ADD:
                            case UserDefineConstants.ALIAS_CHG:
                                break;
                            case UserDefineConstants.ALIAS_DEL:
                                break;
                        }

                        MineManager.getInstance().modifyAlias(userInfo.getUId(), alias, new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                if (StringUtil.isNullOrEmpty(userInfo.getUId())) {
                                    return;
                                } else {
                                    userInfo.setAlias(alias);
                                    listener.onChangeSucess();
                                    return;
                                }
                            }

                            @Override
                            public void onErr(Object obj) {
                                AppContext.toast(context, context.getString(R.string.alias_failed));
                                listener.onChangeFailed();
                            }

                            @Override
                            public void onNoNetwork() {
                                AppContext.toast(context, context.getString(R.string.no_network));
                            }
                        });
                        dialogHelper.dialogDismiss();
                    }

                    @Override
                    public void onClick(View view) {
                        // DO nothing
                    }
                }, true);
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    private OnChangeRemarkNameListener listener;

    public interface OnChangeRemarkNameListener {
        void onChangeSucess();

        void onChangeFailed();
    }

    public void setOnChangeRemarkNameListener(OnChangeRemarkNameListener listener) {
        this.listener = listener;
    }
}

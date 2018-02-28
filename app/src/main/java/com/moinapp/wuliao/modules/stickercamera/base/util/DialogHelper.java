package com.moinapp.wuliao.modules.stickercamera.base.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.interf.IClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public class DialogHelper {
    private Activity mActivity;
    private AlertDialog mAlertDialog;
    private Toast mToast;

    public DialogHelper(Activity activity) {
        mActivity = activity;
    }

    /**
     * 关闭对话框
     */
    public void dialogDismiss() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    /**
     * 弹对话框
     *
     * @param title            标题
     * @param msg              消息
     * @param positive         确定
     * @param positiveListener 确定回调
     * @param negative         否定
     * @param negativeListener 否定回调
     */
    public void alert(final String title, final String msg, final String positive,
                      final DialogInterface.OnClickListener positiveListener,
                      final String negative, final DialogInterface.OnClickListener negativeListener) {
        alert(title, msg, positive, positiveListener, negative, negativeListener, false);
    }

    /**
     * 弹对话框
     *
     * @param title                    标题
     * @param msg                      消息
     * @param positive                 确定
     * @param positiveListener         确定回调
     * @param negative                 否定
     * @param negativeListener         否定回调
     * @param isCanceledOnTouchOutside 是否可以点击外围框
     */
    public void alert(final String title, final String msg, final String positive,
                      final DialogInterface.OnClickListener positiveListener,
                      final String negative,
                      final DialogInterface.OnClickListener negativeListener,
                      final Boolean isCanceledOnTouchOutside) {
        dismissProgressDialog();

        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mActivity == null || mActivity.isFinishing()) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                if (title != null) {
                    builder.setTitle(title);
                }
                if (msg != null) {
                    builder.setMessage(msg);
                }
                if (positive != null) {
                    builder.setPositiveButton(positive, positiveListener);
                }
                if (negative != null) {
                    builder.setNegativeButton(negative, negativeListener);
                }
                mAlertDialog = builder.show();
                mAlertDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
                mAlertDialog.setCancelable(false);

                // 设置button的字体颜色为红色
                Button mNegativeButton = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                mNegativeButton.setTextColor(0xfff66382);
                mPositiveButton.setTextColor(0xfff66382);
            }
        });
    }

    /**
     * 针对5.0以上系统的弹对话框
     *
     * @param title                    标题
     * @param msg                      消息
     * @param leftButton               左边按钮
     * @param leftButtonListener       左边按钮回调
     * @param rightButton              否定
     * @param rightButtonListener      否定回调
     * @param isCanceledOnTouchOutside 是否可以点击外围框
     */
    public void alert4M(final String title, final String msg, final String leftButton,
                        final View.OnClickListener leftButtonListener,
                        final String rightButton,
                        final View.OnClickListener rightButtonListener,
                        final Boolean isCanceledOnTouchOutside) {
        dismissProgressDialog();

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mActivity == null || mActivity.isFinishing()) {
                    return;
                }
                LayoutInflater inflater = LayoutInflater.from(mActivity);
                View view = inflater.inflate(R.layout.dialog_4_m, null);
                TextView tv_delete_msg = (TextView) view.findViewById(R.id.tv_dialog_msg);
                Button mBtnLeft = (Button) view.findViewById(R.id.btn_dialog_left);
                Button mBtnRight = (Button) view.findViewById(R.id.btn_dialog_right);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setView(view);
                if (title != null) {
                    builder.setTitle(title);
                }
                if (msg != null) {
                    tv_delete_msg.setText(msg);
                }
                if (leftButton != null) {
                    mBtnLeft.setText(leftButton);
                    mBtnLeft.setOnClickListener(leftButtonListener);
                }
                if (rightButton != null) {
                    mBtnRight.setText(rightButton);
                    mBtnRight.setOnClickListener(rightButtonListener);
                }
                mAlertDialog = builder.show();
                mAlertDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
                mAlertDialog.setCancelable(true);
            }
        });
    }

    /**
     * 针对5.0以上系统的弹对话框
     *
     * @param title                    标题
     * @param msg                      消息
     * @param defaultEdt               默认输入框文字
     * @param leftButton               左边按钮
     * @param leftButtonListener       左边按钮回调
     * @param rightButton              否定
     * @param rightButtonListener      否定回调
     * @param isCanceledOnTouchOutside 是否可以点击外围框
     */
    public void alert4MWithEditText(final String title, final String msg,
                        final String defaultEdt,
                        final String leftButton,
                        final View.OnClickListener leftButtonListener,
                        final String rightButton,
                        final View.OnClickListener rightButtonListener,
                        final Boolean isCanceledOnTouchOutside) {
        dismissProgressDialog();

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mActivity == null || mActivity.isFinishing()) {
                    return;
                }
                LayoutInflater inflater = LayoutInflater.from(mActivity);
                View view = inflater.inflate(R.layout.dialog_4_m_edtxt, null);
                TextView tv_delete_msg = (TextView) view.findViewById(R.id.tv_dialog_msg);
                EditText mEtxName = (EditText) view.findViewById(R.id.edit_dialog_name);
                if (!StringUtil.isNullOrEmpty(defaultEdt)) {
                    mEtxName.setText(defaultEdt);
                    try {
                        mEtxName.setSelection(defaultEdt.length());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Button mBtnLeft = (Button) view.findViewById(R.id.btn_dialog_left);
                Button mBtnRight = (Button) view.findViewById(R.id.btn_dialog_right);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setView(view);
                if (title != null) {
                    builder.setTitle(title);
                }
                if (msg != null) {
                    tv_delete_msg.setText(msg);
                }
                if (leftButton != null) {
                    mBtnLeft.setText(leftButton);
                    mBtnLeft.setOnClickListener(leftButtonListener);
                }
                if (rightButton != null) {
                    mBtnRight.setText(rightButton);
                    mBtnRight.setOnClickListener(view1 -> {
                        if (rightButtonListener instanceof IClickListener) {
                            ((IClickListener) rightButtonListener).OnClick(mEtxName.getText().toString().replaceAll(" ", ""));
                        }
                    });
                }
                mAlertDialog = builder.show();
                mAlertDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
                mAlertDialog.setCancelable(true);
            }
        });
    }

    /**
     * 弹出使用贴纸引导页的浮层
     */
    public void showStickerDetail(StickerInfo stickerInfo, StickerPackage mStickerPackage) {
        dismissProgressDialog();

        if (stickerInfo == null) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.dialog_sticker_detail, null);
        ImageView close = (ImageView) view.findViewById(R.id.iv_close);
        ImageView guidePic = (ImageView) view.findViewById(R.id.iv_sticker_sample);
        ImageView iconPic = (ImageView) view.findViewById(R.id.iv_icon_poicture);
        TextView stickerName = (TextView) view.findViewById(R.id.tv_name);
        TextView stickerPackageName = (TextView) view.findViewById(R.id.tv_sticker_name);
        TextView stickerAuthor = (TextView) view.findViewById(R.id.tv_sticker_author);
        TextView stickerUseNum = (TextView) view.findViewById(R.id.tv_sticker_use_num);
        Button useSticker = (Button) view.findViewById(R.id.btn_download);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(view);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissProgressDialog();
            }
        });

        useSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.getInstance().isLogin()){
                    UIHelper.showLoginActivity(mActivity, 0);
                    return;
                }
                // 使用贴纸
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(UmengConstants.ITEM_ID, stickerInfo.getStickerId() + "");
                map.put(UmengConstants.FROM, "贴纸详情");
                MobclickAgent.onEvent(mActivity, UmengConstants.STICKER_USE, map);
                // TODO 浮层内下面的[联网获取单张贴纸的详情]已经拿到最新的贴纸信息了, 但是到了这个manager内还要再去调用接口获取一次,有冗余.
                // 如果是推荐的那12张贴纸,就不用去联网获取所属的贴纸包了,直接使用吧.
                boolean isIntimeStickerPackage = StickerConstants.INTIME_STICKER_ID.equals(mStickerPackage.getStickerPackageId());
//                android.util.Log.i("cccc", "你使用的" + (isIntimeStickerPackage ? "是推荐包" : "是贴纸包 " + mStickerPackage.getStickerPackageId()));
                StickerManager.getInstance().useSingleSticker(mActivity, stickerInfo.getStickerId(),
                        isIntimeStickerPackage ? null : mStickerPackage.getStickerPackageId());
//                mActivity.finish();
                dismissProgressDialog();
            }
        });

        mAlertDialog = builder.show();
        mAlertDialog.setCanceledOnTouchOutside(true);
        mAlertDialog.setCancelable(true);

        // 先显示icon的,后面异步显示引导图/大图
        if (stickerInfo.getIcon() != null) {
            ImageLoaderUtils.displayHttpImage(stickerInfo.getIcon().getUri(), iconPic, null);
        }

        //联网获取单张贴纸的详情
        StickerManager.getInstance().getStickerInfo(stickerInfo.getStickerId(), new IListener() {
            @Override
            public void onSuccess(Object obj) {
                StickerInfo sticker = (StickerInfo)obj;
                if (sticker != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (sticker.getSamplePic() != null && sticker.getSamplePic().getUri() != null) {
                                iconPic.setVisibility(View.GONE);
                                ImageLoaderUtils.displayHttpImage(sticker.getSamplePic().getUri(), guidePic,
                                        null);
                            } else if (sticker.getPicture() != null) {
                                //无引导图时需要显示大图
                                ImageLoaderUtils.displayHttpImage(sticker.getPicture().getUri(), iconPic,
                                        null);
                            }
                            String name = sticker.getName();
                            char[] stickerNameArray = name.toCharArray();
                            int stickerNameLen = stickerNameArray.length;
                            if (stickerNameLen<16) {
                                stickerName.setText(sticker.getName());
                            }else {
                                StringBuffer sb = new StringBuffer();
                                for (int i = 0; i < 14; i++) {
                                    sb.append(stickerNameArray[i]);
                                }
                                sb.append("…");
                                sb.append(stickerNameArray[stickerNameLen-2]);
                                sb.append(stickerNameArray[stickerNameLen-1]);
                                stickerName.setText(sb.toString());
                            }
                            stickerPackageName.setText(mActivity.getString(R.string.stick_package_name) + " "
                                    + mStickerPackage.getName());
                            stickerAuthor.setText(sticker.getAuthor());
                            stickerUseNum.setText(mActivity.getString(R.string.stick_usenum) + " "
                                    + sticker.getUseNum());
                        }
                    });
                }
            }

            @Override
            public void onErr(Object obj) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    /**
     * TOAST
     *
     * @param msg    消息
     * @param period 时长
     */
    public void toast(final String msg, final int period) {
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = new Toast(mActivity);
                View view = LayoutInflater.from(mActivity).inflate(
                        R.layout.view_transient_notification, null);
                TextView tv = (TextView) view.findViewById(android.R.id.message);
                tv.setText(msg);
                mToast.setView(view);
                mToast.setDuration(period);

                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
            }
        });
    }

    /**
     * 显示对话框
     *
     * @param showProgressBar 是否显示圈圈
     * @param msg             对话框信息
     */
    public void showProgressDialog(boolean showProgressBar, String msg) {
        showProgressDialog(msg, true, null, showProgressBar);
    }

    /**
     * 显示进度对话框
     *
     * @param msg 消息
     */
    public void showProgressDialog(final String msg) {
        showProgressDialog(msg, true, null, true);
    }

    /**
     * 显示可取消的进度对话框
     *
     * @param msg 消息
     */
    public void showProgressDialog(final String msg, final boolean cancelable,
                                   final OnCancelListener cancelListener,
                                   final boolean showProgressBar) {
        dismissProgressDialog();

        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mActivity == null || mActivity.isFinishing()) {
                    return;
                }

                mAlertDialog = new GenericProgressDialog(mActivity);
                mAlertDialog.setMessage(msg);
                ((GenericProgressDialog) mAlertDialog).setProgressVisiable(showProgressBar);
                mAlertDialog.setCancelable(cancelable);
                mAlertDialog.setOnCancelListener(cancelListener);

                mAlertDialog.show();

                mAlertDialog.setCanceledOnTouchOutside(false);
            }
        });
    }

    public void dismissProgressDialog() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAlertDialog != null && mAlertDialog.isShowing() && !mActivity.isFinishing()) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
            }
        });
    }

}

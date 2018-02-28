package com.moinapp.wuliao.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.preference.CommonsPreference;

/**
 * Created by liujiancheng on 16/1/14.
 */
public class CommentDialogFragment extends DialogFragment {
    private Dialog mDialog;
    private View view;
    private EditText mEt;
    private Button mSend;
    private Activity mActivity;

    public  CommentDialogFragment(Activity activity) {
        super();
        mActivity = activity;
        this.view = mActivity.getLayoutInflater().inflate(R.layout.emoji_title, null);
        view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());

        initViews(view);

        createDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // Get the layout inflater
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.emoji_title, null);
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        builder.setView(view);

//        Dialog dialog = builder.create();

        if (mDialog == null) {
            createDialog();
        }
        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        onShowCallback();
    }

    private void createDialog() {
        mDialog = new Dialog(mActivity, R.style.comment_dialog);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width =  WindowManager.LayoutParams.MATCH_PARENT;
        params.height =  WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity =  Gravity.BOTTOM;
        mDialog.getWindow().setAttributes(params);
//必须设置一个背景，否则会有系统的Dialog样式：外部白框
//        dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void initViews(View view) {
        mEt = (EditText) view.findViewById(R.id.emoji_titile_input);
        TextWatcher watcher = new TextWatcher() {
            private int editStart;
            private int added;
            private int maxlength = mActivity.getResources().getInteger(R.integer.comment_max_len);
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                editStart = mEt.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                added = count - before;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String finalStr = editable.toString();
                //过滤掉@字符
                finalStr = finalStr.replace("@", "");
                if (!finalStr.equalsIgnoreCase(editable.toString())) {
                    mEt.setText(finalStr);
                    mEt.setSelection(mEt.length());
                }
                if(finalStr.length() > 0 ) {
                    if (added > 0) {
                        String inputChar = finalStr.substring(editStart, editStart + 1);
                        if (finalStr.length() == 1 && inputChar.equals("@")) {
                            mSend.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
                        } else {
                            mSend.setBackgroundResource(R.drawable.tag_btn_solid_bg);
                        }
                    } else {
                        mSend.setBackgroundResource(R.drawable.tag_btn_solid_bg);
                    }
                } else {
                    mSend.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
                }

                if (added > 0) {
                    if (finalStr.length() > maxlength) {
                        mEt.removeTextChangedListener(this);
                        mEt.getText().delete(editStart, editStart + added);
                        mEt.setSelection(editStart);
                        mEt.addTextChangedListener(this);
                        return;
                    }
                    String inputChar = finalStr.substring(editStart, editStart + 1);
                    if (inputChar.equals("@")) {
                        mEt.removeTextChangedListener(this);
                        mEt.getText().delete(editStart, editStart + added);
                        mEt.setSelection(editStart);
                        mEt.addTextChangedListener(this);
                    }
                }
            }
        };
        mEt.addTextChangedListener(watcher);
        mEt.setSelectAllOnFocus(true);
        mEt.setFocusable(true);
        mEt.setFocusableInTouchMode(true);
        mSend = (Button) view.findViewById(R.id.emoji_title_send);
        mSend.setOnClickListener(v -> {
            dismiss();
            onClickSend();
        });
    }


    private void onClickSend() {
        if(callback != null) {
            callback.onComment(mEt.getText().toString());
        }
    }

    private void onDissmissCallback() {
        hideSoftKeyboard();
        if(callback != null) {
            callback.onFinish();
        }
    }

    private void onShowCallback() {
        showSoftKeyboard();
        if(callback != null) {
            callback.onStart();
        }
    }

    public void clean() {
        mEt.setText(null);
        mEt.setTag(null);
    }

    public void setHint(String hint) {
        mEt.setHint(hint);
    }

    public View getEditText() {
        return mEt;
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        ((InputMethodManager) mActivity.getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mEt.getWindowToken(), 0);
    }

    /**
     * 显示软键盘
     */
    public void showSoftKeyboard() {
        mEt.requestFocus();
        ((InputMethodManager) mActivity.getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(mEt,
                InputMethodManager.SHOW_FORCED);
    }

    /**
     * 设置背景透明度，实现dialog弹出背景变暗效果
     */
    public void setBgDim_on() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 1.0f;//0.3f
        getActivity().getWindow().setAttributes(lp);
    }

    public void setBgDim_off() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 1.0f;
        mActivity.getWindow().setAttributes(lp);
    }

//    /**
//     * @param gravity	依靠父布局的位置如Gravity.CENTER
//     * @param gravity		gravity
//     * @param xoffset		x偏移量
//     * @param yoffset		y偏移量
//     * 我发现showAtLocation的parent参数可以很随意，只要是activity中的view都可以。
//     */
//    public void show(int gravity, int xoffset, int yoffset) {
//        popwindow.showAtLocation(view, gravity, xoffset, xoffset);
//        setBgDim_on();
//        onShowCallback();
//    }
//
//    /**
//     * 从底部滑出
//     */
//    public void showButtom() {
//        popwindow.setAnimationStyle(R.style.popwin_anim_style);
//        popwindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//        setBgDim_on();
//        onShowCallback();
//    }
//
//
//    /**
//     * 弹出对话框，位置在紧挨着view组件
//     * @param anchor
//     * @param xoffset	x偏移量
//     * @param yoffset	y偏移量
//     */
//    public void showAsDropDown(View anchor, int xoffset, int yoffset) {
//        popwindow.showAsDropDown(anchor, xoffset, yoffset);
//        setBgDim_on();
//    }

    @Override
    public void dismiss() {
        super.dismiss();
        setBgDim_off();
        onDissmissCallback();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        setBgDim_off();
        onDissmissCallback();
    }

    private CommentCallback callback = null;

    public interface CommentCallback {
        void onComment(String string);
        void onFinish();
        void onStart();
    }

    public void setCallback(CommentCallback callback) {
        this.callback = callback;
    }
}

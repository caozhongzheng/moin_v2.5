package com.moinapp.wuliao.modules.stickercamera.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;
import com.moinapp.wuliao.modules.stickercamera.base.BaseActivity;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.umeng.analytics.MobclickAgent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * 编辑文字
 * Created by sky on 2015/7/20.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class EditTextActivity extends BaseActivity {

    ILogger MyLog = LoggerFactory.getLogger("eta");
    private final static int MAX = 10;
    private int stickerType = StickerConstants.STICKER_TEXT_BUBBLE;
    private int maxlength = MAX;
    private int SW = (int) TDevice.getScreenWidth();
    private int rectWidth;
    private int rectHeight;
    private int focusStickerPosition;

    @InjectView(R.id.text_input)
    EditText contentView;
    @InjectView(R.id.tag_input_tips)
    TextView numberTips;

    /**
     * 所有的这些参数都可以用Bundle来进行传递,就不拘泥于各种参数了
     */
    public static void openTextEdit(Activity mContext, String defaultStr, int maxLength, int reqCode) {
        Intent i = new Intent(mContext, EditTextActivity.class);
        i.putExtra(Constants.PARAM_EDIT_TEXT, defaultStr);
        if (maxLength != 0) {
            i.putExtra(Constants.PARAM_MAX_SIZE, maxLength);
        }
        mContext.startActivityForResult(i, reqCode);
    }

    /**
     * 所有的这些参数都可以用Bundle来进行传递,就不拘泥于各种参数了
     */
    public static void openTextEdit(Activity mContext, Bundle bundle, int reqCode) {
        Intent i = new Intent(mContext, EditTextActivity.class);
        if (bundle != null) {
            i.putExtra(Constants.KEY_DEFAULT_TEXT, bundle.getString(Constants.KEY_DEFAULT_TEXT));
            i.putExtra(Constants.KEY_MAX_LENGTH, bundle.getInt(Constants.KEY_MAX_LENGTH, 0));
            i.putExtra(Constants.PARAM_STICKER_TYPE, bundle.getInt(Constants.PARAM_STICKER_TYPE, StickerConstants.STICKER_TEXT_BUBBLE));
            i.putExtra(Constants.KEY_RECT_WIDTH, bundle.getInt(Constants.KEY_RECT_WIDTH, 0));
            i.putExtra(Constants.KEY_RECT_HEIGHT, bundle.getInt(Constants.KEY_RECT_HEIGHT, 0));
            i.putExtra(Constants.KEY_FOCUS_STICKER_POSITION, bundle.getInt(Constants.KEY_FOCUS_STICKER_POSITION, 0));
        }
        mContext.startActivityForResult(i, reqCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        ButterKnife.inject(this);
        maxlength = getIntent().getIntExtra(Constants.PARAM_MAX_SIZE, MAX);
        stickerType = getIntent().getIntExtra(Constants.PARAM_STICKER_TYPE, StickerConstants.STICKER_TEXT_BUBBLE);
        rectWidth = getIntent().getIntExtra(Constants.KEY_RECT_WIDTH, SW);
        rectHeight = getIntent().getIntExtra(Constants.KEY_RECT_HEIGHT, SW);
        focusStickerPosition = getIntent().getIntExtra(Constants.KEY_FOCUS_STICKER_POSITION, 0);

        String defaultStr = getIntent().getStringExtra(Constants.PARAM_EDIT_TEXT);
        MyLog.i("bundle stickerType= " + stickerType
                        + ", maxlength=" + maxlength
                        + ", defaultStr=" + defaultStr
        );
        if (StringUtils.isNotEmpty(defaultStr)) {
            contentView.setText(defaultStr);
            contentView.setSelection(defaultStr.length());
            if (defaultStr.length() <= maxlength) {
                numberTips.setText("你还可以输入" + (maxlength - defaultStr.length()) + "个字  ("
                        + defaultStr.length() + "/" + maxlength + ")");
            }
        } else {
            numberTips.setText("你还可以输入" + maxlength + "个字  ("
                    + "0/" + maxlength + ")");
        }
        titleBar.setRightBtnOnclickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inputOver(contentView.getText().toString());
            }
        });
        contentView.addTextChangedListener(mTextWatcher);
        contentView.setOnEditorActionListener(mOnEditText);
        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                MyLog.i("onKey keyCode= " + keyCode);
                // TODO 不允许输入回车. 但是彩色文字允许回车
                if (KeyEvent.KEYCODE_ENTER == keyCode && keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        && !StickerUtils.isColorText(stickerType)) {
                    inputOver(contentView.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void inputOver(String inputTxt) {
        Intent intent = new Intent();
        intent.putExtra(Constants.PARAM_EDIT_TEXT, inputTxt);
        intent.putExtra(Constants.KEY_FOCUS_STICKER_POSITION, focusStickerPosition);
        // 只有文字气泡才需要设置缩放倍数等参数
        if (StickerUtils.isTextBubble(stickerType)) {
            /**其实是可以把文字颜色[这个是考虑气泡可以有不同颜色的背景],文字大小[这个也是为了气泡更丰富]传回去*/
            intent.putExtra(Constants.KEY_RECT_WIDTH, rectWidth);
            intent.putExtra(Constants.KEY_RECT_HEIGHT, rectHeight);
            if (!StringUtil.isNullOrEmpty(inputTxt)) {
                Bundle bundle = StickerUtils.getScaledRectWidth(inputTxt, rectWidth, rectHeight);
                float newWidth = bundle.getFloat(Constants.KEY_RECT_WIDTH, rectWidth);
                float scale = newWidth / (float) rectWidth;
                intent.putExtra(Constants.KEY_SCALE, scale);
                intent.putExtra(Constants.KEY_BUBBLE_TEXT_LIST, bundle.getParcelableArrayList(Constants.KEY_BUBBLE_TEXT_LIST));
                MyLog.i("应该缩放比例 " + scale);
                MyLog.i("应该缩放比例 " + intent.getFloatExtra(Constants.KEY_SCALE, 1f));

//            Bundle bundle = StickerUtils.scaleBubbleText(inputTxt, rectWidth, rectHeight);
//            intent.putExtra(Constants.KEY_ONE_ROW_TEXT_COUNT, bundle.getInt(Constants.KEY_ONE_ROW_TEXT_COUNT));
//            intent.putExtra(Constants.KEY_ROW_COUNT, bundle.getInt(Constants.KEY_ROW_COUNT));
//            intent.putExtra(Constants.KEY_SCALE, bundle.getDouble(Constants.KEY_SCALE));
            }
        }
        setResult(RESULT_OK, intent);
        MyLog.i("------------------------inputOver start------------------------");
        MyLog.i("KEY_FOCUS_STICKER_POSITION= " + focusStickerPosition);
        MyLog.i("KEY_RECT_WIDTH= " + rectWidth);
        MyLog.i("KEY_RECT_HEIGHT= " + rectHeight);
        MyLog.i("KEY_ONE_ROW_TEXT_COUNT= " + intent.getIntExtra(Constants.KEY_ONE_ROW_TEXT_COUNT, 0));
        MyLog.i("KEY_ROW_COUNT= " + intent.getIntExtra(Constants.KEY_ROW_COUNT, 0));
        MyLog.i("KEY_SCALE= " + intent.getFloatExtra(Constants.KEY_SCALE, 1f));
        MyLog.i("------------------------inputOver  end------------------------");
        finish();
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private int editStart;
        private int editEnd;
        private int added;
        private String beforeText;

        //匹配表情符号的正则表达式
        private final String reg_emj = "[\ue000-\uf8ff]|[\\x{1f300}-\\x{1f7ff}]";
        private Pattern pattern = Pattern.compile(reg_emj);
        //输入表情前的光标位置
        private int cursorPos;
        //输入表情前EditText中的文本
        private String tmp;
        //是否重置了EditText的内容
        private boolean resetText;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            if (!resetText) {
                cursorPos = contentView.getSelectionEnd();
                tmp = charSequence.toString();//这里用s.toString()而不直接用s是因为如果用s，那么，tmp和s在内存中指向的是同一个地址，s改变了，tmp也就改变了，那么表情过滤就失败了
            }

            editStart = contentView.getSelectionStart();
            beforeText = charSequence.toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            MyLog.i("[" + charSequence.toString() + "]");
            if (!resetText) {
                if (StickerUtils.isTextBubble(stickerType) && count >= 2) {
                    try {
                        //表情符号的字符长度最小为2
                        //提取输入的长度大于2的文本
                        CharSequence input = charSequence.subSequence(cursorPos, cursorPos + count);
                        //正则匹配是否是表情符号
                        Matcher matcher = pattern.matcher(input.toString());
                        if (matcher.matches()) {
                            resetText = true;
                            //是表情符号就将文本还原为输入表情符号之前的内容
                            contentView.setText(tmp);
                            contentView.setSelection(tmp.length());
                            contentView.invalidate();
                            //fix umeng bug 0095, 用另外一种toast替换
//                            toast("不支持表情输入", Toast.LENGTH_SHORT);
                            AppContext.toast(EditTextActivity.this, "不支持表情输入");
                        }
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                }
            } else {
                resetText = false;
            }
            added = count - before;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (added > 0 && added < 2) {
                String finalStr = s.toString();
                String inputChar = finalStr.substring(editStart, editStart + 1);
                if (StickerUtils.isTextBubble(stickerType) && inputChar.equals("@")) {
                    // 输入的是@,文字气泡不允许输入@
                    contentView.removeTextChangedListener(this);
                    contentView.getText().delete(editStart, editStart + added);
                    contentView.setSelection(editStart);
                    contentView.addTextChangedListener(this);
                }
            }
            editEnd = contentView.getSelectionEnd();
            if (s.toString().length() > maxlength) {
                //fix umeng bug 0095, 用另外一种toast替换
//                toast("不支持表情输入", Toast.LENGTH_SHORT);
                AppContext.toast(EditTextActivity.this, "你输入的字数已经超过了限制！");
                s.delete(editEnd - 1, editEnd);
                contentView.setText(s);
                contentView.setSelection(maxlength);
            }
            numberTips.setText("你还可以输入"
                    + (maxlength - s.toString().length())
                    + "个字  (" + s.toString().length() + "/"
                    + maxlength + ")");
        }
    };

    // TODO 点击完成,直接结束
    EditText.OnEditorActionListener mOnEditText = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            MyLog.i("onEditorAction actionId= " + actionId + ", txt=[" + textView.getText().toString() + "]");
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    inputOver(textView.getText().toString());
                    break;
            }
            return false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.EDIT_TEXT_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.EDIT_TEXT_ACTIVITY); //
        MobclickAgent.onPause(this);
    }
}

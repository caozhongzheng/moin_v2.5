package com.moinapp.wuliao.modules.stickercamera.app.camera.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moinapp.wuliao.AppConfig;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.ErrorCode;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.modules.sticker.model.StickerId;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraBaseActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StyledText;
import com.moinapp.wuliao.modules.stickercamera.app.ui.WriteAuthFragment;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.util.FileUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 图片发布
 * Created by moying on 15/9/24.
 *
 */
public class PhotoReleaseActivity extends CameraBaseActivity {
    private static final ILogger MyLog = LoggerFactory.getLogger(PhotoReleaseActivity.class.getSimpleName());

    /**
     * 效果图文件本地路径
     */
    public static final String KEY_XGT = "xgt";
    /**
     * 效果图URL,转发时用
     */
    public static final String KEY_XGT_URL = "xgt_url";
    /**
     * 工程文件文件本地路径
     */
    public static final String KEY_GCWJ = "gcwj";
    public static final String KEY_STICKER_IDS = "stick_ids";
    public static final String KEY_STICKER_ID_STRING = "stick_id_string";
    /**
     * 转发时来源图的UCID
     */
    public static final String KEY_PARENT_UCID = "parent_ucid";
    public static final String KEY_PARENT_WRITE_AUTH = "parent_write_auth";
    public static final String KEY_AUDIO_LIST = "audio_list";
    public static final String RECOMMEND_WORD_CACHE_KEY = "Recommend_word_list_cache";
    public static final int REQUEST_ATFOLLOWERS = 0x11;
    public static final int REQUEST_WRITE_AUTH = 0x12;
    public static final int REQUEST_RECOMMEND_WORD = 0x21;

    @InjectView(R.id.sketch)
    ImageView mIvSketch;
    @InjectView(R.id.at)
    TextView mTvAt;
    @InjectView(R.id.tv_recommend)
    TextView mTvRecommend;
    @InjectView(R.id.content)
    EditText mEtContent;
    @InjectView(R.id.content_input_tips)
    TextView numberTips;
    @InjectView(R.id.ll_wauth)
    LinearLayout mLlWauth;
    @InjectView(R.id.auth_item)
    RelativeLayout mRlWauth;
    @InjectView(R.id.auth_selected)
    TextView mTvAuth;
    @InjectView(R.id.forward_wechatfriends)
    ImageView mIvWechatFriends;
    @InjectView(R.id.forward_qqzone)
    ImageView mIvQQZone;
    @InjectView(R.id.forward_sina)
    ImageView mIvSina;

    int mWriteAuth;
    Intent mData;
    int maxlength;
    Map<String, String> atUsersMap;
    List<String> atUsers;
    boolean isForward = false;
    boolean isShare2WechatFriend = false;
    boolean isShare2QQZone = false;
    boolean isShare2Sina = false;

    private ArrayList<String> mRecommendWordList = new ArrayList<String>();
    private String mTopicID;
    private String mStickerIds;
    private ArrayList<StickerAudioInfo> mAudioList;
    private boolean hasCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_release);
        if ((mData = getIntent()) == null)
            finish();
        ButterKnife.inject(this);
        maxlength = getResources().getInteger(R.integer.photo_release_context_max_length);
        initView();
        initEvent();
    }

    private void initView() {
        ViewGroup.LayoutParams layout = mIvSketch.getLayoutParams();
        layout.width = layout.height = AppContext.getApp().getScreenWidth();

        mWriteAuth = getIntent().getIntExtra(KEY_PARENT_WRITE_AUTH, 4);
        MyLog.i("writeauth = " + mWriteAuth);
        isForward = mWriteAuth == 1;
        if (mWriteAuth == 1) {
            // 转发
            ImageLoaderUtils.displayHttpImage(mData.getStringExtra(KEY_XGT_URL), mIvSketch, null, true, null);
        } else {
            // 原创或者改图
            ImageLoaderUtils.displayLocalImage(mData.getStringExtra(KEY_XGT), mIvSketch, null);
        }
        mIvSketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //隐藏软键盘
                imm.hideSoftInputFromWindow(mEtContent.getWindowToken(), 0);
            }
        });

        mTopicID = StickPreference.getInstance().getJoinTopicID();

        mStickerIds = mData.getStringExtra(KEY_STICKER_ID_STRING);

        mAudioList = (ArrayList<StickerAudioInfo>)mData.getSerializableExtra(KEY_AUDIO_LIST);
        setDefaultRecommendText();

        UMWXHandler wxHandler = new UMWXHandler(PhotoReleaseActivity.this,
                Constants.WEICHAT_APPID);
        if (!wxHandler.isClientInstalled()) {
            mIvWechatFriends.setVisibility(View.GONE);
        }

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(PhotoReleaseActivity.this,
                Constants.QQ_APPID, Constants.QQ_APPKEY);
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(PhotoReleaseActivity.this,
                Constants.QQ_APPID, Constants.QQ_APPKEY);
        if (!qqSsoHandler.isClientInstalled() && !qZoneSsoHandler.isClientInstalled()) {
            mIvQQZone.setVisibility(View.GONE);
        }
    }

    /**
     * 设置默认推荐文字,并且更新缓存
     */
    private void setDefaultRecommendText() {

        mRecommendWordList = (ArrayList<String>) CacheManager.readObject(PhotoReleaseActivity.this, getCacheKey());
        if (mRecommendWordList != null && !mRecommendWordList.isEmpty()) {
            insertRecommendWord(mRecommendWordList.get(0));
            hasCache = true;
        } else {
            hasCache = false;
        }

        StickerManager.getInstance().getRecommendTtext(mTopicID, mStickerIds, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                mRecommendWordList = (ArrayList<String>) obj;
                if (mRecommendWordList != null && !mRecommendWordList.isEmpty()) {
                    MyLog.i("推荐文字: " + "topicId :" + mTopicID + "__" + mRecommendWordList.toString());
                    CacheManager.saveObject(getApplicationContext(), mRecommendWordList, getCacheKey());
                    if (!hasCache) {
                        insertRecommendWord(mRecommendWordList.get(0));
                    }
                }
            }

            @Override
            public void onErr(Object obj) {
                AppContext.showToastShort(getResources().getString(R.string.get_recommend_word_failed));
            }

            @Override
            public void onNoNetwork() {
                AppContext.showToastShort(getResources().getString(R.string.no_network));
            }
        });
    }

    private String getCacheKey() {
        return RECOMMEND_WORD_CACHE_KEY + "_" + StickPreference.getInstance().getJoinTopicID();
    }

    ReleaseOkEvent releaseOkEvent;

    private void initEvent() {
        mTvAt.setOnClickListener(view -> {
            atYourFriends();
        });

        mTvRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoReleaseActivity.this, RecommendWordActivity.class);
                intent.putExtra(KEY_STICKER_ID_STRING,mStickerIds);
                startActivityForResult(intent, REQUEST_RECOMMEND_WORD);
//                int enterAnimId = R.anim.anim_pop_up;
//                PhotoReleaseActivity.this.overridePendingTransition(enterAnimId, 0);
            }
        });
        titleBar.setRightBtnClickAble(true);
        titleBar.setRightBtnOnclickListener(v -> {
            if (!AppContext.getInstance().isLogin()) {
                UIHelper.showLoginActivity(this);
                AppContext.showToast(R.string.login_first);
                return;
            }

            if (releaseOkEvent == null) {
                releaseOkEvent = new ReleaseOkEvent();
            }
            releaseOkEvent.setClickTime(TimeUtils.getCurrentTimeInLong());

            MobclickAgent.onEvent(getApplicationContext(), UmengConstants.COSPLAY_SHOW);
            MyLog.i("分享到各个平台的区分:" + isShare2WechatFriend + ", " + isShare2QQZone + ", " + isShare2Sina);
            if (!Tools.isFastDoubleClick()) {
                if (!TDevice.hasInternet()) {
                    AppContext.showToastShort(R.string.tip_network_error);
                    return;
                }
                titleBar.setRightBtnClickAble(false);
                if (isForward) {
                    showProgressDialog(getString(R.string.progress_submit));
                    // 转发
                    DiscoveryManager.getInstance().forwardCosplay(getIntent().getStringExtra(KEY_PARENT_UCID),
                             mEtContent.getText().toString(), getAtUsers(), new IListener() {
                                @Override
                                public void onSuccess(Object obj) {
                                    dismissProgressDialog();
                                    MyLog.e("转发成功 UCID=" + obj + " @ " + TimeUtils.getCurrentTimeInString());
                                    AppContext.showToastShort(R.string.forward_success);

                                    saveSharePlatform((String) obj, mData.getStringExtra(KEY_XGT_URL));

                                    // TODO 跳转到显示界面
                                    EventBus.getDefault().post(releaseOkEvent);
                                    CameraManager.getInst().close();
                                }

                                @Override
                                public void onErr(Object obj) {
                                    dismissProgressDialog();
                                    AppContext.showToastShort(R.string.forward_fail);
                                    MyLog.e("转发失败：" + obj + " @ " + TimeUtils.getCurrentTimeInString());
                                    titleBar.setRightBtnClickAble(true);
                                }

                                @Override
                                public void onNoNetwork() {
                                    dismissProgressDialog();
                                    MyLog.e("转发失败 NO——NETWORK @ " + TimeUtils.getCurrentTimeInString());
                                    titleBar.setRightBtnClickAble(true);
                                }
                            });
                } else {
                    //todo
//                    uploadFile(mData.getStringExtra(KEY_XGT), mData.getStringExtra(KEY_GCWJ));
                    submit();
                }
            }
        });

        if (mWriteAuth == 4) {
            // 如果前作选择最大权限(所有人都可以改),那就可以随意选择权限,默认选中最大权限咯
            mLlWauth.setVisibility(View.VISIBLE);
            mRlWauth.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt(WriteAuthFragment.KEY_WRITE_AUTH, mWriteAuth);
                UIHelper.showWriteAuth(PhotoReleaseActivity.this, REQUEST_WRITE_AUTH, bundle, 0);
            });
        } else {
            // 如果前作不选择所有人能改(最大权限),那就只能选择不让所有人改了.
            mWriteAuth = 1;
            mLlWauth.setVisibility(View.GONE);
        }
        MyLog.i("writeauth last = " + mWriteAuth);
        mEtContent.addTextChangedListener(textWatcher);
        mIvWechatFriends.setOnClickListener(v -> {
            isShare2WechatFriend = !isShare2WechatFriend;
            if (isShare2WechatFriend) {
                mIvWechatFriends.setImageResource(R.drawable.friends_choose_green);
            } else {
                mIvWechatFriends.setImageResource(R.drawable.friends_gray);
            }
        });
        mIvQQZone.setOnClickListener(v -> {
            isShare2QQZone = !isShare2QQZone;
            if (isShare2QQZone) {
                mIvQQZone.setImageResource(R.drawable.qqzone_choose_yellow);
            } else {
                mIvQQZone.setImageResource(R.drawable.qqzone_gray);
            }
        });
        mIvSina.setOnClickListener(v -> {
            isShare2Sina = !isShare2Sina;
            if (isShare2Sina) {
                mIvSina.setImageResource(R.drawable.microblog_choose_red);
            } else {
                mIvSina.setImageResource(R.drawable.microblog_gray);
            }
        });
    }

    // 保存要同步转发到的平台
    private void saveSharePlatform(String ucid, String path) {
        if (StringUtil.isNullOrEmpty(ucid) || StringUtil.isNullOrEmpty(path)) {
            return;
        }
        if (isShare2WechatFriend || isShare2Sina || isShare2QQZone) {
            StringBuilder stringBuilder = new StringBuilder(ucid);
            stringBuilder.append(";").append(path);
            if (isShare2WechatFriend) {
                stringBuilder.append(";").append(SHARE_MEDIA.WEIXIN_CIRCLE.toString());
            }
            if (isShare2Sina) {
                stringBuilder.append(";").append(SHARE_MEDIA.SINA.toString());
            }
            if (isShare2QQZone) {
                stringBuilder.append(";").append(SHARE_MEDIA.QZONE.toString());
            }

            MinePreference.getInstance().setSharePlatform(stringBuilder.toString());
        }
    }

    private void atYourFriends() {
//        Intent intent = new Intent(this, AtFollowersActivity.class);
//        this.startActivityForResult(intent, REQUEST_ATFOLLOWERS);
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            AppContext.showToast(R.string.login_first);
            return;
        }
        UIHelper.showAtFollowers(this, REQUEST_ATFOLLOWERS, 0);
    }

    TextWatcher textWatcher = new TextWatcher() {
        private int editStart;
        private int added;
        private String beforeText;

        /**
         * @param charSequence 改变之前的内容
         * @param start 开始的位置
         * @param count 被改变的旧内容数
         * @param after 改变后的内容数量
         * */
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            beforeText = charSequence.toString();
            editStart = getSelectionStart(mEtContent);
//            MyLog.i("beforeTextChanged:[" + beforeText + "]");
//            MyLog.i("editStart:[" + editStart + "]");
//            MyLog.i("beforeTextChanged:start=" + start + ", count=" + count + ", after=" + after + "]");
        }

        /**
         * @param charSequence 改变之后的内容
         * @param start 开始的位置
         * @param before 改变前的内容数量
         * @param count 新增数
         * */
        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            // 看删除的文字是不是@或者@字符串组中的位置
            added = count - before;
//            MyLog.i("onTextChanged:[" + charSequence + "]");
//            MyLog.i("onTextChanged:start=" + start + ", before=" + before + ", count=" + count + "]");
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // 如果删除了@的内容,那么应该一起删除
            if (added < 0) {
//                MyLog.i("删除 added=[" + added + "], editStart=" + editStart + ",beforeText=["+beforeText+"]");
                StyledText result = StringUtil.getDelPosition(beforeText, editStart);
//                MyLog.i("result=[" + result + "]");
                if (result != null) {
                    int startIndex = result.getStart();
                    int endIndex = result.getEnd();
//                    MyLog.i("startIndex=[" + startIndex + "], endIndex=[" + endIndex + "]");
                    // 之所以删除textWatcher是防止insert/delete时的getText()导致textWatcher又响应第二次.
                    mEtContent.removeTextChangedListener(textWatcher);
                    mEtContent.getText().delete(startIndex, endIndex);
                    mEtContent.setSelection(startIndex);
                    mEtContent.addTextChangedListener(textWatcher);

                    if (atUsersMap != null && atUsersMap.containsValue(result.getUsername())) {
                        String uid = null;
                        Iterator iter = atUsersMap.entrySet().iterator();
                        while (iter.hasNext()) {
                            Map.Entry entry = (Map.Entry) iter.next();
                            if (entry.getValue().equals(result.getUsername())) {
                                uid = (String) entry.getKey();
                            }
                        }
                        if (!StringUtil.isNullOrEmpty(uid)) {
                            atUsersMap.remove(uid);
                            atUsers.remove(uid);
                        }
                    }
                }
            } else if (added > 1) {
                // 如果输入的内容是从外部粘贴进来的文字或者输入的是字符表情,那么替换里面的@
                String finalStr = editable.toString();
                String inputs = finalStr.substring(editStart, editStart + added);
//                MyLog.i("afterTextChanged:inputs[" + inputs + "]");
                if (inputs.contains("@")) {
                    String beforeTxt = finalStr.substring(0, editStart);
//                    MyLog.i("afterTextChanged:beforeTxt[" + beforeTxt + "]");
                    mEtContent.removeTextChangedListener(textWatcher);
                    mEtContent.getText().delete(editStart, editStart + added);
                    String newText = StringUtil.nullToEmpty(inputs.replaceAll("@", ""));
                    int endIndex = editStart;
                    if (StringUtil.isNullOrEmpty(newText)) {
                        mEtContent.setSelection(editStart);
                    } else {
                        editable.insert(editStart, newText);
                        if (editable.toString().length()>140){
                            editable.delete(139,editable.toString().length());
                        }
                        endIndex += newText.length();

                    }
                    mEtContent.setText(StringUtil.getStyledText(editable.toString()));
                    mEtContent.setSelection(endIndex);
                    numberTips.setText(mEtContent.getText().length() + "/"
                            + maxlength);
                    mEtContent.addTextChangedListener(textWatcher);
                }
            } else if (added > 0) {
                // 如果是输入
                int index = StringUtil.getInputPosition(beforeText, editStart);
//                MyLog.i("输入 added=[" + added + "], editStart=" + editStart + ",index=["+index+"]" + ",beforeText=["+beforeText+"]");
                if (index != editStart) {
                    toast("你不能在@好友内容中输入！", Toast.LENGTH_LONG);
//                    MyLog.w("你不能在@好友内容中输入！");
                    // 之所以删除textWatcher是防止insert/delete时的getText()导致textWatcher又响应第二次.
                    mEtContent.removeTextChangedListener(textWatcher);
                    mEtContent.getText().delete(editStart, editStart + added);
                    mEtContent.setSelection(index);
                    numberTips.setText(mEtContent.getText().length() + "/"
                            + maxlength);
                    mEtContent.addTextChangedListener(textWatcher);
                    return;
                } else {
                    String finalStr = editable.toString();
//                    MyLog.i("afterTextChanged:editStart[" + editStart + "]");
                    String inputChar = finalStr.substring(editStart, editStart + 1);
                    char cChar = finalStr.charAt(editStart);
//                    MyLog.i("afterTextChanged:charAt[" + cChar + "]");
//                    MyLog.i("afterTextChanged:inputChar[" + inputChar + "]");
                    if (inputChar.equals("@")) {
                        // 输入的是@
//                        MyLog.i("afterTextChanged:输入的是@");
                        mEtContent.removeTextChangedListener(textWatcher);
                        mEtContent.getText().delete(editStart, editStart + added);
                        mEtContent.setSelection(editStart);
                        mEtContent.addTextChangedListener(textWatcher);

                        atYourFriends();
                    }
                }

            }
            String finalStr = editable.toString();
            if (finalStr.length() > maxlength) {
                toast("你输入的字数已经超过了限制！", Toast.LENGTH_SHORT);
                mEtContent.getText().delete(maxlength,finalStr.length());

//                mEtContent.removeTextChangedListener(textWatcher);
//                mEtContent.getText().delete(editStart, editStart + added);
//                mEtContent.setSelection(editStart);
//                mEtContent.addTextChangedListener(textWatcher);

            }
//            numberTips.setText("你还可以输入"
//                    + (maxlength - finalStr.length())
//                    + "个字  (" + finalStr.length() + "/"
//                    + maxlength + ")");

            numberTips.setText(mEtContent.getText().length() + "/"
                    + maxlength);
        }
    };

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent result) {
        if (requestCode == REQUEST_ATFOLLOWERS && resultCode == RESULT_OK) {
            if (result != null) {
                UserInfo userInfo = (UserInfo) result.getSerializableExtra("user");
                MyLog.i("userInfo=" + userInfo.getUId() + "/" + userInfo.getUsername());
                // -2 是因为还有@name空格.首位两个字符
                if (mEtContent.getText().length() + userInfo.getUsername().length() > maxlength - 2) {
                    toast("你输入的字数已经超过了限制！如果想@好友,请删除一些字符!", Toast.LENGTH_LONG);
                    return;
                }

                if (atUsersMap == null) {
                    atUsersMap = new HashMap<>();
                }
                if (!atUsersMap.containsKey(userInfo.getUId())) {
                    atUsersMap.put(userInfo.getUId(), userInfo.getUsername());
                }

                if (atUsers == null) {
                    atUsers = new ArrayList<>();
                }
                if (atUsers.contains(userInfo.getUId())) {
                    toast("你已经 @" + userInfo.getUsername() + " 了", Toast.LENGTH_SHORT);
                    int startIndex = mEtContent.getText().toString().indexOf("@" + userInfo.getUsername());
                    mEtContent.setSelection(startIndex + ("@" + userInfo.getUsername()).length() + 1);
                } else {
                    atUsers.add(userInfo.getUId());
                    insertText(StringUtil.isNullOrEmpty(userInfo.getUsername()) ? userInfo.getUId() : userInfo.getUsername());
                }
            }
        } else if (requestCode == REQUEST_WRITE_AUTH && resultCode == RESULT_OK) {
            try {
                MyLog.i("REQUEST_WRITE_AUTH result= " + result.getIntExtra(WriteAuthFragment.KEY_WRITE_AUTH, -1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result != null) {
                int auth = result.getIntExtra(WriteAuthFragment.KEY_WRITE_AUTH, 4);
                if (mWriteAuth != auth) {
                    mWriteAuth = auth;
                    mTvAuth.setText(mWriteAuth == 1 ? R.string.auth_none_title : R.string.auth_all_title);
                }
            }
        } else if (requestCode == REQUEST_RECOMMEND_WORD && resultCode == RESULT_OK) {
            String recommendWord = result.getExtras().getString(RecommendWordActivity.RECOMMEND_WORD);
            insertRecommendWord(recommendWord);
        }
    }

    private void insertRecommendWord(String recommendWord) {
        String text = recommendWord + " ";
        mEtContent.removeTextChangedListener(textWatcher);
        Editable editable = mEtContent.getText();

        if (editable != null) {
            editable.delete(0,editable.toString().length());
        }

        mEtContent.setText(text);

        if (atUsersMap != null) {
            atUsersMap.clear();
        }
        if (atUsers != null) {
            atUsers.clear();
        }
        mEtContent.setSelection(text.length());
        numberTips.setText(mEtContent.getText().length() + "/"
                + maxlength);
        mEtContent.addTextChangedListener(textWatcher);
    }

    /**
     * 获取光标位置
     */
    private int getSelectionStart(EditText editText) {
        return editText.getSelectionStart();
    }

    /**
     * 在光标处插入字符
     */
    private void insertText(String text) {
        if (StringUtil.isNullOrEmpty(text))
            return;
        String newText = "@" + text + " ";
        int index = getSelectionStart(mEtContent);
        mEtContent.removeTextChangedListener(textWatcher);
        mEtContent.removeTextChangedListener(textWatcher);
        Editable editable = mEtContent.getText();
        index = StringUtil.getInputPosition(editable.toString(), index);
        editable.insert(index, newText);

        int endIndex = index + newText.length();
        editable.toString(); // 重要!这句应该在setText之前显式的调用一次,否则setSelection时会统计len出错.

        mEtContent.setText(StringUtil.getStyledText(editable.toString()));
        mEtContent.setSelection(endIndex);
        numberTips.setText(mEtContent.getText().length() + "/"
                + maxlength);
        mEtContent.addTextChangedListener(textWatcher);
    }


    /**
     * 删除光标前字符
     */
    private void deleteText(EditText editText, String text) {
        if (StringUtil.isNullOrEmpty(text))
            return;
        int index = getSelectionStart(editText);
        Editable editable = editText.getText();
        editable.delete(index - 1, index);
//        editable.delete(index, text.length() + index - 1);
    }

    private void uploadXgt(String ucid, String xgt) {
        if (!StringUtil.isNullOrEmpty(xgt)) {
            CameraManager.getInst().saveUnuploadFiles2DataBase(DiscoveryConstants.TYPE_XGT, ucid, xgt, 0, System.currentTimeMillis());
            CameraManager.getInst().uploadFiles(this, DiscoveryConstants.TYPE_XGT, ucid, xgt, 0, 0, null);
        } else {
            toast(2);
        }
    }

    private void uploadGcwj(String ucid, String gcwj) {
        if (!StringUtil.isNullOrEmpty(gcwj)) {
            CameraManager.getInst().saveUnuploadFiles2DataBase(DiscoveryConstants.TYPE_PRJ, ucid, gcwj, 0, System.currentTimeMillis());
            CameraManager.getInst().uploadFiles(this, DiscoveryConstants.TYPE_PRJ, ucid, gcwj, 0, 0, null);
        } else {
            toast(2);
        }
    }

    /**
     * @param type 1:noNetwork 2:release failed 3:origin deleted
     */
    private void toast(int type) {
        if (type == 1) {
            dismissProgressDialog();
            titleBar.setRightBtnClickAble(true);
            AppContext.showToastShort(R.string.hasno_network);
        } else if (type == 2) {
            dismissProgressDialog();
            titleBar.setRightBtnClickAble(true);
            AppContext.showToastShort(R.string.release_fail);
        } else if (type == 3) {
            dismissProgressDialog();
            titleBar.setRightBtnClickAble(true);
            AppContext.showToastShort(R.string.cosplay_submit_delete);
        }
    }

    private void submit() {
        showProgressDialog(getString(R.string.progress_submit));
        // TODO 这里是原创，所以parent给了个null,如果是改图转发，应该有parent
        String bucket = StickerManager.getInstance().getBestServerBucket();
        String xgtKey = StringUtil.getUploadKey(DiscoveryConstants.TYPE_XGT, mData.getStringExtra(KEY_XGT), null);
        String gcwjKey = StringUtil.getUploadKey(DiscoveryConstants.TYPE_PRJ, mData.getStringExtra(KEY_GCWJ), null);
        MyLog.i("submit接口:bucket=" + bucket + "xgtKey =" + xgtKey + ", gcwjKey=" + gcwjKey);

        MyLog.i("参与话题的名称:[" + StickPreference.getInstance().getJoinTopicName() + "]");
        //TODO, 发布图片时需要加上话题名称
        StickerManager.getInstance().submitStickerPic(CosplayInfo.TYPE_COSPLAY, bucket, gcwjKey,xgtKey,
                getStickIDs(mData.getStringArrayListExtra(KEY_STICKER_IDS)), null, mAudioList,
                getIntent().getStringExtra(KEY_PARENT_UCID), getAtUsers(),
                mEtContent.getText().toString(),
                StickPreference.getInstance().getJoinTopicName(),
                4, mWriteAuth, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        //dismiss弹框
                        dismissProgressDialog();

                        //后台上传效果图和工程文件
                        String ucid = (String) obj;
                        String xgt = backupCosplayFile(ucid, mData.getStringExtra(KEY_XGT), 1);
                        String gcwj = backupCosplayFile(ucid, mData.getStringExtra(KEY_GCWJ), 2);
                        if (StringUtil.isNullOrEmpty(xgt) || StringUtil.isNullOrEmpty(gcwj)) {
                            AppContext.showToast(R.string.no_sufficient_storage);
                            return;
                        } else {
                            uploadXgt(ucid, xgt);
                            uploadGcwj(ucid, gcwj);
                        }

                        //保存带水印的图到本地相册
                        String path = saveXgt();

                        //toast发布成功
                        MyLog.e("发布成功 UCID=" + obj + " @ " + TimeUtils.getCurrentTimeInString());
                        AppContext.showToastShort(R.string.release_success);

                        //保存分享平台信息
                        saveSharePlatform(ucid, path);

                        //发消息给关注频道并关闭大咖秀制作流程相关页面
                        EventBus.getDefault().post(releaseOkEvent);
                        CameraManager.getInst().close();

                        //跳转关注
                        UIHelper.gotoMain(PhotoReleaseActivity.this, MainActivity.KEY_TAB_FOLLOW, false);

                        StickPreference.getInstance().deleteJoinTopicInfo();
                    }

                    @Override
                    public void onErr(Object obj) {
                        MyLog.e("发布失败：" + obj + " @ " + TimeUtils.getCurrentTimeInString());
                        int error = (int) obj;
                        if (error == ErrorCode.ERROR_COSPLAY_DELETED) {
                            toast(3);
                        } else {
                            toast(2);
                        }
                        StickPreference.getInstance().deleteJoinTopicInfo();
                    }

                    @Override
                    public void onNoNetwork() {
                        MyLog.e("发布失败 NO——NETWORK @ " + TimeUtils.getCurrentTimeInString());
                        toast(1);
                    }
                });
    }

    //备份效果图和工程文件给上传用,标记1:效果图 2:工程文件
    private String backupCosplayFile(String ucid, String src, int flag) {
        String dest = CameraManager.getInst().getCosplayElementPath(ucid, flag);
        if (StringUtil.isNullOrEmpty(dest)) {
            return null;
        }
        FileUtil.getInst().copyFile(src, dest);
        if ((new File(dest)).exists()) {
            return dest;
        }
        return null;
    }

    /**保存有水印的效果图*/
    private String saveXgt() {
        if (AppContext.get(AppConfig.KEY_SAVE_ORIGINAL, true)) {
            String name = mData.getStringExtra(KEY_XGT);
            File xgt = new File(name);

            if (isShare2WechatFriend || isShare2Sina || isShare2QQZone) {
                // 如果三方平台需要用到,就保留这个没水印的图,到Main里去删除
                String destPath = FileUtil.getInst().getPhotoTempPath();
                FileUtil.getInst().moveFile(name, destPath);

                String destFilePath = destPath + File.separator + xgt.getName();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse(destFilePath)));

                return destFilePath;
            }
            // 做一个有水印的即可
            StickerUtils.savePicWithWatermark(PhotoReleaseActivity.this, name, null, true);

            FileUtil.getInst().clearFileWithPath(xgt.getParent());
            FileUtil.removeFolder(xgt.getParent());
        }

        return "";
    }

    private String getAtUsers() {

        if (atUsers == null || atUsers.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < atUsers.size(); i++) {
            if (i != 0) {
                sb.append(",").append(atUsers.get(i));
            } else {
                sb.append(atUsers.get(i));
            }
        }

        return sb.toString();
    }

    private List<StickerId> getStickIDs(ArrayList<String> stickers) {
        List<StickerId> stickerIds = new ArrayList<>();
        if (stickers == null || stickers.isEmpty())
            return stickerIds;
        for (String sticker : stickers) {
            String[] tmp = sticker.split("_");
            StickerId mID = new StickerId(tmp[0], (StringUtil.isNullOrEmpty(tmp[1]) || "null".equalsIgnoreCase(tmp[1])) ? null : tmp[1]);
            if (mID != null) {
                stickerIds.add(mID);
            }
        }
        return stickerIds;
    }

    public static class ReleaseOkEvent {
        private static String releaseOk;
        private static long clickTime;

        public String getReleaseOk() {
            return releaseOk;
        }

        public static long getClickTime() {
            return clickTime;
        }

        public static void setClickTime(long clickTime) {
            ReleaseOkEvent.clickTime = clickTime;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.PHOTO_RELEASE_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.PHOTO_RELEASE_ACTIVITY);
        MobclickAgent.onPause(this);
    }
}

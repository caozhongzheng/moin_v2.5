package com.moinapp.wuliao.modules.mine;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.AppManager;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.FollowStatusChange;
import com.moinapp.wuliao.bean.Location;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.login.LoginManager;
import com.moinapp.wuliao.modules.mine.model.UploadAvatarResult;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.modules.stickercamera.app.camera.ui.PhotoProcessActivity;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.MyNumberPicker;
import com.moinapp.wuliao.ui.MyPopWindow;
import com.moinapp.wuliao.ui.SimpleBackActivity;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.PListHandler;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.UIHelper;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 个人信息界面
 * Created by moying on 15/5/11.
 */
public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {

    private ILogger MyLog = LoggerFactory.getLogger("pif");

    private MyPopWindow alter_avatar_popupWindow, alter_username_popupWindow, alter_nickname_popupWindow,
            alter_age_popupWindow, alter_gender_popupWindow, alter_signature_popupWindow,
            alter_location_popupWindow;

    @InjectView(R.id.titlebar)
    CommonTitleBar mTitleBar;
    @InjectView(R.id.user_avatar)
    ImageView mIvAvatar;
    @InjectView(R.id.username)
    TextView mTvUsername;
    @InjectView(R.id.contact)
    TextView mTvContact;
    @InjectView(R.id.age)
    TextView mTvAge;
    @InjectView(R.id.gender)
    TextView mTvGender;
    @InjectView(R.id.gender_icon)
    ImageView mIvGender;
    @InjectView(R.id.zodiac)
    TextView mTvZodiac;
    @InjectView(R.id.location)
    TextView mTvLocation;
    @InjectView(R.id.signature)
    TextView mTvSignature;
    @InjectView(R.id.register)
    TextView mTvRegister;

    @InjectView(R.id.avatar_title_hint)
    TextView mTvAvatarHint;
    @InjectView(R.id.username_title_hint)
    TextView mTvUsernameHint;
    @InjectView(R.id.gender_title_hint)
    TextView mTvGenderHint;

    private ImageView alter_sex_male_selected, alter_sex_female_selected, clear_username, clear_signature,
            iv_sex_male, iv_sex_female, alter_username_cancel, alter_signature_cancel, alter_sex_cancel;
    private TextView alter_age_cancel, alter_age_sure, alter_avatar_album, alter_avatar_camera, alter_avatar_cancel,
            alter_signature_sure, remaining_number, alter_username_sure,
            alter_location_cancel, alter_location_sure;
    private LinearLayout alter_sex_male, alter_sex_female;
    private EditText alter_username_et, alter_signature_et;
    // --------------------------------------
    public static String KEY_USERNAME = "username";
    public static String KEY_NICKNAME = "nickname";
    public static String KEY_SEX = "sex";
    public static String KEY_HEADIMGURL = "headimgurl";
    public static String KEY_COUNTRY = "country";
    public static String KEY_CITY = "city";
    public static String KEY_PROVINCE = "province";
    public static String KEY_PLATFORM = "platform";
    public static String KEY_ZODIAC = "zodiac";
    public static String KEY_MUST = "must";
    public static String DATE_FORMAT_STRING = "yyyy-MM-dd";
    public static final String FROMMINE = "mine";
    private String FROM = "";
    public static int ZODIAC_NONE = -1;
    private boolean mMust = false;
    // --------------------------------------
    public static final String AGE_NONE = "-1";
    // --------------------------------------
    public static final int NO_SDCARD = -1;
    public static final int NONE = 0;
    public static final int PHOTO_HRAPH = 1;// 拍照
    public static final int PHOTO_ZOOM = 2; // 缩放
    public static final int PHOTO_RESULT = 3;// 结果
    public static final int CROP_CAMERA = 6;// 截取拍照的头像
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final int ZODIAC_RESULT = 4;// 结果
    public static final int CITY_RESULT = 5;// 选择城市结果
    public static final int CONTACT_RESULT = 7;// 修改联系方式结果
    private int zodiac_i = ZODIAC_NONE;
    // --------------------------------------
    private int signature_max = 0;
    private String username_str, contact_str, age_str, sex_param_str, headimgurl_str, country_str, city_str, province_str, signature_str;

    private SHARE_MEDIA platform;
    private DatePicker datePicker;
    private String birthday;
    private int year,month,day,chooseYear,chooseMonth,chooseDay;
    private long birthdayLong;
    private MyNumberPicker np_city,np_province;
    private Location location;
    private EditText datePickerEditText;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregist(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personalinformation;
    }

    @Override
    public void initView() {
        mTitleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBack();
            }
        });

    }

    /**
     * 在intent中如果获取了用户信息，那么就用intent中的
     **/
    @Override
    public void initData() {
        MyLog.i("initData start");
        Intent intent = getIntent();
        age_str = AGE_NONE;
        signature_max = getResources().getInteger(R.integer.signature_max_len);

        if (intent != null) {
            String plat = intent.getStringExtra(KEY_PLATFORM);
            MyLog.i("from=" + plat);
            if (!StringUtil.isNullOrEmpty(plat)) {
                if (plat.equals(FROMMINE)) {
                    FROM = FROMMINE;
                } else {
                    platform = SHARE_MEDIA.convertToEmun(plat);
                    MyLog.i("platform.name=" + platform.name());
                    MyLog.i("platform=" + platform.toString());
                }
            }
            username_str = StringUtil.nullToEmpty(intent.getStringExtra(KEY_USERNAME));
            mTvUsername.setText(username_str);
//            checkUserName(username_str);

            sex_param_str = StringUtil.nullToEmpty(intent.getStringExtra(KEY_SEX));
            if (sex_param_str != null && !sex_param_str.equals("")) {
                mTvGender.setText(StringUtil.getGender(PersonalInfoActivity.this, sex_param_str, true));
                updateGenderIncon(sex_param_str);
            }
            MyLog.i("sex=" + sex_param_str);
            headimgurl_str = intent.getStringExtra(KEY_HEADIMGURL);
            MyLog.i("headimgurl=" + headimgurl_str);
            ImageLoaderUtils.displayHttpImage(headimgurl_str, mIvAvatar, null);
            country_str = intent.getStringExtra(KEY_COUNTRY);
            city_str = intent.getStringExtra(KEY_CITY);
            province_str = intent.getStringExtra(KEY_PROVINCE);

            mMust = intent.getBooleanExtra(KEY_MUST, false);
            mTvAvatarHint.setVisibility(mMust ? View.VISIBLE : View.GONE);
            mTvUsernameHint.setVisibility(mMust ? View.VISIBLE : View.GONE);
            mTvGenderHint.setVisibility(mMust ? View.VISIBLE : View.GONE);

            Location l = new Location();
            l.setProvince(province_str);
            l.setCity(city_str);
            l.setCountry(country_str);
            mTvLocation.setText(getLocation(l));
        }

        mHandler.sendEmptyMessageDelayed(0, platform == null ? 0 : 5000);

//        initAvatarWindow();
        initUsernameWindow();
        initAgeWindow();
        initGenderWindow();
        initSignatureWindow();
        initLocationWindow();

        MyLog.i("initData end");
    }
//
//    @Override
//    protected void reloadHandle() {
//        super.reloadHandle();
//        refreshUserInfo();
//    }


// TODO ————————————————————————————————————————————————————————————————————— 头像 —————————————————————————————————————————————————————————————————————————————————

    private void uploadAvatar() {
        Message message = new Message();
        message.what = 1;
        message.arg1 = 1;
        message.obj = BitmapUtil.getAvatarCropPath();
        mHandler.sendMessage(message);
    }

    private void uploadAvatar(String path) {
        Message message = new Message();
        message.what = 1;
        message.arg1 = 1;
        message.obj = path;
        mHandler.sendMessage(message);
    }

    private void deleteTmpFile() {
        File tmp = new File(BitmapUtil.getTmpAvatarCameraPath());
        if (tmp.exists()) {
            tmp.delete();
        }
        File tmp2 = new File(BitmapUtil.getAvatarCropPath());
        if (tmp2.exists()) {
            tmp2.delete();
        }
    }

    private void saveAvatarByUrl(String headimgurl_str) {
        ImageLoaderUtils.displayHttpImage(headimgurl_str, BitmapUtil.getImageLoaderOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                BitmapUtil.saveUserAvatar(PersonalInfoActivity.this, bitmap);
                mHandler.post(runnable_avatar);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    Runnable runnable_avatar = new Runnable() {
        @Override
        public void run() {
            MyLog.i("runnable_avatar: " + headimgurl_str);
            if (mIvAvatar != null && headimgurl_str != null) {
                ImageLoaderUtils.displayHttpImage(headimgurl_str, mIvAvatar, null);
            }
        }
    };

    /**
     * 修改头像
     */
    public void initAvatarWindow() {
        MyLog.i("initAvatarWindow start");
        View popupWindow_view = getLayoutInflater().inflate(R.layout.alter_avatar, null);
        popupWindow_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        alter_avatar_album = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_album);
        alter_avatar_camera = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_camera);
        alter_avatar_cancel = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_cancel);
        alter_avatar_album.setOnClickListener(this);
        alter_avatar_camera.setOnClickListener(this);
        alter_avatar_cancel.setOnClickListener(this);
        alter_avatar_popupWindow = new MyPopWindow(this, popupWindow_view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        MyLog.i("initAvatarWindow end");
    }

    // 拍照
    private void doCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(BitmapUtil.getTmpAvatarCameraPath())));
        startActivityForResult(intent, PHOTO_HRAPH);
    }

    // 制作一个大咖秀头像
    private void makeCosplayAvatar() {
        Bundle bundle = new Bundle();
        bundle.putString(DiscoveryConstants.FROM, StringUtil.FROM_PERSONAL);
        CameraManager.getInst().openCamera(PersonalInfoActivity.this, bundle);
    }

    // 选择我做过的大咖秀
    private void selectMyCosplay() {
        UIHelper.showMyCosplay(PersonalInfoActivity.this, ClientInfo.getUID());
    }

    public void onEvent(PhotoProcessActivity.AvatarPath avatarPath) {
        if (avatarPath != null && !StringUtil.isNullOrEmpty(avatarPath.getPath())) {
            uploadAvatar();
        }
    }

    public void onEvent(CropCosplayFragment.CropCosplay avatarPath) {
        if (avatarPath != null && !StringUtil.isNullOrEmpty(avatarPath.getPath())) {
            AppManager.getAppManager().finishActivity(SimpleBackActivity.class);
            uploadAvatar();
        }
    }

    /**
     * 剪切头像 此处用大图格式，由服务器生成不同分辨率对应的头像尺寸
     */
    public void cropAvatar(Uri uri) {
        MyLog.i("avatar cropImageUri:" + uri);
        /*
Exta Options Table for image crop:
SetExtra	DataType	Description
crop	String	Signals the crop feature
aspectX	int	Aspect Ratio
aspectY	int	Aspect Ratio
outputX	int	width of output created from this Intent
outputY	int	width of output created from this Intent
scale	boolean	should it scale
return-data	boolean	Return the bitmap with Action=inline-data by using the data
data	Parcelable	Bitmap to process, you may provide it a bitmap (not tested)
circleCrop	String	if this string is not null, it will provide some circular cr
MediaStore.EXTRA_OUTPUT ("output")	URI	Set this URi to a File:
*/

        if (uri == null) {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 640);
            intent.putExtra("outputY", 640);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, BitmapUtil.getAvatarCropUri());
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true); // no face detection
        /*if this string is not null, it will provide some circular cr*/
            // TODO circleCrop目前还不能用
            intent.putExtra("circleCrop", true);
            startActivityForResult(intent, PHOTO_RESULT);
        } else {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 640);
            intent.putExtra("outputY", 640);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, BitmapUtil.getAvatarCropUri());
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true); // no face detection
            // TODO circleCrop目前还不能用
            intent.putExtra("circleCrop", true);
            startActivityForResult(intent, CROP_CAMERA);
        }
    }

// TODO ————————————————————————————————————————————————————————————————————— 昵称 —————————————————————————————————————————————————————————————————————————————————

    /**
     * 修改用户名对话框
     */
    private void initUsernameWindow() {
        View alter_username_view = this.getLayoutInflater().inflate(R.layout.alter_username, null);
        alter_username_et = (EditText) alter_username_view.findViewById(R.id.alter_username);

        setTranHeader(alter_username_view);

        alter_username_cancel = (ImageView) alter_username_view.findViewById(R.id.alter_username_cancel);
        alter_username_sure = (TextView) alter_username_view.findViewById(R.id.alter_username_sure);
        clear_username = (ImageView) alter_username_view.findViewById(R.id.clear_username);
        alter_username_cancel.setOnClickListener(this);
        alter_username_sure.setOnClickListener(this);
        alter_username_sure.setClickable(true);
        clear_username.setOnClickListener(this);

        alter_username_popupWindow = new MyPopWindow(this, alter_username_view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 检查用户名唯一性,并更新用户名
     * 0 已存在，不能使用 1 可以使用
     *
     * @param username
     */
    private void checkUserName(final String username) {
        if (StringUtil.isNullOrEmpty(username)) {
            AppContext.showToastShort("昵称不能为空");
            return;
        }

//        if (username.contains(" ") || username.contains("@")
//                || username.contains(",")|| username.contains(",")){
//            AppContext.showToastShort("不能输入空格或者@符号");
//            return;
//        }

        LoginManager.getInstance().checkUserName(username, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                mTvUsername.setError(null);
                UserInfo us = new UserInfo();
                us.setUsername(username);
                MineManager.getInstance().updateUserInfo(us, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        MyLog.i("username update OK: " + username);
                        MinePreference.getInstance().setUsername(username);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                username_str = username;
                                mTvUsername.setText(username_str);
                            }
                        });
                        alter_username_popupWindow.dismiss();
                    }

                    @Override
                    public void onErr(Object obj) {
                        mHandler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onNoNetwork() {
                        mHandler.sendEmptyMessage(5);
                    }
                });
            }

            @Override
            public void onErr(Object obj) {
//                mTvUsername.setError(getString(R.string.invalid_username_tip));
                AppContext.showToastShort("昵称不合法");
            }

            @Override
            public void onNoNetwork() {
                mHandler.sendEmptyMessage(5);
            }
        });

    }

    private void refreshUserInfo() {
        MineManager.getInstance().getUserInfo(null, new IListener() {
            @Override
            public void onSuccess(Object obj) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        setLoadingMode(MODE_OK);
//                    }
//                });
                Message msg = mHandler.obtainMessage();
                msg.what = 2;
                msg.obj = obj;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onNoNetwork() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        setLoadingMode(MODE_RELOADING);
//                    }
//                });
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onErr(Object object) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        setLoadingMode(MODE_RELOADING);
//                    }
//                });
            }
        });
    }


// TODO ————————————————————————————————————————————————————————————————————— 性别 —————————————————————————————————————————————————————————————————————————————————


    /**
     * 修改性别
     */
    public void initGenderWindow() {
        View popupWindow_view = this.getLayoutInflater().inflate(R.layout.alter_gender, null);
        alter_sex_male = (LinearLayout) popupWindow_view.findViewById(R.id.alter_sex_male);
        alter_sex_female = (LinearLayout) popupWindow_view.findViewById(R.id.alter_sex_female);
        iv_sex_male = (ImageView) popupWindow_view.findViewById(R.id.iv_sex_male);
        iv_sex_female = (ImageView) popupWindow_view.findViewById(R.id.iv_sex_female);
        MyLog.i("initGenderWindow sex_param_str: " + sex_param_str);

        setTranHeader(popupWindow_view);

        selectGender();

        alter_sex_cancel = (ImageView) popupWindow_view.findViewById(R.id.alter_sex_cancel);
        alter_sex_male.setOnClickListener(this);
        alter_sex_female.setOnClickListener(this);
        alter_sex_cancel.setOnClickListener(this);
        alter_gender_popupWindow = new MyPopWindow(this, popupWindow_view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    private void selectGender() {
        if ("1".equals(sex_param_str)) {
            selectMale(true);
        } else if ("2".equals(sex_param_str)) {
            selectMale(false);
        }
    }

    private void updateSex() {
        final String gender_str = StringUtil.getGender(PersonalInfoActivity.this, sex_param_str, true);

        UserInfo us = new UserInfo();
        us.setSex(StringUtil.getGender(PersonalInfoActivity.this, sex_param_str, false));
        MineManager.getInstance().updateUserInfo(us, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("update sex OK:" + gender_str);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTvGender.setText(gender_str);
                        updateGenderIncon(gender_str);
                        // 通知个人空间更改信息
                        EventBus.getDefault().post(new FollowStatusChange());
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("udpate sex NG:" + getString(R.string.no_network));
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("update sex NG:" + gender_str);
                mHandler.sendEmptyMessage(5);
            }
        });
    }

    private void updateGenderIncon(String gender_str) {
        String man = getResources().getString(R.string.male);
        String woman = getResources().getString(R.string.female);
        if ("1".equals(gender_str) || "male".equals(gender_str) || man.equals(gender_str)) {
            mIvGender.setImageResource(R.drawable.male_yellow);
        } else if ("2".equals(gender_str) || "female".equals(gender_str) || woman.equals(gender_str)) {
            mIvGender.setImageResource(R.drawable.female_red);
        } else {
            mIvGender.setVisibility(View.INVISIBLE);
        }
    }

    private void selectMale(boolean b) {
        if (b) {
            //男
            iv_sex_male.setBackgroundResource(R.drawable.male_yellow);
            iv_sex_female.setBackgroundResource(R.drawable.female_black);
//            alter_sex_male.setTextColor(getResources().getColor(R.color.common_text_main));
//            alter_sex_female.setTextColor(getResources().getColor(R.color.common_title_grey));
        } else {
            iv_sex_male.setBackgroundResource(R.drawable.male_black);
            iv_sex_female.setBackgroundResource(R.drawable.female_red);
//            alter_sex_male.setTextColor(getResources().getColor(R.color.common_title_grey));
//            alter_sex_female.setTextColor(getResources().getColor(R.color.common_text_main));
        }
    }

// TODO ————————————————————————————————————————————————————————————————————— 生日 —————————————————————————————————————————————————————————————————————————————————


    /**
     * 修改年龄对话框
     */
    private void initAgeWindow() {
        View alter_age_view = this.getLayoutInflater().inflate(R.layout.alter_age, null);
        alter_age_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        datePicker = (DatePicker) alter_age_view.findViewById(R.id.datePicker);
        datePicker.setMinDate(dateFormat("1970-01-01"));
        datePicker.setMaxDate(System.currentTimeMillis());
        setDatePickerDividerColor(datePicker);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        alter_age_cancel = (TextView) alter_age_view.findViewById(R.id.alter_age_cancel);
        alter_age_sure = (TextView) alter_age_view.findViewById(R.id.alter_age_sure);
        alter_age_cancel.setOnClickListener(this);
        alter_age_sure.setOnClickListener(this);

        alter_age_popupWindow = new MyPopWindow(this, alter_age_view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    private void dateChooseShow() {
        chooseYear = year;
        chooseMonth = month;
        chooseDay = day;
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                chooseYear = year;
                chooseMonth = monthOfYear;
                chooseDay = dayOfMonth;
            }
        });
        alter_age_popupWindow.showButtom();
    }

    private void dateChooseEnsure() {
        alter_age_popupWindow.dismiss();

        year = chooseYear;
        month = chooseMonth;
        day = chooseDay;
        age_str = year + "-" + setNumber(month + 1) + "-" + setNumber(day);
        mTvAge.setText(age_str);
        birthdayLong = dateFormat(age_str);
        updateBirthday(birthdayLong);
        updateConstellation(month, day);
    }

    private void updateConstellation(int month, int day) {
        final int[] constellationEdgeDay = {20, 18, 20, 20, 20, 21, 22, 22, 22, 22, 21, 21};

        if (day <= constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month < 0) {
            month = 11;
        }
        if (month >= 0 && month < 12) {

            UserInfo us_z = new UserInfo();
            us_z.setStars(month + 1);
            final int z = month;

            MineManager.getInstance().updateUserInfo(us_z, new IListener() {
                @Override
                public void onSuccess(Object obj) {
                    MyLog.i("update stars OK:" + getString(AlterZodiacActivity.zodiac_name_arr[z]));
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            zodiac_i = z;
                            mTvZodiac.setText(getString(AlterZodiacActivity.zodiac_name_arr[z]));
                        }
                    });
                }

                @Override
                public void onNoNetwork() {
                    MyLog.i("udpate stars NG:" + getString(R.string.no_network));
                    mHandler.sendEmptyMessage(3);
                }

                @Override
                public void onErr(Object object) {
                    MyLog.i("update stars NG:" + getString(AlterZodiacActivity.zodiac_name_arr[z]));
                    mHandler.sendEmptyMessage(5);
                }
            });
        }

    }

    private void dateChooseCancle() {
        chooseYear = 0;
        chooseMonth = 0;
        chooseDay = 0;
        alter_age_popupWindow.dismiss();
    }

    private long dateFormat(String birthday) {
        // 解析格式，yyyy表示年，MM(大写M)表示月,dd表示天
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING);
        try {
            // 用parse方法，可能会异常，所以要try-catch
            Date date = format.parse(birthday);
            // 获取日期实例
            Calendar calendar = Calendar.getInstance();
            // 将日历设置为指定的时间
            calendar.setTime(date);
            // 获取年
            year = calendar.get(Calendar.YEAR);
            // 这里要注意，月份是从0开始。
            month = calendar.get(Calendar.MONTH);
            // 获取天
            day = calendar.get(Calendar.DAY_OF_MONTH);

            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
    * 判断数字是否小于10,如果是返回"0i"形式的字符串
    * */
    private String setNumber(int i) {
        String num = "";
        if (i < 10) {
            num = "0" + i;
        } else {
            num = i + "";
        }
        return num;
    }

    private void setDatePickerDividerColor(DatePicker datePicker){
        // Divider changing:

        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

        // 获取 NumberPicker
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);
//
//            int childCount = picker.getChildCount();
//
//            for (int k = 0; k < childCount; k++) {
//                View childAt = picker.getChildAt(k);
//                if (childAt instanceof EditText) {
//                    //这里修改字体的属性
//                    ((EditText) childAt).setTextSize(20);
//                }
//            }

            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
//                MyLog.i("pickerFields name : -- " + pf.getName());
                //设置分割线颜色
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, new ColorDrawable(this.getResources().getColor(R.color.transparent)));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }


    private void updateBirthday(long birthdayLong) {

        UserInfo us = new UserInfo();
        us.setBirthday(birthdayLong);
        MineManager.getInstance().updateUserInfo(us, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("udpate birthday OK:" + age_str);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTvAge.setText(age_str);
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("udpate birthday NG:" + getString(R.string.no_network));
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("udpate birthday NG:" + age_str);
                mHandler.sendEmptyMessage(5);
            }
        });
    }


// TODO ————————————————————————————————————————————————————————————————————— 所在地 —————————————————————————————————————————————————————————————————————————————————

    private HashMap<String, Object> provinceMap = new HashMap<>();
    String[] provinces;
    String[] citys;
    UserInfo userInfo;
    /**
     * 修改所在地对话框
     */
    private void initLocationWindow() {
        View alter_location_view = this.getLayoutInflater().inflate(R.layout.alter_location, null);
        alter_location_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());

        alter_location_cancel = (TextView) alter_location_view.findViewById(R.id.alter_location_cancel);
        alter_location_sure = (TextView) alter_location_view.findViewById(R.id.alter_location_sure);
        np_province = (MyNumberPicker) alter_location_view.findViewById(R.id.np_province);
        np_city = (MyNumberPicker) alter_location_view.findViewById(R.id.np_city);

        setDefaultPicker(np_province);
        setDefaultPicker(np_city);

        initCityDate();

        setDisplayDate(np_province, provinces);

        np_province.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                String province = provinces[newVal];

                ArrayList<String> cityList = (ArrayList<String>) provinceMap.get(province);

                if (cityList.size() == 0) {
                    citys = new String[]{" "};
                } else {
                    citys = new String[cityList.size()];
                    cityList.toArray(citys);
                }

                setDisplayDate(np_city, citys, true);
            }
        });

        alter_location_cancel.setOnClickListener(this);
        alter_location_sure.setOnClickListener(this);

        alter_location_popupWindow = new MyPopWindow(this, alter_location_view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * 给NumberPicker设置数据,并使NumberPicker不能循环滑动
     * */
    private void setDisplayDate(NumberPicker numberPicker, String[] date) {
        setDisplayDate(numberPicker, date, false);
    }
    private void setDisplayDate(NumberPicker numberPicker, String[] date, boolean showFirst) {
        if (numberPicker == null || date == null || date.length == 0) return;

        int oldMaxValye = numberPicker.getMaxValue();
        if (oldMaxValye + 1 <= date.length) {
            numberPicker.setDisplayedValues(date);
        }
        numberPicker.setMaxValue(date.length - 1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDisplayedValues(date);
        if (showFirst) {
            numberPicker.setValue(0);
        }
    }

    /**
     * 给NumberPicker设置初始数据
     * */
    private void setDefaultDate() {
        int provinceIndex = 0 ;
        int cityIndex = 0 ;
        if (userInfo != null) {
            location = userInfo.getLocation();
        }

        if (location != null && location.getProvince() != null && !location.getProvince().equals("")) {
            String province = location.getProvince();
            if (province != null || province.equals("")) {
                for (int i = 0; i < provinces.length; i++) {
                    if (provinces[i].equals(province)) {
                        provinceIndex = i;
                        break;
                    }
                }

                if (provinceIndex == 0) {
                    province = provinces[0];
                }

                ArrayList<String> cityList = (ArrayList<String>) provinceMap.get(province);
                if (cityList == null || cityList.size() == 0) {
                    citys = new String[]{" "};
                } else {
                    citys = new String[cityList.size()];
                    cityList.toArray(citys);
                }
                setDisplayDate(np_city, citys);

                String city = location.getCity();

                if (cityList != null && city != null) {
                    cityIndex = cityList.indexOf(city);
                }
            } else {
                setNoLocationDate();
            }
            np_province.setValue(provinceIndex);
            np_city.setValue(cityIndex);
        } else {
            setNoLocationDate();
        }
    }

    /**
     * 如果用户之前未选择过Location,给NumberPicker设置默认数据
     * */
    private void setNoLocationDate() {
        String defaultprovince = provinces[0];
        ArrayList<String> defaultCityLIst = (ArrayList<String>) provinceMap.get(defaultprovince);
        citys = new String[defaultCityLIst.size()];
        defaultCityLIst.toArray(citys);
        setDisplayDate(np_city, citys);
    }

    private void updateLocation() {
        int provinceIndex = np_province.getValue();
        int cityIndex ;
        String city;
        String province = provinces[provinceIndex];

        ArrayList<String> cityList = (ArrayList<String>) provinceMap.get(province);
        if (cityList == null || cityList.size() == 0) {
            city = null;
        }else {
            String[] citys = new String[cityList.size()];
            cityList.toArray(citys);
            cityIndex = np_city.getValue();
            city = citys[cityIndex];
        }

        UserInfo us = new UserInfo();
        Location location = new Location();
        location.setProvince(province);
        location.setCity(city);
        us.setLocation(location);
        final String city_text = getLocation(location);

        MineManager.getInstance().updateUserInfo(us, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("udpate location OK:" + city_text);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        userInfo = us;
                        city_str = city_text;
                        mTvLocation.setText(city_str);
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("udpate location NG:" + getString(R.string.no_network));
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("udpate location NG:" + city_text);
                mHandler.sendEmptyMessage(5);
            }
        });
    }

    /**
     * 加载归属地信息
     * */
    private void initCityDate() {
        SAXParserFactory factorys = SAXParserFactory.newInstance();
        SAXParser saxparser = null;
        InputStream open = null;
        PListHandler plistHandler = null;
        try {
            open = getResources().getAssets().open("CityList.plist");

            saxparser = factorys.newSAXParser();
            plistHandler = new PListHandler();
            saxparser.parse(open, plistHandler);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        List<Object> arrayResult = plistHandler.getArrayResult();

        provinces = new String[arrayResult.size()];

        for (int i = 0; i < arrayResult.size(); i++) {

            HashMap<String, Object> map = (HashMap<String, Object>) arrayResult.get(i);

            Set entries = map.entrySet();
            if (entries != null) {
                Iterator iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String key = (String) entry.getKey();
                    provinces[i] = key;
                }
            }
            provinceMap.putAll(map);
        }
    }


    private String getLocation(Location location) {
        if (location == null) return "";
        StringBuilder sb = new StringBuilder();
        if (!StringUtil.isNullOrEmpty(location.getCountry())) {
            sb.append(location.getCountry()).append(" ");
        }
        if (!StringUtil.isNullOrEmpty(location.getProvince())) {
            if (!StringUtil.isNullOrEmpty(location.getCity())) {
                sb.append(location.getProvince()).append("·").append(location.getCity());
            }else {
                sb.append(location.getProvince());
            }
        }else {
            if (!StringUtil.isNullOrEmpty(location.getCity())) {
                sb.append(location.getCity()).append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * NumberPicker设置分割线颜色,取消点击输入
     * */
    private void setDefaultPicker(MyNumberPicker numberPicker) {
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(this.getResources().getColor(R.color.transparent)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }


    /**
     * 是否是直辖市
     */
    private boolean isMunicipality(String province) {
        if (StringUtil.isNullOrEmpty(province))
            return false;
        String[] municipality = getResources().getStringArray(R.array.municipality);
        if (municipality.length > 0) {
            for (int i = 0; i < municipality.length; i++) {
                if (province.startsWith(municipality[i]))
                    return true;
            }

        }
        return false;
    }

// TODO ————————————————————————————————————————————————————————————————————— 签名 —————————————————————————————————————————————————————————————————————————————————

    /**
     * 修改个性签名对话框
     */
    private void initSignatureWindow() {
        MyLog.i("initSignatureWindow start");
        View alter_signature_view = this.getLayoutInflater().inflate(R.layout.alter_signature, null);
        alter_signature_et = (EditText) alter_signature_view.findViewById(R.id.alter_signature);

        setTranHeader(alter_signature_view);

        alter_signature_cancel = (ImageView) alter_signature_view.findViewById(R.id.alter_signature_cancel);
        alter_signature_sure = (TextView) alter_signature_view.findViewById(R.id.alter_signature_sure);
        clear_signature = (ImageView) alter_signature_view.findViewById(R.id.clear_signature);
        remaining_number = (TextView) alter_signature_view.findViewById(R.id.remaining_number);
        alter_signature_cancel.setOnClickListener(this);
        alter_signature_sure.setOnClickListener(this);
        alter_signature_sure.setClickable(false);
        clear_signature.setOnClickListener(this);
        alter_signature_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!StringUtil.isNullOrEmpty(alter_signature_et.getText().toString())){
                    clear_signature.setVisibility(View.VISIBLE);
                }else {
                    clear_signature.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /**if (editable.toString() != null && !editable.toString().equals(signature_str)) {
                 alter_signature_sure.setClickable(true);
                 } else {
                 alter_signature_sure.setClickable(false);
                 }**/
                remaining_number.setText(editable.toString().length() + "/" + signature_max);
                alter_signature_sure.setClickable(true);
            }
        });
        alter_signature_popupWindow = new MyPopWindow(this, alter_signature_view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        MyLog.i("initSignatureWindow end");
    }

    private void alterSignature() {
        final String signature = StringUtil.filter(alter_signature_et.getText().toString());
        MyLog.i("new signature=[" + signature + "] is null: " + (signature == null));

        UserInfo us_sgn = new UserInfo();
        us_sgn.setSignature(signature);

        MineManager.getInstance().updateUserInfo(us_sgn, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("update signature OK:" + signature);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        signature_str = signature;
                        mTvSignature.setText(signature_str);
                        // 通知个人空间更改信息
                        EventBus.getDefault().post(new FollowStatusChange());
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("udpate signature NG:" + getString(R.string.no_network));
                mHandler.sendEmptyMessage(3);
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("update signature NG:[" + signature + "] is empty:" + (TextUtils.isEmpty(signature)));
                mHandler.sendEmptyMessage(5);
            }
        });

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    AppContext.showToastShort(R.string.invalid_nickname);
                    break;
                case 0:
                    refreshUserInfo();
                    break;
                // 拍照（arg1==1）or剪切头像(arg1==2)返回数据
                case 1:// upload avatar
                    if (msg.obj != null && !StringUtil.isNullOrEmpty(msg.obj.toString())) {
                        final String headimgpath = msg.obj.toString();
                        MyLog.i("update avatar in handler:" + headimgpath + ", arg1:" + msg.arg1);
                        final int arg = msg.arg1;

                        MineManager.getInstance().uploadAvatar(msg.obj.toString(), new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                if (obj != null) {

                                    UploadAvatarResult result = (UploadAvatarResult) obj;
                                    if (result != null && !StringUtil.isNullOrEmpty(result.getUrl())) {
                                        headimgurl_str = result.getUrl();
                                        MyLog.i("arg=" + arg + " upload avatar OK:" + headimgurl_str);
//                                    if(arg == 1) {
                                        deleteTmpFile();

                                        saveAvatarByUrl(headimgurl_str);

                                        if (result.getAvatar() != null) {
                                            //改变登陆注册逻辑后需要在更改头像后再调用接口更新头像
                                            MineManager.getInstance().updateUserAvatar(result.getAvatar()
                                                    , new IListener() {
                                                @Override
                                                public void onSuccess(Object obj) {
                                                    MyLog.i("update user avatar succeed!");
                                                    // 通知个人空间更改信息
                                                    EventBus.getDefault().post(new FollowStatusChange());
                                                }

                                                @Override
                                                public void onErr(Object obj) {

                                                }

                                                @Override
                                                public void onNoNetwork() {

                                                }
                                            });
                                        }
                                        //
                                        MyLog.i("saveAvatarByUrl:" + headimgurl_str);
//                                    }

                                    }
                                }
                            }

                            @Override
                            public void onNoNetwork() {
                                MyLog.i("upload avatar NG:" + getString(R.string.no_network));
                                deleteTmpFile();
                                mHandler.sendEmptyMessage(3);
                            }

                            @Override
                            public void onErr(Object object) {
                                MyLog.i("upload avatar NG:" + headimgpath);
                                deleteTmpFile();
                                mHandler.sendEmptyMessage(4);
                            }
                        });
                    }
                    break;
                // 获取用户信息成功
                case 2:
                    if (msg.obj != null) {
                        UserInfo us = (UserInfo) msg.obj;
                        // 头像
                        try {
                            if (us.getAvatar().getUri() != null) {
                                headimgurl_str = us.getAvatar().getUri();
                                if (headimgurl_str != null) {
                                    saveAvatarByUrl(headimgurl_str);
                                    MyLog.i("headimgurl=" + headimgurl_str);
                                    mHandler.post(runnable_avatar);
                                }
                            }
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                        // 用户名(昵称)
                        try {
                            username_str = StringUtil.nullToEmpty(us.getUsername());
                            mTvUsername.setText(StringUtil.nullToEmpty(us.getUsername()));
                            MinePreference.getInstance().setUsername(StringUtil.nullToEmpty(us.getUsername()));
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }

                        // 联系方式(手机号)
                        try {
                            String contact = StringUtil.nullToEmpty(us.getContact());
                            mTvContact.setText(contact);
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }

                        // 性别
                        try {
                            String sex = StringUtil.nullToEmpty(us.getSex());
                            sex_param_str = sex.equals("female") ? "2" : (sex.equals("male") ? "1" : "");
                            mTvGender.setText(StringUtil.getGender(PersonalInfoActivity.this, sex, true));
                            updateGenderIncon(sex);
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                        // 年代(生日)
                        try {
                            if (us.getBirthday() > 0) {
                                birthdayLong = us.getBirthday();
                                if (birthdayLong > 0) {
                                    birthday = StringUtil.formatDate(birthdayLong, DATE_FORMAT_STRING);
                                    if (birthday != null) {
                                        birthdayLong = dateFormat(birthday);
                                    }
                                    MyLog.i("birthday : " + birthday);
                                    mTvAge.setText(birthday);
                                }
                            }
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }

                        // 星座
                        try {
                            zodiac_i = us.getStars() - 1;
                            if (zodiac_i < 0 || zodiac_i > 12) {
//                                zodiac_i = 0;
                                mTvZodiac.setText("");
                            } else {
                                mTvZodiac.setText(AlterZodiacActivity.zodiac_name_arr[zodiac_i]);
                            }
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                        // 地址
                        try {
                            if (us.getLocation() != null) {
                                location = us.getLocation();
                                mTvLocation.setText(getLocation(location));
                            }
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                        // 签名
                        try {
                            String sgn = StringUtil.nullToEmpty(us.getSignature());
                            if (sgn.length() > signature_max) {
                                signature_str = sgn.substring(0, signature_max);
                            } else {
                                signature_str = sgn;
                            }
                            mTvSignature.setText(signature_str);
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }

                        // 注册时间
                        try {
                            MyLog.i("Register time :" + us.getCreatedAt());
                            mTvRegister.setText(StringUtil.formatDate(us.getCreatedAt(), DATE_FORMAT_STRING));
                        } catch (NullPointerException e) {
                            MyLog.e(e);
                        }
                    }
                    break;
                case 3:
                    AppContext.showToastShort(R.string.no_network);
                    break;
                case 4:
                    AppContext.showToastShort(R.string.upload_icon_fail);
                    break;
                case 5:
                    AppContext.showToastShort(R.string.connection_failed);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NO_SDCARD:
                AppContext.showToastShort(R.string.label_no_sdcard);
                break;
            case PHOTO_HRAPH:
                switch (resultCode) {
                    case Activity.RESULT_OK://照相完成点击确定
                        if (!AppTools.existsSDCARD()) {
                            MyLog.v("SD card is not avaiable/writeable right now.");
                            return;
                        }
                        cropAvatar(Uri.fromFile(new File(BitmapUtil.getTmpAvatarCameraPath())));
//                        Message msg = new Message();
//                        msg.what = 1;
//                        msg.arg1 = 1;
//                        msg.obj = BitmapUtil.getAvatarCropPath();
//                        mHandler.sendMessage(msg);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case PHOTO_RESULT:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = 2;
                        msg.obj = BitmapUtil.getAvatarCropPath();
                        mHandler.sendMessage(msg);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case CROP_CAMERA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        uploadAvatar();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case ZODIAC_RESULT:
                int tmp = ZODIAC_NONE;
                if (data != null)
                    tmp = data.getIntExtra(KEY_ZODIAC, ZODIAC_NONE);
                if (zodiac_i != tmp && tmp >= 0 && tmp < 12) {

                    UserInfo us_z = new UserInfo();
                    us_z.setStars(tmp + 1);
                    final int z = tmp;

                    MineManager.getInstance().updateUserInfo(us_z, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            MyLog.i("update stars OK:" + getString(AlterZodiacActivity.zodiac_name_arr[z]));
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    zodiac_i = z;
                                    mTvZodiac.setText(getString(AlterZodiacActivity.zodiac_name_arr[z]));
                                }
                            });
                        }

                        @Override
                        public void onNoNetwork() {
                            MyLog.i("udpate stars NG:" + getString(R.string.no_network));
                            mHandler.sendEmptyMessage(3);
                        }

                        @Override
                        public void onErr(Object object) {
                            MyLog.i("update stars NG:" + getString(AlterZodiacActivity.zodiac_name_arr[z]));
                            mHandler.sendEmptyMessage(5);
                        }
                    });
                }

                break;
            case CITY_RESULT:
                if (data != null && !StringUtil.isNullOrEmpty((data.getStringExtra(SelectCityActivity.KEY_CITY)))) {
                    UserInfo us = new UserInfo();
                    Location location = new Location();
                    location.setProvince(data.getStringExtra(SelectCityActivity.KEY_PROVINCE));
                    location.setCity(data.getStringExtra(SelectCityActivity.KEY_CITY));
                    location.setStreet(data.getStringExtra(SelectCityActivity.KEY_DISTRICT));
                    us.setLocation(location);
                    final String city_text = getLocation(location);

                    MineManager.getInstance().updateUserInfo(us, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            MyLog.i("udpate location OK:" + city_text);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    city_str = city_text;
                                    mTvLocation.setText(city_str);
                                }
                            });
                        }

                        @Override
                        public void onNoNetwork() {
                            MyLog.i("udpate location NG:" + getString(R.string.no_network));
                            mHandler.sendEmptyMessage(3);
                        }

                        @Override
                        public void onErr(Object object) {
                            MyLog.i("udpate location NG:" + city_text);
                            mHandler.sendEmptyMessage(5);
                        }
                    });
                }
                break;
            case CONTACT_RESULT:
                if (data != null && !StringUtil.isNullOrEmpty((data.getStringExtra(ModifyContactActivity.KEY_CONTACT)))) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            contact_str = data.getStringExtra(ModifyContactActivity.KEY_CONTACT);
                            mTvContact.setText(contact_str);
                        }
                    });

                }
                break;
        }
    }


    private void setTranHeader(View view) {
        View headerView = (View) view.findViewById(R.id.view_header);
        // 如果版本小于5.0
        if(!AppContext.getInstance().isMethodsCompat(Build.VERSION_CODES.LOLLIPOP)){
            headerView.setVisibility(View.VISIBLE);
        }else {
            headerView.setVisibility(View.GONE);
        }
    }


    //    , R.id.return_key_black  R.id.nickname_item,
    @Override
    @OnClick({R.id.avatar_item, R.id.contact_item, R.id.age_item, R.id.gender_item, R.id.zodiac_item, R.id.location_item, R.id.signature_item})
    public void onClick(View view) {
        MyLog.i("onClick start view.getId=" + view.getId());
        switch (view.getId()) {
            // avatar
            case R.id.avatar_item:
                //跳转照片拍摄
                makeCosplayAvatar();

//                alter_avatar_popupWindow.showButtom();
                break;
            case R.id.alter_avatar_album:
                alter_avatar_popupWindow.dismiss();
                cropAvatar(null);
//                selectMyCosplay();
                break;
            case R.id.alter_avatar_camera:
                alter_avatar_popupWindow.dismiss();
//                makeCosplayAvatar();
                doCamera();
                break;
            case R.id.alter_avatar_cancel:
                alter_avatar_popupWindow.dismiss();
                break;
            // username
            case R.id.username_item:
                alter_username_et.setText(username_str);
                alter_username_et.setSelection(username_str != null ? username_str.length() : 0);
                alter_username_popupWindow.showButtom();
                break;
            case R.id.alter_username_cancel:
                alter_username_popupWindow.dismiss();
                break;
            case R.id.clear_username:
                alter_username_et.setText("");
                break;
            case R.id.alter_username_sure:
//                alter_username_popupWindow.dismiss();
                checkUserName(alter_username_et.getText().toString().trim());
                break;
            // contact
            case R.id.contact_item:
                Intent intent = new Intent(PersonalInfoActivity.this, ModifyContactActivity.class);
                startActivityForResult(intent, CONTACT_RESULT);
                break;
            // age
            case R.id.age_item:
                dateChooseShow();
                break;
            case R.id.alter_age_cancel:
                dateChooseCancle();
                break;
            case R.id.alter_age_sure:
                dateChooseEnsure();
                break;
            // gender
            case R.id.gender_item:
                if (!StringUtil.isNullOrEmpty(sex_param_str)) {
                    MyLog.i("show" + sex_param_str);
                    selectGender();
                }
                alter_gender_popupWindow.showButtom();
                break;
            case R.id.alter_sex_male:
                selectMale(true);
                sex_param_str = StringUtil.nullToEmpty("1");
                updateSex();
                alter_gender_popupWindow.dismiss();
                break;
            case R.id.alter_sex_female:
                selectMale(false);
                sex_param_str = StringUtil.nullToEmpty("2");
                updateSex();
                alter_gender_popupWindow.dismiss();
                break;
            case R.id.alter_sex_cancel:
                alter_gender_popupWindow.dismiss();
                break;
            // zodiac
            case R.id.zodiac_item:
                Intent zodiac = new Intent(PersonalInfoActivity.this, AlterZodiacActivity.class);
                zodiac.putExtra(KEY_ZODIAC, zodiac_i);
                startActivityForResult(zodiac, ZODIAC_RESULT);
                break;

            // city
            case R.id.location_item:
                setDefaultDate();
                alter_location_popupWindow.showButtom();
//                Intent location = new Intent(PersonalInfoActivity.this, SelectCityActivity.class);
//                location.putExtra(SelectCityActivity.KEY_CITY, mTvLocation.getText().toString());
//                startActivityForResult(location, CITY_RESULT);
                break;
            case R.id.alter_location_cancel:
                alter_location_popupWindow.dismiss();
                break;
            case R.id.alter_location_sure:
                updateLocation();
                alter_location_popupWindow.dismiss();
                break;
            // signatrue
            case R.id.signature_item:
                alter_signature_et.setText(signature_str);
                int sign_len = signature_str != null ? signature_str.length() : 0;//getStrLen(signature_str);
                alter_signature_et.setSelection(sign_len);
                remaining_number.setText(sign_len + "/" + signature_max);
                alter_signature_popupWindow.showButtom();
                break;
            case R.id.alter_signature_cancel:
                alter_signature_popupWindow.dismiss();
                break;
            case R.id.clear_signature:
                alter_signature_et.setText("");
                remaining_number.setText("0/" + getResources().getInteger(R.integer.signature_max_len));
                break;
            case R.id.alter_signature_sure:
                alter_signature_popupWindow.dismiss();
                alterSignature();
                break;
            default:
                break;
        }
    }


    private void gotoBack() {
//        if(!FROM.equals(FROMMINE)) {
//            AppTools.toIntent(PersonalInfoActivity.this, MainActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            gotoBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onEvent(String avatarPath) {
        MyLog.i("received avatarPath:" + avatarPath);
        uploadAvatar(avatarPath);
    }

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("PersonalInfoActivity"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("PersonalInfoActivity"); //
        MobclickAgent.onPause(this);
    }
}

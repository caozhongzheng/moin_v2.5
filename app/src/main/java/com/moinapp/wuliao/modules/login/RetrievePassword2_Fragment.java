package com.moinapp.wuliao.modules.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.FragmentSkip;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.mine.model.UploadAvatarResult;
import com.moinapp.wuliao.ui.MyPopWindow;
import com.moinapp.wuliao.util.AppTools;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.Tools;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * 补充用户名
 * Created by moying on 15/9/11.
 */
public class RetrievePassword2_Fragment extends Fragment implements View.OnClickListener {

    private ILogger MyLog = LoggerFactory.getLogger("login");

    public static String KEY_USERNAME = "username";
    public static String KEY_GENDER = "gender";
    public static String KEY_AVATAR = "avatar";

    @InjectView(R.id.avatar_item)
    RelativeLayout mRlvAvatar;
    @InjectView(R.id.user_avatar)
    ImageView mIvAvatar;
    @InjectView(R.id.et_username)
    EditText mTvUsername;
    @InjectView(R.id.gender_group)
    RadioGroup mCgGender;
    @InjectView(R.id.gender_male)
    RadioButton mCbMale;
    @InjectView(R.id.gender_female)
    RadioButton mCbFemale;
    private MyPopWindow alter_avatar_popupWindow;

    private TextView alter_avatar_album, alter_avatar_camera, alter_avatar_cancel;

    String mAvatarUrl;
    String mGender;
    Activity mActivity;

    public static final int PHOTO_HRAPH = 1;// 拍照
    public static final int PHOTO_ZOOM = 2; // 缩放
    public static final int PHOTO_RESULT = 3;// 结果
    public static final int CROP_CAMERA = 6;// 截取拍照的头像

    private FragmentSkip callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_userinfo_short, container, false);
        rootView.findViewById(R.id.title_layout).setVisibility(View.GONE);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MyLog.i("onAttach activity=" + activity);
        mActivity = getActivity();
        MyLog.i("onAttach mActivity=" + mActivity);
        callback = (FragmentSkip) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();

        initAvatarWindow();
    }

    private void initData() {
        MineManager.getInstance().getUserInfo(ClientInfo.getUID(), new IListener() {
            @Override
            public void onSuccess(Object obj) {
                if (obj != null) {
                    final UserInfo us = (UserInfo) obj;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGender = StringUtil.nullToEmpty(us.getSex());
                            mCbMale.setChecked(false);
                            mCbFemale.setChecked(false);
                            if (mGender.equals("male")) {
                                mCbMale.setChecked(true);
                            } else if (mGender.equals("female")) {
                                mCbFemale.setChecked(true);
                            }

                            if (us.getAvatar() != null) {
                                mAvatarUrl = StringUtil.nullToEmpty(us.getAvatar().getUri());
                                if (!TextUtils.isEmpty(mAvatarUrl)) {
                                    ImageLoaderUtils.displayHttpImage(mAvatarUrl, mIvAvatar, null);
                                }
                            }
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

    private void initView() {
        mCgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == mCbMale.getId()) {
                    mGender = "male";
                } else if (checkedId == mCbFemale.getId()) {
                    mGender = "female";
                }
            }
        });
    }


    @Override
    @OnClick({R.id.btn_register, R.id.avatar_item, R.id.gender_male, R.id.gender_female})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (!Tools.isFastDoubleClick()) {
                    String names = mTvUsername.getText().toString().replaceAll(" ", "");
                    if (StringUtil.isNullOrEmpty(names)) {
                        mTvUsername.setError("请输入用户名");
                        return;
                    }
                    if (names.length() < 2) {
                        mTvUsername.setError("用户名长度为2至10个字");
                        return;
                    }
                    if (mGender.length() == 0) {
                        AppContext.showToastShort("请选择性别");
                        return;
                    }
                    MyLog.i("开始更新用户名和性别:");
                    checkUserName(mTvUsername.getText().toString().trim());
                }
                break;
            // avatar
            case R.id.avatar_item:
                alter_avatar_popupWindow.showButtom();
                break;
            case R.id.alter_avatar_album:
                alter_avatar_popupWindow.dismiss();
                cropAvatar(null);
                break;
            case R.id.alter_avatar_camera:
                alter_avatar_popupWindow.dismiss();
                doCamera();
                break;
            case R.id.alter_avatar_cancel:
                alter_avatar_popupWindow.dismiss();
                break;
        }
    }

    /**
     * 检查用户名唯一性,并更新用户名
     * 0 已存在，不能使用 1 可以使用
     *
     * @param username
     */
    private void checkUserName(final String username) {
        if (StringUtil.isNullOrEmpty(username)) {
            return;
        }
        LoginManager.getInstance().checkUserName(username, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                mTvUsername.setError(null);
                updateUsername(username);
            }

            @Override
            public void onErr(Object obj) {
                mTvUsername.setError(getString(R.string.invalid_username_tip));
            }

            @Override
            public void onNoNetwork() {
                AppContext.showToastShort(R.string.no_network);
            }
        });
    }

    private void updateUsername(final String username) {
        UserInfo us = new UserInfo();
        us.setUsername(username);
        us.setSex(mGender);
        MineManager.getInstance().updateUserInfo(us, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("username update OK: " + username);
                MinePreference.getInstance().setUsername(username);
                AppContext.showToastShort(String.format(getResources().getString(R.string.regist_userinfo_success), username));

                callback.skip(2, username);
            }

            @Override
            public void onErr(Object obj) {
                AppContext.showToastShort(String.format(getResources().getString(R.string.regist_userinfo_failed), username));
            }

            @Override
            public void onNoNetwork() {
                AppContext.showToastShort(R.string.no_network);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_HRAPH:
                switch (resultCode) {
                    case Activity.RESULT_OK://照相完成点击确定
                        if (!AppTools.existsSDCARD()) {
                            MyLog.v("SD card is not avaiable/writeable right now.");
                            return;
                        }
                        cropAvatar(Uri.fromFile(new File(BitmapUtil.getTmpAvatarCameraPath())));
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case PHOTO_RESULT:
                switch (resultCode) {
                    case Activity.RESULT_OK:
//                        Message msg = new Message();
//                        msg.what = 1;
//                        msg.arg1 = 2;
//                        msg.obj = BitmapUtil.getAvatarCropPath();
//                        mHandler.sendMessage(msg);
                        uploadAvatar();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
            case CROP_CAMERA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
//                        Message message = new Message();
//                        message.what = 1;
//                        message.arg1 = 1;
//                        message.obj = BitmapUtil.getAvatarCropPath();
//                        mHandler.sendMessage(message);
                        uploadAvatar();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    private void uploadAvatar() {
        final String headimgpath = BitmapUtil.getAvatarCropPath();
        MyLog.i("update avatar in handler:" + headimgpath);

        MineManager.getInstance().uploadAvatar(headimgpath, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                UploadAvatarResult result = (UploadAvatarResult) obj;
                if (result != null && !StringUtil.isNullOrEmpty(result.getUrl())) {
                    mAvatarUrl = result.getUrl();
                    MyLog.i(" upload avatar OK:" + mAvatarUrl);
                    deleteTmpFile();

                    saveAvatarByUrl(mAvatarUrl);
                    MyLog.i("saveAvatarByUrl:" + mAvatarUrl);
                }
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("upload avatar NG:" + getString(R.string.no_network));
                deleteTmpFile();
                AppContext.showToastShort(R.string.no_network);
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("upload avatar NG:" + headimgpath);
                deleteTmpFile();
                AppContext.showToastShort(R.string.upload_icon_fail);
            }
        });
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
                BitmapUtil.saveUserAvatar(mActivity, bitmap);
                mActivity.runOnUiThread(runnable_avatar);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    Runnable runnable_avatar = new Runnable() {
        @Override
        public void run() {
            MyLog.i("runnable_avatar: " + mAvatarUrl);
            ImageLoaderUtils.displayHttpImage(mAvatarUrl, mIvAvatar, null);
        }
    };

    /**
     * 修改头像
     */
    public void initAvatarWindow() {
        MyLog.i("initAvatarWindow start:mActivity=" + mActivity);
        MyLog.i("initAvatarWindow start:context=" + mActivity.getBaseContext());
        LayoutInflater inflater = LayoutInflater.from(mActivity.getBaseContext());
        MyLog.i("initAvatarWindow start:inflater=" + inflater);
        View popupWindow_view = inflater.inflate(R.layout.alter_avatar, null);
        popupWindow_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        alter_avatar_album = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_album);
        alter_avatar_camera = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_camera);
        alter_avatar_cancel = (TextView) popupWindow_view.findViewById(R.id.alter_avatar_cancel);
        alter_avatar_album.setOnClickListener(this);
        alter_avatar_camera.setOnClickListener(this);
        alter_avatar_cancel.setOnClickListener(this);
        alter_avatar_popupWindow = new MyPopWindow(mActivity, popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        MyLog.i("initAvatarWindow end");
    }

    // 拍照
    private void doCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(BitmapUtil.getTmpAvatarCameraPath())));
        startActivityForResult(intent, PHOTO_HRAPH);
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.RETRIEVE_PASSWORD2_FRAGMENT); //统计页面，
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.RETRIEVE_PASSWORD2_FRAGMENT);
    }
}

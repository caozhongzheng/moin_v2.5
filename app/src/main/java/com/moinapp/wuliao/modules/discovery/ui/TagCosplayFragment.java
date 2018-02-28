package com.moinapp.wuliao.modules.discovery.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.BeseHaveHeaderListFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.DiscoveryApi;
import com.moinapp.wuliao.modules.discovery.adapter.TagCosplayAdaptor;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.TagCosplayList;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.ui.StickerDetailActivity;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.ExpandableTextView;
import com.moinapp.wuliao.ui.FlowLayout;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.moinapp.wuliao.widget.AvatarView;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 话题详情界面(old)
 * 搜索标签的图片页面
 * 3.2.5修改为瀑布流形式
 *
 * @author liujiancheng
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TagCosplayFragment extends BeseHaveHeaderListFragment<CosplayInfo, TagPop> {
    private static final ILogger MyLog = LoggerFactory.getLogger(TagCosplayFragment.class.getSimpleName());

    private static final String CACHE_KEY_PREFIX = "tag_cosplay_list";
    private static final String CACHE_KEY_PREFI_HEAD = "tagpop_head_list";

    private final static int MAX_USER_COUNT = 7;
    private int mAvatarSize = (int)(TDevice.getScreenWidth()/10);
    private int mIsIdol;//是否已经关注此标签的标记
    private String mTag;
    private String mType;
    private String mTpId;
    private TagPop mTagPop;

//    @InjectView(R.id.title_left_area)
//    public LinearLayout mLeftLayout;
//    @InjectView(R.id.title_middle)
//    public TextView mTitle;
//    @InjectView(R.id.btn_follow)
//    public TextView mFollow;
//    @InjectView(R.id.title_right_area)
//    public LinearLayout lyFollow;
//    @InjectView(R.id.quick)
//    FloatingActionButton mAddBt;
    @InjectView(R.id.title_layout)
    CommonTitleBar mCommonTitleBar;

    private LinearLayout mLyTagDetail;
    private ImageView mCover;
    private TextView mTagName;
    private TextView mPicNum;
    private TextView mLikeNum;
    private TextView mCommentNum;
    FlowLayout mFlTags;
    private TextView mJoin;
    private ExpandableTextView mIntro;
    private ImageView mMore;
    private LinearLayout mAvatar0;
    private LinearLayout mAvatar1;
    private LinearLayout mAvatar2;
    private LinearLayout mAvatar3;
    private LinearLayout mAvatar4;
    private LinearLayout mAvatar5;
    private LinearLayout mAvatar6;

    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTag = args.getString(Constants.BUNDLE_KEY_TAG);
            mType = args.getString(Constants.BUNDLE_KEY_TYPE);
            mTpId = args.getString(Constants.BUNDLE_KEY_ID);
        }

        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_USER_CHANGE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestData(true);
        }
    };
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mListView.setDivider(null);
        mListView.setDividerHeight(0);
//        mAddBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mTagPop != null && mTagPop.getSticker() != null
//                        && (mTagPop.getSticker().isDownload() == 1) ) {
//                    StickPreference.getInstance().setDefaultUseSticker(mTagPop.getSticker().getStickerPackageId());
//                }
//                CameraManager.getInst().openCamera(getActivity(), null);
//                MobclickAgent.onEvent(getApplicationContext(), "cosplay_photo");
//            }
//        });

        StringBuffer stringBuffer = new StringBuffer();

        String name = null;
        if (!TextUtils.isEmpty(mTag)) {
            if (mTag.length() > getResources().getInteger(R.integer.tag_max_len) - 4) {
                name = mTag.substring(0, getResources().getInteger(R.integer.tag_max_len) - 6) + "...";
            } else {
                name = mTag;
            }
        }

        if (mType.equalsIgnoreCase("IP")) {
            stringBuffer.append((name.startsWith("《") ? "" : "《") + name + (name.endsWith("》") ? "" : "》"));
        } else if (mType.equalsIgnoreCase("OP")) {
            stringBuffer.append("#").append(name).append("#");
        } else {
            stringBuffer.append(name);
        }

        mCommonTitleBar.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        if (!mType.equalsIgnoreCase("TP")) {
            mCommonTitleBar.setTitleTxt(stringBuffer.toString());
        }else {
            mCommonTitleBar.setTitleTxt("话题");
        }
        mCommonTitleBar.hideRightBtn();
//        if (mType.equalsIgnoreCase("TP")) {
//            mCommonTitleBar.hideRightBtn();
//        } else {
//            mCommonTitleBar.setRightBtnOnclickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //未登录时提示登陆
//                    if (!AppContext.getInstance().isLogin()) {
//                        UIHelper.showLoginActivity(getActivity());
//                        return;
//                    }
//                    if (Tools.isFastDoubleClick()) {
//                        return;
//                    }
//                    if (mIsIdol == 0) {
//                        HashMap<String, String> map = new HashMap<String, String>();
//                        map.put(UmengConstants.ITEM_ID, mTag);
//                        map.put(UmengConstants.FROM, "标签的图片页面");
//                        MobclickAgent.onEvent(getActivity(), UmengConstants.TAG_FOLLOW, map);
//                        DiscoveryManager.getInstance().followTag(mTag, mType, 1, new IListener() {
//                            @Override
//                            public void onSuccess(Object obj) {
//                                mIsIdol = 1;
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        setFollowUserStatus(mIsIdol);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onErr(Object obj) {
//
//                            }
//
//                            @Override
//                            public void onNoNetwork() {
//
//                            }
//                        });
//                    }
//                }
//            });
//        }

        requestData(true);
    }

    @Override
    protected void requestDetailData(boolean isRefresh) {
    }

    @Override
    protected View initHeaderView() {
        if (!("TP").equalsIgnoreCase(mType)) return null;

        View headerView = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_tagpop_header, null);

        mLyTagDetail = (LinearLayout)headerView.findViewById(R.id.tagpop_head);
        mCover = (ImageView)headerView.findViewById(R.id.tag_cover);
        mTagName = (TextView)headerView.findViewById(R.id.tag_name);
        mPicNum = (TextView)headerView.findViewById(R.id.iv_tag_picnum);
        mLikeNum = (TextView)headerView.findViewById(R.id.tv_tag_like_num);
        mCommentNum = (TextView)headerView.findViewById(R.id.tv_tag_comment_num);
//        mFlTags = (FlowLayout)headerView.findViewById(R.id.fl_tag_container);
        mJoin = (TextView)headerView.findViewById(R.id.btn_join);
        mIntro = (ExpandableTextView)headerView.findViewById(R.id.tv_introduce);
        mMore = (ImageView)headerView.findViewById(R.id.iv_more_user);
        mAvatar0 = (LinearLayout)headerView.findViewById(R.id.avatar0);
        mAvatar1 = (LinearLayout)headerView.findViewById(R.id.avatar1);
        mAvatar2 = (LinearLayout)headerView.findViewById(R.id.avatar2);
        mAvatar3 = (LinearLayout)headerView.findViewById(R.id.avatar3);
        mAvatar4 = (LinearLayout)headerView.findViewById(R.id.avatar4);
        mAvatar5 = (LinearLayout)headerView.findViewById(R.id.avatar5);
        mAvatar6 = (LinearLayout)headerView.findViewById(R.id.avatar6);

        return headerView;
    }

    @Override
    protected String getDetailCacheKey() {
        return CACHE_KEY_PREFI_HEAD + "_" + mTag;
    }

    @Override
    protected void executeOnLoadDetailSuccess(TagPop detailBean) {
        if (detailBean != null && mType.equalsIgnoreCase("TP")) {
            setHeadView(detailBean);
        }
    }

    @Override
    protected TagPop getDetailBean(ByteArrayInputStream is) {
        return null;
    }

    private void setHeadView(TagPop tagPop) {
        if (tagPop == null) return;

        // 保存头数据(detailbean)到缓存
        saveCache(tagPop);

        mTagName.setText(tagPop.getName());
        mPicNum.setText(StringUtil.humanNumber(getActivity(), tagPop.getCosplayNum()));
        mLikeNum.setText(StringUtil.humanNumber(getActivity(), tagPop.getLikeNum()));
        //TODO
        mCommentNum.setText(StringUtil.humanNumber(getActivity(), tagPop.getCommentNum()));
        mIntro.setText(tagPop.getDesc());
        ImageLoaderUtils.displayHttpImage(tagPop.getIcon().getUri(), mCover, null, true, null);

        //显示包含的标签
//        setTagInfo(tagPop);

        //显示参与的user
        setUsers(tagPop);

        //点击参与
        setJoinClick(tagPop);
    }

    private void setTagInfo(TagPop tagPop) {
        mFlTags.removeAllViews();
        FlowLayout.LayoutParams tagParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tagParams.setMargins(0, 0, 5, 5);
        if (tagPop.getTags() != null && !tagPop.getTags().isEmpty()) {
            for (TagInfo tag : tagPop.getTags()) {
                if (tag == null || TextUtils.isEmpty(tag.getType())) continue;
                TextView text = new TextView(getActivity());
                text.setTextSize(13);
                if (tag.getType().equalsIgnoreCase("IP")) {
                    text.setText((tag.getName().startsWith("《") ? "" : "《") + tag.getName() + (tag.getName().endsWith("》") ? "" : "》"));
                } else if (tag.getType().equalsIgnoreCase("OP")) {
                    text.setText("#" + tag.getName());
                } else {
                    text.setText(tag.getName());
                }
                text.setGravity(Gravity.CENTER);
                text.setSingleLine();
                mFlTags.addView(text, tagParams);
            }
        }
    }

    private void setUsers(TagPop tagPop) {
        if (tagPop.getUsers() != null) {
            int userCount = tagPop.getUsers().size();
            MyLog.i("userCount = " + userCount);
            if (userCount > 0) {
                int index = 0;
                for (int i = 0; i < userCount; i ++) {
                    if (index >= MAX_USER_COUNT) break;
                    UserInfo user = tagPop.getUsers().get(i);
                    if (user == null || user.getAvatar() == null
                            || TextUtils.isEmpty(user.getAvatar().getUri())) {
                        MyLog.i("user is empty = " + i);
                        continue;
                    }

                    fillAvatar(index, user);
                    index++;
                }
                if (index < MAX_USER_COUNT) {
                    mMore.setVisibility(View.GONE);
                } else {
                    mMore.setVisibility(View.VISIBLE);
                    mMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UIHelper.showUserList(getActivity(),tagPop.getTagPopId(),1);
                        }
                    });
                }
            }
        } else {
            mMore.setVisibility(View.GONE);
        }
    }

    private void setJoinClick(TagPop tagPop) {
        mJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(UmengConstants.ITEM_ID, tagPop.getTagPopId() + "");
                map.put(UmengConstants.FROM,  "标签的图片页面");
                MobclickAgent.onEvent(getActivity(), UmengConstants.TOPIC_JOIN, map);
                //未登陆提示登陆
                if (!AppContext.getInstance().isLogin()) {
                    UIHelper.showLoginActivity(getActivity());
                    return;
                }
                if (Tools.isFastDoubleClick()) {
                    return;
                }

                if (mTagPop.getSticker() == null ||
                        TextUtils.isEmpty(mTagPop.getSticker().getStickerPackageId())) {
                    AppContext.toast(getActivity(), "贴纸包出错了...");
                    return;
                }

                int download = mTagPop.getSticker().isDownload();
                // 未下载并且不是推荐贴纸包时进详情去下载
                if (download == 0 && !getString(R.string.intime_sticker_pacakge_id).equals(mTagPop.getSticker().getStickerPackageId())) {
                    // 进入贴纸包详情
                    Intent intent = new Intent(BaseApplication.context(), StickerDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(StickerDetailActivity.STICKER_ID, mTagPop.getSticker().getStickerPackageId());
                    intent.putExtra(StickerDetailActivity.STICKER_FROM_JOIN, 1);
                    getActivity().startActivity(intent);
                } else {
                    StickPreference.getInstance().setDefaultUseSticker(mTagPop.getSticker().getStickerPackageId());
                    // 进入拍照页面
                    CameraManager.getInst().openCamera(getActivity(), null);
                }
            }
        });
    }

    private void fillAvatar(int i, UserInfo user) {
        MyLog.i("fillAvatar = " + i);
        AvatarView avatarView;
        switch (i) {
            case 0:
                MyLog.i("case = 0");
                mAvatar0.setVisibility(View.VISIBLE);
                avatarView = (AvatarView)mAvatar0.findViewById(R.id.avatar);
                setAvatarView(avatarView, user);
                break;
            case 1:
                MyLog.i("case = 1");
                mAvatar1.setVisibility(View.VISIBLE);
                avatarView = (AvatarView)mAvatar1.findViewById(R.id.avatar);
                setAvatarView(avatarView, user);
                break;
            case 2:
                MyLog.i("case = 2");
                mAvatar2.setVisibility(View.VISIBLE);
                avatarView = (AvatarView)mAvatar2.findViewById(R.id.avatar);
                setAvatarView(avatarView, user);
                break;
            case 3:
                MyLog.i("case = 3");
                mAvatar3.setVisibility(View.VISIBLE);
                avatarView = (AvatarView)mAvatar3.findViewById(R.id.avatar);
                setAvatarView(avatarView, user);
                break;
            case 4:
                MyLog.i("case = 4");
                mAvatar4.setVisibility(View.VISIBLE);
                avatarView = (AvatarView)mAvatar4.findViewById(R.id.avatar);
                setAvatarView(avatarView, user);
                break;
            case 5:
                MyLog.i("case = 5");
                mAvatar5.setVisibility(View.VISIBLE);
                avatarView = (AvatarView)mAvatar5.findViewById(R.id.avatar);
                setAvatarView(avatarView, user);
                break;
            case 6:
                MyLog.i("case = 6");
                mAvatar6.setVisibility(View.VISIBLE);
                avatarView = (AvatarView)mAvatar6.findViewById(R.id.avatar);
                setAvatarView(avatarView, user);
                break;
        }
    }

    private void setAvatarView(AvatarView avatarView, UserInfo user) {
        ViewGroup.LayoutParams para = avatarView.getLayoutParams();
        para.width = mAvatarSize;
        para.height = mAvatarSize;
        avatarView.setLayoutParams(para);
        avatarView.setUserInfo(user.getUId(), user.getUsername());
        avatarView.setAvatarUrl(user.getAvatar().getUri());

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(user.getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(getActivity(), 0);
                } else {
                    UIHelper.showUserCenter(getActivity(), user.getUId());
                }
            }
        });
    }
//    private void setFollowUserStatus(int isIdol) {
//        if (isIdol == 1) {
//            mCommonTitleBar.setRightTxtBtn("已关注");
//        } else  {
//            mCommonTitleBar.setRightTxtBtn("+ 关注");
//        }
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tag_cosplay;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.TAG_COSPLOY_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.TAG_COSPLOY_FRAGMENT);
    }
    @Override
    protected TagCosplayAdaptor getListAdapter() {
        return new TagCosplayAdaptor();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mTag;
    }

    @Override
    protected TagCosplayList parseList(InputStream is) throws Exception {
        TagCosplayList result = XmlUtils.JsontoBean(TagCosplayList.class, is);
        if (result == null) {
            Log.i("ljc", "result = null");
            return null;
        }
        Log.i("ljc", "result.size = " + result.getList().size());

        mIsIdol = result.getIsIdol();
        mTagPop = result.getTagPop();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mType.equalsIgnoreCase("TP")) {
//                    setFollowUserStatus(mIsIdol);
                } else {
                    setHeadView(mTagPop);
                }
            }
        });

        return result;
    }

    @Override
    protected TagCosplayList readList(Serializable seri) {
        return ((TagCosplayList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((CosplayInfo) enity).getUcid().equalsIgnoreCase(((CosplayInfo) data.get(i)).getUcid())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
        //因为head和list的数据是从同一个接口获取的,所有无网络时无法由父类发起获取缓存的动作,所以
        //暂时先获取缓存的数据
        readDetailCacheData(getDetailCacheKey());

        if (refresh) {
            DiscoveryApi.getTagDetail(mTag, mType, 1, null, mHandler);
//            DiscoveryApi.getTopicDetail(2, mTpId, null, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getUcid() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        DiscoveryApi.getTagDetail(mTag, mType, 1, lastid, mHandler);
//        DiscoveryApi.getTopicDetail(2, mTpId, lastid, mHandler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }
}

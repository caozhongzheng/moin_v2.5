package com.moinapp.wuliao.modules.discovery.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.RecyclingPagerAdapter;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.DeleteOnClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.NodeInfo;
import com.moinapp.wuliao.modules.discovery.result.GetCosplayFamilyResult;
import com.moinapp.wuliao.ui.ShareDialog;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.HackyViewPager;
import com.moinapp.wuliao.widget.LikeLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 图片窗口模式[大咖秀改图列表界面]
 *
 * @author moying
 */
public class CosplayEvolutionActivity extends BaseActivity implements
        OnPageChangeListener {
    private static final ILogger MyLog = LoggerFactory.getLogger(CosplayEvolutionActivity.class.getSimpleName());

    public static final String DEFAULT_DELETE_IMAGE = "http://prdimg.mo-image.com/image/sys/default-del-cosplay.png";
    public static final String KEY_FROM_ROOT = "key_from_root";
    public static final String KEY_FROM_PREVIOUS = "key_from_previous";
    public static final String KEY_FROM_CHILDREN = "key_from_children";
    public static final String KEY_CHILDREN_INDEX = "key_children_index";
    private HackyViewPager mViewPager;
    private SamplePagerAdapter mAdapter;
    private EmptyLayout mErrLayout;
    private RelativeLayout mRlOption, mHeader, mRlAuthor;
    private LinearLayout mLlInfo;
    private LikeLayout mLike;
    private AvatarView mAvatar;
    private TextView mTvImgIndex,  mTvContent, mUserName, mCreateTime;
    private TextView mTvReadNum,  mTvCommentNum, mTvChildrenNum;
    private ImageView mShare;

    private int mCurrentPostion = 0;
    private String mUCID;
    private ArrayList<NodeInfo> mNodeList = new ArrayList<>();
    private int selfIndex = -1;
    private boolean mDisplay = true;

    public static void showCosplayEvolution(Context context, String ucid) {
        showCosplayEvolution(context, ucid, null);
    }

    public static void showCosplayEvolution(Context context, String ucid, Bundle args) {
        Intent intent = new Intent(context, CosplayEvolutionActivity.class);
        if (args != null) {
            intent.putExtras(args);
        }
        intent.putExtra(DiscoveryConstants.UCID, ucid);
        context.startActivity(intent);
    }

    @Override
    protected boolean hasActionBar() {
        getSupportActionBar().hide();
        return true;
    }


    @Override
    protected void onBeforeSetContentLayout() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cosplay_evolution;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        findViewById(R.id.back).setOnClickListener(v -> {
            finish();
        });
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

        mErrLayout = (EmptyLayout) findViewById(R.id.error_layout);
        mUCID = getIntent().getStringExtra(DiscoveryConstants.UCID);
        if (StringUtil.isNullOrEmpty(mUCID)) {
            mErrLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
        } else {
            DiscoveryManager.getInstance().getCosplayFamily(mUCID, new IListener2() {
                @Override
                public void onStart() {
                    super.onStart();
                    mErrLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }

                @Override
                public void onSuccess(Object obj) {
                    mErrLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    getNodes((GetCosplayFamilyResult) obj);

                    mAdapter = new SamplePagerAdapter(mNodeList);
                    mViewPager.setAdapter(mAdapter);
//                    mViewPager.setOffscreenPageLimit(mNodeList.size());
//                    mViewPager.setPageMargin(40);

                    mViewPager.setCurrentItem(selfIndex < 0 ? 0 : selfIndex);

                    updateTitle(selfIndex);
                    updateContentInfo(selfIndex);
                    onPageSelected(selfIndex < 0 ? 0 : selfIndex);
                }

                @Override
                public void onErr(Object obj) {
                    mErrLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onNoNetwork() {
                    mErrLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            });
        }

        mViewPager.setOnPageChangeListener(this);

        mHeader = (RelativeLayout) findViewById(R.id.header);
        mHeader.setOnClickListener(this);
        mTvImgIndex = (TextView) findViewById(R.id.index);
        mShare = (ImageView) findViewById(R.id.more);
        mShare.setOnClickListener(this);

        mLlInfo = (LinearLayout) findViewById(R.id.ll_info);
        mRlOption = (RelativeLayout) findViewById(R.id.rl_option);
        mRlOption.setOnClickListener(this);

        mRlAuthor = (RelativeLayout) findViewById(R.id.authore_layout);
        mRlAuthor.setOnClickListener(this);
        mAvatar = (AvatarView) findViewById(R.id.iv_author_face);
        mUserName = (TextView) findViewById(R.id.tv_author_name);
        mCreateTime = (TextView) findViewById(R.id.tv_create_time);

        mTvContent = (TextView) findViewById(R.id.tv_cosplay_content);
        mTvContent.setOnClickListener(this);
        mTvReadNum = (TextView) findViewById(R.id.iv_discovery_viewnum);
        mTvCommentNum = (TextView) findViewById(R.id.tv_discovery_commentnum);
        mTvChildrenNum = (TextView) findViewById(R.id.iv_discovery_forwardnum);

        mLike = (LikeLayout) findViewById(R.id.like_layout);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_option:
                gotoCosplayDetail(mNodeList.get(mCurrentPostion));
                break;
            case R.id.tv_cosplay_content:
                gotoCosplayDetail(mNodeList.get(mCurrentPostion));
                break;
            case R.id.more:
                handleShare();
                break;
            default:
                break;
        }
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    private void handleShare() {
        final ShareDialog dialog = new ShareDialog(this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        String imageUrl = ImageLoaderUtils.buildNewUrl(mNodeList.get(mCurrentPostion).getPicture().getUri(),
                ImageLoaderUtils.getFixedWidthImageSize());
        dialog.setShareInfo("", null, imageUrl, null);
        dialog.setCosplayUcid(mNodeList.get(mCurrentPostion).getUcid());

        if (mNodeList.get(mCurrentPostion).getAuthor().getUId().equalsIgnoreCase(ClientInfo.getUID())) {
            dialog.setDeleteEnable();
            dialog.setDeleteOnClick(new DeleteOnClickListener() {
                @Override
                public void onClick(Object object) {
                    DiscoveryManager.getInstance().deleteCosplay(mNodeList.get(mCurrentPostion).getUcid(), new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            CosplayEvolutionActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyLog.i("evo: delete succeed!");
                                    mNodeList.get(mCurrentPostion).setStatus(0);
                                    mNodeList.get(mCurrentPostion).getPicture().setUri(DEFAULT_DELETE_IMAGE);
                                    mViewPager.setAdapter(mAdapter);
                                    mViewPager.setCurrentItem(mCurrentPostion);
                                    updateContentInfo(mCurrentPostion);
                                }
                            });
                        }

                        @Override
                        public void onErr(Object obj) {

                        }

                        @Override
                        public void onNoNetwork() {

                        }
                    });
                }
            });
        }

        dialog.show();
    }

    private void setAuthorInfo(UserInfo author) {
        if (author != null) {
            mAvatar.setUserInfo(author.getUId(), author.getUsername());
            if (author.getAvatar() != null) {
                mAvatar.setAvatarUrl(author.getAvatar().getUri());
            } else {
                mAvatar.setAvatarUrl(null);
            }
            mUserName.setText(author.getUsername());
            mCreateTime.setText(StringUtil.humanDate(mNodeList.get(mCurrentPostion).getCreatedAt()));
            final UserInfo finalAuthor = author;

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 进入用户中心
                    MyLog.i("进入用户中心 " + finalAuthor.getUsername());
                    if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(finalAuthor.getUId())) {
                        // 登录用户点击自己头像
                        UIHelper.showMine(CosplayEvolutionActivity.this, 0);
                    } else {
                        UIHelper.showUserCenter(CosplayEvolutionActivity.this, finalAuthor.getUId());
                    }
                }
            };
            mRlAuthor.setOnClickListener(listener);
            mAvatar.setOnClickListener(listener);
        }
    }

    private void getNodes(GetCosplayFamilyResult result) {
        if (result == null) {
            mNodeList.clear();
        } else {
            mNodeList.clear();
//            if (result.hasRoot()) {
//                mNodeList.add(result.getRoot());
//                selfIndex = 0;
//            }
//            if (result.hasPrevious()) {
//                mNodeList.add(result.getPrevious());
//                selfIndex++;
//            }
//            if (result.hasSelf()) {
//                mNodeList.add(result.getSelf());
//                selfIndex++;
//            }
//            if (result.hasChildren()) {
//                mNodeList.addAll(result.getChildren());
//            }
//
//            if (getIntent().getExtras() != null) {
//                String from = getIntent().getExtras().getString(DiscoveryConstants.FROM);
//                if (StringUtil.isNullOrEmpty(from)) {
//                    return;
//                }
//                if (KEY_FROM_ROOT.equals(from)) {
//                    selfIndex = 0;
//                } else if (KEY_FROM_PREVIOUS.equals(from)) {
//                    selfIndex = 1;
//                } else if (KEY_FROM_CHILDREN.equals(from)) {
//                    int childIndex = getIntent().getExtras().getInt(KEY_CHILDREN_INDEX, 1);
//                    selfIndex += childIndex;
//                }
//            }

            if (result.getCosplayList() != null && result.getCosplayList().size() > 0) {
                mNodeList.addAll(result.getCosplayList());
                for (int i = 0; i < mNodeList.size(); i++) {
                    if (mUCID.equalsIgnoreCase(mNodeList.get(i).getUcid())) {
                        selfIndex = i;
                        break;
                    }
                }
            }
        }
    }

    private void updateTitle(int index) {
        if (index < 0) {
            index = 0;
        }
        if (mTvImgIndex != null) {
            mTvImgIndex.setText((index + 1) + "/"
                    + mNodeList.size());
        }
    }

    private void updateContentInfo(int index) {
        if (mDisplay) {
            mHeader.setVisibility(View.VISIBLE);
            if (isNormal(mNodeList.get(index))) {
                mRlOption.setVisibility(View.VISIBLE);
                mLlInfo.setVisibility(View.VISIBLE);
                mTvContent.setVisibility(View.VISIBLE);
                mRlAuthor.setVisibility(View.VISIBLE);
                setAuthorInfo(mNodeList.get(index).getAuthor());
                mLike.setVisibility(View.VISIBLE);
                CosplayInfo tmp = new CosplayInfo();
                tmp.setUcid(mNodeList.get(index).getUcid());
                tmp.setLikeNum(mNodeList.get(index).getLikeNum());
                mLike.setContent(tmp);
                if (StringUtil.isNullOrEmpty(mNodeList.get(index).getContent())) {
                    mTvContent.setVisibility(View.GONE);
                } else {
                    mTvContent.setVisibility(View.VISIBLE);
                    mTvContent.setText(mNodeList.get(index).getContent());
                }
                mTvReadNum.setText(String.valueOf(mNodeList.get(index).getReadNum()));
                mTvCommentNum.setText(String.valueOf(mNodeList.get(index).getCommentNum()));
                mTvChildrenNum.setText(String.valueOf(mNodeList.get(index).getChildrenNum()));
                mShare.setVisibility(View.VISIBLE);
            } else {
                mRlOption.setVisibility(View.INVISIBLE);
                mLlInfo.setVisibility(View.INVISIBLE);
                mTvContent.setVisibility(View.INVISIBLE);
                mRlAuthor.setVisibility(View.INVISIBLE);
                mLike.setVisibility(View.INVISIBLE);
                mShare.setVisibility(View.INVISIBLE);
            }
        } else {
            mHeader.setVisibility(View.INVISIBLE);
            mLike.setVisibility(View.INVISIBLE);
            mTvContent.setVisibility(View.INVISIBLE);
            mRlAuthor.setVisibility(View.INVISIBLE);
            mRlOption.setVisibility(View.INVISIBLE);
            mLlInfo.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isNormal(NodeInfo nodeInfo) {
        return nodeInfo.getStatus() != 0;
    }

    private boolean isNetOKetc() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return false;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(CosplayEvolutionActivity.this);
            return false;
        }
        return true;
    }

    /**
     * 复制链接
     */
    private void copyUrl() {
        String content = null;
        if (mAdapter != null && mAdapter.getCount() > 0) {
            content = mAdapter.getItem(mCurrentPostion).getPicture().getUri();
            TDevice.copyTextToBoard(content);
            AppContext.showToastShort("已复制到剪贴板");
        }
    }


    private String getFileName(String imgUrl) {
        int index = imgUrl.lastIndexOf('/') + 1;
        if (index == -1) {
            return System.currentTimeMillis() + ".jpeg";
        }
        return imgUrl.substring(index);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int idx) {
        mCurrentPostion = idx;
        if (mNodeList != null && mNodeList.size() > 0) {
            updateTitle(idx);
            updateContentInfo(idx);
        }
    }

    class SamplePagerAdapter extends RecyclingPagerAdapter {

        private ArrayList<NodeInfo> nodes = new ArrayList<>();

        SamplePagerAdapter(ArrayList<NodeInfo> nodes) {
            this.nodes = nodes;
        }

        public NodeInfo getItem(int position) {
            return nodes.get(position);
        }

        @Override
        public int getCount() {
            return nodes.size();
        }

        @Override
        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.cosplay_evolution_item, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            MyLog.i("evo: getView..position = " + position + "pic="+mNodeList.get(position).getPicture().getUri());
            vh.image.enable();
            if (mNodeList.get(position).getPicture() != null) {
                ImageLoaderUtils.displayHttpImage(mNodeList.get(position).getPicture().getUri(), vh.image, null);
            }
            vh.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDisplay = !mDisplay;
                    updateContentInfo(position);
                }
            });
            return convertView;
        }
    }

    private void gotoCosplayDetail(NodeInfo nodeInfo) {
        if (nodeInfo == null) return;
        //如果图片已经删除,不能点击进入详情
        if (!isNormal(nodeInfo)) return;

        // TODO 不是结束,而是回到图片详情列表,传当前page的ucid
        MyLog.i("图片详情列表 UCID= " + nodeInfo.getUcid());
        CosplayInfo tmp = new CosplayInfo();
        tmp.setUcid(nodeInfo.getUcid());
        tmp.setPicture(nodeInfo.getPicture());
        UIHelper.showDiscoveryCosplayDetail(CosplayEvolutionActivity.this, tmp, nodeInfo.getUcid(),
                CosplayEvolutionActivity.class.getSimpleName(), TimeUtils.getCurrentTimeInLong());
    }

    private void handleLikeOrNot(Context context, NodeInfo nodeInfo, ImageView mIvLike, TextView mTvLiked) {
        if(Tools.isFastDoubleClick()) {
            return;
        }
        if (!isHasNetAndLogin(context)) {
            return;
        }
    }

    protected boolean isHasNetAndLogin(Context context) {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return false;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(context);
            return false;
        }
        return true;
    }

    static class ViewHolder {
        com.bm.library.PhotoView image;

        ViewHolder(View view) {
            image = (com.bm.library.PhotoView) view.findViewById(R.id.iv_evolution_cosplay);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.COSPLAY_EVOLUTION_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.COSPLAY_EVOLUTION_ACTIVITY); //
        MobclickAgent.onPause(this);
    }
}

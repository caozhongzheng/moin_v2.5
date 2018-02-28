package com.moinapp.wuliao.modules.discovery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.modules.post.PostActivity;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.stickercamera.app.camera.CameraManager;
import com.moinapp.wuliao.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.InjectView;

/** 话题详情的发帖/发图 浮层
 * Created by guyunfei on 16/6/15.16:02.
 */
public class PostArticleActivity extends BaseActivity {

    @InjectView(R.id.post_article)
    TextView post_article;

    @InjectView(R.id.post_picture)
    TextView post_picture;

    @InjectView(R.id.close)
    ImageView close;

    private StickerPackage mSticker;
    private String mTopicName;
    private String mTopicType;
    private String mTopicId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_article;
    }

    @Override
    public void initView() {
        post_article.setOnClickListener(this);
        post_picture.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    @Override
    public void initData() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mSticker = (StickerPackage) args.getSerializable(Constants.BUNDLE_KEY_STICKER);
            mTopicName = args.getString(Constants.BUNDLE_KEY_TAG);
            mTopicType = args.getString(Constants.BUNDLE_KEY_TYPE);
            mTopicId = args.getString(Constants.BUNDLE_KEY_ID);

            android.util.Log.i("PostArticleActivity", "话题[" + mTopicName + "] 对应的贴纸包是 " + (mSticker == null ? null : mSticker));
        }
//
//        android.util.Log.i("paa", "mTopicName=" + mTopicName
//                        + ", mTopicType=" + mTopicType
//                        + ", mTopicID=" + mTopicId
//        );
        if (StringUtil.isNullOrEmpty(mTopicId) && StringUtil.isNullOrEmpty(mTopicName)) {
            AppContext.showToast(R.string.invalid_topic_id);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        HashMap<String, String> map = null;
        switch (v.getId()) {
            case R.id.post_article:
                // 我要发帖-话题详情页
                if (map == null) {
                    map = new HashMap<String, String>();
                }
                map.put(UmengConstants.ITEM_ID, mTopicName + "_" + mTopicId);
                MobclickAgent.onEvent(this, UmengConstants.TOPIC_POST_CLICK_TOPIC_DETAIL, map);
                MobclickAgent.onEvent(this, UmengConstants.TOPIC_POST_CLICK, map);

                Intent intent = new Intent(this, PostActivity.class);
                intent.putExtra(Constants.BUNDLE_KEY_ID, mTopicId);
                intent.putExtra(Constants.BUNDLE_KEY_TAG, mTopicName);
                intent.putExtra(Constants.BUNDLE_KEY_TYPE, mTopicType);
                startActivity(intent);

                this.finish();
                break;
            case R.id.post_picture:
                // 我要发图-话题详情页
                if (map == null) {
                    map = new HashMap<String, String>();
                }
                map.put(UmengConstants.ITEM_ID, mTopicName + "_" + mTopicId);
                MobclickAgent.onEvent(this, UmengConstants.TOPIC_COSPLAY_CLICK_TOPIC_DETAIL, map);
                MobclickAgent.onEvent(this, UmengConstants.TOPIC_COSPLAY_CLICK, map);

                if (mSticker != null && !StringUtil.isNullOrEmpty(mSticker.getStickerPackageId())) {
                    CameraManager.getInst().makeCosplay(PostArticleActivity.this, mSticker.getStickerPackageId(), mTopicId, mTopicType, mTopicName);
                } else {
                    StickPreference.getInstance().setJoinTopicName(mTopicName);
                    CameraManager.getInst().openCamera(this, null);
                }
                this.finish();
                break;
            case R.id.close:
                this.finish();
                break;
        }
    }
}

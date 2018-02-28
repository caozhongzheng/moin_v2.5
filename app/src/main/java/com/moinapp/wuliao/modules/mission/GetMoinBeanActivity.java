package com.moinapp.wuliao.modules.mission;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.MoinBean;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.ui.RiseNumberTextView;

import butterknife.InjectView;

/** 话题详情的发帖/发图 浮层
 * Created by guyunfei on 16/6/15.16:02.
 */
public class GetMoinBeanActivity extends BaseActivity {
    private static ILogger MyLog = LoggerFactory.getLogger("GetMoinBeanActivity");
    public final static String KEY_MOIN_BEAN = "key_moin_bean";
    public final static String KEY_MISSION = "key_mission";

    @InjectView(R.id.fr_bg)
    FrameLayout mBg;

    @InjectView(R.id.rise_textview)
    RiseNumberTextView mTotalBean;

    @InjectView(R.id.tv_task_succeed)
    TextView mTaskSucceed;

    @InjectView(R.id.tv_task_complete)
    TextView mTaskAward;

    @InjectView(R.id.tv_award_num)
    TextView mAwardNum;

    private MoinBean moinBean;
    private int mMission;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_get_moin_bean;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            moinBean = (MoinBean) bundle.getSerializable(KEY_MOIN_BEAN);
            mMission = bundle.getInt(KEY_MISSION, 0);
        }

        //发送获取到魔豆的广播
        this.sendBroadcast(new Intent(Constants.INTENT_GET_MOIN_BEAN));

        String mission = "";
        switch (mMission) {
            case MissionConstants.MISSION_SHARE:
                mission = "分享";
                break;
            case MissionConstants.MISSION_MAKE_COSPLAY:
                mission = "发布";
                break;
            case MissionConstants.MISSION_LIKE:
                mission = "点赞";
                break;
            case MissionConstants.MISSION_COMMENT:
                mission = "评论";
                break;
        }

        if (moinBean != null) {
            mTaskSucceed.setText(mission + "成功");
            mTaskAward.setText(String.format(getString(R.string.mission_complete), mission));
            mAwardNum.setText(String.format(getString(R.string.mission_award), moinBean.getObtainBean()));

            mTotalBean.withNumber(moinBean.getTotalBean(), moinBean.getTotalBean() - moinBean.getObtainBean());
            // 设置动画播放时间
            mTotalBean.setDuration(1000);
            mTotalBean.start();
        } else {
            MyLog.i("moinBean == null, mission=" + mMission);
            finish();
        }

        mBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //延时1秒钟关闭
        new Handler().postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 3000);
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View v) {

    }
}

package com.moinapp.wuliao.modules.mine.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.model.TagInfo;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.util.Tools;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 关注的标签列表列表适配器
 * 新版是:搜索图片结果集 或者 订阅话题列表适配器
 *
 * @author liujiancheng
 */
public class MyTagsAdapter extends ListBaseAdapter<TagInfo> {
    private static final int FOLLOW_ALREADY = 100;//当在页面关注以后设置的临时标记

    private Activity mContext;
    private boolean mCanCancelFollow;
    boolean mIsPicNumVis = false;

    public MyTagsAdapter(Activity activity, boolean canCancelFollow, boolean isPicNumVis) {
        mContext = activity;
        mCanCancelFollow = canCancelFollow;
        mIsPicNumVis = isPicNumVis;
        if (mIsPicNumVis) {
            setFinishText(R.string.no_more_pics);
        } else {
            setFinishText(R.string.no_more_tag);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_follow_tag, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final TagInfo item = mDatas.get(position);

        if (!TextUtils.isEmpty(item.getName())) {
//            if (item.getType().equalsIgnoreCase("IP")) {
//                vh.name.setTextColor(convertView.getResources().getColor(R.color.moin));
//                vh.name.setText((item.getName().startsWith("《") ? "" : "《") + item.getName() + (item.getName().endsWith("》") ? "" : "》"));
//            } else if (item.getType().equalsIgnoreCase("OP")) {
//                vh.name.setText(" #" + item.getName());
//                vh.name.setTextColor(convertView.getResources().getColor(R.color.main_blue));
//            } else if (item.getType().equalsIgnoreCase("TP")) {
//                vh.name.setText(item.getName());
//                vh.name.setTextColor(convertView.getResources().getColor(R.color.main_blue));
//            } else {
//                vh.name.setText(item.getName());
//                vh.name.setTextColor(convertView.getResources().getColor(R.color.main_blue));
//            }
            vh.name.setText(item.getName());
        }
        if (mIsPicNumVis) {
            vh.picNum.setVisibility(View.VISIBLE);
            if (item.getPicNum() >= 0) {
                vh.picNum.setText("共有" + item.getPicNum() + "张图片");
            }
            vh.follow.setVisibility(View.GONE);
        } else {
            vh.picNum.setVisibility(View.GONE);
            vh.follow.setVisibility(View.VISIBLE);
        }

        final int follow = item.getIsFollow();
        Log.i("ljc", "item.isFollow=" + follow);
        setFollowStatus(vh.follow, follow);

        // 关注／取消关注
        vh.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断登陆状态
                if (!AppContext.getInstance().isLogin()) {
                    AppContext.showToast(R.string.unlogin);
                    UIHelper.showLoginActivity(mContext);
                    return;
                }
                if (Tools.isFastDoubleClick()) {
                    return;
                }
                int action = follow == 0 ? 1 : 0;
                if (!mCanCancelFollow && action == 0) {
                    vh.follow.setVisibility(View.GONE);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(UmengConstants.ITEM_ID, item.getName() + "");
                    map.put(UmengConstants.FROM, "订阅话题");
                    MobclickAgent.onEvent(mContext, UmengConstants.TAG_FOLLOW, map);
                    item.setIsFollow(1);
                } else {
                    if (follow == 1) {
                        DialogHelper dialogHelper = new DialogHelper(mContext);
                        dialogHelper.alert4M(null, "真的要取消订阅吗？",
                                "是", view -> {
                                    dialogHelper.dialogDismiss();
                                    doFollow(item);
                                }, "否", view -> dialogHelper.dialogDismiss(), false);
                    } else {
                        doFollow(item);
                    }
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsPicNumVis) {
                    UIHelper.showTagDetail(mContext, item.getName(), item.getType(), 0);
                } else {
                    UIHelper.showTopicDetail(mContext, item.getName(), item.getType(), item.getTagId(), 0);
                }
            }
        });
        return convertView;
    }

    private void doFollow(TagInfo item) {
        int action = item.getIsFollow() == 0 ? 1 : 0;
        DiscoveryManager.getInstance().followTag(item.getName(), item.getType(), action, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                int status = action == 1 ? 1 : 0;

                // 如果不让取消关注,则由关注变已经关注
                if (!mCanCancelFollow && status == 1) {
                    status = FOLLOW_ALREADY;
                }
                item.setIsFollow(status);
                new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }

            @Override
            public void onErr(Object obj) {
                Log.i("ljc", "关注／取消关注 failed!");
            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    private void setFollowStatus(TextView button, int status) {
        if (status == 1) {
            if (mCanCancelFollow) {
                button.setVisibility(View.VISIBLE);
                button.setBackgroundResource(R.drawable.long_boreder_gray);
                button.setText("已订阅");
                button.setTextColor(mContext.getResources().getColor(R.color.date_picker_text_normal));
            } else {
                button.setVisibility(View.GONE);
            }
        } else if (status == 0) {
            button.setBackgroundResource(R.drawable.long_border_pink);
            button.setText("+ 订阅");
            button.setTextColor(mContext.getResources().getColor(R.color.moin));
        } else if (status == FOLLOW_ALREADY) {
            button.setVisibility(View.VISIBLE);
            button.setBackgroundResource(R.drawable.long_boreder_gray);
            button.setText("已订阅");
            button.setTextColor(mContext.getResources().getColor(R.color.date_picker_text_normal));
        }
    }

    @Override
    public boolean isNeedLogin() {
        return true;
    }

    static class ViewHolder {

        @InjectView(R.id.tag_name)
        TextView name;

        @InjectView(R.id.pic_num)
        TextView picNum;

        @InjectView(R.id.btn_follow)
        TextView follow;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

package com.moinapp.wuliao.modules.discovery.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.FollowView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 赞列表列表适配器
 * 
 * @author liujiancheng
 * 
 */
public class LikeCosplayAdapter extends ListBaseAdapter<UserInfo> {
    private Activity mContext;
    private int mFrom;
    public LikeCosplayAdapter(Activity activity, int from) {
        mContext = activity;
        mFrom = from;
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
            final ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_like_cosplay, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final UserInfo item = mDatas.get(position);

        if (!TextUtils.isEmpty(item.getAlias())) {
            vh.name.setText(item.getAlias());
        } else {
            vh.name.setText(item.getUsername());
        }

        setUserInfo(vh, item);

        if (item.getAvatar() != null) {
            vh.avatar.setAvatarUrl(item.getAvatar().getUri());
        } else {
            vh.avatar.setAvatarUrl(null);
        }
        // 设置性别
        if ("male".equalsIgnoreCase(item.getSex())) {
            vh.gender.setImageResource(R.drawable.male_yellow);
        } else {
            vh.gender.setImageResource(R.drawable.female_red);
        }

        int relation = item.getRelation();
        Log.i("ljc", "item.relation=" + relation);

        vh.like.init(item, relation, mFrom);
        if (relation == UserDefineConstants.FRIENDS_SELF) {
            vh.like.setVisibility(View.GONE);
        } else {
            vh.like.setVisibility(View.VISIBLE);
        }

        vh.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(item.getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(mContext, 0);
                } else {
                    UIHelper.showUserCenter(mContext, item.getUId());
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(item.getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(mContext, 0);
                } else {
                    UIHelper.showUserCenter(mContext, item.getUId());
                }
            }
        });
        return convertView;
    }

    private void setUserInfo(ViewHolder vh, UserInfo item) {
        if (mFrom != UserDefineConstants.FOLLOW_SEARCH) {
            vh.sign.setVisibility(View.VISIBLE);
            vh.llNum.setVisibility(View.GONE);
            // 个性签名,如果没有则显示注册时间
            if (TextUtils.isEmpty(item.getSignature())) {
                String s = mContext.getString(R.string.i_register) + ": " +
                        StringUtil.formatDate(item.getCreatedAt(), StringUtil.TIME_PATTERN);
                vh.sign.setText(s);
            } else {
                vh.sign.setText(item.getSignature());
            }
        } else {
            vh.sign.setVisibility(View.GONE);
            vh.llNum.setVisibility(View.VISIBLE);
            vh.userCosplayNum.setText(String.valueOf(item.getCosplayNum()));
            vh.userLikeNum.setText(String.valueOf(item.getLikeNum()));
        }
    }

    static class ViewHolder {

        @InjectView(R.id.user_name)
        TextView name;

        @InjectView(R.id.btn_like)
        FollowView like;

        @InjectView(R.id.avatar)
        AvatarView avatar;

        @InjectView(R.id.iv_gender)
        ImageView gender;

        @InjectView(R.id.tv_user_sign)
        TextView sign;

        @InjectView(R.id.ll_num)
        LinearLayout llNum;

        @InjectView(R.id.iv_user_picnum)
        TextView userCosplayNum;

        @InjectView(R.id.tv_user_like_num)
        TextView userLikeNum;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

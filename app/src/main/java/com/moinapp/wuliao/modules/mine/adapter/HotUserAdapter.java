package com.moinapp.wuliao.modules.mine.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.login.model.HotUser;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.FollowView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 推荐用户列表适配器
 * 
 * @author liujiancheng
 * @created 2015年12月31日
 * 
 */
public class HotUserAdapter extends ListBaseAdapter<HotUser> {
    private static int IMG_WIDTH = (int) (TDevice.getScreenWidth() / 4);
    private Activity mContext;
    private int mJump;//如果为1,那么登录成功后需要跳转到首页的发现界面(曾经是关注界面).如果是0,则表示在某个深层界面触发登录,无需跳转

    public HotUserAdapter(Activity activity, int jump) {
        mContext = activity;
        mJump = jump;
    }
    private int mPosition = 0;
    private boolean animShow = true;
    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView, final ViewGroup parent) {
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_hot_user, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        setCosplayImageView(vh.item0);
        setCosplayImageView(vh.item1);
        setCosplayImageView(vh.item2);
        setCosplayImageView(vh.item3);

        final HotUser item = mDatas.get(position);
        if (item == null) return convertView;

        UserInfo user = item.getUser();
        if (user != null) {
            vh.follow.init(item.getUser(), user.getRelation(),
                    UserDefineConstants.FOLLOW_MY_FOLLOWS_FANS);

            if (user.getAvatar() != null) {
                vh.avatar.setAvatarUrl(user.getAvatar().getUri());
            } else {
                vh.avatar.setAvatarUrl(null);
            }

            vh.name.setText(user.getUsername());
//            vh.signature.setText(user.getSignature());
            vh.picNum.setText(user.getCosplayNum() + "");
            vh.likeNum.setText(user.getLikeNum() + "");

            if (user.getSex().equalsIgnoreCase("male")) {
                vh.sex.setImageResource(R.drawable.boy_yellow);
            } else {
                vh.sex.setImageResource(R.drawable.girl_red);
            }

            View.OnClickListener userCenter = v-> {
                gotoDiscovery();
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(user.getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(parent.getContext(), 0);
                } else {
                    UIHelper.showUserCenter(parent.getContext(), user.getUId());
                }
            };
            vh.userLy.setOnClickListener(userCenter);
            vh.avatar.setOnClickListener(userCenter);
            vh.name.setOnClickListener(userCenter);
        }

        List<CosplayInfo> list = item.getUserCosplays();
        if (list != null) {
            setCosplayInfo(list, vh);
        }
        return convertView;
    }

    private void setCosplayInfo(List<CosplayInfo> list, ViewHolder vh) {
        if (list == null || list.size() == 0) return;

        if (list.get(0) != null) {
            setCosplayInfo(list.get(0), vh.item0);
        }

        if (list.size() > 1) {
            if (list.get(1) != null) {
                setCosplayInfo(list.get(1), vh.item1);
            }
        }
        if (list.size() > 2) {
            if (list.get(2) != null) {
                setCosplayInfo(list.get(2), vh.item2);
            }
        }
        if (list.size() > 3) {
            if (list.get(3) != null) {
                setCosplayInfo(list.get(3), vh.item3);
            }
        }
    }

    private void setCosplayInfo(CosplayInfo cosplayInfo, ImageView imageView) {
//        setCosplayImageView(imageView);
        imageView.setVisibility(View.VISIBLE);
        if (cosplayInfo != null) {
            ImageLoaderUtils.displayHttpImage(cosplayInfo.getPicture().getUri(), imageView, null, animShow, null);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoDiscovery();
                    UIHelper.showDiscoveryCosplayDetail(imageView.getContext(), cosplayInfo,
                            cosplayInfo.getUcid(), TimeUtils.getCurrentTimeInLong());

                }
            });
        }
    }

    private void setCosplayImageView(ImageView imageView) {
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = IMG_WIDTH;
        params.height = IMG_WIDTH;
        imageView.setLayoutParams(params);
        imageView.setVisibility(View.INVISIBLE);
    }

    public void gotoDiscovery() {
        mContext.sendBroadcast(new Intent(Constants.INTENT_ACTION_USER_CHANGE));

        UIHelper.gotoMain(mContext, MainActivity.KEY_TAB_DISCOVERY, true);
    }

    static class ViewHolder {

        @InjectView(R.id.tv_name)
        TextView name;

        @InjectView(R.id.tv_pic_num)
        TextView picNum;

        @InjectView(R.id.tv_like_num)
        TextView likeNum;
//
//        @InjectView(R.id.tv_signature)
//        TextView signature;

        @InjectView(R.id.iv_sex)
        ImageView sex;

        @InjectView(R.id.iv_follow)
        FollowView follow;

        @InjectView(R.id.iv_avatar)
        AvatarView avatar;

        @InjectView(R.id.rl_user)
        RelativeLayout userLy;

        @InjectView(R.id.item0)
        RoundAngleImageView item0;
        @InjectView(R.id.item1)
        RoundAngleImageView item1;
        @InjectView(R.id.item2)
        RoundAngleImageView item2;
        @InjectView(R.id.item3)
        RoundAngleImageView item3;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

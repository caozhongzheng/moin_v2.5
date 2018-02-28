package com.moinapp.wuliao.modules.discovery.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 参与用户/浏览用户 列表适配器
 * Created by guyunfei on 16/2/24.14:18.
 */
public class UserListAdapter extends ListBaseAdapter<UserInfo> {
    final static int COLUMN = 4;
    private Activity mActivity;
    private int mPosition = 0;
    private boolean animShow = true;
    public UserListAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getDataSize() {
        return (mDatas.size() + COLUMN - 1) / COLUMN;
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_user_list_grid, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        for (int i = 0; i < COLUMN; i++) {
            UserInfo userInfo = (position * COLUMN + i < mDatas.size()) ? mDatas.get(position * COLUMN + i) : null;
            setUserInfo(userInfo, i, vh);
        }

        return convertView;
    }

    private void setUserInfo(UserInfo userInfo, int i, ViewHolder vh) {
        LinearLayout item = null;
        switch (i) {
            case 0:
                item = vh.item0;
                break;
            case 1:
                item = vh.item1;
                break;
            case 2:
                item = vh.item2;
                break;
            case 3:
                item = vh.item3;
                break;
        }
        if (item == null) return;
        if (userInfo == null) {
            item.setVisibility(View.INVISIBLE);
            return;
        } else {
            item.setVisibility(View.VISIBLE);
        }

        AvatarView avatarView = (AvatarView) item.findViewById(R.id.iv_user_list_avatar);
        if (avatarView != null) {
            if(userInfo.getAvatar() != null && !StringUtil.isNullOrEmpty(userInfo.getAvatar().getUri())) {
                setAvatarUrl(avatarView,userInfo.getAvatar().getUri(),animShow);
            } else {
                avatarView.setImageResource(R.drawable.widget_dface);
            }
            avatarView.setOnClickListener(v -> {
                // TODO 点击跳转到用户空间
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(userInfo.getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(mActivity, 0);
                } else {
                    UIHelper.showUserCenter(mActivity, userInfo.getUId());
                }
            });
        }

        TextView textView = (TextView) item.findViewById(R.id.tv_user_list_name);
        if (textView != null) {
            textView.setText(String.valueOf(userInfo.getUsername()));
        }
    }

    public void setAvatarUrl(AvatarView avatarView, String url, boolean animShow) {
        if (StringUtils.isEmpty(url)) {
            avatarView.setImageResource(R.drawable.widget_dface);
            return;
        }
        // 由于头像地址默认加了一段参数需要去掉
        int end = url.indexOf('?');
        final String headUrl;
        if (end > 0) {
            headUrl = url.substring(0, end);
        } else {
            headUrl = url;
        }

        ImageLoaderUtils.displayHttpImage(headUrl, avatarView, null, animShow, null);
    }

    static class ViewHolder {
        @InjectView(R.id.user0)
        LinearLayout item0;
        @InjectView(R.id.user1)
        LinearLayout item1;
        @InjectView(R.id.user2)
        LinearLayout item2;
        @InjectView(R.id.user3)
        LinearLayout item3;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

package com.moinapp.wuliao.modules.stickercamera.app.camera.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.widget.AvatarView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 好友列表适配器
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年11月6日 上午11:22:27
 *
 */
public class AtFollowerAdapter extends ListBaseAdapter<UserInfo> {
    private Activity mContext;
    private static final int FOLLOW_ALREADY = 100;//当在页面关注以后设置的临时标记
    public AtFollowerAdapter(Activity context) {
        this.mContext = context;
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_at_follower, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final UserInfo item = mDatas.get(position);
        if (item == null) return convertView;

        vh.name.setText(item.getUsername());

        if (item.getAvatar() != null) {
            setAvatarUrl(vh.avatar,item.getAvatar().getUri());
        } else {
            setAvatarUrl(vh.avatar,null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putSerializable("user", item);
                intent.putExtras(b);
                mContext.setResult(Activity.RESULT_OK, intent);
                mContext.finish();
            }
        });
        return convertView;
    }

    public void setAvatarUrl(AvatarView avatarView,String url) {
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

        avatarView.setUri(headUrl);
        ImageLoaderUtils.displayHttpImage(headUrl, avatarView, null, false, null);
    }

    static class ViewHolder {

        @InjectView(R.id.user_name)
        TextView name;

        @InjectView(R.id.avatar)
        AvatarView avatar;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}


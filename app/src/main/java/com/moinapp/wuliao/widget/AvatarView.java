package com.moinapp.wuliao.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.UIHelper;

public class AvatarView extends CircleImageView {
    public static final String AVATAR_SIZE_REG = "_[0-9]{1,3}";
    public static final String MIDDLE_SIZE = "_100";
    public static final String LARGE_SIZE = "_200";

    private int id;
    private String uid;
    private String name;
    private String uri;

    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AvatarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
//        aty = (Activity) context;
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(name)) {
                    // TODO 显示用户中心页面
//                    UIHelper.showUserCenter(getContext(), id, name);
                    if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(uid)) {
                        // 登录用户点击自己头像
                        UIHelper.showMine(getContext(), 0);
                    } else {
                        UIHelper.showUserCenter(getContext(), uid);
                    }
                }
            }
        });
    }

    public void setUserInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setUserInfo(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public void setAvatarUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            setImageResource(R.drawable.widget_dface);
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

        setUri(headUrl);
        ImageLoaderUtils.displayHttpImage(headUrl, this, null,true,null);
    }

    public void setAvatarPath(String localPath) {
        if (StringUtils.isEmpty(localPath)) {
            setImageResource(R.drawable.widget_dface);
            return;
        }
        setUri(localPath);
        ImageLoaderUtils.displayLocalImage(localPath, this, null);
    }
    public static String getSmallAvatar(String source) {
        return source;
    }

    public static String getMiddleAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, MIDDLE_SIZE);
    }

    public static String getLargeAvatar(String source) {
        if (source == null)
            return "";
        return source.replaceAll(AVATAR_SIZE_REG, LARGE_SIZE);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

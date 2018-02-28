package com.moinapp.wuliao.modules.mine.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.modules.mine.model.UserActivity;
import com.moinapp.wuliao.util.BitmapUtil;
import com.moinapp.wuliao.util.UIHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 *  我的赞列表适配器
 */
public class MyLikeAdapter extends MyCommentAdapter {
    public MyLikeAdapter(Activity activity, String uid) {
        super(activity, uid);
    }

    @Override
    protected String getPrefix(UserActivity userActivity) {
        if (ClientInfo.getUserName().equalsIgnoreCase(userActivity.getTargetName())) {
            return mContext.getString(R.string.my_like_prefix_myself);
        }
        return mContext.getString(R.string.my_like_prefix);
    }

    @Override
    protected String getSuffix() {
        return mContext.getString(R.string.my_like_suffix);
    }

    @Override
    protected boolean needTail() {
        return false;
    }
}

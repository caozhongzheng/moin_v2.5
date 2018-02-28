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
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.modules.mine.model.UserActivity;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 *  我的评论列表适配器
 */
public class MyCommentAdapter extends ListBaseAdapter<UserActivity> {
    private String mUid;
    protected Activity mContext;
    public MyCommentAdapter(Activity activity, String uid) {
        this.mUid = uid;
        this.mContext = activity;
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
            final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_my_comment, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final UserActivity item = mDatas.get(position);
        if (item == null) return convertView;

        setTitleText(vh.comment_title, item);
        setContentText(vh.comment_content, item);
        if (!TextUtils.isEmpty(item.getPicture())) {
            ImageLoaderUtils.displayHttpImage(item.getPicture(),
                    vh.comment_picture, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getType() == Messages.TYPE_IMAGE) {
                    UIHelper.showDiscoveryCosplayDetail(parent.getContext(), null, item.getResource(), TimeUtils.getCurrentTimeInLong());
                } else {
                    // TODO 非大咖秀的全部默认是帖子先.如果以后还有别的类型,需要增加判断.此处是防止服务器下发类型错误.
                    UIHelper.showPostDetail(mContext, item.getResource(), TimeUtils.getCurrentTimeInLong());
                }
            }
        });
        return convertView;
    }

    protected String getPrefix(UserActivity userActivity) {
        switch (userActivity.getAction()) {
            case Messages.ACTION_AT_COSPLAY:
                return mContext.getString(R.string.my_at_prefix);
            case Messages.ACTION_REPLY_COSPLAY:
                return mContext.getString(R.string.my_reply_prefix);
        }
        String prefix;
        if (ClientInfo.getUserName().equalsIgnoreCase(userActivity.getTargetName())) {
            prefix = mContext.getString(R.string.my_comment_prefix_myself);
        } else {
            prefix = mContext.getString(R.string.my_comment_prefix);
        }
        return prefix;
    }

    protected String getSuffix() {
        return "";
    }

    protected boolean needTail() {
        return true;
    }
    private String buildTitleText(UserActivity userActivity) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getPrefix(userActivity));
        if (!TextUtils.isEmpty(userActivity.getTargetName())) {
            if (ClientInfo.getUserName().equalsIgnoreCase(userActivity.getTargetName())) {
                buffer.append(mContext.getString(R.string.my_self));
            } else {
                buffer.append(userActivity.getTargetName());
            }
        }
        buffer.append(getSuffix());
//        if (needTail()) buffer.append("\n");
//        buffer.append(content);

        return buffer.toString();
    }

    private void setTitleText(TextView textView, UserActivity userActivity) {
        String content = buildTitleText(userActivity);

        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.BLACK);
        ForegroundColorSpan graySpan = new ForegroundColorSpan(Color.GRAY);
        builder.setSpan(graySpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int start = getPrefix(userActivity).length();
//        int end;
        if (!TextUtils.isEmpty(userActivity.getTargetName())
                && !ClientInfo.getUserName().equalsIgnoreCase(userActivity.getTargetName())) {
//            end = start + userActivity.getTargetName().length();
            builder.setSpan(blackSpan, start, /*end*/content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(builder);
    }

    private void setContentText(TextView textView, UserActivity userActivity) {
        String content = userActivity.getContent();

        if (content == null) {
            content = "";
        }
        textView.setText(content);
    }

    static class ViewHolder {

        @InjectView(R.id.comment_content)
        TextView comment_content;

        @InjectView(R.id.comment_title)
        TextView comment_title;

        @InjectView(R.id.comment_picture)
        ImageView comment_picture;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

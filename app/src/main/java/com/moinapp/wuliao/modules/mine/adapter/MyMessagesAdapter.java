package com.moinapp.wuliao.modules.mine.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.modules.mine.model.UserActivity;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.FollowView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的消息列表列表适配器
 * 
 * @author liujiancheng
 * 
 */
public class MyMessagesAdapter extends ListBaseAdapter<UserActivity> {
    private Activity mContext;
    public MyMessagesAdapter(Activity activity) {
        EventBus.getDefault().register(this);
        mContext = activity;
    }

    /**
     *  注意使用此adaptor的ui需要显式调用这个方法去反注册event bus
     */
    public void unregisterEventBus() {
        EventBus.getDefault().unregist(this);
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
            final ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(mContext).inflate(
                    R.layout.list_cell_message, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        UserActivity message = mDatas.get(position);
        if (message != null) {
            if (position == 7 || position == 8) {
                Log.i("ljc","action=" + message.getAction() + ", content=" + message.getContent());
            }
//            vh.title.setText(message.getTitle());
            vh.time.setText(StringUtil.humanDate(message.getCreatedAt()));

            //根据不同的消息类型显示不同的icon 3.2.5不要icon了
//            setIcon(vh.icon, message);

            //显示头像
            setAvatar(vh.avatar, message);

            //显示title的格式
            setTitleText(vh.title, message);

            //显示图片
            setPictureAndButton(vh, message);

            //设置点击事件
            setContentClick(convertView, message);
        }
        return convertView;
    }

    // 根据type action来显示不同icon
    private void setIcon(ImageView imageView, UserActivity message) {
        if (imageView == null) return;

        switch (message.getAction()) {
            case Messages.ACTION_LIKE_COSPLAY:
                imageView.setImageResource(R.drawable.icon_like_message);
                break;
            case Messages.ACTION_COMMENT_COSPLAY:
            case Messages.ACTION_REPLY_COSPLAY:
            case Messages.ACTION_COMMENT_LIKE_COSPLAY:
            case Messages.ACTION_REPLY_COMMENT_COSPLAY:
                imageView.setImageResource(R.drawable.icon_action_comment);
                break;
            case Messages.ACTION_FORWARD_COSPLAY:
            case Messages.ACTION_MODIFY_COSPLAY:
                imageView.setImageResource(R.drawable.icon_action_forward);
                break;
            case Messages.ACTION_AT_COSPLAY:
                imageView.setImageResource(R.drawable.icon_action_at);
                break;
            case Messages.ACTION_FOLLOW:
                int resId;
                if (message.getFollow() == null) {
                    resId = R.drawable.message_follow_male;
                } else {
                    resId = message.getFollow().getSex().equalsIgnoreCase("male") ?
                            R.drawable.message_follow_male : R.drawable.message_follow_female;
                }
                imageView.setImageResource(resId);
                break;
            case Messages.ACTION_STICKER_NORMAL:
            case Messages.ACTION_STICKER_INTIME:
            case Messages.ACTION_STICKER_FRAME:
            case Messages.ACTION_STICKER_BUBBLE:
            case Messages.ACTION_STICKER_TEXT:
            case Messages.ACTION_STICKER_SPECIAL:
                imageView.setImageResource(R.drawable.icon_action_sticker);
                break;
            case Messages.ACTION_SYSTEM_BOOT_IMAGE:
                imageView.setImageResource(R.drawable.icon_action_system);
                break;
        }
    }

    private void setAvatar(AvatarView avatarView, UserActivity message) {
        avatarView.setAvatarUrl(message.getIcon());
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(message.getUid())) return;
                //跳转用户中心
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(message.getUid())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(mContext, 0);
                } else {
                    UIHelper.showUserCenter(mContext, message.getUid());
                }
            }
        });
    }

    private void setTitleText(TextView textView, UserActivity message) {
        String content = getMessageContent(message);
        if (TextUtils.isEmpty(content)) return;

        //先把整个字符串设为灰色
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.BLACK);
        ForegroundColorSpan graySpan = new ForegroundColorSpan(Color.GRAY);
        builder.setSpan(message.isComment() ? blackSpan : graySpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(message.getUserName())) {
            if (message.isComment()) {
                int start = message.getUserName().length();
                int end = start + mContext.getString(R.string.my_message_reply).length() + 1;//评论了你/回复了你
                if (message.getAction() == Messages.ACTION_REPLY_COMMENT_COSPLAY) {
                    end--;//回复具体某人
                }
                builder.setSpan(graySpan, start, end, 0);
            } else {
                builder.setSpan(blackSpan, 0, message.getUserName().length(), 0);
            }
        }
        textView.setText(builder);
    }

    private void setPictureAndButton(ViewHolder viewHolder,  UserActivity message) {
        switch (message.getAction()) {
            case Messages.ACTION_STICKER_NORMAL:
                viewHolder.follow.setVisibility(View.GONE);
                viewHolder.picture.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.VISIBLE);
                viewHolder.view.setText("查看");
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MineManager.getInstance().gotoStickerDetail(message.getResId());
                    }
                });
                break;
            case Messages.TYPE_SYSTEM:
                viewHolder.follow.setVisibility(View.GONE);
                viewHolder.picture.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.GONE);
                break;
            case Messages.ACTION_FOLLOW:
                viewHolder.follow.setVisibility(View.VISIBLE);
                viewHolder.picture.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.GONE);
                ProcessFollowUser(viewHolder.follow, message);
                break;
            case Messages.ACTION_AT_COSPLAY:
            case Messages.ACTION_COMMENT_COSPLAY:
            case Messages.ACTION_LIKE_COSPLAY:
            case Messages.ACTION_FORWARD_COSPLAY:
            case Messages.ACTION_MODIFY_COSPLAY:
            case Messages.ACTION_REPLY_COSPLAY:
            case Messages.ACTION_COMMENT_LIKE_COSPLAY:
            case Messages.ACTION_REPLY_COMMENT_COSPLAY:
                viewHolder.follow.setVisibility(View.GONE);
                viewHolder.view.setVisibility(View.GONE);
                viewHolder.picture.setVisibility(View.VISIBLE);
                //image load
                if (!TextUtils.isEmpty(message.getPicture())) {
                    ImageLoaderUtils.displayHttpImage(message.getPicture(),
                            viewHolder.picture, null);
                }
                break;
        }
    }

    private void setContentClick(View convertView, UserActivity message) {
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (message.getAction()) {
                    case Messages.ACTION_AT_COSPLAY:
                    case Messages.ACTION_COMMENT_COSPLAY:
                    case Messages.ACTION_LIKE_COSPLAY:
                    case Messages.ACTION_FORWARD_COSPLAY:
                    case Messages.ACTION_MODIFY_COSPLAY:
                    case Messages.ACTION_REPLY_COSPLAY:
                    case Messages.ACTION_COMMENT_LIKE_COSPLAY:
                    case Messages.ACTION_REPLY_COMMENT_COSPLAY:
                        android.util.Log.i(MyMessagesAdapter.class.getSimpleName(), "你点击了 消息 " + message.toString());
                        if (message.getType() == Messages.TYPE_IMAGE) {
                            UIHelper.showDiscoveryCosplayDetail(mContext, null, message.getResource(), TimeUtils.getCurrentTimeInLong());
                        } else {
                            // TODO 非大咖秀的全部默认是帖子先.如果以后还有别的类型,需要增加判断.此处是防止服务器下发类型错误.
                            UIHelper.showPostDetail(mContext, message.getResource(), TimeUtils.getCurrentTimeInLong());
                        }
                        break;
                    case Messages.ACTION_FOLLOW:
                        if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(message.getUid())) {
                            // 登录用户点击自己头像
                            UIHelper.showMine(mContext, 0);
                        } else {
                            UIHelper.showUserCenter(mContext, message.getUid());
                        }
                        break;
                    case Messages.ACTION_STICKER_NORMAL:
                        MineManager.getInstance().gotoStickerDetail(message.getResource());
                        break;
                }

            }
        });

    }

    private void ProcessFollowUser(FollowView follow, UserActivity message) {
        UserInfo user = message.getFollow();
        if (user == null) return;
        follow.init(user, user.getRelation(), UserDefineConstants.FOLLOW_MESSAGE);
    }

    private String getMessageContent(UserActivity message) {
        StringBuffer content = new StringBuffer();
        content.append(message.getUserName());
        switch (message.getAction()) {
            case Messages.ACTION_AT_COSPLAY:
                content.append("@了你");
                break;
            case Messages.ACTION_COMMENT_COSPLAY:
            case Messages.ACTION_COMMENT_LIKE_COSPLAY:
                content.append("评论了你\n");
                content.append(message.getContent());
                break;
            case Messages.ACTION_LIKE_COSPLAY:
                content.append(mContext.getString(R.string.my_message_like_suffix));
                break;
            case Messages.ACTION_FORWARD_COSPLAY:
                content.append(mContext.getString(R.string.my_message_forward_suffix));
                break;
            case Messages.ACTION_MODIFY_COSPLAY:
                content.append(mContext.getString(R.string.my_message_modify_suffix));
                break;
            case Messages.ACTION_REPLY_COSPLAY:
                content.append(mContext.getString(R.string.my_message_reply));
                content.append(mContext.getString(R.string.my_message_you));
                content.append("\n");
                content.append(message.getContent());
                break;
            case Messages.ACTION_REPLY_COMMENT_COSPLAY:
                content.append(mContext.getString(R.string.my_message_reply));
                content.append(message.getTargetName());
                content.append("\n");
                content.append(message.getContent());
                break;
            case Messages.ACTION_FOLLOW:
                content.append(mContext.getString(R.string.my_message_follow_suffix));
                break;
        }
        return content.toString();
    }

    /**
     * 在消息列表页面如果收到消息,需要在当前页面实时显示, 所以在接收消息的receive里面post 消息过来,
     * 这里收到后在插入到第一条
     */
    public void onEvent(MineManager.ReceivedMessage message) {
        Log.i("ljc","onEvent:message:" + message.getMessageTitle());
//        Gson gson = new Gson();
//        try {
//            Messages item = gson.fromJson(message.getMessageBody(), Messages.class);
//            if (item != null) {
//                item.setTitle(message.getMessageTitle());
//                new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mDatas.add(0, item);
//                        notifyDataSetInvalidated();
//                    }
//                });
//            }
//        } catch (Exception e) {
//
//        }
    }

    @Override
    public boolean isNeedLogin() {
        return true;
    }

    static class ViewHolder {

//        @InjectView(R.id.message_icon)
//        ImageView icon;

        @InjectView(R.id.message_avatar)
        AvatarView avatar;

        @InjectView(R.id.message_content)
        TextView title;

        @InjectView(R.id.message_time)
        TextView time;

        @InjectView(R.id.btn_view)
        Button view;

        @InjectView(R.id.btn_follow)
        FollowView follow;

        @InjectView(R.id.message_picture)
        ImageView picture;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

package com.moinapp.wuliao.modules.mine.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.chat.ChatLayoutManager;
import com.moinapp.wuliao.modules.mine.model.ChatMessage;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;

import java.util.List;

/**
 * 聊天列表页适配器
 * Created by guyunfei on 16/4/13.17:47.
 */
public class ChatListAdapter extends BaseAdapter {
    private List<ChatMessage> messagesList;
    private Context mContext;

    public ChatListAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ChatMessage> messageList) {
        this.messagesList = messageList;
    }

    @Override
    public int getCount() {
        return messagesList.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_chat_list, null);
        }
        ViewHolder holder = ViewHolder.getViewHolder(convertView);

        ChatMessage message = messagesList.get(position);
        if (message != null) {
            if (message.getChatUser() != null && message.getChatUser().getAvatar() != null
                    && message.getChatUser().getAvatar().getUri() != null) {
                ImageLoaderUtils.displayHttpImage(message.getChatUser().getAvatar().getUri(), holder.avatar, null);
            }
            if (MineManager.getInstance().hasUnreadChat(message.getChatUser().getUId())) {
                holder.redPoint.setVisibility(View.VISIBLE);
            } else {
                holder.redPoint.setVisibility(View.GONE);
            }
            if (message.getChatUser() != null ) {
                if (message.getChatUser().getAlias() != null && !(message.getChatUser().getAlias().trim()).equals("")) {
                    holder.userName.setText(message.getChatUser().getAlias());
                } else {
                    if (message.getChatUser().getUsername() != null) {
                        holder.userName.setText(message.getChatUser().getUsername());
                    }
                }
            }
            if (message.getSendStatus() == 1) {
                holder.ivError.setVisibility(View.GONE);
            } else if (message.getSendStatus() == 0) {

            } else if (message.getSendStatus() == -1 || message.getSendStatus() == -2) {
                holder.ivError.setVisibility(View.VISIBLE);
            }
            if (message.getContent() != null) {
                if (message.getContentType() == 1) {
                    holder.content.setText(message.getContent());
                } else {
                    holder.content.setText("[图片]");
                }
            }
            holder.time.setText(StringUtil.humanDate(message.getServerTime()));
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置聊天已读
                MineManager.getInstance().markChatRecord(message.getChatUser().getUId(), 1);
                // 跳转到聊天详情页
                UIHelper.showChat(mContext, message.getChatUser().getUId(), StringUtil.getUserName(message.getChatUser()));
            }
        };
        holder.lyContent.setOnClickListener(listener);
        holder.avatar.setOnClickListener(listener);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除聊天信息
                MineManager.getInstance().deleteChatRecord(message.getChatUser().getUId());
                messagesList.remove(position);
                notifyDataSetChanged();
                ChatLayoutManager.getInstance().clearCurrentLayout();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        AvatarView avatar, redPoint;
        TextView userName, content, time, delete;
        ImageView ivError;
        LinearLayout lyContent;

        public ViewHolder(View convertView) {
            lyContent = (LinearLayout) convertView.findViewById(R.id.ly_chat_list_content);
            avatar = (AvatarView) convertView.findViewById(R.id.iv_avatar);
            redPoint = (AvatarView) convertView.findViewById(R.id.iv_red_point);
            userName = (TextView) convertView.findViewById(R.id.tv_user_name);
            ivError = (ImageView) convertView.findViewById(R.id.iv_error);
            content = (TextView) convertView.findViewById(R.id.tv_content);
            time = (TextView) convertView.findViewById(R.id.tv_time);
            delete = (TextView) convertView.findViewById(R.id.tv_delete);
        }

        public static ViewHolder getViewHolder(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}

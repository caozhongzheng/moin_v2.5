package com.moinapp.wuliao.modules.mine.chat;


import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.emoji.DisplayRules;
import com.moinapp.wuliao.emoji.InputHelper;
import com.moinapp.wuliao.modules.mine.model.ChatMessage;
import com.moinapp.wuliao.ui.imageselect.ImageLoader;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.MyLinkMovementMethod;
import com.moinapp.wuliao.widget.MyURLSpan;
import com.moinapp.wuliao.widget.TweetTextView;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 聊天界面信息适配器
 * Created by moying on 16/4/14.
 */

public class ChatMessageDetailAdapter extends ListBaseAdapter<ChatMessage> {

    private static final ILogger MyLog = LoggerFactory.getLogger(ChatMessageDetailAdapter.class.getSimpleName());

//    KJBitmap mKjBitmap = Core.getKJBitmap();
    private OnRetrySendMessageListener mOnRetrySendMessageListener;

    @Override
    protected boolean loadMoreHasBg() {
        return false;
    }

    public ChatMessageDetailAdapter() {
        /*try {
            //初始化display，设置图片的最大宽高和最小宽高
            ChatImageDisplayer displayer = new ChatImageDisplayer(new BitmapConfig());
            int maxWidth = TDevice.getDisplayMetrics().widthPixels / 2;
            int maxHeight = maxWidth;
            int minWidth = maxWidth / 2;
            int minHeight = minWidth;
            displayer.setImageSize(maxWidth, maxHeight, minWidth, minHeight);
            //kjBitmap 不能设置自定义的displayer，这里通过反射设置自定义的displayer
            Class<?> classType = mKjBitmap.getClass();
            Field field = classType.getDeclaredField("displayer");
            field.setAccessible(true);
            field.set(mKjBitmap, displayer);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        final ChatMessage item = mDatas.get(mDatas.size() - position - 1);
        MyLog.i((mDatas.size() - position - 1) + ", item="+item.toString());

        boolean needCreateView = false;
        ViewHolder vh = null;
        if (convertView == null) {
            needCreateView = true;
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (vh == null || vh.type != item.getType()) {
            needCreateView = true;
        }

        if (needCreateView) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    item.getType() == 2 ? R.layout.list_cell_chat_from : R.layout.list_cell_chat_to,
                    null);
            vh = new ViewHolder(convertView);
            vh.type = item.getType();
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        //检查是否需要显示时间
        if (item.isShowDate()) {
            vh.time.setText(StringUtils.friendly_time3(item.getServerTime()));
            vh.time.setVisibility(View.VISIBLE);
        } else {
            vh.time.setVisibility(View.GONE);
        }

        // 头像
        if (item.getType() == 2) {
            // 接收的消息
            if (item.getChatUser() != null && item.getChatUser().getAvatar() != null) {
                vh.avatar.setAvatarUrl(item.getChatUser().getAvatar().getUri());
                vh.avatar.setUserInfo(item.getChatUser().getUId(), item.getChatUser().getUsername());
            }
        } else {
            // 发送的消息
            vh.avatar.setAvatarUrl(AppContext.getInstance().getUserInfo().getAvatar().getUri());
            vh.avatar.setUserInfo(AppContext.getInstance().getUserInfo().getUId(), AppContext.getInstance().getUserInfo().getUsername());
        }

        //判断是不是图片
        if (item.getContentType() == 2) {
            //图片消息
            showImage(vh, item);
        } else if(item.getContentType() == 1) {
            //文本消息
            showText(vh, item);
        } else if(item.getContentType() == 3) {
            //预制图片
            showEmoji(vh, item);
        }
        showStatus(vh, item);

        return convertView;
    }

    /**
     * 显示文字消息
     *
     * @param vh
     * @param msg
     */
    private void showText(ViewHolder vh, ChatMessage msg) {
        MyLog.i("显示文本 " + msg.getContent());
        vh.bubble.setBackgroundResource(msg.getType() == 2 ? R.drawable.chat_from_bg_selector : R.drawable.chat_to_bg_selector);
        vh.image.setVisibility(View.GONE);
        vh.content.setVisibility(View.VISIBLE);
        Spanned span = Html.fromHtml(msg.getContent());
        span = InputHelper.displayEmoji(vh.content.getResources(), span);
        vh.content.setText(span);
        MyURLSpan.parseLinkText(vh.content, span);
    }

    /**
     * 显示图片
     *
     * @param vh
     * @param msg
     */
    private void showImage(ViewHolder vh, ChatMessage msg) {
        MyLog.i("显示图片 " + msg.getContent());
        vh.bubble.setBackgroundResource(0);
        vh.content.setVisibility(View.GONE);
        vh.image.setVisibility(View.VISIBLE);

        int SW = (int) TDevice.dpToPixel(100f);
        vh.image.setLayoutParams(new LinearLayout.LayoutParams(SW, SW));
        //加载图片
        int emjResID = getEmojiResId(msg.getContent());
        if (emjResID != 0) {
            //加载预置图片
            vh.image.setImageResource(emjResID);
            vh.image.setTag(msg.getContent());
        } else if (msg.getContent().startsWith("http")) {
            vh.image.setImageResource(R.drawable.load_img_loading);
            ImageLoaderUtils.displayHttpImage(msg.getContent(), vh.image, null);

//            HttpConfig.sCookie = ApiHttpClient.getCookie(AppContext.getInstance());
//            mKjBitmap.display(vh.image, msg.getContent(), R.drawable.default_img, 0, 0,
//                    null);
        } else {
            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(msg.getContent(), vh.image);
//            ImageLoaderUtils.displayHttpImage(, , null, true, null);
        }
    }

    /**
     * 显示预置图片
     *
     * @param vh
     * @param msg
     */
    private void showEmoji(ViewHolder vh, ChatMessage msg) {
        //表情设置为87dp
        int SW = (int) TDevice.dpToPixel(100f);
        vh.image.setLayoutParams(new LinearLayout.LayoutParams(SW, SW));

        int emjResID = getEmojiResId(msg.getContent());
        if (emjResID == 0) {
            showImage(vh, msg);
            return;
        } else {
            //加载预置图片
            vh.image.setImageResource(emjResID);
            vh.image.setTag(msg.getContent());
        }
        vh.bubble.setBackgroundResource(0);
        MyLog.i("显示Emoji " + msg.getContent()
                        + ",map=" + emjResID
        );
        vh.content.setVisibility(View.GONE);
        vh.image.setVisibility(View.VISIBLE);
    }

    private int getEmojiResId(String url) {
        int emojiId = 0;
        Map<String, Integer> emojiMap = DisplayRules.getMapAll();
        if (emojiMap != null) {
            Integer obj = emojiMap.get(url);
            if (obj != null) {
                MyLog.i("找到预置emoji, resid=" + obj);
                emojiId = obj;
            }
        }
        return  emojiId;
    }
    /**
     * 显示消息状态
     *
     * @param vh
     * @param msg
     */
    private void showStatus(ViewHolder vh, ChatMessage msg) {
        //如果msg正在发送，则显示progressBar. 发送错误显示错误图标
        if (msg.getType() == 1 && msg.getSendStatus() != ChatMessage.SendStatus.NORMAL) {
            vh.msgStatusPanel.setVisibility(View.VISIBLE);
            if (msg.getSendStatus() == ChatMessage.SendStatus.SENDING) {
                //sending 正在发送
                vh.progressBar.setVisibility(View.VISIBLE);
                vh.error.setVisibility(View.GONE);
                vh.error.setTag(null);
            } else {
                //error 发送出错
                vh.progressBar.setVisibility(View.GONE);
                vh.error.setVisibility(View.VISIBLE);
                //设置tag为msg id，以便点击重试发送
                vh.error.setTag(msg.getMessageId());
            }
        } else {
            //注意，此处隐藏要用INVISIBLE，不能使用GONE
            vh.msgStatusPanel.setVisibility(View.INVISIBLE);
            vh.error.setTag(null);
        }
    }

    public OnRetrySendMessageListener getOnRetrySendMessageListener() {
        return mOnRetrySendMessageListener;
    }

    public void setOnRetrySendMessageListener(OnRetrySendMessageListener
                                                      onRetrySendMessageListener) {
        this.mOnRetrySendMessageListener = onRetrySendMessageListener;
    }

    @Override
    protected boolean hasFooterView() {
        return false;
    }

    class ViewHolder {
        int type;
        @InjectView(R.id.iv_avatar)
        AvatarView avatar;
        @InjectView(R.id.tv_time)
        TextView time;
        @InjectView(R.id.tv_content)
        TweetTextView content;

        @InjectView(R.id.ly_content)
        LinearLayout bubble;

        @InjectView(R.id.iv_img)
        ImageView image;
        @InjectView(R.id.progress)
        ProgressBar progressBar;
        @InjectView(R.id.rl_msg_status_panel)
        RelativeLayout msgStatusPanel;
        @InjectView(R.id.itv_error)
        ImageView error;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);

            content.setMovementMethod(MyLinkMovementMethod.a());
            content.setFocusable(false);
            content.setDispatchToParent(true);
            content.setLongClickable(false);
        }
// TODO 大图浏览
        @OnClick(R.id.iv_img)
        void viewImage(View v) {
            if (v.getTag() != null) {
                String url = (String) v.getTag();
                UIHelper.showImagePreview(v.getContext(), new String[]{url});
            }
        }

        /**
         * 重试发送 TODO
         *
         * @param v
         */
        @OnClick(R.id.itv_error)
        void retry(View v) {
            if (v.getTag() != null && mOnRetrySendMessageListener != null) {
                mOnRetrySendMessageListener.onRetrySendMessage((String) v.getTag());
            }
        }
    }


    public interface OnRetrySendMessageListener {
        void onRetrySendMessage(String msgId);
    }
}

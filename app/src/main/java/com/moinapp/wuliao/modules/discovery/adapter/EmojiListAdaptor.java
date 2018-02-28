package com.moinapp.wuliao.modules.discovery.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.model.EmojiSet;
import com.moinapp.wuliao.modules.discovery.ui.EmojiDetailActivity;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.widget.AvatarView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 表情页面列表适配器
 * Created by liujiancheng on 15/9/22.
 */
public class EmojiListAdaptor extends ListBaseAdapter<EmojiSet> {
    private static final ILogger MyLog = LoggerFactory.getLogger("EmojiListAdaptor");
    public EmojiListAdaptor() {

    }

    /**
     *  注意使用此adaptor的ui需要显式调用这个方法去反注册event bus
     */
    public void unregisterEventBus() {
    }

    @Override
    public int getDataSize() {
        return (mDatas.size() + 1) / 2;
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_emojiset, null);

            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final EmojiSet item0 = mDatas.get(position * 2);
        final EmojiSet item1 = (position * 2 + 1 < mDatas.size()) ? mDatas.get(position * 2 + 1) : null;

        if (item0 != null ) {
            setView(item0, vh.item0);
        }

        if (item1 != null) {
            setView(item1, vh.item1);
        }

        return convertView;
    }

    private void setView(EmojiSet item, View view) {
        AvatarView cover = (AvatarView)view.findViewById(R.id.emoji_cover);
        TextView name = (TextView)view.findViewById(R.id.emojiset_name);
        TextView count = (TextView)view.findViewById(R.id.emoji_count);
        TextView update = (TextView)view.findViewById(R.id.emoji_update);

        if (item.getIcon() != null)
            cover.setAvatarUrl(item.getIcon().getUri());
        name.setText(item.getName());
        count.setText(BaseApplication.context().getString(R.string.stick_count)+item.getEmojiNum() + "张");
        update.setText(BaseApplication.context().getString(R.string.stick_update) + StringUtil.humanDate(item.getUpdateAt(), StringUtil.TIME_PATTERN));
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDetial(item.getEmojiId());
            }
        });
    }

    static class ViewHolder {
        @InjectView(R.id.emoji_list_item0)
        View item0;

        @InjectView(R.id.emoji_list_item1)
        View item1;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private void gotoDetial(String emojiid) {
        Intent intent = new Intent(BaseApplication.context(), EmojiDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EmojiDetailActivity.EMOJI_ID, emojiid);
        BaseApplication.context().startActivity(intent);
    }
}


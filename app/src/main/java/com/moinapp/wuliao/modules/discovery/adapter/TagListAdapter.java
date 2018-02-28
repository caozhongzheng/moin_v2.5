package com.moinapp.wuliao.modules.discovery.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.util.ImageLoaderUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 话题列表适配器
 * Created by guyunfei on 16/3/1.11:29.
 */
public class TagListAdapter extends ListBaseAdapter<TagPop> {

    private static final ILogger MyLog = LoggerFactory.getLogger(TagListAdapter.class.getSimpleName());

    private Activity mActivity;
    private int mPosition = 0;
    private boolean animShow = true;

    public TagListAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getDataSize() {
        return mDatas.size();
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
                    R.layout.item_topic_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TagPop tagPop = mDatas.get(position);
        if (tagPop != null) {
            setUserInfo(tagPop, vh);
        }

        return convertView;
    }

    private void setUserInfo(TagPop tagPop, ViewHolder vh) {

        ImageLoaderUtils.displayHttpImage(tagPop.getIcon().getUri(), vh.topicImg, null, animShow, null);

        if (tagPop.getName() != null) {
            vh.topicName.setText(tagPop.getName() + "");
        }

        if (tagPop.getCategoryName() != null) {
            vh.topicCategory.setText(tagPop.getCategoryName());
        }

        if (tagPop.getReadNum() >= 0) {
            vh.viewNum.setText(tagPop.getReadNum() + "");
        }

        if (tagPop.getCosplayNum() >= 0) {
            vh.contentNum.setText(tagPop.getCosplayNum() + "");
        }

        if (tagPop.getCommentNum() >= 0) {
            vh.commentNum.setText(tagPop.getCommentNum() + "");
        }

        if (tagPop.getDesc() != null) {
            vh.topicDes.setText(tagPop.getDesc());
        }
    }

    static class ViewHolder {
        @InjectView(R.id.ic_topic_pic)
        RoundAngleImageView topicImg;
        @InjectView(R.id.tv_topic_name)
        TextView topicName;
        @InjectView(R.id.tv_topic_category)
        TextView topicCategory;
        @InjectView(R.id.tv_view_num)
        TextView viewNum;
        @InjectView(R.id.tv_content_num)
        TextView contentNum;
        @InjectView(R.id.tv_comment_num)
        TextView commentNum;
        @InjectView(R.id.tv_desc)
        TextView topicDes;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

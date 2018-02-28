package com.moinapp.wuliao.modules.discovery.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.util.ImageLoaderUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 发现频道热门话题适配器
 * Created by guyunfei on 16/6/6.19:11.
 */
public class RecommendTopicAdapter extends ListBaseAdapter<TagPop> {
    private Activity mActivity;
    private int mPosition = 0;
    private boolean animShow = true;

    public RecommendTopicAdapter(Activity activity) {
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
                    R.layout.item_hot_topic, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TagPop tagPop = mDatas.get(position);

        ImageLoaderUtils.displayHttpImage(tagPop.getIcon().getUri(), vh.pic, null, animShow, null);

        if (tagPop.getName() != null) {
            vh.name.setText(tagPop.getName() + "");
        }

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.ic_topic_pic)
        RoundAngleImageView pic;
        @InjectView(R.id.tv_topic_name)
        TextView name;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

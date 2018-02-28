package com.moinapp.wuliao.modules.events.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.events.model.EventsInfo;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 活动列表适配器
 */
public class EventsListAdapter extends ListBaseAdapter<EventsInfo> {
    private ILogger MyLog = LoggerFactory.getLogger("EventsListAdapter");

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_events_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final EventsInfo item = mDatas.get(position);
        if (item == null) return convertView;

        if (item.getIcon() != null) {
            ImageLoaderUtils.displayHttpImage(item.getIcon().getUri(), vh.image, ImageLoaderUtils.getImageLoaderOptionWithoutDefPic(), true, null);
        }
        vh.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo跳转活动详情
                UIHelper.showEventsDetail(parent.getContext(), item);
            }
        });
        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.iv_events)
        RoundAngleImageView image;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

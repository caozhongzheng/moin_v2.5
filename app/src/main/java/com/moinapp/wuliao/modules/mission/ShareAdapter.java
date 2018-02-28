package com.moinapp.wuliao.modules.mission;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.discovery.adapter.RecyclerViewHeaderFotterAdapter;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.events.model.EventsInfo;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;

import java.util.List;

/**
 * Created by guyunfei on 16/7/25.15:52.
 */
public class ShareAdapter extends RecyclerViewHeaderFotterAdapter {

    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private HeaderViewHolder headerViewHolder;
    //热门推荐
    private List<CosplayInfo> mHotListDatas;
    private EventsInfo mEventsInfo;

    public ShareAdapter(Activity activity, RecyclerView recyclerView) {
        mActivity = activity;
        mRecyclerView = recyclerView;
        setSpanCount(recyclerView);
    }

    public String getLastId() {
        if (mHotListDatas != null && mHotListDatas.size() > 0) {
            CosplayInfo cosplayInfo = mHotListDatas.get(mHotListDatas.size() - 1);
            if (cosplayInfo != null) {
                return cosplayInfo.getUcid();
            }
        }
        return null;
    }

    public List<CosplayInfo> getDatas() {
        if (mHotListDatas != null) {
            return mHotListDatas;
        }
        return null;
    }

    public void setHotListData(List<CosplayInfo> mDatas) {
        this.mHotListDatas = mDatas;
    }

    public void addHotListData(List<CosplayInfo> mDatas) {
        mHotListDatas.addAll(mDatas);
    }


    public void setHotEventData(EventsInfo eventsInfo) {
        this.mEventsInfo = eventsInfo;
    }

    @Override
    public int getItemCount() {
        if (mHotListDatas != null) {
            return mHotListDatas.size() + 2;
        }
        return 2;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent) {
        DiscoveryRecycleViewHolder holder = new DiscoveryRecycleViewHolder(
                LayoutInflater.from(mActivity).inflate(R.layout.item_discovery_simple,
                        parent, false));
        return holder;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_share_fragment_header, parent, false);
        headerViewHolder = new HeaderViewHolder(view);
        return headerViewHolder;
    }

    @Override
    protected void setHeaderLayoutParams(StaggeredGridLayoutManager.LayoutParams layoutParams) {
        headerViewHolder.rootView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < 0) {
            return;
        }
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        DiscoveryRecycleViewHolder viewHolder = (DiscoveryRecycleViewHolder) holder;
        if (position >= mHotListDatas.size()) {
            return;
        }
        CosplayInfo cosplayInfo = mHotListDatas.get(position);
        if (cosplayInfo != null) {
            if (cosplayInfo.getPicture() != null) {
                if (cosplayInfo.getPicture().getUri() != null) {
                    ImageLoaderUtils.displayHttpImage(false, cosplayInfo.getPicture().getUri(), viewHolder.image,
                            null, animShow, null);
                }
            }

            if (cosplayInfo.getContent() != null && !cosplayInfo.getContent().equals("")) {
                viewHolder.desc.setVisibility(View.VISIBLE);
                viewHolder.desc.setText(cosplayInfo.getContent());
            } else {
                viewHolder.desc.setVisibility(View.GONE);
            }
        }

        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showDiscoveryCosplayDetail(mActivity, cosplayInfo, cosplayInfo.getUcid(),
                        StringUtil.FROM_MISSION, TimeUtils.getCurrentTimeInLong());
            }
        });
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder vh = (HeaderViewHolder) holder;
        if (mEventsInfo != null) {
            ImageLoaderUtils.displayHttpImage(mEventsInfo.getIcon().getUri(), vh.image, null, true, null);
            vh.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转活动详情
                    UIHelper.showEventsDetail(mActivity, mEventsInfo, MissionConstants.MISSION_SHARE);
                }
            });
        }
    }

    /**
     * 正常布局ViewHolder
     */
    class DiscoveryRecycleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        RoundAngleImageView image;
        TextView desc;

        public DiscoveryRecycleViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView.findViewById(R.id.ll_container);
            image = (RoundAngleImageView) itemView.findViewById(R.id.image);
            desc = (TextView) itemView.findViewById(R.id.desc);
        }
    }

    /**
     * 头布局ViewHolder
     */
    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        RoundAngleImageView image;
        LinearLayout rootView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            image = (RoundAngleImageView) itemView.findViewById(R.id.iv_events);
            rootView = (LinearLayout) itemView.findViewById(R.id.event_root_view);
        }
    }
}

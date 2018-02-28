package com.moinapp.wuliao.modules.discovery.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

/**
 * Created by guyunfei on 16/7/25.15:22.
 */
public abstract class RecyclerViewHeaderFotterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final ILogger MyLog = LoggerFactory.getLogger(DiscoveryRecycleViewAdapter.class.getSimpleName());

    public static final int FOOTER_MODE_LOADING = 0;
    public static final int FOOTER_MODE_NO_MORE = 1;
    public static final int FOOTER_MODE_ERROR = 2;

    //正常条目
    private static final int TYPE_NORMAL_ITEM = 0;
    //加载条目
    private static final int TYPE_LOADING_ITEM = 1;
    //头布局
    private static final int TYPE_HEADER_ITEM = 2;
    //瀑布流
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    //加载viewHolder
    private LoadingViewHolder mLoadingViewHolder;


    public int mPosition = 0;
    public boolean animShow = true;

    @Override
    public abstract int getItemCount();


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER_ITEM;
        } else if (position >= getItemCount() - 1) {
            return TYPE_LOADING_ITEM;
        } else {
            return TYPE_NORMAL_ITEM;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL_ITEM) {
            return onCreateNormalViewHolder(parent);
        } else if (viewType == TYPE_LOADING_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_cell_footer, parent, false);
            mLoadingViewHolder = new LoadingViewHolder(view);
            return mLoadingViewHolder;
        } else if (viewType == TYPE_HEADER_ITEM) {
            return onCreateHeaderViewHolder(parent);
        } else {
            return null;
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent);


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_NORMAL_ITEM) {
            onBindNormalViewHolder(holder, position - 1);
        } else if (type == TYPE_LOADING_ITEM) {
            if (mStaggeredGridLayoutManager != null) {
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        new StaggeredGridLayoutManager.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);

                mLoadingViewHolder.llyLoading.setLayoutParams(layoutParams);
            }
        } else if (type == TYPE_HEADER_ITEM) {
            if (mStaggeredGridLayoutManager != null) {
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        new StaggeredGridLayoutManager.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setFullSpan(true);

                setHeaderLayoutParams(layoutParams);
                onBindHeaderViewHolder(holder);
            }
        }
    }

    protected abstract void setHeaderLayoutParams(StaggeredGridLayoutManager.LayoutParams layoutParams);

    /**
     * 绑定正常布局viewHolder
     *
     * @param holder   viewHolder
     * @param position position
     */
    protected abstract void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position);


    /**
     * 绑定头布局ViewHolder
     *
     * @param holder
     */
    protected abstract void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) ;

    public void setFooterMode(int mode) {
        switch (mode) {
            case FOOTER_MODE_LOADING:
                progressBar.setVisibility(View.VISIBLE);
                tvLoading.setText(R.string.loading);
                break;
            case FOOTER_MODE_NO_MORE:
                progressBar.setVisibility(View.GONE);
                tvLoading.setText(R.string.loading_no_data);
                break;
            case FOOTER_MODE_ERROR:
                progressBar.setVisibility(View.GONE);
                tvLoading.setText("加载失败,请重试");
                break;
        }
    }

    public ProgressBar progressBar;
    public TextView tvLoading;

    /**
     * 底部加载更多布局ViewHolder
     */
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llyLoading;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view
                    .findViewById(R.id.progressbar);
            tvLoading = (TextView) view.findViewById(R.id.text);
            tvLoading.setTextSize(13);
            llyLoading = (LinearLayout) view.findViewById(R.id.ll_loading_more);
            llyLoading.setBackgroundColor(BaseApplication.context().getResources().getColor(R.color.white));
        }
    }


    /**
     * 设置加载item占据一行
     *
     * @param recyclerView recycleView
     */
    protected void setSpanCount(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager == null) {
            MyLog.i("LayoutManager 为空,请先设置 recycleView.setLayoutManager(...)");
        }

        //网格布局
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == TYPE_NORMAL_ITEM) {
                        return 1;
                    } else {
                        return gridLayoutManager.getSpanCount();
                    }
                }
            });
        }

        //瀑布流布局
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            mStaggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
        }
    }

}

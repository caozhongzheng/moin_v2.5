package com.moinapp.wuliao.modules.stickercamera.app.camera.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.stickercamera.app.camera.effect.FilterEffect;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.GPUImageFilterTools;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * 滤镜适配器
 * @author tongqian.ni
 */
public class FilterAdapter extends BaseAdapter {

    List<FilterEffect> filterUris;
    Context            mContext;
    private Bitmap     background;

    private int        selectFilter = 0;

    public void setSelectFilter(int selectFilter) {
        this.selectFilter = selectFilter;
        notifyDataSetChanged();
    }

    public int getSelectFilter() {
        return selectFilter;
    }

    public FilterAdapter(Context context, List<FilterEffect> effects, int filter, Bitmap backgroud) {
        filterUris = effects;
        mContext = context;
        this.selectFilter = filter;
        if (filterUris != null && filterUris.size() > 0 && filter >= filterUris.size()) {
            this.selectFilter = filterUris.size() - 1;
        }
        if (filter < 0) {
            this.selectFilter = 0;
        }
        this.background = backgroud;
    }

    @Override
    public int getCount() {
        return filterUris.size();
    }

    @Override
    public Object getItem(int position) {
        return filterUris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EffectHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_bottom_filter, null);
            holder = new EffectHolder();
            holder.imageContainer = (LinearLayout) convertView.findViewById(R.id.small_filter_container);
            holder.filteredImg = (GPUImageView) convertView.findViewById(R.id.small_filter);
            holder.filterName = (TextView) convertView.findViewById(R.id.filter_name);
            convertView.setTag(holder);
        } else {
            holder = (EffectHolder) convertView.getTag();
        }

        final FilterEffect effect = (FilterEffect) getItem(position);

        holder.filteredImg.setImage(background);
        holder.filterName.setText(effect.getTitle());

        if (position == getSelectFilter()) {
            holder.imageContainer.setBackgroundResource(R.drawable.bottom_tool_selected);
            holder.filterName.setTextColor(mContext.getResources().getColor(R.color.moin));
        } else {
            holder.imageContainer.setBackgroundResource(R.drawable.bottom_tool);
//            holder.imageContainer.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bottom_tool));
            holder.filterName.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        //if (!effect.isOri() && effect.getType() != null) {
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(mContext, effect.getType());
        holder.filteredImg.setFilter(filter);

        return convertView;
    }

    class EffectHolder {
        LinearLayout imageContainer;
        GPUImageView filteredImg;
        TextView     filterName;
    }

}

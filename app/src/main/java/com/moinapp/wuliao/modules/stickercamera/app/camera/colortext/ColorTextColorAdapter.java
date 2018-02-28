package com.moinapp.wuliao.modules.stickercamera.app.camera.colortext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.moinapp.wuliao.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 彩色字的颜色适配器
 */
public class ColorTextColorAdapter extends BaseAdapter {

    List<ColorTextColor> ColorTextColors;
    Context mContext;

    private int lastClickPos = -1;
    private boolean isColorTextStickerSelected = false;

    public void setLastClickPos(int lastClickPos) {
        this.lastClickPos = lastClickPos;
        notifyDataSetChanged();
    }

    public void setIsColorTextStickerSelected(boolean isColorTextStickerSelected) {
        this.isColorTextStickerSelected = isColorTextStickerSelected;
        notifyDataSetChanged();
    }

    public int getLastClickPos() {
        return lastClickPos;
    }

    public ColorTextColorAdapter(Context context, List<ColorTextColor> styles, int pos, boolean isColorTextStickerSelected) {
        mContext = context;
        ColorTextColors = styles;
        this.lastClickPos = pos;
        this.isColorTextStickerSelected = isColorTextStickerSelected;
        if (ColorTextColors != null && ColorTextColors.size() > 0 && pos >= ColorTextColors.size()) {
            this.lastClickPos = ColorTextColors.size() - 1;
        }

    }

    @Override
    public int getCount() {
        return ColorTextColors.size();
    }

    @Override
    public Object getItem(int position) {
        return ColorTextColors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_bottom_color_text_color, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final ColorTextColor effect = (ColorTextColor) getItem(position);

        vh.bgColor.setBackgroundResource(effect.getBgDrw());
        if (isColorTextStickerSelected) {
            vh.fgColor.setBackgroundResource(effect.getFgDrw());
        } else {
            vh.fgColor.setBackgroundResource(effect.getFgAlphaDrw());
        }

        if (position == getLastClickPos()) {
            vh.bgColor.setVisibility(View.VISIBLE);
        } else {
            vh.bgColor.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.bgColor)
        View bgColor;
        @InjectView(R.id.fgColor)
        View fgColor;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

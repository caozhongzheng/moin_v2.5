package com.moinapp.wuliao.modules.stickercamera.app.camera.colortext;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 彩色字的字体适配器
 *
 */
public class ColorTextStyleAdapter extends BaseAdapter {

    private static final ILogger MyLog = LoggerFactory.getLogger(ColorTextStyleAdapter.class.getSimpleName());
    List<ColorTextStyle> colorTextStyles;
    Context            mContext;

    private int lastClickPos = -1;
    private String selectColor = AppContext.getInstance().getString(R.string.color_text_color_0);

    public void setLastClickPos(int lastClickPos) {
        this.lastClickPos = lastClickPos;
        notifyDataSetChanged();
    }

    public int getLastClickPos() {
        return lastClickPos;
    }

    public String getSelectColor() {
        return selectColor;
    }

    public void setSelectColor(String selectColor) {
        MyLog.i("old color = " + this.selectColor + ", new is " + selectColor);
        this.selectColor = selectColor;
        MyLog.i("old color = " + this.selectColor + ", new is " + selectColor);

        notifyDataSetChanged();
    }

    public ColorTextStyleAdapter(Context context, List<ColorTextStyle> styles, int pos, String color) {
        mContext = context;
        colorTextStyles = styles;
        this.lastClickPos = pos;
        this.selectColor = color;
        if (colorTextStyles != null && colorTextStyles.size() > 0 && pos >= colorTextStyles.size()) {
            this.lastClickPos = colorTextStyles.size() - 1;
        }

        MyLog.i("colorTextStyles.size= " + colorTextStyles.size() + ", lastClickPos= " + lastClickPos + ", selectColor= " + selectColor);
    }

    @Override
    public int getCount() {
        return colorTextStyles.size();
    }

    @Override
    public Object getItem(int position) {
        return colorTextStyles.get(position);
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
            convertView = layoutInflater.inflate(R.layout.item_bottom_color_text_style, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final ColorTextStyle textStyle = (ColorTextStyle) getItem(position);
        MyLog.i(position + ", with color ["+selectColor + "], getView " + textStyle.toString());

        vh.text.setTextColor(Color.parseColor(selectColor));
        vh.text.setTypeface(textStyle.getTf(), textStyle.getStyle());

        vh.text.getPaint().setAntiAlias(true);
        vh.text.getPaint().setFlags(textStyle.getFlag());

        if (position == getLastClickPos()) {
            vh.text.setBackgroundResource(R.drawable.shape_color_text_style_bg_selected);
        } else {
            vh.text.setBackgroundResource(R.drawable.shape_color_text_style_bg_normal);
        }

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.tv_ct)
        TextView text;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

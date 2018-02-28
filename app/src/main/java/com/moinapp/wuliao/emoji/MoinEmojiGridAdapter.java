/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.moinapp.wuliao.emoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 表情适配器
 * 
 * @author kymjs (http://www.kymjs.com)
 * 
 */
public class MoinEmojiGridAdapter extends BaseAdapter {

    private List<Emojicon> datas;
    private final Context cxt;

    public MoinEmojiGridAdapter(Context cxt, List<Emojicon> datas) {
        this.cxt = cxt;
        if (datas == null) {
            datas = new ArrayList<Emojicon>(0);
        }
        this.datas = datas;
    }

    public void refresh(List<Emojicon> datas) {
        if (datas == null) {
            datas = new ArrayList<Emojicon>(0);
        }
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {

        @InjectView(R.id.tv_chat_image)
        TextView name;

        @InjectView(R.id.iv_chat_image)
        ImageView image;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(
                    R.layout.grid_cell_chat_emoji, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageResource(datas.get(position).getResId());
        holder.name.setText(datas.get(position).getEmojiStr());
//        android.util.Log.i(MoinEmojiGridAdapter.class.getSimpleName(), "get["+position+"] emj=" + datas.get(position).toString());
        return convertView;
    }
}

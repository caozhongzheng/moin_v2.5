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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.moinapp.wuliao.modules.discovery.DiscoveryConstants;
import com.moinapp.wuliao.ui.imageselect.SelectPhotoActivity;
import com.moinapp.wuliao.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情页，每页的显示
 * 
 * @author kymjs (http://www.kymjs.com)
 * 
 */
@SuppressLint("ValidFragment")
public class MoinEmojiPageFragment extends Fragment {
    private List<Emojicon> datas;
    private GridView sGrid;
    private MoinEmojiGridAdapter adapter;
    private OnEmojiClickListener listener;

    public MoinEmojiPageFragment(int index, int type, OnEmojiClickListener l) {
        initData(index, type);
        this.listener = l;
    }

    private void initData(int index, int type) {
        datas = new ArrayList<Emojicon>();
        if (MoinEmojiFragment.EMOJI_TAB_CONTENT > 1) {
            datas = DisplayRules.getAllByType(type);
        } else {
            List<Emojicon> dataAll = DisplayRules.getAllByType(type);
            int max = Math.min((index + 1) * KJEmojiConfig.COUNT_IN_PAGE_MOIN,
                    dataAll.size());
            android.util.Log.i(MoinEmojiPageFragment.class.getSimpleName(),
                    " initData max=[" + max
                            + " type=" + type
                            + "] index=" + index);

            // TODO 设置8个预制图的地方
            for (int i = index * KJEmojiConfig.COUNT_IN_PAGE_MOIN; i < max; i++) {
                datas.add(dataAll.get(i));
                android.util.Log.i(MoinEmojiPageFragment.class.getSimpleName(),
                        " addData [" + i
                                + dataAll.get(i).toString());
            }
//            datas.add(new Emojicon(KJEmojiConfig.DELETE_EMOJI_ID, 1, "delete:",
//                    "delete:"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        sGrid = new GridView(getActivity());
        sGrid.setNumColumns(KJEmojiConfig.COLUMNS_MOIN);
        adapter = new MoinEmojiGridAdapter(getActivity(), datas);
        sGrid.setAdapter(adapter);
        sGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Emojicon mEmojicon = (Emojicon) parent.getAdapter()
                        .getItem(position);
                if (position == 0 && mEmojicon.getValue() == 0) {
                    // 点击了相册
                    Intent intent = new Intent(getActivity(), SelectPhotoActivity.class);
                    intent.putExtra(DiscoveryConstants.FROM, StringUtil.FROM_CHAT);
                    getActivity().startActivity(intent);
                } else {
//                EditText editText = (EditText) getActivity().findViewById(
//                        R.id.emoji_titile_input);
                    if (listener != null) {
                        listener.onEmojiClick(mEmojicon);
                        android.util.Log.i(MoinEmojiPageFragment.class.getSimpleName(),
                                "MoinEmoji MoinEmojiPageFragment listener.onEmojiClick " + mEmojicon.toString());
                    }
                    // 将表情加入到editText中去
//                InputHelper.input2OSC(editText, mEmojicon);
                }
            }
        });
        sGrid.setSelector(new ColorDrawable(android.R.color.transparent));
        return sGrid;
    }

    public GridView getRootView() {
        return sGrid;
    }
}

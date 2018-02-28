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

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 表情页适配器（FragmentPagerAdapter的好处是fragment常驻内存，对于要求效率而页卡很少的表情控件最合适）
 * 
 * @author kymjs (http://www.kymjs.com)
 * 
 */
public class MoinEmojiPagerAdapter extends FragmentPagerAdapter {

    private OnEmojiClickListener listener;

    public MoinEmojiPagerAdapter(FragmentManager fm, int tabCount,
                                 OnEmojiClickListener l) {
        super(fm);
        MoinEmojiFragment.EMOJI_TAB_CONTENT = tabCount;
        listener = l;
    }

    public MoinEmojiPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public MoinEmojiPageFragment getItem(int index) {
        if (MoinEmojiFragment.EMOJI_TAB_CONTENT > 1) {
            return new MoinEmojiPageFragment(index, index, listener);
        } else {
            return new MoinEmojiPageFragment(index, 3, listener);
        }
    }

    /**
     * 显示模式：如果只有一种Emoji表情，则像QQ表情一样左右滑动分页显示<br>
     * 如果有多种Emoji表情，每页显示一种，Emoji筛选时上下滑动筛选。
     */
    @Override
    public int getCount() {
        if (MoinEmojiFragment.EMOJI_TAB_CONTENT > 1) {
            return MoinEmojiFragment.EMOJI_TAB_CONTENT;
        } else {
            // 采用进一法取小数
            return (DisplayRules.getAllByType(3).size() - 1 + KJEmojiConfig.COUNT_IN_PAGE_MOIN)
                    / KJEmojiConfig.COUNT_IN_PAGE_MOIN;
        }
    }
}

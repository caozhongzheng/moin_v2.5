package com.moinapp.wuliao.ui.a2zletter;

import android.widget.BaseAdapter;

/**
 * Created by moying on 15/7/8.
 * 带有侧边字母列表的listView适配器
 */
public abstract class LetterBaseAdapter extends BaseAdapter {
    /** 字母表头部 **/
    protected static final char HEADER = '+';
    /** 字母表尾部 **/
    protected static final char FOOTER = '#';

    /**
     * 带有侧边字母列表的listView适配器
     *
     * @return true 隐藏, false 不隐藏
     */
    public abstract boolean hideLetterNotMatch();

    /**
     * 是否需要隐藏没有匹配到的字母
     *
     * @return position
     */
    public abstract int getIndex(char letter);
}

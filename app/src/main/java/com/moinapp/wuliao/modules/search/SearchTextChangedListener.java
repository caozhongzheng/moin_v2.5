package com.moinapp.wuliao.modules.search;

/**
 * 文字变化时的实时搜索
 * Created by liujiancheng on 15/11/26.
 */
public interface SearchTextChangedListener {
    public void onSearchTextChanged(String searchText);
    public void onSearchTextInvalid();
}

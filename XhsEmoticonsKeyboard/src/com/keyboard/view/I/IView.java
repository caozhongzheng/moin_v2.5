package com.keyboard.view.I;

import android.view.View;

import com.keyboard.bean.EmoticonBean;

public interface IView {
    void onItemClick(EmoticonBean bean);
    boolean onItemLongClick(int position, View converView, EmoticonBean bean);
    void onItemDisplay(EmoticonBean bean);
    void onPageChangeTo(int position);
}

package com.moinapp.wuliao.listener;

/**
 * Created by liujiancheng on 15/9/7.
 */
/**
 * Created by moying on 15/5/8.
 * callback, 用于ui层和业务逻辑处理层Manager交互
 */
public interface IListener {
    void onSuccess(Object obj);
    void onErr(Object obj);
    void onNoNetwork();
}

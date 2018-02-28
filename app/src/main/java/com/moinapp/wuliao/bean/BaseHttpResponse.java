package com.moinapp.wuliao.bean;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/9/7.
 * 基本的http请求后返回的结果类
 */
public class BaseHttpResponse implements Serializable {
    /**
     * 请求的结果，1是成功，0，失败
     */
    private int result = -1;

    /**
     * error code，一般用于ui显示不同的失败原因
     */
    private int error;

    /**
     * 分页加载，1:最后一页, 0:未完待续
     */
    private int isLast;

    public void setResult(int result) { this.result = result; }
    public int getResult() { return this.result; }
    public void setError(int error) { this.error = error; }
    public int getError() { return this.error; }
    public boolean isLast() {
        return this.isLast == 1;
    }

    @Override
    public String toString() {
        return "BaseHttpResponse{" +
                "result=" + result +
                ", error=" + error +
                ", isLast=" + isLast +
                '}';
    }
}

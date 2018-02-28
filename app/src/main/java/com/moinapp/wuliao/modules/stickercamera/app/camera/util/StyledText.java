package com.moinapp.wuliao.modules.stickercamera.app.camera.util;

/**
 * 给@好友时标注用户名及其起始位置用
 * Created by moying on 15/9/28.
 */
public class StyledText {
    int start;
    int length;
    String username;

    public StyledText(int start, int length, String username) {
        this.start = start;
        this.length = length;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }

    public int getEnd() {
        return start + length;
    }

    @Override
    public String toString() {
        return "StyledText{" +
                "start=" + start +
                ", length=" + length +
                ", username='" + username + '\'' +
                '}';
    }
}

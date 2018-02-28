package com.moinapp.wuliao.bean;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/5/12.
 * 最基本的image对象，包括url，大小宽高等等
 */
public class BaseImage implements Serializable {
    private String fileType;
    private String uri;
    private int width;
    private int height;
    private int size;
    private String name;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BaseImage{" +
                "uri='" + uri + '\'' +
                '}';
    }
}

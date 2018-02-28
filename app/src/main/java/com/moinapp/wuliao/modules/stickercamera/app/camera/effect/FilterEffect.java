package com.moinapp.wuliao.modules.stickercamera.app.camera.effect;


import com.moinapp.wuliao.modules.stickercamera.app.camera.util.GPUImageFilterTools;

/**
 * 描述滤镜类[标题,类型,和参数]
 * @author tongqian.ni
 */
public class FilterEffect  {

    private String     title;
    private GPUImageFilterTools.FilterType type;
    private int        degree;

    /**
     * @param title
     * @param type
     * @param degree
     */
    public FilterEffect(String title, GPUImageFilterTools.FilterType type, int degree) {
        this.type = type;
        this.degree = degree;
        this.title = title;
    }


    public GPUImageFilterTools.FilterType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getDegree() {
        return degree;
    }

}

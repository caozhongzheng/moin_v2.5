package com.moinapp.wuliao.modules.stickercamera.app.camera;


import com.moinapp.wuliao.modules.stickercamera.app.camera.effect.FilterEffect;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.GPUImageFilterTools;

import java.util.ArrayList;
import java.util.List;

public class EffectService {

    private static EffectService mInstance;

    public static EffectService getInst() {
        if (mInstance == null) {
            synchronized (EffectService.class) {
                if (mInstance == null)
                    mInstance = new EffectService();
            }
        }
        return mInstance;
    }

    private EffectService() {
    }

    public List<FilterEffect> getLocalFilters() {
        List<FilterEffect> filters = new ArrayList<FilterEffect>();
        filters.add(new FilterEffect("原始", GPUImageFilterTools.FilterType.NORMAL, 0));//

        filters.add(new FilterEffect("淡蓝", GPUImageFilterTools.FilterType.ACV_DANLAN, 0));
        filters.add(new FilterEffect("复古", GPUImageFilterTools.FilterType.ACV_FUGU, 0));
        filters.add(new FilterEffect("晕影", GPUImageFilterTools.FilterType.VIGNETTE, 0));//晕影，形成黑色圆形边缘，突出中间图像的效果
        filters.add(new FilterEffect("傲娇", GPUImageFilterTools.FilterType.ACV_WENNUAN, 0));//温暖
        filters.add(new FilterEffect("暧昧", GPUImageFilterTools.FilterType.ACV_AIMEI, 0));
        filters.add(new FilterEffect("丐穷", GPUImageFilterTools.FilterType.GRAYSCALE, 0));//黑白
        filters.add(new FilterEffect("可爱", GPUImageFilterTools.FilterType.ACV_KEAI, 0));
        filters.add(new FilterEffect("怀旧", GPUImageFilterTools.FilterType.ACV_HUAIJIU, 0));
        filters.add(new FilterEffect("日系", GPUImageFilterTools.FilterType.ACV_RIXI, 0));
        filters.add(new FilterEffect("乳酸", GPUImageFilterTools.FilterType.BULGE_DISTORTION, 0));//鱼眼 凸起失真，鱼眼效果
        filters.add(new FilterEffect("翠花", GPUImageFilterTools.FilterType.LOOKUP_AMATORKA, 0));//流年
        filters.add(new FilterEffect("淡定", GPUImageFilterTools.FilterType.SEPIA, 0));//回忆//褐色（怀旧）
        filters.add(new FilterEffect("强烈", GPUImageFilterTools.FilterType.CONTRAST, 0));
        filters.add(new FilterEffect("耽美", GPUImageFilterTools.FilterType.BOX_BLUR, 0));//动感 盒状模糊
        filters.add(new FilterEffect("人妖", GPUImageFilterTools.FilterType.SHARPEN, 0));//锐化
        filters.add(new FilterEffect("高冷", GPUImageFilterTools.FilterType.ACV_GAOLENG, 0));
        filters.add(new FilterEffect("加强", GPUImageFilterTools.FilterType.ACV_MORENJIAQIANG, 0));
        filters.add(new FilterEffect("单色", GPUImageFilterTools.FilterType.MONOCHROME, 0));
        filters.add(new FilterEffect("色调曲线", GPUImageFilterTools.FilterType.TONE_CURVE, 0));
/*
        filters.add(new FilterEffect("暧昧", GPUImageFilterTools.FilterType.ACV_AIMEI, 0));
        filters.add(new FilterEffect("淡蓝", GPUImageFilterTools.FilterType.ACV_DANLAN, 0));
        filters.add(new FilterEffect("淡黄", GPUImageFilterTools.FilterType.ACV_DANHUANG, 0));
        filters.add(new FilterEffect("复古", GPUImageFilterTools.FilterType.ACV_FUGU, 0));
        filters.add(new FilterEffect("高冷", GPUImageFilterTools.FilterType.ACV_GAOLENG, 0));
        filters.add(new FilterEffect("怀旧", GPUImageFilterTools.FilterType.ACV_HUAIJIU, 0));
        filters.add(new FilterEffect("胶片", GPUImageFilterTools.FilterType.ACV_JIAOPIAN, 0));
        filters.add(new FilterEffect("可爱", GPUImageFilterTools.FilterType.ACV_KEAI, 0));
        filters.add(new FilterEffect("落寞", GPUImageFilterTools.FilterType.ACV_LOMO, 0));
        filters.add(new FilterEffect("加强", GPUImageFilterTools.FilterType.ACV_MORENJIAQIANG, 0));
        filters.add(new FilterEffect("暖心", GPUImageFilterTools.FilterType.ACV_NUANXIN, 0));
        filters.add(new FilterEffect("清新", GPUImageFilterTools.FilterType.ACV_QINGXIN, 0));
        filters.add(new FilterEffect("日系", GPUImageFilterTools.FilterType.ACV_RIXI, 0));
        filters.add(new FilterEffect("热情", GPUImageFilterTools.FilterType.SATURATION, 0));//饱和度

        filters.add(new FilterEffect("强烈", GPUImageFilterTools.FilterType.CONTRAST, 0));
//        filters.add(new FilterEffect("黑白", GPUImageFilterTools.FilterType.GRAYSCALE, 0));
//        filters.add(new FilterEffect("回忆", GPUImageFilterTools.FilterType.SEPIA, 0));
        filters.add(new FilterEffect("漫画", GPUImageFilterTools.FilterType.SOBEL_EDGE_DETECTION, 0));//Sobel边缘检测算法(白边，黑内容，有点漫画的反色效果)
        filters.add(new FilterEffect("THREE_X_THREE_CONVOLUTION", GPUImageFilterTools.FilterType.THREE_X_THREE_CONVOLUTION, 0));
        filters.add(new FilterEffect("岩画", GPUImageFilterTools.FilterType.FILTER_GROUP, 0));
        filters.add(new FilterEffect("3D浮雕", GPUImageFilterTools.FilterType.EMBOSS, 0));//浮雕效果，带有点3d的感觉
        filters.add(new FilterEffect("噪点", GPUImageFilterTools.FilterType.POSTERIZE, 0));//色调分离，形成噪点效果
        filters.add(new FilterEffect("伽马线", GPUImageFilterTools.FilterType.GAMMA, 0));
//        filters.add(new FilterEffect("高亮", GPUImageFilterTools.FilterType.BRIGHTNESS, 0));
        filters.add(new FilterEffect("反色", GPUImageFilterTools.FilterType.INVERT, 0));
        filters.add(new FilterEffect("色度", GPUImageFilterTools.FilterType.HUE, 0));
        filters.add(new FilterEffect("像素化", GPUImageFilterTools.FilterType.PIXELATION, 0));
//        filters.add(new FilterEffect("热情", GPUImageFilterTools.FilterType.SATURATION, 0));//饱和度
        filters.add(new FilterEffect("曝光", GPUImageFilterTools.FilterType.EXPOSURE, 0));
        filters.add(new FilterEffect("提亮阴影", GPUImageFilterTools.FilterType.HIGHLIGHT_SHADOW, 0));
        filters.add(new FilterEffect("单色", GPUImageFilterTools.FilterType.MONOCHROME, 0));
        filters.add(new FilterEffect("不透明", GPUImageFilterTools.FilterType.OPACITY, 0));
        filters.add(new FilterEffect("RGB", GPUImageFilterTools.FilterType.RGB, 0));
        filters.add(new FilterEffect("白平横", GPUImageFilterTools.FilterType.WHITE_BALANCE, 0));
        filters.add(new FilterEffect("晕影", GPUImageFilterTools.FilterType.VIGNETTE, 0));//晕影，形成黑色圆形边缘，突出中间图像的效果
        filters.add(new FilterEffect("色调曲线", GPUImageFilterTools.FilterType.TONE_CURVE, 0));
//        filters.add(new FilterEffect("BLEND_COLOR_BURN", GPUImageFilterTools.FilterType.BLEND_COLOR_BURN, 0));
//        filters.add(new FilterEffect("BLEND_COLOR_DODGE", GPUImageFilterTools.FilterType.BLEND_COLOR_DODGE, 0));
//        filters.add(new FilterEffect("BLEND_DARKEN", GPUImageFilterTools.FilterType.BLEND_DARKEN, 0));
//        filters.add(new FilterEffect("BLEND_DIFFERENCE", GPUImageFilterTools.FilterType.BLEND_DIFFERENCE, 0));
//        filters.add(new FilterEffect("BLEND_DISSOLVE", GPUImageFilterTools.FilterType.BLEND_DISSOLVE, 0));
//        filters.add(new FilterEffect("BLEND_EXCLUSION", GPUImageFilterTools.FilterType.BLEND_EXCLUSION, 0));
//        filters.add(new FilterEffect("BLEND_SOURCE_OVER", GPUImageFilterTools.FilterType.BLEND_SOURCE_OVER, 0));
//        filters.add(new FilterEffect("BLEND_HARD_LIGHT", GPUImageFilterTools.FilterType.BLEND_HARD_LIGHT, 0));
//        filters.add(new FilterEffect("BLEND_LIGHTEN", GPUImageFilterTools.FilterType.BLEND_LIGHTEN, 0));
//        filters.add(new FilterEffect("BLEND_ADD", GPUImageFilterTools.FilterType.BLEND_ADD, 0));
//        filters.add(new FilterEffect("BLEND_DIVIDE", GPUImageFilterTools.FilterType.BLEND_DIVIDE, 0));
//        filters.add(new FilterEffect("BLEND_MULTIPLY", GPUImageFilterTools.FilterType.BLEND_MULTIPLY, 0));
//        filters.add(new FilterEffect("BLEND_OVERLAY", GPUImageFilterTools.FilterType.BLEND_OVERLAY, 0));
//        filters.add(new FilterEffect("BLEND_SCREEN", GPUImageFilterTools.FilterType.BLEND_SCREEN, 0));
//        filters.add(new FilterEffect("BLEND_ALPHA", GPUImageFilterTools.FilterType.BLEND_ALPHA, 0));
//        filters.add(new FilterEffect("BLEND_COLOR", GPUImageFilterTools.FilterType.BLEND_COLOR, 0));
//        filters.add(new FilterEffect("BLEND_HUE", GPUImageFilterTools.FilterType.BLEND_HUE, 0));
//        filters.add(new FilterEffect("BLEND_SATURATION", GPUImageFilterTools.FilterType.BLEND_SATURATION, 0));
//        filters.add(new FilterEffect("BLEND_LUMINOSITY", GPUImageFilterTools.FilterType.BLEND_LUMINOSITY, 0));
//        filters.add(new FilterEffect("BLEND_LINEAR_BURN", GPUImageFilterTools.FilterType.BLEND_LINEAR_BURN, 0));
//        filters.add(new FilterEffect("BLEND_SOFT_LIGHT", GPUImageFilterTools.FilterType.BLEND_SOFT_LIGHT, 0));
//        filters.add(new FilterEffect("BLEND_SUBTRACT", GPUImageFilterTools.FilterType.BLEND_SUBTRACT, 0));
//        filters.add(new FilterEffect("BLEND_CHROMA_KEY", GPUImageFilterTools.FilterType.BLEND_CHROMA_KEY, 0));
//        filters.add(new FilterEffect("BLEND_NORMAL", GPUImageFilterTools.FilterType.BLEND_NORMAL, 0));
//        filters.add(new FilterEffect("流年", GPUImageFilterTools.FilterType.LOOKUP_AMATORKA, 0));
        filters.add(new FilterEffect("高斯模糊", GPUImageFilterTools.FilterType.GAUSSIAN_BLUR, 0));
        filters.add(new FilterEffect("黑白网", GPUImageFilterTools.FilterType.CROSSHATCH, 0));//交叉线阴影，形成黑白网状画面
//        filters.add(new FilterEffect("动感", GPUImageFilterTools.FilterType.BOX_BLUR, 0));
        filters.add(new FilterEffect("CGA滤镜", GPUImageFilterTools.FilterType.CGA_COLORSPACE, 0));//CGA色彩滤镜，形成黑、浅蓝、紫色块的画面
        filters.add(new FilterEffect("黑白模糊", GPUImageFilterTools.FilterType.DILATION, 0));//扩展边缘模糊，变黑白
        filters.add(new FilterEffect("水粉画", GPUImageFilterTools.FilterType.KUWAHARA, 0));//桑原(Kuwahara)滤波,水粉画的模糊效果；处理时间比较长，慎用
        filters.add(new FilterEffect("色彩模糊", GPUImageFilterTools.FilterType.RGB_DILATION, 0));//RGB扩展边缘模糊，有色彩
        filters.add(new FilterEffect("素描", GPUImageFilterTools.FilterType.SKETCH, 0));
        filters.add(new FilterEffect("黑线描边", GPUImageFilterTools.FilterType.TOON, 0));//卡通效果（黑色粗线描边）
        filters.add(new FilterEffect("粗线描边", GPUImageFilterTools.FilterType.SMOOTH_TOON, 0));//相比上面的效果更细腻，上面是粗旷的画风
        filters.add(new FilterEffect("水晶球", GPUImageFilterTools.FilterType.GLASS_SPHERE, 0));//水晶球效果
        filters.add(new FilterEffect("朦胧", GPUImageFilterTools.FilterType.HAZE, 0));//朦胧加暗
        filters.add(new FilterEffect("拉普拉斯", GPUImageFilterTools.FilterType.LAPLACIAN, 0));
        filters.add(new FilterEffect("黑白点", GPUImageFilterTools.FilterType.NON_MAXIMUM_SUPPRESSION, 0));//非最大抑制，只显示亮度最高的像素，其他为黑
        filters.add(new FilterEffect("折射球", GPUImageFilterTools.FilterType.SPHERE_REFRACTION, 0));
        filters.add(new FilterEffect("漩涡", GPUImageFilterTools.FilterType.SWIRL, 0));//漩涡，中间形成卷曲的画面
        filters.add(new FilterEffect("泼墨", GPUImageFilterTools.FilterType.WEAK_PIXEL_INCLUSION, 0));
        filters.add(new FilterEffect("色彩替换", GPUImageFilterTools.FilterType.FALSE_COLOR, 0));//色彩替换（替换亮部和暗部色彩）
        filters.add(new FilterEffect("色彩平衡", GPUImageFilterTools.FilterType.COLOR_BALANCE, 0));
*/
        return filters;
    }

}

package com.moinapp.wuliao.util;

import android.content.Context;
import android.util.Xml;

import com.google.gson.Gson;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.sticker.model.StickerTextInfo;
import com.moinapp.wuliao.modules.stickercamera.app.camera.util.StickerUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * XmlUtil
 * 解析本地xml,但不一定适合你的xml格式
 * assets/wxemoticons.zip/wxemoticons.xml
 */

public class XmlUtil {
    private static ILogger MyLog = LoggerFactory.getLogger("XmlUtil");
    Context mContext;

    public XmlUtil(Context context) {
        this.mContext = context;
    }

    public InputStream getXmlFromAssets(String xmlName) {
        try {
            InputStream inStream = this.mContext.getResources().getAssets().open(xmlName);
            return inStream;
        } catch (IOException e) {
            MyLog.e(e);
            e.printStackTrace();
        }
        return null;
    }

    public InputStream getXmlFromSD(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream inStream = new FileInputStream(file);
                return inStream;
            } else {
            }
        } catch (IOException e) {
            MyLog.e(e);
            e.printStackTrace();
        }
        return null;
    }


    public EmoticonSetBean ParserXml(InputStream inStream, String emoticonFilePath) {

        String arrayParentKey = "EmoticonBean";
        EmoticonSetBean emoticonSetBean = new EmoticonSetBean();
        ArrayList<EmoticonBean> emoticonList = new ArrayList<EmoticonBean>();
        emoticonSetBean.setEmoticonList(emoticonList);
        EmoticonBean emoticonBeanTemp = null;

//        String emoticonFilePath = Environment.getExternalStorageDirectory() + "/wxemoticons/" ;
        // wxemoticons 是新表情的zip名
//        String emoticonFilePath = BitmapUtil.BITMAP_EMOJI + "wxemoticons/wxemoticons/";

        boolean isChildCheck = false;

        String bubbleTextKey = "bubbleText";
        boolean isBubbleTextCheck = false;
        StickerTextInfo stickerTextInfo = null;
        Gson mGson = null;

        if (null != inStream) {
            XmlPullParser pullParser = Xml.newPullParser();
            try {
                pullParser.setInput(inStream, "UTF-8");
                int event = pullParser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {

                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            String skeyName = pullParser.getName();

                            /**
                             * EmoticonBeans data
                             */
                            if (isChildCheck && emoticonBeanTemp != null) {
                                if (skeyName.equals("eventType")) {
                                    try {
                                        String value = pullParser.nextText();
                                        emoticonBeanTemp.setEventType(Integer.parseInt(value));
                                        MyLog.i("eventType=" + value);
                                    } catch (NumberFormatException e) {
                                        MyLog.e(skeyName + "/" + e);
                                    }
                                } else if (skeyName.equals("id")) {
                                    try {
                                        String value = pullParser.nextText();
                                        emoticonBeanTemp.setId(value);
                                        // TODO 临时这么设置一下
//                                        emoticonBeanTemp.setStickerId(emoticonBeanTemp.getParentId() + "_" + value);
                                        MyLog.i("id=" + value);
                                    } catch (NumberFormatException e) {
                                        MyLog.e(skeyName + "/" + e);
                                    }

                                } else if (skeyName.equals("parentid")) {
                                    try {
                                        String value = pullParser.nextText();
                                        emoticonBeanTemp.setParentId(value);
                                        MyLog.i("parentid=" + value);
                                    } catch (NumberFormatException e) {
                                        MyLog.e(skeyName + "/" + e);
                                    }

                                } else if (skeyName.equals("stickerid")) {//v2.7 增加贴纸id
                                    try {
                                        String value = pullParser.nextText();
                                        emoticonBeanTemp.setStickerId(value);
                                        MyLog.i("stickerid=" + value);
                                    } catch (NumberFormatException e) {
                                        MyLog.e(skeyName + "/" + e);
                                    }

                                }  else if (skeyName.equals("stickType")) {
                                    String value = pullParser.nextText();
                                    emoticonBeanTemp.setStickType(Integer.parseInt(value));
                                    MyLog.i("stickType=" + value);
                                } else if (skeyName.equals("tag")) {
                                    String value = pullParser.nextText();
                                    emoticonBeanTemp.setTags(value);
                                    MyLog.i("tag=" + value);
                                } else if (skeyName.equals("useStat")) {
                                    try {
                                        String value = pullParser.nextText();
                                        emoticonBeanTemp.setUseStat(Integer.parseInt(value));
                                    } catch (NumberFormatException e) {
                                        MyLog.e(skeyName + "/" + e);
                                    }
                                } else if (skeyName.equals("zoom")) {
                                    try {
                                        String value = pullParser.nextText();
                                        emoticonBeanTemp.setZoom(Integer.parseInt(value));
                                    } catch (NumberFormatException e) {
                                        MyLog.e(skeyName + "/" + e);
                                    }
                                } else if (skeyName.equals("iconUri")) {
                                    String value = pullParser.nextText();
//                                    emoticonBeanTemp.setIconUri("file://" + emoticonFilePath + "/" + emoticonBeanTemp.getParentId() + "/" + value);
                                    emoticonBeanTemp.setIconUri("file://" + emoticonFilePath + "/" + emoticonBeanTemp.getStickerId() + StickerUtils.STICKER_ICON_EXTENSION);
                                    MyLog.i("iconUri=" + value);
                                } else if (skeyName.equals("iconUrl")) {
                                    String value = pullParser.nextText();
                                    emoticonBeanTemp.setIconUrl(value);
                                    MyLog.i("iconUrl=" + value);
                                } else if (skeyName.equals("gifUri")) {
                                    String value = pullParser.nextText();
//                                    emoticonBeanTemp.setGifUri("file://" + emoticonFilePath + "/" + emoticonBeanTemp.getParentId() + "/" + value);
                                    emoticonBeanTemp.setGifUri("file://" + emoticonFilePath + "/" + emoticonBeanTemp.getStickerId() + StickerUtils.STICKER_PIC_EXTENSION);
                                    MyLog.i("gifUri=" + value);
                                } else if (skeyName.equals("gifUrl")) {
                                    String value = pullParser.nextText();
                                    emoticonBeanTemp.setGifUrl(value);
                                    MyLog.i("gifUrl=" + value);
                                } else if (skeyName.equals("content")) {
                                    String value = pullParser.nextText();
                                    emoticonBeanTemp.setContent(value);
                                    MyLog.i("content=" + value);
                                } else if (isBubbleTextCheck && stickerTextInfo != null) {
                                    if (skeyName.equals("text")) {
                                        String value = pullParser.nextText();
                                        stickerTextInfo.setText(value);
                                        MyLog.i("text=" + value);
                                    } else if (skeyName.equals("x")) {
                                        String value = pullParser.nextText();
                                        try {
                                            stickerTextInfo.setX(Integer.parseInt(value));
                                            MyLog.i("x=" + value);
                                        } catch (NumberFormatException e) {
                                            MyLog.e(skeyName + "/" + e);
                                        }
                                    } else if (skeyName.equals("y")) {
                                        String value = pullParser.nextText();
                                        try {
                                            stickerTextInfo.setY(Integer.parseInt(value));
                                            MyLog.i("y=" + value);
                                        } catch (NumberFormatException e) {
                                            MyLog.e(skeyName + "/" + e);
                                        }
                                    } else if (skeyName.equals("width")) {
                                        String value = pullParser.nextText();
                                        try {
                                            stickerTextInfo.setWidth(Integer.parseInt(value));
                                            MyLog.i("width=" + value);
                                        } catch (NumberFormatException e) {
                                            MyLog.e(skeyName + "/" + e);
                                        }
                                    } else if (skeyName.equals("height")) {
                                        String value = pullParser.nextText();
                                        try {
                                            stickerTextInfo.setHeight(Integer.parseInt(value));
                                            MyLog.i("height=" + value);
                                        } catch (NumberFormatException e) {
                                            MyLog.e(skeyName + "/" + e);
                                        }
                                    }
                                }
                            }
                            /**
                             * EmoticonSet data
                             */
                            else {
                                try {
                                    if (skeyName.equals("name")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setName(value);
                                        MyLog.i("Set name=" + value);
                                    }  else if (skeyName.equals("parentId")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setId(value);
                                        MyLog.i("Set parentId=" + value);
                                    }  else if (skeyName.equals("stickType")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setStickType(Integer.parseInt(value));
                                        MyLog.i("Set stickType=" + value);
                                    }  else if (skeyName.equals("line")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setLine(Integer.parseInt(value));
                                        MyLog.i("Set line=" + value);
                                    } else if (skeyName.equals("row")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setRow(Integer.parseInt(value));
                                        MyLog.i("Set row=" + value);
                                    }  else if (skeyName.equals("iconUri")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setIconUri(value);
                                        MyLog.i("Set iconUri=" + value);
                                    }  else if (skeyName.equals("iconUrl")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setIconUrl(value);
                                        MyLog.i("Set iconUrl=" + value);
                                    }  else if (skeyName.equals("iconName")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setIconName(value);
                                        MyLog.i("Set iconName=" + value);
                                    } else if (skeyName.equals("isShowDelBtn")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setShowDelBtn(Integer.parseInt(value) == 1);
                                        MyLog.i("Set isShowDelBtn=" + value);
                                    } else if (skeyName.equals("itemPadding")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setItemPadding(Integer.parseInt(value));
                                        MyLog.i("Set itemPadding=" + value);
                                    } else if (skeyName.equals("horizontalSpacing")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setHorizontalSpacing(Integer.parseInt(value));
                                        MyLog.i("Set horizontalSpacing=" + value);
                                    } else if (skeyName.equals("verticalSpacing")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setVerticalSpacing(Integer.parseInt(value));
                                        MyLog.i("Set verticalSpacing=" + value);
                                    } else if (skeyName.equals("updateAt")) {
                                        String value = pullParser.nextText();
                                        emoticonSetBean.setUpdateTime(Long.parseLong(value));
                                        MyLog.i("Set updateAt=" + value);
                                    }
                                } catch (NumberFormatException e) {
//                                    MyLog.e(e);
                                } catch (Exception e) {
//                                    MyLog.e(e);
                                }
                            }

                            if (skeyName.equals(arrayParentKey)) {
                                isChildCheck = true;
                                emoticonBeanTemp = new EmoticonBean();
                                emoticonBeanTemp.setParentId(emoticonSetBean.getId());
                                emoticonBeanTemp.setParentStickType(emoticonSetBean.getStickType());
                                emoticonBeanTemp.setId("-1");
                                emoticonBeanTemp.setStickType(1);
                                emoticonBeanTemp.setEventType(2);
                                emoticonBeanTemp.setContent("");
                                emoticonBeanTemp.setTags("");
                            }
                            if (skeyName.equals(bubbleTextKey)) {
                                isBubbleTextCheck = true;
                                stickerTextInfo = new StickerTextInfo();
                                if (mGson == null) {
                                    mGson = new Gson();
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            String ekeyName = pullParser.getName();
                            if (isChildCheck) {
                                if (ekeyName.equals(arrayParentKey)) {
                                    isChildCheck = false;
                                    if (StringUtil.isNullOrEmpty(emoticonBeanTemp.getStickerId())) {
                                        emoticonBeanTemp.setIconUri("file://" + emoticonFilePath + "/" + emoticonBeanTemp.getParentId() + "/"
                                                + emoticonBeanTemp.getParentId() + "_" + emoticonBeanTemp.getId() + StickerUtils.STICKER_ICON_EXTENSION);
                                        emoticonBeanTemp.setGifUri("file://" + emoticonFilePath + "/" + emoticonBeanTemp.getParentId() + "/"
                                                + emoticonBeanTemp.getParentId() + "_" + emoticonBeanTemp.getId() + StickerUtils.STICKER_PIC_EXTENSION);
                                    } else {
                                        emoticonBeanTemp.setIconUri("file://" + emoticonFilePath + "/" + emoticonBeanTemp.getStickerId() + StickerUtils.STICKER_ICON_EXTENSION);
                                        emoticonBeanTemp.setGifUri("file://" + emoticonFilePath + "/" + emoticonBeanTemp.getStickerId() + StickerUtils.STICKER_PIC_EXTENSION);
                                    }
                                    if (emoticonBeanTemp.getZoom() <= 0 || emoticonBeanTemp.getZoom() > 100) {
                                        emoticonBeanTemp.setZoom(65);
                                    }
                                    emoticonList.add(emoticonBeanTemp);
                                } else if (ekeyName.equals(bubbleTextKey)) {
                                    isBubbleTextCheck = false;
                                    emoticonBeanTemp.setBubbleText(mGson.toJson(stickerTextInfo));
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    event = pullParser.next();
                }
                return emoticonSetBean;
            } catch (XmlPullParserException e) {
                MyLog.e(e);
                e.printStackTrace();
            } catch (IOException e) {
                MyLog.e(e);
                e.printStackTrace();
            }
        }
        return emoticonSetBean;
    }
}

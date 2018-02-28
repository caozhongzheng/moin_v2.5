package com.moinapp.wuliao.util;

import com.google.gson.Gson;
import com.moinapp.wuliao.AppException;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DoubleConverter;
import com.thoughtworks.xstream.converters.basic.FloatConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.LongConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * xml解析工具类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年9月27日 下午2:04:19
 * 
 */

public class XmlUtils {

//    private final static String TAG = XmlUtils.class.getSimpleName();
    private static final ILogger MyLog = LoggerFactory.getLogger(XmlUtils.class.getSimpleName());

    private static Gson mGson = new Gson();
    /**
     * 将一个xml流转换为bean实体类
     * 
     * @param type
     * @param is
     * @return
     * @throws AppException
     */
    @SuppressWarnings("unchecked")
    public static <T> T toBean(Class<T> type, InputStream is) {
        XStream xmStream = new XStream(new DomDriver("UTF-8"));
        // 设置可忽略为在javabean类中定义的界面属性
        xmStream.ignoreUnknownElements();
        xmStream.registerConverter(new MyIntCoverter());
        xmStream.registerConverter(new MyLongCoverter());
        xmStream.registerConverter(new MyFloatCoverter());
        xmStream.registerConverter(new MyDoubleCoverter());
        xmStream.processAnnotations(type);
        T obj = null;
        try {
            obj = (T) xmStream.fromXML(is);
        } catch (Exception e) {
            MyLog.e(e);
//            TLog.log(TAG, "解析xml发生异常：" + e.getMessage());
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    MyLog.e(e);
//                    TLog.log(TAG, "关闭流出现异常：" + e.getMessage());
                }
            }
        }
        return obj;
    }
    
    public static <T> T toBean(Class<T> type, byte[] bytes) {
        if (bytes == null) return null;
        return toBean(type, new ByteArrayInputStream(bytes));
    }

    /**
     * 将一个json字节数组转换为bean实体类
     *
     * @param type
     * @param bytes
     * @return
     * @throws AppException
     */
    @SuppressWarnings("unchecked")
    public static <T> T JsontoBean(Class<T> type,  byte[] bytes) {
        if (bytes == null) return null;
        return JsontoBean(type, new String(bytes));
    }

    public static <T> T JsontoBean(Class<T> type,  String s) {
        if (s == null) return null;
        T object = null;
        try {
            object = mGson.fromJson(s, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static <T> T JsontoBean(Class<T> type,  InputStream is) {
        if (is == null) return null;

        String s = null;
        T object = null;
        try {
            s = inputStream2String(is);
            if (s == null) return null;
            object = mGson.fromJson(s, type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    private static class MyIntCoverter extends IntConverter {

        @Override
        public Object fromString(String str) {
            int value;
            try {
                value = (Integer) super.fromString(str);
            } catch (Exception e) {
                value = 0;
            }
            return value;
        }

        @Override
        public String toString(Object obj) {
            return super.toString(obj);
        }
    }

    private static class MyLongCoverter extends LongConverter {
        @Override
        public Object fromString(String str) {
            long value;
            try {
                value = (Long) super.fromString(str);
            } catch (Exception e) {
                value = 0;
            }
            return value;
        }

        @Override
        public String toString(Object obj) {
            return super.toString(obj);
        }
    }

    private static class MyFloatCoverter extends FloatConverter {
        @Override
        public Object fromString(String str) {
            float value;
            try {
                value = (Float) super.fromString(str);
            } catch (Exception e) {
                value = 0;
            }
            return value;
        }

        @Override
        public String toString(Object obj) {
            return super.toString(obj);
        }
    }

    private static class MyDoubleCoverter extends DoubleConverter {
        @Override
        public Object fromString(String str) {
            double value;
            try {
                value = (Double) super.fromString(str);
            } catch (Exception e) {
                value = 0;
            }
            return value;
        }

        @Override
        public String toString(Object obj) {
            return super.toString(obj);
        }
    }

    final static int BUFFER_SIZE = 4096;

    // 将InputStream转换成String
    public static String inputStream2String(InputStream in) throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        byte[] data = new byte[BUFFER_SIZE];

        int count = -1;

        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)

            outStream.write(data, 0, count);

        data = null;

        return new String(outStream.toByteArray(), "utf-8");
    }
}

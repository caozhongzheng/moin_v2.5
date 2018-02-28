package com.moinapp.wuliao.commons.gif;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by liujiancheng on 15/6/9.
 */
public class GifUtils {
    public static final ILogger MyLog = LoggerFactory.getLogger(GifUtils.class.getSimpleName());

    /**
     * 图片缓存的核心类
     */
    private LruCache<String, GifDrawable> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 线程池的线程数量，默认为1
     */
    private int mThreadCount = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.FIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTasks;
    /**
     * 轮询的线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHander;

    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private Handler mHandler;

    /**
     * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     */
    private volatile Semaphore mPoolSemaphore;

    private static GifUtils mInstance;

    /**
     * 队列的调度方式
     */
    public enum Type {
        FIFO, LIFO
    }

    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static GifUtils getInstance() {
        return getInstance(1, Type.LIFO);
    }

    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static GifUtils getInstance(int threadCount, Type type) {

        if (mInstance == null) {
            synchronized (GifUtils.class) {
                if (mInstance == null) {
                    mInstance = new GifUtils(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    private GifUtils(int threadCount, Type type) {
        init(threadCount, type);
    }

    private void init(int threadCount, Type type) {
        // loop thread
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                mPoolThreadHander = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                // 释放一个信号量
                mSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, GifDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, GifDrawable value) {
                if(value != null) {
                    Bitmap tmp = value.getCurrentFrame();
                    MyLog.i("GIF width*height=" + tmp.getRowBytes() +"*"+ tmp.getHeight());
                    return tmp.getRowBytes() * tmp.getHeight();
                }
                return 1000000;
//                return value.getRowBytes() * value.getHeight();
            }

        };

        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<Runnable>();
        mType = type == null ? Type.LIFO : type;

    }


    public void loadImage(final String path, final ImageView imageView) {
        loadImage(path, imageView, null);
    }

    /**
     * 加载图片
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView, final IListener callback) {
        if (imageView == null) return;
        // set tag
        imageView.setTag(path);
        // UI线程
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    ImageView imageView = holder.imageView;
                    GifDrawable bm = holder.gifDrawable;
                    String path = holder.path;
                    if (imageView.getTag().toString().equals(path)) {
                        imageView.setImageDrawable(bm);
                        if(callback != null) {
                            callback.onSuccess(bm);
                        }
                    }
                }
            };
        }

        GifDrawable bm = getBitmapFromLruCache(path);
        if (bm != null) {
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.gifDrawable = bm;
            holder.imageView = imageView;
            holder.path = path;
            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {

                    GifDrawable gifFromPath = null;
                    try {
                        gifFromPath = new GifDrawable(path);
//
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    addBitmapToLruCache(path, gifFromPath);
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.gifDrawable = getBitmapFromLruCache(path);
                    holder.imageView = imageView;
                    holder.path = path;
                    Message message = Message.obtain();
                    message.obj = holder;
                    // Log.e("TAG", "mHandler.sendMessage(message);");
                    mHandler.sendMessage(message);
                    mPoolSemaphore.release();
                }
            });
        }

    }

    /**
     * 添加一个任务
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        try {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHander == null)
                mSemaphore.acquire();
        } catch (InterruptedException e) {
        }
        mTasks.add(runnable);

        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    /**
     * 取出一个任务
     *
     * @return
     */
    private synchronized Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTasks.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTasks.removeLast();
        }
        return null;
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private GifDrawable getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * 往LruCache中添加一张图片
     *
     * @param key
     * @param gifDrawable
     */
    private void addBitmapToLruCache(String key, GifDrawable gifDrawable) {
        if (getBitmapFromLruCache(key) == null) {
            if (gifDrawable != null)
                mLruCache.put(key, gifDrawable);
        }
    }

    private class ImgBeanHolder {
        GifDrawable gifDrawable;
        ImageView imageView;
        String path;
    }

    //*********************************************************************************************************
    public static GifDrawable gifFromPath;
    /**
     * 这个方法是利用开源框架gifdrawable库在c代码层decode并显示的，解决oom问题
     * @param imageView
     * @param file
     */
    public static void displayGif(ImageView imageView, File file) {
//        GifDrawable gifFromPath;
        try {
            gifFromPath = new GifDrawable(file);
            imageView.setImageDrawable(gifFromPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public static boolean isGifFormat(String path) {
        boolean result;
        GifDrawable gifFromPath = null;
        try {
            gifFromPath = new GifDrawable(path);
            if (gifFromPath != null) {
                gifFromPath.recycle();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}

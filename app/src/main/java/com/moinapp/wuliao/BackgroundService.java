package com.moinapp.wuliao;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.db.DBHelper;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.bean.MoinBean;
import com.moinapp.wuliao.bean.MultiClickEvent;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.discovery.DiscoveryManager;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.modules.discovery.model.LikeCosplay;
import com.moinapp.wuliao.modules.discovery.model.LikeTopic;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.modules.mission.MissionConstants;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerConstants;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by moying on 16/3/31.
 */
public class BackgroundService extends Service {
    private static final ILogger MyLog = LoggerFactory.getLogger(BackgroundService.class.getSimpleName());

    private PendingIntent mPendingIntent;

    public static final String IMMEDIATE_PERIOD_CHECK_ACTION = "com.moinapp.wuliao.BackgroundService.immediatePeriodCheck";
    public static final String REQUEST_PERIOD_CHECK_ACTION = "com.moinapp.wuliao.BackgroundService.requestPeriodCheck";
    private static final String SELF_PERIOD_CHECK_ACTION = "com.moinapp.wuliao.BackgroundService.selfPeriodCheck";

    private static long CHECK_DELAY_SHORT = 2 * DateUtils.SECOND_IN_MILLIS;
    private static long CHECK_DELAY_LONG = DateUtils.HOUR_IN_MILLIS;
    private static long CHECK_DELAY = CHECK_DELAY_SHORT;
    private static HashMap<String, Integer> CosplayMap = new HashMap<>();
    private static HashMap<String, Integer> TopicMap = new HashMap<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.i(" onStartCommand intent=" + intent + ", action=" + (intent == null ? "null" : intent.getAction()));

        if (intent == null) {
            return START_STICKY;
        }

        String action = intent.getAction();

        if (action == null) {
            return START_STICKY;
        }

        if (REQUEST_PERIOD_CHECK_ACTION.equals(action)) {
            MyLog.i(" onStartCommand register eventbus" );
            EventBus.getDefault().register(this);
            long now = System.currentTimeMillis();
            if (Math.abs(now - StickPreference.getInstance().getLastPeriodCheck()) >= CHECK_DELAY) {
                processAction(action, true);
            }
        } else if (IMMEDIATE_PERIOD_CHECK_ACTION.equals(action) || SELF_PERIOD_CHECK_ACTION.equals(action)) {
            processAction(action, true);
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        EventBus.getDefault().register(this);
        MyLog.i("onBind register eventbus");
        return null;
    }

    /**
     * 接收点赞事件
     */
    public void onEvent(MultiClickEvent event) {
        MyLog.i(" multiClick oriNum=" + event.builder.getOriginNum() + ", clkNum=" + event.builder.getClickNum()/* + ", obj=" + event.builder.getObject()*/);
        if (CHECK_DELAY != CHECK_DELAY_SHORT) {
            MyLog.i("short CHECK_DELAY = 5s , and process upload after 5s");
            changeDelay(0);
            processAction(SELF_PERIOD_CHECK_ACTION, false);
        }
        if (event.builder.getObject() != null) {
            Object object = event.builder.getObject();
            if (object instanceof CosplayInfo) {
                addItem((CosplayInfo) object);
            } else if (object instanceof TagPop) {
                addItem((TagPop) object);
            }
        }

    }

    /**
     * 调整定期检查时间间隔
     */
    private void changeDelay(int i) {
        if (i == 1) {
            CHECK_DELAY = CHECK_DELAY_LONG;
            MyLog.i("longer CHECK_DELAY = 1hour when empty");
        } else if (i == 0) {
            CHECK_DELAY = CHECK_DELAY_SHORT;
            MyLog.i("short CHECK_DELAY = 2s when unEmpty");
        }
    }

    /**
     * 调整定期检查时间间隔
     */
    private void changeDelayIfEmpty() {
        if (TopicMap.isEmpty() && CosplayMap.isEmpty()) {
            changeDelay(1);
        } else {
            changeDelay(0);
        }
    }

    /**
     * 话题点赞数+1
     */
    private void addItem(TagPop tagPop) {
        if (tagPop == null || StringUtil.isNullOrEmpty(tagPop.getTagPopId())) {
            return;
        }
        String id = tagPop.getTagPopId();
        if (TopicMap.containsKey(id)) {
            int num = TopicMap.get(id);
            TopicMap.put(id, num + 1);
        } else {
            TopicMap.put(id, 1);
        }
        MyLog.i(id + " ,topic num= " + TopicMap.get(id));
    }

    /**
     * 图片点赞数+1
     */
    private void addItem(CosplayInfo cosplayInfo) {
        if (cosplayInfo == null || StringUtil.isNullOrEmpty(cosplayInfo.getUcid())) {
            return;
        }
        String id = cosplayInfo.getUcid();
        if (CosplayMap.containsKey(id)) {
            int num = CosplayMap.get(id);
            CosplayMap.put(id, num + 1);
        } else {
            CosplayMap.put(id, 1);
        }
        MyLog.i(id + " ,cosplay num= " + CosplayMap.get(id));
    }

    /**
     * 获取多次点击赞的数
     */
    public static int getMultiLikeNum(Object object) {
        if (object == null) {
            return 0;
        }
        if (object instanceof CosplayInfo) {
            return getMultiLikeNum((CosplayInfo) object);
        } else if (object instanceof TagPop) {
            return getMultiLikeNum((TagPop) object);
        }
        return 0;
    }

    /**
     * 获取图片多次点击赞的数
     */
    public static int getMultiLikeNum(CosplayInfo cosplayInfo) {
        if (cosplayInfo == null || StringUtil.isNullOrEmpty(cosplayInfo.getUcid())) {
            return 0;
        }
        String id = cosplayInfo.getUcid();
        if (CosplayMap.containsKey(id)) {
            return CosplayMap.get(id);
        } else {
            return 0;
        }
    }

    /**
     * 获取话题多次点击赞的数
     */
    public static int getMultiLikeNum(TagPop tagPop) {
        if (tagPop == null || StringUtil.isNullOrEmpty(tagPop.getTagPopId())) {
            return 0;
        }
        String id = tagPop.getTagPopId();
        if (TopicMap.containsKey(id)) {
            return TopicMap.get(id);
        } else {
            return 0;
        }
    }

    /**
     * 定期处理
     */
    private void processAction(String action, boolean upload) {
        MyLog.i(upload + ", processAction action=" + action);
        if (SELF_PERIOD_CHECK_ACTION.equals(action)) {
            StickPreference.getInstance().setLastPeriodCheck(System.currentTimeMillis());
        }

        if (upload) {
            // TODO upload like click number
            uploadClickCosNum();
            uploadClickTopicNum();
        }
        cancelAlarmWork(SELF_PERIOD_CHECK_ACTION);
        startNextAlarmWork(SELF_PERIOD_CHECK_ACTION, CHECK_DELAY);
        BackgroundService.this.stopSelf();

        DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
        ArrayList<EmoticonSetBean> localList = dbHelper.queryUnUpdateEmoticonSetByType(ClientInfo.getUID(), StickerConstants.STICKER_TYPE_NORMAL);
        if (localList != null && !localList.isEmpty()) {
            StickerManager mStickerManager = StickerManager.getInstance();
            for (EmoticonSetBean setBean : localList) {
                if (setBean == null || StringUtil.isNullOrEmpty(setBean.getId())) {
                    continue;
                }
                mStickerManager.getStickerUpdate(setBean.getStickType(),
                        setBean.getUpdateTime(), setBean.getId(), new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                if (!TextUtils.isEmpty(setBean.getId())) {
                                    dbHelper.updateEmoticonSetFlag(setBean.getId(),
                                            obj == null ? StickerConstants.FLAG_NORMAL : StickerConstants.FLAG_UNUPDATED);
                                }

                                MyLog.i("更新贴纸包 1");
                                if (obj != null) {
                                    // 下载更新贴纸包
                                    mStickerManager.downloadStickerList((StickerPackage) obj, setBean);
                                    Bundle result = mStickerManager.downloadStickerIcon((StickerPackage) obj, setBean);
                                    if (result != null) {
                                        mStickerManager.saveStickers2DB(setBean.getStickType(), (StickerPackage) obj, result.getString(mStickerManager.KEY_URL), result.getString(mStickerManager.KEY_PATH));
                                        if (setBean.getStickType() > 1) {
                                            dbHelper.updateEmoticonSetFlag(setBean.getStickType(), StickerConstants.FLAG_NORMAL);
                                        } else {
                                            dbHelper.updateEmoticonSetFlag(setBean.getId(), StickerConstants.FLAG_NORMAL);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onErr(Object obj) {
                                // TODO 更新失败或者无网络时需要添加个标志啦,然后在贴纸制作activity中检查是否有更新没做,继续做
                                MyLog.i("更新贴纸包 2 sticker=onErr");
                            }

                            @Override
                            public void onNoNetwork() {
                                MyLog.i("更新贴纸包 3 sticker=onNoNetwork");
                            }
                        });
            }
        }
    }

    private void startNextAlarmWork(String command, long interval) {
        MyLog.i("startNextAlarmWork action="+command);

        Intent startIntent = new Intent(this, BackgroundService.class);
        startIntent.setAction(command);
        mPendingIntent = PendingIntent.getService(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        long now = System.currentTimeMillis();
        mAlarmManager.set(AlarmManager.RTC, now + interval, mPendingIntent);
    }

    private void cancelAlarmWork(String command) {
        MyLog.i("cancelAlarmWork action=" + command);

        Intent startIntent = new Intent(this, BackgroundService.class);
        startIntent.setAction(command);
        mPendingIntent = PendingIntent.getService(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (mPendingIntent != null){
            AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.cancel(mPendingIntent);
        }
    }

    /************************************************************************************
     * 上传大咖秀点赞数
     */
    private void uploadClickCosNum() {
        MyLog.i("uploadClickCosNum !!" + CHECK_DELAY);
        if (CosplayMap.isEmpty()) {
            changeDelayIfEmpty();

            return;
        }
        Iterator iter = CosplayMap.entrySet().iterator();
        ArrayList<LikeCosplay> likeList = new ArrayList<>(CosplayMap.size());
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            likeList.add(new LikeCosplay((String) key, (int) val));
        }

        new android.os.Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                DiscoveryManager.getInstance().likeCosplay(likeList, new IListener2() {
                    @Override
                    public void onSuccess(Object obj) {
                        MyLog.i("上传成功!!");


                        refreshCosplayMap(likeList);

                        changeDelayIfEmpty();

                        //判断是否获得了魔豆
                        showGetMoinBean(obj);
                    }

                    @Override
                    public void onErr(Object obj) {

                    }

                    @Override
                    public void onNoNetwork() {

                    }
                });
            }
        });
    }

    /**
     * 更新大咖秀点赞数
     */
    private void refreshCosplayMap(ArrayList<LikeCosplay> likeList) {
        if (likeList == null || likeList.isEmpty()) {
            return;
        }
        for (LikeCosplay likecos : likeList) {
            if (CosplayMap.containsKey(likecos.getUcid())) {
                MyLog.i("local num = " + CosplayMap.get(likecos.getUcid()));
                MyLog.i("uplod num = " + likecos.getLikeNum());
                int num = CosplayMap.get(likecos.getUcid()) - likecos.getLikeNum();
                MyLog.i("minus num = " + num);
                if (num <= 0) {
                    CosplayMap.remove(likecos.getUcid());
                    MyLog.i("remov key = " + likecos.getUcid());
                } else {
                    CosplayMap.put(likecos.getUcid(), num);
                    MyLog.i("final num = " + CosplayMap.get(likecos.getUcid()));
                }
            }
        }
    }


    /************************************************************************************
     * 上传话题点赞数
     */
    private void uploadClickTopicNum() {
        MyLog.i("uploadClickTopicNum !!" + CHECK_DELAY);
        if (TopicMap.isEmpty()) {
            changeDelayIfEmpty();

            return;
        }
        Iterator iter = TopicMap.entrySet().iterator();
        ArrayList<LikeTopic> likeList = new ArrayList<>(TopicMap.size());
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            likeList.add(new LikeTopic((String) key, (int) val));
        }
        new android.os.Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DiscoveryManager.getInstance().likeTopic(likeList, new IListener2() {
                    @Override
                    public void onSuccess(Object obj) {
                        MyLog.i("上传成功!!");

                        refreshTopicMap(likeList);

                        changeDelayIfEmpty();

                        //判断是否获得了魔豆
                        showGetMoinBean(obj);
                    }

                    @Override
                    public void onErr(Object obj) {

                    }

                    @Override
                    public void onNoNetwork() {

                    }
                });
            }
        });
    }

    /**
     * 更新话题点赞数
     */
    private void refreshTopicMap(ArrayList<LikeTopic> likeList) {
        if (likeList == null || likeList.isEmpty()) {
            return;
        }
        for (LikeTopic likeTopic : likeList) {
            if (TopicMap.containsKey(likeTopic.getTopicid())) {
                MyLog.i("local num = " + TopicMap.get(likeTopic.getTopicid()));
                MyLog.i("uplod num = " + likeTopic.getLikeNum());
                int num = TopicMap.get(likeTopic.getTopicid()) - likeTopic.getLikeNum();
                MyLog.i("minus num = " + num);
                if (num <= 0) {
                    TopicMap.remove(likeTopic.getTopicid());
                    MyLog.i("remov key = " + likeTopic.getTopicid());
                } else {
                    TopicMap.put(likeTopic.getTopicid(), num);
                    MyLog.i("final num = " + TopicMap.get(likeTopic.getTopicid()));
                }
            }
        }
    }

    /**
     * 获得魔豆后调起获得魔豆的页面
     */
    private void showGetMoinBean(Object obj) {
        if (obj != null) {
            MoinBean bean = (MoinBean)obj;
            if (bean != null && bean.getTotalBean() > 0 && bean.getObtainBean() > 0) {
                UIHelper.showMoinBeanActivity(bean, MissionConstants.MISSION_LIKE);
            }
        }
    }
}

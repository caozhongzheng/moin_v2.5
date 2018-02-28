package com.moinapp.wuliao.modules.sticker.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.db.DBHelper;
import com.mobeta.android.dslv.DragSortListView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.listener.IListener2;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.mine.chat.ChatLayoutManager;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.modules.sticker.adapter.MyStickerAdaptor;
import com.moinapp.wuliao.modules.sticker.model.StickerPackage;
import com.moinapp.wuliao.modules.sticker.ui.mall.StickerCenterViewPagerFragment;
import com.moinapp.wuliao.modules.stickercamera.app.camera.event.AddMySticker;
import com.moinapp.wuliao.modules.stickercamera.app.camera.event.DeleteMySticker;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.MyPopWindow;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的贴纸
 *
 * @author liujiancheng
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyStickerFragment extends BaseFragment implements OnTabReselectListener {

    private static final ILogger MyLog = LoggerFactory.getLogger("msf");

    private static final String CACHE_KEY_PREFIX = "mysticker_list";
    public static final int MODE_NORMAL = 0;
    public static final int MODE_EDIT = 1;

    private Activity mContext;
    private String mUid;
    private ArrayList<StickerPackage> datas = new ArrayList<>();
    protected MyStickerAdaptor mAdapter;
    DBHelper dbHelper = DBHelper.getInstance(BaseApplication.context());
    ArrayList<EmoticonSetBean> setBeanList;
    private String mLastId;
    private int mMode;

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    @InjectView(R.id.dragsort_listview)
    protected DragSortListView mListView;

    @InjectView(R.id.error_layout)
    protected EmptyLayout mErrorLayout;

    @InjectView(R.id.edit_button)
    protected TextView mEditBtn;

    @InjectView(R.id.rl_header)
    protected RelativeLayout mRlHeader;
    private long startTime, lastEndtme, endTime, duration;
    private View view;
    private boolean isFirstTimeIn = true;
    int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle args = getArguments();
        if (args != null) {
            mUid = args.getString(Constants.BUNDLE_KEY_UID);
        }
        mUid = ClientInfo.getUID();
        mContext = getActivity();
        if (isFirstTimeIn) {
            startTime = TimeUtils.getCurrentTimeInLong();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_download_sticker, container,
                false);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                mContext.finish();
            }
        });

        datas.clear();
        mLastId = null;
        mMode = MODE_NORMAL;
        setModeText();
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMode = mMode == MODE_NORMAL ? MODE_EDIT : MODE_NORMAL;
                setModeText();
                if (mAdapter != null) {
                    mAdapter.setMode(mMode);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        initEmptyLayout();

        mListView.setDropListener(onDrop);
//        mListView.setRemoveListener(onRemove);
        mListView.setDragEnabled(true);

        mAdapter = new MyStickerAdaptor(mContext);
        mAdapter.setRemoveCallback(onRemove);
        mListView.setAdapter(mAdapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    //如果垂直滑动,则需要关闭已经打开的Layout
                    ChatLayoutManager.getInstance().closeCurrentLayout();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    // 初始化emptylayout的显示图片文字等
    protected void initEmptyLayout() {
        if (AppContext.getInstance().isLogin()) {
            mErrorLayout.setEmptyImage(R.drawable.no_date_history_sticker);
            mErrorLayout.setNoDataContent(getString(R.string.no_sticker_my));
//            mErrorLayout.setBtnVisibility(View.VISIBLE);
            mErrorLayout.setOnLayoutClickListener(v -> {
                List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
                for (int i = 0; i < fragments.size(); i++) {
                    if ((fragments.get(i).getClass()).equals(StickerCenterViewPagerFragment.class)) {
                        StickerCenterViewPagerFragment stickerCenterViewPagerFragment = (StickerCenterViewPagerFragment) fragments.get(i);
                        stickerCenterViewPagerFragment.setGroupCheck(0);
                    }
                }
            });
            mErrorLayout.hideNoLogin();
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
            //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
            mErrorLayout.setOnLayoutClickListener(v -> {
                if (AppContext.getInstance().isLogin()) {
                    requestData();
                } else {
                    UIHelper.showLoginActivity(mContext, 0);
                }
            });
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("onResume");
        initEmptyLayout();
        refreshDataView();
        if (!isGetting && AppContext.getInstance().isLogin()) {
            requestData();
        }
        if (!isFirstTimeIn) {
            startTime = TimeUtils.getCurrentTimeInLong();
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                endTime = TimeUtils.getCurrentTimeInLong();
                if (lastEndtme == 0 || count < 6) {
                    lastEndtme = endTime;
                } else {
                    count = 0;
                    duration = endTime - startTime;
                    if (isFirstTimeIn){
                        HashMap<String, String> forward = new HashMap<String, String>();
                        forward.put("type", UmengConstants.T_COSPLAY_MINE);
                        onEvent(getActivity(), UmengConstants.T_COSPLAY_MINE, forward, duration);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                }
                count += 1;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        isFirstTimeIn = false;
        ChatLayoutManager.getInstance().clearCurrentLayout();
    }

    private void setModeText() {
        if (mMode == MODE_NORMAL) {
            mEditBtn.setText("排序");
        } else {
            mEditBtn.setText("完成");
        }
    }

    // 刷新同步按钮的状态
//    private void refreshReloadBtn() {
//        boolean reload = false;
//
//        setBeanList = dbHelper.queryEmoticonSetByType(mUid, 1);
//        MyLog.i("ljc: mUid =" + mUid + "type =1");
//        if (setBeanList == null || setBeanList.isEmpty()) {
//            MyLog.i("ljc: setBeanList is empty");
//            if (datas.size() > 0) {
//                // 服务器下发的我的贴纸列表不为空, 但是本地数据库为空, 需要重新下载
//                MyLog.i("ljc: datas.size > 0");
//                reload = true;
//            }
//        } else {
//            if (datas.size() > setBeanList.size()) {
//                reload = true;
//            } else {
//                //检查是否有需要重新下载的贴纸包
//                List<EmoticonSetBean> list = StickerManager.getInstance().
//                        getNeedReloadStickers(ClientInfo.getUID());
//                if (list != null && list.size() > 0) {
//                    MyLog.i("ljc: getNeedReloadStickers list.size > 0");
//                    reload = true;
//                }
//            }
//        }
//
//        if (reload) {
//            title.displayRightBtn();
//            title.setRightTxtBtn("同步");
//            title.setRightBtnOnclickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MobclickAgent.onEvent(mContext, UmengConstants.STICKER_SYNC);
//                    mAdapter.reloadAllStickers();
//                }
//            });
//        } else {
//            title.hideRightBtn();
//        }
//
//    }

    MyPopWindow refresh_sticker_package_popupWindow;

    /**
     * 显示和隐藏更新按钮,以及更新时的popwindow
     */
    private void toggleStickerRefreshProgress(boolean isProgress) {
        if (isProgress) {
            if (refresh_sticker_package_popupWindow != null) {
                refresh_sticker_package_popupWindow.dismiss();
                refresh_sticker_package_popupWindow = null;
            }
            initRefreshPopWindow();
            refresh_sticker_package_popupWindow.show(Gravity.CENTER, false);
        } else {
            if (refresh_sticker_package_popupWindow != null)
                refresh_sticker_package_popupWindow.dismiss();
        }
    }

    public void initRefreshPopWindow() {
        View popupWindow_view = LayoutInflater.from(mContext).inflate(R.layout.update_sticker_package_dialog, null);
        popupWindow_view.setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());
        refresh_sticker_package_popupWindow = new MyPopWindow(mContext, popupWindow_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        refresh_sticker_package_popupWindow.setOutsideTouchable(false);
    }

    private void fillUI() {
        if (mState == ERROR) {
            MyLog.i("fillUI mState == ERROR");
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            mErrorLayout.setBtnVisibility(View.GONE);
            mErrorLayout.setOnLayoutClickListener(v -> {
                requestData();
            });
        } else if (datas.isEmpty() && mState == NO_MORE) {
            MyLog.i("fillUI mState == NODATA");
            mErrorLayout.setBtnVisibility(View.VISIBLE);
            mErrorLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
        } else {
            MyLog.i("fillUI mState == " + mState + ", datas.size = " + datas.size());
            setBeanList = dbHelper.queryEmoticonSetByType(mUid, 1);
            // 因为现在没有同步了,如果本地没有但是联网获取到了,需要先入库
            if ((setBeanList == null || setBeanList.isEmpty())
                    && (datas != null && datas.size() > 0)) {
                insertStickers2DB();
                setBeanList = dbHelper.queryEmoticonSetByType(mUid, 1);
            }

            if (setBeanList != null && setBeanList.size() > 0) {
                ArrayList<StickerPackage> sortedList = new ArrayList<>();
                final int size = setBeanList.size();
                for (int i = 0; i < size; i++) {
                    EmoticonSetBean set = setBeanList.get(i);
                    // 直接把数据库中的顺序逆序修改一下
                    dbHelper.updateEmoticonSetOrder(set.getId(), size - i - 1);
                    for (StickerPackage stickPackage : datas) {
                        if (stickPackage.getStickerPackageId().equals(set.getId())) {
                            sortedList.add(stickPackage);
                            datas.remove(stickPackage);
                            break;
                        }
                    }
                }
                if (sortedList.size() > 0) {
                    datas.addAll(sortedList);
                }
            }

            mAdapter.setData(datas);
            MyLog.i("fillUI mState == HIDE_LAYOUT");
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

//            refreshReloadBtn();
        }
        refreshDataView();
    }

    private void insertStickers2DB() {
        if (datas == null || datas.isEmpty()) return;

        for (StickerPackage stickerPackage : datas) {
            StickerManager.getInstance().downloadStickerPackage(stickerPackage, null, 1);
        }
    }
    // 刷新listview和emptylayout的显示隐藏状态
    private void refreshDataView() {
        if (datas != null && datas.size() > 0) {
            mRlHeader.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mRlHeader.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }

        if (datas.size() > 0) {
            mLastId = datas.get(datas.size() - 1).getStickerPackageId();
        } else {
            mLastId = null;
        }

    }

    //监听器在手机拖动停下的时候触发
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {//from to 分别表示 被拖动控件原位置 和目标位置
                    MyLog.i("from=" + from + ", to=" + to);
                    if (from != to) {
                        final int size = setBeanList.size() - 1;

                        StickerPackage item = (StickerPackage) mAdapter.getItem(from);//得到listview的适配器
                        //在适配器中”原位置“的数据。
                        mAdapter.remove(from);
                        //在目标位置中插入被拖动的控件。
                        mAdapter.insert(item, to);

                        // 更改数据库中贴纸包的顺序
                        if (from > size || to > size) return;
                        dbHelper.changeStickerOrder(size - from, size - to);
                        // 本地数据也同步一下顺序
                        EmoticonSetBean tmp = setBeanList.get(from);
                        setBeanList.remove(from);
                        setBeanList.add(to, tmp);

                        MinePreference.getInstance().setNeedRefreshPhotoEdit(true);
                    }
                }
            };
    //删除监听器，点击左边差号就触发。删除item操作。
//    private DragSortListView.RemoveListener onRemove =
//            new DragSortListView.RemoveListener() {
    private MyStickerAdaptor.RemoveCallback onRemove =
            new MyStickerAdaptor.RemoveCallback() {
                @Override
                public void onRemove(int position) {
                     MyLog.i("delete =" + position);
                    final StickerPackage item = mAdapter.getData().get(position);
                    DialogHelper dialogHelper = new DialogHelper(mContext);
                    dialogHelper.alert4M(null, "确定删除吗?", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogHelper.dialogDismiss();
                        }
                    }, "确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StickerManager.getInstance().deleteSticker(item.getStickerPackageId(), new IListener2() {
                                @Override
                                public void onSuccess(Object obj) {
                                    StickerManager.getInstance().deleteStickerFromDB(item.getStickerPackageId());
                                    // 在同步后,再删除时有new标记的bug
                                    if (StickerManager.getInstance().isNewest(item)) {
                                        StickerManager.getInstance().setViewedNewest(item);
                                    }

                                    MinePreference.getInstance().setNeedRefreshPhotoEdit(true);

                                    // 本地数据也同步一下顺序
                                    EmoticonSetBean tmp = null;
                                    int j = 0;
                                    if (setBeanList != null) {
                                        for (; j < setBeanList.size(); j++) {
                                            if (item.getStickerPackageId().equalsIgnoreCase(setBeanList.get(j).getId())) {
                                                tmp = setBeanList.get(j);
                                                setBeanList.remove(j);
                                                break;
                                            }
                                        }

                                        // 更改数据库中贴纸包的顺序
                                        final int size = setBeanList.size();
                                        if (dbHelper == null) {
                                            dbHelper = DBHelper.getInstance(BaseApplication.context());
                                        }

                                        for (int i = 0; i < j; i++) {
                                            dbHelper.updateEmoticonSetOrder(
                                                    setBeanList.get(i).getId(), size - i - 1);
                                        }
                                    }

                                    mAdapter.getData().remove(position);
                                    datas.remove(item);

                                    // notify放在data edit后面
                                    mAdapter.notifyDataSetChanged();
                                    ChatLayoutManager.getInstance().clearCurrentLayout();

                                    refreshDataView();
                                    if (mAdapter.getData().isEmpty()) {
                                        mErrorLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
                                    }

                                    try {
                                        // 清空以前的旧文件
                                        if (tmp != null) {
                                            removeAllStickers(tmp);
                                        }
                                    } catch (Exception e) {
                                        MyLog.e(e);
                                    }

                                    EventBus.getDefault().post(new DeleteMySticker(item.getStickerPackageId()));
                                }

                                @Override
                                public void onErr(Object obj) {

                                }

                                @Override
                                public void onNoNetwork() {

                                }

                                @Override
                                public void onFinish() {
                                    super.onFinish();
                                    dialogHelper.dialogDismiss();
                                }
                            });
                        }
                    }, true);
                }
            };

    private void removeAllStickers(EmoticonSetBean tmp) {
        if (tmp == null) return;
        String filename;
        File file;

        filename = tmp.getIconUri().replace("file://", "");
        file = new File(filename);
        if (file != null && file.exists()) {
            file.delete();
        }

        if (tmp.getEmoticonList() == null || tmp.getEmoticonList().size() == 0) return;
        for (EmoticonBean bean : tmp.getEmoticonList()) {
            if (bean == null) continue;
            if (!TextUtils.isEmpty(bean.getIconUri())) {
                filename = bean.getIconUri().replace("file://", "");
                file = new File(filename);
                if (file != null && file.exists()) {
                    file.delete();
                }
            }

            if (!TextUtils.isEmpty(bean.getGifUri())) {
                filename = bean.getGifUri().replace("file://", "");
                file = new File(filename);
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
        }

    }

    private void updateDB() {
        if (datas == null || datas.size() == 0) return;

        for (StickerPackage stickerPackage : datas) {

        }
    }

    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mUid;
    }

    boolean isGetting = false;

    protected void requestData() {
        isGetting = true;
        sendRequestData();
    }

    private final int NONE = 0, ERROR = -1, MORE = -2, NO_MORE = 1;
    private int mState = NONE;

    protected void sendRequestData() {
        MyLog.i("sendRequestData lastid = " + mLastId + ", datas.size() = " + datas.size());
        StickerManager.getInstance().getMyStickerList(null, null, null, mLastId, new IListener2() {
            int state;

            @Override
            public void onStart() {
                super.onStart();
                MyLog.i("开始 获取到了");
                state = 0;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (state < 0) {
                    MyLog.i("获取结束 mState=ERROR");
                    mState = ERROR;
                    isGetting = false;
                    fillUI();
                }
                if (mState == NO_MORE) {
                    MyLog.i("获取结束 mState=NO_MORE");
                    isGetting = false;
                    fillUI();
                } else if (mState == MORE) {
                    MyLog.w("还有更多哦~~~~ ");
                    requestData();
                }
            }

            @Override
            public void onSuccess(Object obj) {
                state = 1;
                List<StickerPackage> list = (List<StickerPackage>) obj;
                MyLog.i("获取到了" + list.size() + "条");
                if (list != null && list.size() > 0) {
                    mState = MORE;

                    mLastId = list.get(list.size() - 1).getStickerPackageId();

                    // 去除重复数据
                    if (datas != null && datas.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (compareTo(datas, list.get(i))) {
                                list.remove(i);
                                i--;
                            }
                        }
                    }

                    datas.addAll(list);
                } else {
                    mState = NO_MORE;
                }
            }

            @Override
            public void onErr(Object obj) {
                state = -1;
                MyLog.i("获取failed");
                hideWaitDialog();
                AppContext.showToast(R.string.connection_failed);
            }

            @Override
            public void onNoNetwork() {
                state = -2;
                MyLog.i("获取failed no_network");
                hideWaitDialog();
                AppContext.showToast(R.string.no_network);
            }
        });
        MyLog.i("sendRequestData OVER: " + datas.size());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.unregisterEventBus();
        }
        EventBus.getDefault().unregist(this);
    }

    protected boolean compareTo(List<? extends StickerPackage> data, StickerPackage enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (enity.getStickerPackageId().equalsIgnoreCase(data.get(i).getStickerPackageId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onEvent(AddMySticker addMySticker) {
        new Handler(BaseApplication.context().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                StickerPackage sticker = addMySticker.getSticker();
                // 也更新下本地数据
                EmoticonSetBean setBean = StickerManager.getInstance().convertStickerPackageToEmoticonSetBean(1, sticker, null, null);
                if (setBean == null || TextUtils.isEmpty(setBean.getId())) {
                    return;
                }
                MyLog.i("AddMySticker: setBean.id = " + setBean.getId() + ", name = " + setBean.getName());
                if (setBeanList != null) {
                    for (int i = 0; i < setBeanList.size(); i++) {
                        if (setBean.getId().equalsIgnoreCase(setBeanList.get(i).getId())) {
                            //有重复的数据,不插入
                            return;
                        }
                    }
                    setBeanList.add(0, setBean);
                }
            }
        });
    }

    @Override
    public void onTabReselect() {
        mListView.setSelection(0);
    }

    public static void onEvent(Context context, String id, HashMap<String, String> m, long value) {
        m.put("__ct__", String.valueOf(value));
        MobclickAgent.onEvent(context, id, m);
    }
}

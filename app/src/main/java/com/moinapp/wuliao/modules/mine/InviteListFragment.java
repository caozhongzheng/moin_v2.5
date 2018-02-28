package com.moinapp.wuliao.modules.mine;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.AliasEvent;
import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.FollowStatusChange;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.IClickListener;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.login.model.ThirdInfo;
import com.moinapp.wuliao.modules.mine.adapter.InviteAdapter;
import com.moinapp.wuliao.modules.mine.model.GetThirdFriendsResult;
import com.moinapp.wuliao.modules.stickercamera.base.util.DialogHelper;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.a2zletter.LetterComparator;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/** 通讯录/微博 邀请好友列表
 * Created by moying on 16/4/7.
 */
public class InviteListFragment extends BaseFragment {

    public static final String ALIAS_PREFEX = "ALIAS_PREFEX_";
    public static final int ALIAS_PREFEX_L = ALIAS_PREFEX.length();
    private ILogger MyLog = LoggerFactory.getLogger(InviteListFragment.class.getSimpleName());
    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;

    @InjectView(R.id.listview)
    protected ListView mListView;

    @InjectView(R.id.error_layout)
    protected EmptyLayout mErrorLayout;

    protected ListBaseAdapter<UserInfo> mAdapter;

    private static final String CACHE_KEY_PREFIX = "invite_list";
    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

    // 0:通讯录好友, 1:微博好友
    private int mInviteType;
    private String mContacts;
    private String mPhones;
    private ThirdInfo mToken;
    private String mTokenString;

    protected int mStoreEmptyState = -1;

    private ParserTask mParserTask;
    // 错误信息
    protected BaseHttpResponse mResult;

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mInviteType = args.getInt(Constants.BUNDLE_KEY_TYPE, 0);
            mContacts = args.getString(Constants.BUNDLE_KEY_CONTACTS);
            mToken = (ThirdInfo)args.getSerializable(Constants.BUNDLE_KEY_TOKEN);
            if (mToken != null) {
                mTokenString = new Gson().toJson(mToken);
            }
        }
        MyLog.i("mInviteType = " + mInviteType);
        MyLog.i("mContacts = " + mContacts);

        if (isContacts())  getContacts();

//        mPhones = "18201122521,13812345678";
//        mContacts = "18201122521_曹中正,13812345678_隔壁老王";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.invite_fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
        initData();

        EventBus.getDefault().register(this);
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        title.setTitleTxt(isContacts() ? "通讯录" : "微博");
        title.hideRightBtn();
        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        title.setTitleOnclickListener(v -> {
            scrollToTop();
        });

        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            mAdapter = getListAdapter();
            mListView.setAdapter(mAdapter);

            mErrorLayout.setEmptyImage(R.drawable.no_data_fan_follow);
            if (isContacts()) {
                mErrorLayout.setNoDataContent(getString(R.string.no_contacts));
                // 本地通讯录为空时,显示没数据
                if (StringUtil.isNullOrEmpty(mContacts)) {
                    mErrorLayout.setErrorType(EmptyLayout.NODATA);
//                    checkAuthority(1);
                } else {
                    requestData();
                }
            } else {
                mErrorLayout.setNoDataContent(getString(R.string.no_sina_friends));

                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                mState = STATE_REFRESH;
                requestData();
            }
        }
        if (mStoreEmptyState != -1) {
            mErrorLayout.setErrorType(mStoreEmptyState);
        }
        mErrorLayout.setClickable(false);
    }

    /**检查权限*/
    private void checkAuthority(int type) {
        DialogHelper dialogHelper = new DialogHelper(getActivity());
        dialogHelper.alert4M(null, getString(type == 1 ? R.string.sms_empty : R.string.sms_empty),
                getString(R.string.sms_authority_cancel), v -> dialogHelper.dialogDismiss(),
                getString(R.string.sms_authority_go), v -> {
                    // 本地通讯录为空 可以让用户确认下权限
                    showInstalledAppDetails(getActivity(), ClientInfo.getPackageName());
                    dialogHelper.dialogDismiss();
                }, true);
    }

    /**
     * 得到手机通讯录联系人信息
     * 这个比较全,包含了sim卡的联系人信息*
     */
    private void getContacts() {
        ContentResolver resolver = getActivity().getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION,
                null, null, null);

        if (phoneCursor != null) {
            int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            int i = 1;
            ArrayList<String> contactList = new ArrayList<>();
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);

                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                if (phoneNumber.contains("-")) {
                    phoneNumber = phoneNumber.replaceAll("-", "");
                }
                if (phoneNumber.length() > 11) {
                    phoneNumber = phoneNumber.substring(phoneNumber.length() - 11);
                }
                if (phoneNumber.startsWith("1")) {
                    String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                    if (StringUtil.isNullOrEmpty(contactName)) {
                        contactName = phoneNumber;
                    }
                    contactList.add(contactName + "_" + phoneNumber);

                    i++;
//                    MyLog.i(i++ + "-----: " + phoneNumber + ", " + contactName);
                }
            }
            MyLog.i("local Contacts size = " + i);

            boolean isFirst = true;
            StringBuilder phonelist = new StringBuilder();
            StringBuilder contacts = new StringBuilder();
            if (contactList.size() > 0) {
                LetterComparator pinyinComparator = new LetterComparator();
                Collections.sort(contactList, pinyinComparator);

                for (String name_phone : contactList) {
                    if (isFirst) {
                        phonelist.append(name_phone.substring(name_phone.indexOf("_") + 1));
                        contacts.append(name_phone);
                        isFirst = false;
                    } else {
                        phonelist.append(",").append(name_phone.substring(name_phone.indexOf("_") + 1));
                        contacts.append(",").append(name_phone);
                    }
                }

            }

            mPhones = phonelist.toString();
            mContacts = contacts.toString();
            phoneCursor.close();
        }
    }

    private static final String SCHEME = "package";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
     */
    private static final String APP_PKG_NAME_22 = "pkg";
    /**
     * InstalledAppDetails所在包名
     */
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    /**
     * InstalledAppDetails类名
     */
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    /**
     * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
     * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
     *
     * @param context
     * @param packageName 应用程序的包名
     */
    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= Build.VERSION_CODES.GINGERBREAD) {
            /** 2.3（ApiLevel 9）以上，使用SDK提供的接口*/
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else {
            /**2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
             * 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
             * */
            final String appPkgName = (apiLevel == Build.VERSION_CODES.FROYO ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }

    /**
     * 是否是通讯录好友列表
     */
    private boolean isContacts() {
        return mInviteType == 0;
    }

    /**
     * 列表滑动到顶部
     */
    public void scrollToTop() {
        mListView.setSelection(0);
    }

    /**
     * TODO 获取通讯录好友列表
     */
    private void requestData() {
        if (isContacts()) {
//            MyLog.i("requestData=" + mPhones == null ? "" : mPhones.substring(0, mPhones.length() > 20 ? 20 : mPhones.length()));
//            && !StringUtil.isNullOrEmpty(mPhones)
            MineApi.getThirdFriends(mPhones, null, mHandler);
        } else {
            if (mToken != null) {
                MineApi.getThirdFriends(null, mTokenString, mHandler);
            } else {
                AppContext.showToast(getActivity().getString(R.string.sina_weibo_token_empty));
            }
        }
    }

    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              byte[] responseBytes) {
            if (isAdded()) {
                executeParserTask(responseBytes);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            if (isAdded()) {
                executeOnLoadDataError(null);
            }
            executeOnLoadFinish();
        }
    };

    /**
     * 将未匹配的数据也添加进去,并对这个数据进行分组
     */
    private List<UserInfo> addUnmatchedLis(List<UserInfo> data) {
        if (data == null) {
            data = new ArrayList<>();
        }

        ArrayList<UserInfo> list = null;
        if (!StringUtil.isNullOrEmpty(mContacts)) {
            list = new ArrayList<>();
            String[] arr = mContacts.split(",");
            String group = isContacts() ? "uc" : "us";
            for (int a = 0; a < arr.length; a++) {
                try {
                    String[] c = arr[a].split("_");
                    UserInfo tmp = new UserInfo();
                    tmp.setThirdName(c[0]);
                    tmp.setPhone(c[1]);
                    tmp.setUsername_abc(group); // unmatched contacts/sina friends
                    list.add(tmp);
                } catch (Exception e) {
                    MyLog.e(e);
                }
            }

        }


        if (!data.isEmpty()) {
            String group = isContacts() ? "c" : "s";
            for (UserInfo d : data) {
                d.setUsername_abc(group); // match contacts/sina friends
            }
        }
        if (list == null || list.isEmpty()) {
            return data;
        } else {
            if (data.isEmpty()) {
                data.addAll(list);
            } else {
                for (int i = list.size() - 1; i >= 0; i--) {
                    UserInfo item = list.get(i);
                    for (int j = data.size() - 1; j >= 0; j--) {
                        UserInfo d = data.get(j);
                        try {
                            if (!StringUtil.isNullOrEmpty(item.getPhone()) && item.getPhone().equals(d.getPhone())) {
                                // TODO 设置第三方名字
                                d.setThirdName(item.getThirdName());
                                data.add(j, d);
                                data.remove(j + 1);
                                list.remove(i);
                                break;
                            }
                        } catch (Exception e) {
                            MyLog.e(e);
                        }
                    }
                }

                data.addAll(list);
            }
        }

        //过滤自己
        if (data != null) {
            for (int k = 0; k < data.size(); k++) {
                UserInfo user = data.get(k);
                if (user != null) {
                    if (ClientInfo.getUID().equalsIgnoreCase(user.getUId())) {
                        data.remove(k);
                        break;
                    }
                }
            }
        }
        
        return data;
    }


    /**
     * 微博的MOIN好友和非MOIN好友的列表组合
     */
    private void buildSinaUserList(GetThirdFriendsResult result, List<UserInfo> list) {
        if (list != null) {
            for (UserInfo userInfo : list) {
                userInfo.setUsername_abc("s");
            }
            List<UserInfo> invites = result.getInvites();
            if (invites != null) {
                for (UserInfo userInfo : invites) {
                    userInfo.setUsername_abc("us");
                }
                list.addAll(invites);
            }
        }
    }

    protected void executeOnLoadDataSuccess(List<UserInfo> data) {
        if (data == null) {
            data = new ArrayList<UserInfo>();
        }

        if (mResult != null && mResult.getResult() == 0) {
            AppContext.showToast(R.string.error_view_load_error_click_to_refresh);
        }

        if (mAdapter == null) {
            mAdapter = getListAdapter();
            mListView.setAdapter(mAdapter);
        }

        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mAdapter.clear();

        // 添加未匹配数据
        if (isContacts()) {
            data = addUnmatchedLis(data);
        }

        int adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
        if (data.isEmpty()) {
            adapterState = ListBaseAdapter.STATE_EMPTY_ITEM;
        } else {
            adapterState = ListBaseAdapter.STATE_OTHER;
        }

        mAdapter.setState(adapterState);
        mAdapter.addData(data);
        mAdapter.notifyDataSetChanged();
    }

    private void executeOnLoadDataError(Object o) {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
    }

    private void executeOnLoadFinish() {
        mState = STATE_NONE;
    }

    private void executeParserTask(byte[] data) {
        cancelParserTask();
        mParserTask = new ParserTask(data);
        mParserTask.execute();
    }

    private void cancelParserTask() {
        if (mParserTask != null) {
            mParserTask.cancel(true);
            mParserTask = null;
        }
    }

    class ParserTask extends AsyncTask<Void, Void, String> {
        private final byte[] reponseData;
        private boolean parserError;
        private List<UserInfo> list;

        public ParserTask(byte[] data) {
            this.reponseData = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                GetThirdFriendsResult result = XmlUtils.JsontoBean(GetThirdFriendsResult.class, reponseData);
                list = result.getFriends();

                //如果是微博好友列表,赋标记
                if (!isContacts()) {
                    buildSinaUserList(result, list);
                }

                if (list == null) {
                    BaseHttpResponse resultBean = XmlUtils.JsontoBean(BaseHttpResponse.class,
                            reponseData);
                    if (resultBean != null) {
                        mResult = resultBean;
                    }
                }
            } catch (Exception e) {
                MyLog.e(e);
                parserError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (parserError) {
                executeOnLoadDataError(null);
            } else {
                executeOnLoadDataSuccess(list);
            }
            executeOnLoadFinish();
        }
    }

    private ListBaseAdapter<UserInfo> getListAdapter() {
        return new InviteAdapter(mTokenString);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStoreEmptyState = mErrorLayout.getErrorState();
        EventBus.getDefault().unregist(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.INVITE_LIST_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.INVITE_LIST_FRAGMENT);
    }

    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + (isContacts() ? "" : "_");
    }

    /**
     * 关注后,显示备注
     */
    public void onEvent(FollowStatusChange status) {
        if (status == null || StringUtil.isNullOrEmpty(status.getUid())) {
            return;
        }
        if (mAdapter.getDataSize() > 0) {
            ArrayList<UserInfo> list = mAdapter.getData();
            for (int i = 0; i < list.size(); i++) {
                UserInfo u = list.get(i);
                if (StringUtil.isNullOrEmpty(u.getUId())) {
                    break;
                } else if (u.getUId().equals(status.getUid())){
                    list.remove(i);
                    u.setRelation(status.getNewRelation());
                    list.add(i, u);
                    MyLog.i("onEvent uid= " + list.get(i).toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setData(list);
                        }
                    });
                    break;
                }
            }
        }
    }

    /**
     * 修改备注,成功后替换名字为备注名
     */
    public void onEvent(AliasEvent aliasEvent) {
        MyLog.i("onEvent AliasEvent " + aliasEvent);
        if (aliasEvent == null || aliasEvent.getUserInfo() == null
                || StringUtil.isNullOrEmpty(aliasEvent.getUserInfo().getUId())) {
            return;
        }

        if (StringUtil.isNullOrEmpty(aliasEvent.getUserInfo().getUId())) {
            return;
        }
        changeAlias(aliasEvent.getUserInfo());
    }

    /**修改备注*/
    private void changeAlias(UserInfo userInfo) {
        DialogHelper dialogHelper = new DialogHelper(getActivity());
        String olgName = userInfo.getAlias();
        if (StringUtil.isNullOrEmpty(olgName) || ALIAS_PREFEX.equals(olgName)) {
            olgName = userInfo.getUsername();
        }
        dialogHelper.alert4MWithEditText(
                null,
                getString(R.string.alias_title),
                olgName,
                getString(R.string.cancle), v -> dialogHelper.dialogDismiss(),
                getString(R.string.ok),
                new IClickListener() {
                    @Override
                    public void OnClick(Object obj) {
                        // 新的备注在这里进行回调
                        String alias = (String) obj;
                        int result = StringUtil.checkAlias(getActivity(), userInfo.getAlias(), alias);
                        switch (result) {
                            case UserDefineConstants.ALIAS_UND:
                            case UserDefineConstants.ALIAS_LNG:
                            case UserDefineConstants.ALIAS_INV:
                                break;
                            case UserDefineConstants.ALIAS_ADD:
                            case UserDefineConstants.ALIAS_CHG:
                                break;
                            case UserDefineConstants.ALIAS_DEL:
                                break;
                        }

                        MineManager.getInstance().modifyAlias(userInfo.getUId(), alias, new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                if (mAdapter.getDataSize() > 0) {
                                    ArrayList<UserInfo> list = mAdapter.getData();
                                    for (int i = 0; i < list.size(); i++) {
                                        UserInfo u = list.get(i);
                                        MyLog.i("modifyAlias uid= " + u.getUId());
                                        if (StringUtil.isNullOrEmpty(u.getUId())) {
                                            break;
                                        } else if (u.getUId().equals(userInfo.getUId())){
                                            list.remove(i);
                                            u.setAlias(ALIAS_PREFEX + alias);
                                            list.add(i, u);
                                            MyLog.i("modifyAlias refresh uid= " + list.get(i).toString());
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mAdapter.setData(list);
                                                }
                                            });
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onErr(Object obj) {
                                AppContext.toast(getActivity(), getString(R.string.alias_failed));
                            }

                            @Override
                            public void onNoNetwork() {
                                AppContext.toast(getActivity(), getString(R.string.no_network));
                            }
                        });
                        dialogHelper.dialogDismiss();
                    }

                    @Override
                    public void onClick(View view) {
                        // DO nothing
                    }
                }, true);
    }
}

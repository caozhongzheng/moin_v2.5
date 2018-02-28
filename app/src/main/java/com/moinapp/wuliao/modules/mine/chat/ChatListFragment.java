package com.moinapp.wuliao.modules.mine.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.model.ChatMessage;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 聊天列表
 * Created by guyunfei on 16/4/10.22:52.
 */
public class ChatListFragment extends Fragment {
    private static final ILogger MyLog = LoggerFactory.getLogger(ChatListFragment.class.getSimpleName());

    @InjectView(R.id.title_layout)
    protected CommonTitleBar title;
    @InjectView(R.id.ly_no_internet)
    protected LinearLayout lyNoInternet;
    @InjectView(R.id.listview)
    protected ListView listView;
    @InjectView(R.id.ly_empty)
    protected EmptyLayout emptyLayout;

    private List<ChatMessage> messagesList;
    private ChatListAdapter chatListAdapter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!gprs.isConnected() && !wifi.isConnected()) {
                lyNoInternet.setVisibility(View.VISIBLE);
            } else {
                lyNoInternet.setVisibility(View.GONE);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        EventBus.getDefault().unregist(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initData();
        initView(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.CHAT_LIST_FRAGMENT);
        initData();
        if (messagesList.size() == 0) {
            emptyLayout.setErrorType(EmptyLayout.NODATA);
            emptyLayout.setVisibility(View.VISIBLE);
            emptyLayout.setNoDataContent(getResources().getString(R.string.no_data_chat_list));
            emptyLayout.setEmptyImage(R.drawable.big_chat_gray);
        } else {
            emptyLayout.setVisibility(View.GONE);
            chatListAdapter.setData(messagesList);
            chatListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.CHAT_LIST_FRAGMENT);
        ChatLayoutManager.getInstance().clearCurrentLayout();
    }

    private void initData() {
        messagesList = MineManager.getInstance().getChatRecordList();
        if (messagesList != null) {
            for (ChatMessage message : messagesList) {
                MyLog.i(message.getChatUser().getUsername() + " :" + message.getContent());
            }
        }
    }

    private void initView(View view) {

        emptyLayout.hideTitleDownArea();

        title.setTitleTxt("聊天");

        title.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        title.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showUserSearch(getActivity(), false);
            }
        });

        if (!TDevice.hasInternet()) {
            lyNoInternet.setVisibility(View.VISIBLE);
        } else {
            lyNoInternet.setVisibility(View.GONE);
        }

        if (messagesList != null) {
            chatListAdapter = new ChatListAdapter(getActivity());
            chatListAdapter.setData(messagesList);
            listView.setAdapter(chatListAdapter);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
            if (messagesList.size() == 0) {
                emptyLayout.setErrorType(EmptyLayout.NODATA);
                emptyLayout.setVisibility(View.VISIBLE);
                emptyLayout.setNoDataContent(getResources().getString(R.string.no_data_chat_list));
                emptyLayout.setEmptyImage(R.drawable.big_chat_gray);
            }
        } else {
            emptyLayout.setErrorType(EmptyLayout.NODATA);
            emptyLayout.setVisibility(View.VISIBLE);
            emptyLayout.setNoDataContent(getResources().getString(R.string.no_data_chat_list));
            emptyLayout.setEmptyImage(R.drawable.big_chat_gray);
        }
    }

    public void onEvent(MineManager.NewChatMessageEvent message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initData();
                if (messagesList != null) {
                    chatListAdapter.setData(messagesList);
                    chatListAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
package com.moinapp.wuliao.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.api.remote.OSChinaApi;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.MessageList;
import com.moinapp.wuliao.bean.Messages;
import com.moinapp.wuliao.bean.Notice;
import com.moinapp.wuliao.modules.mine.adapter.MyMessagesAdapter;
import com.moinapp.wuliao.ui.MainActivity;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.DialogHelp;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;

public class MessageFragment extends BaseListFragment<Messages> {
    @Override
    protected ListBaseAdapter<Messages> getListAdapter() {
        return null;
    }
}

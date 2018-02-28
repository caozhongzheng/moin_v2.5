package com.moinapp.wuliao.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.MessageDetailAdapter;
import com.moinapp.wuliao.api.OperationResponseHandler;
import com.moinapp.wuliao.api.remote.OSChinaApi;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.Comment;
import com.moinapp.wuliao.bean.CommentList;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.Result;
import com.moinapp.wuliao.bean.ResultBean;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.emoji.KJEmojiFragment;
import com.moinapp.wuliao.emoji.OnSendClickListener;
import com.moinapp.wuliao.ui.empty.EmptyLayout;
import com.moinapp.wuliao.util.DialogHelp;
import com.moinapp.wuliao.util.HTMLUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 与某人的聊天记录界面（留言详情）
 * 
 * @author kymjs (http://www.kymjs.com/)
 * 
 */
public class MessageDetailFragment extends BaseListFragment<Comment> implements
        OnItemLongClickListener, OnSendClickListener {
    protected static final String TAG = ActiveFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "message_detail_list";

    private int mFid;
    private String mFName;
    public KJEmojiFragment emojiFragment = new KJEmojiFragment();

    private final AsyncHttpResponseHandler mPublicHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            hideWaitDialog();
            try {
                ResultBean resb = XmlUtils.toBean(ResultBean.class,
                        new ByteArrayInputStream(arg2));
                Result res = resb.getResult();
                if (res.OK()) {
                    AppContext
                            .showToastShort(R.string.tip_message_public_success);
                    mAdapter.addItem(0, resb.getComment());
                } else {
                    AppContext.showToastShort(res.getErrorMessage());
                }
                emojiFragment.clean();
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                Throwable arg3) {
            hideWaitDialog();
            AppContext.showToastShort(R.string.tip_message_public_faile);
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mErrorLayout != null) {
                mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
                //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mFid = args.getInt(Constants.BUNDLE_KEY_ID);
            mFName = args.getString(Constants.BUNDLE_KEY_NAME);
            mCatalog = CommentList.CATALOG_MESSAGE;
        }
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);

        ((BaseActivity) getActivity()).setActionBarTitle(mFName);

        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getActivity().getWindow().setSoftInputMode(mode);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.emoji_container, emojiFragment).commit();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        if (emojiFragment.isShowEmojiKeyBoard()) {
            emojiFragment.hideAllKeyBoard();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    @Override
    protected MessageDetailAdapter getListAdapter() {
        return new MessageDetailAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mFid;
    }

    @Override
    protected CommentList parseList(InputStream is) throws Exception {
        CommentList list = XmlUtils.toBean(CommentList.class, is);
        return list;
    }

    @Override
    protected CommentList readList(Serializable seri) {
        CommentList list = ((CommentList) seri);
        return list;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setOnItemLongClickListener(this);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    requestData(false);
                } else {
                    UIHelper.showLoginActivity(getActivity());
                }
            }
        });
    }

    @Override
    protected void requestData(boolean refresh) {
        mErrorLayout.setErrorMessage("");
        if (AppContext.getInstance().isLogin()) {
            super.requestData(refresh);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NO_LOGIN);
            //mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getCommentList(mFid, mCatalog, mCurrentPage, mHandler);
    }

    @Override
    protected boolean isReadCacheData(boolean refresh) {
        if (!TDevice.hasInternet()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void executeOnLoadDataSuccess(List<Comment> data, boolean isLast) {
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (data == null) {
            data = new ArrayList<Comment>(1);
        }
        if (mAdapter != null) {
            if (mCurrentPage == 0)
                mAdapter.clear();
            mAdapter.addData(data);
            if (data.size() == 0 && mState == STATE_REFRESH) {
                mErrorLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
            } else if (data.size() < getPageSize()) {
                mAdapter.setState(ListBaseAdapter.STATE_OTHER);
            } else {
                mAdapter.setState(ListBaseAdapter.STATE_LOAD_MORE);
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mListView.getBottom());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {}

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
            int position, long id) {
        final Comment message = mAdapter.getItem(position);
        DialogHelp.getSelectDialog(getActivity(), getResources().getStringArray(R.array.message_list_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(message
                                .getContent()));
                        break;
                    case 1:
                        handleDeleteMessage(message);
                        break;
                    default:
                        break;
                }
            }
        }).show();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.MESSAGE_DETAIL_FRAGMENT); //统计页面，
        emojiFragment.hideFlagButton();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.MESSAGE_DETAIL_FRAGMENT);
    }

    private void handleDeleteMessage(final Comment message) {
        DialogHelp.getConfirmDialog(getActivity(), "是否删除该留言?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showWaitDialog(R.string.progress_submit);
                OSChinaApi.deleteComment(mFid,
                        CommentList.CATALOG_MESSAGE, message.getId(),
                        message.getAuthorId(),
                        new DeleteMessageOperationHandler(message));
            }
        }).show();
    }

    class DeleteMessageOperationHandler extends OperationResponseHandler {

        public DeleteMessageOperationHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccess(int code, ByteArrayInputStream is, Object[] args)
                throws Exception {
            Result res = XmlUtils.toBean(ResultBean.class, is).getResult();
            if (res.OK()) {
                Comment msg = (Comment) args[0];
                mAdapter.removeItem(msg);
                mAdapter.notifyDataSetChanged();
                hideWaitDialog();
                AppContext.showToastShort(R.string.tip_delete_success);
            } else {
                AppContext.showToastShort(res.getErrorMessage());
                hideWaitDialog();
            }
        }

        @Override
        public void onFailure(int code, String errorMessage, Object[] args) {
            AppContext.showToastShort(R.string.tip_delete_faile);
            hideWaitDialog();
        }
    }

    @Override
    public void onClickSendButton(String str) {
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
        if (StringUtils.isEmpty(str)) {
            AppContext.showToastShort(R.string.tip_content_empty);
            return;
        }
        showWaitDialog("提交中...");
        OSChinaApi.publicMessage(AppContext.getInstance().getLoginUid(), mFid,
                str, mPublicHandler);
    }

    @Override
    public void onClickFlagButton() {}
}

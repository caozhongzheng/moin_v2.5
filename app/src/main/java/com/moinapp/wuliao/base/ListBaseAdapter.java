package com.moinapp.wuliao.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.emoji.InputHelper;
import com.moinapp.wuliao.ui.ShareDialog;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.MyLinkMovementMethod;
import com.moinapp.wuliao.widget.MyURLSpan;
import com.moinapp.wuliao.widget.TweetTextView;

import java.util.ArrayList;
import java.util.List;

public class ListBaseAdapter<T extends Entity> extends BaseAdapter {
    public static final int STATE_EMPTY_ITEM = 0;
    public static final int STATE_LOAD_MORE = 1;
    public static final int STATE_NO_MORE = 2;
    public static final int STATE_NO_DATA = 3;
    public static final int STATE_LESS_ONE_PAGE = 4;
    public static final int STATE_NETWORK_ERROR = 5;
    public static final int STATE_OTHER = 6;
//    private static final ILogger MyLog = LoggerFactory.getLogger(ListBaseAdapter.class.getSimpleName());

    protected int state = STATE_LESS_ONE_PAGE;

    protected int _loadmoreText;
    protected int _loadFinishText;
    protected int _noDateText;
    protected int mScreenWidth;

    private LayoutInflater mInflater;

    protected LayoutInflater getLayoutInflater(Context context) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return mInflater;
    }

    public void setScreenWidth(int width) {
        mScreenWidth = width;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    protected ArrayList<T> mDatas = new ArrayList<T>();

    public ListBaseAdapter() {
        _loadmoreText = R.string.loading;
        _loadFinishText = R.string.no_more_data;
        _noDateText = R.string.loading_no_data;
    }

    public void setFinishText(int id) {
        _loadFinishText = id;
    }

    @Override
    public int getCount() {
        switch (getState()) {
        case STATE_EMPTY_ITEM:
            return getDataSizePlus1();
        case STATE_NETWORK_ERROR:
        case STATE_LOAD_MORE:
            return getDataSizePlus1();
        case STATE_NO_DATA:
            return 1;
        case STATE_NO_MORE:
        case STATE_OTHER:
            return getDataSizePlus1();
        case STATE_LESS_ONE_PAGE:
            return getDataSize();
        default:
            break;
        }
        return getDataSize();
    }
    public int getDataSizePlus1(){
        if(hasFooterView()){
            return getDataSize() + 1;
        }
        return getDataSize();
    }

    public int getDataSize() {
        return mDatas.size();
    }

    @Override
    public T getItem(int arg0) {
        if (arg0 >=0 && mDatas.size() > arg0) {
            return mDatas.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void setData(ArrayList<T> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

    public ArrayList<T> getData() {
        return mDatas == null ? (mDatas = new ArrayList<T>()) : mDatas;
    }

    public void addData(List<T> data) {
        if (mDatas != null && data != null && !data.isEmpty()) {
            mDatas.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addItem(T obj) {
        if (mDatas != null) {
            mDatas.add(obj);
        }
        notifyDataSetChanged();
    }

    public void addItem(int pos, T obj) {
        if (mDatas != null) {
            mDatas.add(pos, obj);
        }
        notifyDataSetChanged();
    }

    public void removeItem(Object obj) {
        mDatas.remove(obj);
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void setLoadmoreText(int loadmoreText) {
        _loadmoreText = loadmoreText;
    }

    public void setLoadFinishText(int loadFinishText) {
        _loadFinishText = loadFinishText;
    }

    public void setNoDataText(int noDataText) {
        _noDateText = noDataText;
    }

    protected boolean loadMoreHasBg() {
        return true;
    }
    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 最后一条
        if (position == getCount() - 1 && hasFooterView()) {
            // if (position < _data.size()) {
            // position = getCount() - 2; // footview
            // }
            if (getState() == STATE_LOAD_MORE || getState() == STATE_NO_MORE
                    || getState() == STATE_EMPTY_ITEM
                    || getState() == STATE_OTHER
                    || getState() == STATE_NETWORK_ERROR) {
                this.mFooterView = (LinearLayout) LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.list_cell_footer,
                        null);
                if (!loadMoreHasBg()) {
                    mFooterView.setBackgroundDrawable(null);
                }
                ProgressBar progress = (ProgressBar) mFooterView
                        .findViewById(R.id.progressbar);
                TextView text = (TextView) mFooterView.findViewById(R.id.text);
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                switch (getState()) {
                case STATE_LOAD_MORE:
                    setFooterViewLoading();
                    break;
                case STATE_NO_MORE:
                    mFooterView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                    text.setText(_loadFinishText);
                    // 没有更多数据的时候字体小,无背景.
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    mFooterView.setBackgroundDrawable(null);
                    break;
                case STATE_EMPTY_ITEM:
                    if (needShowFootWhenEmpty()) {
                        progress.setVisibility(View.GONE);
                        mFooterView.setVisibility(View.VISIBLE);
                        text.setText(_noDateText);
                        // 空数据的时候字体小,无背景.
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        mFooterView.setBackgroundDrawable(null);
                    } else {
                        mFooterView.setVisibility(View.GONE);
                    }
                    break;
                case STATE_NETWORK_ERROR:
                    mFooterView.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    text.setVisibility(View.VISIBLE);
                    if (TDevice.hasInternet()) {
                        text.setText(R.string.loading_error);
//                        text.setText("加载出错了");
                    } else {
                        text.setText(R.string.tip_network_error);
//                        text.setText("没有可用的网络");
                    }
                    break;
                default:
                    progress.setVisibility(View.GONE);
                    mFooterView.setVisibility(View.GONE);
                    text.setVisibility(View.GONE);
                    break;
                }
                return mFooterView;
            }
        }
        if (position < 0) {
            position = 0; // 若列表没有数据，是没有footview/headview的
        }
        return getRealView(position, convertView, parent);
    }

    protected boolean needShowFootWhenEmpty() {
        return false;
    }

    protected View getRealView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    private LinearLayout mFooterView;

    protected boolean hasFooterView(){
        return true;
    }

    public View getFooterView() {
        return this.mFooterView;
    }

    public void setFooterViewLoading(String loadMsg) {
        ProgressBar progress = (ProgressBar) mFooterView
                .findViewById(R.id.progressbar);
        TextView text = (TextView) mFooterView.findViewById(R.id.text);
        mFooterView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        text.setVisibility(View.VISIBLE);
        if (StringUtils.isEmpty(loadMsg)) {
            text.setText(_loadmoreText);
        } else {
            text.setText(loadMsg);
        }
    }

    public void setFooterViewLoading() {
        setFooterViewLoading("");
    }

    public void setFooterViewText(String msg) {
        ProgressBar progress = (ProgressBar) mFooterView
                .findViewById(R.id.progressbar);
        TextView text = (TextView) mFooterView.findViewById(R.id.text);
        mFooterView.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        text.setVisibility(View.VISIBLE);
        text.setText(msg);
    }

    public void HideFooterViewLoading() {
        mFooterView.setVisibility(View.INVISIBLE);
    }

    // TODO 支持文字和链接，表情等混排
    protected void setContent(TweetTextView contentView, String content) {
        contentView.setMovementMethod(MyLinkMovementMethod.a());
        contentView.setFocusable(false);
        contentView.setDispatchToParent(true);
        contentView.setLongClickable(false);
        Spanned span = Html.fromHtml(TweetTextView.modifyPath(content));
        span = InputHelper.displayEmoji(contentView.getResources(),
                span.toString());
        contentView.setText(span);
        MyURLSpan.parseLinkText(contentView, span);
    }

    protected void setText(TextView textView, String text, boolean needGone) {
        if (text == null || TextUtils.isEmpty(text)) {
            if (needGone) {
                textView.setVisibility(View.GONE);
            }
        } else {
            textView.setText(text);
        }
    }

    protected void setText(TextView textView, String text) {
        setText(textView, text, false);
    }


    protected boolean isHasNetAndLogin(Context context) {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return false;
        }
        if (isNeedLogin() && !AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(context);
            return false;
        }
        return true;
    }

    public boolean isNeedLogin() {
        return false;
    }


    // 分享
    public void handleShare(Context context, T obj) {
        if (mDatas == null || TextUtils.isEmpty(getShareContent(obj))
                || TextUtils.isEmpty(getShareUrl(obj)) || TextUtils.isEmpty(getShareTitle(obj))) {
            AppContext.showToast("内容加载失败...");
            return;
        }
        final ShareDialog dialog = new ShareDialog(context);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(R.string.share_to);
        dialog.setShareInfo(getShareTitle(obj), getShareContent(obj), null, getShareUrl(obj));
        dialog.show();
    }

    public String getShareTitle(T obj) {
        return "";
    }

    public String getShareContent(T obj) {
        return "";
    }

    public String getShareUrl(T obj) {
        return "";
    }
}

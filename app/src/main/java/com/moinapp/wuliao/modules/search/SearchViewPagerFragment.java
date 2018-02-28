package com.moinapp.wuliao.modules.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.adapter.ViewPageFragmentAdapter;
import com.moinapp.wuliao.base.BaseFragment;
import com.moinapp.wuliao.bean.Constants;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.interf.OnTabReselectListener;
import com.moinapp.wuliao.ui.InviteFriendDialog;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 搜索页面,包括搜人和搜标签
 *
 * @author liujiancheng
 */
public class SearchViewPagerFragment extends BaseFragment {
    private ILogger MyLog = LoggerFactory.getLogger("SearchViewPagerFragment");

    private int mInitTabIdx;
    private boolean mShowInvite;
    private String mSearchText;

    private static SearchTextChangedListener mTagListener;
    private static SearchTextChangedListener mTopicListener;
    private static SearchTextChangedListener mUserListener;
    private static SearchKeyBoardListener mTopicSearchListener;
    private static SearchKeyBoardListener mCosplaySearchListener;

    protected ViewPageFragmentAdapter mTabsAdapter;
    @InjectView(R.id.pager_tabstrip)
    protected PagerSlidingTabStrip mTabStrip;
    @InjectView(R.id.pager)
    protected ViewPager mViewPager;

    @InjectView(R.id.search_et)
    public EditText mSearchEdit;
    @InjectView(R.id.clear_iv)
    public ImageView mClear_iv;
    @InjectView(R.id.cancel)
    public TextView mCancel;

    @InjectView(R.id.iv_hint_search)
    public ImageView mHint_iv;
    @InjectView(R.id.tv_hint)
    public TextView mHint_tv;
    @InjectView(R.id.tv_invite_friend)
    public TextView mInviteFriend;

    @InjectView(R.id.title_layout)
    public LinearLayout titleLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_viewpage_fragment, null);
        ButterKnife.inject(this, view);
        initViews();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mTabsAdapter = new ViewPageFragmentAdapter(getChildFragmentManager(),
                mTabStrip, mViewPager);
        onSetupTabAdapter(mTabsAdapter);

        mTabStrip.setOnClickTabListener(new PagerSlidingTabStrip.OnClickTabListener() {
            @Override
            public void onClickTab(View tab, int index) {
                if (index == mViewPager.getCurrentItem()) {
                    try {
                        Fragment currentFragment = getChildFragmentManager().getFragments()
                                .get(index);
                        if (currentFragment != null
                                && currentFragment instanceof OnTabReselectListener) {
                            OnTabReselectListener listener = (OnTabReselectListener) currentFragment;
                            listener.onTabReselect();
                        }
                    } catch (NullPointerException e) {
                    }
                }
            }
        });

        if (mShowInvite) {
            mInviteFriend.setVisibility(View.VISIBLE);
        } else {
            mInviteFriend.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mInitTabIdx = args.getInt(Constants.BUNDLE_KEY_TABINDEX, 0);
        mShowInvite = args.getBoolean(Constants.BUNDLE_KEY_SHOW_INVITE, false);
    }

    public static void setTagListener(SearchTextChangedListener listener) {
        mTagListener = listener;
    }

    public static void setTopicListener(SearchTextChangedListener listener) {
        mTopicListener = listener;
    }

    public static void setUserListener(SearchTextChangedListener listener) {
        mUserListener = listener;
    }

    public static void setSearchTopicListener(SearchKeyBoardListener listener) {
        mTopicSearchListener = listener;
    }

    public static void setCosplayListener(SearchKeyBoardListener listener) {
        mCosplaySearchListener = listener;
    }

    private void initViews() {
        //设置默认缓存页数,保证切换界面时可以实时搜索
        mViewPager.setOffscreenPageLimit(2);
        //默认弹出键盘
        mSearchEdit.setFocusable(true);
        mSearchEdit.setFocusableInTouchMode(true);
        mSearchEdit.requestFocus();
        if (mInitTabIdx == 0) {
            showKeyboard();
        }
        //设置实时搜索
        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    hideHint();
                } else {
                    showHint();
                }
                mSearchText = editable.toString().trim();
                if (mSearchText != null && mSearchText.length() >= 1) {
                    notifySearchValid();
                } else {
                    notifySearchInvalid();
                }
            }
        });

        //设置键盘上搜索键的响应
        mSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    TDevice.hideSoftKeyboard(v);
                    if (mInitTabIdx == 1) {
                        notifySearchTopic();
                    } else {
                        if (mViewPager.getCurrentItem() == 0) {
                            if (TextUtils.isEmpty(mSearchText)) {
                                AppContext.toast(getActivity(), getString(R.string.invalid_search));
                                return false;
                            }
                            //点击键盘的搜索键后需要跳转图片的搜索结果界面
                            notifySearchCosplay();
                        } else if (mViewPager.getCurrentItem() == 1) {
                            if (TextUtils.isEmpty(mSearchText)) {
                                AppContext.toast(getActivity(), getString(R.string.invalid_search));
                                return false;
                            }
                            //点击键盘的搜索键后需要跳转话题的搜索结果界面
                            notifySearchTopic();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        //清除搜索输入框的文本
        mClear_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEdit.setText(null);
            }
        });

        mCancel.setOnClickListener(v -> {
            getActivity().finish();
        });

        mInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleInvite();
            }
        });
    }

    private void hideHint() {
        mHint_iv.setVisibility(View.GONE);
        mHint_tv.setVisibility(View.GONE);
        mClear_iv.setVisibility(View.VISIBLE);
    }

    private void showHint() {
        mHint_iv.setVisibility(View.VISIBLE);
        mHint_tv.setVisibility(View.VISIBLE);
        mClear_iv.setVisibility(View.GONE);
    }
    private void handleInvite() {
        final InviteFriendDialog dialog = new InviteFriendDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void notifySearchValid() {
        if (mInitTabIdx == 1) {
            if (mTopicListener != null) {
                mTopicListener.onSearchTextChanged(mSearchText);
            }
        } else {
            if (mTagListener != null) {
                mTagListener.onSearchTextChanged(mSearchText);
            }
            if (mTopicListener != null) {
                mTopicListener.onSearchTextChanged(mSearchText);
            }
            if (mUserListener != null) {
                mUserListener.onSearchTextChanged(mSearchText);
            }
        }
    }

    private void notifySearchInvalid() {
        if (mInitTabIdx == 1) {
            if (mTopicListener != null) {
                mTopicListener.onSearchTextInvalid();
            }
        } else {
            if (mTagListener != null) {
                mTagListener.onSearchTextInvalid();
            }
            if (mTopicListener != null) {
                mTopicListener.onSearchTextInvalid();
            }
            if (mUserListener != null) {
                mUserListener.onSearchTextInvalid();
            }
        }
    }

    private void notifySearchTopic() {
        if (mTopicSearchListener != null) {
            mTopicSearchListener.onSearchKeyClicked(mSearchText);
        }
    }

    private void notifySearchCosplay() {
        if (mCosplaySearchListener != null) {
            mCosplaySearchListener.onSearchKeyClicked(mSearchText);
        }
    }

    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(R.array.search);
        if (mInitTabIdx==1){
            adapter.addTab("话题", "tag", SearchTopicFragment.class, getBundle());
            titleLayout.setVisibility(View.GONE);
        }else {
            adapter.addTab(title[0], "picture", SearchTagFragment.class, getBundle());
            adapter.addTab(title[1], "topic", SearchTopicFragment.class, getBundle());
            adapter.addTab(title[2], "user", SearchUserFragment.class, getBundle());

            mViewPager.setCurrentItem(mInitTabIdx);
        }
    }

    /**
     * 为防止由于页面加载过程弹不出软键盘,延迟500毫秒再弹
     */
    private void showKeyboard() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) mSearchEdit.getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mSearchEdit, 0);
            }
        }, 500);
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("STYLE", R.style.search_text_bg);
        return bundle;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initView(View view) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initData() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.SEARCH_VIEWPAGER_FRAGMENT); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.SEARCH_VIEWPAGER_FRAGMENT);
    }
}

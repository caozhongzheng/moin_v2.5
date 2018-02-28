package com.moinapp.wuliao.modules.stickercamera.app.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseListFragment;
import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.FollowersList;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.fragment.FriendsFragment;
import com.moinapp.wuliao.modules.mine.MineApi;
import com.moinapp.wuliao.modules.stickercamera.app.camera.adapter.AtFollowerAdapter;
import com.moinapp.wuliao.util.XmlUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @用户的ui
 * @author liujiancheng
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AtFollowersFragment extends BaseListFragment<UserInfo> {
    public final static int PAGE_SIZE = 10;

    protected static final String TAG = FriendsFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "at_follower_list";
    private String mUid = ClientInfo.getUID();

    @InjectView(R.id.tv_left)
    protected RelativeLayout rl_Left;
    @InjectView(R.id.search_et)
    protected EditText searchEdit;
    @InjectView(R.id.clear_iv)
    protected ImageView clear_iv;

    @Override
    public void initView(View view) {
        super.initView(view);

        rl_Left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        searchEdit.clearFocus();
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                clear_iv.setVisibility(editable.toString().length() > 0 ? View.VISIBLE : View.GONE);
                mAdapter.clear();
                requestData(true);
            }
        });
        clear_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.setText(null);
            }
        });
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initView(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_followers;
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.AT_FOLLOWERS_FRAGMENT); //统计页面
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.AT_FOLLOWERS_FRAGMENT);
    }

    @Override
    protected AtFollowerAdapter getListAdapter() {
        return new AtFollowerAdapter(getActivity());
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCatalog + "_" + mUid;
    }

    @Override
    protected FollowersList parseList(InputStream is) throws Exception {
        FollowersList result = XmlUtils.JsontoBean(FollowersList.class, is);
        if (result == null) {
            return null;
        }
        return result;
    }

    @Override
    protected FollowersList readList(Serializable seri) {
        return ((FollowersList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null && ((UserInfo) enity).getUId() != null) {
            for (int i = 0; i < s; i++) {
                if (((UserInfo) enity).getUId().equalsIgnoreCase(((UserInfo) data.get(i)).getUId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void requestData(boolean refresh) {
        if (refresh) {
            String input = searchEdit.getText().toString().replaceAll(" ", "");
            MineApi.getMyIdols(mUid, input, null, mHandler);
        } else {
            sendRequestData();
        }
    }

    @Override
    protected void sendRequestData() {
        String lastid = null;
        if (mCurrentPage != 0 && mAdapter.getCount() > 0) {
            lastid = mAdapter.getItem(mAdapter.getData().size() - 1) != null ? mAdapter.getItem(mAdapter.getData().size() - 1).getUId() : null;
            Log.i("ljc", "lastid = " + lastid);
        }
        String input = searchEdit.getText().toString().replaceAll(" ", "");
        MineApi.getMyIdols(mUid, input, lastid, mHandler);
    }

    @Override
    protected int getPageSize() {
        return PAGE_SIZE;
    }
}

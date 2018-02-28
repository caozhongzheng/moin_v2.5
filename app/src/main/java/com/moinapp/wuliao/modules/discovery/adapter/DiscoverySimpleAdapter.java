package com.moinapp.wuliao.modules.discovery.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 3.2.7版本大家都在看列表适配器
 * Created by guyunfei on 16/6/8.11:02.
 */
public class DiscoverySimpleAdapter extends ListBaseAdapter<CosplayInfo> {

    private static final ILogger MyLog = LoggerFactory.getLogger(DiscoverySimpleAdapter.class.getSimpleName());
    private Activity mActivity;

    private Context context;
    final static int COLUMN = 2;
    private int mPosition = 0;
    private boolean animShow = true;

    public static String[] COSPLAY_TEXT = AppContext.getInstance().getString(R.string.discovery_cosplay_text).replaceAll(" ", "").split("\n");
    private String cosplayContent;

    public DiscoverySimpleAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getDataSize() {
        return (mDatas.size() + COLUMN - 1) / COLUMN;
    }

    //关注和发现页面在下拉时不显示正在加载的信息
    @Override
    public void setFooterViewLoading() {
        HideFooterViewLoading();
    }

    @Override
    protected View getRealView(final int position, View convertView,
                               final ViewGroup parent) {
        context = parent.getContext();
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        final ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_discovery_cosplay_item_simple, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        for (int i = 0; i < COLUMN; i++) {
            CosplayInfo cosplayInfo = (position * COLUMN + i < mDatas.size()) ? mDatas.get(position * COLUMN + i) : null;
            updateDiscoveryItem(vh, cosplayInfo, i, position);
        }

        return convertView;
    }

    private void updateDiscoveryItem(ViewHolder vh, CosplayInfo cosplayInfo, int i, int position) {
        FrameLayout item = null;
        switch (i) {
            case 0:
                item = vh.item0;
                break;
            case 1:
                item = vh.item1;
                break;
        }
        if (item == null) return;

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position >= 0 && position < 6) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(UmengConstants.ITEM_ID, cosplayInfo.getUcid() + "");
                    map.put(UmengConstants.FROM, "发现页");
                    MobclickAgent.onEvent(mActivity, UmengConstants.HOTPIC_CLICK, map);
                }
                UIHelper.showDiscoveryCosplayDetail(context, cosplayInfo, cosplayInfo.getUcid(), TimeUtils.getCurrentTimeInLong());
            }
        });

        if (cosplayInfo == null || cosplayInfo.getPicture() == null) {
            item.setVisibility(View.INVISIBLE);
            return;
        } else {
            item.setVisibility(View.VISIBLE);
        }

        RoundAngleImageView cosplayImage = (RoundAngleImageView) item.findViewById(R.id.image);
        TextView cosplayDesc = (TextView) item.findViewById(R.id.desc);
        if (cosplayInfo != null) {
            if (cosplayInfo.getPicture() != null) {
                ImageLoaderUtils.displayHttpImage(true, cosplayInfo.getPicture().getUri(), cosplayImage,
                        null, animShow, null);
            }

            if (cosplayInfo.getContent() != null && !cosplayInfo.getContent().equals("")) {
                cosplayContent = cosplayInfo.getContent();
            } else {
                cosplayContent = "";
                if (isNeedContentWhenDescEmpty() && COSPLAY_TEXT != null) {
                    Random random = new Random();
                    int x = random.nextInt(COSPLAY_TEXT.length);
                    cosplayContent = COSPLAY_TEXT[x];
                }
            }
            cosplayDesc.setText(cosplayContent);
        }
    }

    public boolean isNeedContentWhenDescEmpty() {
        return false;
    }

    @Override
    public boolean isNeedLogin() {
        return false;
    }

    @Override
    public void setFinishText(int id) {
        super.setFinishText(R.string.no_more_data);
    }

    static class ViewHolder {
        @InjectView(R.id.discovery_item0)
        FrameLayout item0;
        @InjectView(R.id.discovery_item1)
        FrameLayout item1;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

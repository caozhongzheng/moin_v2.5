package com.moinapp.wuliao.modules.mine.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.modules.discovery.model.CosplayInfo;
import com.moinapp.wuliao.util.ImageLoaderUtils;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.TDevice;
import com.moinapp.wuliao.util.TimeUtils;
import com.moinapp.wuliao.util.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 我的个人信息界面,发表的图片适配器
 *
 */
public class MyCosplayInfoAdapter extends ListBaseAdapter<CosplayInfo> {

    private Context context;
    final static int COLUMN = 3;
    private static int IMG_WIDTH = (int) (TDevice.getScreenWidth() / COLUMN);// - TDevice.dpToPixel(3f));
    private int mPosition = 0;
    private boolean animShow = true;
    @Override
    public int getDataSize() {
        return (mDatas.size() + COLUMN - 1) / COLUMN;
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_myinfo, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        for (int i = 0; i < COLUMN; i++) {
            CosplayInfo cosplayInfo = (position * COLUMN + i < mDatas.size()) ? mDatas.get(position * COLUMN + i) : null;
            setCosImage(cosplayInfo, i == 0 ? vh.left_item : i == 1 ? vh.middle_item : vh.right_item);
        }

        return convertView;
    }

    private void setCosImage(CosplayInfo cosplayInfo, ImageView imageView) {
        if (cosplayInfo == null || cosplayInfo.getPicture() == null || StringUtil.isNullOrEmpty(cosplayInfo.getPicture().getUri())) {
            imageView.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = IMG_WIDTH;
            params.height = IMG_WIDTH;
            imageView.setLayoutParams(params);
        } else {
            imageView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = IMG_WIDTH;
            params.height = IMG_WIDTH;
            imageView.setLayoutParams(params);

            ImageLoaderUtils.displayHttpImage(cosplayInfo.getPicture().getUri(), imageView, null, animShow, null);
            imageView.setOnClickListener(v -> {
//                if(getFrom() == 2) {
//                    // 来自我的空间图片
//                    Bundle args = new Bundle();
//                    args.putSerializable(DiscoveryConstants.COSPLAY_INFO, cosplayInfo);
//                    args.putString(DiscoveryConstants.UCID, cosplayInfo.getUcid());
//                    android.util.Log.i("mca", "来自我的空间图片:" + cosplayInfo.toString());
//                    UIHelper.showSimpleBack(context, SimpleBackPage.CROP_COSPLAY, args);
//                } else {
                // 来自我的个人信息页图片
                // TODO 进入图片详情
//                android.util.Log.i("mca", "进入图片详情:" + cosplayInfo.toString());
                UIHelper.showDiscoveryCosplayDetail(context, cosplayInfo, cosplayInfo.getUcid(), TimeUtils.getCurrentTimeInLong());
//                }
            });
        }
    }

    @Override
    public boolean isNeedLogin() {
        return true;
    }

    @Override
    public void setFinishText(int id) {
        super.setFinishText(R.string.no_more_pics);
    }

    private int from;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    static class ViewHolder {

        @InjectView(R.id.left_item)
        ImageView left_item;
        @InjectView(R.id.middle_item)
        ImageView middle_item;
        @InjectView(R.id.right_item)
        ImageView right_item;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

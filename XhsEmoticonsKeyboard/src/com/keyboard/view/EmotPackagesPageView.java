package com.keyboard.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.keyboard.adpater.EmotPackagesAdapter;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.utils.EmoticonsKeyboardBuilder;
import com.keyboard.utils.Utils;
import com.keyboard.view.I.IEmoticonsKeyboard;
import com.keyboard.view.I.IView;
import com.keyboard.view.I.OnEmoticonsPageViewListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 贴纸包一级界面
 * Created by moin on 2016/5/27.
 */
public class EmotPackagesPageView extends ViewPager implements IEmoticonsKeyboard, IView {

    private static final String TAG = EmotPackagesPageView.class.getSimpleName();
    private Context mContext;
    private int mHeight = 0;
    private int mMaxEmoticonSetPageCount = 0;
    public int mOldPagePosition = -1;

    private List<EmoticonSetBean> mEmoticonSetBeanList;
    private EmoticonsViewPagerAdapter mEmoticonsViewPagerAdapter;
    private ArrayList<View> mEmoticonPageViews = new ArrayList<View>();

    public EmotPackagesPageView(Context context) {
        this(context, null);
    }

    public EmotPackagesPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        updateSelf(h);
    }

    public void updateSelf(int h) {
        mHeight = h;
        Log.i(TAG, "updateSelf h=" + h);
    }


    public void updateView() {
        if (mEmoticonSetBeanList == null || mEmoticonSetBeanList.isEmpty()) {
            return;
        }
        mEmoticonsViewPagerAdapter = null;

        Log.i(TAG, "updateView mEmoticonSetBeanList.size=" + mEmoticonSetBeanList.size());
        int screenWidth = Utils.getDisplayWidthPixels(mContext);
        int maxPagerHeight = mHeight;

        mEmoticonPageViews.clear();

        int emoticonSetSum = mEmoticonSetBeanList.size();
        int row = 4;
        int line = 2;
        int horizontalSpacing = 0;
        int verticalSpacing = 5;

        int pageCount = mMaxEmoticonSetPageCount = getPageCount();

        int everyPageMaxSum = row * line;
        int start = 0;
        int end = everyPageMaxSum > emoticonSetSum ? emoticonSetSum : everyPageMaxSum;

        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        gridParams.addRule(ResizeLayout.CENTER_VERTICAL);

        int itemHeight = Math.min((screenWidth - (row - 1) * Utils.dip2px(mContext, horizontalSpacing)) / row,
                (maxPagerHeight - (line - 1) * Utils.dip2px(mContext, verticalSpacing)) / line);

        for (int i = 0; i < pageCount; i++) {
            RelativeLayout rl = new RelativeLayout(mContext);
            GridView gridView = new GridView(mContext);
            gridView.setMotionEventSplittingEnabled(false);
            gridView.setNumColumns(row);
            gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            gridView.setCacheColorHint(0);
            gridView.setHorizontalSpacing(Utils.dip2px(mContext, horizontalSpacing));
            gridView.setVerticalSpacing(Utils.dip2px(mContext, 1));
            gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            gridView.setGravity(Gravity.CENTER);
            gridView.setVerticalScrollBarEnabled(false);

            List<EmoticonSetBean> list = new ArrayList<>();
            for (int j = start; j < end; j++) {
                list.add(mEmoticonSetBeanList.get(j));
            }

            while (list.size() < everyPageMaxSum) {
                android.util.Log.i(TAG, "add nullB @" + list.size());
                list.add(null);
            }

            EmotPackagesAdapter adapter = new EmotPackagesAdapter(mContext, list);
            adapter.setHeight(itemHeight, Utils.dip2px(mContext, 4/*bean.getItemPadding()*/));
            android.util.Log.i(TAG, "itemHeight=" + itemHeight);
//                    adapter.notifyDataSetChanged();
            gridView.setAdapter(adapter);
            rl.addView(gridView, gridParams);
            mEmoticonPageViews.add(rl);
            adapter.setOnItemListener(this);

            start = everyPageMaxSum + i * everyPageMaxSum;
            end = everyPageMaxSum + (i + 1) * everyPageMaxSum;
            if (end >= emoticonSetSum) {
                end = emoticonSetSum;
            }
        }


        if (mEmoticonsViewPagerAdapter == null) {
            mEmoticonsViewPagerAdapter = new EmoticonsViewPagerAdapter();
            setAdapter(mEmoticonsViewPagerAdapter);
            if (mOnEmoticonsPageViewListener != null) {
                mOnEmoticonsPageViewListener.emoticonsPageViewCountChanged(pageCount);
            }
            setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    if (mOldPagePosition < 0) {
                        mOldPagePosition = 0;
                    }
                    if (mOnEmoticonsPageViewListener != null) {
                        mOnEmoticonsPageViewListener.playBy(mOldPagePosition, position);
                    }
                    mOldPagePosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {}
            });
        }

        mEmoticonsViewPagerAdapter.notifyDataSetChanged();
        invalidate();

        if(mOnEmoticonsPageViewListener != null) {
            mOnEmoticonsPageViewListener.emoticonsPageViewInitFinish(mMaxEmoticonSetPageCount);
            if (mOldPagePosition < 0) {
                mOldPagePosition = 0;
            }
            if (mOldPagePosition >= mEmoticonSetBeanList.size()) {
                mOldPagePosition = mEmoticonSetBeanList.size() - 1;
            }
            // 这句话可要可不要
            mOnEmoticonsPageViewListener.emoticonsPageViewCountChanged(pageCount);
        }
    }

    public void setPageSelect(int position) {
        if (getAdapter() == null) {
            Log.i(TAG, "defaultUse getAdapter=null");
        }
        if (getAdapter() != null && position >= 0 && position < mEmoticonSetBeanList.size()) {
            setCurrentItem(position);

            mOldPagePosition = position;
        }
    }

    public int getPageHeight() {
        return mHeight;
    }
    public int getPageCount() {
        int pageCount = 0;
        if (mEmoticonSetBeanList != null) {
            if(mEmoticonSetBeanList.size() < 8) {
                pageCount = 1;
            } else {
                pageCount = (int) Math.ceil((double) mEmoticonSetBeanList.size() / 8);
            }
        }
        return pageCount;
    }

    @Override
    public void setBuilder(EmoticonsKeyboardBuilder builder) {
        if(null != mEmoticonSetBeanList && mEmoticonSetBeanList.size() > 0)
            mEmoticonSetBeanList.clear();
        mEmoticonSetBeanList = builder.builder.getEmoticonSetBeanList();
    }

    public void addEmoticonSetBeanIntoList(EmoticonSetBean mEmoticonSetBean, boolean start) {
        if (mEmoticonSetBean == null) return;
        if(null == mEmoticonSetBeanList) {
            mEmoticonSetBeanList = new ArrayList<>();
        }

        if (start) {
            mEmoticonSetBeanList.add(0, mEmoticonSetBean);
        } else {
            mEmoticonSetBeanList.add(mEmoticonSetBean);
        }
    }

    private class EmoticonsViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mEmoticonPageViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mEmoticonPageViews.get(arg1));
            return mEmoticonPageViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }


    @Override
    public void onItemClick(EmoticonBean bean) {
        if (mIViewListeners != null && !mIViewListeners.isEmpty()) {
            for (IView listener : mIViewListeners) {
                listener.onItemClick(bean);
            }
        }
    }

    @Override
    public boolean onItemLongClick(int position, View converView, EmoticonBean bean) {
        boolean result = false;
        if (mIViewListeners != null && !mIViewListeners.isEmpty()) {
            for (IView listener : mIViewListeners) {
                result |= listener.onItemLongClick(position, converView, bean);
            }
        }
        return result;
    }

    @Override
    public void onItemDisplay(EmoticonBean bean) {

    }

    @Override
    public void onPageChangeTo(int position) {

    }

    private List<IView> mIViewListeners;

    public void addIViewListener(IView listener) {
        if (mIViewListeners == null) {
            mIViewListeners = new ArrayList<IView>();
        }
        mIViewListeners.add(listener);
    }

    public void setIViewListener(IView listener) {
        addIViewListener(listener);
    }

    public void resetIViewListener(IView listener) {
        if (mIViewListeners == null) {
            mIViewListeners = new ArrayList<IView>();
        }
        mIViewListeners.clear();
        mIViewListeners.add(listener);
    }

    private OnEmoticonsPageViewListener mOnEmoticonsPageViewListener;
    public void setOnIndicatorListener(OnEmoticonsPageViewListener listener) {
        mOnEmoticonsPageViewListener = listener;
    }
}

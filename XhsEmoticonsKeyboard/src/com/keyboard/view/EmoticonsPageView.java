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

import com.keyboard.adpater.EmoticonsAdapter;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.utils.EmoticonsKeyboardBuilder;
import com.keyboard.utils.Utils;
import com.keyboard.view.I.IEmoticonsKeyboard;
import com.keyboard.view.I.IView;
import com.keyboard.view.I.OnEmoticonsPageViewListener;

import java.util.ArrayList;
import java.util.List;

public class EmoticonsPageView extends ViewPager implements IEmoticonsKeyboard, IView {

    private static final String TAG = "epv";
    private Context mContext;
    private int mHeight = 0;
    private int mMaxEmoticonSetPageCount = 0;
    public int mOldPagePosition = -1;

    private List<EmoticonSetBean> mEmoticonSetBeanList;
    private EmoticonsViewPagerAdapter mEmoticonsViewPagerAdapter;
    private ArrayList<View> mEmoticonPageViews = new ArrayList<View>();

    public EmoticonsPageView(Context context) {
        this(context, null);
    }

    public EmoticonsPageView(Context context, AttributeSet attrs) {
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
//
//        EmoticonsPageView.this.post(new Runnable() {
//            @Override
//            public void run() {
//                updateView();
//            }
//        });
    }


    public void updateView() {
        if (mEmoticonSetBeanList == null) {
            return;
        }
        mEmoticonsViewPagerAdapter = null;

//        Log.i(TAG, "updateView mEmoticonSetBeanList.size=" + mEmoticonSetBeanList.size());
        int screenWidth = Utils.getDisplayWidthPixels(mContext);
        int maxPagerHeight = mHeight;

        mEmoticonPageViews.clear();

        for (EmoticonSetBean bean : mEmoticonSetBeanList) {
            ArrayList<EmoticonBean> emoticonList = bean.getEmoticonList();
//            android.util.Log.i("czz", "1111=" + bean.getName() + " has " + emoticonList.size());
            if (emoticonList != null) {
                int emoticonSetSum = emoticonList.size();
                int row = bean.getRow();
                int line = bean.getLine();

                int del = bean.isShowDelBtn() ? 1 : 0;
                int everyPageMaxSum = row * line - del;
                int pageCount = getPageCount(bean);

                mMaxEmoticonSetPageCount = Math.max(mMaxEmoticonSetPageCount, pageCount);

                int start = 0;
                int end = everyPageMaxSum > emoticonSetSum ? emoticonSetSum : everyPageMaxSum;

                RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                gridParams.addRule(ResizeLayout.CENTER_VERTICAL);

                int itemHeight = Math.min((screenWidth - (bean.getRow() - 1) * Utils.dip2px(mContext, bean.getHorizontalSpacing())) / bean.getRow(),
                        (maxPagerHeight - (bean.getLine() - 1) * Utils.dip2px(mContext, bean.getVerticalSpacing())) / bean.getLine());

//                // 计算行距
//                if (bean.getHeight() > 0) {
//                    int verticalspacing = Utils.dip2px(mContext, bean.getVerticalSpacing());
//                    itemHeight = Math.min(itemHeight,Utils.dip2px(mContext, bean.getHeight()));
//                    while (verticalspacing > 0) {
//                        int userdefHeigth = (bean.getLine() - 1) * verticalspacing + Utils.dip2px(mContext, bean.getHeight()) * (bean.getLine());
//                        if (userdefHeigth <= maxPagerHeight) {
//                            bean.setVerticalSpacing(Utils.px2dip(mContext, verticalspacing));
//                            break;
//                        }
//                        bean.setVerticalSpacing(Utils.px2dip(mContext, verticalspacing));
//                        verticalspacing = (int)Math.ceil((float)verticalspacing / 2);
//                    }
//                }

                for (int i = 0; i < pageCount; i++) {
                    RelativeLayout rl = new RelativeLayout(mContext);
                    GridView gridView = new GridView(mContext);
                    gridView.setMotionEventSplittingEnabled(false);
                    gridView.setNumColumns(bean.getRow());
//                    gridView.setBackgroundColor(getResources().getColor(R.color.common_sticker_grey));
                    gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                    gridView.setCacheColorHint(0);
                    gridView.setHorizontalSpacing(Utils.dip2px(mContext, bean.getHorizontalSpacing()));
                    gridView.setVerticalSpacing(Utils.dip2px(mContext, 1));
                    gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
                    gridView.setGravity(Gravity.CENTER);
                    gridView.setVerticalScrollBarEnabled(false);

//                    android.util.Log.i("czz", "hs=" + bean.getHorizontalSpacing());
//                    android.util.Log.i("czz", "vs=" + bean.getVerticalSpacing());
//                    android.util.Log.i("czz", "start=" + start + ", end=" + end);
//                    android.util.Log.i("czz", "sbean=" + emoticonList.get(start).toString());
//                    android.util.Log.i("czz", "ebean=" + emoticonList.get(end-1).toString());

                    List<EmoticonBean> list = new ArrayList<EmoticonBean>();
                    for (int j = start; j < end; j++) {
//                        android.util.Log.i("czz", j+"=" + start + ", emoticonList.get(j)=" + emoticonList.get(j).toString());
                        list.add(emoticonList.get(j));
                    }

                    // 删除按钮
                    if (bean.isShowDelBtn()) {
                        int count = bean.getLine() * bean.getRow();
                        while (list.size() < count - 1) {
//                            android.util.Log.i("czz", "add nullA @" + list.size());
                            list.add(null);
                        }
                        EmoticonBean delBean = new EmoticonBean();
                        delBean.setEventType(EmoticonBean.FACE_TYPE_DEL);
                        delBean.setIconUri("drawable://icon_del");
//                        android.util.Log.i("czz", "add 删除按钮@" + list.size());
                        list.add(delBean);
                    } else {
                        int count = bean.getLine() * bean.getRow();
                        while (list.size() < count) {
//                            android.util.Log.i("czz", "add nullB @" + list.size());
                            list.add(null);
                        }
                    }

                    EmoticonsAdapter adapter = new EmoticonsAdapter(mContext, list);
                    adapter.setHeight(itemHeight, Utils.dip2px(mContext, 4/*bean.getItemPadding()*/));
//                    android.util.Log.i("czz", "h=" + itemHeight + ", padding=" + bean.getItemPadding());
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
            }
        }

        if (mEmoticonsViewPagerAdapter == null) {
            mEmoticonsViewPagerAdapter = new EmoticonsViewPagerAdapter();
            setAdapter(mEmoticonsViewPagerAdapter);
            setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    if (mOldPagePosition < 0) {
                        mOldPagePosition = 0;
                    }
                    int end = 0;
                    int pagerPosition = 0;
                    for (EmoticonSetBean emoticonSetBean : mEmoticonSetBeanList) {

                        int size = getPageCount(emoticonSetBean);

                        if (end + size > position) {
                            if (mOnEmoticonsPageViewListener != null) {
                                mOnEmoticonsPageViewListener.emoticonsPageViewCountChanged(size);
                            }
                            // 上一页
                            if (mOldPagePosition - end >= size) {
                                if (position - end >= 0) {
                                    if (mOnEmoticonsPageViewListener != null) {
                                        mOnEmoticonsPageViewListener.playTo(position - end);
                                    }
                                }
                                if (mIViewListeners != null && !mIViewListeners.isEmpty()) {
                                    for (IView listener : mIViewListeners) {
                                        listener.onPageChangeTo(pagerPosition);

                                        Log.i(TAG, "onPageChangeTo previous=" + pagerPosition);
                                    }
                                }
                                break;
                            }
                            // 下一页
                            if (mOldPagePosition - end < 0) {
                                if (mOnEmoticonsPageViewListener != null) {
                                    mOnEmoticonsPageViewListener.playTo(0);
                                }
                                if (mIViewListeners != null && !mIViewListeners.isEmpty()) {
                                    for (IView listener : mIViewListeners) {
                                        listener.onPageChangeTo(pagerPosition);

                                        Log.i(TAG, "onPageChangeTo next=" + pagerPosition);
                                    }
                                }
                                break;
                            }
                            // 本页切换
                            if (mOnEmoticonsPageViewListener != null) {
                                mOnEmoticonsPageViewListener.playBy(mOldPagePosition - end, position - end);
                            }
                            break;
                        }
                        pagerPosition++;
                        end += size;
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
            EmoticonSetBean setBean = mOldPagePosition < 0 ? null : mEmoticonSetBeanList.get(mOldPagePosition);
            if (setBean != null) {
                int currtCount = getPageCount(setBean);
                mOnEmoticonsPageViewListener.emoticonsPageViewCountChanged(currtCount);
            }
        }
    }

    public void setPageSelect(int position) {
        if (getAdapter() == null) {
            Log.i(TAG, "defaultUse getAdapter=null");
        }
        if (getAdapter() != null && position >= 0 && position < mEmoticonSetBeanList.size()) {
            int count = 0;
            for (int i = 0; i < position; i++) {
                count += getPageCount(mEmoticonSetBeanList.get(i));
            }

            Log.i(TAG, "defaultUse setCurrentItem(" + position + ")=" + count);
//            Log.i(TAG, "defaultUse setPageSelect setCurrentItem("+position+")="+count);
            setCurrentItem(count);

            mOldPagePosition = position;
        }
    }

    public void setPageSelect(int position, int playBy) {
        if (getAdapter() != null && position >= 0 && position < mEmoticonSetBeanList.size()) {

            int count = 0;
            for (int i = 0; i < position; i++) {
                count += getPageCount(mEmoticonSetBeanList.get(i));
            }
            Log.i(TAG, "defaultUse setPageSelect setCurrentItem("+position+")="+count);
            setCurrentItem(count);

            mOldPagePosition = position;
            // 本页切换
            if (mOnEmoticonsPageViewListener != null) {
                if (playBy < 0) {
                    playBy = 0;
                }
                int pageCount = getPageCount(mEmoticonSetBeanList.get(position));
                if (playBy >= pageCount) {
                    playBy = pageCount - 1;
                }
                if (playBy >= 0) {
                    mOnEmoticonsPageViewListener.playBy(0, playBy);
                }
            }
        }
    }

    public void refresh() {
        if (getAdapter() != null) {
            getAdapter().notifyDataSetChanged();
            try {
                int curItem = getCurrentItem();
                Log.i(TAG, "curItem= " + curItem + ", mOldPagePosition=" + mOldPagePosition);
                if (curItem >= 0 && curItem < mEmoticonPageViews.size()) {
                    RelativeLayout rl = (RelativeLayout) mEmoticonPageViews.get(curItem);
                    Log.i(TAG, "get RL= OK " + (rl != null));
                    GridView gridView = (GridView) rl.getChildAt(0);
                    Log.i(TAG, "get GridView= OK " + (gridView != null));
                    EmoticonsAdapter adapter = (EmoticonsAdapter) gridView.getAdapter();
                    Log.i(TAG, "get EmoticonsAdapter= OK " + (adapter != null));
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, e.toString());
            }
        }
    }

    public int getPageHeight() {
        return mHeight;
    }
    public int getPageCount(EmoticonSetBean emoticonSetBean) {
        int pageCount = 0;
        if (emoticonSetBean != null && emoticonSetBean.getEmoticonList() != null) {
            int del = emoticonSetBean.isShowDelBtn() ? 1 : 0;
            int everyPageMaxSum = emoticonSetBean.getRow() * emoticonSetBean.getLine() - del;
            if(emoticonSetBean.getEmoticonList().size() < everyPageMaxSum) {
                pageCount = 1;
            } else {
                pageCount = (int) Math.ceil((double) emoticonSetBean.getEmoticonList().size() / everyPageMaxSum);
            }
//            pageCount = pageCount != 0 ? pageCount : (emoticonSetBean.getEmoticonList().size() > 0 ? 1 : 0);
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


    public List<EmoticonSetBean> getEmoticonSetBeanList() {
        return mEmoticonSetBeanList;
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

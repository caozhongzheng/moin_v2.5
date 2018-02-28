package com.keyboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.utils.EmoticonsKeyboardBuilder;
import com.keyboard.utils.imageloader.ImageLoader;
import com.keyboard.view.I.IEmoticonsKeyboard;
import com.nineoldandroids.view.ViewHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmoticonsToolBarView extends RelativeLayout implements IEmoticonsKeyboard {

    private LayoutInflater inflater;
    private Context mContext;
    private HorizontalScrollView hsv_toolbar;
    private LinearLayout ly_parent;
    private LinearLayout ly_tool;

    private List<EmoticonSetBean> mEmoticonSetBeanList;
    private ArrayList<View> mToolBtnList = new ArrayList<View>();
    private int mBtnWidth = 60;

    public EmoticonsToolBarView(Context context) {
        this(context, null);
    }

    public EmoticonsToolBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_emoticonstoolbar, this);
        this.mContext = context;
        findView();
    }

    private void findView() {
        hsv_toolbar = (HorizontalScrollView) findViewById(R.id.hsv_toolbar);
        ly_parent = (LinearLayout) findViewById(R.id.ly_parent);
        ly_tool = (LinearLayout) findViewById(R.id.ly_tool);
    }

    private void scrollToBtnPosition(final int position){
        int childCount = ly_tool.getChildCount();
        if(position < childCount){
            hsv_toolbar.post(new Runnable() {
                @Override
                public void run() {
                    // 多考虑的childWidth * 5 是去除滤镜,声音,彩色文字,贴纸和最近5个按钮的影响
                    int mScrollX = hsv_toolbar.getScrollX();

                    int childX = (int)ViewHelper.getX(ly_tool.getChildAt(position));
                    int childWidth = ly_tool.getChildAt(position).getWidth();
                    int scrollLeft = mScrollX - childWidth * 5;

                    if(childX < scrollLeft){
                        hsv_toolbar.scrollTo(childX, 0);
                        return;
                    }

                    int hsvWidth = hsv_toolbar.getWidth();
                    int childRight = childX + childWidth * 6;
                    int scrollRight = mScrollX + hsvWidth;

                    if(scrollRight < childRight){
                        hsv_toolbar.scrollBy(childRight - scrollRight, 0);
                        return;
                    }
                }
            });
        }
    }

    public void setToolBtnSelect(int select) {
        if (select < 0) {
            for (int i = 0; i < mToolBtnList.size(); i++) {
                mToolBtnList.get(i).findViewById(R.id.ly_toolbtn).setBackgroundColor(getResources().getColor(R.color.white));
            }
            return;
        }
        scrollToBtnPosition(select);
        for (int i = 0; i < mToolBtnList.size(); i++) {
            if (select == i) {
                mToolBtnList.get(i).findViewById(R.id.ly_toolbtn).setBackgroundColor(getResources().getColor(R.color.list_item_background_pressed));
                mToolBtnList.get(i).findViewById(R.id.tv_flag).setVisibility(GONE);
            } else {
                mToolBtnList.get(i).findViewById(R.id.ly_toolbtn).setBackgroundColor(getResources().getColor(R.color.white));
            }
        }
    }

    public void onToolBtnSelect(final int finalI) {
        if (mItemClickListeners != null && !mItemClickListeners.isEmpty()) {
            for (OnToolBarItemClickListener listener : mItemClickListeners) {
                listener.onToolBarItemClick(finalI);
                setToolBtnSelect(finalI);
            }
        }
    }

    public int getBtnWidth(){
        return getResources().getDimensionPixelSize(R.dimen.bar_width);
    }
    public void setBtnWidth(int width){
        mBtnWidth = width;
    }

    public void addData(int rec){
        if(ly_tool != null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View toolBtnView = inflater.inflate(R.layout.item_toolbtn,null);
            ImageView iv_icon = (ImageView)toolBtnView.findViewById(R.id.iv_icon);
            iv_icon.setImageResource(rec);
            RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(getBtnWidth(), LayoutParams.MATCH_PARENT);
//            iv_icon.setLayoutParams(imgParams);
            ly_tool.addView(toolBtnView, imgParams);
            final int position = mToolBtnList.size();
            mToolBtnList.add(toolBtnView);
            iv_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListeners != null && !mItemClickListeners.isEmpty()) {
                        for (OnToolBarItemClickListener listener : mItemClickListeners) {
                            listener.onToolBarItemClick(position);
                        }
                    }
                }
            });
        }
    }

    private int getIdValue(){
        int childCount = getChildCount();
        int id = 1;
        if(childCount == 0){
            return id;
        }
        boolean isKeep = true;
        while (isKeep){
            isKeep = false;
            Random random = new Random();
            id = random.nextInt(100);
            for(int i = 0 ; i < childCount ; i++){
                if(getChildAt(i).getId() == id){
                    isKeep = true;
                    break;
                }
            }
        }
        return id;
    }

    public void addFixedView(View view , boolean isRight){
        int width = (getResources().getDimensionPixelSize(R.dimen.bar_width)
//                + getResources().getDimensionPixelSize(R.dimen.verticalspit_view_width)
        );
//                * 2; // 有滤镜,所以要宽度*2. 2.7版本不要滤镜了.
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams hsvParams = (RelativeLayout.LayoutParams) hsv_toolbar.getLayoutParams();
        if(view.getId() <= 0){
            view.setId(getIdValue());
        }
        if(isRight){
            params.addRule(ALIGN_PARENT_RIGHT);
            hsvParams.addRule(LEFT_OF, view.getId());
        }
        else{
            params.addRule(ALIGN_PARENT_LEFT);
            hsvParams.addRule(RIGHT_OF,view.getId());
        }
        addView(view, params);
        hsv_toolbar.setLayoutParams(hsvParams);
    }

    public int addInnerView(View view, int count, boolean isRight){
        int width = (getResources().getDimensionPixelSize(R.dimen.bar_width)
//                + getResources().getDimensionPixelSize(R.dimen.verticalspit_view_width)
        )
                * count;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.MATCH_PARENT);
        if(view.getId() <= 0){
            view.setId(getIdValue());
        }
        if(isRight){
            ly_parent.addView(view, 1, params);
        } else{
            ly_parent.addView(view, 0, params);
        }
        return view.getId();
    }

    @Override
    public void setBuilder(EmoticonsKeyboardBuilder builder) {
        setBuilder(builder, 0);
    }


    public void setBuilder(EmoticonsKeyboardBuilder builder, int position) {
        if(null != mEmoticonSetBeanList && mEmoticonSetBeanList.size() > 0)
            mEmoticonSetBeanList.clear();
        mEmoticonSetBeanList = builder.builder == null ? null : builder.builder.getEmoticonSetBeanList();
        if(mEmoticonSetBeanList == null){
            return;
        }

        int i = 0;
        ly_tool.removeAllViews();
        mToolBtnList.clear();
        for(EmoticonSetBean bean : mEmoticonSetBeanList){
            if (bean == null) {
                continue;
            }
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View toolBtnView = inflater.inflate(R.layout.item_toolbtn, null);
            RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(getBtnWidth(), LayoutParams.MATCH_PARENT);
            toolBtnView.setLayoutParams(imgParams);
            ImageView iv_icon = (ImageView)toolBtnView.findViewById(R.id.iv_icon);
            TextView tv_name = (TextView) toolBtnView.findViewById(R.id.tv_name);
            if (bean.getName() == null || bean.getName().length() < 7) {
                tv_name.setText(bean.getName());
            } else {
                tv_name.setText(bean.getName().substring(0, 5) + "…");
            }
            TextView tv_flag = (TextView) toolBtnView.findViewById(R.id.tv_flag);
            tv_flag.setVisibility((bean.getFlag() & 0x1) == 1 ? VISIBLE : GONE);
            ly_tool.addView(toolBtnView, imgParams);

            if (bean.getIconUri() != null && !bean.getIconUri().isEmpty()) {
                String filePath = bean.getIconUri().replaceFirst("file://", "");
                if ((new File(filePath)).exists()) {
                    try {
                        ImageLoader.getInstance(mContext).displayImage(bean.getIconUri(), iv_icon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    // 如果表情图标被删除，则加载url的图
                    try {
//                        Log.i(TAG, bean.getName()+"load from url:"+bean.getIconUrl());
                        ImageLoader.getInstance(mContext).displayImage(bean.getIconUrl(), iv_icon);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
//            if (bean.getStickType() == 100) {
//                iv_icon.setImageResource(R.drawable.icon_recently);
//            }
            mToolBtnList.add(toolBtnView);

            final int finalI = i;
            toolBtnView.findViewById(R.id.ly_toolbtn).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListeners != null && !mItemClickListeners.isEmpty()) {
                        for (OnToolBarItemClickListener listener : mItemClickListeners) {
                            listener.onToolBarItemClick(finalI);
                            setToolBtnSelect(finalI);
                        }
                    }
                }
            });
            i++;
        }
        setToolBtnSelect(position);
    }

    public List<EmoticonSetBean> getEmoticonSetBeanList() {
        return mEmoticonSetBeanList;
    }

    public int getTooBtnSize() {
        return mToolBtnList.size();
    }
    private List<OnToolBarItemClickListener> mItemClickListeners;
    public interface OnToolBarItemClickListener {
        void onToolBarItemClick(int position);
    }
    public void addOnToolBarItemClickListener(OnToolBarItemClickListener listener) {
        if (mItemClickListeners == null) {
            mItemClickListeners = new ArrayList<OnToolBarItemClickListener>();
        }
        mItemClickListeners.add(listener);
    }
    public void setOnToolBarItemClickListener(OnToolBarItemClickListener listener) { addOnToolBarItemClickListener(listener);}
}

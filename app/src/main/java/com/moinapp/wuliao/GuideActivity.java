package com.moinapp.wuliao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.ui.MainActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by moying on 15/8/4.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {
    /**
     * Called when the activity is first created.
     */
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide_layout);

        // 初始化viewPager
        viewPager = (ViewPager) findViewById(R.id.vp_guide);
        viewPager.setAdapter(new PicAdapter());
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            gotoMoin();
        }
        return super.onKeyDown(keyCode, event);
    }

    public int[] ids = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    class PicAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return ids.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (ids.length < position && position < 100) {
                return null;
            }

            View imageLayout = LayoutInflater.from(GuideActivity.this).inflate(R.layout.banner_viewpager_item, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            imageView.setImageResource(ids[position % ids.length]);

            if (position == getCount() - 1) {
                imageView.setOnClickListener(v -> {
                    gotoMoin();
                });
            }

            ((ViewPager) container).addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

    }

    private void gotoMoin() {
        MinePreference.getInstance().setFirstEnter(false);

        //启动的时候根据是否登录分别跳转至关注和发现
//        int tabIndex = AppContext.getInstance().isLogin() ? 0 : 1;
//        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
//        intent.putExtra(MainActivity.BUNDLE_KEY_TABINDEX, tabIndex);
//        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("GuideActivity"); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("GuideActivity"); //
        MobclickAgent.onPause(this);
    }
}

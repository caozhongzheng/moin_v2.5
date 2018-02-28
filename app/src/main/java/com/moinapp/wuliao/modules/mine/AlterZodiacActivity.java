package com.moinapp.wuliao.modules.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 选择星座
 * Created by moying on 15/5/12.
 */
public class AlterZodiacActivity extends Activity {

    private ILogger MyLog = LoggerFactory.getLogger("login");
    @InjectView(R.id.zodiac_lv)
    GridView zodiac_lv;
    @InjectView(R.id.alter_zodiac_cancel)
    TextView alter_zodiac_cancel;

    private int zodiac_i = PersonalInfoActivity.ZODIAC_NONE;
    ArrayList<HashMap<String, String>> zodiac_listitem = new ArrayList<HashMap<String, String>>();
    public static final int[] zodiac_name_arr = new int[]{
            R.string.zodiac_name1, R.string.zodiac_name2, R.string.zodiac_name3,
            R.string.zodiac_name4, R.string.zodiac_name5, R.string.zodiac_name6,
            R.string.zodiac_name7, R.string.zodiac_name8, R.string.zodiac_name9,
            R.string.zodiac_name10, R.string.zodiac_name11, R.string.zodiac_name12,
    };
    public static final int[] zodiac_date_arr = new int[]{
            R.string.zodiac_date1, R.string.zodiac_date2, R.string.zodiac_date3,
            R.string.zodiac_date4, R.string.zodiac_date5, R.string.zodiac_date6,
            R.string.zodiac_date7, R.string.zodiac_date8, R.string.zodiac_date9,
            R.string.zodiac_date10, R.string.zodiac_date11, R.string.zodiac_date12,
    };
    public static final int[] zodiac_png_arr = new int[]{
            R.drawable.star_aquarius, R.drawable.star_pisces, R.drawable.star_aries,
            R.drawable.star_taurus, R.drawable.star_gemini, R.drawable.star_cancer,
            R.drawable.star_leo, R.drawable.star_virgo, R.drawable.star_libra,
            R.drawable.star_scorpio, R.drawable.star_sagittarius, R.drawable.star_capricorn,
    };
    public static final int[] zodiac_select_png_arr = new int[]{
            R.drawable.star_aquarius_tap, R.drawable.star_pisces_tap, R.drawable.star_aries_tap,
            R.drawable.star_taurus_tap, R.drawable.star_gemini_tap, R.drawable.star_cancer_tap,
            R.drawable.star_leo_tap, R.drawable.star_virgo_tap, R.drawable.star_libra_tap,
            R.drawable.star_scorpio_tap, R.drawable.star_sagittarius_tap, R.drawable.star_capricorn_tap,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alter_zodiac);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        for (int i = 0; i < zodiac_name_arr.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", getString(zodiac_name_arr[i]));
            map.put("date", getString(zodiac_date_arr[i]));
            zodiac_listitem.add(map);
        }

        alter_zodiac_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setZodiac(zodiac_i);
            }
        });

        zodiac_i = getIntent().getIntExtra(PersonalInfoActivity.KEY_ZODIAC, PersonalInfoActivity.ZODIAC_NONE);

        zodiac_lv.setAdapter(new MyAdapter(AlterZodiacActivity.this,
                zodiac_listitem, zodiac_i));
        zodiac_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setZodiac(i);
            }
        });
    }

    private void setZodiac(int pos) {
        Intent mIntent = new Intent();
        mIntent.putExtra(PersonalInfoActivity.KEY_ZODIAC, pos);
        // 设置结果，并进行传送
        this.setResult(0, mIntent);
        finish();
    }

    private class MyAdapter extends BaseAdapter {

        ArrayList<HashMap<String, String>> mItems = new ArrayList<HashMap<String, String>>();
        private int mCurrent = -1;
        private LayoutInflater mInflater;

        private MyAdapter(Context context, ArrayList<HashMap<String, String>> items, int current) {
            this.mInflater = LayoutInflater.from(context);
            this.mItems = items;
            this.mCurrent = current;
        }

        public ArrayList<HashMap<String, String>> getmItems() {
            return mItems;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(R.layout.list_cell_zodiac, null);
            }
            TextView date = (TextView) view.findViewById(R.id.zodiac_date);
            date.setText(mItems.get(i).get("date"));
            ImageView png = (ImageView) view.findViewById(R.id.zodiac_png);
            png.setImageResource(zodiac_png_arr[i]);
            if (mCurrent == i) {
                png.setImageResource(zodiac_select_png_arr[i]);
            } else {
                png.setImageResource(zodiac_png_arr[i]);
            }

            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.ALTER_ZODIAC_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.ALTER_ZODIAC_ACTIVITY); //
        MobclickAgent.onPause(this);
    }
}



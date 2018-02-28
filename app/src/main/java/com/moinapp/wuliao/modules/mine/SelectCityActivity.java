package com.moinapp.wuliao.modules.mine;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.bean.UmengConstants;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.ui.CommonTitleBar;
import com.moinapp.wuliao.ui.a2zletter.LetterBaseListAdapter;
import com.moinapp.wuliao.ui.a2zletter.LetterComparator;
import com.moinapp.wuliao.ui.a2zletter.LetterListView;
import com.moinapp.wuliao.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 选择城市 [3.2.6后停用]
 * Created by moying on 15/6/28.
 */
public class SelectCityActivity extends BaseActivity/* implements AMapLocationListener*/ {
    protected static final String KEY_PROVINCE = "province";
    protected static final String KEY_CITY = "city";
    protected static final String KEY_DISTRICT = "district";
    private ILogger MyLog = LoggerFactory.getLogger("login");

    private LayoutInflater mInflater;
    private View mHeaderView;
    private CommonTitleBar mCommonTitleBar;
    private LinearLayout mHotCityLayout;
    //    private EditText autoLocate;
    private LetterListView cityList;
//    private ListView hotcityList;
    private String[] cityArr;
    private String[] hotcityArr;
    private String currentCity;
//    private LocationManagerProxy mAMapLocationManager;
//    private AMapLocation mMapLocation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_city;
    }

    @Override
    public void initView() {
        mInflater = LayoutInflater.from(SelectCityActivity.this);
        mHeaderView = mInflater.inflate(R.layout.activity_select_city_header, null, false);
//        autoLocate = (EditText) mHeaderView.findViewById(R.id.auto_locate);
//        hotcityList = (ListView) mHeaderView.findViewById(R.id.hotcitylist);
        mHotCityLayout = (LinearLayout) mHeaderView.findViewById(R.id.hotcity_ly);

        hotcityArr = getRes().getStringArray(R.array.hot_city);
        if (hotcityArr.length > 0) {

            if (getIntent() != null) {
                currentCity = StringUtil.nullToEmpty(getIntent().getStringExtra(KEY_CITY));
            }
            for (int i = 0; i < hotcityArr.length; i++) {
                View parent = mInflater.inflate(R.layout.list_cell_city, null);
                TextView textView = (TextView) parent.findViewById(R.id.city);
                textView.setText(hotcityArr[i]);
                ImageView imageView = (ImageView) parent.findViewById(R.id.iv_city_selected);
//                MyLog.i("currentCity= [" + currentCity + "], hotcityArr[i]= [" + hotcityArr[i] + "], isEqual=" + (currentCity.replaceAll(" ","").equals(hotcityArr[i])));
                if (currentCity.replaceAll(" ", "").equals(hotcityArr[i])) {
                    textView.setTextColor(getRes().getColor(R.color.common_text_main));
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    textView.setTextColor(getRes().getColor(R.color.common_title_grey));
                    imageView.setVisibility(View.GONE);
                }
                final int position = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setCity(hotcityArr[position]);
                    }
                });


                mHotCityLayout.addView(parent);
            }

        }
//        hotcityList.setAdapter(new HotCityAdapter(SelectCityActivity.this, hotcityArr));
//        com.moinapp.wuliao.util.UiUtils.fixListViewHeight(hotcityList);

        mCommonTitleBar = (CommonTitleBar) findViewById(R.id.title_layout);
        mCommonTitleBar.setLeftBtnOnclickListener(v -> {
            finish();
        });

        cityList = (LetterListView) findViewById(R.id.citylist);

        cityArr = getRes().getStringArray(R.array.city);
        cityList.setHeader(mHeaderView);
        cityList.setAdapter(new CityAdapter(SelectCityActivity.this, cityArr));

        /*
        autoLocate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                MyLog.i("autoLocate OnTouchListener ");
                setLocation();
                return true;
            }
        });
        LocateUtil locateUtil = new LocateUtil(SelectCityActivity.this, new LocateUtil.LocateListener() {
            @Override
            public void onLocateFinish(final AMapLocation aMapLocation) {
                MyLog.i("onLocateFinish " + aMapLocation);

                mMapLocation = aMapLocation;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        autoLocate.setText(aMapLocation.getCity());
                    }
                });

                destory();
            }

            @Override
            public void onLocateFailed() {
                MyLog.i("onLocateFailed ");
                destory();
            }
        });
        locateUtil.startLocation();
        */
    }

    private Resources getRes() {
        Resources resources = getResources();
        if (resources == null) {
            resources = AppContext.resources();
        }
        return resources;
    }

    @Override
    public void initData() {

    }

    private void setCity(String city) {
//        destory();
        Intent mIntent = new Intent();
        mIntent.putExtra(SelectCityActivity.KEY_CITY, city);
        this.setResult(0, mIntent);
        finish();
    }

    /*
    private void setLocation() {
        Intent mIntent = new Intent();

        if(mMapLocation == null) {
            this.setResult(0, mIntent);
        } else {
            mIntent.putExtra(SelectCityActivity.KEY_PROVINCE, mMapLocation.getProvince());
            mIntent.putExtra(SelectCityActivity.KEY_CITY, mMapLocation.getCity());
            mIntent.putExtra(SelectCityActivity.KEY_DISTRICT, mMapLocation.getDistrict());
            this.setResult(0, mIntent);
        }
        finish();
    }


    // 定位成功后回调函数
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        MyLog.i("onLocationChanged " + aMapLocation);
        if (aMapLocation == null){
            aMapLocation = new AMapLocation("AMapLocation");
            aMapLocation.setLatitude(0);
            aMapLocation.setLongitude(0);
        }
        destory();
    }

    @Override
    public void onLocationChanged(Location location) {
        MyLog.i("onLocationChanged " + location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        MyLog.i("onStatusChanged " + bundle);
    }

    @Override
    public void onProviderEnabled(String s) {
        MyLog.i("onProviderEnabled " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        MyLog.i("onProviderDisabled " + s);
    }

    // 停止定位
    private void destory(){
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }
*/
    @Override
    public void onClick(View view) {

    }

    public class HotCityAdapter extends BaseAdapter {
        Context mContext;
        private LayoutInflater mLayoutInflater;
        String[] mCityArr;

        public HotCityAdapter(Context context, String[] cityArr) {
            mContext = context;
            if (mLayoutInflater == null) {
                mLayoutInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            mCityArr = cityArr;
        }

        @Override
        public int getCount() {
            return mCityArr.length;
        }

        @Override
        public Object getItem(int i) {
            return mCityArr[i % mCityArr.length];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_cell_city, null);
                viewHolder = getListViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            setContentView(viewHolder, position);

            return convertView;
        }

        class ViewHolder {
            public TextView mCity;
            public ImageView mCitySelected;
        }

        private ViewHolder getListViewHolder(View convertView) {
            ViewHolder holder = new ViewHolder();
            holder.mCity = (TextView) convertView.findViewById(R.id.city);
            holder.mCitySelected = (ImageView) convertView.findViewById(R.id.iv_city_selected);
            return holder;
        }

        private void setContentView(ViewHolder viewHolder, final int position) {
            viewHolder.mCity.setText(mCityArr[position]);
            if (currentCity.replaceAll(" ", "").equals(mCityArr[position])) {
                viewHolder.mCitySelected.setVisibility(View.VISIBLE);
                viewHolder.mCity.setTextColor(getRes().getColor(R.color.common_text_main));
            } else {
                viewHolder.mCitySelected.setVisibility(View.GONE);
                viewHolder.mCity.setTextColor(getRes().getColor(R.color.common_title_grey));
            }
            viewHolder.mCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCity(mCityArr[position]);
                }
            });
        }

    }

    private class CityAdapter extends LetterBaseListAdapter<NameValuePair> {
        Context mContext;
        private LayoutInflater mLayoutInflater;
        List<NameValuePair> dataList;

        public CityAdapter(Context context, String[] cityArr) {
            super();

            mContext = context;
            if (mLayoutInflater == null) {
                mLayoutInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            ArrayList<String> citylist = new ArrayList(cityArr.length);
            for (int i = 0; i < cityArr.length; i++) {
                citylist.add(cityArr[i]);
            }
            LetterComparator pinyinComparator = new LetterComparator();
            Collections.sort(citylist, pinyinComparator);
            dataList = new ArrayList<NameValuePair>();
            for (int i = 0; i < cityArr.length; i++) {
                NameValuePair pair = new BasicNameValuePair(String.valueOf(i), citylist.get(i));
                dataList.add(pair);
            }
            setContainerList(dataList);
        }

        /**
         * 字母对应的key,因为字母是要插入到列表中的,为了区别,所有字母的item都使用同一的key.
         **/
        private static final String LETTER_KEY = "letter";

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public String getItemString(NameValuePair t) {
            return t.getValue();
        }

        @Override
        public NameValuePair create(char letter) {
            return new BasicNameValuePair(LETTER_KEY, String.valueOf(letter));
        }

        @Override
        public boolean isLetter(NameValuePair t) {
            //判断是不是字母行,通过key比较,这里是NameValuePair对象,其他对象,就由你自己决定怎么判断了.
            return t.getName().equals(LETTER_KEY);
        }

        @Override
        public View getLetterView(int position, View convertView, ViewGroup parent) {
            //这里是字母的item界面设置.
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_cell_city, null);
                TextView textView = (TextView) convertView.findViewById(R.id.city);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                convertView.setBackgroundColor(getRes().getColor(R.color.common_grey));
            }
            ((TextView) convertView.findViewById(R.id.city)).setText(list.get(position).getValue());

            return convertView;
        }

        @Override
        public View getContainerView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            //这里是其他正常数据的item界面设置.
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_cell_city, null);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_city_selected);
            textView = (TextView) convertView.findViewById(R.id.city);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setText(list.get(position).getValue());
            if (currentCity.replaceAll(" ", "").equals(list.get(position).getValue())) {
                textView.setTextColor(getRes().getColor(R.color.common_text_main));
                imageView.setVisibility(View.VISIBLE);
            } else {
                textView.setTextColor(getRes().getColor(R.color.common_title_grey));
                imageView.setVisibility(View.GONE);
            }
            final int index = position;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCity(list.get(index).getValue());
                }
            });
            MyLog.i(position + " : " + list.get(position).getValue());

            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConstants.SELECT_CITY_ACTIVITY); //统计页面
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConstants.SELECT_CITY_ACTIVITY); //
        MobclickAgent.onPause(this);
    }
}

package com.moinapp.wuliao.modules.stickercamera.app.camera.ui;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseActivity;
import com.moinapp.wuliao.cache.CacheManager;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.sticker.StickPreference;
import com.moinapp.wuliao.modules.sticker.StickerManager;
import com.moinapp.wuliao.ui.CommonTitleBar;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * 推荐文字
 * Created by guyunfei on 16/4/20.14:27.
 * <p>
 *   ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　　┃
 * ┃　　　━　　　┃
 * ┃　┳┛　┗┳　┃
 * ┃　　　　　　　┃
 * ┃　　　┻　　　┃
 * ┃　　　　　　　┃
 * ┗━┓　　　┏━┛
 *    ┃　　　┃   神兽保佑
 *    ┃　　　┃   代码无BUG！
 *    ┃　　　┗━━━┓
 *    ┃　　　　　　　┣┓
 *    ┃　　　　　　　┏┛
 *    ┗┓┓┏━┳┓┏┛
 *     ┃┫┫　┃┫┫
 *     ┗┻┛　┗┻┛
 */
public class RecommendWordActivity extends BaseActivity {
    private static final ILogger MyLog = LoggerFactory.getLogger(RecommendWordActivity.class.getSimpleName());

    public static final String RECOMMEND_WORD = "Recommend_word";
    public static final String KEY_STICKER_ID_STRING = "stick_id_string";
    public static final String RECOMMEND_WORD_CACHE_KEY = "Recommend_word_list_cache";

    private ArrayList<String> mRecommendWordList = new ArrayList<String>();
    private String mStickerIds;
    private String mTopicID;

    @InjectView(R.id.title_layout)
    CommonTitleBar mTitle;

    @InjectView(R.id.listview)
    ListView mListView;
    private RecommendWordAdapter recommendWordAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recommend_word;
    }

    @Override
    public void initView() {

        mTitle.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecommendWordActivity.this.finish();
            }
        });

        mTitle.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecommendText();
                if (mRecommendWordList != null) {
                    recommendWordAdapter.notifyDataSetChanged();
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(RECOMMEND_WORD, mRecommendWordList.get(position));
                setResult(RESULT_OK, intent);
                RecommendWordActivity.this.finish();
            }
        });
    }

    @Override
    public void initData() {
        mTopicID = StickPreference.getInstance().getJoinTopicID();
        Intent intent = getIntent();
        mStickerIds = intent.getStringExtra(KEY_STICKER_ID_STRING);

        MyLog.i("StickerIds__:" + mStickerIds);
        recommendWordAdapter = new RecommendWordAdapter();
        getRecommendText();
    }

    private void getRecommendText() {
        StickerManager.getInstance().getRecommendTtext(mTopicID, mStickerIds, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                mRecommendWordList = (ArrayList<String>) obj;
                if (mRecommendWordList != null && !mRecommendWordList.isEmpty() && mRecommendWordList.size() > 0) {
                    MyLog.i("推荐文字: " + "topicId :" + mTopicID + "__" + mRecommendWordList.toString());
                    CacheManager.saveObject(getApplicationContext(), mRecommendWordList, getCacheKey());
                    setAdapter();
                }
            }

            @Override
            public void onErr(Object obj) {
                AppContext.showToastShort(getResources().getString(R.string.get_recommend_word_failed));
                mRecommendWordList = (ArrayList<String>) CacheManager.readObject(RecommendWordActivity.this, getCacheKey());
                if (mRecommendWordList != null && !mRecommendWordList.isEmpty() && mRecommendWordList.size() > 0) {
                    MyLog.i("缓存文字: " + "topicId :" + mTopicID + "__" + mRecommendWordList.toString());
                    setAdapter();
                }
            }

            @Override
            public void onNoNetwork() {
                AppContext.showToastShort(getResources().getString(R.string.no_network));
                mRecommendWordList = (ArrayList<String>) CacheManager.readObject(RecommendWordActivity.this, getCacheKey());
                if (mRecommendWordList != null && !mRecommendWordList.isEmpty() && mRecommendWordList.size() > 0) {
                    MyLog.i("缓存文字: " + "topicId :" + mTopicID + "__" + mRecommendWordList.toString());
                    setAdapter();
                }
            }
        });
    }

    private void setAdapter() {
        if (mRecommendWordList != null && !mRecommendWordList.isEmpty() && mRecommendWordList.size() > 0) {
            if (mListView != null) {
                mListView.setAdapter(recommendWordAdapter);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    private String getCacheKey() {
        return RECOMMEND_WORD_CACHE_KEY + "_" + StickPreference.getInstance().getJoinTopicID();
    }


    class RecommendWordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mRecommendWordList.size();
        }

        @Override
        public String getItem(int position) {
            return mRecommendWordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(RecommendWordActivity.this, R.layout.item_recommend_word, null);
            }
            ViewHolder holder = ViewHolder.getHolder(convertView);

            holder.mTvRecommendWord.setText(getItem(position));
            return convertView;
        }
    }

    static class ViewHolder {
        TextView mTvRecommendWord;

        public ViewHolder(View convertView) {
            mTvRecommendWord = (TextView) convertView.findViewById(R.id.tv_recommend_word);
        }

        public static ViewHolder getHolder(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }

}

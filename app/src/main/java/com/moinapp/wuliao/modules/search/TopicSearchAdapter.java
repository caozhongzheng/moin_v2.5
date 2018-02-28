package com.moinapp.wuliao.modules.search;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keyboard.view.RoundAngleImageView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.BaseApplication;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.discovery.adapter.TagListAdapter;
import com.moinapp.wuliao.modules.discovery.model.TagPop;
import com.moinapp.wuliao.util.ImageLoaderUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by guyunfei on 16/7/22.16:50.
 */
public class TopicSearchAdapter extends ListBaseAdapter<TagPop> {

    private static final ILogger MyLog = LoggerFactory.getLogger(TagListAdapter.class.getSimpleName());

    private Activity mActivity;
    private int mPosition = 0;
    private boolean animShow = true;

    private String mKeyword;

    public TopicSearchAdapter(Activity activity) {
        mActivity = activity;
    }

    public void setKeyword(String keyword) {
        mKeyword = keyword;
        notifyDataSetChanged();
    }

    @Override
    public int getDataSize() {
        return mDatas.size();
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        if (mPosition < position) {
            mPosition = position;
            animShow = true;
        } else {
            animShow = false;
        }
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_topic_list, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TagPop tagPop = mDatas.get(position);
        if (tagPop != null) {
            setUserInfo(tagPop, vh);
        }

        return convertView;
    }

    private void setUserInfo(TagPop tagPop, ViewHolder vh) {

        ImageLoaderUtils.displayHttpImage(tagPop.getIcon().getUri(), vh.topicImg, null, animShow, null);

        if (tagPop.getName() != null) {
            if (mKeyword == null) {
                vh.topicName.setText(tagPop.getName());
            } else {
                SpannableStringBuilder textString = highlight(tagPop.getName(), mKeyword);
                vh.topicName.setText(textString);
            }
        }

        if (tagPop.getCategoryName() != null) {
            vh.topicCategory.setText(tagPop.getCategoryName());
        }

        if (tagPop.getReadNum() >= 0) {
            vh.viewNum.setText(tagPop.getReadNum() + "");
        }

        if (tagPop.getCosplayNum() >= 0) {
            vh.contentNum.setText(tagPop.getCosplayNum() + "");
        }

        if (tagPop.getCommentNum() >= 0) {
            vh.commentNum.setText(tagPop.getCommentNum() + "");
        }

        if (tagPop.getDesc() != null) {
            vh.topicDes.setText(tagPop.getDesc());
        }
    }


    /**
     * 关键字高亮显示
     *
     * @param text   需要显示的文字
     * @param target 需要高亮的关键字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    private static SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(BaseApplication.context().getResources().getColor(R.color.topic_search_highlight));// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }


    static class ViewHolder {
        @InjectView(R.id.ic_topic_pic)
        RoundAngleImageView topicImg;
        @InjectView(R.id.tv_topic_name)
        TextView topicName;
        @InjectView(R.id.tv_topic_category)
        TextView topicCategory;
        @InjectView(R.id.tv_view_num)
        TextView viewNum;
        @InjectView(R.id.tv_content_num)
        TextView contentNum;
        @InjectView(R.id.tv_comment_num)
        TextView commentNum;
        @InjectView(R.id.tv_desc)
        TextView topicDes;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

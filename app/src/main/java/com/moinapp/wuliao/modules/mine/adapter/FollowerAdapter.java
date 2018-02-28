package com.moinapp.wuliao.modules.mine.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.util.StringUtil;
import com.moinapp.wuliao.util.StringUtils;
import com.moinapp.wuliao.util.UIHelper;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.FollowView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 好友列表适配器
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年11月6日 上午11:22:27
 */
public class FollowerAdapter extends ListBaseAdapter<UserInfo> {
    private ILogger MyLog = LoggerFactory.getLogger("FollowerAdapter");
    private String mUid;
    private static final int FOLLOW_ALREADY = 100;//当在页面关注以后设置的临时标记

    public FollowerAdapter(String uid) {
        this.mUid = uid;
        setFinishText(R.string.no_more_follow);
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_follower, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final UserInfo item = mDatas.get(position);
        if (item == null) return convertView;
//        String userName = item.getUsername();

        //获取当前用户名的首字符
//        String pinyin = PinYinUtil.getPinyin(userName);
        String pinyin = item.getUsername_abc();
        String currentWord;
        if (StringUtils.isEmpty(pinyin)) {
            currentWord = "";
        } else {
            currentWord = pinyin.charAt(0) + "";
        }
        //如果首字符不是字母,则把首字符变为'#'
        currentWord = isLetter(currentWord);
        if (position > 0) {
            //如果当前用户不是第一个,获取上一个用户名的首字母
//            String lastUserName = mDatas.get(position - 1).getUsername();
            String lastWord;
//            String lastpinyin = PinYinUtil.getPinyin(lastUserName);
            String lastpinyin = mDatas.get(position - 1).getUsername_abc();
            if (StringUtils.isEmpty(lastpinyin)) {
                lastWord = "";
            } else {
                lastWord = lastpinyin.charAt(0) + "";
            }
            lastWord = isLetter(lastWord);
            if (currentWord.equals(lastWord)) {
                //如果当前用户名的首字母和上一个用户名的首字母相同,则把用户名上边显示字母的TextView给隐藏掉
                vh.user_word.setVisibility(View.GONE);
                vh.grey_line.setVisibility(View.VISIBLE);
            } else {
                //如果当前用户名的首字母和上一个用户名的首字母不同,
                // 则把该首字母设置给用户名上方显示首字母的TextView并把TextView设置为显示状态
                vh.user_word.setVisibility(View.VISIBLE);
                vh.user_word.setText(currentWord);
                vh.grey_line.setVisibility(View.GONE);
            }
        } else {
            //如果当前用户是第一个,则直接把首字母设置给用户名上方显示首字母的TextView并把TextView设置为显示状态
            vh.user_word.setVisibility(View.VISIBLE);
            vh.user_word.setText(currentWord);
            vh.grey_line.setVisibility(View.GONE);
        }


        // 用户昵称(备注名)
        if (!TextUtils.isEmpty(item.getAlias())) {
            vh.name.setText(item.getAlias());
        } else {
            vh.name.setText(item.getUsername());
        }

        // 个性签名,如果没有则显示注册时间
        if (TextUtils.isEmpty(item.getSignature())) {
            String s = parent.getContext().getString(R.string.i_register) + ": " +
                    StringUtil.formatDate(item.getCreatedAt(), StringUtil.TIME_PATTERN);
            vh.signature.setText(s);
        } else {
            vh.signature.setText(item.getSignature());
        }

        // 设置头像
        if (item.getAvatar() != null) {
            vh.avatar.setAvatarUrl(item.getAvatar().getUri());
        } else {
            vh.avatar.setAvatarUrl(null);
        }

        // 设置性别
        if ("male".equalsIgnoreCase(item.getSex())) {
            vh.gender.setImageResource(R.drawable.male_yellow);
        } else {
            vh.gender.setImageResource(R.drawable.girl_red);
        }

        vh.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(item.getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(parent.getContext(), 0);
                } else {
                    UIHelper.showUserCenter(parent.getContext(), item.getUId());
                }
            }
        });

        // 与本人关系
        Log.i("ljc", "item.relation=" + item.getRelation());
        int from = 0;
        if (ClientInfo.getUID().equalsIgnoreCase(mUid)) {
            from = UserDefineConstants.FOLLOW_MY_FOLLOWS_FANS;
        } else {
            from = UserDefineConstants.FOLLOW_OTHER_FOLLOWS_FANS;
        }
        vh.follow.init(item, item.getRelation(), from);

        // 点击跳转用户中心
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin() && ClientInfo.isLoginUser(item.getUId())) {
                    // 登录用户点击自己头像
                    UIHelper.showMine(parent.getContext(), 0);
                } else {
                    UIHelper.showUserCenter(parent.getContext(), item.getUId());
                }
            }
        });
        return convertView;
    }

    /**
     * 判断字符是否是字母,如果不是就把字符改为'#',而且如果是字母就转换成大写
     */
    private String isLetter(String str) {
        String reg = "[a-zA-Z]";//定义正则用来判断字符是否是字母
        boolean isLetter = str.matches(reg);
        if (!isLetter) {
            str = "#";
        } else {
            str = str.toUpperCase();
        }
        return str;
    }

    static class ViewHolder {

        @InjectView(R.id.user_name)
        TextView name;

        @InjectView(R.id.tv_user_sign)
        TextView signature;

        @InjectView(R.id.btn_follow)
        FollowView follow;

        @InjectView(R.id.avatar)
        AvatarView avatar;

        @InjectView(R.id.iv_gender)
        ImageView gender;

        @InjectView(R.id.tv_user_name_letter)
        TextView user_word;

        @InjectView(R.id.view_grey_line)
        View grey_line;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

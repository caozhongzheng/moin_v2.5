package com.moinapp.wuliao.modules.mine.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.AppContext;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.base.ListBaseAdapter;
import com.moinapp.wuliao.bean.AliasEvent;
import com.moinapp.wuliao.bean.UserInfo;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.listener.IListener;
import com.moinapp.wuliao.modules.mine.InviteListFragment;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.UserDefineConstants;
import com.moinapp.wuliao.util.DisplayUtil;
import com.moinapp.wuliao.widget.AvatarView;
import com.moinapp.wuliao.widget.FollowView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 邀请好友列表适配器
 *
 * Created by moying on 16/4/7.
 */
public class InviteAdapter extends ListBaseAdapter<UserInfo> {
    private ILogger MyLog = LoggerFactory.getLogger(InviteAdapter.class.getSimpleName());
    private String groupID = "";
    private String mToken;
    //记录微博已经发送过邀请的好友uid
    private List<String> mSinaSentList = new ArrayList<String>();

    public InviteAdapter(String token) {
        mToken = token;
    }

    @SuppressLint("InflateParams")
    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_invite, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final UserInfo item = mDatas.get(position);
        try {
            MyLog.i(position + ":" + item.getUsername()
                    + ", phone= " + item.getPhone()
                    + ", relation= " + item.getRelation()
                    + ", ThirdName= " + item.getThirdName()
                    + ", alias= " + item.getAlias()
                    + ", uid= " + item.getUId()
                    + ", abc= " + item.getUsername_abc());
        } catch (Exception e) {
            MyLog.e(e);
        }
        if (item == null) return convertView;

        String currentWord = groupID = item.getUsername_abc();
        vh.top_line.setVisibility(View.GONE);
        vh.btm_line.setVisibility(View.GONE);

        if (position > 0) {
            String lastWord = mDatas.get(position - 1).getUsername_abc();
            if (currentWord.equals(lastWord)) {
                // 组内
                vh.tv_group.setVisibility(View.GONE);
                vh.top_line.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) vh.top_line.getLayoutParams();
                if (currentWord.startsWith("u")) {
                    params.leftMargin = DisplayUtil.dip2px(parent.getContext(), 10);
                } else {
                    params.leftMargin = parent.getContext().getResources().getDimensionPixelOffset(R.dimen.invite_line_marginLeft);
                }
            } else {
                // 下一组
                groupID = currentWord;
                vh.tv_group.setVisibility(View.VISIBLE);
                vh.tv_group.setText(getGroupName(currentWord));
            }

            String nextWord= "";
            if (position < getDataSize() - 1) {
                nextWord = mDatas.get(position + 1).getUsername_abc();
            }
            if (!currentWord.equals(nextWord)) {
                // 组尾
                vh.btm_line.setVisibility(View.VISIBLE);
            }
        } else {
            // 第一个
            vh.tv_group.setVisibility(View.VISIBLE);
            vh.tv_group.setText(getGroupName(currentWord));
        }

        if (!groupID.startsWith("u")) {
            vh.rl_matched.setVisibility(View.VISIBLE);
            vh.rl_unmatched.setVisibility(View.GONE);

            // 头像
            if (item.getAvatar() != null) {
                vh.avatar.setAvatarUrl(item.getAvatar().getUri());
            } else {
                vh.avatar.setAvatarUrl(null);
            }
            View.OnClickListener userCenter = view -> {
//                UIHelper.showUserCenter(parent.getContext(), item.getUId());
            };
            vh.avatar.setOnClickListener(userCenter);

            boolean showAlias = true;
            boolean aliasDeleted = InviteListFragment.ALIAS_PREFEX.equals(item.getAlias());
            // 昵称
            if (TextUtils.isEmpty(item.getAlias()) || aliasDeleted) {
                vh.name.setText(item.getUsername());
                if (aliasDeleted) {
                    showAlias = false;
                }
            } else {
                if (item.getAlias().startsWith(InviteListFragment.ALIAS_PREFEX)) {
                    // 如果是已经改过了,就将备注消失
                    showAlias = false;
                    vh.name.setText(item.getAlias().substring(InviteListFragment.ALIAS_PREFEX_L));
                } else {
                    vh.name.setText(item.getAlias());
                }
            }

            // 备注
            if (showAlias && (item.getRelation() == UserDefineConstants.FRIENDS_FOLLOWERS
                    || item.getRelation() == UserDefineConstants.FRIENDS_FANS_FOLLOWERS)) {
                vh.atlas.setVisibility(View.VISIBLE);
                vh.atlas.setOnClickListener(view -> {
                    // TODO 修改备注,修改成功后备注这个文字消失,而且名字变为备注
                    EventBus.getDefault().post(new AliasEvent(item));
                });
            } else {
                vh.atlas.setVisibility(View.GONE);
            }
            // 性别 无性别时默认女性
            if (item.getSex() != null) {
                vh.gender.setImageResource(item.getSex().equals("male") ? R.drawable.boy_yellow
                        : R.drawable.girl_red);
            } else {
                vh.gender.setImageResource(R.drawable.girl_red);
            }
            // 来源
            String name = groupID.equalsIgnoreCase("c") ? "通讯录好友:" : "微博名称:";
            vh.from.setText(name + item.getThirdName());

            // 关注
            vh.follow.init(item, item.getRelation(), UserDefineConstants.INVITE_LIST);

            convertView.setOnClickListener(userCenter);
        } else {
            vh.rl_unmatched.setVisibility(View.VISIBLE);
            vh.rl_matched.setVisibility(View.GONE);

            vh.local_name.setText(item.getThirdName());
            final LinearLayout invite = vh.invite;
            final TextView invite_text = vh.invite_text;

            setSinaInvitedStatus(vh.invite, item, vh.invite_text);
            invite.setOnClickListener(view -> {
                // 邀请好友
                if (groupID.equalsIgnoreCase("uc")) {
                    sendSms(parent.getContext(), item);
                } else {
                    sendSinaInvite(item, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            AppContext.showToastShort("发送邀请成功");
                            mSinaSentList.add(item.getThirdUid());
                            invite.setEnabled(false);
                            invite.setBackgroundDrawable(invite.getContext().getResources().getDrawable(R.drawable.long_boreder_gray));
                            invite_text.setTextColor(invite.getContext().getResources().getColor(R.color.invited));
                            invite_text.setText("已邀请");
                        }

                        @Override
                        public void onErr(Object obj) {
                            AppContext.showToastShort("发送邀请失败");
                        }

                        @Override
                        public void onNoNetwork() {
                            invite.setEnabled(true);
                            AppContext.showToastShort(parent.getContext().getString(R.string.hasno_network));
                        }
                    });
                }
            });
            convertView.setOnClickListener(null);
        }

        return convertView;
    }

    private void setSinaInvitedStatus(LinearLayout linearLayout, UserInfo userInfo, TextView invite) {
        boolean enable = true;
        if (mSinaSentList != null && mSinaSentList.size() > 0) {
            for (String uid : mSinaSentList) {
                if (userInfo.getThirdUid().equalsIgnoreCase(uid)) {
                    enable = false;
                    break;
                }
            }
        }
        if (enable) {
            linearLayout.setEnabled(true);
            linearLayout.setBackgroundDrawable(linearLayout.getContext().getResources().getDrawable(R.drawable.border_yellow));
            invite.setTextColor(invite.getContext().getResources().getColor(R.color.invite));
        } else {
            linearLayout.setEnabled(false);
            linearLayout.setBackgroundDrawable(linearLayout.getContext().getResources().getDrawable(R.drawable.long_boreder_gray));
            invite.setTextColor(invite.getContext().getResources().getColor(R.color.invited));
            invite.setText("已邀请");
        }
    }

    private void sendSms(Context context, UserInfo userInfo) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + userInfo.getPhone()));
        intent.putExtra("sms_body", String.format(context.getResources().getString(R.string.sms_invite), userInfo.getThirdName()));
        context.startActivity(intent);
    }

    private void sendSinaInvite(UserInfo userInfo, IListener listener) {
        MineManager.getInstance().sendInvite(null, userInfo.getThirdName(), mToken, listener);
    }

    private String getGroupName(String currentWord) {
        if (currentWord.startsWith("u")) {
            return "邀请ta们加入MOIN吧";
        }
        return "已加入MOIN";
    }

    static class ViewHolder {

        @InjectView(R.id.tv_group)
        TextView tv_group;

        @InjectView(R.id.rl_matched)
        RelativeLayout rl_matched;

        @InjectView(R.id.rl_unmatched)
        RelativeLayout rl_unmatched;

        @InjectView(R.id.avatar)
        AvatarView avatar;

        @InjectView(R.id.gender)
        ImageView gender;

        @InjectView(R.id.user_name)
        TextView name;

        @InjectView(R.id.add_nickname)
        TextView atlas;

        @InjectView(R.id.user_from)
        TextView from;

        @InjectView(R.id.btn_follow)
        FollowView follow;

        @InjectView(R.id.unmatched_user_name)
        TextView local_name;

        @InjectView(R.id.ly_invite)
        LinearLayout invite;

        @InjectView(R.id.tv_invite)
        TextView invite_text;

        @InjectView(R.id.view_top_line)
        View top_line;

        @InjectView(R.id.view_bottom_line)
        View btm_line;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}

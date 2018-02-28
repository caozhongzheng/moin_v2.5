package com.moinapp.wuliao.bean;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.fragment.BrowserFragment;
import com.moinapp.wuliao.fragment.CommentFrament;
import com.moinapp.wuliao.modules.discovery.ui.ArticleListFragment;
import com.moinapp.wuliao.modules.discovery.ui.CosplaySearchResultFragment;
import com.moinapp.wuliao.modules.discovery.ui.LikeCommentViewPagerFragment;
import com.moinapp.wuliao.modules.discovery.ui.TopicDetailNewFragment;
import com.moinapp.wuliao.modules.discovery.ui.TopicListFragment;
import com.moinapp.wuliao.modules.discovery.ui.TopicPhotoListFragment;
import com.moinapp.wuliao.modules.discovery.ui.UserListFragment;
import com.moinapp.wuliao.modules.events.ui.EventsFragment;
import com.moinapp.wuliao.modules.login.HotUserFragment;
import com.moinapp.wuliao.modules.login.RegistFragment_step1;
import com.moinapp.wuliao.modules.mine.AboutFragment;
import com.moinapp.wuliao.modules.mine.AboutProtocalFragment;
import com.moinapp.wuliao.modules.mine.AboutWechatFragment;
import com.moinapp.wuliao.modules.mine.CropCosplayFragment;
import com.moinapp.wuliao.modules.mine.DebugFragment;
import com.moinapp.wuliao.modules.mine.FansFragment;
import com.moinapp.wuliao.modules.mine.FollowersFragment;
import com.moinapp.wuliao.modules.mine.InviteListFragment;
import com.moinapp.wuliao.modules.mine.MineViewPagerFragment;
import com.moinapp.wuliao.modules.mine.MyCommentFragment;
import com.moinapp.wuliao.modules.mine.MyCosplayFragment;
import com.moinapp.wuliao.modules.mine.MyFeedbackFragment;
import com.moinapp.wuliao.modules.mine.MyFeedbackLamentFragment;
import com.moinapp.wuliao.modules.mine.MyLikeFragment;
import com.moinapp.wuliao.modules.mine.MySettingsFragment;
import com.moinapp.wuliao.modules.mine.MyTagsFragment;
import com.moinapp.wuliao.modules.mine.NotificationStyleFragment;
import com.moinapp.wuliao.modules.mine.SecurityFragment;
import com.moinapp.wuliao.modules.mine.SettingsMyNotificationFragment;
import com.moinapp.wuliao.modules.mine.UserActivityFragment;
import com.moinapp.wuliao.modules.mine.chat.ChatFragment;
import com.moinapp.wuliao.modules.mine.chat.ChatListFragment;
import com.moinapp.wuliao.modules.mine.message.MyMessageViewPageFragment;
import com.moinapp.wuliao.modules.mission.MoinBeanPKFragment;
import com.moinapp.wuliao.modules.mission.ShareFragment;
import com.moinapp.wuliao.modules.search.SearchViewPagerFragment;
import com.moinapp.wuliao.modules.search.TopicSearchResultFragment;
import com.moinapp.wuliao.modules.sticker.ui.SearchStickerFragment;
import com.moinapp.wuliao.modules.sticker.ui.SearchStickerResultFragment;
import com.moinapp.wuliao.modules.sticker.ui.mall.StickerCenterViewPagerFragment;
import com.moinapp.wuliao.modules.stickercamera.app.ui.AtFollowersFragment;
import com.moinapp.wuliao.modules.stickercamera.app.ui.WriteAuthFragment;
import com.moinapp.wuliao.viewpagerfragment.FriendsViewPagerFragment;

public enum SimpleBackPage {

    COMMENT(1, R.string.actionbar_title_comment, CommentFrament.class),

    MY_FRIENDS(11, R.string.actionbar_title_my_friends,
            FriendsViewPagerFragment.class),

    SEARCH(20, R.string.actionbar_title_search, SearchViewPagerFragment.class),

    BROWSER(26, R.string.app_name, BrowserFragment.class),

//    STICKER_LIST(43, R.string.stick_center, StickerCenterPageFragment.class),
    STICKER_LIST(43, R.string.stick_center, StickerCenterViewPagerFragment.class),

    COSPLAY_LIKE_COMMENT_LIST(44, R.string.like_comment, LikeCommentViewPagerFragment.class),

    COSPLAY_TAG_DETAIL(45, R.string.tag_detail, CosplaySearchResultFragment.class),

    MSG_MINE(46, R.string.actionbar_title_my_information, MineViewPagerFragment.class),

    MSG_MY_TAGS(47, R.string.myinfo_tag, MyTagsFragment.class),

    MSG_MY_FOLLOWERS(48, R.string.myinfo_foc, FollowersFragment.class),

    MSG_MY_FANS(49, R.string.myinfo_fan, FansFragment.class),

    MY_COSPLAY(50, R.string.s_cosplay, MyCosplayFragment.class),

    CROP_COSPLAY(51, R.string.crop_cosplay, CropCosplayFragment.class),

    MY_SETTINGS(52, R.string.setting, MySettingsFragment.class),

    SETTINGS_SECURITY(53, R.string.account_security, SecurityFragment.class),

    SETTINGS_NOTIFICATION(54, R.string.mes_notice, SettingsMyNotificationFragment.class),

    SETTINGS_FEEDBACK(55, R.string.feedback, MyFeedbackFragment.class),

    SETTINGS_FEEDBACK_LAMENT(56, R.string.feedback_bad, MyFeedbackLamentFragment.class),

    SETTINGS_ABOUT(57, R.string.about, AboutFragment.class),

    SETTINGS_ABOUT_PROTOCAL(58, R.string.service_agreement, AboutProtocalFragment.class),

    SETTINGS_STICKER(60, R.string.sticker, SecurityFragment.class),

    SETTINGS_ABOUT_WECHAT(61, R.string.contact, AboutWechatFragment.class),

    AT_FOLLOWERS(63, R.string.follow_user, AtFollowersFragment.class),

    SHOW_DEBUG(64, R.string.debug_title, DebugFragment.class),

    WRITE_AUTH(65, R.string.write_auth, WriteAuthFragment.class),

    SHOW_HOT_USER(66, R.string.hot_user, HotUserFragment.class),

    SHOW_MY_COMMENTS(67, R.string.my_comment, MyCommentFragment.class),

    SHOW_MY_LIKE(68, R.string.my_like, MyLikeFragment.class),

    SHOW_MY_MESSAGE(69, R.string.my_message, MyMessageViewPageFragment.class),

    USER_ACTIVITY(70, R.string.user_activity, UserActivityFragment.class),

//    SHOW_FILL_PHONE(71, R.string.register_fill_phone, FillPhoneFragment.class),

    SEARCH_STICKER(72, R.string.search_sticke, SearchStickerFragment.class),

    SEARCH_STICKER_RESULT(73, R.string.search_sticke_result, SearchStickerResultFragment.class),

    USER_LIST(74, R.string.user_list, UserListFragment.class),

    TOPIC_LIST(75, R.string.topic_list, TopicListFragment.class),

    TOPIC_DETAIL(76, R.string.topic_detail, TopicDetailNewFragment.class),

    INVITE_LIST(77, R.string.invite_list, InviteListFragment.class),

    CHAT_LIST(78, R.string.chat_list, ChatListFragment.class),

    CHAT(79, R.string.chat, ChatFragment.class),

    REGISTRY(80, R.string.regist, RegistFragment_step1.class),

    ARTICLE_LIST(81, R.string.article_list, ArticleListFragment.class),

    TOPIC_PHOTO_LIST(82, R.string.topic_photo_list, TopicPhotoListFragment.class),

    EVENTS_LIST(83, R.string.events_title, EventsFragment.class),

    TOPIC_SEARCH_RESULT(84, R.string.topic_search_result, TopicSearchResultFragment.class),

    SHARE_MISSION(85, R.string.share_title, ShareFragment.class),

    MOIN_BEAN_PK(86, R.string.moin_bean_pk, MoinBeanPKFragment.class),

    SETTINGS_NEW_MSG_NOTIFY_STYLE(87, R.string.message_notification, NotificationStyleFragment.class);

    private int title;
    private Class<?> clz;
    private int value;

    private SimpleBackPage(int value, int title, Class<?> clz) {
        this.value = value;
        this.title = title;
        this.clz = clz;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static SimpleBackPage getPageByValue(int val) {
        for (SimpleBackPage p : values()) {
            if (p.getValue() == val)
                return p;
        }
        return null;
    }
}

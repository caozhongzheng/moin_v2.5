package com.moinapp.wuliao.modules.mine.chat;

/** 聊天列表页布局管理器
 * Created by guyunfei on 16/4/10.22:52.
 */
public class ChatLayoutManager {
    private static ChatLayoutManager mInstance = new ChatLayoutManager();

    private ChatLayoutManager() {
    }

    public static ChatLayoutManager getInstance() {
        return mInstance;
    }

    //用来记录当前打开的ChatItemlayout
    private ChatListItemLayout currentLayout;

    public void setChatItmeLayout(ChatListItemLayout layout) {
        this.currentLayout = layout;
    }

    //判断是否可以滑动,如果没有打开的则可以滑动
    public boolean isShouldSwipe(ChatListItemLayout layout) {
        if (currentLayout == null) {
            //当前没有打开的Layout
            return true;
        } else {
            //有打开的,如果是打开的布局还是可以滑动,否则就不可以滑动
            return currentLayout == layout;
        }
    }

    //关闭当前打开的布局
    public void closeCurrentLayout() {
        if (currentLayout != null) {
            currentLayout.close();
        }
    }

    //清空当前打开的布局
    public void clearCurrentLayout() {
        currentLayout = null;
    }
}

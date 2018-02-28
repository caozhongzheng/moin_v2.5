package com.moinapp.wuliao.modules.discovery.model;

import com.moinapp.wuliao.bean.Entity;
import com.moinapp.wuliao.bean.ListEntity;

import java.util.List;

/**
 * 表情专辑对象列表
 * Created by liujiancheng on 15/10/10.
 */
public class EmojiSetList extends Entity implements ListEntity<EmojiSet> {

    private List<EmojiSet> emojiList;

    public List<EmojiSet> getEmojis() {
        return emojiList;
    }

    public void setEmojis(List<EmojiSet> emojis) {
        this.emojiList = emojis;
    }

    @Override
    public List<EmojiSet> getList() {
        return emojiList;
    }
}

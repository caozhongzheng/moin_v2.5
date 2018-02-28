package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.EmojiSet;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 */
public class GetEmojiListResult extends BaseHttpResponse {
    private int isIdol;
    private List<EmojiSet> emojiList;

    public int getIsIdol() {
        return isIdol;
    }

    public void setIsIdol(int isIdol) {
        this.isIdol = isIdol;
    }

    public List<EmojiSet> getEmojiList() {
        return emojiList;
    }

    public void setEmojiList(List<EmojiSet> emojiList) {
        this.emojiList = emojiList;
    }
}

package com.moinapp.wuliao.modules.discovery.result;

import com.moinapp.wuliao.bean.BaseHttpResponse;
import com.moinapp.wuliao.modules.discovery.model.EmojiSet;
import com.moinapp.wuliao.modules.mine.emoji.EmojiResource;

import java.util.List;

/**
 * Created by liujiancheng on 15/10/9.
 */
public class GetEmojiDetailResult extends BaseHttpResponse {
    private EmojiSet emoji;

    public EmojiSet getEmoji() {
        return emoji;
    }

    public void setEmoji(EmojiSet emoji) {
        this.emoji = emoji;
    }
}

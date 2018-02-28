package com.moinapp.wuliao.modules.stickercamera.app.camera.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.sticker.model.StickerAudioInfo;
import com.moinapp.wuliao.util.MyAudioPlayer;

import java.util.List;

/**
 * 预置声音试听的适配器
 */
public class VoiceAdapter extends BaseAdapter {
    private static ILogger MyLog = LoggerFactory.getLogger(VoiceAdapter.class.getSimpleName());
    List<StickerAudioInfo> autioList;
    Context            mContext;
    VoiceSlectCallback mCallBack;
    private int mLastPosition = -1;

    public VoiceAdapter(Context context, List<StickerAudioInfo> audios) {
        autioList = audios;
        mContext = context;
    }

    @Override
    public int getCount() {
        return autioList.size();
    }

    @Override
    public Object getItem(int position) {
        return autioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AudioHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_fixed_voice, null);
            holder = new AudioHolder();
            holder.llPlay = (LinearLayout) convertView.findViewById(R.id.ll_play);
            holder.borderImg = (ImageView) convertView.findViewById(R.id.voice_border);
            holder.audioImg = (ImageView) convertView.findViewById(R.id.voice_image);
            holder.audioName = (TextView) convertView.findViewById(R.id.voice_name);
            holder.fl_playing = (FrameLayout) convertView.findViewById(R.id.fl_playing);
            holder.playingImg = (ImageView) convertView.findViewById(R.id.iv_playing);
            convertView.setTag(holder);
        } else {
            holder = (AudioHolder) convertView.getTag();
        }

        final StickerAudioInfo audio = (StickerAudioInfo) getItem(position);
        if (audio != null) {
            holder.audioImg.setImageDrawable(getBackgroundImage(position));
            holder.audioName.setText(audio.getText());
            setSelectStatus(holder, position);
            setPlayStatus(holder, audio);
            final AudioHolder vh = holder;
            //点击试听开始播放声音
            holder.llPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (StickerAudioInfo audioInfo : autioList) {
                        audioInfo.setPlaying(0);
                    }
                    audio.setPlaying(1);
                    MyAudioPlayer.getInstance().playAssertsAudio(mContext, audio.getUri(), new MyAudioPlayer.PlayEndCallback() {
                        @Override
                        public void onCompleted() {
                            audio.setPlaying(0);
                            notifyDataSetChanged();
                        }
                    });
                    notifyDataSetChanged();
                }
            });
            //点击播放动画停止播放
            holder.fl_playing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    audio.setPlaying(0);
                    MyAudioPlayer.getInstance().stopPlay();
                    setPlayStatus(vh, audio);
                }
            });

            //点击声音背景图选择声音到舞台
            holder.audioImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallBack != null) {
                        mCallBack.onVoiceSelect(audio);
                    }
                    if (mLastPosition == -1) {
                        mLastPosition = position;
                        notifyDataSetChanged();
                    }
                }
            });
        }
        return convertView;
    }

    public void stopPlayAudio() {
        for (StickerAudioInfo audioInfo : autioList) {
            audioInfo.setPlaying(0);
        }
        MyAudioPlayer.getInstance().stopPlay();
        notifyDataSetChanged();
    }

    //设置当前选中的声音边框为红色
    private void setSelectStatus(AudioHolder holder, int position) {
        if (mLastPosition == position) {
            holder.borderImg.setVisibility(View.VISIBLE);
        } else {
            holder.borderImg.setVisibility(View.INVISIBLE);
        }
    }

    //设置播放状态
    private void setPlayStatus(AudioHolder holder, StickerAudioInfo audio) {
        if (audio.getPlaying() == 0) {
            holder.llPlay.setVisibility(View.VISIBLE);
            holder.fl_playing.setVisibility(View.GONE);
        } else {
            holder.fl_playing.setVisibility(View.VISIBLE);
            holder.playingImg.setImageResource(R.drawable.sticker_audio);
            AnimationDrawable animationDrawable = (AnimationDrawable) holder.playingImg.getDrawable();
            animationDrawable.start();
            holder.llPlay.setVisibility(View.GONE);
        }
    }

    private Drawable getBackgroundImage(int position) {
        int[] audioImg = {R.drawable.audition_color_1, R.drawable.audition_color_2,
                        R.drawable.audition_color_3, R.drawable.audition_color_4,
                        R.drawable.audition_color_5, R.drawable.audition_color_6};
        Drawable drawable = mContext.getResources().getDrawable(audioImg[(position + 1) % audioImg.length]);
        return drawable;
    }

    public void setCallBack(VoiceSlectCallback callBack) {
        mCallBack = callBack;
    }
    public interface VoiceSlectCallback {
        public void onVoiceSelect(StickerAudioInfo audio);
    }

    public void deleteSelection() {
        mLastPosition = -1;
        notifyDataSetChanged();
    }

    class AudioHolder {
        ImageView borderImg;
        ImageView audioImg;
        TextView  audioName;
        LinearLayout llPlay;
        FrameLayout fl_playing;
        ImageView playingImg;
    }

}

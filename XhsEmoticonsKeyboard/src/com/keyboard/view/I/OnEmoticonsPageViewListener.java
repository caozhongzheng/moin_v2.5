package com.keyboard.view.I;

/**
 * Created by moying on 16/5/30.
 */
public interface OnEmoticonsPageViewListener {
    void emoticonsPageViewInitFinish(int count);
    void emoticonsPageViewCountChanged(int count);
    void playTo(int position);
    void playBy(int oldPosition, int newPosition);
}

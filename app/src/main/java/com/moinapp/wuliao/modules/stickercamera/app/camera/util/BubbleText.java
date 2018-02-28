package com.moinapp.wuliao.modules.stickercamera.app.camera.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 气泡内某行文字
 * Created by moying on 15/11/17.
 */
public class BubbleText extends StyledText implements Parcelable {
    int width; // 这行文字的宽度
    int height; // 这行文字的高度

    public BubbleText(int start, int length, String username, int width, int height) {
        super(start, length, username);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "BubbleText{" +
                "text='" + super.getUsername() + '\'' +
                ", start=" + super.getStart() +
                ", length=" + super.getLength() +
                ", end=" + super.getEnd() +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.start);
        parcel.writeInt(this.length);
        parcel.writeString(this.username);
        parcel.writeInt(this.width);
        parcel.writeInt(this.height);
    }

    // 添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Parcelable.Creator<BubbleText> CREATOR = new Parcelable.Creator<BubbleText>() {
        @Override
        public BubbleText createFromParcel(Parcel source) {
            // 从Parcel中读取数据，返回person对象
            return new BubbleText(source.readInt(), source.readInt(), source.readString(), source.readInt(), source.readInt());
        }

        @Override
        public BubbleText[] newArray(int size) {
            return new BubbleText[size];
        }
    };
}

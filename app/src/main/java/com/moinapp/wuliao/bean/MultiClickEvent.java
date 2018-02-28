package com.moinapp.wuliao.bean;

/**
 * Created by moying on 16/3/30.
 */
public class MultiClickEvent {

    public Builder builder;

    public MultiClickEvent(Builder builder) {
        this.builder = builder;
    }

    public static class Builder {

        Object object = new Object();
        int originNum = 0;
        int clickNum = 0;

        public Builder() {
        }

        public Object getObject() {
            return object;
        }

        public int getOriginNum() {
            return originNum;
        }

        public int getClickNum() {
            return clickNum;
        }

        public Builder setObject(Object object) {
            this.object = object;
            return this;
        }

        public Builder setOriginNum(int originNum) {
            this.originNum = originNum;
            return this;
        }

        public Builder setClickNum(int clickNum) {
            this.clickNum = clickNum;
            return this;
        }

        public MultiClickEvent build() {
            return new MultiClickEvent(this);
        }
    }
}

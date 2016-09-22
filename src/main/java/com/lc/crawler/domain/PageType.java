package com.lc.crawler.domain;

/**
 * Created by Ling Cao on 2016/8/17.
 */
public enum PageType {
    HOME((byte) 0), LIST((byte) 1), DETAIL((byte) 2);
    PageType(byte type) {
        this.type = type;
    }

    byte type;
    public byte getType() {
        return type;
    }
}

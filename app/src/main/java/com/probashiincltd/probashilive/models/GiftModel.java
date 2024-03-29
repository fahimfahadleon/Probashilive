package com.probashiincltd.probashilive.models;

public class GiftModel {
    GiftItem giftItem;
    String from;
    String to;
    String index;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public GiftItem getGiftItem() {
        return giftItem;
    }

    public void setGiftItem(GiftItem giftItem) {
        this.giftItem = giftItem;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

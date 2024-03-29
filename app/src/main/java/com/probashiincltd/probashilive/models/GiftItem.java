package com.probashiincltd.probashilive.models;

public class GiftItem {

    public static final String GIFT_TYPE_IMAGE = "image";
    public static final String GIFT_TYPE_LOTTIE = "lottie";
    public static final String GIFT_TYPE_SVGA_HALF = "svga_half";
    public static final String GIFT_TYPE_SVGA_FULL = "svga_full";
    String giftReference;
    String giftPrice;
    String giftName;
    String giftType;

    public String getGiftType() {
        return giftType;
    }

    public void setGiftType(String giftType) {
        this.giftType = giftType;
    }

    public String getGiftReference() {
        return giftReference;
    }

    public void setGiftReference(String giftReference) {
        this.giftReference = giftReference;
    }

    public String getGiftPrice() {
        return giftPrice;
    }

    public void setGiftPrice(String giftPrice) {
        this.giftPrice = giftPrice;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    @Override
    public String toString() {
        return "GiftItem{" +
                "giftReference='" + giftReference + '\'' +
                ", giftPrice='" + giftPrice + '\'' +
                ", giftName='" + giftName + '\'' +
                ", giftType='" + giftType + '\'' +
                '}';
    }
}

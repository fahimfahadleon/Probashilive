package com.probashiincltd.probashilive.callbacks;

import org.jivesoftware.smackx.httpfileupload.element.Slot;

import java.util.ArrayList;

public interface ImageUploadCallback {
    void onFailed(String reason);
    void onSuccess(ArrayList<Slot> originalSlots,ArrayList<Slot>thumbnailSlots,String type);
}

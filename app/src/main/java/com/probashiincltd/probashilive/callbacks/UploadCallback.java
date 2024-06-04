package com.probashiincltd.probashilive.callbacks;
import org.jivesoftware.smackx.httpfileupload.element.Slot;

public interface UploadCallback {
    void onFailed();
    void onSuccess(Slot slot);
}

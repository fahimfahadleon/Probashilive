package com.probashiincltd.probashilive.callbacks;



import java.io.InputStream;

public interface ImageLoadCallback {

    void onSuccess(InputStream inputStream);
    void onFailed(String error);
}

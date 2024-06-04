package com.probashiincltd.probashilive.cache;


import com.probashiincltd.probashilive.config.Unit;

public interface Callback {
    void onCacheLoad(Unit unit);
    void onSourceLoad(Unit unit);
    Unit sourceRequest();
}

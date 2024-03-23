package com.probashiincltd.probashilive.models;

import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;

public class MessageProfileModel {
    String id;
    ProfileItem profileItem;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProfileItem getProfileItem() {
        return profileItem;
    }

    public void setProfileItem(ProfileItem profileItem) {
        this.profileItem = profileItem;
    }


}

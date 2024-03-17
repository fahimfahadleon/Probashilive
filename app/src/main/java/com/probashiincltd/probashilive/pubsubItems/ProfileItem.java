package com.probashiincltd.probashilive.pubsubItems;

import static com.probashiincltd.probashilive.utils.Configurations.CONTENT;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;

import android.util.Log;

import androidx.annotation.NonNull;

import com.probashiincltd.probashilive.functions.Functions;

import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ProfileItem extends UniversalModelMap{

    public static final String NAME = "name";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String EMAIL = "email";
    public static final String PHONE = "phone";
    public static final String COIN = "coin";
    public static final String VIP = "vip";


    public ProfileItem(HashMap<String, String> content) {
        super(content, new ArrayList<>(Arrays.asList(
                NAME
                , PROFILE_PICTURE
                , EMAIL
                , PHONE
                , COIN
                , VIP
        )));
    }

    @Override
    public String getName() {
        return Functions.getSP(LOGIN_USER, "");
    }

    @Override
    public String getXmlns() {
        return "pubsub:live:users:user";
    }


    @Override
    public String getID() {
        return Functions.getSP(LOGIN_USER, "");
    }


    public static ProfileItem parseProfileItem(Item item) throws JSONException {
        if(item == null){
            throw new JSONException("empty json");
        }
        JSONObject json = XML.toJSONObject(item.toXML().toString());
        JSONObject jsonObject = json.getJSONObject("item");
        String id = jsonObject.getString("id");
        JSONObject content = jsonObject.getJSONObject(id);

        HashMap<String,String> profileMap = new HashMap<>();
        profileMap.put(NAME, content.getJSONObject(NAME).optString(CONTENT));
        profileMap.put(PROFILE_PICTURE,content.getJSONObject(PROFILE_PICTURE).optString(CONTENT));
        profileMap.put(EMAIL, content.getJSONObject(EMAIL).optString(CONTENT));
        profileMap.put(PHONE, content.getJSONObject(PHONE).optString(CONTENT));
        profileMap.put(COIN, content.getJSONObject(COIN).optString(CONTENT));
        profileMap.put(VIP, content.getJSONObject(VIP).optString(CONTENT));
        return new ProfileItem(profileMap);


    }


    @NonNull
    @Override
    public String toString() {
        return "content=" + content;
    }
    @Override
    public boolean equals(Object obj) {
        Log.e("checking","1");
        if (this == obj) return true;
        Log.e("checking","2");

        if (obj == null || getClass() != obj.getClass()) return false;
        Log.e("checking","3");

        if (!super.equals(obj)) return false; // Check equality in UniversalModelMap
        Log.e("checking","4");

        ProfileItem that = (ProfileItem) obj;
        Log.e("checking","5");

        return content.get(NAME).equals(that.content.get(NAME));
    }


}

package com.probashiincltd.probashilive.pubsubItems;

import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;

import com.probashiincltd.probashilive.functions.Functions;

import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ProfileItem extends UniversalModelMap {


    public ProfileItem(HashMap<String, String> content) {
        super(content, new ArrayList<>(Arrays.asList(
                "name"
                , "profile_picture"
                , "email"
                , "phone"
                , "coin"
                , "vip"
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
        profileMap.put("name", content.getJSONObject("name").optString("content"));
        profileMap.put("profile_picture",content.getJSONObject("profile_picture").optString("content"));
        profileMap.put("email", content.getJSONObject("email").optString("content"));
        profileMap.put("phone", content.getJSONObject("phone").optString("content"));
        profileMap.put("coin", content.getJSONObject("coin").optString("content"));
        profileMap.put("vip", content.getJSONObject("vip").optString("content"));
        return new ProfileItem(profileMap);


    }


}

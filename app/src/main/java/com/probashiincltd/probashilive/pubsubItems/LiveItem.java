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

public class LiveItem extends UniversalModelMap{
    
    public LiveItem(HashMap<String,String> content) {
        super(content,new ArrayList<>(Arrays.asList(
                "profile_image",
                "name",
                "room_id",
                "viewers",
                "startedAt",
                "type",
                "vip",
                "sdp",
                "country",
                "ip",
                "country_code",
                "city",
                "timezone",
                "regionName"

        )));
    }

    @Override
    public String getName() {
        return Functions.getSP(LOGIN_USER,"");
    }

    @Override
    public String getXmlns() {
        return "pubsub:live:user:user";
    }

    @Override
    public String getID() {
        return Functions.getSP(LOGIN_USER,"");
    }

    public static LiveItem parseLiveItem(Item item) throws JSONException {
        if(item == null){
            throw new JSONException("empty json");
        }
        JSONObject json = XML.toJSONObject(item.toXML().toString());
        JSONObject jsonObject = json.getJSONObject("item");
        String id = jsonObject.getString("id");
        JSONObject content = jsonObject.getJSONObject(id);

        String name = content.getJSONObject("name").optString("content");
        String profilePic = content.getJSONObject("profile_image").optString("content");
        String room_id = content.getJSONObject("room_id").optString("content");
        String viewers = content.getJSONObject("viewers").optString("content");
        String startedAt = content.getJSONObject("startedAt").optString("content");
        String type = content.getJSONObject("type").optString("content");
        String sdp = content.getJSONObject("sdp").optString("content");
        String vip = content.getJSONObject("vip").optString("content");
        String country = content.getJSONObject("country").optString("content");
        String ip = content.getJSONObject("ip").optString("content");
        String country_code = content.getJSONObject("country_code").optString("content");
        String city = content.getJSONObject("city").optString("content");
        String timezone = content.getJSONObject("timezone").optString("content");
        String regionName = content.getJSONObject("regionName").optString("content");

        HashMap<String,String> map = new HashMap<>();
        map.put("profile_image", profilePic);
        map.put("name",name);
        map.put("room_id",room_id);
        map.put("viewers",viewers);
        map.put("startedAt",startedAt);
        map.put("type",type);
        map.put("sdp",sdp);
        map.put("vip",vip);
        map.put("country", country);
        map.put("ip",ip);
        map.put("country_code",country_code);
        map.put("city",city);
        map.put("timezone",timezone);
        map.put("regionName",regionName);

        return new LiveItem(map);


    }

}

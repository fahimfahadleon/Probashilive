package com.probashiincltd.probashilive.pubsubItems;

import static com.probashiincltd.probashilive.utils.Configurations.CONTENT;
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
    public static final String PROFILE_IMAGE = "profile_image";
    public static final String NAME = "name";
    public static final String ROOM_ID = "room_id";
    public static final String VIEWERS = "viewers";
    public static final String STARTED_AT = "startedAt";
    public static final String TYPE = "type";
    public static final String VIP = "vip";
    public static final String SDP = "sdp";
    public static final String COUNTRY = "country";
    public static final String IP = "ip";
    public static final String COUNTRY_CODE = "country_code";
    public static final String CITY = "city";
    public static final String TIME_ZONE = "timezone";
    public static final String REGION_NAME = "regionName";
    
    
    
    public LiveItem(HashMap<String,String> content) {
        super(content,new ArrayList<>(Arrays.asList(
                PROFILE_IMAGE,
                NAME,
                ROOM_ID,
                VIEWERS,
                STARTED_AT,
                TYPE,
                VIP,
                SDP,
                COUNTRY,
                IP,
                COUNTRY_CODE,
                CITY,
                TIME_ZONE,
                REGION_NAME

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

        String name = content.getJSONObject(NAME).optString(CONTENT);
        String profilePic = content.getJSONObject(PROFILE_IMAGE).optString(CONTENT);
        String room_id = content.getJSONObject(ROOM_ID).optString(CONTENT);
        String viewers = content.getJSONObject(VIEWERS).optString(CONTENT);
        String startedAt = content.getJSONObject(STARTED_AT).optString(CONTENT);
        String type = content.getJSONObject(TYPE).optString(CONTENT);
        String sdp = content.getJSONObject(SDP).optString(CONTENT);
        String vip = content.getJSONObject(VIP).optString(CONTENT);
        String country = content.getJSONObject(COUNTRY).optString(CONTENT);
        String ip = content.getJSONObject(IP).optString(CONTENT);
        String country_code = content.getJSONObject(COUNTRY_CODE).optString(CONTENT);
        String city = content.getJSONObject(CITY).optString(CONTENT);
        String timezone = content.getJSONObject(TIME_ZONE).optString(CONTENT);
        String regionName = content.getJSONObject(REGION_NAME).optString(CONTENT);

        HashMap<String,String> map = new HashMap<>();
        map.put(PROFILE_IMAGE, profilePic);
        map.put(NAME,name);
        map.put(ROOM_ID,room_id);
        map.put(VIEWERS,viewers);
        map.put(STARTED_AT,startedAt);
        map.put(TYPE,type);
        map.put(SDP,sdp);
        map.put(VIP,vip);
        map.put(COUNTRY, country);
        map.put(IP,ip);
        map.put(COUNTRY_CODE,country_code);
        map.put(CITY,city);
        map.put(TIME_ZONE,timezone);
        map.put(REGION_NAME,regionName);

        return new LiveItem(map);


    }

}

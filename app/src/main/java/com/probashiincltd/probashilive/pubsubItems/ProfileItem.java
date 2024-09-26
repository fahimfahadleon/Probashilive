package com.probashiincltd.probashilive.pubsubItems;

import static com.probashiincltd.probashilive.connectionutils.CM.NODE_USERS;
import static com.probashiincltd.probashilive.utils.Configurations.CONTENT;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;

import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.utils.XmppXMLParser;

import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

public class ProfileItem extends UniversalModelMap{

    public static final String NAME = "name";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String EMAIL = "email";
    public static final String LANDING_ANIMATION = "landing_animation";
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
                , LANDING_ANIMATION
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

        String s = "";
        if(!XmppXMLParser.load(item.toXML().toString()).getText("/item/"+item.getId()+"/phone/").isEmpty()){
            s = XmppXMLParser.load(item.toXML().toString()).getText("/item/"+item.getId()+"/phone/").get(0);
        }

        profileMap.put(PHONE, s);

        profileMap.put(COIN, content.getJSONObject(COIN).optString(CONTENT));
        profileMap.put(VIP, content.getJSONObject(VIP).optString(CONTENT));
        profileMap.put(LANDING_ANIMATION,content.getJSONObject(LANDING_ANIMATION).optString(CONTENT));
        return new ProfileItem(profileMap);
    }


//    @NonNull
//    @Override
//    public String toString() {
//        return "content=" + content;
//    }


    @Override
    public String toString() {
        return "ProfileItem{" +
                "content=" + content +
                ", requiredFields=" + requiredFields +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false; // Check equality in UniversalModelMap
        ProfileItem that = (ProfileItem) obj;
        return content.get(NAME).equals(that.content.get(NAME));
    }

    public static void updateProfileItem(ProfileItem profileItem){
        try {
            Item i = Functions.createRawItem(profileItem);
            Functions.publishToNode(NODE_USERS, i, i.getId());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }




}

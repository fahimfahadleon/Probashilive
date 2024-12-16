package com.probashiincltd.probashilive.viewmodels;

import static com.probashiincltd.probashilive.connectionutils.CM.NODE_USERS;
import static com.probashiincltd.probashilive.connectionutils.RosterHandler.TYPE_FOLLOWING;
import static com.probashiincltd.probashilive.connectionutils.RosterHandler.TYPE_NO_FRIEND;
import static com.probashiincltd.probashilive.connectionutils.RosterHandler.getRosterHandler;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.CITY;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.COUNTRY;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.COUNTRY_CODE;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.IP;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.NAME;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.PROFILE_IMAGE;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.REGION_NAME;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.ROOM_ID;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.SDP;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.STARTED_AT;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.TIME_ZONE;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.TYPE;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.VIEWERS;
import static com.probashiincltd.probashilive.pubsubItems.LiveItem.VIP;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_ENDED;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_LEFT;
import static com.probashiincltd.probashilive.utils.Configurations.ADD_PERSON;
import static com.probashiincltd.probashilive.utils.Configurations.CLOSE_LIVE;
import static com.probashiincltd.probashilive.utils.Configurations.DATA;
import static com.probashiincltd.probashilive.utils.Configurations.END_CALL_1;
import static com.probashiincltd.probashilive.utils.Configurations.END_CALL_2;
import static com.probashiincltd.probashilive.utils.Configurations.GIFT;
import static com.probashiincltd.probashilive.utils.Configurations.GIFT1;
import static com.probashiincltd.probashilive.utils.Configurations.HIDE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.INITIAL_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.JOIN_REQUEST;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_VIDEO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_AUDIENCE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_JOIN_REQUEST;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE_1;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE_2;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_TIMER_DIALOG;
import static com.probashiincltd.probashilive.utils.Configurations.SHOW_VIEWERS;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMPETITOR_LEFT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMPETITOR_LIST;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_HOST_REMOVED_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_JOINED_LIVE;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_JOIN_REQUEST;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_LIVE_ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_REQUEST_ACCEPTED;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_REQUEST_TO_LEAVE_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_SEND_GIFT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_DISABLE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_INVITATION;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_INVITATION_ACCEPTED;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_STREAM_JOINED;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIEWERS_LIST;
import static com.probashiincltd.probashilive.utils.Configurations.SWITCH_CAMERA;
import static com.probashiincltd.probashilive.utils.Configurations.isOccupied;

import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.CommentAdapter;
import com.probashiincltd.probashilive.callbacks.HeadlineMessageListener;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.CommentModel;
import com.probashiincltd.probashilive.models.GiftItem;
import com.probashiincltd.probashilive.models.GiftModel;
import com.probashiincltd.probashilive.models.LiveAction;
import com.probashiincltd.probashilive.models.MessageProfileModel;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.impl.JidCreate;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePublisher;

public class RTMPCallViewModel extends ViewModel {
    NodePublisher nodePublisher;
    NodePlayer nodePlayer;
    String action;
    boolean isCommentDisabled = false;
    HashMap<String, String> live = new HashMap<>();

    public HashMap<String,String>getLive(){
        return live;
    }
    HashMap<String, String> data;
    ArrayList<String> viewers;

    ArrayList<LiveItem> competitorList = new ArrayList<>();
    HashMap<String, ProfileItem> viewerProfiles = new HashMap<>();

    public int getViewersCount() {
        return viewers.size();
    }


    public ArrayList<LiveItem> getCompetitorList() {
        return competitorList;
    }
    private final ArrayList<MessageProfileModel> joinRequests = new ArrayList<>();

    public ArrayList<MessageProfileModel>getRequests(){
        return joinRequests;
    }


    private final MutableLiveData<String> onSendButtonClick = new MutableLiveData<>();
    private final MutableLiveData<ProfileItem> onCompetitorAccepted = new MutableLiveData<>();
    public LiveData<ProfileItem>getOnCompetitorAccepted(){
        return onCompetitorAccepted;
    }
    private final MutableLiveData<ArrayList<LiveItem>> onViewUpdate = new MutableLiveData<>();
    private final MutableLiveData<ProfileItem>onCompetitorJoined = new MutableLiveData<>();
    CommentAdapter adapter;
    private final MutableLiveData<CommentModel> selectedItem = new MutableLiveData<>();
    private final MutableLiveData<GiftModel> onGiftReceived = new MutableLiveData<>();
    private final MutableLiveData<LiveAction> liveActiondata = new MutableLiveData<>();
    private final MutableLiveData<Integer> getLiveViewerCount = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, String>> setUpComplete = new MutableLiveData<>();
    private final MutableLiveData<Boolean> optionsMenu = new MutableLiveData<>();

    private final MutableLiveData<ProfileItem> openProfile = new MutableLiveData<>();
    private final MutableLiveData<Integer> followClicked = new MutableLiveData<>();
    private final MutableLiveData<String> showToast = new MutableLiveData<>();

    private final MutableLiveData<Boolean> updateCommentSection = new MutableLiveData<>();
    public LiveData<Integer>getFollowClicked(){
        return followClicked;
    }
    private final MutableLiveData<CommentModel> onCommentInserted = new MutableLiveData<>();
    public LiveData<CommentModel>getOnCommentInserted(){
        return onCommentInserted;
    }

    void setOpenProfile(ProfileItem profile) {
        openProfile.setValue(profile);
    }

    public LiveData<ProfileItem> getOpenProfile() {
        return openProfile;
    }
    public LiveData<String> getShowToast() {
        return showToast;
    }
    public LiveData<GiftModel> getGiftReceived() {
        return onGiftReceived;
    }
    public LiveData<Boolean> getUpdateCommentSection() {
        return updateCommentSection;
    }

    public LiveData<ArrayList<LiveItem>> getViewUpdate() {
        return onViewUpdate;
    }

    public LiveData<Integer> getLiveViewerCount() {
        return getLiveViewerCount;
    }
    private final MutableLiveData<Message>receiveJoinRequest = new MutableLiveData<>();
    public LiveData<Message>getReceivedJoinRequest(){
        return receiveJoinRequest;
    }

    String myJid;
    ArrayList<String> users;
    boolean isOptionsOpen = false;


    public LiveData<LiveAction> getLiveAction() {
        return liveActiondata;
    }

    public LiveData<Boolean> getOptionsMenuVisibility() {
        return optionsMenu;
    }

    public LiveData<HashMap<String, String>> getOnSetUpComplete() {
        return setUpComplete;
    }


    public void onClickSend(View vi) {
        int id = vi.getId();
        if (id == R.id.send) {
            onSendButtonClick.setValue(SUBJECT_TYPE_COMMENT);
        }  else if (id == R.id.profile || id == R.id.name || id == R.id.vip) {
            switch (action) {
                case LIVE_USER_TYPE_AUDIENCE:
                case LIVE_USER_TYPE_COMPETITOR: {
                    try {
                        ProfileItem profileItem = ProfileItem.parseProfileItem(Functions.getSingleItemOfNode(NODE_USERS,data.get(ROOM_ID).split("@")[0]));
                        setOpenProfile(profileItem);
                    }catch (Exception e){
                        e.fillInStackTrace();
                    }

                    break;
                }
            }
        } else if (id == R.id.options) {
            isOptionsOpen = !isOptionsOpen;
            optionsMenu.setValue(isOptionsOpen);
        } else if (id == R.id.option1) {
            if (!isOccupied) {
                sendComment("I would like to join the live.","Join Request");
                onSendButtonClick.setValue(JOIN_REQUEST);
            }
        } else if (id == R.id.option2) {
            onSendButtonClick.setValue(SWITCH_CAMERA);
        } else if (id == R.id.option3) {
            onSendButtonClick.setValue(HIDE_COMMENT);
        } else if (id == R.id.option4 || id == R.id.gift0) {
            onSendButtonClick.setValue(GIFT);
        }  else if (id == R.id.gift1) {
            onSendButtonClick.setValue(GIFT1);
        } else if (id == R.id.addPeople) {
            onSendButtonClick.setValue(ADD_PERSON);
        } else if (id == R.id.profile1 || id == R.id.name1 || id == R.id.vip1) {
            onSendButtonClick.setValue(OPEN_PROFILE_1);
        } else if (id == R.id.profile2 || id == R.id.name2 || id == R.id.vip2) {
            onSendButtonClick.setValue(OPEN_PROFILE_2);
        } else if (id == R.id.endCall1) {
            onSendButtonClick.setValue(END_CALL_1);
        } else if (id == R.id.endCall2) {
            onSendButtonClick.setValue(END_CALL_2);
        } else if (id == R.id.viewers) {
            onSendButtonClick.setValue(SHOW_VIEWERS);
        }else if(id == R.id.option6){
            onSendButtonClick.postValue(OPEN_JOIN_REQUEST);
        } else if (id == R.id.follow) {
            String text = "";
            String jid = data.get(NAME)+ "@" + Configurations.getHostName();
            int isFriendStatus = Functions.isFollowingOrFollower(jid);
            if (isFriendStatus == TYPE_FOLLOWING) {
                getRosterHandler().removeEntry(jid);
                isFriendStatus = TYPE_NO_FRIEND;
                text = "Unfollowed";
            } else if (isFriendStatus == TYPE_NO_FRIEND) {
                getRosterHandler().createEntry(jid, data.get(NAME));
                isFriendStatus = TYPE_FOLLOWING;
                text = "Following";
            } else {
                getRosterHandler().addGroup(jid);
                isFriendStatus = TYPE_FOLLOWING;
                text = "Congratulations you become a Fan";
            }
            followClicked.setValue(isFriendStatus);
            showToast.setValue(text);
        } else if (id == R.id.option7) {
            isCommentDisabled = !isCommentDisabled;
            sendCommentDisabled();
            showToast.setValue(isCommentDisabled?"Comment Disabled":"Comment Enabled");
        }else if(id == R.id.option5){
            onSendButtonClick.setValue(OPEN_TIMER_DIALOG);
        }else if(id == R.id.closeLive){
            onSendButtonClick.setValue(Configurations.CLOSE_LIVE_HOST);
        }
    }

    void sendCommentDisabled(){
        broadCastEvent(isCommentDisabled,SUBJECT_TYPE_VIDEO_DISABLE_COMMENT);
        broadCastToCompetitor(isCommentDisabled,SUBJECT_TYPE_VIDEO_DISABLE_COMMENT);
    }



    public LiveData<String> getSendComment() {
        return onSendButtonClick;
    }

    public void sendComment(String ... comment) {
        CommentModel cm = new CommentModel();
        cm.setComment(comment[0]);
        cm.setName(CM.getProfile().getContent().get(ProfileItem.NAME));
        cm.setPp(CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));
        cm.setVip(CM.getProfile().getContent().get(ProfileItem.VIP));
        cm.setId(myJid);
        HashMap<String,String>map = new HashMap<>();
        try {
            map.put(TYPE,comment[1]);
        }catch (Exception e){
            //ignored
        }
        cm.setContent(map);
        if (action.equals(LIVE_USER_TYPE_HOST)) {
            adapter.addData(cm);
            broadCastEvent(cm, SUBJECT_TYPE_COMMENT);
            broadCastToCompetitor(cm, SUBJECT_TYPE_COMMENT);
        } else {
            CM.sendHLM(SUBJECT_TYPE_COMMENT, new Gson().toJson(cm), myJid, getData().get(ROOM_ID));
        }

    }
    public LiveData<ProfileItem>getCompetitorJoined(){
        return onCompetitorJoined;
    }

    public void broadCastEvent(Object cm, String type) {
        for (String s : viewers) {
            CM.sendHLM(type, new Gson().toJson(cm), myJid, s);
        }
    }

    public ProfileItem getViewerProfile(String id){
        return viewerProfiles.get(id);
    }

    HeadlineMessageListener listener = stanza -> {
        Message message = (Message) stanza;
        switch (message.getSubject()) {
            case SUBJECT_TYPE_SEND_GIFT:{
                GiftModel giftModel = new Gson().fromJson(message.getBody(),GiftModel.class);
                if(action.equals(LIVE_USER_TYPE_HOST)){
                    broadCastEvent(giftModel,SUBJECT_TYPE_SEND_GIFT);
                    broadCastToCompetitor(giftModel,SUBJECT_TYPE_SEND_GIFT);

                    CommentModel cm = new CommentModel();
                    cm.setComment(giftModel.getFrom().split("@")[0] +" sent gift "+giftModel.getGiftItem().getGiftName().replace(".png","")+"("+giftModel.getGiftItem().getGiftPrice()+") to "+giftModel.getTo().split("@")[0]);
                    cm.setName(CM.getProfile().getContent().get(ProfileItem.NAME));
                    cm.setPp(CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));
                    cm.setVip(CM.getProfile().getContent().get(ProfileItem.VIP));
                    cm.setId(myJid);
                    HashMap<String,String>map = new HashMap<>();
                    map.put(TYPE,GIFT);
                    cm.setContent(map);
                    broadCastEvent(cm, SUBJECT_TYPE_COMMENT);
                    broadCastToCompetitor(cm, SUBJECT_TYPE_COMMENT);

                }
                onGiftReceived.postValue(giftModel);
                break;
            }
            case SUBJECT_TYPE_VIDEO_INVITATION_ACCEPTED: {
//                for Host only
                onCompetitorJoined.postValue(new Gson().fromJson(message.getBody(),ProfileItem.class));
                isOccupied = true;
                break;
            }
            case SUBJECT_TYPE_VIDEO_STREAM_JOINED: {
//                for Host only
                LiveItem liveItem = new Gson().fromJson(message.getBody(), LiveItem.class);
                competitorList.add(liveItem);
                broadCastEvent(competitorList, SUBJECT_TYPE_COMPETITOR_LIST);
                broadCastToCompetitor(competitorList, SUBJECT_TYPE_COMPETITOR_LIST);
                if(isCommentDisabled){
                    CM.sendHLM(SUBJECT_TYPE_VIDEO_DISABLE_COMMENT, String.valueOf(true), CM.getConnection().getUser().asFullJidOrThrow().toString(), message.getFrom().asFullJidOrThrow().toString());
                }
                onViewUpdate.postValue(competitorList);
                break;
            }
            case SUBJECT_TYPE_JOINED_LIVE: {
//                for Host only
                viewerJoined(message);
                break;
            }
            case SUBJECT_TYPE_COMMENT: {
//                for All
                CommentModel cm = new Gson().fromJson(message.getBody(), CommentModel.class);
                adapter.addData(cm);
                if (action.equals(LIVE_USER_TYPE_HOST)) {
                    broadCastEvent(cm, SUBJECT_TYPE_COMMENT);
                    broadCastToCompetitor(cm, SUBJECT_TYPE_COMMENT);
                }
                break;
            }
            case SUBJECT_TYPE_LIVE_ACTION: {

//                for all
                LiveAction liveAction = new Gson().fromJson(message.getBody(), LiveAction.class);
                liveActiondata.postValue(liveAction);
                break;
            }
            case SUBJECT_TYPE_VIEWERS_LIST: {

//                for competitor and audience
                Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                viewers = new ArrayList<>(new Gson().fromJson(message.getBody(), listType));
                updateViewerProfiles();
                getLiveViewerCount.postValue(viewers.size());
                break;
            }
            case SUBJECT_TYPE_COMPETITOR_LIST: {
//                for audience and competitor
                Type listType = new TypeToken<ArrayList<LiveItem>>() {
                }.getType();
                competitorList = new ArrayList<>(new Gson().fromJson(message.getBody(), listType));
                setUpForCompetitorforHost();
                break;
            }
            case SUBJECT_TYPE_COMPETITOR_LEFT: {
                ProfileItem profileItem = new Gson().fromJson(message.getBody(), ProfileItem.class);
                removeFromCompetitorList(profileItem);
                postActionRemoveCompetitor(profileItem);
                break;
            }
            case SUBJECT_TYPE_HOST_REMOVED_COMPETITOR: {
                action = LIVE_USER_TYPE_AUDIENCE;
                onSendButtonClick.postValue(SUBJECT_TYPE_HOST_REMOVED_COMPETITOR);
                break;
            } case SUBJECT_TYPE_JOIN_REQUEST:{
                MessageProfileModel messageProfileModel = new Gson().fromJson(message.getBody(), MessageProfileModel.class);
                boolean shouldAdd = true;
                for(MessageProfileModel m: joinRequests){
                    if(m.getId().equals(message.getFrom().asFullJidOrThrow().toString())){
                        shouldAdd = false;
                        break;
                    }
                }
                if(shouldAdd){
                    joinRequests.removeIf(m -> Objects.equals(m.getProfileItem().getContent().get(ProfileItem.NAME), messageProfileModel.getProfileItem().getContent().get(ProfileItem.NAME)));
                    joinRequests.add(new Gson().fromJson(message.getBody(), MessageProfileModel.class));
                }

                receiveJoinRequest.postValue(message);
                break;
            } case SUBJECT_TYPE_REQUEST_ACCEPTED:{
                this.action= LIVE_USER_TYPE_COMPETITOR;
                ProfileItem profileItem = new Gson().fromJson(message.getBody(),ProfileItem.class);
                onCompetitorAccepted.postValue(profileItem);
                viewers.remove(CM.getConnection().getUser().asFullJidOrThrow().toString());
                getLiveViewerCount.postValue(viewers.size());
                break;
            }case SUBJECT_TYPE_REQUEST_TO_LEAVE_COMPETITOR:{
                removeCompetitor(0);
                break;
            } case SUBJECT_TYPE_VIDEO_DISABLE_COMMENT:{
                isCommentDisabled = new Gson().fromJson(message.getBody(),Boolean.class);
                if(isCommentDisabled){
                    showToast.postValue("Comment Disabled by Host!");
                    updateCommentSection.postValue(true);
                }else {
                    showToast.postValue("Comment Enabled by Host!");
                    updateCommentSection.postValue(false);
                }

                break;
            }
        }
    };
    public void requestToLeave(){
        CM.sendHLM(SUBJECT_TYPE_REQUEST_TO_LEAVE_COMPETITOR,"",CM.getConnection().getUser().asFullJidOrThrow().toString(),data.get(ROOM_ID));
    }

    public void sendJoinRequest(){
        MessageProfileModel model = new MessageProfileModel();
        model.setId(CM.getConnection().getUser().asFullJidOrThrow().toString());
        model.setProfileItem(CM.getProfile());
        CM.sendHLM(SUBJECT_TYPE_JOIN_REQUEST,new Gson().toJson(model),CM.getConnection().getUser().asFullJidOrThrow().toString(),data.get(ROOM_ID));
    }

    void postActionRemoveCompetitor(ProfileItem profileItem) {
        if (action.equals(LIVE_USER_TYPE_HOST)) {
            broadCastEvent(profileItem, SUBJECT_TYPE_COMPETITOR_LEFT);
            broadCastToCompetitor(profileItem, SUBJECT_TYPE_COMPETITOR_LEFT);
            if (competitorList.isEmpty()) {
                isOccupied = false;
            }
        }
        onViewUpdate.postValue(competitorList);
    }

    void removeFromCompetitorList(ProfileItem profileItem) {
        for (LiveItem liveItem : competitorList) {
            if (Objects.requireNonNull(liveItem.getContent().get(NAME)).equals(profileItem.getContent().get(ProfileItem.NAME))) {
                competitorList.remove(liveItem);
                break;
            }
        }
    }

    void updateViewerProfiles() {
        ArrayList<String> itemsToBeAdded = new ArrayList<>();
        for (String s : viewers) {
            if (!viewerProfiles.containsKey(s)) {
                itemsToBeAdded.add(s.split("@")[0]);
            }
        }
        if (itemsToBeAdded.isEmpty()) {
            return;
        }
        List<Item> userProfiles = Functions.getMultipleItemOfNode(NODE_USERS, itemsToBeAdded);
        try {
            for (Item i : userProfiles) {
                viewerProfiles.put(i.getId(), ProfileItem.parseProfileItem(i));
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }

    }

    void setUpForCompetitorforHost() {
        onViewUpdate.postValue(competitorList);
    }


    void broadCastToCompetitor(Object o, String type) {
        if (isOccupied) {
            for (LiveItem liveItem : competitorList) {
                CM.sendHLM(type, new Gson().toJson(o), CM.getConnection().getUser().asFullJidOrThrow().toString(), liveItem.getContent().get(ROOM_ID));
            }
        }
    }

    void viewerJoined(Message message) {
        viewers.add(message.getFrom().asFullJidOrThrow().toString());
        if (isOccupied) {
            CM.sendHLM(SUBJECT_TYPE_COMPETITOR_LIST, new Gson().toJson(competitorList), CM.getConnection().getUser().asFullJidOrThrow().toString(), message.getFrom().asFullJidOrThrow().toString());
        }
        if(isCommentDisabled){
            CM.sendHLM(SUBJECT_TYPE_VIDEO_DISABLE_COMMENT, String.valueOf(true), CM.getConnection().getUser().asFullJidOrThrow().toString(), message.getFrom().asFullJidOrThrow().toString());
        }
        getLiveViewerCount.postValue(viewers.size());
        broadCastEvent(viewers, SUBJECT_TYPE_VIEWERS_LIST);
        broadCastToCompetitor(viewers, SUBJECT_TYPE_VIEWERS_LIST);
        updateLiveItem();
        updateViewerProfiles();
    }

    public void viewerLeft(String s) {
        viewers.remove(s);
        getLiveViewerCount.postValue(viewers.size());
        broadCastEvent(viewers, SUBJECT_TYPE_VIEWERS_LIST);
        broadCastToCompetitor(viewers, SUBJECT_TYPE_VIEWERS_LIST);
        updateLiveItem();
    }

    void updateLiveItem() {
        try {
            if (!live.isEmpty()) {
                live.put(VIEWERS, String.valueOf(viewers.size()));
            } else {
                live.put(PROFILE_IMAGE, CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));
                live.put(NAME, CM.getProfile().getContent().get(ProfileItem.NAME));
                live.put(ROOM_ID, CM.getConnection().getUser().asFullJidOrThrow().toString());
                live.put(SDP, "");
                live.put(TYPE, LIVE_TYPE_VIDEO);
                live.put(VIP, CM.getProfile().getContent().get("vip"));
                live.put(COUNTRY, CM.getIPModel().getCountry());
                live.put(IP, CM.getIPModel().getQuery());
                live.put(COUNTRY_CODE, CM.getIPModel().getCountryCode());
                live.put(CITY, CM.getIPModel().getCity());
                live.put(TIME_ZONE, CM.getIPModel().getTimezone());
                live.put(REGION_NAME, CM.getIPModel().getRegionName());

                ZonedDateTime currentTime = ZonedDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'");
                String currentTimeString = currentTime.format(formatter);

                live.put(STARTED_AT, currentTimeString);
                live.put(VIEWERS, "0");
            }

            LiveItem liveItem = new LiveItem(live);
            Item item = Functions.createRawItem(liveItem);
            Functions.publishToNode(CM.NODE_LIVE_USERS, item, item.getId());
        } catch (ParserConfigurationException e) {
            Log.e("Checking","checkingException");
        }
    }


    @SuppressWarnings("unchecked")
    public void initViewModel(Context context, Intent i, FrameLayout surface, FrameLayout surface2) throws JSONException {
        this.action = i.getStringExtra(ACTION);

        users = new ArrayList<>();
        viewers = new ArrayList<>();
        myJid = CM.getConnection().getUser().asFullJidOrThrow().toString();

        switch (action) {
            case LIVE_USER_TYPE_HOST: {

                Log.e("checking","checking for host");
                setUpForHost(context, surface);
                setUpComplete.setValue(new HashMap<>());
                break;
            }
            case LIVE_USER_TYPE_AUDIENCE: {

                this.data = (HashMap<String, String>) i.getSerializableExtra("data");
                setUpForAudience(context, surface);
                setUpComplete.setValue(data);
                break;
            }
            case LIVE_USER_TYPE_COMPETITOR: {

                ProfileItem profileItem = new Gson().fromJson(i.getStringExtra(DATA), ProfileItem.class);
                this.data = LiveItem.parseLiveItem(Functions.getSingleItemOfNode(CM.NODE_LIVE_USERS, profileItem.getContent().get(ProfileItem.NAME))).getContent();
                setUpForCompetitor(context, surface, surface2);
                setUpComplete.setValue(data);
                break;
            }

        }
        CM.setHeadlineMessageListener(listener);
        adapter.setOnItemClickListener(this::setSelectedItem);
        adapter.setOnItemInsertedListener(this::onItemInserted);


    }

    private void onItemInserted(CommentModel commentModel) {
        onCommentInserted.postValue(commentModel);
    }

    public void switchCamera() {
        nodePublisher.switchCamera();
    }


    public void inviteFriend(FullJid id) {
        CM.sendHLM(SUBJECT_TYPE_VIDEO_INVITATION, new Gson().toJson(CM.getProfile()), CM.getConnection().getUser().asFullJidOrThrow().toString(), id.toString());
    }


    void setUpForHost(Context context, FrameLayout vi) {

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        nodePublisher = new NodePublisher(context, "");
        nodePublisher.setAudioCodecParam(NodePublisher.NMC_CODEC_ID_AAC, NodePublisher.NMC_PROFILE_AUTO, 48000, 1, 64_000);
        nodePublisher.setVideoOrientation(NodePublisher.VIDEO_ORIENTATION_PORTRAIT);
        nodePublisher.setVideoCodecParam(NodePublisher.NMC_CODEC_ID_H264, NodePublisher.NMC_PROFILE_AUTO, width, height, 30, 2_000_000);
        nodePublisher.attachView(vi);
        //not working in nokia 3
        nodePublisher.setHWAccelEnable(true);
        nodePublisher.setCameraFrontMirror(true);
        nodePublisher.setLinearZoom(0.0f);
        nodePublisher.setVolume(100.0f);
        nodePublisher.openCamera(true);
        nodePublisher.start(Configurations.RTMP_URL + CM.getProfile().getName());
        Log.e("streamingat", Configurations.RTMP_URL + CM.getProfile().getName());
        updateLiveItem();
    }

    public void setSelectedItem(CommentModel item) {
        selectedItem.setValue(item);
    }

    public LiveData<CommentModel> getSelectedItem() {
        return selectedItem;
    }

    public CommentAdapter getAdapter() {
        return adapter;
    }

    public String getAction() {
        return action;
    }

    public void setAdapter(CommentAdapter adapter) {
        this.adapter = adapter;
    }


    void setUpForAudience(Context context, FrameLayout surface) {
        String receiver = getData().get("room_id");
        nodePlayer = new NodePlayer(context, "");
        nodePlayer.attachView(surface);
        nodePlayer.start(Configurations.RTMP_URL + getData().get("name"));
        CM.sendHLM(SUBJECT_TYPE_JOINED_LIVE, new Gson().toJson(CM.getProfile()), myJid, receiver);
        sendComment(INITIAL_COMMENT);

    }

    LiveItem getLiveItemForCompetitor() {
        HashMap<String, String> live = new HashMap<>();
        live.put(PROFILE_IMAGE, CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));
        live.put(NAME, CM.getProfile().getContent().get(ProfileItem.NAME));
        live.put(ROOM_ID, CM.getConnection().getUser().asFullJidOrThrow().toString());
        live.put(SDP, "");
        live.put(TYPE, LIVE_TYPE_VIDEO);
        live.put(VIP, CM.getProfile().getContent().get("vip"));
        live.put(COUNTRY, CM.getIPModel().getCountry());
        live.put(IP, CM.getIPModel().getQuery());
        live.put(COUNTRY_CODE, CM.getIPModel().getCountryCode());
        live.put(CITY, CM.getIPModel().getCity());
        live.put(TIME_ZONE, CM.getIPModel().getTimezone());
        live.put(REGION_NAME, CM.getIPModel().getRegionName());
        ZonedDateTime currentTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'");
        String currentTimeString = currentTime.format(formatter);
        live.put(STARTED_AT, currentTimeString);
        live.put(VIEWERS, "0");
        return new LiveItem(live);
    }

    public void setUpForCompetitor(Context context, FrameLayout surface1, FrameLayout surface2) {
        LiveItem liveItem = getLiveItemForCompetitor();
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        nodePublisher = new NodePublisher(context, "");
        nodePublisher.setAudioCodecParam(NodePublisher.NMC_CODEC_ID_AAC, NodePublisher.NMC_PROFILE_AUTO, 48000, 2, 64_000);
        nodePublisher.setVideoOrientation(NodePublisher.VIDEO_ORIENTATION_PORTRAIT);
        nodePublisher.setVideoCodecParam(NodePublisher.NMC_CODEC_ID_H264, NodePublisher.NMC_PROFILE_AUTO, width, height, 30, 1_000_000);
        nodePublisher.attachView(surface2);
        nodePublisher.setHWAccelEnable(true);
        nodePublisher.setCameraFrontMirror(true);
        nodePublisher.setLinearZoom(0.0f);
        nodePublisher.setVolume(100.0f);
        nodePublisher.openCamera(true);
        nodePublisher.start(Configurations.RTMP_URL + CM.getProfile().getName());

        nodePlayer = new NodePlayer(context, "");
        nodePlayer.attachView(surface1);
        nodePlayer.start(Configurations.RTMP_URL + data.get(NAME));

        CM.sendHLM(SUBJECT_TYPE_VIDEO_STREAM_JOINED, new Gson().toJson(liveItem), CM.getConnection().getUser().asFullJidOrThrow().toString(), data.get(ROOM_ID));

        //todo change if there are multiple competitors move this to headline message sector
        onViewUpdate.setValue(new ArrayList<>(Collections.singleton(liveItem)));
    }

    public HashMap<String, String> getData() {
        if (data == null) {
            throw new RuntimeException("data not exists");
        }
        return data;
    }

    public void onDestroy() {

        if (action.equals(LIVE_USER_TYPE_HOST)) {
            nodePublisher.closeCamera();
//            nodePublisher.detachView();
            nodePublisher.stop();
            LiveAction liveAction = new LiveAction();
            liveAction.setActionType(ACTION_TYPE_LIVE_ENDED);
            liveAction.setActionContent(ACTION_TYPE_LIVE_ENDED);
            liveAction.setContentMap(new HashMap<>());
            broadCastEvent(liveAction, SUBJECT_TYPE_LIVE_ACTION);
            broadCastToCompetitor(liveAction, SUBJECT_TYPE_LIVE_ACTION);
            Functions.deleteFromNode(CM.NODE_LIVE_USERS, CM.getProfile().getContent().get("name"));
            Log.e("checking","ondestroy called from model");
        } else if (action.equals(LIVE_USER_TYPE_AUDIENCE)) {
            LiveAction liveAction = new LiveAction();
            liveAction.setActionType(ACTION_TYPE_LIVE_LEFT);
            liveAction.setActionContent(ACTION_TYPE_LIVE_LEFT);
            HashMap<String, String> map = new HashMap<>();
            map.put("name", CM.getProfile().getName());
            map.put("jid", CM.getConnection().getUser().asFullJidOrThrow().toString());
            liveAction.setContentMap(map);
            nodePlayer.detachView();
            nodePlayer.stop();
            CM.sendHLM(SUBJECT_TYPE_LIVE_ACTION, new Gson().toJson(liveAction), myJid, data.get("room_id"));
        } else {
            nodePublisher.closeCamera();
            nodePublisher.stop();
            nodePlayer.stop();
            CM.sendHLM(SUBJECT_TYPE_COMPETITOR_LEFT, new Gson().toJson(CM.getProfile()), CM.getConnection().getUser().asFullJidOrThrow().toString(), data.get(ROOM_ID));
        }
        CM.removeListener(listener);
        if (nodePlayer != null) {
            nodePlayer.stop();
        }
        if (nodePublisher != null) {
            nodePublisher.stop();
        }

        isOccupied = false;
    }

    public void removeCompetitor(int i) {
        try {
            LiveItem liveItem = competitorList.get(i);
            CM.sendHLM(SUBJECT_TYPE_HOST_REMOVED_COMPETITOR, "", CM.getConnection().getUser().asFullJidOrThrow().toString(), liveItem.getContent().get(LiveItem.ROOM_ID));

            ProfileItem profileItem = ProfileItem.parseProfileItem(Functions.getSingleItemOfNode(NODE_USERS, liveItem.getContent().get(NAME)));
            removeFromCompetitorList(profileItem);

            Message message = StanzaBuilder.buildMessage().build();
            message.setFrom(JidCreate.fullFrom(liveItem.getContent().get(ROOM_ID)));
            viewerJoined(message);

            postActionRemoveCompetitor(profileItem);



        } catch (Exception e) {
            Log.e("Checking","checkingException2");
        }
    }

    public void requestAccepted(MessageProfileModel model) {
        CM.sendHLM(SUBJECT_TYPE_REQUEST_ACCEPTED,new Gson().toJson(CM.getProfile()),CM.getConnection().getUser().asFullJidOrThrow().toString(),model.getId());
        viewerLeft(model.getId());
    }

    public void print(){
        Log.e("viewers",viewers.toString());
        Log.e("competitors",competitorList.toString());
    }

    public void sendGift(int index,String giftItem) {
        GiftItem giftItem1 = new Gson().fromJson(giftItem,GiftItem.class);
        GiftModel giftModel = new GiftModel();
        giftModel.setGiftItem(giftItem1);
        giftModel.setFrom(CM.getConnection().getUser().asFullJidOrThrow().toString());
        giftModel.setTo(index == 0?data.get(ROOM_ID):competitorList.get(0).getContent().get(ROOM_ID));
        giftModel.setIndex(String.valueOf(index));
        CM.sendHLM(SUBJECT_TYPE_SEND_GIFT,new Gson().toJson(giftModel),CM.getConnection().getUser().asFullJidOrThrow().toString(),data.get(ROOM_ID));
    }
}

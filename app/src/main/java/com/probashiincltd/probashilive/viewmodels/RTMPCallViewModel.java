package com.probashiincltd.probashilive.viewmodels;

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
import static com.probashiincltd.probashilive.utils.Configurations.HIDE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.INITIAL_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.JOIN_REQUEST;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_VIDEO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_AUDIENCE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE_1;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE_2;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMPETITOR_LEFT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMPETITOR_LIST;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_JOINED_LIVE;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_LIVE_ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_INVITATION;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_INVITATION_ACCEPTED;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_STREAM_JOINED;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIEWERS_LIST;
import static com.probashiincltd.probashilive.utils.Configurations.SWITCH_CAMERA;
import static com.probashiincltd.probashilive.utils.Configurations.isOccupied;

import android.content.Context;
import android.content.Intent;
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
import com.probashiincltd.probashilive.models.LiveAction;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;
import org.jxmpp.jid.FullJid;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePublisher;

public class RTMPCallViewModel extends ViewModel {
    NodePublisher nodePublisher;
    NodePlayer nodePlayer;
    String action;
    HashMap<String,String>live = new HashMap<>();
    HashMap<String,String>data;
    ArrayList<String>viewers;

    ArrayList<LiveItem> competitorList = new ArrayList<>();

    public int getViewersCount(){
        return viewers.size();
    }




    private final MutableLiveData<String> onSendButtonClick = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<LiveItem>> onViewUpdate = new MutableLiveData<>();
    CommentAdapter adapter;
    private final MutableLiveData<CommentModel> selectedItem = new MutableLiveData<>();
    private final MutableLiveData<LiveAction> liveActiondata = new MutableLiveData<>();
    private final MutableLiveData<Integer> getLiveViewerCount = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String,String>> setUpComplete = new MutableLiveData<>();
    private final MutableLiveData<Boolean> optionsMenu = new MutableLiveData<>();

    private final MutableLiveData<String> openProfile = new MutableLiveData<>();
    void setOpenProfile(String profile){
        openProfile.setValue(profile);
    }
    public LiveData<String>getOpenProfile(){
        return openProfile;
    }

    public LiveData<ArrayList<LiveItem>>getViewUpdate(){
        return onViewUpdate;
    }

    public LiveData<Integer>getLiveViewerCount(){
        return getLiveViewerCount;
    }
    String myJid;
    ArrayList<String>users;
    boolean isOptionsOpen = false;


    public LiveData<LiveAction>getLiveAction(){
        return liveActiondata;
    }
    public LiveData<Boolean>getOptionsMenuVisibility(){
        return optionsMenu;
    }
    public LiveData<HashMap<String,String>>getOnSetUpComplete(){
        return setUpComplete;
    }



    public void onClickSend(View vi){
        int id = vi.getId();
        if(id == R.id.send){
            onSendButtonClick.setValue(SUBJECT_TYPE_COMMENT);
        }else if(id == R.id.closeLive){
            onSendButtonClick.setValue(CLOSE_LIVE);
        }else if(id == R.id.profile || id == R.id.name || id == R.id.vip){
            switch (action){
                case LIVE_USER_TYPE_AUDIENCE:
                case LIVE_USER_TYPE_COMPETITOR: {
                    setOpenProfile(data.get(ROOM_ID));
                    break;
                }
            }
        }else if(id == R.id.options){
            isOptionsOpen = !isOptionsOpen;
            optionsMenu.setValue(isOptionsOpen);
        }else if(id == R.id.option1){
            if(!isOccupied){
                onSendButtonClick.setValue(JOIN_REQUEST);
            }
        }else if(id == R.id.option2){
            onSendButtonClick.setValue(SWITCH_CAMERA);
        }else if(id == R.id.option3){
            onSendButtonClick.setValue(HIDE_COMMENT);
        }else if(id == R.id.option4){
            onSendButtonClick.setValue(GIFT);
        }else if(id == R.id.addPeople){
            onSendButtonClick.setValue(ADD_PERSON);
        }else if(id == R.id.profile1 || id == R.id.name1 || id == R.id.vip1){
            onSendButtonClick.setValue(OPEN_PROFILE_1);
        }else if(id ==  R.id.profile2 ||id == R.id.name2 ||id == R.id.vip2){
            onSendButtonClick.setValue(OPEN_PROFILE_2);
        }else if(id == R.id.endCall1){
            onSendButtonClick.setValue(END_CALL_1);
        }else if(id == R.id.endCall2){
            onSendButtonClick.setValue(END_CALL_2);
        }
    }

    public LiveData<String> getSendComment(){
        return onSendButtonClick;
    }
    public void sendComment(String comment) {
        CommentModel cm = new CommentModel();
        cm.setComment(comment);
        cm.setName(CM.getProfile().getContent().get("name"));
        cm.setPp(CM.getProfile().getContent().get("profile_picture"));
        cm.setVip(CM.getProfile().getContent().get("vip"));
        cm.setId(myJid);
        cm.setContent(new HashMap<>());
        Log.e("action",action);
        if(action.equals(LIVE_USER_TYPE_HOST)){
            adapter.addData(cm);
            broadCastEvent(cm,SUBJECT_TYPE_COMMENT);
            broadCastToCompetitor(cm,SUBJECT_TYPE_COMMENT);
        }else {
            CM.sendHLM(SUBJECT_TYPE_COMMENT,new Gson().toJson(cm),myJid,getData().get("room_id"));
        }

    }
    public void broadCastEvent(Object cm,String type){
        for(String s:viewers){
            CM.sendHLM(type,new Gson().toJson(cm),myJid,s);
        }
    }


    HeadlineMessageListener listener = stanza -> {
        Message message = (Message) stanza;
        switch (message.getSubject()){
            case SUBJECT_TYPE_VIDEO_INVITATION_ACCEPTED:{
//                for Host only
                isOccupied = true;
                break;
            } case SUBJECT_TYPE_VIDEO_STREAM_JOINED:{
//                for Host only

                LiveItem liveItem = new Gson().fromJson(message.getBody(),LiveItem.class);
                competitorList.add(liveItem);
                broadCastEvent(competitorList,SUBJECT_TYPE_COMPETITOR_LIST);
                broadCastToCompetitor(competitorList,SUBJECT_TYPE_COMPETITOR_LIST);
                onViewUpdate.postValue(competitorList);
                break;
            }
            case SUBJECT_TYPE_JOINED_LIVE:{
//                for Host only
                    viewerJoined(message);
                break;
            } case SUBJECT_TYPE_COMMENT:{

//                for All
                CommentModel cm = new Gson().fromJson(message.getBody(),CommentModel.class);
                adapter.addData(cm);
                if(action.equals(LIVE_USER_TYPE_HOST)){
                    broadCastEvent(cm,SUBJECT_TYPE_COMMENT);
                    broadCastToCompetitor(cm,SUBJECT_TYPE_COMMENT);
                }
                break;
            } case SUBJECT_TYPE_LIVE_ACTION:{

//                for all
                LiveAction liveAction = new Gson().fromJson(message.getBody(),LiveAction.class);
                liveActiondata.postValue(liveAction);
                break;
            }case SUBJECT_TYPE_VIEWERS_LIST:{

//                for competitor and audience
                Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                viewers = new ArrayList<>(new Gson().fromJson(message.getBody(), listType));
                getLiveViewerCount.postValue(viewers.size());
                break;
            }case SUBJECT_TYPE_COMPETITOR_LIST:{

//                for audience and competitor
                Type listType = new TypeToken<ArrayList<LiveItem>>(){}.getType();
                competitorList = new ArrayList<>(new Gson().fromJson(message.getBody(),listType));
                setUpForCompetitorforHost();
                break;
            }
            case SUBJECT_TYPE_COMPETITOR_LEFT:{
                ProfileItem profileItem = new Gson().fromJson(message.getBody(),ProfileItem.class);
                removeFromCompetitorList(profileItem);

                if(action.equals(LIVE_USER_TYPE_HOST)){
                    broadCastEvent(profileItem,SUBJECT_TYPE_COMPETITOR_LEFT);
                    broadCastToCompetitor(profileItem,SUBJECT_TYPE_COMPETITOR_LEFT);
                    if(competitorList.isEmpty()){
                        isOccupied = false;
                    }
                }
                onViewUpdate.postValue(competitorList);
            }
        }
    };

    void removeFromCompetitorList(ProfileItem profileItem){
        for(LiveItem liveItem:competitorList){
            if(Objects.requireNonNull(liveItem.getContent().get(NAME)).equals(profileItem.getContent().get(ProfileItem.NAME))){
                competitorList.remove(liveItem);
                break;
            }
        }
    }

    void setUpForCompetitorforHost(){
        onViewUpdate.postValue(competitorList);
    }


    void broadCastToCompetitor(Object o,String type){
        if(isOccupied){
            for(LiveItem liveItem: competitorList){
                CM.sendHLM(type,new Gson().toJson(o),CM.getConnection().getUser().asFullJidOrThrow().toString(),liveItem.getContent().get(ROOM_ID));
            }
        }
    }

    void viewerJoined(Message message){
        viewers.add(message.getFrom().asFullJidOrThrow().toString());
        if(isOccupied){
            CM.sendHLM(SUBJECT_TYPE_COMPETITOR_LIST,new Gson().toJson(competitorList), CM.getConnection().getUser().asFullJidOrThrow().toString(),message.getFrom().asFullJidOrThrow().toString());
        }
        getLiveViewerCount.postValue(viewers.size());
        broadCastEvent(viewers,SUBJECT_TYPE_VIEWERS_LIST);
        broadCastToCompetitor(viewers,SUBJECT_TYPE_VIEWERS_LIST);
        updateLiveItem();
    }

    public void viewerLeft(String s){
        viewers.remove(s);
        getLiveViewerCount.postValue(viewers.size());
        broadCastEvent(viewers,SUBJECT_TYPE_VIEWERS_LIST);
        broadCastToCompetitor(viewers,SUBJECT_TYPE_VIEWERS_LIST);
        updateLiveItem();
    }

    void updateLiveItem(){
        try {
            if(!live.isEmpty()){
                live.put(VIEWERS,String.valueOf(viewers.size()));
            }else {
                live.put(PROFILE_IMAGE,CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));
                live.put(NAME,CM.getProfile().getContent().get(ProfileItem.NAME));
                live.put(ROOM_ID,CM.getConnection().getUser().asFullJidOrThrow().toString());
                live.put(SDP,"");
                live.put(TYPE,LIVE_TYPE_VIDEO);
                live.put(VIP,CM.getProfile().getContent().get("vip"));
                live.put(COUNTRY,CM.getIPModel().getCountry());
                live.put(IP,CM.getIPModel().getQuery());
                live.put(COUNTRY_CODE,CM.getIPModel().getCountryCode());
                live.put(CITY,CM.getIPModel().getCity());
                live.put(TIME_ZONE,CM.getIPModel().getTimezone());
                live.put(REGION_NAME,CM.getIPModel().getRegionName());

                ZonedDateTime currentTime = ZonedDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'");
                String currentTimeString = currentTime.format(formatter);

                live.put(STARTED_AT, currentTimeString);
                live.put(VIEWERS,"0");
            }

            LiveItem liveItem = new LiveItem(live);
            Item item = Functions.createRawItem(liveItem);
            Functions.publishToNode(CM.NODE_LIVE_USERS,item,item.getId());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }



    @SuppressWarnings("unchecked")
    public void initViewModel(Context context, Intent i, FrameLayout surface,FrameLayout surface2) throws JSONException {
        this.action = i.getStringExtra(ACTION);

        users = new ArrayList<>();
        viewers = new ArrayList<>();
        myJid = CM.getConnection().getUser().asFullJidOrThrow().toString();

        switch (action) {
            case LIVE_USER_TYPE_HOST: {
                adapter = new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_HOST);
                setUpForHost(context,surface);
                setUpComplete.setValue(new HashMap<>());
                break;
            }
            case LIVE_USER_TYPE_AUDIENCE: {
                adapter = new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_AUDIENCE);
                this.data = (HashMap<String, String>) i.getSerializableExtra("data");
                setUpForAudience(context,surface);
                setUpComplete.setValue(data);
                break;
            }
            case LIVE_USER_TYPE_COMPETITOR: {
                adapter = new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_COMPETITOR);
                ProfileItem profileItem = new Gson().fromJson(i.getStringExtra(DATA),ProfileItem.class);
                this.data = LiveItem.parseLiveItem(Functions.getSingleItemOfNode(CM.NODE_LIVE_USERS,profileItem.getContent().get(ProfileItem.NAME))).getContent();
                setUpForCompetitor(context,surface,surface2);
                setUpComplete.setValue(data);
                break;
            }

        }
        CM.setHeadlineMessageListener(listener);
        adapter.setOnItemClickListener(this::setSelectedItem);



    }

    public void switchCamera(){
        nodePublisher.switchCamera();
    }


    public void inviteFriend(FullJid id){
        CM.sendHLM(SUBJECT_TYPE_VIDEO_INVITATION,new Gson().toJson(CM.getProfile()),CM.getConnection().getUser().asFullJidOrThrow().toString(),id.toString());
    }


    void setUpForHost(Context context,FrameLayout vi) {

        int width= context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        nodePublisher = new NodePublisher(context, "");
        nodePublisher.setAudioCodecParam(NodePublisher.NMC_CODEC_ID_AAC, NodePublisher.NMC_PROFILE_AUTO, 48000, 2, 64_000);
        nodePublisher.setVideoOrientation(NodePublisher.VIDEO_ORIENTATION_PORTRAIT);
        nodePublisher.setVideoCodecParam(NodePublisher.NMC_CODEC_ID_H264, NodePublisher.NMC_PROFILE_AUTO, width, height, 30, 1_000_000);
        nodePublisher.attachView(vi);
        //not working in nokia 3
        nodePublisher.setHWAccelEnable(true);
        nodePublisher.setCameraFrontMirror(true);
        nodePublisher.setLinearZoom(0.0f);
        nodePublisher.setVolume(100.0f);
        nodePublisher.openCamera(true);
        nodePublisher.start(Configurations.RTMP_URL+CM.getProfile().getName());
        Log.e("streamingat",Configurations.RTMP_URL+CM.getProfile().getName());
        updateLiveItem();
    }

    public void setSelectedItem(CommentModel item) {
        selectedItem.setValue(item);
    }
    public LiveData<CommentModel> getSelectedItem(){
        return selectedItem;
    }

    public CommentAdapter getAdapter(){
        return adapter;
    }

    public String getAction(){
        return action;
    }

    public void setAdapter(CommentAdapter adapter){
        this.adapter = adapter;
    }


    void setUpForAudience(Context context,FrameLayout surface) {
        String receiver = getData().get("room_id");
        nodePlayer = new NodePlayer(context,"");
        nodePlayer.attachView(surface);
        nodePlayer.start(Configurations.RTMP_URL+getData().get("name"));
        CM.sendHLM(SUBJECT_TYPE_JOINED_LIVE,new Gson().toJson(CM.getProfile()),myJid,receiver);
        sendComment(INITIAL_COMMENT);

    }

    LiveItem getLiveItemForCompetitor(){
        HashMap<String,String> live = new HashMap<>();
        live.put(PROFILE_IMAGE,CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));
        live.put(NAME,CM.getProfile().getContent().get(ProfileItem.NAME));
        live.put(ROOM_ID,CM.getConnection().getUser().asFullJidOrThrow().toString());
        live.put(SDP,"");
        live.put(TYPE,LIVE_TYPE_VIDEO);
        live.put(VIP,CM.getProfile().getContent().get("vip"));
        live.put(COUNTRY,CM.getIPModel().getCountry());
        live.put(IP,CM.getIPModel().getQuery());
        live.put(COUNTRY_CODE,CM.getIPModel().getCountryCode());
        live.put(CITY,CM.getIPModel().getCity());
        live.put(TIME_ZONE,CM.getIPModel().getTimezone());
        live.put(REGION_NAME,CM.getIPModel().getRegionName());
        ZonedDateTime currentTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'");
        String currentTimeString = currentTime.format(formatter);
        live.put(STARTED_AT, currentTimeString);
        live.put(VIEWERS,"0");
        return new LiveItem(live);
    }
    void setUpForCompetitor(Context context,FrameLayout surface1,FrameLayout surface2) {
        LiveItem liveItem = getLiveItemForCompetitor();
        int width= context.getResources().getDisplayMetrics().widthPixels;
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
        nodePublisher.start(Configurations.RTMP_URL+CM.getProfile().getName());

        nodePlayer = new NodePlayer(context,"");
        nodePlayer.attachView(surface1);
        nodePlayer.start(Configurations.RTMP_URL+data.get(NAME));

        CM.sendHLM(SUBJECT_TYPE_VIDEO_STREAM_JOINED,new Gson().toJson(liveItem), CM.getConnection().getUser().asFullJidOrThrow().toString(),data.get(ROOM_ID));

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
        isOccupied = false;
        if(action.equals(LIVE_USER_TYPE_HOST)){
            nodePublisher.closeCamera();
//            nodePublisher.detachView();
            nodePublisher.stop();
            LiveAction liveAction = new LiveAction();
            liveAction.setActionType(ACTION_TYPE_LIVE_ENDED);
            liveAction.setActionContent(ACTION_TYPE_LIVE_ENDED);
            liveAction.setContentMap(new HashMap<>());
            for(String s:viewers){
                CM.sendHLM(SUBJECT_TYPE_LIVE_ACTION,new Gson().toJson(liveAction),myJid,s);
            }
            Functions.deleteFromNode(CM.NODE_LIVE_USERS,CM.getProfile().getContent().get("name"));

        }else if(action.equals(LIVE_USER_TYPE_AUDIENCE)){
            LiveAction liveAction = new LiveAction();
            liveAction.setActionType(ACTION_TYPE_LIVE_LEFT);
            liveAction.setActionContent(ACTION_TYPE_LIVE_LEFT);
            HashMap<String,String> map = new HashMap<>();
            map.put("name",CM.getProfile().getName());
            map.put("jid",CM.getConnection().getUser().asFullJidOrThrow().toString());
            liveAction.setContentMap(map);
            nodePlayer.detachView();
            nodePlayer.stop();
            CM.sendHLM(SUBJECT_TYPE_LIVE_ACTION,new Gson().toJson(liveAction),myJid,data.get("room_id"));
        }else {
            nodePublisher.closeCamera();
            nodePublisher.stop();
            nodePlayer.stop();
            CM.sendHLM(SUBJECT_TYPE_COMPETITOR_LEFT,new Gson().toJson(CM.getProfile()),CM.getConnection().getUser().asFullJidOrThrow().toString(),data.get(ROOM_ID));
        }
        CM.removeListener(listener);
        if(nodePlayer!=null){
            nodePlayer.stop();
        }
        if(nodePublisher!=null){
            nodePublisher.stop();
        }

    }
}

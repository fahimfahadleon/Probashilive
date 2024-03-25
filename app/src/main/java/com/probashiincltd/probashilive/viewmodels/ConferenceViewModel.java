package com.probashiincltd.probashilive.viewmodels;

import static com.probashiincltd.probashilive.pubsubItems.LiveItem.*;


import static com.probashiincltd.probashilive.utils.Configurations.*;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.CommentAdapter;
import com.probashiincltd.probashilive.adapter.ConferenceAdapter;
import com.probashiincltd.probashilive.callbacks.HeadlineMessageListener;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.CommentModel;
import com.probashiincltd.probashilive.models.ConferenceModel;
import com.probashiincltd.probashilive.models.LiveAction;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.pubsub.Item;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePublisher;

public class ConferenceViewModel extends ViewModel {
    private ConferenceAdapter conferenceAdapter;
    private CommentAdapter commentAdapter;
    String action;
    ArrayList<String>users;
    ArrayList<String>viewers;
    String myJid;
    HashMap<String,String>data;
    NodePlayer nodePlayer;
    NodePublisher nodePublisher;

    public HashMap<String,String>getData(){
        return data;
    }

    HeadlineMessageListener listener = new HeadlineMessageListener() {
        @Override
        public void onHeadlineMessage(Stanza stanza) {
            Message message = (Message)stanza;
            switch (message.getSubject()){
                case SUBJECT_TYPE_JOINED_LIVE:{
                    viewers.add(message.getFrom().asFullJidOrThrow().toString());
                    break;
                } case SUBJECT_TYPE_COMMENT:{
                    CommentModel cm = new Gson().fromJson(message.getBody(),CommentModel.class);
                    commentAdapter.addData(cm);
                    if(action.equals(LIVE_USER_TYPE_HOST)){
                        broadCastComment(cm);
                    }

                    break;
                } case SUBJECT_TYPE_LIVE_ACTION:{
                    LiveAction liveAction = new Gson().fromJson(message.getBody(),LiveAction.class);
                    liveActiondata.postValue(liveAction);
                    break;
                }
            }
        }
    };

    private final MutableLiveData<ConferenceModel> conferenceModelItemClick = new MutableLiveData<>();
    private final MutableLiveData<String> onSendButtonClick = new MutableLiveData<>();
    private final MutableLiveData<LiveAction> liveActiondata = new MutableLiveData<>();
    private final MutableLiveData<CommentModel> commentModelItemClick = new MutableLiveData<>();
    private final MutableLiveData<CommentModel> onCommentInserted = new MutableLiveData<>();
    public LiveData<CommentModel>getOnCommentInserted(){
        return onCommentInserted;
    }
    public ConferenceAdapter getConferenceAdapter(){
        return conferenceAdapter;
    }

    public LiveData<LiveAction>getLiveAction(){
        return liveActiondata;
    }


    public CommentAdapter getCommentAdapter(){
        return commentAdapter;
    }

    public LiveData<ConferenceModel> getConferenceAdapterlItemClick(){
        return conferenceModelItemClick;
    }
    public LiveData<CommentModel> getCommentAdapterItemClick(){
        return commentModelItemClick;
    }

    public void onClickSend(View vi){
        int id = vi.getId();
        if(id == R.id.send){
            onSendButtonClick.setValue(SUBJECT_TYPE_COMMENT);
        }
    }

    public LiveData<String>getSendComment(){
        return onSendButtonClick;
    }



    public void initViewModel(Context context, Intent i){
        action = i.getStringExtra(ACTION);
        users = new ArrayList<>();
        viewers = new ArrayList<>();
        myJid = CM.getConnection().getUser().asFullJidOrThrow().toString();

        switch (action) {
            case LIVE_USER_TYPE_HOST: {
                commentAdapter = new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_HOST);
                conferenceAdapter = new ConferenceAdapter(ConferenceAdapter.CONFERENCE_ADAPTER_TYPE_HOST);
                setUpForHost(context);
                break;
            }
            case LIVE_USER_TYPE_AUDIENCE: {
                commentAdapter = new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_AUDIENCE);
                conferenceAdapter = new ConferenceAdapter(ConferenceAdapter.CONFERENCE_ADAPTER_TYPE_AUDIENCE);
                this.data = (HashMap<String, String>) i.getSerializableExtra("data");
                setUpForAudience(context);
                break;
            }
            case LIVE_USER_TYPE_COMPETITOR: {
                commentAdapter = new CommentAdapter(CommentAdapter.COMMENT_ADAPTER_TYPE_COMPETITOR);
                conferenceAdapter = new ConferenceAdapter(ConferenceAdapter.CONFERENCE_ADAPTER_TYPE_COMPETITOR);
                this.data = (HashMap<String, String>) i.getSerializableExtra("data");
                setUpForCompetitor(context);
                break;
            }

        }

        CM.setHeadlineMessageListener(listener);
        conferenceAdapter.setOnConferenceClickListener(conferenceModelItemClick::setValue);
        commentAdapter.setOnItemClickListener(commentModelItemClick::setValue);
        commentAdapter.setOnItemInsertedListener(this::onCommentInserted);
    }

    private void onCommentInserted(CommentModel commentModel) {
        onCommentInserted.postValue(commentModel);
    }

    private void setUpForHost(Context context) {
        nodePublisher = new NodePublisher(context, "");
        nodePublisher.setAudioCodecParam(NodePublisher.NMC_CODEC_ID_AAC, NodePublisher.NMC_PROFILE_AUTO, 48000, 2, 64_000);
        nodePublisher.setVolume(100.0f);
        nodePublisher.start(Configurations.RTMP_URL+CM.getProfile().getName());
        Log.e("streamingat",Configurations.RTMP_URL+CM.getProfile().getName());
        try {
            HashMap<String,String>live = new HashMap<>();
            live.put(PROFILE_IMAGE,CM.getProfile().getContent().get(ProfileItem.PROFILE_PICTURE));
            live.put(NAME,CM.getProfile().getContent().get(ProfileItem.NAME));
            live.put(ROOM_ID,CM.getConnection().getUser().asFullJidOrThrow().toString());
            live.put(VIEWERS,"0");
            live.put(STARTED_AT, ZonedDateTime.now().toString());
            live.put(TYPE,LIVE_TYPE_AUDIO);
            live.put(SDP,"");
            live.put(VIP,CM.getProfile().getContent().get(ProfileItem.VIP));
            live.put(COUNTRY,CM.getIPModel().getCountry());
            live.put(IP,CM.getIPModel().getQuery());
            live.put(COUNTRY_CODE,CM.getIPModel().getCountryCode());
            live.put(CITY,CM.getIPModel().getCity());
            live.put(TIME_ZONE,CM.getIPModel().getTimezone());
            live.put(REGION_NAME,CM.getIPModel().getRegionName());

            LiveItem liveItem = new LiveItem(live);
            Item item = Functions.createRawItem(liveItem);
            Functions.publishToNode(CM.NODE_LIVE_USERS,item,item.getId());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    private void setUpForAudience(Context context) {
        String receiver = getData().get("room_id");
        if(!viewers.contains(receiver)){
            viewers.add(receiver);
        }
        nodePlayer = new NodePlayer(context,"");
        nodePlayer.start(Configurations.RTMP_URL+getData().get("name"));
        CM.sendHLM(SUBJECT_TYPE_JOINED_LIVE,"",myJid,receiver);
        sendComment(INITIAL_COMMENT);
    }

    private void setUpForCompetitor(Context context) {

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
            commentAdapter.addData(cm);
            broadCastComment(cm);
        }else {
            CM.sendHLM(SUBJECT_TYPE_COMMENT,new Gson().toJson(cm),myJid,getData().get("room_id"));
        }

    }

    public void broadCastComment(CommentModel cm){
        for(String s:viewers){
            CM.sendHLM(SUBJECT_TYPE_COMMENT,new Gson().toJson(cm),myJid,s);
        }
    }

    public void onDestroy() {
        switch (action) {
            case LIVE_USER_TYPE_HOST: {
                nodePublisher.stop();

                LiveAction liveAction = new LiveAction();
                liveAction.setActionType(ACTION_TYPE_LIVE_ENDED);
                liveAction.setActionContent(ACTION_TYPE_LIVE_ENDED);
                liveAction.setContentMap(new HashMap<>());
                for (String s : viewers) {
                    CM.sendHLM(SUBJECT_TYPE_LIVE_ACTION, new Gson().toJson(liveAction), myJid, s);
                }
                Functions.deleteFromNode(CM.NODE_LIVE_USERS, CM.getProfile().getContent().get("name"));
                CM.removeListener(listener);
                break;
            }
            case LIVE_USER_TYPE_AUDIENCE: {
                LiveAction liveAction = new LiveAction();
                liveAction.setActionType(ACTION_TYPE_LIVE_LEFT);
                liveAction.setActionContent(ACTION_TYPE_LIVE_LEFT);
                HashMap<String, String> map = new HashMap<>();
                map.put("name", CM.getProfile().getName());
                map.put("jid", CM.getConnection().getUser().asFullJidOrThrow().toString());
                liveAction.setContentMap(map);
                nodePlayer.stop();
                for (String s : viewers) {
                    CM.sendHLM(SUBJECT_TYPE_LIVE_ACTION, new Gson().toJson(liveAction), myJid, s);
                }
                CM.removeListener(listener);
                break;
            }
            case LIVE_USER_TYPE_COMPETITOR:
                //todo do for competitor
                break;
        }

    }


    public String getAction() {
        return action;
    }
}

package com.probashiincltd.probashilive.viewmodels;

import static com.probashiincltd.probashilive.utils.Configurations.ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_ENDED;
import static com.probashiincltd.probashilive.utils.Configurations.ACTION_TYPE_LIVE_LEFT;
import static com.probashiincltd.probashilive.utils.Configurations.CLOSE_LIVE;
import static com.probashiincltd.probashilive.utils.Configurations.INITIAL_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_VIDEO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_AUDIENCE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.OPEN_PROFILE;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_JOINED_LIVE;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_LIVE_ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIEWERS_LIST;

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
import com.probashiincltd.probashilive.utils.Configurations;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.pubsub.Item;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

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

    public int getViewersCount(){
        return viewers.size();
    }



    private final MutableLiveData<String> onSendButtonClick = new MutableLiveData<>();
    CommentAdapter adapter;
    private final MutableLiveData<CommentModel> selectedItem = new MutableLiveData<>();
    private final MutableLiveData<LiveAction> liveActiondata = new MutableLiveData<>();
    private final MutableLiveData<Integer> getLiveViewerCount = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String,String>> setUpComplete = new MutableLiveData<>();

    public LiveData<Integer>getLiveViewerCount(){
        return getLiveViewerCount;
    }
    String myJid;
    ArrayList<String>users;

    public LiveData<LiveAction>getLiveAction(){
        return liveActiondata;
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
            onSendButtonClick.setValue(OPEN_PROFILE);
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
            case SUBJECT_TYPE_JOINED_LIVE:{
                    viewerJoined(message);
                break;
            } case SUBJECT_TYPE_COMMENT:{
                CommentModel cm = new Gson().fromJson(message.getBody(),CommentModel.class);
                adapter.addData(cm);
                if(action.equals(LIVE_USER_TYPE_HOST)){
                    broadCastEvent(cm,SUBJECT_TYPE_COMMENT);
                }

                break;
            } case SUBJECT_TYPE_LIVE_ACTION:{
                LiveAction liveAction = new Gson().fromJson(message.getBody(),LiveAction.class);
                liveActiondata.postValue(liveAction);
                break;
            }case SUBJECT_TYPE_VIEWERS_LIST:{
                Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                viewers = new ArrayList<>(new Gson().fromJson(message.getBody(), listType));
                getLiveViewerCount.postValue(viewers.size());
                break;
            }
        }
    };


    void viewerJoined(Message message){
        viewers.add(message.getFrom().asFullJidOrThrow().toString());
        getLiveViewerCount.postValue(viewers.size());
        broadCastEvent(viewers,SUBJECT_TYPE_VIEWERS_LIST);
        updateLiveItem();
    }

    public void viewerLeft(String s){
        viewers.remove(s);
        getLiveViewerCount.postValue(viewers.size());
        broadCastEvent(viewers,SUBJECT_TYPE_VIEWERS_LIST);
        updateLiveItem();
    }

    void updateLiveItem(){
        try {
            if(!live.isEmpty()){
                live.put("viewers",String.valueOf(viewers.size()));
            }else {
                live.put("profile_image",CM.getProfile().getContent().get("profile_picture"));
                live.put("name",CM.getProfile().getContent().get("name"));
                live.put("room_id",CM.getConnection().getUser().asFullJidOrThrow().toString());
                live.put("sdp","");
                live.put("type",LIVE_TYPE_VIDEO);
                live.put("vip",CM.getProfile().getContent().get("vip"));

                ZonedDateTime currentTime = ZonedDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'");
                String currentTimeString = currentTime.format(formatter);

                live.put("startedAt", currentTimeString);
                live.put("viewers","0");
            }

            LiveItem liveItem = new LiveItem(live);
            Item item = Functions.createRawItem(liveItem);
            Functions.publishToNode(CM.NODE_LIVE_USERS,item,item.getId());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("unchecked")
    public void initViewModel(Context context, Intent i, FrameLayout surface){
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
                this.data = (HashMap<String, String>) i.getSerializableExtra("data");
                setUpForCompetitor();
                setUpComplete.setValue(data);
                break;
            }

        }
        CM.setHeadlineMessageListener(listener);
        adapter.setOnItemClickListener(this::setSelectedItem);



    }



    void setUpForHost(Context context,FrameLayout vi) {

        int width= context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;

        nodePublisher = new NodePublisher(context, "");
        nodePublisher.setAudioCodecParam(NodePublisher.NMC_CODEC_ID_AAC, NodePublisher.NMC_PROFILE_AUTO, 48000, 2, 64_000);
        nodePublisher.setVideoOrientation(NodePublisher.VIDEO_ORIENTATION_PORTRAIT);
        nodePublisher.setVideoCodecParam(NodePublisher.NMC_CODEC_ID_H264, NodePublisher.NMC_PROFILE_AUTO, width, height, 30, 1_000_000);
        nodePublisher.attachView(vi);
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

    public void setAdapter(CommentAdapter adapter){
        this.adapter = adapter;
    }


    void setUpForAudience(Context context,FrameLayout surface) {
        String receiver = getData().get("room_id");
        nodePlayer = new NodePlayer(context,"");
        nodePlayer.attachView(surface);
        nodePlayer.start(Configurations.RTMP_URL+getData().get("name"));
        CM.sendHLM(SUBJECT_TYPE_JOINED_LIVE,"",myJid,receiver);
        sendComment(INITIAL_COMMENT);

    }
    void setUpForCompetitor() {

    }
    public HashMap<String, String> getData() {
        if (data == null) {
            throw new RuntimeException("data not exists");
        }
        return data;
    }

    public void onDestroy() {
        if(action.equals(LIVE_USER_TYPE_HOST)){
            nodePublisher.closeCamera();
            nodePublisher.stop();
            LiveAction liveAction = new LiveAction();
            liveAction.setActionType(ACTION_TYPE_LIVE_ENDED);
            liveAction.setActionContent(ACTION_TYPE_LIVE_ENDED);
            liveAction.setContentMap(new HashMap<>());
            for(String s:viewers){
                CM.sendHLM(SUBJECT_TYPE_LIVE_ACTION,new Gson().toJson(liveAction),myJid,s);
            }
            Functions.deleteFromNode(CM.NODE_LIVE_USERS,CM.getProfile().getContent().get("name"));
            CM.removeListener(listener);
        }else {
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
            CM.removeListener(listener);
        }

    }
}

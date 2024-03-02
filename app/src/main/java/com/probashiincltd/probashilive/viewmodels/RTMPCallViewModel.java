package com.probashiincltd.probashilive.viewmodels;

import static com.probashiincltd.probashilive.utils.Configurations.ACTION;
import static com.probashiincltd.probashilive.utils.Configurations.INITIAL_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_TYPE_VIDEO;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_AUDIENCE;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_COMPETITOR;
import static com.probashiincltd.probashilive.utils.Configurations.LIVE_USER_TYPE_HOST;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_COMMENT;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_JOINED_LIVE;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.adapter.CommentAdapter;
import com.probashiincltd.probashilive.callbacks.HeadlineMessageListener;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.CommentModel;
import com.probashiincltd.probashilive.pubsubItems.LiveItem;
import com.probashiincltd.probashilive.utils.Configurations;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.pubsub.Item;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePublisher;

public class RTMPCallViewModel extends ViewModel {
    NodePublisher nodePublisher;
    NodePlayer nodePlayer;
    String action;
    HashMap<String,String>data;
    ArrayList<String>viewers;

    private final MutableLiveData<String> onSendButtonClick = new MutableLiveData<>();
    CommentAdapter adapter;
    private final MutableLiveData<CommentModel> selectedItem = new MutableLiveData<>();
    String myJid;
    ArrayList<String>users;

    public void onClickSend(View vi){
        int id = vi.getId();
        if(id == R.id.send){
            onSendButtonClick.setValue(SUBJECT_TYPE_COMMENT);
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
        adapter.addData(cm);
        for(String s:users){
            CM.sendHLM(SUBJECT_TYPE_COMMENT,new Gson().toJson(cm),myJid,s);
        }
    }

    HeadlineMessageListener headlineMessageListener = stanza -> {
        Message message = (Message) stanza;
        switch (message.getSubject()){
            case SUBJECT_TYPE_JOINED_LIVE:{
                viewers.add(message.getFrom().asFullJidOrThrow().toString());
                break;
            }
            case SUBJECT_TYPE_COMMENT:{
                CommentModel cm = new Gson().fromJson(message.getBody(),CommentModel.class);
                adapter.addData(cm);
            }
        }
    };






    public void initViewModel(Context context, Intent i, FrameLayout surface){
        this.action = i.getStringExtra(ACTION);
        users = new ArrayList<>();
        viewers = new ArrayList<>();
        myJid = CM.getConnection().getUser().asFullJidOrThrow().toString();

        switch (action) {
            case LIVE_USER_TYPE_HOST: {

                setUpForHost(context,surface);
                break;
            }
            case LIVE_USER_TYPE_AUDIENCE: {
                this.data = (HashMap<String, String>) i.getSerializableExtra("data");
                setUpForAudience(context,surface);
                break;
            }
            case LIVE_USER_TYPE_COMPETITOR: {
                this.data = (HashMap<String, String>) i.getSerializableExtra("data");
                setUpForCompetitor();
                break;
            }

        }
        CM.setHeadlineMessageListener(headlineMessageListener);
        adapter.setOnItemClickListener(this::setSelectedItem);



    }
    void setUpForHost(Context context,FrameLayout vi) {

        int width= context.getResources().getDisplayMetrics().widthPixels;

        nodePublisher = new NodePublisher(context, "");
        nodePublisher.setAudioCodecParam(NodePublisher.NMC_CODEC_ID_AAC, NodePublisher.NMC_PROFILE_AUTO, 48000, 1, 64_000);
        nodePublisher.setVideoOrientation(NodePublisher.VIDEO_ORIENTATION_PORTRAIT);
        nodePublisher.setVideoCodecParam(NodePublisher.NMC_CODEC_ID_H264, NodePublisher.NMC_PROFILE_AUTO, width, width, 30, 1_000_000);
        nodePublisher.attachView(vi);
        nodePublisher.setCameraFrontMirror(true);
        nodePublisher.setLinearZoom(0.0f);

        nodePublisher.openCamera(true);
        nodePublisher.start(Configurations.RTMP_URL+CM.getProfile().getName());

        try {

            HashMap<String,String>live = new HashMap<>();
            live.put("profile_image",CM.getProfile().getContent().get("profile_picture"));
            live.put("name",CM.getProfile().getContent().get("name"));
            live.put("room_id",CM.getConnection().getUser().asFullJidOrThrow().toString());
            live.put("viewers","0");
            live.put("startedAt",ZonedDateTime.now().toString());
            live.put("type",LIVE_TYPE_VIDEO);
            live.put("sdp","");

            LiveItem liveItem = new LiveItem(live);
            Item item = Functions.createRawItem(liveItem);
            Functions.publishToNode(CM.NODE_LIVE_USERS,item,item.getId());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
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
        if(!users.contains(receiver)){
            users.add(receiver);
        }
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
            nodePublisher.detachView();
            nodePublisher.closeCamera();
            nodePublisher.stop();
        }else {
            nodePlayer.detachView();
            nodePlayer.stop();
        }

    }
}

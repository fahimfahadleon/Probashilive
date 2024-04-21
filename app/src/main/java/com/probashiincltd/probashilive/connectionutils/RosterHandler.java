package com.probashiincltd.probashilive.connectionutils;


import android.content.Context;
import android.util.Log;

import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;
import com.probashiincltd.probashilive.utils.CustomRosterStore;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jivesoftware.smackx.pubsub.Item;
import org.json.JSONException;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class RosterHandler implements SubscribeListener, PresenceEventListener, RosterListener {
    public Roster roster;
    public CustomRosterStore rosterStore;
    boolean isStoreLoaded = false;
    boolean isRosterLoaded = false;
    static RosterHandler rosterHandler;
    ArrayList<ProfileItem> onlineFriends;
    public static final int TYPE_NO_FRIEND = 0;
    public static final int TYPE_FOLLOWING = 1;
    public static final int TYPE_FOLLOWER = 2;
    public static RosterHandler getRosterHandler(){
        return rosterHandler;
    }
    public ArrayList<ProfileItem>getOnlineFriends(){
        return onlineFriends;
    }

    public void createEntry(String id,String name){
        try {
            roster.createItemAndRequestSubscription(JidCreate.bareFrom(id),name,new String[]{CM.getProfile().getName()});
        }catch (Exception e){
            //ignored
        }
    }
    public void removeEntry(String id){
        try {
            roster.removeEntry(RosterHandler.getRosterHandler().roster.getEntry(JidCreate.bareFrom(id)));
        }catch (Exception e){
            //ignored
        }
    }
//    public static void removeEntry(String name){
//        getRosterHandler().removeEntry(profileItem.getContent().get(ProfileItem.NAME)+ "@" + Configurations.getHostName());
//    }
    public void addGroup(String id){
        try {
            RosterGroup rosterGroup = roster.createGroup(CM.getProfile().getName());
            if(rosterGroup == null){
                rosterGroup = roster.getGroup(CM.getProfile().getName());
            }
            rosterGroup.addEntry(roster.getEntry(JidCreate.bareFrom(id)));
        }catch (Exception e){
            //ignored
        }

    }

    void getInitialOnlineFriends(){
        ArrayList<String>onlineProfile = new ArrayList<>();
        for(RosterEntry entry:roster.getEntries()){
            Presence presence = roster.getPresence(entry.getJid());
            if (presence.isAvailable()) {
                onlineProfile.add(entry.getJid().toString().split("@")[0]);
            }
        }


        if(onlineProfile.isEmpty()){
            return;
        }
        List<Item> items = Functions.getMultipleItemOfNode(CM.NODE_USERS,onlineProfile);
        for(Item i: items){
            try {
                ProfileItem profileItem = ProfileItem.parseProfileItem(i);
                onlineFriends.add(profileItem);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

    }
    public RosterHandler(Context context) {
        rosterHandler = this;
        onlineFriends = new ArrayList<>();
        File versionFile = new File(context.getCacheDir() + CM.getProfile().getName() + "_rosterStore");
        boolean isStore = true;
        rosterStore = CustomRosterStore.open(versionFile);
        if (rosterStore == null) {
            rosterStore = CustomRosterStore.init(versionFile);
            isStore = false;
        }

        roster = Roster.getInstanceFor(CM.getConnection());
        roster.setRosterStore(rosterStore);
        try {
            roster.reloadAndWait();
            roster.addRosterListener(this);
            roster.addSubscribeListener(this);
            roster.addPresenceEventListener(this);
            isStoreLoaded = true;
            if (isStore) {
                getInitialOnlineFriends();
            } else {
                roster.addRosterLoadedListener(new RosterLoadedListener() {
                    @Override
                    public void onRosterLoaded(Roster roster) {
                        getInitialOnlineFriends();
                    }

                    @Override
                    public void onRosterLoadingFailed(Exception exception) {}
                });
            }
            isRosterLoaded = true;
        } catch (Exception e) {
            Log.e("RosterHandlerErr", e.toString());
        }
    }


    @Override
    public SubscribeAnswer processSubscribe(Jid from, Presence subscribeRequest) {
        Log.e("presenceSubscribe", "called");
        return SubscribeAnswer.ApproveAndAlsoRequestIfRequired;
    }

    @Override
    public void presenceAvailable(FullJid address, Presence availablePresence) {
        String user = availablePresence.getFrom().asBareJid().toString();
        if(availablePresence.getFrom().toString().split("@")[0].equals(CM.getConnection().getUser().toString().split("@")[0])){
            return;
        }
        Log.e("userOnline", user);
        try {
            onlineFriends.add(ProfileItem.parseProfileItem(Functions.getSingleItemOfNode(CM.NODE_USERS,address.toString().split("@")[0])));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void presenceUnavailable(FullJid address, Presence presence) {
        Log.e("userOnline", address.toString());
        for(ProfileItem p: onlineFriends){
            if(p.getContent().get(ProfileItem.NAME).equals(address.toString().split("@")[0])){
                onlineFriends.remove(p);
                break;
            }
        }

    }

    @Override
    public void presenceChanged(Presence presence) {
        Log.e("presenceChanged", presence.toXML().toString());
    }

    @Override
    public void presenceError(Jid address, Presence errorPresence) {
            Log.e("RosterPresenceError", errorPresence.toXML().toString());
    }

    @Override
    public void presenceSubscribed(BareJid address, Presence subscribedPresence) {
        Log.e("presenceSubscribed", "called");
    }

    @Override
    public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
        Log.e("presenceUnsubscribed", "called");
        try {
            roster.removeEntry(roster.getEntry(address));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void entriesAdded(Collection<Jid> addresses) {
        Log.e("entriesAdded", "called");
    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {
        Log.e("entriesUpdated", "called");
    }

    @Override
    public void entriesDeleted(Collection<Jid> addresses) {
        Log.e("entriesDeleted", "called");
    }


}

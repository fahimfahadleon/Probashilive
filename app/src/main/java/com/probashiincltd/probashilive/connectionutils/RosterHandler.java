package com.probashiincltd.probashilive.connectionutils;


import android.content.Context;
import android.util.Log;

import com.probashiincltd.probashilive.utils.CustomRosterStore;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;

import java.io.File;
import java.util.Collection;


public class RosterHandler implements SubscribeListener, PresenceEventListener, RosterListener {
    public Roster roster;
    public CustomRosterStore rosterStore;
    boolean isStoreLoaded = false;
    boolean isRosterLoaded = false;
    static RosterHandler rosterHandler;
    public static RosterHandler getRosterHandler(){
        return rosterHandler;
    }

    public RosterHandler(Context context) {
        rosterHandler = this;
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


            } else {
                roster.addRosterLoadedListener(new RosterLoadedListener() {
                    @Override
                    public void onRosterLoaded(Roster roster) {


                    }

                    @Override
                    public void onRosterLoadingFailed(Exception exception) {

                    }
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
        Log.e("userOnline", user);
    }

    @Override
    public void presenceUnavailable(FullJid address, Presence presence) {
        Log.e("userOnline", address.toString());
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

    }

    @Override
    public void entriesAdded(Collection<Jid> addresses) {

    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {

    }

    @Override
    public void entriesDeleted(Collection<Jid> addresses) {
    }


}

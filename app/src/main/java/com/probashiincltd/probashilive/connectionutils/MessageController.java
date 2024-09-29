package com.probashiincltd.probashilive.connectionutils;

import android.util.Log;

import com.probashiincltd.probashilive.callbacks.MessageLoadCallBack;
import com.probashiincltd.probashilive.callbacks.WarningCallback;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.xdata.FormField;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.time.Instant;
import java.util.LinkedList;

public class MessageController {
    MamManager mamManager;
    MamManager.MamQueryArgs mamQueryArgs;
    MamManager.MamQuery mamQuery;
    String userJid;
    MamManager.MamQueryPage page;
    Jid jid;
    public boolean isFinished = false;


    public MessageController(String userJid, Instant first) {
        this.userJid = userJid;
        mamManager = MamManager.getInstanceFor(CM.getConnection());
        try {
            jid = JidCreate.bareFrom(userJid);
            FormField formField = FormField.builder("with").setValue(JidCreate.bareFrom(userJid))
                    .build();
            FormField formField2 = FormField.builder("end").setValue(first.toString())
                    .build();
            mamQueryArgs = MamManager.MamQueryArgs.builder()
                    .setResultPageSize(30)
                    .queryLastPage()
                    .withAdditionalFormField(formField)
                    .withAdditionalFormField(formField2)
                    .build();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    public  void loadFirst(Instant firstCacheMessage, MessageLoadCallBack messageLoadCallBack){
        try {
            FormField formField = FormField.builder("with").setValue(JidCreate.bareFrom(userJid))
                    .build();
            FormField formField2 = FormField.builder("start").setValue(firstCacheMessage.toString())
                    .build();
            MamManager.MamQueryArgs mamQueryArgs1 = MamManager.MamQueryArgs.builder()
                    .queryLastPage()
                    .withAdditionalFormField(formField)
                    .withAdditionalFormField(formField2)
                    .build();
            messageLoadCallBack.onServer(new LinkedList<>(mamManager.queryArchive(mamQueryArgs1).getMessages()));
        }catch (Exception e){
            messageLoadCallBack.onCache(new LinkedList<>());
        }
    }

    public MessageController(String userJid) {
        this.userJid = userJid;
        try {
            jid = JidCreate.bareFrom(userJid);
            FormField formField = FormField.builder("with").setValue(JidCreate.bareFrom(userJid))
                    .build();
            mamQueryArgs = MamManager.MamQueryArgs.builder()
                    .setResultPageSize(30)
                    .queryLastPage()
                    .withAdditionalFormField(formField)
                    .build();
        } catch (XmppStringprepException e) {
            e.fillInStackTrace();
        }
    }




    public LinkedList<Message> load(int count, WarningCallback warningCallback) {
        LinkedList<Message> linkedList;
        try {
            if (page == null) {
                if(mamManager == null){
                    return null;
                }
                mamQuery = mamManager.queryArchive(mamQueryArgs);
                page = mamQuery.getPage();

                linkedList = new LinkedList<>(mamQuery.getMessages());
                if (linkedList.size() < 30) {
                    isFinished = true;
                }

            } else {
                Log.e("Messagecontroller.load","pagenotnull");
                linkedList = (!isFinished ? new LinkedList<>(mamQuery.pagePrevious(count)) : new LinkedList<>());
                if (linkedList.size() != 30) {
                    isFinished = true;
                }
            }
            warningCallback.onSuccess();


            return linkedList;


        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException | SmackException.NotLoggedInException | InterruptedException e) {
            warningCallback.onFailed();
            return null;
        }

    }


    public int getMessageCount() throws NullPointerException {
        return page.getMamFinIq().getRSMSet().getCount();
    }


//    public void sendMessageReceivedNotification(String messageId) {
//        try {
//            if (ConnectionHolder.getMessageEventManager() != null) {
//                MessageEvent messageEvent = new MessageEvent();
//                messageEvent.setDisplayed(true);
//                messageEvent.setStanzaId(messageId);
//
//
//                String rand = Functions.createRandom();
//                Message msg = ConnectionHolder.getConnection().getStanzaFactory().buildMessageStanza()
//                        .to(jid)
//                        .addExtension(messageEvent)
//                        .build();
//                msg.setStanzaId();
//                // Send the packet
//                msg.setStanzaId(rand);
//
//
//
//                ConnectionHolder.getConnection().sendStanza(msg);
//
//            }
//        } catch (Exception e) {
//            Log.e("errorDelivery",e.toString());
//            e.printStackTrace();
//        }
//    }


//    public void sendComposingNotification(String messageId) {
//        if (TextUtils.isEmpty(messageId)) {
//            return;
//        }
//        try {
//            ConnectionHolder.getMessageEventManager().sendComposingNotification(jid,messageId);
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }

//    public void sendComposingCancelled(String messageId) {
//        if (TextUtils.isEmpty(messageId)) {
//            return;
//        }
//        try {
//            ConnectionHolder.getMessageEventManager().sendCancelledNotification(jid, messageId);
//        } catch (SmackException.NotConnectedException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

}

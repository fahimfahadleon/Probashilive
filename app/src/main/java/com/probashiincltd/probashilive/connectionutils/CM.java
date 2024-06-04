package com.probashiincltd.probashilive.connectionutils;

import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_TYPE;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_TYPE_GOOGLE;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;
import static com.probashiincltd.probashilive.utils.Configurations.SUBJECT_TYPE_VIDEO_INVITATION;
import static com.probashiincltd.probashilive.utils.Configurations.USER_EMAIL;
import static com.probashiincltd.probashilive.utils.Configurations.USER_PROFILE;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.probashiincltd.probashilive.cache.CacheManager;
import com.probashiincltd.probashilive.callbacks.HeadlineMessageListener;
import com.probashiincltd.probashilive.callbacks.RegisterCallback;
import com.probashiincltd.probashilive.functions.Functions;
import com.probashiincltd.probashilive.models.IpModel;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.utils.Configurations;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.parsing.SmackParsingException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.ByteUtils;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.android.ServerPingWithAlarmManager;
import org.json.JSONException;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.security.auth.callback.CallbackHandler;

public class CM extends XmppConnection {
    String userID;
    String passwrod;
    String action;
    static AbstractXMPPConnection connection;
    static IpModel model;
    public static void setIPModel(IpModel ipModel){
        model = ipModel;
    }
    public static IpModel getIPModel(){
        return model;
    }


    final String tag = "connection";
    int isReconnecting = 0;
    boolean isRosterLoaded = false;
    RegisterCallback registerCallback;
    public static final String NODE_LIVE_USERS = "liveusers";
    public static final String NODE_USERS = "users";

    public static AbstractXMPPConnection getConnection() {
        return connection;
    }

    public static boolean isConnected() {
        return connection.isConnected() && connection.isAuthenticated();
    }

    static ProfileItem profileModel;

    public static ProfileItem getProfile() {
        return profileModel;
    }

    static ArrayList<HeadlineMessageListener> headlineMessageListeners;

    public static void removeListener(HeadlineMessageListener listener){
        headlineMessageListeners.remove(listener);
    }


    public static void setHeadlineMessageListener(HeadlineMessageListener listener) {
        headlineMessageListeners.add(listener);
    }

    void publishHeadlineMessage(Stanza packet) {
        for (HeadlineMessageListener listener : headlineMessageListeners) {
            listener.onHeadlineMessage(packet);
        }
    }




    public static MutableLiveData<Message> headlineObserver = new MutableLiveData<>();



    public CM(Context context, String userid, String pass, String action, RegisterCallback registerCallback) {
        super(context, userid, pass);
        this.registerCallback = registerCallback;
        this.userID = userid;
        this.passwrod = pass;
        this.action = action;
        headlineMessageListeners = new ArrayList<>();

        connectionListener = new ConnectionListener() {
            @Override
            public void connecting(XMPPConnection connection) {
                ConnectionListener.super.connecting(connection);
            }

            @Override
            public void connected(XMPPConnection connection) {
                Log.e(tag, "connected");
                SASLAuthentication.registerSASLMechanism(new SASLMechanism() {
                    @Override
                    protected void authenticateInternal(CallbackHandler callbackHandler) {
                    }

                    @Override
                    protected byte[] getAuthenticationText() {
                        byte[] authcid = toBytes('\u0000' + this.authenticationId);
                        byte[] passw = toBytes('\u0000' + this.password);
                        return ByteUtils.concat(authcid, passw);
                    }

                    @Override
                    public String getName() {
                        return "PLAIN";
                    }

                    @Override
                    public int getPriority() {
                        return 410;
                    }

                    @Override
                    public void checkIfSuccessfulOrThrow() {
                    }

                    @Override
                    protected SASLMechanism newInstance() {
                        return this;
                    }
                });
                EntityCapsManager manager = EntityCapsManager.getInstanceFor(mConnection);
                manager.enableEntityCaps();


                if (isReconnecting == 1 && isRosterLoaded) {
                    isReconnecting = -1;
                    //todo reconnection successful
                } else if (isReconnecting == 0) {
                    switch (action) {
                        case "login":
                            try {
                                mConnection.login(userid, pass);
                            } catch (XMPPException | SmackException | IOException |
                                     InterruptedException e) {
                                //todo incorrect password
                                mConnection.disconnect();
                            }
                            break;
                        case "register": {
                            createAccount(userid, pass);
                            break;
                        }

                        case "autologin":
                        case "service": {
                            try {
                                mConnection.login(userid, pass);
                            } catch (XMPPException | SmackException | IOException |
                                     InterruptedException e) {
//                                Animation.isLoggedin = false;
                            }
                            break;
                        }
                    }
                }

            }

            @Override
            public void authenticated(XMPPConnection c, boolean resumed) {
                registerCallback.registerSuccessful();
                connection = mConnection;
                setUpStanzaListener();
                setUpPingManager();

                CacheManager.setCacheDir(context.getCacheDir().toString());
                CacheManager.load("/", "xml");
//                createProfile();

                if (action.equals("register")) {
                    //todo create everything that is needed for the first time
                    CM.profileModel = createProfile();
                } else {
                    try {
                        profileModel = ProfileItem.parseProfileItem(Functions.getSingleItemOfNode(NODE_USERS, Functions.getSP(LOGIN_USER, "")));
                    } catch (JSONException e) {
                        CM.profileModel = createProfile();
                    }
                }


                try {
                    new RosterHandler(context);
                }catch (Exception e){
                    e.fillInStackTrace();
                    Log.e("CM",e.toString());
                }


//                Log.e("createNodW","called");
//
//
//                //create pubsub
//                try {
//                    Functions.createNodeWithPublicAccess(NODE_USERS);
//                    Log.e("createNodW","called");
//                    Functions.createNodeWithPublicAccess(NODE_LIVE_USERS);
//                    Log.e("createNodW","called");
//
//
//                } catch (XmlPullParserException | IOException | SmackParsingException |
//                         SmackException.NotConnectedException | InterruptedException |
//                         XMPPException.XMPPErrorException | SmackException.NoResponseException e) {
//                    Log.e("createNodW",e.toString());
//                    throw new RuntimeException(e);
//                }

//delete all items of a node
//                for(Item i: Functions.getAllItemsOfNode(NODE_LIVE_USERS)){
//                    Functions.deleteFromNode(NODE_LIVE_USERS,i.getId());
//                }


            }

            @Override
            public void connectionClosed() {
                ConnectionListener.super.connectionClosed();
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                ConnectionListener.super.connectionClosedOnError(e);
            }
        };
    }


    private void setUpPingManager() {
        ServerPingWithAlarmManager.getInstanceFor(connection).setEnabled(true);
        PingManager pingManager = PingManager.getInstanceFor(connection);
        pingManager.setPingInterval(250);
        pingManager.pingServerIfNecessary();
    }



    public static void sendHLM(String subject, String body, String from, String to) {
        try {
            MessageBuilder messageBuilder = StanzaBuilder.buildMessage();
            messageBuilder.ofType(Message.Type.headline);
            messageBuilder.setBody(body);
            messageBuilder.setSubject(subject);
            Message message = messageBuilder.build();
            message.setFrom(JidCreate.fullFrom(from));
            message.setTo(JidCreate.fullFrom(to));
            getConnection().sendStanza(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void setUpStanzaListener() {
        connection.addAsyncStanzaListener(packet -> {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                if(message.getType() == Message.Type.headline && message.getSubject().equals(SUBJECT_TYPE_VIDEO_INVITATION)){
                    headlineObserver.postValue(message);
                }else if (message.getType() == Message.Type.headline) {
                    publishHeadlineMessage(packet);
                }
            }

        }, stanza -> {


            if (isReconnecting == 1) {
//                updateLastActivityAndPresence();
                isReconnecting = -1;
//                if (MainActivity.isActivityActivy) {
//                    checkPremiumData();
//                    MainActivity.dismissLoading();
//                }


            }
            return true;
        });
    }

    public void createAccount(String loginuser, String passwordUser, HashMap<String, String> map) {

        new Thread(() -> {
            try {
                HashMap<String, String> attributes = new HashMap<>();
                attributes.put("username", Localpart.from(loginuser).toString());
                attributes.put("password", passwordUser);
                if (!map.isEmpty()) {

                    attributes.putAll(map);

                    String s = "<iq type='set' xml:lang='en' id='" + UUID.randomUUID().toString() + "'>\n" +
                            "       <query xmlns='jabber:iq:register'>\n" +
                            "           <x xmlns='jabber:x:data' type='submit'>\n" +
                            "           <field var='FORM_TYPE'>\n" +
                            "           <value>jabber:iq:register</value>\n" +
                            "           </field>\n";

                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> val : attributes.entrySet()) {
                        if (val.getKey().equals("FORM_TYPE")) {
                            continue;
                        }
                        String section = "<field var='" + val.getKey() + "'><value>" + val.getValue() + "</value></field>\n";
                        sb = new StringBuilder(sb + section);
                    }


                    String a = "</x>\n" +
                            "    </query>\n" +
                            "</iq>";

                    IQ stanza = PacketParserUtils.parseStanza(s + sb + a);
                    mConnection.createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();

                } else {
                    String s = "<iq type='set' id='" + UUID.randomUUID().toString() + "'>\n" +
                            "  <query xmlns='jabber:iq:register'>\n" +
                            "    <username>" + userid + "</username>\n" +
                            "    <password>" + passwordUser + "</password>\n" +
                            "  </query>\n" +
                            "</iq>";

                    IQ stanza = PacketParserUtils.parseStanza(s);
                    mConnection.createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
                }

                mConnection.login(loginuser, passwordUser);

//                    setSharedPreference("isLoggedIn", "true");
//                    setUpVCard();


            } catch (XmlPullParserException | SmackParsingException | IOException e) {
                //ignore

                registerCallback.registerFailed(0);
            } catch (SmackException.NotConnectedException | InterruptedException e) {
                //todo not connected
                registerCallback.registerFailed(1);
                mConnection.disconnect();
            } catch (SmackException.NoResponseException e) {
                //todo no response from server
                registerCallback.registerFailed(2);
                mConnection.disconnect();
            } catch (XMPPException.XMPPErrorException e) {
                Log.e(tag, e.toString());
                if (e.toString().contains("User already exists")) {
                    try {
                        action = "login";
                        mConnection.login(loginuser, passwordUser);
                    } catch (XMPPException | SmackException | IOException |
                             InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    registerCallback.registerFailed(4);
                    mConnection.disconnect();
                }

            } catch (XMPPException | SmackException e) {
                registerCallback.registerFailed(5);
                Log.e(tag, e.toString());
                mConnection.disconnect();
            }
        }).start();


    }

    public void createAccount(String loginuser, String passwordUser) {

        try {
            String s = "   <iq to='" + Configurations.getHostName() + "' id='" + UUID.randomUUID().toString() + "' type='get'>\n" +
                    "                                         <query xmlns='jabber:iq:register'>\n" +
                    "                                         </query>\n" +
                    "                                       </iq>";

            IQ stanza = PacketParserUtils.parseStanza(s);

//            IQ result =
            mConnection.createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
//            DataForm dataForm = (DataForm) result.getExtension(DataForm.NAMESPACE);

            createAccount(loginuser, passwordUser, new HashMap<>());
        } catch (Exception e) {
            Log.e("ertr", e.toString());
            e.fillInStackTrace();
        }
    }

    public static void setProfile(ProfileItem profileItem){
        profileModel = profileItem;
    }

    private ProfileItem createProfile() {
        HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put(ProfileItem.NAME, Functions.getSP(LOGIN_USER, ""));
        if (Functions.getSP(LOGIN_TYPE, "").equals(LOGIN_TYPE_GOOGLE)) {
            profileMap.put(ProfileItem.PROFILE_PICTURE, Functions.getSP(USER_PROFILE, ""));
            profileMap.put(ProfileItem.EMAIL, Functions.getSP(USER_EMAIL, ""));
        } else {
            profileMap.put(ProfileItem.PROFILE_PICTURE, "");
            profileMap.put(ProfileItem.EMAIL, "");
        }
        profileMap.put(ProfileItem.PHONE, Functions.getSP("", ""));
        profileMap.put(ProfileItem.COIN, Functions.getSP("0", ""));
        profileMap.put(ProfileItem.VIP, Functions.getSP("false", ""));
        profileMap.put(ProfileItem.LANDING_ANIMATION,"");
        ProfileItem profileItem = new ProfileItem(profileMap);
        ProfileItem.updateProfileItem(profileItem);
        return profileItem;
    }


}

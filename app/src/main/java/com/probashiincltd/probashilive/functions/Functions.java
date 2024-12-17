package com.probashiincltd.probashilive.functions;

import static com.probashiincltd.probashilive.connectionutils.RosterHandler.TYPE_FOLLOWER;
import static com.probashiincltd.probashilive.connectionutils.RosterHandler.TYPE_FOLLOWING;
import static com.probashiincltd.probashilive.connectionutils.RosterHandler.TYPE_NO_FRIEND;
import static com.probashiincltd.probashilive.connectionutils.RosterHandler.getRosterHandler;
import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;
import static org.jivesoftware.smackx.pubsub.packet.PubSub.createPubsubPacket;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAPlayer;
import com.opensource.svgaplayer.SVGASoundManager;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.probashiincltd.probashilive.R;
import com.probashiincltd.probashilive.cache.CacheManager;
import com.probashiincltd.probashilive.callbacks.HttpRequestCallback;
import com.probashiincltd.probashilive.callbacks.ImageLoadCallback;
import com.probashiincltd.probashilive.callbacks.ImageUploadCallback;
import com.probashiincltd.probashilive.callbacks.UploadCallback;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.pubsubItems.ProfileItem;
import com.probashiincltd.probashilive.pubsubItems.UniversalModelMap;
import com.probashiincltd.probashilive.utils.Configurations;
import com.probashiincltd.probashilive.utils.CountingFileRequestBody;
import com.probashiincltd.probashilive.utils.Pair;

import org.jetbrains.annotations.NotNull;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.parsing.SmackParsingException;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager;
import org.jivesoftware.smackx.httpfileupload.element.Slot;
import org.jivesoftware.smackx.pubsub.GetItemsRequest;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jxmpp.jid.impl.JidCreate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Functions {

    static SharedPreferences preferences;
    private static PowerManager.WakeLock mProximityWakeLock;
    private static final String PROXIMITY_WAKE_LOCK_TAG = "live:in-rtp-session";


    public static ArrayList<ProfileItem> removeDuplicateProfiles(ArrayList<ProfileItem> list) {
        Set<String> set = new HashSet<>();
        ArrayList<ProfileItem> finalitem = new ArrayList<>();
        for (ProfileItem i : list) {
            if (set.add(i.getContent().get(ProfileItem.NAME))) {
                finalitem.add(i);
            }
        }
        return finalitem;
    }

    public static void init(Context context) {
        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void setImageView(Context context, String name, ImageView imageView) {
        try {
            InputStream inputStream = context.getAssets().open("important/images/" + name);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            //ignored
        }
    }

    public static void setLottieAnimation(String name, LottieAnimationView animationView) {
        animationView.setAnimation(name.replace(".png", ".json"));
        animationView.playAnimation();
    }


    public static int isFollowingOrFollower(String id) {
        int val = TYPE_NO_FRIEND;
        try {
            if (getRosterHandler().roster.contains(JidCreate.bareFrom(id))) {
                if (!getRosterHandler().roster.getEntry(JidCreate.bareFrom(id)).getGroups().isEmpty()) {
                    val = TYPE_FOLLOWING;
                } else {
                    val = TYPE_FOLLOWER;
                }
            }
        } catch (Exception e) {
            //ignored
        }

        return val;
    }

    public static void loadSVGAAnimation(Context context, String fileName, SVGAPlayer svgaPlayer) {
        SVGAParser parser = new SVGAParser(context);
        try {
            parser.parse(fileName.replace(".png", ".svga"), new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NonNull SVGAVideoEntity videoItem) {
                    svgaPlayer.setVideoItem(videoItem);
                    svgaPlayer.startAnimation();
                    SVGASoundManager manager = SVGASoundManager.INSTANCE;
                    manager.init();
                    manager.setVolume(1f, videoItem);
                }

                @Override
                public void onError() {
                    Log.e("error", "error loading animation");
                }
            });
        } catch (Exception e) {
            //ignored
        }
    }


    public static GoogleSignInClient getGoogleSigninClient(Context context) {

        GoogleSignInOptions mGoogleSigninOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(context, mGoogleSigninOptions);
    }

    public static void setSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSP(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }


    public static void releaseProximityWakeLock() {
        if (mProximityWakeLock != null && mProximityWakeLock.isHeld()) {
            mProximityWakeLock.release(PowerManager.RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY);
            mProximityWakeLock = null;
        }
    }

    public static void deleteNode(String nodename) throws XmlPullParserException, IOException, SmackParsingException, SmackException.NotConnectedException, InterruptedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        String s = "<iq type='set'\n" +
                "    from='" + CM.getConnection().getUser() + "'\n" +
                "    to='pubsub." + Configurations.getHostName() + "'\n" +
                "    id='" + createRandom() + "'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>\n" +
                "    <delete node='" + nodename + "'/>\n" +
                "  </pubsub>\n" +
                "</iq>";

        IQ stanza = PacketParserUtils.parseStanza(s);
        if (CM.isConnected()) {
            CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
        } else {
            Log.e("Functions: ", "could not delete node");
        }

    }

    public static void createNode(String nodename) throws XmlPullParserException, IOException, SmackParsingException, SmackException.NotConnectedException, InterruptedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {

        String s = "<iq type='set'\n" +
                "    to='" + PubSubManager.getInstanceFor(CM.getConnection()).getServiceJid() + "'\n" +
                "    id='" + createRandom() + "'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "    <create node='" + nodename + "'/>\n" +
                "  </pubsub>\n" +
                "</iq>";

        IQ stanza = PacketParserUtils.parseStanza(s);
        if (CM.isConnected()) {
            CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
        } else {
            Log.e("Functions: ", "could not create node");
        }


    }

    public static void createNodeUsersWithSubscriberAccess(String nodename) throws XmlPullParserException, IOException, SmackParsingException, SmackException.NotConnectedException, InterruptedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        String s = "<iq type='set'\n" +
                "    to='" + PubSubManager.getInstanceFor(CM.getConnection()).getServiceJid() + "'\n" +
                "    id='" + createRandom() + "'>\n" +
                "        <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "              <create node='" + nodename + "'/>\n" +
                "               <configure>\n" +
                "                   <x xmlns='jabber:x:data' type='submit'>\n" +
                "                        <field var='FORM_TYPE' type='hidden'>\n" +
                "                               <value>http://jabber.org/protocol/pubsub#node_config</value>\n" +
                "                         </field>\n" +
                "                          <field var='pubsub#publish_model'><value>subscribers</value></field>\n" +
                "                          <field var='pubsub#max_items'><value>5</value></field>\n" +
                "                    </x>\n" +
                "               </configure>" +
                "         </pubsub>\n" +
                "</iq>";

        IQ stanza = PacketParserUtils.parseStanza(s);
        if (CM.isConnected()) {
            CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
        } else {
            Log.e("Functions: ", "could not create node");
        }
    }

    public static void createNodeWithPublicAccess(String nodename) throws XmlPullParserException, IOException, SmackParsingException, SmackException.NotConnectedException, InterruptedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        String s = "<iq type='set'\n" +
                "    to='" + PubSubManager.getInstanceFor(CM.getConnection()).getServiceJid() + "'\n" +
                "    id='" + createRandom() + "'>\n" +
                "        <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                "              <create node='" + nodename + "'/>\n" +
                "               <configure>\n" +
                "                   <x xmlns='jabber:x:data' type='submit'>\n" +
                "                        <field var='FORM_TYPE' type='hidden'>\n" +
                "                               <value>http://jabber.org/protocol/pubsub#node_config</value>\n" +
                "                         </field>\n" +
                "                          <field var='pubsub#access_model'><value>open</value></field>\n" +
                "                          <field var='pubsub#publish_model'><value>open</value></field>\n" +
                "                    </x>\n" +
                "               </configure>" +
                "         </pubsub>\n" +
                "</iq>";

        IQ stanza = PacketParserUtils.parseStanza(s);
        if (CM.isConnected()) {
            CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
        } else {
            Log.e("Functions: ", "could not create node");
        }
    }

    public static String createRandom() {
        final int min = 99999;
        final int max = 999999;
        final int random = new Random().nextInt((max - min) + 1) + min;
        return String.valueOf(random);
    }

    public static String getStringFromDocument(Document document) {
        try {
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            transformer.transform(domSource, result);
            String output = writer.toString();
            String regex = "(<\\?xml.*\\?>)";
            return output.replaceAll(regex, "");

        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Item createLiveItem(String name, String type, String sdp, String viewers, String startedAt, String profile) throws ParserConfigurationException {

        String user = getSP(LOGIN_USER, "");

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder parser = factory.newDocumentBuilder();
        Document doc = parser.newDocument();
        Element root = doc.createElement(user);
        root.setAttribute("xmlns", "pubsub:live:user:user");
        Element em1 = doc.createElement("name");
        em1.setTextContent(name);
        Element em0 = doc.createElement("type");
        em0.setTextContent(type);
        Element em2 = doc.createElement("sdp");
        em2.setTextContent(sdp);
        Element em3 = doc.createElement("viewers");
        em3.setTextContent(viewers);
        Element em4 = doc.createElement("startedAt");
        em4.setTextContent(startedAt);


        Element em5 = doc.createElement("profile");
        em5.setTextContent(profile);

        root.appendChild(em1);
        root.appendChild(em0);
        root.appendChild(em2);
        root.appendChild(em3);
        root.appendChild(em4);

        doc.appendChild(root);
        String out = getStringFromDocument(doc);
        assert out != null;
        return new PayloadItem<>(name, new SimplePayload(out));
    }

    static <T extends UniversalModelMap> Element getRoot(Document doc, Element root, T content) {
        ArrayList<String> mylist = content.getNewList();
        for (String s : mylist) {
            Log.e("checking", s);
            Element em1 = doc.createElement(s);
            em1.setTextContent(content.getContent().get(s));
            root.appendChild(em1);
        }
        return root;
    }

    public static Item getSingleItemOfNode(String nodename, String itemID) {
        Item i = null;
        try {
            if (CM.isConnected()) {
                String s = "<iq type='get'\n" +
                        "    to='pubsub." + Configurations.getHostName() + "'\n" +
                        "    id='" + createRandom() + "'>\n" +
                        "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                        "    <items node='" + nodename + "'>\n" +
                        "      <item id='" + itemID + "'/>\n" +
                        "    </items>\n" +
                        "  </pubsub>\n" +
                        "</iq>";
                IQ stanza = PacketParserUtils.parseStanza(s);
                PubSub result = CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
                ItemsExtension itemsElem = result.getExtension(PubSubElementType.ITEMS);

                if (itemsElem.getItems().size() != 0) {
                    i = (Item) itemsElem.getItems().get(0);
                }

            }

            return i;
        } catch (Exception e) {
            e.fillInStackTrace();
            return null;
        }
    }


    public static ArrayList<Item> getAllItemsOfNode(String nodename) {
        ArrayList<Item> items = new ArrayList<>();
        try {
            if (CM.isConnected()) {
                String s = "<iq type='get'\n" +
                        "    to='pubsub." + Configurations.getHostName() + "'\n" +
                        "    id='" + createRandom() + "'>\n" +
                        "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                        "    <items node='" + nodename + "'/>\n" +
                        "  </pubsub>\n" +
                        "</iq>";
                IQ stanza = PacketParserUtils.parseStanza(s);
                PubSub result = CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
                ItemsExtension itemsElem = result.getExtension(PubSubElementType.ITEMS);
                for (NamedElement e : itemsElem.getItems()) {
                    Item item = (Item) e;
                    items.add(item);
                }
            }

            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return items;
        }
    }


    public static void deleteFromNode(String nodename, String itemID) {
        try {
            if (CM.isConnected()) {

                String s = "<iq type='set'\n" +
                        "    to='pubsub." + Configurations.getHostName() + "'\n" +
                        "    id='" + createRandom() + "'>\n" +
                        "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                        "    <retract node='" + nodename + "'>\n" +
                        "      <item id='" + itemID + "'/>\n" +
                        "    </retract>\n" +
                        "  </pubsub>\n" +
                        "</iq>";
                IQ stanza = PacketParserUtils.parseStanza(s);
                CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//    public static void createFirebaseNode(ChatItem chatItem) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference reference = database.getReference();
//        reference.child(FIREBASE_CHAT_BOX).child(chatItem.getJid()).setValue(chatItem);
//
//    }

//    public static ArrayList<String> getChatList(Context context){
//        ArrayList<String> chatList = new ArrayList<>();
//
//        reference.child()
//
//
//        return chatList;
//    }

    public static <T extends UniversalModelMap> Item createRawItem(T map) throws ParserConfigurationException {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder parser = factory.newDocumentBuilder();

        Document doc = parser.newDocument();
        Element root = doc.createElement(map.getName());
        root.setAttribute("xmlns", map.getXmlns());
        Element r = getRoot(doc, root, map);
        doc.appendChild(r);
        String out = getStringFromDocument(doc);

        return new PayloadItem<>(map.getID(), new SimplePayload(out));
    }

    @SuppressWarnings("unchecked")
    public static Pair getPagenatedDataStart(String nodeName) {
        try {
            PubSub request = createPubsubPacket(
                    JidCreate.bareFrom("pubsub." + Configurations.getHostName()),
                    IQ.Type.get,
                    new GetItemsRequest(nodeName, 25)
            );
            PubSub result = CM.getConnection().createStanzaCollectorAndSend(request).nextResultOrThrow();
            ItemsExtension itemsElem = result.getExtension(PubSubElementType.ITEMS);
            RSMSet set = RSMSet.from(result);

            return new Pair((List<Item>) itemsElem.getItems(), set.getLast());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Pair getNextPagenatedData(String nodeName, String lastItemId) {
        try {
            Log.e("lastItemId", lastItemId);
            IQ iq = new IQ("query", "http://jabber.org/protocol/disco#items") {
                @Override
                protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
                    xml.attribute("node", nodeName);
//                        xml.attribute("max_items", "5");
                    xml.rightAngleBracket();
                    xml.halfOpenElement("set");
                    xml.attribute("xmlns", "http://jabber.org/protocol/rsm");
                    xml.rightAngleBracket();
                    xml.halfOpenElement("max");
                    xml.rightAngleBracket();
                    xml.append("25");
                    xml.closeElement("max");
                    xml.halfOpenElement("before");
                    xml.rightAngleBracket();
                    xml.append(lastItemId);
                    xml.closeElement("before");
                    xml.closeElement("set");
                    return xml;
                }
            };
            iq.setType(IQ.Type.get);
            iq.setTo(JidCreate.bareFrom("pubsub." + Configurations.getHostName()));
            DiscoverItems result = CM.getConnection().createStanzaCollectorAndSend(iq).nextResultOrThrow();
            List<DiscoverItems.Item> disItems = result.getItems();

            List<String> ids = new ArrayList<>();
            List<Item> items = new ArrayList<>();
            for (DiscoverItems.Item i : disItems) {
                ids.add(i.getName());
                items.add(new Item(i.getName()));
            }


            PubSub request = createPubsubPacket(JidCreate.bareFrom("pubsub." + Configurations.getHostName()), IQ.Type.get, new ItemsExtension(ItemsExtension.ItemsElementType.items, nodeName, items));
            RSMSet rsmSet1 = new RSMSet(25);
            request.addExtension(rsmSet1);
            PubSub resultrequest = CM.getConnection().createStanzaCollectorAndSend(request).nextResultOrThrow();
            rsmSet1 = RSMSet.from(resultrequest);

            return new Pair(getMultipleItemOfNode(nodeName, ids), rsmSet1.getLast());
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Item> getMultipleItemOfNode(String nodename, List<String> itemID) {
        ArrayList<Item> items = new ArrayList<>();

        try {
            StringBuilder builder = new StringBuilder();
            for (String s : itemID) {
                builder.append("<item id='").append(s).append("'/>\n");
            }
            String s = "<iq type='get'\n" +
                    "    to='pubsub." + Configurations.getHostName() + "'\n" +
                    "    id='" + createRandom() + "'>\n" +
                    "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                    "    <items node='" + nodename + "'>\n" +
                    builder +
                    "    </items>\n" +
                    "  </pubsub>\n" +
                    "</iq>";
            IQ stanza = PacketParserUtils.parseStanza(s);
            if (!CM.isConnected()) {
                return new ArrayList<>();
            }
            PubSub result = CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
            ItemsExtension itemsElem = result.getExtension(PubSubElementType.ITEMS);
            for (NamedElement n : itemsElem.getItems()) {
                Item i = (Item) n;
                items.add(i);
            }

            return items;
        } catch (Exception e) {
            e.fillInStackTrace();
            return items;
        }
    }



    static class ExtDisco extends IQ {
        public static final String ELEMENT = "services";
        public static final String NAMESPACE = "urn:xmpp:extdisco:2";

        public ExtDisco() {
            super(ELEMENT, NAMESPACE);
        }

        @Override
        protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
            xml.rightAngleBracket();
            return xml;
        }
    }

    public static String unEscapeXml(String s) {
        return s.replaceAll("&amp;", "&")
                .replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'");
    }

    public static String EscapeXml(String s) {
        return s.replaceAll("&", "&amp;")
                .replaceAll(">", "&gt;")
                .replaceAll("<", "&lt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&apos;");
    }

    public static String removeAllSign(String s){
        return s.replace(".","_");
    }
    public static void publishToNode(String nodename, Item item, String id) {
        try {


            String s = "<iq type='set'\n" +
                    "    to='pubsub." + Configurations.getHostName() + "'\n" +
                    "    id='" + id + "'>\n" +
                    "  <pubsub xmlns='http://jabber.org/protocol/pubsub'>\n" +
                    "    <publish node='" + nodename + "'>\n" +
                    item.toXML().toString() +
                    "    </publish>\n" +
                    "  </pubsub>\n" +
                    "</iq>";
            IQ stanza = PacketParserUtils.parseStanza(s);
            if (CM.isConnected()) {
                CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
            } else {
                Log.e("Functions: ", "could not create node");
            }

        } catch (Exception e) {
            e.fillInStackTrace();
            Log.e("exceptionFound", e.toString());
        }
    }


    //configure pubsub
    //<iq type='set'
    //    from='hamlet@denmark.lit/elsinore'
    //    to='pubsub.shakespeare.lit'
    //    id='create1'>
    //  <pubsub xmlns='http://jabber.org/protocol/pubsub'>
    //    <create node='princely_musings'/>
    //    <configure>
    //      <x xmlns='jabber:x:data' type='submit'>
    //        <field var='FORM_TYPE' type='hidden'>
    //          <value>http://jabber.org/protocol/pubsub#node_config</value>
    //        </field>
    //        <field var='pubsub#title'><value>Princely Musings (Atom)</value></field>
    //        <field var='pubsub#deliver_notifications'><value>1</value></field>
    //        <field var='pubsub#deliver_payloads'><value>1</value></field>
    //        <field var='pubsub#persist_items'><value>1</value></field>
    //        <field var='pubsub#max_items'><value>10</value></field>
    //        <field var='pubsub#item_expire'><value>604800</value></field>
    //        <field var='pubsub#access_model'><value>open</value></field>
    //        <field var='pubsub#publish_model'><value>publishers</value></field>
    //        <field var='pubsub#purge_offline'><value>0</value></field>
    //        <field var='pubsub#send_last_published_item'><value>never</value></field>
    //        <field var='pubsub#presence_based_delivery'><value>false</value></field>
    //        <field var='pubsub#notification_type'><value>headline</value></field>
    //        <field var='pubsub#notify_config'><value>0</value></field>
    //        <field var='pubsub#notify_delete'><value>0</value></field>
    //        <field var='pubsub#notify_retract'><value>0</value></field>
    //        <field var='pubsub#notify_sub'><value>0</value></field>
    //        <field var='pubsub#max_payload_size'><value>1028</value></field>
    //        <field var='pubsub#type'><value>urn:example:e2ee:bundle</value></field>
    //        <field var='pubsub#body_xslt'>
    //          <value>http://jabxslt.jabberstudio.org/atom_body.xslt</value>
    //        </field>
    //      </x>
    //    </configure>
    //  </pubsub>
    //</iq>


    public static void requestHttp(String u, HttpRequestCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(u);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                inputStream.close();
                urlConnection.disconnect();
                callback.onResponse(stringBuilder.toString());
            } catch (Exception e) {
                callback.onResponse(null);
            }

        });
    }


    public static void uploadProtectedFile(AbstractXMPPConnection connection, ArrayList<File> files, Context context, ImageUploadCallback callback) {
        Runnable r = new Runnable() {
            public void fun() {
                synchronized (this) {
                    notify();
                }
            }


            @Override
            public void run() {
                try {
                    ArrayList<Slot> originalSlots = new ArrayList<>();
                    String mimtype = "";
                    for (int i = 0; i < files.size(); i++) {
                        File file = files.get(i);
                        mimtype = getMimeType(context, file);
                        String finalMimtype1 = mimtype;


                        Bitmap thumbnail = BitmapFactory.decodeFile(file.getPath());

                        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                        int targetWidth = (width / 3) * 2;
                        double aspectRatio = (double) thumbnail.getHeight() / (double) thumbnail.getWidth();
                        int targetHeight = (int) (targetWidth * aspectRatio);
                        if (targetHeight > targetWidth) {
                            targetWidth = targetWidth / 2;
                            targetHeight = targetHeight / 2;
                        }
                        Bitmap result = Bitmap.createScaledBitmap(thumbnail, targetWidth, targetHeight, false);
                        File mymile = new File(context.getExternalCacheDir() + "_" + file.getName());

                        if (!mymile.exists()) {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            result.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                            byte[] bitmapdata = bos.toByteArray();
                            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                            try {
                                try {
                                    OutputStream output = new FileOutputStream(mymile);
                                    byte[] buffer = new byte[1024]; // or other buffer size
                                    int read;
                                    while ((read = bs.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                    }
                                    output.flush();
                                } catch (IOException e) {
                                    e.fillInStackTrace();
                                }
                            } finally {
                                try {
                                    bs.close();
                                } catch (IOException e) {
                                    e.fillInStackTrace();
                                }
                            }
                        }
                        uploadFile(context, connection, mymile, new UploadCallback() {
                            @Override
                            public void onSuccess(Slot slot) {
                                originalSlots.add(slot);
                                callback.onSuccess(originalSlots, null, finalMimtype1);
                                boolean ignored = mymile.delete();
                                fun();
                            }

                            @Override
                            public void onFailed() {

                            }
                        });










                        synchronized (this) {
                            wait();
                        }
                    }
                } catch (Exception ex) {
                    ex.fillInStackTrace();
                    callback.onFailed(ex.toString());
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    static RequestBody getRequestBody(Context context, File file) {
        String mimeType = getMimeType(context, file);
        long totalSize = file.length();
        return new CountingFileRequestBody(file, mimeType, num -> {
            float progress = (num / (float) totalSize) * 100;
            Log.e("persendage", String.valueOf(progress));
            if (progress == 100) {
//                    dismissLoadingDialog();
            }
        });
    }

    public static void loadImage(Context context, ImageView imageView, String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                CacheManager.httpCache("profile load iamge", "image", url, new ImageLoadCallback() {
                    @Override
                    public void onSuccess(InputStream inputStream) {
                        Log.e("onSuccess", "called");
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                            Glide.with(imageView).load(bitmap).into(imageView);
                            imageView.setImageBitmap(bitmap);
                        });
                    }

                    @Override
                    public void onFailed(String error) {
                        Log.e("onfailed", error);
                        new Handler(Looper.getMainLooper()).post(() -> imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.person)));
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(() -> imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.person)));
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public static String getMimeType(Context context, File file) {
        Uri uri = Uri.fromFile(file);
        String mimeType;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {

            // Create a trust manager that does not validate certificate chains
            @SuppressLint("CustomX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    static Slot uploadFile(Context context, AbstractXMPPConnection connection, File file, UploadCallback callback) throws Exception {
        HttpFileUploadManager httpFileUploadManager = HttpFileUploadManager.getInstanceFor(connection);
        if (httpFileUploadManager.discoverUploadService()) {
            if (httpFileUploadManager.isUploadServiceDiscovered()) {
                RequestBody requestBody = getRequestBody(context, file);
                final Slot slot = httpFileUploadManager.requestSlot(file.getName(), requestBody.contentLength(), getMimeType(context, file));
                OkHttpClient client = getUnsafeOkHttpClient();
                Request request = new Request.Builder()
                        .url(slot.getPutUrl())
                        .put(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                        Log.e("exceptionasdf", e.toString());
                        callback.onFailed();
                    }

                    @Override
                    public void onResponse(@NotNull final Call call, @NotNull final Response response) {
                        callback.onSuccess(slot);
                    }
                });

                return slot;

            } else {
                Log.e("uploadservice", "false");
            }
        } else {
            Log.e("httpuploadservice", "false");
        }
        return null;
    }


}

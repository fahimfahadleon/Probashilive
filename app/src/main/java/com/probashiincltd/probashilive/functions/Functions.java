package com.probashiincltd.probashilive.functions;

import static com.probashiincltd.probashilive.utils.Configurations.LOGIN_USER;
import static org.jivesoftware.smackx.pubsub.packet.PubSub.createPubsubPacket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.probashiincltd.probashilive.callbacks.HttpRequestCallback;
import com.probashiincltd.probashilive.connectionutils.CM;
import com.probashiincltd.probashilive.pubsubItems.UniversalModelMap;
import com.probashiincltd.probashilive.utils.Configurations;
import com.probashiincltd.probashilive.utils.Pair;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.parsing.SmackParsingException;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Functions {

    static SharedPreferences preferences;
    private static PowerManager.WakeLock mProximityWakeLock;
    private static final String PROXIMITY_WAKE_LOCK_TAG = "live:in-rtp-session";

    public static void init(Context context){
        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static GoogleSignInClient getGoogleSigninClient(Context context) {

        GoogleSignInOptions mGoogleSigninOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(context, mGoogleSigninOptions);
    }

    public static void setSetSharedPreference(String key, String value) {
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
                "    from='"+ CM.getConnection().getUser()+"'\n" +
                "    to='pubsub."+Configurations.getHostName()+"'\n" +
                "    id='"+createRandom()+"'>\n" +
                "  <pubsub xmlns='http://jabber.org/protocol/pubsub#owner'>\n" +
                "    <delete node='"+nodename+"'/>\n" +
                "  </pubsub>\n" +
                "</iq>";

        IQ stanza = PacketParserUtils.parseStanza(s);
        if(CM.isConnected()){
            CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
        }else {
            Log.e("Functions: ","could not delete node");
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
        if(CM.isConnected()){
            CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
        }else {
            Log.e("Functions: ","could not create node");
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
                "               </configure>"+
                "         </pubsub>\n" +
                "</iq>";

        IQ stanza = PacketParserUtils.parseStanza(s);
        if(CM.isConnected()){
            CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
        }else {
            Log.e("Functions: ","could not create node");
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
                "               </configure>"+
                "         </pubsub>\n" +
                "</iq>";

        IQ stanza = PacketParserUtils.parseStanza(s);
        if(CM.isConnected()){
            CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
        }else {
            Log.e("Functions: ","could not create node");
        }    }

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

    public static Item createLiveItem(String name,String type, String sdp,String viewers,String startedAt,String profile ) throws ParserConfigurationException {

        String user = getSP(LOGIN_USER,"");

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

    static <T extends UniversalModelMap>Element getRoot(Document doc,Element root, T content){
        ArrayList<String>mylist = content.getNewList();
        for(String s: mylist){
            Element em1 = doc.createElement(s);
            em1.setTextContent(content.getContent().get(s));
            root.appendChild(em1);
        }
        return root;
    }

    public static Item getSingleItemOfNode(String nodename, String itemID) {
        Item i = null;
        try {
            if(CM.isConnected()){
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
            e.printStackTrace();
            return null;
        }
    }


    public static ArrayList<Item> getAllItemsOfNode(String nodename) {
        ArrayList<Item> items = new ArrayList<>();
        try {
            if(CM.isConnected()){
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
            if(CM.isConnected()){

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

    public static <T extends UniversalModelMap>Item createRawItem(T map) throws ParserConfigurationException {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder parser = factory.newDocumentBuilder();

        Document doc = parser.newDocument();
        Element root = doc.createElement(map.getName());
        root.setAttribute("xmlns", map.getXmlns());
        Element r = getRoot(doc,root,map);
        doc.appendChild(r);
        String out = getStringFromDocument(doc);

        return new PayloadItem<>(map.getID(), new SimplePayload(out));
    }

    @SuppressWarnings("unchecked")
    public static Pair getPagenatedDataStart(String nodeName) {
        try {
            PubSub request = createPubsubPacket(
                    JidCreate.bareFrom("pubsub."+Configurations.getHostName()),
                    IQ.Type.get,
                    new GetItemsRequest(nodeName, 25)
            );
            PubSub result = CM.getConnection().createStanzaCollectorAndSend(request).nextResultOrThrow();
            ItemsExtension itemsElem = result.getExtension(PubSubElementType.ITEMS);
            RSMSet set = RSMSet.from(result);

            return new Pair((List<Item>) itemsElem.getItems(),set.getLast());
        } catch ( Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Pair getNextPagenatedData(String nodeName, String lastItemId) {
        try {
            Log.e("lastItemId",lastItemId);
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
            iq.setTo(JidCreate.bareFrom("pubsub."+Configurations.getHostName()));
            DiscoverItems result = CM.getConnection().createStanzaCollectorAndSend(iq).nextResultOrThrow();
            List<DiscoverItems.Item> disItems = result.getItems();

            List<String> ids = new ArrayList<>();
            List<Item> items = new ArrayList<>();
            for (DiscoverItems.Item i : disItems) {
                ids.add(i.getName());
                items.add(new Item(i.getName()));
            }



            PubSub request = createPubsubPacket(JidCreate.bareFrom("pubsub."+Configurations.getHostName()), IQ.Type.get, new ItemsExtension(ItemsExtension.ItemsElementType.items, nodeName, items));
            RSMSet rsmSet1 = new RSMSet(25);
            request.addExtension(rsmSet1);
            PubSub resultrequest = CM.getConnection().createStanzaCollectorAndSend(request).nextResultOrThrow();
            rsmSet1 = RSMSet.from(resultrequest);

            return new Pair(getMultipleItemOfNode(nodeName,ids),rsmSet1.getLast());
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
            if(!CM.isConnected()){
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




    public static void publishToNode(String nodename, Item item,String id) {
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
            if(CM.isConnected()){
                CM.getConnection().createStanzaCollectorAndSend(new StanzaIdFilter(stanza.getStanzaId()), stanza).nextResultOrThrow();
            }else {
                Log.e("Functions: ","could not create node");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exceptionFound",e.toString());
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


    public static void requestHttp(String u, HttpRequestCallback callback){
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
            }catch (Exception e){
                callback.onResponse(null);
            }

        });
    }



}

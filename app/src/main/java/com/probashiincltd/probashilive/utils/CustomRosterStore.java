package com.probashiincltd.probashilive.utils;

import android.util.Log;

import com.probashiincltd.probashilive.connectionutils.CM;

import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.provider.RosterPacketProvider;
import org.jivesoftware.smack.roster.rosterstore.RosterStore;
import org.jivesoftware.smack.util.FileUtils;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.stringencoder.Base32;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jxmpp.jid.Jid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CustomRosterStore implements RosterStore {

    private final File fileDir;

    private static final String ENTRY_PREFIX = "entry-";
    private static final String VERSION_FILE_NAME = "__version__";
    private static final String STORE_ID = "DEFAULT_ROSTER_STORE";
    private static final Logger LOGGER = Logger.getLogger(CustomRosterStore.class.getName());
    public boolean isRosterReset = false;

    private static boolean rosterDirFilter(File file) {
        String name = file.getName();
        return name.startsWith(ENTRY_PREFIX);
    }

    private CustomRosterStore(final File baseDir) {
        this.fileDir = baseDir;
    }

    public static CustomRosterStore init(final File baseDir) {
        if(!baseDir.exists()){
            boolean ignored = baseDir.mkdirs();
        }
        CustomRosterStore store = new CustomRosterStore(baseDir);
        if (store.setRosterVersion("")) {
            return store;
        }
        else {
            return null;
        }
    }

    public static CustomRosterStore open(final File baseDir) {
        CustomRosterStore store = new CustomRosterStore(baseDir);
        String s = FileUtils.readFile(store.getVersionFile());
        if (s != null && s.startsWith(STORE_ID + "\n")) {
            return store;
        }
        else {
            return null;
        }
    }

    private File getVersionFile() {
        File file = new File(fileDir, VERSION_FILE_NAME);
        try {
            if(file.exists()){
                return file;
            }else {
                try {
                    boolean ignored = file.createNewFile();
                }catch (IOException e){
                    boolean ignored =   fileDir.mkdirs();
                    boolean ignored2 = file.createNewFile();
                }


            }
            return file;
        }catch (Exception e){
            Log.e("CustomRosterStoreErr",e.toString());
            return file;
        }


    }

    @Override
    public List<RosterPacket.Item> getEntries() {
        List<RosterPacket.Item> entries = new ArrayList<>();

        File[] files  = fileDir.listFiles(CustomRosterStore::rosterDirFilter);
        if(files == null){
            return entries;
        }

        for (File file : files) {
            RosterPacket.Item entry = readEntry(file);
            if (entry == null) {
                // Roster directory store corrupt. Abort and signal this by returning null.
                return entries;
            }
            entries.add(entry);
        }

        return entries;
    }

    @Override
    public RosterPacket.Item getEntry(Jid bareJid) {
        if(bareJid == null){
            return null;
        }
        if(CM.isConnected()) {
            if(bareJid.toString().equals(CM.getConnection().getUser().asEntityBareJidString())){
                return null;
            }
        }

        Log.e("Calledremoveentry",bareJid.toString());

        return readEntry(getBareJidFile(bareJid));
    }

    @Override
    public String getRosterVersion() {
        String s = FileUtils.readFile(getVersionFile());
        if (s == null) {
            return null;
        }
        String[] lines = s.split("\n", 2);
        if (lines.length < 2) {
            return null;
        }
        return lines[1];
    }

    private boolean setRosterVersion(String version) {
        return FileUtils.writeFile(getVersionFile(), STORE_ID + "\n" + version);
    }

    @Override
    public boolean addEntry(RosterPacket.Item item, String version) {
        Log.e("addEntry",item.getJid().toString());
        return addEntryRaw(item) && setRosterVersion(version);
    }

    @Override
    public boolean removeEntry(Jid bareJid, String version) {
        Log.e("Calledremoveentry",bareJid.toString());
        return getBareJidFile(bareJid).delete() && setRosterVersion(version);
    }

    @Override
    public boolean resetEntries(Collection<RosterPacket.Item> items, String version) {
        isRosterReset = true;
        List<RosterPacket.Item> changedItem = new ArrayList<>();

        if(getEntries()!=null){
            for(RosterPacket.Item i:getEntries()){
                for(RosterPacket.Item ii:items){
                    if(ii.getJid().toString().equals(i.getJid().toString())){
                        if(!i.toXML().toString().equals(ii.toXML().toString())){
                            changedItem.add(ii);
                            break;
                        }
                    }
                }
            }
        }

        ArrayList<RosterPacket.Item> itemtoberemoved = new ArrayList<>(changedItem);

        items.removeAll(itemtoberemoved);

        for (File file : fileDir.listFiles(CustomRosterStore::rosterDirFilter)) {
            boolean ignored = file.delete();
        }
        for (RosterPacket.Item item : items) {
            if (!addEntryRaw(item)) {
                return false;
            }
        }

//        FriendsController.setUpInitialFriendList(new ArrayList<>(items));

        return setRosterVersion(version);
    }


    @Override
    public void resetStore() {
        resetEntries(Collections.<RosterPacket.Item>emptyList(), "");
    }

    @SuppressWarnings("DefaultCharset")
    private static RosterPacket.Item readEntry(File file) {
        Reader reader;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            return null;
        }

        try {
            XmlPullParser parser = PacketParserUtils.getParserFor(reader);
            RosterPacket.Item item = RosterPacketProvider.parseItem(parser);
            reader.close();
            return item;
        } catch (XmlPullParserException | IOException | IllegalArgumentException e) {
            boolean deleted = file.delete();
            String message = "Exception while parsing roster entry.";
            if (deleted) {
                message += " File was deleted.";
            }
            LOGGER.log(Level.SEVERE, message, e);
            return null;
        }
    }

    private boolean addEntryRaw (RosterPacket.Item item) {
        return FileUtils.writeFile(getBareJidFile(item.getJid()), item.toXML());
    }

    private File getBareJidFile(Jid bareJid) {
        String encodedJid = Base32.encode(bareJid.toString());
        try {
            File file = new File(fileDir, ENTRY_PREFIX + encodedJid);
            if(!file.exists()){
                boolean ignored = file.createNewFile();
            }
            return file;
        }catch (Exception e){
           Log.e("CustomRosterStoreErr",e.toString());
            return null;
        }

    }

}
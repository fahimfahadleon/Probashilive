package com.probashiincltd.probashilive.config;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    final HashMap<String, Item> items = new HashMap<>();
    Callback callback = new Callback();
    public void setCallback(Callback callback) {
        changeCallback("MeCloak", callback);
    }
    public Callback getCallback() {
        return callback;
    }
    public void removeCallback() {
        this.callback = new Callback();
        for (Map.Entry<String, Item> it:items.entrySet()){
            it.getValue().removeCallback();
        }
    }
    public void changeCallback(String oldId, Callback callback) {
        if (oldId.equals(this.callback.id())){
            this.callback = callback;
            for (Map.Entry<String, Item> it:items.entrySet()){
                it.getValue().changeCallback(oldId, callback);
            }
        }
    }
    public void load(String itemXML, String identifier) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(itemXML)));
            if (items.containsKey(identifier)) items.replace(identifier, new Item(doc.getDocumentElement()));
            else items.put(identifier, new Item(doc.getDocumentElement()));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

    }
    public Unit get(String key) {
        String delimiter = "/";
        String[] keys = key.split(delimiter);
        String[] modifiedArray = Arrays.copyOfRange(keys, 1, keys.length);
        String result = String.join(delimiter, modifiedArray);
        if (result.equals("")) throw new RuntimeException("Configuration index not found");
        else return items.get(keys[0]).get(result);
    }
    public Item getItem(String key) {
        return items.get(key);
    }

}

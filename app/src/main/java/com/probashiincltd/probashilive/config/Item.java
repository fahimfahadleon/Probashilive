package com.probashiincltd.probashilive.config;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Item {
    final HashMap <String, Unit> valueMap = new HashMap<>();
    final HashMap<String, String> attributes = new HashMap<>();
    String nodeName;
    Callback callback = new Callback();
    public void setCallback(Callback callback) {
        changeCallback("MeCloak", callback);
    }
    public Callback getCallback() {
        return callback;
    }
    public void removeCallback() {
        this.callback = new Callback();
        for (Map.Entry<String, Unit> it:valueMap.entrySet()){
            it.getValue().removeCallback();
        }
    }
    public void changeCallback(String oldId, Callback callback) {
        if (oldId.equals(this.callback.id())){
            this.callback = callback;
            for (Map.Entry<String, Unit> it:valueMap.entrySet()){
                it.getValue().changeCallback(oldId, callback);
            }
        }
    }
    public Item(Node item){
        parse(item);
        nodeName = item.getNodeName();
        NamedNodeMap attrs = item.getAttributes();
        int numAttrs = attrs.getLength();
        for (int i = 0; i < numAttrs; i++) {
            Node attr = attrs.item(i);
            attributes.put(attr.getNodeName(), attr.getNodeValue());
        }
    }
    void parse (Node item) {

        NodeList nodeList = item.getChildNodes();
        for (int i = 0; i<nodeList.getLength();i++) {
            valueMap.put(nodeList.item(i).getNodeName(), new Unit(nodeList.item(i)));
            System.out.println(nodeList.item(i).getNodeName());
        }
    }
    public HashMap <String, Unit> getMap () {
        return new HashMap<>(valueMap);
    }

    public Unit get(String key) {
        String delimiter = "/";
        String[] keys = key.split(delimiter);
        String[] modifiedArray = Arrays.copyOfRange(keys, 1, keys.length);
        Unit unit = valueMap.get(keys[0]);
        for (String str:modifiedArray) {
            unit = unit.get(str);
            if (unit == null){
                return null;
            }
        }
        return unit;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("<" + nodeName);
        for (Map.Entry<String, String> a : attributes.entrySet()) {
            str.append(" ").append(a.getKey()).append("='").append(a.getValue()).append("'");
        }
        str.append(">");
        for (Map.Entry<String, Unit> b : valueMap.entrySet()) {
            str.append(b.getValue().toString());
        }
        str.append("</").append(nodeName).append(">");
        return str.toString();
    }

    public HashMap<String, String> getAttributes() {
        return new HashMap<>(attributes);
    }
}

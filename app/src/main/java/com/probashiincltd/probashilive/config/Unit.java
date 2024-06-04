package com.probashiincltd.probashilive.config;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Unit {
    final HashMap<String, Unit> value = new HashMap<>();
    final HashMap<String, String> attributes = new HashMap<>();
    String textValue;
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
        for (Map.Entry<String, Unit> it : value.entrySet()) {
            it.getValue().removeCallback();
        }
    }

    public void changeCallback(String oldId, Callback callback) {
        if (oldId.equals(this.callback.id())) {
            this.callback = callback;
            for (Map.Entry<String, Unit> it : value.entrySet()) {
                it.getValue().changeCallback(oldId, callback);
            }
        }
    }

    public Unit(Node node) {
        nodeName = node.getNodeName();
        NamedNodeMap attrs = node.getAttributes();
        int numAttrs = attrs.getLength();
        for (int i = 0; i < numAttrs; i++) {
            Node attr = attrs.item(i);
            attributes.put(attr.getNodeName(), attr.getNodeValue());
        }
        iterateNodes(node);
    }

    private Unit() {
    }

    public void add(String nodeName, String textValue) {
        Unit unit = new Unit();
        unit.nodeName = nodeName;
        unit.textValue = textValue;
        value.put(generateNodeName(nodeName, 0), unit);
        callback.onInsert();
    }

    public void add(String nodeName, String textValue, HashMap<String, String> attributes) {
        Unit unit = new Unit();
        unit.nodeName = nodeName;
        unit.textValue = textValue;
        unit.attributes.putAll(attributes);
        value.put(generateNodeName(nodeName, 0), unit);
        callback.onInsert();
    }

    public void add(String nodeName, Node node) {
        value.put(generateNodeName(nodeName, 0), new Unit(node));
        callback.onInsert();
    }

    public void add(String nodeName, Unit unit) {
        value.put(generateNodeName(nodeName, 0), unit);
        callback.onInsert();
    }

    public void remove(String nodeName) {
        value.remove(nodeName);
        callback.onRemove();
    }

    public void removeAttribute(String attribute) {
        this.attributes.remove(attribute);
        callback.onRemove();
    }

    public void change(String textValue) {
        value.clear();
        this.textValue = textValue;
        callback.onChange();
    }

    public void change(Node node) {
        value.clear();
        attributes.clear();
        textValue = null;
        nodeName = node.getNodeName();
        NamedNodeMap attrs = node.getAttributes();
        int numAttrs = attrs.getLength();
        for (int i = 0; i < numAttrs; i++) {
            Node attr = attrs.item(i);
            attributes.put(attr.getNodeName(), attr.getNodeValue());
        }
        iterateNodes(node);
        callback.onChange();
    }

    public void change(HashMap<String, String> attributes) {
        this.attributes.clear();
        this.attributes.putAll(attributes);
        callback.onChange();
    }

    private String generateNodeName(String name, int index) {
        String nameTry = name;
        if (index > 0) nameTry = nameTry + "*" + index;
        if (value.get(nameTry) != null) return generateNodeName(name, ++index);
        else return nameTry;
    }

    private void iterateNodes(Node node) {
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                value.put(generateNodeName(currentNode.getNodeName(), 0), new Unit(currentNode));
            }
        }
        if (nodeList.getLength() == 1) textValue = node.getTextContent().trim();
    }

    public String getTextValue() {
        if (value.size() == 0) return textValue;
        else throw new RuntimeException("More configuration level exist");
    }
    public boolean has(String key) {
        String delimiter = "/";
        String[] keys = key.split(delimiter);
        String[] modifiedArray = Arrays.copyOfRange(keys, 1, keys.length);
        Unit unit = get(keys[0]);
        for (String str:modifiedArray) {
            unit = unit.get(str);
        }
        return unit != null;
    }

    public Unit get(String key) {
        return value.get(key);
    }
    public HashMap<String, Unit> getValue(){
        return value;
    }
    public HashMap<String, String> getAttributes() {
        return attributes;
    }
    public boolean equal(Unit unit) {
        return toString().equals(unit.toString());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("<" + nodeName);
        for (Map.Entry<String, String> a : attributes.entrySet()) {
            str.append(" ").append(a.getKey()).append("='").append(a.getValue()).append("'");
        }
        str.append(">");
        if (textValue != null && value.size() ==0) str.append(textValue);
        for (Map.Entry<String, Unit> b : value.entrySet()) {
            str.append(b.getValue().toString());
        }
        str.append("</").append(nodeName).append(">");
        return str.toString();
    }
}

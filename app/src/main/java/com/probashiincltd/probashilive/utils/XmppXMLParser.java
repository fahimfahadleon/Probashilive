package com.probashiincltd.probashilive.utils;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.NamespaceSupport;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XmppXMLParser {
    Document doc;
    XPath xpath;
    String xml;

    public static XmppXMLParser load(String xml) {
        try {
            XmppXMLParser xmppXMLParser = new XmppXMLParser();
            xmppXMLParser.xml = xml;
            //Build DOM
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false); // never forget this!

            DocumentBuilder builder = factory.newDocumentBuilder();
            NamespaceSupport support = new NamespaceSupport();
            support.pushContext();
            support.declarePrefix("xmpp", "http://schema.intuit.com/finance/v3");

            //Create XPath

            XPathFactory xpathfactory = XPathFactory.newInstance();
            xmppXMLParser.xpath = xpathfactory.newXPath();
            xmppXMLParser.xpath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    if ("xmpp".equals(prefix)) {
                        Log.e("test", "prefix");
                        return "http://schema.intuit.com/finance/v3";
                    }
                    return "";
                }

                @Override
                public String getPrefix(String namespaceURI) {
                    return null;
                }

                @Override
                public Iterator getPrefixes(String namespaceURI) {
                    return null;
                }
            });

            xmppXMLParser.doc = builder.parse(new
                    ByteArrayInputStream(xmppXMLParser.xml.getBytes()));
            return xmppXMLParser;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public ArrayList<String> getText(String regex) {
        try {
            ArrayList<String> out = new ArrayList<>();
            XPathExpression expr = xpath.compile(regex+"/text()");
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                if(nodes.item(i).getNodeValue()!=null){
                    out.add(nodes.item(i).getNodeValue());
                }
            }
            return out;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public ArrayList<String> getAttribute(String regex) {
        try {
            ArrayList<String> out = new ArrayList<>();
            XPathExpression expr = xpath.compile(regex);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                if(nodes.item(i).getNodeValue()!=null){
                    out.add(nodes.item(i).getNodeValue());
                }
            }
            return out;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    //   HashMap<String,String> m = parser.getAttributeMap("/iq/services/service["+i+"]/@*"); get all attribute from a node
    public HashMap<String,String> getMap(String regex) throws
            XPathExpressionException {
        HashMap<String,String> map = new HashMap<>();
        XPathExpression expr = xpath.compile(regex);
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;


        XPathExpression expr1 = xpath.compile(regex+"/text()");
        Object result1 = expr1.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes1 = (NodeList) result1;
        for (int i = 0; i < nodes.getLength(); i++) {
            map.put(nodes.item(i).getNodeName(),nodes1.item(i).getNodeValue() == null?"null":nodes1.item(i).getNodeValue());
        }
        return map;
    }

    public NodeList getMapNodes(String regex) throws
            XPathExpressionException {
        HashMap<String,String> map = new HashMap<>();
        XPathExpression expr = xpath.compile(regex);
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        return (NodeList) result;
    }
    public HashMap<String,String> getAttributeMap(String regex){

        try {
            HashMap<String,String> map = new HashMap<>();
            XPathExpression expr = xpath.compile(regex);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            NodeList nodes1 = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                map.put(nodes.item(i).getNodeName(),nodes1.item(i).getNodeValue() == null?"null":nodes1.item(i).getNodeValue());
            }
            return map;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }

    }
}
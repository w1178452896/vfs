package com.wyf.utils;

import org.dom4j.Document;

/**
 * @author wangyaofeng
 * @projectName vfs
 * @description:
 * @date 2019/8/21 19:52
 */
public class XmlUtil {

    /**
     *   org.w3c.dom.Document   ->   org.dom4j.Document
     *   @param   doc   Document(org.w3c.dom.Document)
     *   @return   Document
     */
    public   static   Document   parse(org.w3c.dom.Document   doc)   throws   Exception   {
        if   (doc   ==   null)   {
            return   (null);
        }
        org.dom4j.io.DOMReader   xmlReader   =   new   org.dom4j.io.DOMReader();
        return   (xmlReader.read(doc));
    }

    /**
     *   org.dom4j.Document   ->   org.w3c.dom.Document
     *   @param   doc   Document(org.dom4j.Document)
     *   @throws   Exception
     *   @return   Document
     */
    public   static   org.w3c.dom.Document   parse(Document doc)   throws   Exception   {
        if   (doc   ==   null)   {
            return   (null);
        }
        java.io.StringReader   reader   =   new   java.io.StringReader(doc.asXML());
        org.xml.sax.InputSource   source   =   new   org.xml.sax.InputSource(reader);
        javax.xml.parsers.DocumentBuilderFactory   documentBuilderFactory   =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder   documentBuilder   =   documentBuilderFactory.
                newDocumentBuilder();
        return   (documentBuilder.parse(source));
    }

}

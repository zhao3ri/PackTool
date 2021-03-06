package com.qinglan.tool.xml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XmlTool {
    /**
     * @param xmlStr 字符串
     * @param c      对象Class类型
     * @return 对象实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T xml2Object(String xmlStr, Class<T> c) {
//        try {
//            JAXBContext context = JAXBContext.newInstance(c);
//            Unmarshaller unmarshaller = context.createUnmarshaller();
//            T t = (T) unmarshaller.unmarshal(new StringReader(xmlStr));
//            return t;
//        } catch (JAXBException e) {
//            e.printStackTrace();
//            return null;
//        }
        ObjectMapper xmlMapper = new XmlMapper();
        try {
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            T t = xmlMapper.readValue(xmlStr, c);
            return t;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param object 对象
     * @return 返回xmlStr
     */
    public static String object2Xml(Object object) {
        try {
            StringWriter writer = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshal = context.createMarshaller();

            marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化输出
            marshal.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// 编码格式,默认为utf-8
            marshal.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xml头信息
            marshal.setProperty("jaxb.encoding", "utf-8");
            marshal.marshal(object, writer);

            return new String(writer.getBuffer());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Document createDocument(String xmlPath) {
        Document document = null;
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            FileInputStream xmlInputStream = new FileInputStream(xmlPath);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            document = builder.parse(xmlInputStream);
            document.normalize();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static NodeList getDocumentRootNodeList(Document document) {
        Element root = document.getDocumentElement();
        NodeList stuNodeList = root.getChildNodes();
        return stuNodeList;
    }

    public static void addElement(Document document, Element addEle) {
        Element root = document.getDocumentElement();
        root.appendChild(addEle);
    }

    public static void saveXml(Document document, String xmlPath) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new FileOutputStream(xmlPath));
            transformer.transform(domSource, streamResult);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
}

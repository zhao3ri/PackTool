package com.qinglan.tool.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Channel {
    @JacksonXmlProperty(localName = "id", isAttribute = true)
    int id;

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    String name;

    @JacksonXmlProperty(localName = "class-name")
    String clazz;

    @JacksonXmlProperty(localName = "filter")
    Filter filter;

    public int getId() {
        return id;
    }

    @XmlAttribute(name = "id")
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    @XmlElement(name = "class-name")
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Filter getFilter() {
        return filter;
    }

    @XmlElement(name = "filter")
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Channel) obj).id;
    }

}

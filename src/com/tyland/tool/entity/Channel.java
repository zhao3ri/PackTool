package com.tyland.tool.entity;

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

    @JacksonXmlProperty(localName = "launcher")
    String launcher;

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

    public String getLauncher() {
        return launcher;
    }

    @XmlElement(name = "launcher")
    public void setLauncher(String launcher) {
        this.launcher = launcher;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Channel) obj).id;
    }

    @Override
    public String toString() {
        return "Channel id=" + getId() + ", name=" + getName();
    }
}

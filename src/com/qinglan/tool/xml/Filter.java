package com.qinglan.tool.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

public class Filter {
    @JacksonXmlElementWrapper(localName = "package-list")
    @JacksonXmlProperty(localName = "package")
    List<Package> packageNameList;

    @JacksonXmlElementWrapper(localName = "libs")
    @JacksonXmlProperty(localName = "lib")
    List<String> libsName;

    @JacksonXmlElementWrapper(localName = "res")
    @JacksonXmlProperty(localName = "name")
    List<String> resName;

    @JacksonXmlElementWrapper(localName = "assets")
    @JacksonXmlProperty(localName = "asset")
    List<String> assets;

    public List<Package> getPackageNameList() {
        return packageNameList;
    }

    @XmlElementWrapper(name = "package-list")
    @XmlElement(name = "package")
    public void setPackageNameList(List<Package> packageNameList) {
        this.packageNameList = packageNameList;
    }

    public List<String> getLibsName() {
        return libsName;
    }

    @XmlElementWrapper(name = "libs")
    @XmlElement(name = "lib")
    public void setLibsName(List<String> libsName) {
        this.libsName = libsName;
    }

    public List<String> getResNames() {
        return resName;
    }

    @XmlElementWrapper(name = "res")
    @XmlElement(name = "name")
    public void setResName(List<String> resName) {
        this.resName = resName;
    }

    public List<String> getAssets() {
        return assets;
    }

    @XmlElementWrapper(name = "assets")
    @XmlElement(name = "asset")
    public void setAssets(List<String> assets) {
        this.assets = assets;
    }

    public static class Package {
        @JacksonXmlProperty(localName = "base-name", isAttribute = true)
        String name;

        public String getName() {
            return name;
        }

        @XmlAttribute(name = "base-name")
        public void setName(String name) {
            this.name = name;
        }
    }
}

package com.tyland.tool.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@JacksonXmlRootElement(localName = "channel-config")
public class ChannelList {
    @JacksonXmlElementWrapper(localName = "channel-list")
    @JacksonXmlProperty(localName = "chanel")
    List<Channel> channelList;

    @XmlElementWrapper(name = "channel-list")
    @XmlElement(name = "channel")
    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }
}

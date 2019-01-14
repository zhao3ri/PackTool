package com.qinglan.tool.entity;

public class SignInfo {
    private String path;
    private String passwords;
    private String alias;

    public SignInfo() {
    }

    public SignInfo(String path, String pass, String alias) {
        this.path = path;
        this.passwords = pass;
        this.alias = alias;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

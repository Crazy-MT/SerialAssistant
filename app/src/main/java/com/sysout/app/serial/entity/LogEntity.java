package com.sysout.app.serial.entity;

public class LogEntity {

    private String name;

    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "LogEntity{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}

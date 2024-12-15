package com.example.bazadanychczycos;

public class Shortcut {
    private String name;
    private int icon;

    public Shortcut(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
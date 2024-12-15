package com.example.bazadanychczycos;

public class Item {

    private int id;
    private String name;
    private float price;
    private String description;
    private String picture;
    private String category;

    public Item(int id, String name, float price, String description, String picture, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.picture = picture;
        this.category = category;
    }

    public Item(int id, String name, float price, String description, String picture) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name; // Umożliwia wyświetlanie nazw produktów w Spinnerze
    }
}

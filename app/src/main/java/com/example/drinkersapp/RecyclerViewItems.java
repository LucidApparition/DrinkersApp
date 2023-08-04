package com.example.drinkersapp;

/*
 * Template for recyclerview list items where all product data is needed
 * Used for Customer home
 */
public class RecyclerViewItems {

    // these are the field names in the database
    String name, image, abv, size, subType, type, category;
    long price;

    // template constructors
    public RecyclerViewItems(){}

    public RecyclerViewItems(String name, long price, String image, String abv, String size, String type, String subType, String category) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.abv = abv;
        this.size = size;
        this.type = type;
        this.subType = subType;
        this.category = category;
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAbv() {
        return abv;
    }

    public void setAbv(String abv) {
        this.abv = abv;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}

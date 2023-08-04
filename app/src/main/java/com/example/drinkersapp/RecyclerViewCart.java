package com.example.drinkersapp;
/*
 * Template for recyclerview list items where only product name, price, and image are needed.
 * Used for Customer cart, Store owner view order
 */
public class RecyclerViewCart {

    // these are the field names in the database
    String name, image;
    long price;

    // template constructors
    public RecyclerViewCart(){}

    public RecyclerViewCart(String name, long price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}

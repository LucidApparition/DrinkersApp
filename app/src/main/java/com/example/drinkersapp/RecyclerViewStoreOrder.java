package com.example.drinkersapp;

/*
 * Template for recyclerview list items where all product data is needed
 * Used for Store Owner orders and Driver accept/decline UI
 */
public class RecyclerViewStoreOrder {

    // this is the field name in the database
    String uid;

    // template constructors
    public RecyclerViewStoreOrder(){}

    public RecyclerViewStoreOrder(String uid){
        this.uid = uid;
    }

    // getter and setter
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}

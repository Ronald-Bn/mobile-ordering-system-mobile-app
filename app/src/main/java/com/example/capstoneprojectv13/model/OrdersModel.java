package com.example.capstoneprojectv13.model;

public class OrdersModel {

    public OrdersModel(){

    }

    public OrdersModel(String uid, String key,String name, int price, String products, String address, String zipcode, String date, String status) {
        this.uid = key;
        this.key = key;
        this.name = name;
        this.price = price;
        this.products = products;
        this.address = address;
        this.zipcode = zipcode;
        this.date = date;
        this.status = status;
    }

    private String uid;
    private String key;
    private String name;
    private int price;
    private String products;
    private String address;
    private String zipcode;
    private String date;
    private String status;
    private String cartId;


    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

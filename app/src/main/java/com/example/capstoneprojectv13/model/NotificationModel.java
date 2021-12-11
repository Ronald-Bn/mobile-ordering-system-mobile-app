package com.example.capstoneprojectv13.model;

public class NotificationModel  {

    private String date;
    private boolean notify = false;
    private String cartId = "0";
    private String ordersid = "0";
    private String userid = "0";

    public NotificationModel(){

    }

    public NotificationModel(String date, boolean notify, String cartId, String ordersid, String userid) {
        this.date = date;
        this.notify = notify;
        this.cartId = cartId;
        this.ordersid = ordersid;
        this.userid = userid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getOrdersid() {
        return ordersid;
    }

    public void setOrdersid(String ordersid) {
        this.ordersid = ordersid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

package com.example.capstoneprojectv13.model;

public class Products {

    public Products(){

    }

    public Products(String name, String price, String category, String description, String status, String image) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.status = status;
        this.image = image;
    }


    private String name;
    private String price;
    private String category;
    private String description;
    private String status;
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
package com.example.capstoneprojectv13.model;

public class Products {

    public Products(){

    }



    public Products(String name, String price, String category, String description, String status, String image, int five_star, int four_star, int three_star, int two_star, int one_star) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.status = status;
        this.image = image;
        this.five_star = five_star;
        this.four_star = four_star;
        this.three_star = three_star;
        this.two_star = two_star;
        this.one_star = one_star;
    }

    private String name;
    private String price;
    private String category;
    private String description;
    private String status;
    private String image;
    private int five_star;
    private int four_star;
    private int three_star;
    private int two_star;
    private int one_star;

    public int getFive_star() {
        return five_star;
    }

    public void setFive_star(int five_star) {
        this.five_star = five_star;
    }

    public int getFour_star() {
        return four_star;
    }

    public void setFour_star(int four_star) {
        this.four_star = four_star;
    }

    public int getThree_star() {
        return three_star;
    }

    public void setThree_star(int three_star) {
        this.three_star = three_star;
    }

    public int getTwo_star() {
        return two_star;
    }

    public void setTwo_star(int two_star) {
        this.two_star = two_star;
    }

    public int getOne_star() {
        return one_star;
    }

    public void setOne_star(int one_star) {
        this.one_star = one_star;
    }

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
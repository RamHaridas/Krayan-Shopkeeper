package com.oceans.shopowner.ui.home;

public class HomeItem {
    int img;
    String name;
    double cost;
    boolean inStock;
    boolean isExpanded;

    public HomeItem(int img, String name, double cost, boolean inStock) {
        this.img = img;
        this.name = name;
        this.cost = cost;
        this.inStock = inStock;
        isExpanded=false;
    }
    public int getImg() {
        return img;
    }
    public void setImg(int img) {
        this.img = img;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    public boolean isInStock() {
        return inStock;
    }
    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
    public boolean isExpanded() {
        return isExpanded;
    }
    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

}

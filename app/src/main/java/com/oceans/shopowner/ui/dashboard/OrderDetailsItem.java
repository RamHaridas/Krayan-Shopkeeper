package com.oceans.shopowner.ui.dashboard;

public class OrderDetailsItem {
    String name,detail;
    int quantity;

    public OrderDetailsItem(String name, String detail, int quantity) {
        this.name = name;
        this.detail = detail;
        this.quantity = quantity;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

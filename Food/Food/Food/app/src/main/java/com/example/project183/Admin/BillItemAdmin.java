package com.example.project183.Admin;

public class BillItemAdmin {
    private String id;       // ID của hóa đơn
    private String itemName; // Tên mặt hàng
    private String status;    // Trạng thái hóa đơn

    public BillItemAdmin(String id, String itemName, String status) {
        this.id = id;
        this.itemName = itemName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


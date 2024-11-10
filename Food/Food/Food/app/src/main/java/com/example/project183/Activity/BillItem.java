package com.example.project183.Activity;
public class BillItem {
    private String billId;
    private String bill;
    private String status; // Thêm trường trạng thái

    public BillItem(String billId, String bill, String status) { // Cập nhật constructor
        this.billId = billId;
        this.bill = bill;
        this.status = status; // Khởi tạo trạng thái
    }

    public String getBillId() {
        return billId;
    }

    public String getBill() {
        return bill;
    }

    public String getStatus() { // Thêm phương thức để lấy trạng thái
        return status;
    }
}

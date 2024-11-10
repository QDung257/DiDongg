package com.example.project183.Profile;

public class User {
    public String username;  // Đổi tên trường từ name thành username
    public String email;
    public int balance; // Có thể là số dư tài khoản hoặc các dữ liệu khác

    // Constructor mặc định bắt buộc cho Firebase
    public User() {}

    // Constructor để khởi tạo đối tượng người dùng
    public User(String username, String email, int balance) {
        this.username = username;
        this.email = email;
        this.balance = balance;
    }
}

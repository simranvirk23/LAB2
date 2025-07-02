package com.example;

public class TestConnection {
    public static void main(String[] args) {
        try {
            var conn = DBUtil.getConnection();
            System.out.println("Connected to DB !");
            conn.close();
        } catch (Exception e) {
            System.out.println("Failed to connect!");
            e.printStackTrace();
        }
    }
}
package com.example.inventoryapp.model;

    public class Item {
        private int id;
        private String code;
        private String name;
        private int stock;
        private double price;

        public Item() {
            // Default constructor
        }

        public Item(int id, String code, String name, int stock, double price) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.stock = stock;
            this.price = price;
        }

        // --- Getters and Setters ---
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }


package com.example.foodordering.dto.response;

public class MonthlySalesData {
    private String month;
    private double revenue;

    public MonthlySalesData() {

    }
    // Constructor, getters, and setters
    public MonthlySalesData(String month, double revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return "Month: " + month + ". Revenue: " + revenue;
    }
}


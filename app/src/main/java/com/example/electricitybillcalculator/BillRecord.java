package com.example.electricitybillcalculator;

public class BillRecord {
    private int id;
    private String month;
    private int kWh;
    private double totalCharges;
    private double finalCost;
    private int rebatePercent;

    public BillRecord() {}

    public BillRecord(String month, int kWh, double totalCharges, double finalCost, int rebatePercent) {
        this.month = month;
        this.kWh = kWh;
        this.totalCharges = totalCharges;
        this.finalCost = finalCost;
        this.rebatePercent = rebatePercent;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getKWh() { return kWh; }
    public void setKWh(int kWh) { this.kWh = kWh; }

    public double getTotalCharges() { return totalCharges; }
    public void setTotalCharges(double totalCharges) { this.totalCharges = totalCharges; }

    public double getFinalCost() { return finalCost; }
    public void setFinalCost(double finalCost) { this.finalCost = finalCost; }

    public int getRebatePercent() { return rebatePercent; }
    public void setRebatePercent(int rebatePercent) { this.rebatePercent = rebatePercent; }
}

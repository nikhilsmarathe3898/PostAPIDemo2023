package com.pojo;

/**
 * <h>FilteredPriceResponse</h>
 * Created by Ali on 2/2/2018.
 */

public class FilteredPriceResponse
{

    private boolean isMin,isMax;
    private double minPrice,MaxPrice,minAmount,maxAmount;

    public boolean isMin() {
        return isMin;
    }

    public void setMin(boolean min) {
        isMin = min;
    }

    public boolean isMax() {
        return isMax;
    }

    public void setMax(boolean max) {
        isMax = max;
    }

    public double isMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double isMaxPrice() {
        return MaxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        MaxPrice = maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return MaxPrice;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }
}

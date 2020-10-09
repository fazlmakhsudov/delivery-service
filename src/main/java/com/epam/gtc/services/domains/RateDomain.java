package com.epam.gtc.services.domains;

import java.io.Serializable;


public class RateDomain implements Serializable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2l; // need to redefine'

    private int id;
    private String name;
    private double maxWeight;
    private double maxLength;
    private double maxWidth;
    private double maxHeight;
    private double maxDistance;
    private double cost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public double getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(double maxLength) {
        this.maxLength = maxLength;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "RateDomain{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxWeight=" + maxWeight +
                ", maxLength=" + maxLength +
                ", maxWidth=" + maxWidth +
                ", maxHeight=" + maxHeight +
                ", maxDistance=" + maxDistance +
                ", cost=" + cost +
                '}';
    }
}

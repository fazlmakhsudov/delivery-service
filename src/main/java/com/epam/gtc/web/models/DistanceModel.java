package com.epam.gtc.web.models;

import java.io.Serializable;

/**
 * Distance model
 *
 * @author Fazliddin Makhsudov
 */
public class DistanceModel implements Serializable {

    private static final long serialVersionUID = 2l; // need to redefine

    private int id;
    private int fromCityId;
    private int toCityId;
    private double distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromCityId() {
        return fromCityId;
    }

    public void setFromCityId(int fromCityId) {
        this.fromCityId = fromCityId;
    }

    public int getToCityId() {
        return toCityId;
    }

    public void setToCityId(int toCityId) {
        this.toCityId = toCityId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "DistanceModel{" +
                "id=" + id +
                ", fromCityId=" + fromCityId +
                ", toCityId=" + toCityId +
                ", distance=" + distance +
                '}';
    }
}

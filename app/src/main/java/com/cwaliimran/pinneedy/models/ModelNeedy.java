package com.cwaliimran.pinneedy.models;

import java.io.Serializable;
import java.util.List;

public class ModelNeedy implements Serializable {
    String id, needyName, address, phoneNumber, state, notes;
    Double latitude, longitude;
    List<Float> ratingArray;

    public ModelNeedy() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNeedyName() {
        return needyName;
    }

    public void setNeedyName(String needyName) {
        this.needyName = needyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<Float> getRatingArray() {
        return ratingArray;
    }

    public void setRatingArray(List<Float> ratingArray) {
        this.ratingArray = ratingArray;
    }
}

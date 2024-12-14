package com.furkansabuncu.javatravelbook.model;

import java.util.Date;

public class Comment {
    public Comment(double longitude, double latitude, String comment, String username,String downloadUrl,Date timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.comment = comment;
        this.username = username;
        this.downloadUrl=downloadUrl;
        this.timestamp=timestamp;

    }
    public Comment() {}
    public Date timestamp;

    public double longitude;
    public double latitude;
    public String comment;
    public String username;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String downloadUrl;

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getComment() {
        return comment;
    }

    public String getUsername() {
        return username;
    }



}

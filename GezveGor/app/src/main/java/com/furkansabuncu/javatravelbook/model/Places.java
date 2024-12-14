package com.furkansabuncu.javatravelbook.model;
import android.os.Parcel;
import android.os.Parcelable;

public class Places implements Parcelable {
    public String place;
    public String downloadUrl;
    public String city;

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String comment;
    public double longitude;
    public double latitude;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double distance;

    public Places(String place, String downloadUrl, String city, double longitude, double latitude, String comment) {
        this.place = place;
        this.downloadUrl = downloadUrl;
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        this.comment = comment;
    }
    public Places(String place, String downloadUrl, String city, double longitude, double latitude, String comment,double distance) {
        this.place = place;
        this.downloadUrl = downloadUrl;
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        this.comment = comment;
        this.distance=distance;
    }

    protected Places(Parcel in) {
        place = in.readString();
        downloadUrl = in.readString();
        city = in.readString();
        comment = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        distance = in.readDouble(); // distance alanını da okuyun
    }

    public static final Creator<Places> CREATOR = new Creator<Places>() {
        @Override
        public Places createFromParcel(Parcel in) {
            return new Places(in);
        }

        @Override
        public Places[] newArray(int size) {
            return new Places[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(place);
        dest.writeString(downloadUrl);
        dest.writeString(city);
        dest.writeString(comment);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeDouble(distance); // distance alanını da yazın
    }
}
package com.furkansabuncu.javatravelbook.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
    public String comment;
    public String downloadUrl;
    public String title;
    public String fullName;
    public String email;

    public Post(String comment, String downloadUrl, String title, String fullName, String email) {
        this.comment = comment;
        this.downloadUrl = downloadUrl;
        this.title = title;
        this.fullName = fullName;
        this.email = email;
    }

    protected Post(Parcel in) {
        comment = in.readString();
        downloadUrl = in.readString();
        title = in.readString();
        fullName = in.readString();
        email = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeString(downloadUrl);
        dest.writeString(title);
        dest.writeString(fullName);
        dest.writeString(email);
    }
}

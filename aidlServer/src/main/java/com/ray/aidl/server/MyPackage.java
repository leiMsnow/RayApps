package com.ray.aidl.server;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangleilei on 4/16/16.
 */
public class MyPackage implements Parcelable {

    private String sunglasses;
    private String pants;
    private String slippers;
    private String t_shirt;


    protected MyPackage(Parcel in) {
        sunglasses = in.readString();
        pants = in.readString();
        slippers = in.readString();
        t_shirt = in.readString();
    }

    public static final Creator<MyPackage> CREATOR = new Creator<MyPackage>() {
        @Override
        public MyPackage createFromParcel(Parcel in) {
            return new MyPackage(in);
        }

        @Override
        public MyPackage[] newArray(int size) {
            return new MyPackage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sunglasses);
        dest.writeString(pants);
        dest.writeString(slippers);
        dest.writeString(t_shirt);
    }

    @Override
    public String toString() {
        return "您的行李清单：{" +
                "sunglasses='" + sunglasses + '\'' +
                ", pants='" + pants + '\'' +
                ", slippers='" + slippers + '\'' +
                ", t_shirt='" + t_shirt + '\'' +
                '}';
    }
}

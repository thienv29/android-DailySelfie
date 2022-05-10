package com.android.devthien.dailyselfie;

import android.graphics.Bitmap;

public class ImageModel {
    Bitmap photo;
    String name;
    String des;

    public ImageModel() {
    }

    public ImageModel(Bitmap photo, String name, String des) {
        this.photo = photo;
        this.name = name;
        this.des = des;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

}

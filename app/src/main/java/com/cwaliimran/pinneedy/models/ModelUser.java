package com.cwaliimran.pinneedy.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.cwaliimran.pinneedy.R;
import com.bumptech.glide.Glide;

public class ModelUser {
    //data binding

    @BindingAdapter("android:loadUserImage")
    public static void loadUserImage(ImageView imageView, String imageUrl) {
        Glide.with(imageView).load(imageUrl).placeholder(R.drawable.profile_placeholder).into(imageView);
    }

    String id, name, email, image;

    public ModelUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

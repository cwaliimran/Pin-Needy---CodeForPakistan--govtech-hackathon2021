package com.cwaliimran.pinneedy.activities;

import android.os.Bundle;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.databinding.ActivityProfileBinding;
import com.cwaliimran.pinneedy.utils.MyApp;

public class ProfileActivity extends BaseActivity {
    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        modelUser = MyApp.getAppUser();
        binding.setData(modelUser);
    }
}
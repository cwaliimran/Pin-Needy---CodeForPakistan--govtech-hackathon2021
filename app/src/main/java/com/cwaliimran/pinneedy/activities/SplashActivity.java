package com.cwaliimran.pinneedy.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.R;
import com.cwaliimran.pinneedy.databinding.ActivitySplashBinding;
import com.cwaliimran.pinneedy.utils.GlobalClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends BaseActivity {
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        fullScreen(ContextCompat.getColor(context, R.color.transparent));
        mAuth = FirebaseAuth.getInstance();
        if (!GlobalClass.isOnline(context)) {
            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Alert!")
                    .setMessage("No Internet Connection, check your settings")
                    .setPositiveButton("Close", (dialog, which) -> finish())
                    .show();
        } else {
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "onStart: user logged in" + currentUser.getUid());
                gotoActivityFinishAll(HomeActivity.class);
            } else {
                Log.d(TAG, "onStart: user not logged in");
                gotoActivityFinishAll(LoginActivity.class);
            }
        }
    }

}
package com.cwaliimran.pinneedy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.cwaliimran.pinneedy.models.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    public String TAG = "response";
    public Context context;
    public Intent intent;
    public String name, email, password;
    public FirebaseAuth mAuth;
    public FirebaseFirestore db;
    public ModelUser modelUser;
    public Bundle bundle;
    public String tag;
    public String randomId;

    protected void gotoActivity(Class<?> activity) {
        intent = new Intent(context, activity);
        startActivity(intent);
    }

    //starts new activity and finishes current activity
    protected void gotoActivityFinish(Class<?> activity) {
        intent = new Intent(context, activity);
        startActivity(intent);
        finish();
    }

    //starts new activity and finishes all previous activities
    protected void gotoActivityFinishAll(Class<?> activity) {
        intent = new Intent(context, activity);
        startActivity(intent);
        finishAffinity();
    }

    protected void fullScreen(int colorRes) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(colorRes);
    }

    public void showProgress(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideProgress(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
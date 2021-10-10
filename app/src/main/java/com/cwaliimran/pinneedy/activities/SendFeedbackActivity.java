package com.cwaliimran.pinneedy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.databinding.ActivitySendFeedbackBinding;
import com.cwaliimran.pinneedy.utils.AppConstants;
import com.cwaliimran.pinneedy.utils.GlobalClass;
import com.cwaliimran.pinneedy.utils.MyApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SendFeedbackActivity extends BaseActivity {
    ActivitySendFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Feedback");
        context = this;
        db = FirebaseFirestore.getInstance();
        randomId = GlobalClass.randomId();
        modelUser = MyApp.getAppUser();
    }

    public void btnClick(View view) {
        if (!binding.etMessage.getText().toString().equals("")) {
            GlobalClass.hideKeyboard(SendFeedbackActivity.this);
            showProgress(binding.progressBar);
            Map<String, Object> map = new HashMap<>();
            map.put("id", randomId);
            map.put("email", modelUser.getEmail());
            map.put("userId", modelUser.getId());
            map.put("message", binding.etMessage.getText().toString());
            db.collection(AppConstants.TBL_FEEDBACKS).document(randomId).set(map).addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Feedback sent.", Toast.LENGTH_SHORT).show();
                hideProgress(binding.progressBar);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                hideProgress(binding.progressBar);
            });
        } else {
            binding.etMessage.setError("Enter message");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
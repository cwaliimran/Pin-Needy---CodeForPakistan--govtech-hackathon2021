package com.cwaliimran.pinneedy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.databinding.ActivityForgotPasswordBinding;
import com.cwaliimran.pinneedy.utils.GlobalClass;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends BaseActivity {
    ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot password");
        context = this;
        mAuth = FirebaseAuth.getInstance();
    }

    public void btnClick(View view) {
        if (checkValidation()) {
            GlobalClass.hideKeyboard(ForgotPasswordActivity.this);
            showProgress(binding.progressBar);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Email has been sent successfully.", Toast.LENGTH_LONG).show();
                            hideProgress(binding.progressBar);
                            finish();
                        } else {
                            hideProgress(binding.progressBar);
                            Toast.makeText(context, "" + Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    public boolean checkValidation() {
        email = binding.etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            binding.etEmail.setError("Enter email");
            binding.etEmail.requestFocus();
            return false;
        }
        if (GlobalClass.isValidEmail(email)) {
            binding.etEmail.setError("Enter valid email");
            binding.etEmail.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
package com.cwaliimran.pinneedy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.databinding.ActivitySignupBinding;
import com.cwaliimran.pinneedy.models.ModelUser;
import com.cwaliimran.pinneedy.utils.AppConstants;
import com.cwaliimran.pinneedy.utils.GlobalClass;
import com.cwaliimran.pinneedy.utils.MyApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends BaseActivity {
    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign up");
        context = this;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void btnClick(View view) {
        tag = view.getTag().toString();
        if (tag.equals("fPass")) {
            gotoActivity(ForgotPasswordActivity.class);
        } else {
            //signup
            if (checkValidation()) {
                //check if user is online
                if (GlobalClass.isOnline(context)) {
                    //signup user
                    GlobalClass.hideKeyboard(SignupActivity.this);
                    showProgress(binding.progressBar);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                Log.d(TAG, "onComplete: " + user_id);
                                Map<String, Object> map = new HashMap<>();
                                map.put("id", user_id);
                                map.put("name", name);
                                map.put("email", email);
                                db.collection(AppConstants.TBL_USERS).document(user_id).set(map).addOnSuccessListener(aVoid -> {
                                    getCurrentUser(user_id);
                                })
                                        .addOnFailureListener(e -> {
                                            Log.d(AppConstants.TAG, "e: " + e);
                                            hideProgress(binding.progressBar);
                                            Toast.makeText(context, "Authentication failed." + e,
                                                    Toast.LENGTH_LONG).show();
                                            hideProgress(binding.progressBar);
                                        });
                            }
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
                        hideProgress(binding.progressBar);
                    });
                } else {
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Alert!")
                            .setMessage("No Internet Connection, check your settings")
                            .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            }
        }

    }

    public boolean checkValidation() {
        name = binding.etName.getText().toString().trim();
        email = binding.etEmail.getText().toString().trim();
        password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();
        if (name.isEmpty() || name.length() < 3) {
            binding.etName.setError("Enter valid name");
            binding.etName.requestFocus();
            return false;
        }
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
        if (password.isEmpty()) {
            binding.etPassword.setError("Enter password");
            binding.etPassword.requestFocus();
            return false;
        }
        return true;
    }

    public void getCurrentUser(String user_id) {
        db.collection(AppConstants.TBL_USERS)
                .document(user_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        String json = new Gson().toJson(document.toObject(ModelUser.class));
                        MyApp.shared.setString(AppConstants.CURRENT_USER, json);
                        hideProgress(binding.progressBar);
                        startActivity(new Intent(context, HomeActivity.class));
                    }
                }).addOnFailureListener(e -> {
            Toast.makeText(context, "Try logging in.", Toast.LENGTH_SHORT).show();
            hideProgress(binding.progressBar);
            gotoActivityFinishAll(LoginActivity.class);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
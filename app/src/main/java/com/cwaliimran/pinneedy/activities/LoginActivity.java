package com.cwaliimran.pinneedy.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.databinding.ActivityLoginBinding;
import com.cwaliimran.pinneedy.models.ModelUser;
import com.cwaliimran.pinneedy.utils.AppConstants;
import com.cwaliimran.pinneedy.utils.GlobalClass;
import com.cwaliimran.pinneedy.utils.MyApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Objects;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void btnClick(View view) {
        tag = view.getTag().toString();
        if (tag.equals("fPass")) {
            gotoActivity(ForgotPasswordActivity.class);
        }
        else if (tag.equals("signup")) {
            gotoActivity(SignupActivity.class);
        } else {
            //login
            if (checkValidation()) {
                //check if user is online
                if (GlobalClass.isOnline(context)) {
                    //check if user exist and login
                    GlobalClass.hideKeyboard(LoginActivity.this);
                    showProgress(binding.progressBar);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    getCurrentUser(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                                } else {
                                    // If sign in fails, display a message to the user.
                                    hideProgress(binding.progressBar);
                                    Toast.makeText(LoginActivity.this, "Login failed " + task.getException(),
                                            Toast.LENGTH_LONG).show();
                                }
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
        email = binding.etEmail.getText().toString().trim();
        password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();

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
                        Toast.makeText(LoginActivity.this, "Login successful",
                                Toast.LENGTH_SHORT).show();
                        hideProgress(binding.progressBar);
                        gotoActivityFinishAll(HomeActivity.class);

                    }
                }).addOnFailureListener(e -> {
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        });
    }
}
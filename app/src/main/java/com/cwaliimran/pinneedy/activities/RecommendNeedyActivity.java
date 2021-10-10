package com.cwaliimran.pinneedy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.databinding.ActivityRecommendNeedyBinding;
import com.cwaliimran.pinneedy.utils.AppConstants;
import com.cwaliimran.pinneedy.utils.GlobalClass;
import com.cwaliimran.pinneedy.utils.MyApp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RecommendNeedyActivity extends BaseActivity {
    ActivityRecommendNeedyBinding binding;
    String needyName, state, phoneNumber, address, notes;
    double latitude;
    double longitude;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecommendNeedyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recommend Needy");
        context = this;
        db = FirebaseFirestore.getInstance();
        randomId = GlobalClass.randomId();
        modelUser = MyApp.getAppUser();

        binding.etAddress.setOnClickListener(v -> {
            Intent i = new Intent(this, SelectAddressActivity.class);
            startActivityForResult(i, SECOND_ACTIVITY_REQUEST_CODE);
        });

    }

    public void btnClick(View view) {
        if (checkValidation()) {
            GlobalClass.hideKeyboard(RecommendNeedyActivity.this);
            showProgress(binding.progressBar);
            Map<String, Object> map = new HashMap<>();
            map.put("id", randomId);
            map.put("needyName", needyName);
            map.put("address", address);
            map.put("email", modelUser.getEmail());
            map.put("latitude", latitude);
            map.put("longitude", longitude);
            map.put("notes", notes);
            map.put("phoneNumber", phoneNumber);
            map.put("userId", modelUser.getId());
            map.put("state", state);
            map.put("createdAt", FieldValue.serverTimestamp());
            db.collection(AppConstants.TBL_NEEDIES).document(randomId).set(map).addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Needy, needs request sent.", Toast.LENGTH_LONG).show();
                hideProgress(binding.progressBar);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
                hideProgress(binding.progressBar);
            });
        }
    }

    private boolean checkValidation() {
        needyName = binding.etneedyName.getText().toString();
      //  state = binding.etState.getText().toString();
        phoneNumber = binding.etPhoneNumber.getText().toString();
        address = binding.etAddress.getText().toString();
        notes = binding.etNotes.getText().toString();
        if (needyName.equals("") || phoneNumber.equals("") || address.equals("")) {
            Toast.makeText(context, "Fill out all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK){
                if (data != null) {
                    address = data.getStringExtra("address");
                    latitude = data.getDoubleExtra("lati", 0);
                    longitude = data.getDoubleExtra("longi", 0);
                    state = data.getStringExtra("state");
                    Log.d(TAG, "onActivityResult: " + address);
                    binding.etAddress.setText(String.format("%s", address));
                }
            }

        }else {
            Log.d(TAG, "onActivityResult: no data");
        }
    }
}
package com.cwaliimran.pinneedy.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.databinding.ActivityAddReviewBinding;
import com.cwaliimran.pinneedy.models.ModelNeedy;
import com.cwaliimran.pinneedy.utils.AppConstants;
import com.cwaliimran.pinneedy.utils.GlobalClass;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddReviewActivity extends BaseActivity {
    ActivityAddReviewBinding binding;
    private int PICK_IMAGE_REQUEST = 1;
    private String picUrl = "";
    private Bitmap bitmap;
    ModelNeedy modelNeedy;
    List<Float> ratingArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Review");
        context = this;

        db = FirebaseFirestore.getInstance();
        randomId = GlobalClass.randomId();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            modelNeedy = (ModelNeedy) bundle.getSerializable("store");
            if (modelNeedy.getRatingArray() != null) {
                ratingArray.addAll(modelNeedy.getRatingArray());
            }
            Log.d(TAG, "onCreate: ratingArray : " + ratingArray);
        } else {
            Toast.makeText(context, "Try later.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void btnClick(View view) {
        tag = view.getTag().toString();
        if (tag.equals("submit")) {
            if (checkValidation()) {
                //submit review
                if (bitmap != null) {
                    showProgress(binding.progressBar);
                    upload(bitmap);
                } else {
                    showProgress(binding.progressBar);
                    addReview();
                }
            }
        } else if (tag.equals("imageView")) {
            openGallery();
        }
    }


    private void openGallery() {
        if (isReadStoragePermissionGranted()) {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                binding.imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    public void upload(final Bitmap bitmap) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String ref = "ReviewPics/" + randomId + ".jpg";
        final StorageReference filref = storageRef.child(ref);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = filref.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            hideProgress(binding.progressBar);
            Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> filref.getDownloadUrl().addOnSuccessListener(uri -> {
            picUrl = uri.toString();
            addReview();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
            hideProgress(binding.progressBar);
        }));
    }

    private void addReview() {
        GlobalClass.hideKeyboard(AddReviewActivity.this);
        Float rating = binding.rating.getRating();
        Map<String, Object> map = new HashMap<>();
        map.put("attachment", picUrl);
        map.put("comment", binding.etComment.getText().toString());
        map.put("id", randomId);
        map.put("rating", rating);
        map.put("createdAt", FieldValue.serverTimestamp());
        db.collection(AppConstants.TBL_REVIEWS).document(modelNeedy.getId()).collection(AppConstants.TBL_NEEDY_REVIEWS).document(randomId).set(map).addOnSuccessListener(aVoid -> {
            //add rating
            ratingArray.add(rating);
            db.collection(AppConstants.TBL_NEEDIES).document(modelNeedy.getId()).update("ratingArray", ratingArray).addOnSuccessListener(aVoid1 -> {
                Toast.makeText(AddReviewActivity.this, "Review submitted.", Toast.LENGTH_LONG).show();
                hideProgress(binding.progressBar);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(AddReviewActivity.this, "Something went wrong try again.", Toast.LENGTH_LONG).show();
                hideProgress(binding.progressBar);
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(AddReviewActivity.this, "Something went wrong try again.", Toast.LENGTH_LONG).show();
            hideProgress(binding.progressBar);
        });


    }

    public boolean checkValidation() {
        email = binding.etComment.getText().toString().trim();
        if (email.isEmpty()) {
            binding.etComment.setError("Enter comments");
            binding.etComment.requestFocus();
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
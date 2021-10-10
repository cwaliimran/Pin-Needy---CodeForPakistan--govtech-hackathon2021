package com.cwaliimran.pinneedy.activities.ui.profile;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.cwaliimran.pinneedy.databinding.FragmentProfileBinding;
import com.cwaliimran.pinneedy.models.ModelUser;
import com.cwaliimran.pinneedy.utils.AppConstants;
import com.cwaliimran.pinneedy.utils.MyApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    ModelUser modelUser;
    private int PICK_IMAGE_REQUEST = 1;
    private String picUrl = "";
    private Bitmap bitmap;
    FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        db = FirebaseFirestore.getInstance();
        modelUser = MyApp.getAppUser();
        binding.setData(modelUser);
        binding.ivProfile.setOnClickListener(v -> {
            if (isReadStoragePermissionGranted()) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });


        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                binding.ivProfile.setImageBitmap(bitmap);
                upload(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    public void upload(final Bitmap bitmap) {
        showProgress(binding.progressBar, true);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String ref = "UsersPics/" + modelUser.getId() + ".jpg";
        final StorageReference filref = storageRef.child(ref);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = filref.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {

            showProgress(binding.progressBar, false);
        }).addOnSuccessListener(taskSnapshot -> filref.getDownloadUrl().addOnSuccessListener(uri -> {
            picUrl = uri.toString();
            Map<String, Object> map = new HashMap<>();
            map.put("image", picUrl);
            db.collection(AppConstants.TBL_USERS).document(modelUser.getId()).update(map).addOnSuccessListener(aVoid -> {
                getCurrentUser(modelUser.getId());
            }).addOnFailureListener(e -> {
                Log.d(AppConstants.TAG, "e: " + e);
                showProgress(binding.progressBar, false);
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();

            showProgress(binding.progressBar, false);
        }));
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
                        modelUser = MyApp.getAppUser();
                        binding.setData(modelUser);
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Profile updated.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    public void showProgress(ProgressBar progressBar, boolean show) {
        if (show){
        progressBar.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }else {
            progressBar.setVisibility(View.GONE);
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }

}
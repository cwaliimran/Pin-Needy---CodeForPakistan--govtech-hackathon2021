package com.cwaliimran.pinneedy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.adapters.MyPostedNeediesAdapter;
import com.cwaliimran.pinneedy.databinding.ActivityMyPostedNeediesBinding;
import com.cwaliimran.pinneedy.models.ModelNeedy;
import com.cwaliimran.pinneedy.utils.AppConstants;
import com.cwaliimran.pinneedy.utils.MyApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MyPostedNeediesActivity extends BaseActivity {
    ActivityMyPostedNeediesBinding binding;
    MyPostedNeediesAdapter adapter;
    ArrayList<ModelNeedy> modelNeedy = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPostedNeediesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Needies added by me");
        context = this;
        db = FirebaseFirestore.getInstance();
        bundle = getIntent().getExtras();
        modelUser = MyApp.getAppUser();
        getMyPosts(modelUser.getId());
        adapter = new MyPostedNeediesAdapter(modelNeedy, context);
        binding.recyclerView.setAdapter(adapter);
    }

    private void getMyPosts(String id) {
        showProgress(binding.progressBar);
        db.collection(AppConstants.TBL_NEEDIES).whereEqualTo("userId", id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    modelNeedy.add(document.toObject(ModelNeedy.class));
                }
                if (modelNeedy.size() == 0) {
                    binding.noData.tvNoDataFound.setVisibility(View.VISIBLE);
                } else {
                    binding.noData.tvNoDataFound.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
                hideProgress(binding.progressBar);

            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
            hideProgress(binding.progressBar);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
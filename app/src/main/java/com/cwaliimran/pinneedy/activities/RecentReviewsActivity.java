package com.cwaliimran.pinneedy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.adapters.ReviewsAdapter;
import com.cwaliimran.pinneedy.databinding.ActivityRecentReviewsBinding;
import com.cwaliimran.pinneedy.models.ModelReviews;
import com.cwaliimran.pinneedy.models.ModelNeedy;
import com.cwaliimran.pinneedy.utils.AppConstants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class RecentReviewsActivity extends BaseActivity {
    ActivityRecentReviewsBinding binding;
    ModelNeedy modelNeedy;
    ReviewsAdapter adapter;
    ArrayList<ModelReviews> modelReviews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecentReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reviews");
        context = this;
        db = FirebaseFirestore.getInstance();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            modelNeedy = (ModelNeedy) bundle.getSerializable("store");
            getReviews(modelNeedy.getId());
        }
        adapter = new ReviewsAdapter(modelReviews, context);
        binding.recyclerView.setAdapter(adapter);
    }

    private void getReviews(String id) {
        showProgress(binding.progressBar);
        db.collection(AppConstants.TBL_REVIEWS).document(id).collection(AppConstants.TBL_NEEDY_REVIEWS).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    modelReviews.add(document.toObject(ModelReviews.class));
                }
                if (modelReviews.size() == 0) {
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
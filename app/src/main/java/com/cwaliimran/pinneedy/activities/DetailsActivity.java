package com.cwaliimran.pinneedy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.cwaliimran.pinneedy.BaseActivity;
import com.cwaliimran.pinneedy.databinding.ActivityDetailsBinding;
import com.cwaliimran.pinneedy.models.ModelNeedy;

import java.util.Objects;

public class DetailsActivity extends BaseActivity {
    ActivityDetailsBinding binding;
    ModelNeedy modelNeedy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Store details");
        context = this;
        bundle = getIntent().getExtras();
        if (bundle != null) {
            modelNeedy = (ModelNeedy) bundle.getSerializable("store");
            binding.setData(modelNeedy);
        }
    }


    public void btnClick(View view) {
        tag = view.getTag().toString();
        if (tag.equals("recentReviews")) {
            Intent intent = new Intent(context, RecentReviewsActivity.class);
            intent.putExtra("store", modelNeedy);
            startActivity(intent);
        }
        if (tag.equals("navigateToStore")) {

            String uri = "http://maps.google.com/maps?q=loc:" + modelNeedy.getLatitude() + "," + modelNeedy.getLongitude() + " (" + modelNeedy.getNeedyName() + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);

//            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", modelNeedy.getLatitude(), modelNeedy.getLongitude());
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//            context.startActivity(intent);
        }
        if (tag.equals("addReview")) {
            Intent intent = new Intent(context, AddReviewActivity.class);
            intent.putExtra("store", modelNeedy);
            startActivity(intent);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
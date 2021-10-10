package com.cwaliimran.pinneedy.activities.ui.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cwaliimran.pinneedy.BuildConfig;
import com.cwaliimran.pinneedy.activities.LoginActivity;
import com.cwaliimran.pinneedy.activities.MyPostedNeediesActivity;
import com.cwaliimran.pinneedy.activities.RecommendNeedyActivity;
import com.cwaliimran.pinneedy.activities.SendFeedbackActivity;
import com.cwaliimran.pinneedy.databinding.FragmentMoreBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MoreFragment extends Fragment {
    FragmentMoreBinding binding;
    FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMoreBinding.inflate(getLayoutInflater(), container, false);
        auth = FirebaseAuth.getInstance();
        binding.addNeedy.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), RecommendNeedyActivity.class));
        });
        binding.sendFeedback.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SendFeedbackActivity.class));
        });
        binding.myPostedNeedies.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyPostedNeediesActivity.class));
        });
        binding.shareApp.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out this helping app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });
        binding.logout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finishAffinity();
        });
        return binding.getRoot();
    }
}
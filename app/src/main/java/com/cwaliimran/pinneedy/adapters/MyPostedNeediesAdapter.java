package com.cwaliimran.pinneedy.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cwaliimran.pinneedy.R;
import com.cwaliimran.pinneedy.databinding.RowMyPostedNeediesBinding;
import com.cwaliimran.pinneedy.databinding.RowRecentReviewsBinding;
import com.cwaliimran.pinneedy.models.ModelNeedy;
import com.cwaliimran.pinneedy.models.ModelReviews;
import com.cwaliimran.pinneedy.models.ModelUser;
import com.cwaliimran.pinneedy.utils.MyApp;

import java.util.ArrayList;

public class MyPostedNeediesAdapter extends RecyclerView.Adapter<MyPostedNeediesAdapter.MyViewHolder> {
    private Context context;
    ArrayList<ModelNeedy> data;
    LayoutInflater inflater;
    private static final String TAG = "response";
    ModelUser modelUser;

    public MyPostedNeediesAdapter(ArrayList<ModelNeedy> model, Context context) {
        this.data = model;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        modelUser = MyApp.getAppUser();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(RowMyPostedNeediesBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelNeedy modelNeedies = data.get(position);
        holder.binding.tvUserName.setText(modelUser.getName());
        holder.binding.tvComment.setText(modelNeedies.getAddress());
        holder.binding.tvNeeds.setText(modelNeedies.getNotes());
        holder.binding.ivDelete.setOnClickListener(v -> {
            Toast.makeText(context, "Coming soon.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RowMyPostedNeediesBinding binding;

        public MyViewHolder(@NonNull RowMyPostedNeediesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
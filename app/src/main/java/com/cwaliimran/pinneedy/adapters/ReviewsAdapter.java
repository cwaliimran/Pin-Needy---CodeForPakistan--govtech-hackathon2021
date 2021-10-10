package com.cwaliimran.pinneedy.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cwaliimran.pinneedy.R;
import com.cwaliimran.pinneedy.databinding.RowRecentReviewsBinding;
import com.cwaliimran.pinneedy.models.ModelReviews;
import com.cwaliimran.pinneedy.models.ModelUser;
import com.cwaliimran.pinneedy.utils.MyApp;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {
    private Context context;
    ArrayList<ModelReviews> data;
    LayoutInflater inflater;
    private static final String TAG = "response";
    ModelUser modelUser;

    public ReviewsAdapter(ArrayList<ModelReviews> model, Context context) {
        this.data = model;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        modelUser = MyApp.getAppUser();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(RowRecentReviewsBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelReviews modelReviews = data.get(position);
        holder.binding.tvUserName.setText(modelUser.getName());
        holder.binding.tvComment.setText(modelReviews.getComment());
        holder.binding.rating.setRating(modelReviews.getRating());
        Glide.with(context).load(modelReviews.getAttachment()).placeholder(R.drawable.image_placeholder).into(holder.binding.imageView);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RowRecentReviewsBinding binding;

        public MyViewHolder(@NonNull RowRecentReviewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
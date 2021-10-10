package com.cwaliimran.pinneedy.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cwaliimran.pinneedy.activities.DetailsActivity;
import com.cwaliimran.pinneedy.databinding.RowNearybyStoresBinding;
import com.cwaliimran.pinneedy.models.ModelNeedy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NearbyPlacesAdapter extends RecyclerView.Adapter<NearbyPlacesAdapter.MyViewHolder> {
    private Context context;
    LayoutInflater inflater;
    private static final String TAG = "response";
    ArrayList<ModelNeedy> data;
    List<Float> ratingArray = new ArrayList<>();

    public NearbyPlacesAdapter(ArrayList<ModelNeedy> model, Context context) {
        this.data = model;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(RowNearybyStoresBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelNeedy modelNeedy = data.get(position);
        holder.binding.tvName.setText(modelNeedy.getNeedyName());
        if (modelNeedy.getRatingArray() != null && modelNeedy.getRatingArray().size()>0) {
            ratingArray.addAll(modelNeedy.getRatingArray());
            float sum = 0.0f;
            for (int z = 0; z < ratingArray.size(); z++) {
                sum += ratingArray.get(z);
            }
            float avg = sum / ratingArray.size();
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            holder.binding.tvRating.setText(String.format("%s (%s)", df.format(avg), ratingArray.size()));
        } else {
            holder.binding.tvRating.setText("N/A");
        }

        holder.binding.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("store", modelNeedy);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RowNearybyStoresBinding binding;

        public MyViewHolder(@NonNull RowNearybyStoresBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
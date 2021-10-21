package com.example.capstoneprojectv13.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.model.Reviews;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ReviewsAdapter extends FirebaseRecyclerAdapter<Reviews, ReviewsAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ReviewsAdapter(@NonNull FirebaseRecyclerOptions<Reviews> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Reviews model) {
        holder.userTv.setText(model.getName());
        holder.reviewTv.setText(model.getReview());
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productsLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list,parent,false);
        return new myViewHolder(productsLayout);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView userTv, reviewTv;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            userTv = itemView.findViewById(R.id.tvUsers);
            reviewTv = itemView.findViewById(R.id.tvReview);
        }
    }
}

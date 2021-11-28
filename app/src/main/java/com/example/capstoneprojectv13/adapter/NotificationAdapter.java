package com.example.capstoneprojectv13.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneprojectv13.model.NotificationModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class NotificationAdapter  extends FirebaseRecyclerAdapter<NotificationModel,MainProductsAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NotificationAdapter(@NonNull FirebaseRecyclerOptions<NotificationModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MainProductsAdapter.myViewHolder holder, int position, @NonNull NotificationModel model) {

    }

    @NonNull
    @Override
    public MainProductsAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }
}

package com.example.capstoneprojectv13.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import com.example.capstoneprojectv13.ProductsActivity;
import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.model.Products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MainProductsAdapter extends FirebaseRecyclerAdapter<Products,MainProductsAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    Context context;
    public MainProductsAdapter(Context context, @NonNull FirebaseRecyclerOptions<Products> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Products model) {
        holder.Name.setText(model.getName());
        holder.Price.setText(model.getPrice());
        holder.Category.setText(model.getCategory());
        holder.Description.setText(model.getDescription());
        holder.Status.setText(model.getStatus());
        Glide.with(holder.Image.getContext())
                .load(model.getImage())
                .error(R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.Image);

        final String id = getRef(position).getKey();
        final String name = model.getName();
        final String price = model.getPrice();
        final String category = model.getCategory();
        final String description = model.getDescription();
        final String status = model.getStatus();
        final String image = model.getImage();

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductsActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name",name);
                intent.putExtra("price", price);
                intent.putExtra("category", category);
                intent.putExtra("description", description);
                intent.putExtra("status", status);
                intent.putExtra("image", image);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productsLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_products_list,parent,false);
        return new myViewHolder(productsLayout);

    }

    static class myViewHolder extends RecyclerView.ViewHolder{


        TextView Id,Name,Price,Category,Description,Status;
        ImageView Image;

        LinearLayout linearLayout;


        public myViewHolder(@NonNull View itemView) {

            super(itemView);

            Name = itemView.findViewById(R.id.HomeProductNameTv);
            Price = itemView.findViewById(R.id.HomePriceTv);
            Category = itemView.findViewById(R.id.HomeCategoryTv);
            Description = itemView.findViewById(R.id.HomeDescriptionTv);
            Status = itemView.findViewById(R.id.HomeDescriptionTv);
            Image = itemView.findViewById(R.id.HomeImageIv);
            linearLayout = itemView.findViewById(R.id.HomelLayout);

        }
    }


}

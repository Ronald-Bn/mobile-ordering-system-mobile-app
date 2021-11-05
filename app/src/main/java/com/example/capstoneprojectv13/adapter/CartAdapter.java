package com.example.capstoneprojectv13.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.model.CartModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;
import java.util.UUID;

public class CartAdapter extends FirebaseRecyclerAdapter<CartModel,CartAdapter.myViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;
    public CartAdapter(Context context ,@NonNull FirebaseRecyclerOptions<CartModel> options)
    {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CartAdapter.myViewHolder holder, int position, @NonNull CartModel model) {
        holder.CartNameTv.setText(model.getName());
        holder.CartPriceTv.setText(String.valueOf(model.getTotalPrice()));
        holder.CartQuantityTv.setText("x" + (model.getQuantity()));
        Glide.with(holder.CartImageIv.getContext())
                .load(model.getImage())
                .error(R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.CartImageIv);

    }

    @NonNull
    @Override
    public CartAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productsLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list,parent,false);
        return new CartAdapter.myViewHolder(productsLayout);
    }

    public class myViewHolder extends  RecyclerView.ViewHolder{

        ImageView CartImageIv;

        TextView CartQuantityTv, CartPriceTv, CartNameTv;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            CartImageIv = itemView.findViewById(R.id.CartImageIv);
            CartNameTv = itemView.findViewById(R.id.CartNameTv);
            CartPriceTv = itemView.findViewById(R.id.CartPriceTv);
            CartQuantityTv = itemView.findViewById(R.id.CartQuantityTv);
        }
    }

    public int grandTotal(List<CartModel> items){

        int totalPrice = 0;
        for(int i = 0 ; i < items.size(); i++) {
            totalPrice += items.get(i).getTotalPrice();
        }

        return totalPrice;
    }
}

package com.example.capstoneprojectv13.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.model.CartModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CartAdapter extends FirebaseRecyclerAdapter<CartModel,CartAdapter.myViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    Context context;
    int a = 0;
    public CartAdapter(Context context ,@NonNull FirebaseRecyclerOptions<CartModel> options)
    {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CartAdapter.myViewHolder holder, int position, @NonNull CartModel model) {
        if(model.getKey().equals("ongoing")){
            holder.HomelLayout.setEnabled(true);
        }else{
            holder.HomelLayout.setEnabled(false);
        }
        holder.cartStatus.setText(model.getStatus());
        holder.CartNameTv.setText(model.getName());
        holder.CartPriceTv.setText(String.valueOf(model.getTotalPrice()));
        holder.CartQuantityTv.setText("x" + (model.getQuantity()));
        Glide.with(holder.CartImageIv.getContext())
                .load(model.getImage())
                .error(R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.CartImageIv);
        holder.HomelLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, model.getStatus(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @NonNull
    @Override
    public CartAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productsLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list,parent,false);
        return new CartAdapter.myViewHolder(productsLayout);
    }

    public class myViewHolder extends  RecyclerView.ViewHolder{

        ImageView CartImageIv;
        LinearLayout HomelLayout;
        TextView CartQuantityTv, CartPriceTv, CartNameTv, cartStatus;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            CartImageIv = itemView.findViewById(R.id.CartImageIv);
            CartNameTv = itemView.findViewById(R.id.CartNameTv);
            CartPriceTv = itemView.findViewById(R.id.CartPriceTv);
            CartQuantityTv = itemView.findViewById(R.id.CartQuantityTv);
            HomelLayout = itemView.findViewById(R.id.HomelLayout);
            cartStatus = itemView.findViewById(R.id.cartStatus);
        }
    }


    @Override
    public void startListening() {
        super.startListening();
    }

    @Override
    public void stopListening() {
        super.stopListening();
    }

}

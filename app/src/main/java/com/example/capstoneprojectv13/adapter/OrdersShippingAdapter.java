package com.example.capstoneprojectv13.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneprojectv13.PendingActivity;
import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.ShippingActivity;
import com.example.capstoneprojectv13.model.OrdersModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class OrdersShippingAdapter extends FirebaseRecyclerAdapter<OrdersModel, OrdersShippingAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private Context context;

    public OrdersShippingAdapter(Context context, @NonNull FirebaseRecyclerOptions<OrdersModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull OrdersShippingAdapter.myViewHolder holder, int position, @NonNull OrdersModel model) {

        holder.OrdersBtn.setText("Shipping");
        holder.OrdersNameTv.setText(model.getCartId());
        holder.OrdersDateTv.setText(model.getDate());
        holder.OrdersAddressTv.setText(model.getAddress());
        holder.OrdersZipcodeTv.setText(model.getZipcode());
        holder.OrdersBtn.setEnabled(false);
        holder.OrdersLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShippingActivity.class);
                intent.putExtra("ordersId", getRef(position).getKey());
                intent.putExtra("cartId", model.getCartId());
                context.startActivity(intent);
            }
        });


    }

    @NonNull
    @Override
    public OrdersShippingAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ordersLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_order,parent,false);
        return new OrdersShippingAdapter.myViewHolder(ordersLayout);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        LinearLayout OrdersLLayout;
        Button OrdersBtn;
        TextView OrdersAddressTv, OrdersDateTv, OrdersZipcodeTv, OrdersNameTv, OrdersCartIdTv, TvOrderId;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            OrdersLLayout = itemView.findViewById(R.id.ordersLLayout);
            OrdersCartIdTv = itemView.findViewById(R.id.tvOrdersCartId);
            OrdersBtn = itemView.findViewById(R.id.btnOrders);
            OrdersNameTv = itemView.findViewById(R.id.tvOrdersName);
            OrdersAddressTv = itemView.findViewById(R.id.TvOrdersAddress);
            OrdersDateTv = itemView.findViewById(R.id.tvOrdersDate);
            OrdersZipcodeTv = itemView.findViewById(R.id.TvOrdersZipcode);
            TvOrderId = itemView.findViewById(R.id.TvOrderId);
        }
    }
}

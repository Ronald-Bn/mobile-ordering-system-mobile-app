package com.example.capstoneprojectv13.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneprojectv13.PaymentActivity;
import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.fragment.FragmentOrderCancelled;
import com.example.capstoneprojectv13.model.CartModel;
import com.example.capstoneprojectv13.model.OrdersModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class OrdersAdapter extends FirebaseRecyclerAdapter<OrdersModel, OrdersAdapter.myViewHolder>{

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    Context context;
    private boolean activate;
    private String name;

    public OrdersAdapter(Context context ,@NonNull FirebaseRecyclerOptions<OrdersModel> options) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull OrdersModel model) {

        holder.OrdersNameTv.setText(model.getName());
        holder.OrdersDateTv.setText(model.getDate());
        holder.OrdersAddressTv.setText(model.getAddress());
        holder.OrdersZipcodeTv.setText(model.getZipcode());
        if (activate) {
            holder.OrdersBtn.setText(name);
            holder.OrdersBtn.setVisibility(View.VISIBLE);
        } else {
            holder.OrdersBtn.setVisibility(View.GONE);
        }
        holder.OrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PaymentActivity.class);

                if(name.toLowerCase().equals("pending"))
                {
                    intent.putExtra("btnName", "pending");
                    intent.putExtra("ordersId", getRef(position).getKey());
                    intent.putExtra("cartId", model.getCartId());
                    context.startActivity(intent);
                }else if(name.toLowerCase().equals("receiving")){
                    Toast.makeText(context, "Error on Click" , Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Error on Click" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @NonNull
    @Override
    public OrdersAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ordersLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_order,parent,false);
        return new OrdersAdapter.myViewHolder(ordersLayout);
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        Button OrdersBtn;


        TextView OrdersAddressTv, OrdersDateTv, OrdersZipcodeTv, OrdersNameTv;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            OrdersBtn = itemView.findViewById(R.id.btnOrders);
            OrdersNameTv = itemView.findViewById(R.id.tvOrdersName);
            OrdersAddressTv = itemView.findViewById(R.id.TvOrdersAddress);
            OrdersDateTv = itemView.findViewById(R.id.tvOrdersDate);
            OrdersZipcodeTv = itemView.findViewById(R.id.TvOrdersZipcode);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public void activateButtons(boolean activate , String name) {
        this.name = name;
        this.activate = activate;
        notifyDataSetChanged(); //need to call it for the child views to be re-created with buttons.
    }
}

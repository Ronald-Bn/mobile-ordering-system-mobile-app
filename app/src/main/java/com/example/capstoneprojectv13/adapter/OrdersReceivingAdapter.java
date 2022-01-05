package com.example.capstoneprojectv13.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.ReceivingActivity;
import com.example.capstoneprojectv13.ShippingActivity;
import com.example.capstoneprojectv13.model.OrdersModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class OrdersReceivingAdapter extends FirebaseRecyclerAdapter<OrdersModel, OrdersReceivingAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private Context context;
    private FirebaseAuth mAuth;

    public OrdersReceivingAdapter(Context context, @NonNull FirebaseRecyclerOptions<OrdersModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull OrdersReceivingAdapter.myViewHolder holder, int position, @NonNull OrdersModel model) {
        if(model.getNotify().equals("1")){
            holder.OrdersBtn.setEnabled(true);
            holder.OrdersBtn.setBackgroundColor(holder.OrdersBtn.getContext().getResources().getColor(R.color.reddish));
        }else{
            holder.OrdersBtn.setEnabled(false);
            holder.OrdersBtn.setBackgroundColor(holder.OrdersBtn.getContext().getResources().getColor(R.color.dark_light));
        }
        holder.OrdersBtn.setText("Received");
        holder.OrdersNameTv.setText(model.getCartId());
        holder.OrdersDateTv.setText(model.getDate());
        holder.OrdersAddressTv.setText(model.getAddress());
        holder.OrdersZipcodeTv.setText(model.getZipcode());
        holder.OrdersLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReceivingActivity.class);
                intent.putExtra("ordersId", getRef(position).getKey());
                intent.putExtra("cartId", model.getCartId());
                intent.putExtra("notify", model.getNotify());
                context.startActivity(intent);
            }
        });
        holder.OrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.order_received_dialog, null);
                ImageView cancelIv = view.findViewById(R.id.cancelIv);
                Button cancelBtn = view.findViewById(R.id.cancelBtn);
                Button confirmBtn = view.findViewById(R.id.confirmBtn);
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                builder.setView(view);

                AlertDialog dialog = builder.create();
                dialog.show();

                cancelIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference reference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Orders")
                                .child(getRef(position).getKey());

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                FirebaseUser user = mAuth.getInstance().getCurrentUser();
                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("receivedate",dateAndTime());
                                    updateData.put("status", "completed");
                                    updateData.put("status_userid", "completed_" + user.getUid());
                                    reference.updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Thank you for buying our product(s)", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });


            }
        });

    }

    @NonNull
    @Override
    public OrdersReceivingAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ordersLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_order,parent,false);
        return new OrdersReceivingAdapter.myViewHolder(ordersLayout);
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


    private String dateAndTime(){
        // Current Date and Time
        Date dateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDate = dateFormat.format(dateAndTime);
        String currentTime = timeFormat.format(dateAndTime);

        return new StringBuilder().append(currentDate).append(" ").append(currentTime).toString();
    }
}

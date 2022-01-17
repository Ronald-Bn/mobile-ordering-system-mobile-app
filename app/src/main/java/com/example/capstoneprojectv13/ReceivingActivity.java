package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneprojectv13.adapter.CartAdapter;
import com.example.capstoneprojectv13.model.CartModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class
ReceivingActivity extends AppCompatActivity {

    private TextView TvAddress, TvZipCode, TvSubtotal, TvTotalPayment , TvShip, OrderDateTv, OrderIdTv, confirmDate, paymentDate, shipDate, gCashPaymentRefNo, amountTv, receiptBtn;
    private Button receiveBtn, returnRefundBtn;
    private String ordersId;
    private int sum = 0;
    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Parcelable state;
    private FirebaseAuth mAuth;
    private RelativeLayout cashPaymentRL, gCashPaymentRL, gCashReceiptPaymentRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_receiving);

        //Toolbar Customization
        Toolbar mToolbar = findViewById(R.id.toolbar_order_details);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());


        String cartId = getIntent().getStringExtra("cartId");
        ordersId = getIntent().getStringExtra("ordersId");
        String status = getIntent().getStringExtra("notify");

        TvAddress = findViewById(R.id.TvAddress);
        TvZipCode = findViewById(R.id.TvZipcode);
        TvSubtotal = findViewById(R.id.SubTotalTv);
        TvShip = findViewById(R.id.ShipPriceTv);
        TvTotalPayment = findViewById(R.id.TvTotalTv);
        receiveBtn = findViewById(R.id.receiveBtn);
        OrderDateTv = findViewById(R.id.orderDateTv);
        OrderIdTv = findViewById(R.id.orderIdTv);
        confirmDate =findViewById(R.id.confirmDate);
        paymentDate = findViewById(R.id.paymentDate);
        shipDate =findViewById(R.id.shipDate);
        cashPaymentRL = findViewById(R.id.cashPaymentRL);
        gCashPaymentRL = findViewById(R.id.gCashPaymentRL);
        gCashPaymentRefNo = findViewById(R.id.gCashPaymentRefNo);
        gCashReceiptPaymentRL = findViewById(R.id.gCashReceiptPaymentRL);
        receiptBtn = findViewById(R.id.receiptBtn);
        returnRefundBtn = findViewById(R.id.returnRefundBtn);
        returnRefundBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, RequestReturnActivity.class);
            intent.putExtra("cartId",cartId);
            intent.putExtra("ordersId", ordersId);
            intent.putExtra("orderTime",OrderDateTv.getText().toString());
            startActivity(intent);
        });


        if(status.equals("1")){
            receiveBtn.setEnabled(true);
            returnRefundBtn.setEnabled(true);
            returnRefundBtn.setBackgroundColor(getResources().getColor(R.color.reddish));
            receiveBtn.setBackgroundColor(getResources().getColor(R.color.reddish));
        }else{
            receiveBtn.setEnabled(false);
            returnRefundBtn.setEnabled(false);
            returnRefundBtn.setBackgroundColor(getResources().getColor(R.color.dark_light));
            receiveBtn.setBackgroundColor(getResources().getColor(R.color.dark_light));
        }

        recyclerView = findViewById(R.id.CheckOutRecyclerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Cart_List")
                                .child(user.getUid()).child(cartId), CartModel.class)
                        .build();

        cartAdapter = new CartAdapter(this, options);
        recyclerView.setAdapter(cartAdapter);


        DocumentReference documentReference = fStore.collection("Users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        TvAddress.setText(documentSnapshot.getString("Address"));
                        TvZipCode.setText(documentSnapshot.getString("Zipcode"));
                    }
                }
            }
        });

        databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Cart_List")
                .child(user.getUid())
                .child(cartId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Object price = map.get("totalPrice");
                    int pValue = Integer.parseInt(String.valueOf(price));
                    sum += pValue;
                    TvSubtotal.setText(String.valueOf(sum));
                    TvTotalPayment.setText(String.valueOf(sum + Integer.parseInt(TvShip.getText().toString())));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReceivingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Getting data's into views
        databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Orders");
        databaseReference.orderByChild("cartId").equalTo(cartId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Object orderDate = map.get("date");
                    Object orderId = map.get("cartId");
                    Object confirmdate = map.get("confirmdate");
                    Object paymentdate = map.get("paymentdate");
                    Object shipdate = map.get("shipdate");
                    Object refno = map.get("refno");
                    Object payment = map.get("payment");
                    Object receipt = map.get("receipt");

                    if(String.valueOf(payment).equals("Gcash") &&  !String.valueOf(refno).equals("0")){
                        gCashPaymentRL.setVisibility(View.VISIBLE);
                        gCashPaymentRefNo.setText(String.valueOf(refno));
                    }else if(String.valueOf(receipt) != null){
                        gCashReceiptPaymentRL.setVisibility(View.VISIBLE);
                        receiptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ReceivingActivity.this , FullScreenImageActivity.class);
                                intent.putExtra("receipt", String.valueOf(receipt));
                                startActivity(intent);
                            }
                        });
                    }else{
                        cashPaymentRL.setVisibility(View.VISIBLE);
                    }

                    OrderDateTv.setText(String.valueOf(orderDate));
                    OrderIdTv.setText(String.valueOf(orderId));
                    confirmDate.setText(String.valueOf(confirmdate));
                    paymentDate.setText(String.valueOf(paymentdate));
                    shipDate.setText(String.valueOf(shipdate));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReceivingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        receiveBtn.setOnClickListener(new View.OnClickListener() {
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
                                .child(ordersId);

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                FirebaseUser user = mAuth.getInstance().getCurrentUser();
                                Map<String, Object> updateData = new HashMap<>();
                                updateData.put("receivedate",dateAndTime());
                                updateData.put("completed",reportDateAndTime());
                                updateData.put("status_userid", "completed_" + user.getUid());
                                reference.updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(ReceivingActivity.this, "Thank you for buying our product(s)", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ReceivingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ReceivingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getLayoutManager().onRestoreInstanceState(state);
    }

    @Override
    public void onPause() {
        super.onPause();
        state = recyclerView.getLayoutManager().onSaveInstanceState();
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

    private String reportDateAndTime(){
        // Current Date and Time
        Date dateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.getDefault());
        String currentDate = dateFormat.format(dateAndTime);

        return currentDate;
    }
}
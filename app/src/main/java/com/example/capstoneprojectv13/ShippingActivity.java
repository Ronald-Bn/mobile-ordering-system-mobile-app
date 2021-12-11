package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneprojectv13.adapter.CartAdapter;
import com.example.capstoneprojectv13.model.CartModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.Map;

public class ShippingActivity extends AppCompatActivity {

    private TextView TvAddress, TvZipCode, TvSubtotal, TvTotalPayment , TvShip, OrderDateTv, OrderIdTv, confirmDate, paymentDate, gCashPaymentRefNo, TvPhone, TvName;
    private Button btnPlaceOrder;
    private String ordersId;
    private int sum = 0;
    private FirebaseFirestore fStore;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Parcelable state;
    private FirebaseAuth mAuth;
    private RelativeLayout cashPaymentRL, gCashPaymentRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_shipping);
        Toolbar mToolbar = findViewById(R.id.toolbar_order_details);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        String cartId = getIntent().getStringExtra("cartId");
        ordersId = getIntent().getStringExtra("ordersId");


        TvName = findViewById(R.id.TvName);
        TvPhone = findViewById(R.id.TvPhone);
        TvAddress = findViewById(R.id.TvAddress);
        TvZipCode = findViewById(R.id.TvZipcode);
        TvSubtotal = findViewById(R.id.SubTotalTv);
        TvShip = findViewById(R.id.ShipPriceTv);
        TvTotalPayment = findViewById(R.id.TvTotalTv);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        OrderDateTv = findViewById(R.id.orderDateTv);
        OrderIdTv = findViewById(R.id.orderIdTv);
        confirmDate =findViewById(R.id.confirmDate);
        paymentDate = findViewById(R.id.paymentDate);
        cashPaymentRL = findViewById(R.id.cashPaymentRL);
        gCashPaymentRL = findViewById(R.id.gCashPaymentRL);
        gCashPaymentRefNo = findViewById(R.id.gCashPaymentRefNo);


        recyclerView = findViewById(R.id.CheckOutRecyclerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Cart_List")
                                .child(user.getUid())
                                .child(cartId), CartModel.class)
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
                        TvName.setText(documentSnapshot.getString("FullName"));
                        TvPhone.setText(documentSnapshot.getString("Phone"));
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
                Toast.makeText(ShippingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
                    Object refno = map.get("refno");
                    Object payment = map.get("payment");

                    if(String.valueOf(payment).equals("Gcash")){
                        gCashPaymentRL.setVisibility(View.VISIBLE);
                        gCashPaymentRefNo.setText(String.valueOf(refno));
                    }else{
                        cashPaymentRL.setVisibility(View.VISIBLE);
                    }

                    OrderDateTv.setText(String.valueOf(orderDate));
                    OrderIdTv.setText(String.valueOf(orderId));
                    confirmDate.setText(String.valueOf(confirmdate));
                    paymentDate.setText(String.valueOf(paymentdate));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShippingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
}
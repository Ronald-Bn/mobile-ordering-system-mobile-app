package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneprojectv13.adapter.CartAdapter;
import com.example.capstoneprojectv13.listener.ICartLoadListener;
import com.example.capstoneprojectv13.model.CartModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PendingActivity extends AppCompatActivity {

    private TextView TvAddress, TvZipCode, TvSubtotal, TvTotalPayment , TvShip , OrderDateTv, OrderIdTv, TvPhone, TvName;
    private Button btnPlaceOrder;
    private String uid;
    private String ordersId;
    private int sum = 0;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Parcelable state;
    private LinearLayout linearLayout,linearLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_pending);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_orders_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TvName = findViewById(R.id.TvName);
        TvPhone = findViewById(R.id.TvPhone);
        TvAddress = findViewById(R.id.TvAddress);
        TvZipCode = findViewById(R.id.TvZipcode);
        TvSubtotal = findViewById(R.id.SubTotalTv);
        TvShip = findViewById(R.id.ShipPriceTv);
        TvTotalPayment = findViewById(R.id.TvTotalTv);
        OrderDateTv = findViewById(R.id.orderDateTv);
        OrderIdTv = findViewById(R.id.orderIdTv);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        String cartId = getIntent().getStringExtra("cartId");
        ordersId = getIntent().getStringExtra("ordersId");

        linearLayout = findViewById(R.id.PaymentLayout);
        linearLayout2 = findViewById(R.id.PaymentLayout2);
        linearLayout.setVisibility(View.GONE);
        btnPlaceOrder.setText("Cancel Order");

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();

        recyclerView = findViewById(R.id.CheckOutRecyclerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Cart")
                                .child(user.getUid()).orderByChild("cartId").equalTo(cartId), CartModel.class)
                        .build();

        cartAdapter = new CartAdapter(this, options);
        recyclerView.setAdapter(cartAdapter);

        DocumentReference documentReference = fStore.collection("Users").document(uid);
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
                .getReference("Orders");
        databaseReference.orderByChild("cartId").equalTo(cartId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Object orderDate = map.get("date");
                    Object orderId = map.get("cartId");

                    OrderDateTv.setText(String.valueOf(orderDate));
                    OrderIdTv.setText(String.valueOf(orderId));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Cart")
                .child(user.getUid());
        databaseReference.orderByChild("cartId").equalTo(cartId).addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(PendingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
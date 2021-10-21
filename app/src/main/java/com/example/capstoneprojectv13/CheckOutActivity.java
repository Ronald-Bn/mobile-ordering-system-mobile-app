package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static java.lang.String.format;

public class CheckOutActivity extends AppCompatActivity {

    private TextView TvAddress, TvZipCode, TvSubtotal, TvTotalPayment , TvShip;
    private Button btnPlaceOrder;
    private String ongoing = "ongoing";
    private String subtotal;
    private String Address, Zipcode, userName;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Parcelable state;
    private UUID uuid = UUID.randomUUID();
    private String uuidAsString = uuid.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_check_out);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TvAddress = findViewById(R.id.TvAddress);
        TvZipCode = findViewById(R.id.TvZipcode);
        TvSubtotal = findViewById(R.id.SubTotalTv);
        TvShip = findViewById(R.id.ShipPriceTv);
        TvTotalPayment = findViewById(R.id.TvTotalTv);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        subtotal = getIntent().getStringExtra("subtotal");

        recyclerView = findViewById(R.id.CheckOutRecyclerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Cart")
                                .child(user.getUid()).orderByChild("key").equalTo("ongoing"), CartModel.class)
                        .build();

        cartAdapter = new CartAdapter(this,options);
        recyclerView.setAdapter(cartAdapter);


        DocumentReference documentReference = fStore.collection("Users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        userName = documentSnapshot.getString("FullName");
                        TvAddress.setText(documentSnapshot.getString("Address"));
                        TvZipCode.setText(documentSnapshot.getString("Zipcode"));
                    }
                }
            }
        });


        TvSubtotal.setText(subtotal);
        int a = Integer.parseInt(subtotal);
        int b = Integer.parseInt(TvShip.getText().toString());
        int c = a + b;

        TvTotalPayment.setText(String.valueOf(c));

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToOrders();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        state = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getLayoutManager().onRestoreInstanceState(state);
    }


    private void addToOrders () {

        // Current Date and Time
        Date dateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(dateAndTime);
        String currentTime = timeFormat.format(dateAndTime);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Orders");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Map<String,Object> updateData = new HashMap<>();
                updateData.put("userid", user.getUid());
                updateData.put("key", snapshot.getRef().getKey());
                updateData.put("cartId", uuidAsString);
                updateData.put("name", userName);
                updateData.put("address", TvAddress.getText().toString());
                updateData.put("zipcode", TvZipCode.getText().toString());
                updateData.put("date", currentDate + " " + currentTime);
                updateData.put("status", "pending");
                updateData.put("totalpayment", TvTotalPayment.getText().toString());

                databaseReference.push()
                        .setValue(updateData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Map<String, Object> updateCart = new HashMap<>();
                                updateCart.put("key", "pending");
                                databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Cart").child(user.getUid());

                                databaseReference.orderByChild("key").equalTo("ongoing").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for(DataSnapshot childSnapshot: snapshot.getChildren()) {

                                            //Check whether child with key 'isAvailable' exist or not
                                            if (childSnapshot.hasChild("key")) {

                                                //Now update the status with false
                                                databaseReference.child(childSnapshot.getKey()).child("key").setValue("pending");
                                                databaseReference.child(childSnapshot.getKey()).child("cartId").setValue(uuidAsString);
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(CheckOutActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(CheckOutActivity.this, "Add to Cart Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CheckOutActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CheckOutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckOutActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


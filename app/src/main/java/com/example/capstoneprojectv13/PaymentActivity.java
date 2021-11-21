package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView TvAddress, TvZipCode, TvSubtotal, TvTotalPayment , TvShip, OrderDateTv, OrderIdTv, confirmDate, TvPhone, TvName;
    private Button btnPlaceOrder;
    private String ordersId;
    private int sum = 0;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Parcelable state;
    private RadioButton cashPaymentBtn, gCashPaymentBtn;
    private LinearLayout linearLayout,linearLayout2 ;
    private EditText referenceNoEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_payment);
        Toolbar mToolbar = findViewById(R.id.toolbar_order_details);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());;

        TvName = findViewById(R.id.TvName);
        TvPhone = findViewById(R.id.TvPhone);
        TvAddress = findViewById(R.id.TvAddress);
        TvZipCode = findViewById(R.id.TvZipcode);
        TvSubtotal = findViewById(R.id.SubTotalTv);
        TvShip = findViewById(R.id.ShipPriceTv);
        TvTotalPayment = findViewById(R.id.TvTotalTv);
        cashPaymentBtn = findViewById(R.id.radio_one);
        gCashPaymentBtn = findViewById(R.id.radio_two);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        OrderDateTv = findViewById(R.id.orderDateTv);
        OrderIdTv = findViewById(R.id.orderIdTv);
        confirmDate =findViewById(R.id.confirmDate);
        referenceNoEt =findViewById(R.id.referenceNoEt);
        btnPlaceOrder.setOnClickListener(this);

        String cartId = getIntent().getStringExtra("cartId");
        ordersId = getIntent().getStringExtra("ordersId");

        linearLayout = findViewById(R.id.PaymentLayout);
        linearLayout2 = findViewById(R.id.PaymentLayout2);
        linearLayout.setVisibility(View.VISIBLE);
        btnPlaceOrder.setText("Pay");

        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.CheckOutRecyclerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Cart")
                                .child(user.getUid()).orderByChild("cartId").equalTo(cartId), CartModel.class)
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
                Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    String input =  String.valueOf(orderId);
                    String orderID;
                    if (input.length() > 12)
                    {
                        orderID = input.substring(0, 12);
                    }
                    else
                    {
                        orderID = input;
                    }

                    OrderDateTv.setText(String.valueOf(orderDate));
                    OrderIdTv.setText(replaceAll(orderID));
                    confirmDate.setText(String.valueOf(confirmdate));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == btnPlaceOrder){
            if(cashPaymentBtn.isChecked()){
                cashPayment();
            }else if(gCashPaymentBtn.isChecked()){
                gCashPayment();
            }else{
                Toast.makeText(this, "Select payment method", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cashPayment(){
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                .child("Orders").child(ordersId);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    Map<String,Object> updateData = new HashMap<>();

                    updateData.put("payment", "Cash on delivery");
                    updateData.put("status", "shipping");
                    updateData.put("amount", "0");
                    updateData.put("paymentdate", dateAndTime());


                            rootRef.updateChildren(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(PaymentActivity.this, "Add to Cart Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PaymentActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void gCashPayment(){
            if(TextUtils.isEmpty(referenceNoEt.getText().toString())){
            Toast.makeText(this, "Enter the reference number", Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference rootRef = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                    .child("Orders").child(ordersId);

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Map<String,Object> updateData = new HashMap<>();
                        updateData.put("payment","Gcash");
                        updateData.put("status","shipping");
                        updateData.put("refno", referenceNoEt.getText().toString().trim());
                        updateData.put("paymentdate", dateAndTime());

                        rootRef.updateChildren(updateData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(PaymentActivity.this, "Add to Cart Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PaymentActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_one:
                if (checked)
                    linearLayout2.setVisibility(View.GONE);
                break;
            case R.id.radio_two:
                if (checked)
                    linearLayout2.setVisibility(View.VISIBLE);
                break;
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

    private static String replaceAll(String string){
        return string.replaceAll("\\D+","");
    }
}
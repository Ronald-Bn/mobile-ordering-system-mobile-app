package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView TvAddress, TvZipCode, TvSubtotal, TvTotalPayment , TvShip, OrderDateTv, OrderIdTv, confirmDate, TvPhone, TvName;
    private Button btnPlaceOrder, uploadPhotoBtn;
    private String ordersId;
    private int sum = 0;
    private FirebaseFirestore fStore;
    private FirebaseStorage mStorage;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Parcelable state;
    private RadioButton cashPaymentBtn, gCashPaymentBtn;
    private LinearLayout linearLayout,linearLayout2 ;
    private EditText referenceNoEt;
    private Uri imageUrl = null;
    private Dialog dialog;
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
        uploadPhotoBtn = findViewById(R.id.uploadPhotoBtn);
        uploadPhotoBtn.setEnabled(true);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        OrderDateTv = findViewById(R.id.orderDateTv);
        OrderIdTv = findViewById(R.id.orderIdTv);
        confirmDate =findViewById(R.id.confirmDate);
        referenceNoEt =findViewById(R.id.referenceNoEt);
        btnPlaceOrder.setOnClickListener(this);
        dialog = new Dialog(this);

        String cartId = getIntent().getStringExtra("cartId");
        ordersId = getIntent().getStringExtra("ordersId");

        linearLayout = findViewById(R.id.PaymentLayout);
        linearLayout2 = findViewById(R.id.PaymentLayout2);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.GONE);
        btnPlaceOrder.setText("Submit");

        fStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.CheckOutRecyclerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        referenceNoEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    uploadPhotoBtn.setEnabled(true);
                } else {
                    uploadPhotoBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(PaymentActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Browse"), 1);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == btnPlaceOrder){
            if(cashPaymentBtn.isChecked()){
                dialog.setContentView(R.layout.loading_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();
                cashPayment();
            }else if(gCashPaymentBtn.isChecked()){
                dialog.setContentView(R.layout.loading_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();
                gCashPayment();
            }else{
                dialog.setContentView(R.layout.loading_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);
                dialog.show();
                Toast.makeText(this, "Choose payment method", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
    }

    private void cashPayment(){
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                .child("Orders").child(ordersId);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    Map<String,Object> updateData = new HashMap<>();

                    updateData.put("payment", "Cash on delivery");
                    updateData.put("status", "shipping");
                    updateData.put("status_userid",  "shipping_" + user.getUid());
                    updateData.put("receipt", "https://firebasestorage.googleapis.com/v0/b/capstone-project-v-1-3.appspot.com/o/image%2Fno_photo_uploaded.png?alt=media&token=3dff5f60-6a6d-4f6c-a600-00aa5d4a79ac");
                    updateData.put("paymentdate", dateAndTime());
                    updateData.put("refno","0");

                            rootRef.updateChildren(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Toast.makeText(PaymentActivity.this, "Payment Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PaymentActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void gCashPayment(){
            FirebaseUser user = mAuth.getInstance().getCurrentUser();
            if(imageUrl != null && TextUtils.isEmpty(referenceNoEt.getText().toString())){
                StorageReference ref = mStorage.getReference().child("image").child(imageUrl.getLastPathSegment());
                ref.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()) {
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                                            .child("Orders").child(ordersId);

                                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Map<String,Object> updateData = new HashMap<>();
                                            updateData.put("payment","Gcash");
                                            updateData.put("status","shipping");
                                            updateData.put("status_userid", "shipping_" + user.getUid());
                                            updateData.put("receipt",task.getResult().toString());
                                            updateData.put("refno", "0");
                                            updateData.put("paymentdate", dateAndTime());

                                            rootRef.updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog.dismiss();
                                                    Toast.makeText(PaymentActivity.this, "Payment Success", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(PaymentActivity.this,MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog.dismiss();
                                                    Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            dialog.dismiss();
                                            Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
           }else if(imageUrl == null && TextUtils.isEmpty(referenceNoEt.getText().toString())){
                dialog.dismiss();
            Toast.makeText(this, "Enter Reference Number", Toast.LENGTH_SHORT).show();
            }else{
            DatabaseReference rootRef = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                    .child("Orders").child(ordersId);

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Map<String,Object> updateData = new HashMap<>();
                        updateData.put("payment","Gcash");
                        updateData.put("status", "shipping");
                        updateData.put("status_userid",  "shipping_" + user.getUid());
                        updateData.put("receipt","https://firebasestorage.googleapis.com/v0/b/capstone-project-v-1-3.appspot.com/o/image%2Fno_photo_uploaded.png?alt=media&token=3dff5f60-6a6d-4f6c-a600-00aa5d4a79ac");
                        updateData.put("refno", referenceNoEt.getText().toString().trim());
                        updateData.put("paymentdate", dateAndTime());

                        rootRef.updateChildren(updateData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        Toast.makeText(PaymentActivity.this, "Payment Success", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PaymentActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    dialog.dismiss();
                    Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                .child("Orders").child(ordersId);
                Map<String,Object> updateData = new HashMap<>();
                updateData.put("shipping", reportDateAndTime());
                 rootRef.child("Report").setValue(updateData);
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

    private String reportDateAndTime(){
        // Current Date
        Date dateAndTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.getDefault());
        String currentDate = dateFormat.format(dateAndTime);
        return currentDate;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUrl=data.getData();
            Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show();
            referenceNoEt.setEnabled(false);
            referenceNoEt.setInputType(InputType.TYPE_NULL);
        }
    }

    private static String replaceAll(String string){
        return string.replaceAll("\\D+","");
    }
}
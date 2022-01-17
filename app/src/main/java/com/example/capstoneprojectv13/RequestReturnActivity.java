package com.example.capstoneprojectv13;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneprojectv13.adapter.CartAdapter;
import com.example.capstoneprojectv13.model.CartModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

public class RequestReturnActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Parcelable state;
    private TextView orderIdTv, orderTimeTv, reasonBtn, reasonTv;
    private ImageView uploadPhotoBtn;
    private Uri imageUrl = null;
    private LinearLayout layout;
    private int px;
    private ArrayList<Uri> FileList = new ArrayList<Uri>();
    private FirebaseStorage mStorage;
    private String cartId, ordersId,orderTime;
    private Button submitBtn;
    private ArrayList<String> stringUrl = new ArrayList<>();
    private Dialog dialog;
    private String usersId , image;
    private EditText usersComment, emailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_return);

        //Toolbar Customization
        Toolbar mToolbar = findViewById(R.id.toolbar_return_refund);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        cartId = getIntent().getStringExtra("cartId");
        ordersId = getIntent().getStringExtra("ordersId");
        orderTime = getIntent().getStringExtra("orderTime");

        reasonTv = findViewById(R.id.reasonTv);
        submitBtn = findViewById(R.id.submitBtn);
        layout = findViewById(R.id.layout);
        reasonBtn = findViewById(R.id.reasonBtn);
        uploadPhotoBtn = findViewById(R.id.uploadPhotoBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        orderTimeTv = findViewById(R.id.orderTimeTv);
        usersComment = findViewById(R.id.usersComment);
        emailAddress = findViewById(R.id.emailAddress);
        orderIdTv.setText(cartId);
        orderTimeTv.setText(orderTime);

        Resources r = getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 62,r.getDisplayMetrics()));


        recyclerView = findViewById(R.id.refundRecyclerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mStorage = FirebaseStorage.getInstance();
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        usersId = user.getUid();
        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("Cart_List")
                                .child(user.getUid())
                                .child(cartId), CartModel.class)
                                .build();

        cartAdapter = new CartAdapter(this, options);
        recyclerView.setAdapter(cartAdapter);

        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(RequestReturnActivity.this)
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

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reasonTv.getText().toString().equals("") || emailAddress.getText().toString().equals("")){
                    Toast.makeText(RequestReturnActivity.this, "Please fill in all the required fields.", Toast.LENGTH_SHORT).show();
                }else{
                    dialog = new Dialog(RequestReturnActivity.this);
                    dialog.setContentView(R.layout.loading_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCancelable(false);
                    dialog.show();
                    uploadFiles();
                }
            }
        });

        reasonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(RequestReturnActivity.this);
                dialog.setContentView(R.layout.reason_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.show();

                Button submitBtn = dialog.findViewById(R.id.submitBtn);
                RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectId = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton;
                        radioButton = dialog.findViewById(selectId);
                        reasonTv.setText(radioButton.getText().toString());
                        reasonTv.setVisibility(View.VISIBLE);
                        dialog.dismiss();
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


    private void addView(ImageView imageView, int width, int height){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,height);
        imageView.setLayoutParams(layoutParams);
        layout.addView(imageView);
    }

    private void uploadFiles() {
        HashMap<String, Object> postData = new HashMap<>();


        for (int j = 0; j < FileList.size(); j++) {
            Uri PerFile = FileList.get(j);

            StorageReference ref = mStorage.getReference().child(cartId).child(PerFile.getLastPathSegment());
            ref.putFile(PerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                          image = String.valueOf(uri);
                          DatabaseReference databaseReference = FirebaseDatabase.getInstance().getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    .getReference()
                                    .child("Orders")
                                    .child(ordersId);

                            postData.put("request_image", image);
                            postData.put("reason" , reasonTv.getText().toString().trim());
                            postData.put("comment" , usersComment.getText().toString().trim());
                            postData.put("email_address" ,emailAddress.getText().toString());
                            postData.put("status" , "return");
                            databaseReference.updateChildren(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Toast.makeText(RequestReturnActivity.this, "Request Return & Refund Success", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(RequestReturnActivity.this, "Please wait for the Response", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });
                        }
                    });
                }
            });
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null ) {
            imageUrl=data.getData();
            ImageView imageView = new ImageView(RequestReturnActivity.this);
            imageView.setImageURI(imageUrl);
            FileList.add(data.getData());
            Toast.makeText(this, String.valueOf(FileList.size()), Toast.LENGTH_SHORT).show();
            addView(imageView,px,px);
        }
    }
}
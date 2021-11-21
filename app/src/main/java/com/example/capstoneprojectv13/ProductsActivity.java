package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstoneprojectv13.adapter.ReviewsAdapter;
import com.example.capstoneprojectv13.listener.ICartLoadListener;
import com.example.capstoneprojectv13.model.CartModel;
import com.example.capstoneprojectv13.model.Reviews;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseFirestore fStore;
    private ImageView Image;
    private TextView  nameTv, priceTv , descriptionTv, CartTotalTv;
    private EditText etQuantity;
    private Dialog dialog;
    private int quantity = 1;
    private String uid, username;

    private String pid, price , name, image;
    private Parcelable state;
    private RecyclerView recyclerView;
    private ReviewsAdapter reviewAdapter;
    private Button BtnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());;

        nameTv = findViewById(R.id. ProductNameTv);
        priceTv = findViewById(R.id.ProductPriceTv);
        descriptionTv = findViewById(R.id.ProductDescriptionTv);
        Image = findViewById(R.id.ProductImageIv);
        BtnAddToCart = findViewById(R.id.BtnAddToCart);
        CartTotalTv = findViewById(R.id.CartTotalTv);
        etQuantity = findViewById(R.id.etQuantity);
        etQuantity.setFocusableInTouchMode(false);
        etQuantity.setFocusable(false);

        dialog = new Dialog(this);

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.rgb(254, 186, 31), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.rgb(254, 186, 31), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.rgb(254, 186, 31), PorterDuff.Mode.SRC_ATOP);

        //Get string from adapter;
         pid = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        price = getIntent().getStringExtra("price");
        image = getIntent().getStringExtra("image");
        String category = getIntent().getStringExtra("category");
        String description = getIntent().getStringExtra("description");
        String status = getIntent().getStringExtra("status");


        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        DocumentReference documentReference = fStore.collection("Users").document(user.getUid());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                username = value.getString("FullName");
            }
        });



        nameTv.setText(name);
        priceTv.setText(price);
        descriptionTv.setText(description);
        Glide.with(this)
                .load(image)
                .into(Image);

        //Recyclerview
        recyclerView = findViewById(R.id.ReviewRecyclerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.stopScroll();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Reviews> options =
                new FirebaseRecyclerOptions.Builder<Reviews>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Reviews"), Reviews.class)
                        .build();

        reviewAdapter = new ReviewsAdapter(options);
        recyclerView.setAdapter(reviewAdapter);

        BtnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        reviewAdapter.startListening();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.products_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.cart_menu:

                return true;

            case R.id.star_menu:
                 getRatings();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    public void increment (View view) {
        quantity++;
        etQuantity.setText("" + quantity);
    }

    public void decrement (View view) {
        if (quantity > 1){
            quantity--;
            etQuantity.setText("" + quantity);
        }
    }

    private void addToCart(){
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Cart")
                .child(user.getUid());

        databaseReference.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            int number = Integer.parseInt(etQuantity.getText().toString());
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists() && snapshot.hasChild("cartId")) // If the product exists in database
                {
                    CartModel cartModel = snapshot.getValue(CartModel.class);
                    cartModel.setQuantity(cartModel.getQuantity()+number);
                    Map<String,Object> updateData = new HashMap<>();
                    updateData.put("quantity", cartModel.getQuantity());
                    updateData.put("totalPrice", cartModel.getQuantity()*Float.parseFloat(price));

                    databaseReference.child(pid)
                            .updateChildren(updateData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ProductsActivity.this, "Add to Cart Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else // If product does not have in database
                {
                    CartModel cartModel = new CartModel();
                    cartModel.setName(name);
                    cartModel.setImage(image);
                    cartModel.setKey("ongoing");
                    cartModel.setItemprice(price);
                    cartModel.setQuantity(number);
                    cartModel.setTotalPrice(number * Float.parseFloat(price));
                    databaseReference.child(pid)
                            .setValue(cartModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ProductsActivity.this, "Add to Cart Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getRatings(){
        dialog.setContentView(R.layout.reviews);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        EditText etReviews = dialog.findViewById(R.id.etReviews);
        etReviews.setGravity(Gravity.TOP);
        RatingBar ratingBar2 = dialog.findViewById(R.id.ratingBar2);
        LayerDrawable stars = (LayerDrawable) ratingBar2.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.rgb(254, 186, 31), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.rgb(255,255,255), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.rgb(254, 186, 31), PorterDuff.Mode.SRC_ATOP);
        ImageView imageViewClose = dialog.findViewById(R.id.imageViewBack);
        Button btnSubmit = dialog.findViewById(R.id.BtnSubmit);
        dialog.show();

        imageViewClose.setOnClickListener(v -> dialog.cancel());
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Username = nameTv.getText().toString().trim();
                final float Rating = ratingBar2.getRating();
                final String Review = etReviews.getText().toString().trim();

                DatabaseReference ratings = databaseReference.push();
                ratings.child("Name").setValue(Username);
                ratings.child("Review").setValue(Review);
                dialog.cancel();
                }
            }
        );
    }
}
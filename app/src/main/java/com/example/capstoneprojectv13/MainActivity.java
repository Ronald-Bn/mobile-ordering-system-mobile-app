package com.example.capstoneprojectv13;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.capstoneprojectv13.adapter.MainProductsAdapter;
import com.example.capstoneprojectv13.fragment.FragmentCart;
import com.example.capstoneprojectv13.fragment.FragmentHome;
import com.example.capstoneprojectv13.fragment.FragmentOrder;
import com.example.capstoneprojectv13.fragment.FragmentPayment;
import com.example.capstoneprojectv13.fragment.FragmentProfile;
import com.example.capstoneprojectv13.model.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    MainProductsAdapter mainProductsAdapter;
    private EditText search;
    private BottomNavigationView btnViewid;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new FragmentHome());
        search = findViewById(R.id.search_field);
        btnViewid = findViewById(R.id.bottom_navigation_view);
        FrameLayout frameLayout = findViewById(R.id.frameLayout);

        btnViewid.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.home_nav:
                        fragment = new FragmentHome();
                        break;

                    case R.id.cart_nav:
                        fragment = new FragmentCart();
                        break;

                    case R.id.order_nav:
                        fragment = new FragmentOrder();
                        break;

                    case R.id.notification_nav:
                        fragment = new FragmentNotifications();
                        break;

                    case R.id.profile_nav:
                        fragment = new FragmentProfile();
                        break;
                }
                return loadFragment(fragment);
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void txtSearch(String str) {
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Products").orderByChild("name").startAt(str).endAt(str + "~"), Products.class)
                        .build();

        mainProductsAdapter = new MainProductsAdapter(this, options);
        mainProductsAdapter.startListening();
    }

}
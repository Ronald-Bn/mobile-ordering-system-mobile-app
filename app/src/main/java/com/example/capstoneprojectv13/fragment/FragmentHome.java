package com.example.capstoneprojectv13.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneprojectv13.adapter.MainProductsAdapter;
import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.model.Products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private MainProductsAdapter mainProductsAdapter;
    private FirebaseAuth mAuth;
    private EditText search_field;
    private Button AllProducts, Men, Women;
    private Parcelable state;

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.HomeRecyclerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Products")
                                .orderByChild("status").equalTo("Available"), Products.class)
                        .build();

        mainProductsAdapter = new MainProductsAdapter(view.getContext(),options);
        recyclerView.setAdapter(mainProductsAdapter);

        search_field = view.findViewById(R.id.search_field);
        AllProducts = view.findViewById(R.id.AllProducts);
        AllProducts.setOnClickListener(v->{
            getAllProducts();
        });

        Men = view.findViewById(R.id.Men);
        Men.setOnClickListener(v->{
            getAllMenProducts();
        });
        Women = view.findViewById(R.id.Women);
        Women.setOnClickListener(v -> {
            getAllWomenProducts();
        });

        search_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    txtSearch(search_field.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        return view;
    }



    private void txtSearch(String str) {
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Products").orderByChild("name").startAt(str).endAt(str + "~"), Products.class)
                        .build();

        mainProductsAdapter = new MainProductsAdapter(getActivity(), options);
        recyclerView.setAdapter(mainProductsAdapter);
        mainProductsAdapter.startListening();
    }

    private void getAllProducts() {
        FirebaseRecyclerOptions<Products> allProducts =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Products").orderByChild("status").equalTo("Available"), Products.class)
                        .build();

        mainProductsAdapter = new MainProductsAdapter(getActivity(), allProducts);
        recyclerView.setAdapter(mainProductsAdapter);
        mainProductsAdapter.startListening();
    }

    private void getAllMenProducts() {
        FirebaseRecyclerOptions<Products> allMenProducts =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Products").orderByChild("category").equalTo("MEN"), Products.class)
                        .build();

        mainProductsAdapter = new MainProductsAdapter(getActivity(), allMenProducts);
        recyclerView.setAdapter(mainProductsAdapter);
        mainProductsAdapter.startListening();
    }

    private void getAllWomenProducts() {
        FirebaseRecyclerOptions<Products> allMenProducts =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Products").orderByChild("category").equalTo("WOMEN"), Products.class)
                        .build();

        mainProductsAdapter = new MainProductsAdapter(getActivity(), allMenProducts);
        recyclerView.setAdapter(mainProductsAdapter);
        mainProductsAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        mainProductsAdapter.startListening();
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

}
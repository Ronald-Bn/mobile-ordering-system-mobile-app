package com.example.capstoneprojectv13.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.capstoneprojectv13.CheckOutActivity;
import com.example.capstoneprojectv13.ProductsActivity;
import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.SwipeHelper;
import com.example.capstoneprojectv13.adapter.CartAdapter;
import com.example.capstoneprojectv13.model.CartModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCart extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private FirebaseAuth mAuth;
    private Button btnCheckOut;
    private Parcelable state;
    private DatabaseReference databaseReference;
    private TextView CartTotalTv;

    private List<CartModel> cartModelList;

    public FragmentCart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCart.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCart newInstance(String param1, String param2) {
        FragmentCart fragment = new FragmentCart();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        btnCheckOut = view.findViewById(R.id.CheckOut);
        CartTotalTv = view.findViewById(R.id.CartTotalTv);


        recyclerView = view.findViewById(R.id.CartRecyclerList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Cart")
                                .child(user.getUid()).orderByChild("key").equalTo("ongoing"), CartModel.class)
                        .build();

        cartAdapter = new CartAdapter(view.getContext(),options);
        recyclerView.setAdapter(cartAdapter);

        SwipeHelper swipeHelper = new SwipeHelper(getActivity(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Delete",
                        0,
                        Color.parseColor("#FF3C30"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                String key = ((TextView) recyclerView.findViewHolderForAdapterPosition(pos).itemView.findViewById(R.id.cartStatus)).getText().toString();
                                databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("Cart")
                                        .child(user.getUid())
                                        .child(key);
                                databaseReference.removeValue();
                            }
                        }
                ));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Edit",
                        0,
                        Color.parseColor("#FF9502"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                String key = ((TextView) recyclerView.findViewHolderForAdapterPosition(pos).itemView.findViewById(R.id.cartStatus)).getText().toString();
                                String quantity = ((TextView) recyclerView.findViewHolderForAdapterPosition(pos).itemView.findViewById(R.id.cartStatus)).getText().toString();
                                Intent intent = new Intent(getActivity().getApplication(), ProductsActivity.class);
                                intent.putExtra("id" , key);
                                intent.putExtra("quantity", quantity);
                                startActivity(intent);
                            }
                        }
                ));
            }
        };

        swipeHelper.attachSwipe();

        databaseReference = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Cart")
                .child(user.getUid());
        databaseReference.orderByChild("key").equalTo("ongoing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int sum = 0;
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    Map<String,Object> map = (Map<String, Object>) ds.getValue();
                    Object price = map.get("totalPrice");
                    int pValue = Integer.parseInt(String.valueOf(price));
                    sum += pValue;
                    CartTotalTv.setText(String.valueOf(sum));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnCheckOut.setOnClickListener(v -> {
            if(CartTotalTv.getText().toString().equals("0")){
                return;
            }else{
                Intent intent = new Intent(view.getContext(), CheckOutActivity.class);
                intent.putExtra("subtotal", CartTotalTv.getText().toString().trim());
                startActivity(intent);
            }
        });



        return view;
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
    public void onStart() {
        super.onStart();
        cartAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        cartAdapter.stopListening();
    }

}
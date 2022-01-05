package com.example.capstoneprojectv13.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.adapter.OrdersReceivingAdapter;
import com.example.capstoneprojectv13.adapter.OrdersReturnAdapter;
import com.example.capstoneprojectv13.model.OrdersModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOrderReturnRefund#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOrderReturnRefund extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private OrdersReturnAdapter ordersReturnAdapter;
    private Parcelable state;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentOrderReturnRefund() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOrderReturnRefund.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOrderReturnRefund newInstance(String param1, String param2) {
        FragmentOrderReturnRefund fragment = new FragmentOrderReturnRefund();
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
        View view =  inflater.inflate(R.layout.fragment_order_return_refund, container, false);

        recyclerView = view.findViewById(R.id.OrdersPendingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Query query = FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
                .child("Orders").orderByChild("status_userid").equalTo("return_" + user.getUid());

        FirebaseRecyclerOptions<OrdersModel> options =
                new FirebaseRecyclerOptions.Builder<OrdersModel>()
                        .setQuery(query, OrdersModel.class)
                        .build();

        ordersReturnAdapter = new OrdersReturnAdapter(view.getContext(),options);
        recyclerView.setAdapter(ordersReturnAdapter);
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
        ordersReturnAdapter.startListening();
    }
}
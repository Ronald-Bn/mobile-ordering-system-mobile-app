package com.example.capstoneprojectv13.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.capstoneprojectv13.R;
import com.example.capstoneprojectv13.adapter.OrdersAdapter;
import com.example.capstoneprojectv13.model.OrdersModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOrderCancelled#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOrderCancelled extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private Parcelable state;

    public FragmentOrderCancelled() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOrderCancelled.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOrderCancelled newInstance(String param1, String param2) {
        FragmentOrderCancelled fragment = new FragmentOrderCancelled();
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
        View view = inflater.inflate(R.layout.fragment_order_cancelled, container, false);

        recyclerView = view.findViewById(R.id.OrdersPendingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        FirebaseRecyclerOptions<OrdersModel> options =
                new FirebaseRecyclerOptions.Builder<OrdersModel>()
                        .setQuery(FirebaseDatabase.getInstance("https://capstone-project-v-1-3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Orders")
                                .orderByChild("status").equalTo("rejected"), OrdersModel.class)
                        .build();



        ordersAdapter = new OrdersAdapter(view.getContext(),options);
        recyclerView.setAdapter(ordersAdapter);
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
        ordersAdapter.startListening();
    }
}
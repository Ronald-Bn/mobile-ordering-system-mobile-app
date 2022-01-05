package com.example.capstoneprojectv13.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.capstoneprojectv13.fragment.FragmentOrderCancelled;
import com.example.capstoneprojectv13.fragment.FragmentOrderCompleted;
import com.example.capstoneprojectv13.fragment.FragmentOrderPending;
import com.example.capstoneprojectv13.fragment.FragmentOrderReceiving;
import com.example.capstoneprojectv13.fragment.FragmentOrderReturnRefund;
import com.example.capstoneprojectv13.fragment.FragmentOrderShipping;
import com.example.capstoneprojectv13.fragment.FragmentPayment;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    private String[] titles = new String[]{"Pending","To Pay","To Ship","To Receive","Completed","Cancelled","Return/Refund"};

    public ViewPagerFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FragmentOrderPending();
            case 1:
                return new FragmentPayment();
            case 2:
                return new FragmentOrderShipping();
            case 3:
                return new FragmentOrderReceiving();
            case 4:
                return new FragmentOrderCompleted();
            case 5:
                return new FragmentOrderCancelled();
            case 6:
                return new FragmentOrderReturnRefund();
        }
        return new FragmentOrderPending();
    }


    @Override
    public int getItemCount() {
        return titles.length;
    }
}

package com.example.capstoneprojectv13.listener;

import com.example.capstoneprojectv13.model.CartModel;
import com.example.capstoneprojectv13.model.Products;

import java.util.List;

public interface ICartLoadListener {

    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailed(String message);
}

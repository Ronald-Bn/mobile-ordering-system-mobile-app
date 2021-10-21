package com.example.capstoneprojectv13.listener;

import com.example.capstoneprojectv13.model.Products;

import java.util.List;

public interface IProductLoadListener {
    void onProductLoadSuccess(List<Products> productsList);
    void onProductLoadFailed(String message);
}

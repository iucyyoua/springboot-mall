package com.lucywu.springbootmall.service;

import com.lucywu.springbootmall.model.Product;

public interface ProductService {

    //根據producyID取得商品資訊
    Product getProductById(Integer productId);
}

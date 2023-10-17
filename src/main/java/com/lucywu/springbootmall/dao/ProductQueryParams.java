package com.lucywu.springbootmall.dao;

import com.lucywu.springbootmall.constant.ProductCategory;

//改成此寫法易於日後修改參數、增加可讀性
public class ProductQueryParams {

    private ProductCategory category;
    private String search;

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
